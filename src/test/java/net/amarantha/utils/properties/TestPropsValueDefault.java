package net.amarantha.utils.properties;

import net.amarantha.utils.properties.entity.Property;

public class TestPropsValueDefault {

    @Property("String1") private String string1;
    @Property("String2") private String string2 = "Fetlocks Blowing";

    public String getString1() {
        return string1;
    }

    public String getString2() {
        return string2;
    }
}
