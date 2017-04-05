package net.amarantha.utils.string;

import java.util.HashMap;
import java.util.Map;

public class StringMap {

    private Map<String, String> map = new HashMap<>();

    public StringMap add(String key, String value) {
        map.put(key, value);
        return this;
    }

    public StringMap add(String key, Object value) {
        return add(key, value==null ? null : value.toString());
    }

    public Map<String, String> get() {
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        map.forEach((key,value)->sb.append(key).append("=").append(value).append("\n"));
        return sb.toString();
    }
}
