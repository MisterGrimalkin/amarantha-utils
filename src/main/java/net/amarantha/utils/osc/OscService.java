package net.amarantha.utils.osc;

public interface OscService {

    void send(OscCommand command);

    void onReceive(int port, String address, OscListener listener);

}
