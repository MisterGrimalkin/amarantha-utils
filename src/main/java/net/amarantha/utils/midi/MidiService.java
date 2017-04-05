package net.amarantha.utils.midi;

import net.amarantha.utils.midi.entity.MidiCommand;
import net.amarantha.utils.service.AbstractService;

public abstract class MidiService extends AbstractService {

    public MidiService(String name) {
        super(name);
    }

    public abstract void send(MidiCommand midiCommand);

    public abstract void send(int command, int channel, int data1, int data2);

}
