package framework;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TransformLibrary {

    private static final Map<String, Function<Object, Object>> REGISTRY = new HashMap<>();

    static {
       
    	REGISTRY.put("seconds-to-minutes", val -> {
    	    try {
    	        if (val == null) return 1; 
    	        
    	        int seconds = Integer.parseInt(val.toString());
    	        int inputMinutes = seconds / 60;
    	        
    	        int[] allowedIntervals = {1, 5, 10, 15, 30, 60};
    	        
    	        int closest = allowedIntervals[0];
    	        int minDifference = Math.abs(inputMinutes - closest);
    	        
    	        for (int interval : allowedIntervals) {
    	            int diff = Math.abs(inputMinutes - interval);
    	            if (diff < minDifference) {
    	                minDifference = diff;
    	                closest = interval;
    	            }
    	        }
    	        return closest;
    	    } catch (Exception e) { 
    	        return 1;
    	    }
    	});
    	
    	
    

        REGISTRY.put("ms-to-seconds", val -> {
            try {
                return (val == null) ? 0 : Integer.parseInt(val.toString()) / 1000;
            } catch (Exception e) { return 0; }
        });

        REGISTRY.put("prefix-test", val -> (val == null) ? "TEST_UNKNOWN" : "TEST_" + val.toString());
        

        REGISTRY.put("to-uppercase", val -> (val == null) ? "GET" : val.toString().toUpperCase());
    }

    /**
     * Executes the mapping logic based on the function name defined in XML.
     */
    public static Object execute(String functionName, Object value) {
        if (functionName == null || functionName.trim().isEmpty()) {
            return value;
        }

        Function<Object, Object> transform = REGISTRY.get(functionName);
        
        if (transform == null) {
            System.err.println("[WARN] Transform function not found: " + functionName);
            return value; 
        }

        return transform.apply(value);
    }
}