package net.amarantha.utils.osc;

import java.util.Date;
import java.util.List;

public interface OscListener {
    void onReceive(Date date, List<Object> args);
}
