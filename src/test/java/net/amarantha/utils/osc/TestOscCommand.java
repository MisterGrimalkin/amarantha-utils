package net.amarantha.utils.osc;

import net.amarantha.utils.osc.entity.OscCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOscCommand {

    @Test
    public void testParsing() {

        OscCommand command = new OscCommand("localhost", 9090, "something");
        OscCommand copy = OscCommand.fromString(command.toString());

        assertEquals(command, copy);

    }
}
