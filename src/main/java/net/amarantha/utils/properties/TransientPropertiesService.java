package net.amarantha.utils.properties;

import java.util.HashMap;

public class TransientPropertiesService extends PropertiesService {

    public TransientPropertiesService() {
        super("NotAFile");
    }

    @Override
    protected void loadFromFile() {
        propsMap = new HashMap<>();
    }

    @Override
    protected void saveToFile() {

    }
}
