package net.amarantha.utils.properties.entity;

public class PropertyNotFoundException extends Exception {

    private String groupName = "";
    private String key = "";

    public PropertyNotFoundException(String groupName, String key) {
        this(groupName, key, "Property '" + groupName + "/" + key + "' not found.");

    }

    public PropertyNotFoundException(String groupName, String key, String message) {
        super(message);
        this.groupName = groupName;
        this.key = key;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getKey() {
        return key;
    }
}
