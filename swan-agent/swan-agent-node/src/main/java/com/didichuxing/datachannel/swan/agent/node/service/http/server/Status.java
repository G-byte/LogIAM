package com.didichuxing.datachannel.swan.agent.node.service.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.node.LaunchService;
import com.didichuxing.datachannel.swan.agent.node.ConfigService;
import com.didichuxing.datachannel.swan.agent.node.SwanAgent;

import com.didichuxing.datachannel.swan.agent.common.loggather.LogGather;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 启停swan-agent
 * @author: huangjw
 * @Date: 18/8/28 14:17
 */
public class Status extends Handler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class.getName());
    public final static String  URI    = "/swan-agent/status";

    private LaunchService       launchService;

    private ConfigService       configService;

    public Status(LaunchService launchService, ConfigService configService) {
        this.launchService = launchService;
        this.configService = configService;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // 获得输入流
        InputStream in = httpExchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        // 将BufferedReader转化为字符串
        String text = IOUtils.toString(reader);
        if (text.contains(LogConfigConstants.STATUS_TAG)) {
            try {
                String status = text.substring(text.indexOf("=") + 1);
                if (StringUtils.isNotBlank(status)) {
                    if (status.equals(LogConfigConstants.START_TAG)) {
                        start();
                    } else if (status.equals(LogConfigConstants.STOP_TAG)) {
                        stop();
                    }
                    writer("success", httpExchange);
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("Status error!", "handle error!", e);
                writer("failed", httpExchange);
            }
        }
    }

    private void stop() {
        LOGGER.info("begin to stop swan agent");
        try {
            if (SwanAgent.isRunning) {
                configService.stop(true);
                launchService.stop(true);
                SwanAgent.isRunning = false;
            } else {
                LOGGER.info("swan agent is already stopped. ignore!");
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("Status error!", "stop error!", e);
        }
        LOGGER.info("success to stop swan agent");
    }

    private void start() {
        LOGGER.info("begin to start swan agent");
        try {
            if (!SwanAgent.isRunning) {
                configService.init(null);
                launchService.init(null);

                configService.start();
                launchService.start();
                SwanAgent.isRunning = true;
            } else {
                LOGGER.info("swan agent is already started. ignore!");
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("Status error!", "start error!", e);
        }
        LOGGER.info("success to start swan agent");
    }
}
