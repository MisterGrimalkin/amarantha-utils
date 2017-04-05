package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.entity.Property;

class TestProps {

    @Property("TestString") private String testString;
    @Property("TestInt") private Integer testInt;
    @Property("TestRGB") private RGB testRGB;
    @Property("TestNotFound") private String notFound;
    @Property("TestClass") private Class<?> testClass;

    String getTestString() {
        return testString;
    }

    Integer getTestInt() {
        return testInt;
    }

    RGB getTestRGB() {
        return testRGB;
    }

    Class<?> getTestClass() { return testClass; }

    public String getNotFound() {
        return notFound;
    }
}
