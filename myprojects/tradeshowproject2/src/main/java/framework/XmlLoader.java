package framework;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class XmlLoader {

    /**
     * Maps Vendor technology types to Framework types.
     * Bridges BOTH 'vendor-type' and 'sub-type' to support Datadog's hierarchical structure.
     * Key (Vendor's Name)	Value (Framework's Name)
     * it is vendor specific
		"api"	"RESTAPI"
		"http"	"RESTAPI"
     */
	public static Map<String, String> loadTypeBridge(String path, String vendorName) throws Exception {
        Map<String, String> bridge = new HashMap<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList vendors = doc.getElementsByTagName("vendor");
        for (int i = 0; i < vendors.getLength(); i++) {
            Element v = (Element) vendors.item(i);
            if (v.getAttribute("name").equalsIgnoreCase(vendorName)) {
                NodeList types = v.getElementsByTagName("monitor-type");
                for (int j = 0; j < types.getLength(); j++) {
                    Element t = (Element) types.item(j);
                    String frameworkType = t.getAttribute("framework-type");
                    bridge.put(t.getAttribute("vendor-type"), frameworkType);
                    String subType = t.getAttribute("sub-type");
                    if (!subType.isEmpty()) bridge.put(subType, frameworkType);
                }
            }
        }
        return bridge;
    }
    /**
     * it takes all enpoints and have it in set 
     * eg : ["/api/location_profiles", "/api/user_groups"]
     */
    public static List<String> getLookupEndpoints(String path) {
        Set<String> endpoints = new LinkedHashSet<>(); 
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
            NodeList nl = doc.getElementsByTagName("lookup-endpoint");
            for (int i = 0; i < nl.getLength(); i++) {
                endpoints.add(nl.item(i).getTextContent().trim());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new ArrayList<>(endpoints);
    }
    /**
     * Loads JsonPaths from normalization.xml 
     * Maps "Normalized Name" -> "Source JsonPath"
     * vendor- spefic
     * rest => name and jsonpath
     */
    public static Map<String, Map<String, String>> loadNormalizationPaths(String path, String vendorName)
            throws Exception {
        Map<String, Map<String, String>> normMap = new HashMap<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList vendors = doc.getElementsByTagName("vendor");

        for (int i = 0; i < vendors.getLength(); i++) {
            Element v = (Element) vendors.item(i);
            if (!v.getAttribute("name").equalsIgnoreCase(vendorName)) continue;

            NodeList types = v.getElementsByTagName("monitor-type");
            for (int j = 0; j < types.getLength(); j++) {
                Element t = (Element) types.item(j);
                String frameworkType = t.getAttribute("framework-type");
                Map<String, String> fieldPaths = new HashMap<>();

                NodeList fields = t.getElementsByTagName("field");
                for (int k = 0; k < fields.getLength(); k++) {
                    Element f = (Element) fields.item(k);
                    fieldPaths.put(getTag(f, "normalized-name"), getTag(f, "source-jsonpath"));
                }
                normMap.put(frameworkType, fieldPaths);
            }
        }
        return normMap;
    }

    /**
     * Loads Mapping Rules and dynamic Response Paths for API resolution from site24x7-mappings.xml
     */
    public static Map<String, List<Map<String, String>>> loadMappings(String path, String vendorName) throws Exception {
        Map<String, List<Map<String, String>>> mappings = new HashMap<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList types = doc.getElementsByTagName("monitor-type");

        for (int i = 0; i < types.getLength(); i++) {
            Element t = (Element) types.item(i);
            String typeName = t.getAttribute("name");
            List<Map<String, String>> fields = new ArrayList<>();
            NodeList fNodes = t.getElementsByTagName("field");

            for (int j = 0; j < fNodes.getLength(); j++) {
                Element f = (Element) fNodes.item(j);
                Map<String, String> m = new HashMap<>();
                
                m.put("target", f.getAttribute("target-field"));
                m.put("type", f.getAttribute("type")); 
                m.put("transform", getTag(f, "transform"));
                m.put("resolve", getTag(f, "resolve-by"));
                
                NodeList endpointNodes = f.getElementsByTagName("lookup-endpoint");
                if (endpointNodes.getLength() > 0) {
                    Element ep = (Element) endpointNodes.item(0);
                    m.put("endpoint", ep.getTextContent().trim());
                    m.put("responsePath", ep.getAttribute("response-path"));
                }

                m.put("matchOn", getTag(f, "match-on"));
                m.put("idField", getTag(f, "id-field"));
                m.put("default", getTag(f, "default-value"));

                NodeList vendors = f.getElementsByTagName("vendor");
                for (int k = 0; k < vendors.getLength(); k++) {
                    Element v = (Element) vendors.item(k);
                    if (v.getAttribute("name").equalsIgnoreCase(vendorName)) {
                        m.put("sourceKey", getTag(v, "source-key"));
                        String vDef = getTag(v, "default-value");
                        if (!vDef.isEmpty()) m.put("default", vDef);
                    }
                }
                fields.add(m);
            }
            mappings.put(typeName, fields);
        }
        return mappings;
    }

    /**
     * Retrieves the base API URL and endpoint for a specific vendor.
     * vendor -specific
     */
    public static Map<String, String> getVendorMonitorConfig(String path, String vendorName) throws Exception {
        Map<String, String> config = new HashMap<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList vendors = doc.getElementsByTagName("vendor");

        for (int i = 0; i < vendors.getLength(); i++) {
            Element v = (Element) vendors.item(i);
            if (!v.getAttribute("name").equalsIgnoreCase(vendorName)) continue;

            Node apiNode = v.getElementsByTagName("api").item(0);
            if (apiNode != null) config.put("api", apiNode.getTextContent().trim());

            Element monitors = (Element) v.getElementsByTagName("monitors").item(0);
            if (monitors != null) {
                config.put("endpoint", monitors.getAttribute("endpoint"));
                config.put("rootPath", monitors.getAttribute("root-path"));
            }
            break;
        }
        return config;
    }

    /**
     * Gets credentials required for a specific vendor from the normalization XML.
     */
    public static List<String> getRequiredCredentials(String path, String vendorName) throws Exception {
        List<String> requirements = new ArrayList<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList vendors = doc.getElementsByTagName("vendor");

        for (int i = 0; i < vendors.getLength(); i++) {
            Element v = (Element) vendors.item(i);
            if (v.getAttribute("name").equalsIgnoreCase(vendorName)) {
                NodeList keys = v.getElementsByTagName("key");
                for (int j = 0; j < keys.getLength(); j++) {
                    requirements.add(((Element) keys.item(j)).getAttribute("id"));
                }
            }
        }
        return requirements;
    }

    /**
     * Loads regional configuration (accounts and base URLs) based on DC ID.
     * If you call getRegionConfig(path, "in"), the method 
     * returns:KeyValue (Extracted Data)
     * "accounts-url : "https://accounts.zoho.in"
     * base-url : "https://www.site24x7.in"
     */
    public static Map<String, String> getRegionConfig(String path, String dcId) throws Exception {
        Map<String, String> config = new HashMap<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList regions = doc.getElementsByTagName("region");

        for (int i = 0; i < regions.getLength(); i++) {
            Element r = (Element) regions.item(i);
            if (r.getAttribute("id").equalsIgnoreCase(dcId)) {
                config.put("accounts-url", r.getAttribute("accounts-url"));
                config.put("base-url", r.getAttribute("base-url"));
                return config; 
            }
        }
        return config;
    }

    public static String getSetting(String path, String tagName) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        NodeList settings = doc.getElementsByTagName(tagName);
        return (settings.getLength() > 0) ? settings.item(0).getTextContent().trim() : "";
    }

    private static String getTag(Element e, String tag) {
        NodeList nl = e.getElementsByTagName(tag);
        return (nl.getLength() > 0) ? nl.item(0).getTextContent().trim() : "";
    }
}