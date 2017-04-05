package net.amarantha.utils.osc;

import net.amarantha.utils.osc.entity.OscCommand;
import net.amarantha.utils.osc.entity.OscListener;
import net.amarantha.utils.service.AbstractService;

public abstract class OscService extends AbstractService {

    public OscService(String name) {
        super(name);
    }

    public abstract void send(OscCommand command);

    public abstract void onReceive(int port, String address, OscListener listener);

}
