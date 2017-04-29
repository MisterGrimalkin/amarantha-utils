package net.amarantha.utils.osc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.osc.entity.OscListener;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.utils.shell.Utility.log;

public class OscServiceImpl extends OscService {

    public OscServiceImpl() {
        super("OSC Service");
    }

    @Override
    public void send(OscCommand command) {
        try {
            if ( !command.isSilent() ) {
                log("--> OSC " + command.toString());
            }
            OSCPortOut sender = new OSCPortOut(InetAddress.getByName(command.getHost()), command.getPort());
            OSCMessage msg = new OSCMessage("/"+command.getAddress(), command.getArguments());
            sender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(int port, String address, OscListener listener) {
        try {
            OSCPortIn receiver = getInPort(port);
            receiver.addListener("/" + address, (date, oscMessage) -> listener.onReceive(date, oscMessage.getArguments()));
            receiver.startListening();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, OSCPortIn> inPorts = new HashMap<>();

    private OSCPortIn getInPort(int port) throws SocketException {
        OSCPortIn result = inPorts.get(port);
        if ( result==null ) {
            result = new OSCPortIn(port);
            inPorts.put(port, result);
        }
        return result;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
