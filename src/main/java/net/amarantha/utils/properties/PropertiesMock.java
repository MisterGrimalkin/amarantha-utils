package net.amarantha.utils.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesMock extends PropertiesService {

    private Map<String, String> properties = new HashMap<>();

    @Override
    protected Properties loadProperties(String filename, boolean create) {
        return new Properties();
    }

    @Override
    protected void saveProperties() {
        // do nothing
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }

}
