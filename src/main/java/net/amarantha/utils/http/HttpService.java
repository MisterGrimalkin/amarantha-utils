package net.amarantha.utils.http;


import net.amarantha.utils.http.entity.HttpCallback;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.http.entity.Param;
import net.amarantha.utils.service.AbstractService;

public abstract class HttpService extends AbstractService {

    public HttpService(String name) {
        super(name);
    }

    public abstract String fire(HttpCommand command);

    public abstract void fireAsync(HttpCallback callback, HttpCommand command);

    public abstract String get(String host, int port, String path, Param... params);

    public abstract void getAsync(HttpCallback callback, String host, int port, String path, Param... params);

    public abstract String post(String host, int port, String path, String payload, Param... params);

    public abstract void postAsync(HttpCallback callback, String host, int port, String path, String payload, Param... params);

}
