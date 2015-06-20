package com.battlehack.melder.api;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aakhmerov on 16/08/14.
 */
public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int MIN_THREADS = 10;
    private static final int MAX_THREADS = 100;

    /**
     * Hidden constructor, just to indicate that no one should instantiate entry point
     */
    private Main() {
    }

    private static final int PORT = 9001;
    private static final String WEB_XML =
            "WEB-INF/web.xml";

    public static void main(String[] args) {
        Server server;
        server = new Server();
        server.setThreadPool(createThreadPool());
        server.addConnector(createConnector());
        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);
        try {
            server.start();
        } catch (Exception e) {
            LOGGER.info("failed to start application server - exiting", e);
            System.exit(1);
        }
    }

    private static ThreadPool createThreadPool() {
        // TODO: You should configure these appropriately
        // for your environment - this is an example only
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(MIN_THREADS);
        threadPool.setMaxThreads(MAX_THREADS);
        return threadPool;
    }

    private static SelectChannelConnector createConnector() {
        SelectChannelConnector connector =
                new SelectChannelConnector();
        connector.setPort(PORT);
        connector.setHost("0.0.0.0");
        return connector;
    }

    private static HandlerCollection createHandlers() {
        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath("/");
        ctx.setWar(getShadedWarUrl());
        List<Handler> handlers = new ArrayList<Handler>();

        handlers.add(ctx);

        HandlerList contexts = new HandlerList();
        contexts.setHandlers(handlers.toArray(new Handler[0]));

        RequestLogHandler log = new RequestLogHandler();

        HandlerCollection result = new HandlerCollection();
        result.setHandlers(new Handler[]{contexts, log});

        return result;
    }

    private static String getShadedWarUrl() {
        String urlStr = getResource(WEB_XML).toString();
        // Strip off "WEB-INF/web.xml"
        return urlStr.substring(0, urlStr.length() - WEB_XML.length());
    }

    private static URL getResource(String aResource) {
        return Thread.currentThread().
                getContextClassLoader().getResource(aResource);
    }
}