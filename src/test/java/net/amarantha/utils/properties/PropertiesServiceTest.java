package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;
import org.junit.Test;

import static net.amarantha.utils.colour.RGB.MAGENTA;
import static org.junit.Assert.*;

public class PropertiesServiceTest {

    private PropertiesService props = new PropertiesServiceMock();

    private static final String TEST_STRING = "There once was a very famous man";
    private static final int TEST_INT = 42;
    private static final RGB TEST_RGB = MAGENTA;

    @Test
    public void testProperties() {

        props.setProperty("TestString", TEST_STRING);
        props.setProperty("TestInt", TEST_INT+"");
        props.setProperty("TestRGB", TEST_RGB.toString());
        props.setProperty("TestClass", "net.amarantha.utils.properties.TestPropsInject");

        TestProps testProps = new TestProps();

        assertNull(testProps.getTestString());
        assertNull(testProps.getTestInt());
        assertNull(testProps.getTestRGB());
        assertNull(testProps.getTestClass());

        try {
            props.injectProperties(testProps);
        } catch (PropertyNotFoundException e) {
            assertTrue(e instanceof PropertyNotFoundException && e.getMessage().contains("TestNotFound"));
        }

        assertEquals(TEST_STRING, testProps.getTestString());
        assertEquals(TEST_INT, testProps.getTestInt().intValue());
        assertEquals(TEST_RGB, testProps.getTestRGB());
        assertEquals(TestPropsInject.class, testProps.getTestClass());

    }

    @Test
    public void testCommandLine() {

        String[] args = new String[] { "-flag", "-option=value" };
        PropertiesService.processArgs(args);

        assertTrue(props.isArgumentPresent("flag"));
        assertEquals("value", props.getArgumentValue("option"));

    }

}
