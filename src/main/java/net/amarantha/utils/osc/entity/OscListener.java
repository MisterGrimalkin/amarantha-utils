package net.amarantha.utils.osc.entity;

import java.util.Date;
import java.util.List;

public interface OscListener {
    void onReceive(Date date, List<Object> args);
}
