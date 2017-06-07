package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.amarantha.utils.colour.RGB.*;
import static org.junit.Assert.*;

public class PropertiesServiceTest {

    private static final String TEST_STRING = "There once was a very famous man";
    private static final int TEST_INT = 42;
    private static final RGB TEST_RGB = MAGENTA;

    @Test
    public void testInjection() {

        PropertiesService props = new PropertiesService("test/settings.yaml");

        TestProps test = new TestProps();
        assertNull(test.getTestString());
        assertNull(test.getTestBoolean());
        assertNull(test.getTestInt());
        assertNull(test.getTestDouble());
        assertNull(test.getTestRGB());
        assertNull(test.getTestClass());
//        assertNull(test.testStringList);

        try {
            props.injectProperties(test);
        } catch (PropertyNotFoundException e) {
            fail(e.getMessage());
        }

        assertEquals("Hello Internet",      test.getTestString());
        assertEquals(true,                  test.getTestBoolean());
        assertEquals(42,                    test.getTestInt().intValue());
        assertEquals(3.142,                 test.getTestDouble(), 0.00001);
        assertEquals(RGB.MAGENTA,           test.getTestRGB());
        assertEquals(TestProps.class,       test.getTestClass());

        TestGroupProps testGroup = new TestGroupProps();
        assertNull(testGroup.getTestString());
        assertNull(testGroup.getTestBoolean());
        assertNull(testGroup.getTestInt());
        assertNull(testGroup.getTestDouble());
        assertNull(testGroup.getTestRGB());
        assertNull(testGroup.getTestClass());

        try {
            props.injectProperties(testGroup);
        } catch (PropertyNotFoundException e) {
            fail(e.getMessage());
        }

        assertEquals("Flaggy Flag",         testGroup.getTestString());
        assertEquals(false,                 testGroup.getTestBoolean());
        assertEquals(17,                    testGroup.getTestInt().intValue());
        assertEquals(17.71,                 testGroup.getTestDouble(), 0.00001);
        assertEquals(RGB.YELLOW,            testGroup.getTestRGB());
        assertEquals(TestGroupProps.class,  testGroup.getTestClass());

    }

    @Test
    public void testFieldValueDefault() {

        try {

            String str1 = "My Lovely Horse";
            String str2 = "Fetlocks Blowing";
            String str3 = "In The Wind";

            PropertiesService props = new TransientPropertiesService();
            assertFalse(props.isSet("String1"));
            assertFalse(props.isSet("String2"));

            TestPropsValueDefault test = new TestPropsValueDefault();
            assertNull(test.getString1());
            assertEquals(str2, test.getString2());

            props.set("String1", str1);

            props.injectProperties(test);
            assertEquals(str1, test.getString1());
            assertEquals(str2, test.getString2());
            assertEquals(str2, props.getString("String2"));

            test = new TestPropsValueDefault();
            assertNull(test.getString1());
            assertEquals(str2, test.getString2());

            props.set("String2", str3);

            props.injectProperties(test);
            assertEquals(str1, test.getString1());
            assertEquals(str3, test.getString2());
            assertEquals(str3, props.getString("String2"));

        } catch (PropertyNotFoundException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testDefaults() {

        PropertiesService props = new TransientPropertiesService();

        assertFalse(props.isSet("String"));
        assertEquals("TestString", props.getStringOrDefault("String", "TestString"));
        assertTrue(props.isSet("String"));

        assertFalse(props.isSet("Boolean"));
        assertEquals(false, props.getBooleanOrDefault("Boolean", false));
        assertTrue(props.isSet("Boolean"));

        assertFalse(props.isSet("Integer"));
        assertEquals(999, props.getIntOrDefault("Integer", 999).intValue());
        assertTrue(props.isSet("Integer"));

        assertFalse(props.isSet("Double"));
        assertEquals(99.991, props.getDoubleOrDefault("Double", 99.991), 0.00001);
        assertTrue(props.isSet("Double"));

        assertFalse(props.isSet("RGB"));
        assertEquals(RGB.BLUE, props.getRgbOrDefault("RGB", RGB.BLUE));
        assertTrue(props.isSet("RGB"));

        try {
            props.set("TestClass", TestPropsInject.class.getName());
            assertEquals(TestPropsInject.class, props.getClass("TestClass"));
        } catch (PropertyNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testPropertiesFromFile() {

        PropertiesService props = new PropertiesService("test/settings.yaml");

        try
        {
            assertEquals("Hello Internet",      props.getString("TestString"));
            assertEquals(true,                  props.getBoolean("TestBoolean"));
            assertEquals(42,                    props.getInt("TestInteger").intValue());
            assertEquals(3.142,                 props.getDouble("TestDouble"), 0.000001);
            assertEquals(RGB.MAGENTA,           props.getRgb("TestRGB"));
            assertEquals(TestProps.class,       props.getClass("TestClass"));

            List<RGB> testList1 = props.getRgbList("TestRgbList");
            assertTrue(testList1.contains(RGB.WHITE));
            assertTrue(testList1.contains(RGB.BLACK));
            assertTrue(testList1.contains(RGB.GREEN));

            List<String> testList2 = props.getStringList("TestStringList");
            assertTrue(testList2.contains("One"));
            assertTrue(testList2.contains("Two"));
            assertTrue(testList2.contains("Three"));

            assertEquals("Flaggy Flag",         props.getString("TestGroup", "TestString"));
            assertEquals(false,                 props.getBoolean("TestGroup", "TestBoolean"));
            assertEquals(17,                    props.getInt("TestGroup", "TestInteger").intValue());
            assertEquals(17.71,                 props.getDouble("TestGroup", "TestDouble"), 0.000001);
            assertEquals(RGB.YELLOW,            props.getRgb("TestGroup", "TestRGB"));
            assertEquals(TestGroupProps.class,  props.getClass("TestGroup", "TestClass"));


        } catch (PropertyNotFoundException e) {
            fail(e.getMessage());
        }

        boolean failed = false;
        try {
            props.getString("TestNotFound");
        } catch (PropertyNotFoundException e) {
            assertEquals("TestNotFound", e.getKey());
            failed = true;
        }
        assertTrue(failed);

//        List<String> in = props.get("")

        props.set("OutputTest", "Greeting", "Hello you!");
        props.set("OutputTest", "TrueEnough", false);
        props.set("OutputTest", "Twelve", 12);
        props.set("OutputTest", "ThreePointFour", 3.4);
        props.set("OutputTest", "Magenta", RGB.MAGENTA);
        props.set("OutputTest", "TestProps", TestProps.class.getName());

        List<RGB> list = new ArrayList<>();
        list.add(RED);
        list.add(BLUE);
        list.add(GREEN);
        list.add(CYAN);

        props.set("OutputTest", "TestList", list);

    }

    @Test
    public void testCommandLine() {

        String[] args = new String[] { "-flag", "-option=value" };
        PropertiesService.processArgs(args);

        assertTrue(PropertiesService.isArgumentPresent("flag"));
        assertEquals("value", PropertiesService.getArgumentValue("option"));

    }

}
