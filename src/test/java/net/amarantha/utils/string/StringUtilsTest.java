package net.amarantha.utils.string;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static net.amarantha.utils.string.StringUtils.asList;
import static net.amarantha.utils.string.StringUtils.asMap;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testBreakStrings() {

        String listInput = "Looking;Up ";
        String mapInput = "At= Crack;Of =Dawn";

        List<String> list = asList(listInput);
        assertEquals(2, list.size());
        assertTrue(list.contains("Looking"));
        assertTrue(list.contains("Up"));

        Map<String, String> map = asMap(mapInput);
        assertEquals(2, map.size());
        assertEquals("Crack", map.get("At"));
        assertEquals("Dawn", map.get("Of"));

    }

    @Test
    public void testStringMap() {

        StringMap stringMap =
            new StringMap()
                .add("You", "Are")
                .add("My", "Moonshine")
                .add("Like", 69)
                .add("Null", null)
            ;


        Map<String, String> map = stringMap.get();
        assertEquals("Are", map.get("You"));
        assertEquals("Moonshine", map.get("My"));
        assertEquals("69", map.get("Like"));
        assertTrue(map.containsKey("Null"));
        assertNull(map.get("Null"));

        String string = stringMap.toString();
        assertEquals(4, string.split("\n").length);
        assertTrue(string.contains("You=Are"));
        assertTrue(string.contains("My=Moonshine"));
        assertTrue(string.contains("Like=69"));

    }

}
