package net.amarantha.utils.http.entity;

import javax.ws.rs.core.Response;

public interface HttpCallback {
    void call(Response response);
}
