package net.amarantha.utils.midi.entity;

import net.amarantha.utils.midi.MidiService;

import javax.sound.midi.ShortMessage;
import java.lang.reflect.Field;

public class MidiCommand {

    private int command = 0;
    private int channel = 1;
    private int data1 = 0;
    private int data2 = 0;

    public MidiCommand() {}

    public MidiCommand(int command, int channel, int data1, int data2) {
        this.command = command;
        this.channel = channel;
        this.data1 = data1;
        this.data2 = data2;
    }

    public static MidiCommand fromString(String input) {
        String[] pieces = input.split("\\|");
        if ( pieces.length==4 ) {
            int command;
            try {
                command = Integer.parseInt(pieces[0]);
            } catch ( NumberFormatException e ) {
                command = parseCommandType(pieces[0]);
            }
            int channel = Integer.parseInt(pieces[1]);
            int data1 = Integer.parseInt(pieces[2]);
            int data2 = Integer.parseInt(pieces[3]);
            return new MidiCommand(command, channel, data1, data2);
        }
        return null;
    }

    public static int parseCommandType(String commandType) {
        Field[] fields = ShortMessage.class.getDeclaredFields();
        for ( Field field : fields ) {
            if ( field.getName().equalsIgnoreCase(commandType) ) {
                try {
                    return field.getInt(null);
                } catch (IllegalAccessException ignored) {}
            }
        }
        return -1;
    }

    public void send(MidiService midi) {
        midi.send(command, channel, data1, data2);
    }

    public int getCommand() {
        return command;
    }

    public int getChannel() {
        return channel;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    @Override
    public String toString() {
        return command +"|" + channel + "|" + data1 + "|" + data2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MidiCommand that = (MidiCommand) o;

        if (command != that.command) return false;
        if (channel != that.channel) return false;
        if (data1 != that.data1) return false;
        return data2 == that.data2;

    }

    @Override
    public int hashCode() {
        int result = command;
        result = 31 * result + channel;
        result = 31 * result + data1;
        result = 31 * result + data2;
        return result;
    }
}
