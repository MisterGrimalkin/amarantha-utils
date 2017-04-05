package net.amarantha.utils.osc;

import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.osc.entity.OscListener;

import java.util.*;

public class OscServiceMock extends OscService {

    private OscCommand lastCommand;
    private Map<String, List<OscListener>> allListeners = new HashMap<>();

    public OscServiceMock() {
        super("OSC Service Mock");
    }

    @Override
    public void send(OscCommand command) {
        lastCommand = command;
    }

    @Override
    public void onReceive(int port, String address, OscListener listener) {
        List<OscListener> listeners = allListeners.get(address);
        if ( listeners==null ) {
            listeners = new LinkedList<>();
            allListeners.put(address, listeners);
        }
        listeners.add(listener);
    }

    public void receive(String address, List<Object> args) {
        List<OscListener> listeners = allListeners.get(address);
        if ( listeners!=null ) {
            for (OscListener listener : listeners) {
                listener.onReceive(new Date(), args);
            }
        }
    }

    public OscCommand getLastCommand() {
        return lastCommand;
    }

    public void clearLastCommand() {
        lastCommand = null;
    }


    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
