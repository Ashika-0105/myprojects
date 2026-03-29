package framework;

import org.json.JSONArray;
import org.json.JSONObject;
import com.jayway.jsonpath.JsonPath;
import java.util.*;

public class MainEngine {

    private Map<String, Map<String, String>> globalCache = new HashMap<>();
    private Map<String, String> typeBridge = new HashMap<>();

    public List<Map<String, Object>> fetchAndPrepare(
            String vendor,
            Map<String, String> srcHeaders,
            String normXml,
            String mapXml,
            String targetBaseUrl,
            Map<String, String> targetHeaders) throws Exception {

        List<Map<String, Object>> preparedList = new ArrayList<>();

        
        this.typeBridge = XmlLoader.loadTypeBridge(normXml, vendor);
        Map<String, List<Map<String, String>>> mappingRules = XmlLoader.loadMappings(mapXml, vendor);
        Map<String, Map<String, String>> normPaths = XmlLoader.loadNormalizationPaths(normXml, vendor);
        Map<String, String> vendorConfig = XmlLoader.getVendorMonitorConfig(normXml, vendor);

        String api = vendorConfig.get("api");
        String endpoint = vendorConfig.get("endpoint");

        if (api == null || api.isEmpty()) {
            throw new IllegalStateException("Vendor API URL missing for: " + vendor);
        }

        
        if (api.contains("{domain}")) {
            String actualDomain = srcHeaders.get("domain"); 
            if (actualDomain != null && !actualDomain.isEmpty()) {
                api = api.replace("{domain}", actualDomain.trim());
            } else {
                throw new RuntimeException("Domain credential is required but missing.");
            }
        }

        
        api = api.replaceAll("/$", "");
        endpoint = (endpoint == null) ? "" : endpoint.replaceAll("^/", "");
        String finalUrl = api + (endpoint.isEmpty() ? "" : "/" + endpoint);

        System.out.println("DEBUG: Final Request URL -> " + finalUrl);
        String rawData = GenericClient.fetch(finalUrl, srcHeaders);

        
        String rootPath = vendorConfig.getOrDefault("rootPath", "$");
        Object listObj = JsonPath.read(rawData, rootPath);
        JSONArray monitors = new JSONArray(listObj instanceof List ? (List<?>) listObj : new ArrayList<>());

        
        for (int i = 0; i < monitors.length(); i++) {
            JSONObject sourceJson = monitors.getJSONObject(i);
            
            String sourceType = sourceJson.optString("type");
            String sourceSubtype = sourceJson.optString("subtype");

            String frameworkType = typeBridge.get(sourceSubtype); 
            
           
            if (frameworkType == null || frameworkType.isEmpty()) {
                frameworkType = typeBridge.get(sourceType);
            }

            if (frameworkType == null || !mappingRules.containsKey(frameworkType)) {
                System.out.println("DEBUG: Skipping monitor - No mapping for: " + 
                    (sourceSubtype.isEmpty() ? sourceType : sourceSubtype));
                continue;
            }

            
            Model model = new Model();
            String monitorRawString = sourceJson.toString();

            for (Map<String, String> rule : mappingRules.get(frameworkType)) {
                String targetField = rule.get("target");
                Object finalValue = null;

                if ("api".equalsIgnoreCase(rule.get("resolve"))) {
                    
                    String lookupEndpoint = rule.get("endpoint");
                    lookupEndpoint = (lookupEndpoint == null) ? "" : lookupEndpoint;

                    String cacheUrl = targetBaseUrl.replaceAll("/$", "") + "/" + lookupEndpoint.replaceAll("^/", "");

                    if (!globalCache.containsKey(lookupEndpoint)) {
                        loadCache(cacheUrl, targetHeaders, lookupEndpoint,
                                  rule.get("matchOn"), rule.get("idField"), rule.get("responsePath"),
                                  null, false);
                    }

                    if ("list".equalsIgnoreCase(rule.get("type"))) {
                        finalValue = new ArrayList<>(globalCache.get(lookupEndpoint).values());
                    } else {
                        String path = normPaths.get(frameworkType).get(rule.get("sourceKey"));
                        String lookupName = String.valueOf(extract(monitorRawString, path, rule.get("default")));
                        finalValue = globalCache.get(lookupEndpoint).getOrDefault(lookupName, rule.get("default"));
                    }
                } else {
                   
                    String path = normPaths.get(frameworkType).get(rule.get("sourceKey"));
                    Object rawVal = extract(monitorRawString, path, rule.get("default"));
                    finalValue = TransformLibrary.execute(rule.get("transform"), rawVal);
                }
                model.addField(targetField, finalValue);
            }
            preparedList.add(model.data);
        }
        return preparedList;
    }

    private Object extract(String json, String path, String def) {
        try {
            if (path == null || path.isEmpty()) return def;
            Object result = JsonPath.read(json, path);
            return (result == null) ? def : result;
        } catch (Exception e) {
            return def;
        }
    }

    private void loadCache(
            String url,
            Map<String, String> headers,
            String endpoint,
            String nameK,
            String idK,
            String respPath,
            String filterType,
            boolean singleValue) throws Exception { 

        System.out.println("DEBUG: Loading cache URL: " + url);
        String resp = GenericClient.fetch(url, headers);
        List<Map<String, Object>> items = JsonPath.read(resp, respPath);

        Map<String, String> subCache = new HashMap<>();
        for (Map<String, Object> item : items) {
            String key = String.valueOf(item.get(nameK));
            String val = String.valueOf(item.get(idK));
            subCache.put(key, val);
        }
        
        System.out.println("DEBUG: Cache loaded for " + endpoint + " size=" + subCache.size());
        globalCache.put(endpoint, subCache);
    }
}