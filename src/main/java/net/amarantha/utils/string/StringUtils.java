package net.amarantha.utils.string;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringUtils {

    public static List<String> asList(String input) {
        return asList(input, ",");
    }

    public static List<String> asList(String input, String separator) {
        List<String> result = new LinkedList<>();
        String[] comps = input.split(separator);
        for ( String comp : comps ) {
            result.add(comp.trim());
        }
        return result;
    }

    public static Map<String, String> asMap(String input) {
        return asMap(input, ",");
    }

    public static Map<String, String> asMap(String input, String separator) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = input.split(separator);
        for ( String pair : pairs ) {
            String[] comps = pair.split("=");
            if ( comps.length==2 ) {
                result.put(comps[0].trim(), comps[1].trim());
            }
        }
        return result;
    }

}
