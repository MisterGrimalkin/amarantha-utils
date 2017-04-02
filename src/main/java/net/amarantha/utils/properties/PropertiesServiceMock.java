package net.amarantha.utils.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesServiceMock extends PropertiesService {

    private Map<String, String> properties = new HashMap<>();

    @Override
    protected Properties loadProperties(String filename, boolean create) {
        Properties properties = new Properties();
        propertySets.put(filename, properties);
        return properties;
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
