package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.entity.Property;

class TestProps {

    @Property("TestString") private String testString;
    @Property("TestBoolean") private Boolean testBoolean;
    @Property("TestInteger") private Integer testInt;
    @Property("TestDouble") private Double testDouble;
    @Property("TestRGB") private RGB testRGB;
    @Property("TestClass") private Class<?> testClass;
    @Property("TestFromValue") private Class<?> testFromValue = Property.class;

    public String getTestString() {
        return testString;
    }

    public Boolean getTestBoolean() {
        return testBoolean;
    }

    public Integer getTestInt() {
        return testInt;
    }

    public Double getTestDouble() {
        return testDouble;
    }

    public RGB getTestRGB() {
        return testRGB;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

}
