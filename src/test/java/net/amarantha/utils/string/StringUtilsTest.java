package net.amarantha.utils.string;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static net.amarantha.utils.string.StringUtils.asList;
import static net.amarantha.utils.string.StringUtils.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void testBreakStrings() {

        String listInput = "Looking,Up ";
        String mapInput = "At= Crack,Of =Dawn";

        List<String> list = asList(listInput);
        assertEquals(2, list.size());
        assertTrue(list.contains("Looking"));
        assertTrue(list.contains("Up"));

        Map<String, String> map = asMap(mapInput);
        assertEquals(2, map.size());
        assertEquals("Crack", map.get("At"));
        assertEquals("Dawn", map.get("Of"));

    }

}
