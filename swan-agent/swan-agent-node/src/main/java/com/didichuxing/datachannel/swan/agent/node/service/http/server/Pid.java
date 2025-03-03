package com.didichuxing.datachannel.swan.agent.node.service.http.server;

import java.io.IOException;

import com.didichuxing.datachannel.swan.agent.engine.utils.SystemUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Pid extends Handler implements HttpHandler {

    public final static String URI = "/swan-agent/pid";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        writer(SystemUtils.getPid(), exchange);
    }
}
