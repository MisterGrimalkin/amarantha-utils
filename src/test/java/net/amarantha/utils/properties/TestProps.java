package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;

class TestProps {

    @Property("TestString") private String testString;
    @Property("TestInt") private Integer testInt;
    @Property("TestRGB") private RGB testRGB;
    @Property("TestNotFound") private String notFound;

    String getTestString() {
        return testString;
    }

    Integer getTestInt() {
        return testInt;
    }

    RGB getTestRGB() {
        return testRGB;
    }

    public String getNotFound() {
        return notFound;
    }
}
