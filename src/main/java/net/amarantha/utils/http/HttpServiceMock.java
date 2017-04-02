package net.amarantha.utils.http;

import net.amarantha.utils.http.entity.HttpCallback;
import net.amarantha.utils.http.entity.HttpCommand;
import net.amarantha.utils.http.entity.Param;

import static net.amarantha.utils.http.entity.HttpCommand.GET;
import static net.amarantha.utils.http.entity.HttpCommand.POST;

public class HttpServiceMock extends HttpServiceImpl {

    private HttpCommand lastCommand;

    @Override
    public String get(String host, int port, String path, Param... params) {
        lastCommand = new HttpCommand(GET, host, port, path, "", "", params);
        return "";
    }

    @Override
    public void getAsync(HttpCallback callback, String host, int port, String path, Param... params) {
        lastCommand = new HttpCommand(GET, host, port, path, "", "", params);
    }

    @Override
    public String post(String host, int port, String path, String payload, Param... params) {
        lastCommand = new HttpCommand(POST, host, port, path, "", payload, params);
        return "";
    }

    @Override
    public void postAsync(HttpCallback callback, String host, int port, String path, String payload, Param... params) {
        lastCommand = new HttpCommand(POST, host, port, path, "", payload, params);
    }

    public HttpCommand getLastCommand() {
        return lastCommand;
    }

    public void clearLastCommand() {
        lastCommand = null;
    }
}
