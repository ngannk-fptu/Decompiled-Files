/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.xml.ws.resources.HttpserverMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.server.ServerConnectionImpl;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WSHttpHandler
implements HttpHandler {
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String HEAD_METHOD = "HEAD";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";
    private static final Logger LOGGER = Logger.getLogger("com.sun.xml.ws.server.http");
    private static final boolean fineTraceEnabled = LOGGER.isLoggable(Level.FINE);
    private final HttpAdapter adapter;
    private final Executor executor;

    public WSHttpHandler(@NotNull HttpAdapter adapter, @Nullable Executor executor) {
        assert (adapter != null);
        this.adapter = adapter;
        this.executor = executor;
    }

    @Override
    public void handle(HttpExchange msg) {
        try {
            if (fineTraceEnabled) {
                LOGGER.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if (this.executor != null) {
                this.executor.execute(new HttpHandlerRunnable(msg));
            } else {
                this.handleExchange(msg);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleExchange(HttpExchange msg) throws IOException {
        ServerConnectionImpl con = new ServerConnectionImpl(this.adapter, msg);
        try {
            String method;
            if (fineTraceEnabled) {
                LOGGER.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if ((method = msg.getRequestMethod()).equals(GET_METHOD) || method.equals(POST_METHOD) || method.equals(HEAD_METHOD) || method.equals(PUT_METHOD) || method.equals(DELETE_METHOD)) {
                this.adapter.handle(con);
            } else if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
            }
        }
        finally {
            msg.close();
        }
    }

    class HttpHandlerRunnable
    implements Runnable {
        final HttpExchange msg;

        HttpHandlerRunnable(HttpExchange msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                WSHttpHandler.this.handleExchange(this.msg);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

