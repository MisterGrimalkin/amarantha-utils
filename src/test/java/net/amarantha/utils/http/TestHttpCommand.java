package net.amarantha.utils.http;

import net.amarantha.utils.http.entity.HttpCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestHttpCommand {

    @Test
    public void testParsing() {

        HttpCommand command = new HttpCommand("GET", "knickers.com", 8080, "buy", "thong", "Sweetness");
        HttpCommand copy = HttpCommand.fromString(command.toString());

        assertEquals(command, copy);

    }
}
