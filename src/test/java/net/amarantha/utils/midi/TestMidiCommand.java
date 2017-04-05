package net.amarantha.utils.midi;

import net.amarantha.utils.midi.entity.MidiCommand;
import org.junit.Test;

import static javax.sound.midi.ShortMessage.NOTE_ON;
import static org.junit.Assert.assertEquals;

public class TestMidiCommand {

    @Test
    public void testParsing() {
        MidiCommand command = new MidiCommand(NOTE_ON, 1, 64, 100);
        MidiCommand copy = MidiCommand.fromString(command.toString());
        assertEquals(command, copy);
    }

    @Test
    public void testCommandParsing() {
        MidiCommand thisCommand = new MidiCommand(NOTE_ON, 1, 64, 100);
        MidiCommand thatCommand = MidiCommand.fromString("NOTE_ON|1|64|100");
        assertEquals(thisCommand, thatCommand);
    }
}
