package net.amarantha.utils.midi;

import com.google.inject.Singleton;
import net.amarantha.utils.midi.entity.MidiCommand;

@Singleton
public class MidiServiceMock extends MidiService {

    private boolean deviceOpen = false;
    private MidiCommand lastMidiCommand = null;

    public MidiServiceMock() {
        super("MIDI Service Mock");
    }

    @Override
    public void onStart() {
        deviceOpen = true;
    }

    @Override
    public void onStop() {
        deviceOpen = false;
    }

    @Override
    public void send(MidiCommand midiCommand) {
        lastMidiCommand = midiCommand;
    }

    @Override
    public void send(int command, int channel, int data1, int data2) {
        lastMidiCommand = new MidiCommand(command, channel, data1, data2);
    }

    public MidiCommand getLastCommand() {
        return lastMidiCommand;
    }

    public boolean isDeviceOpen() {
        return deviceOpen;
    }

    public void clearLastCommand() { lastMidiCommand = null; }

}
