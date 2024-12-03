/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.spi.http.HttpExchange
 *  javax.xml.ws.spi.http.HttpHandler
 */
package com.sun.xml.ws.transport.http.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.resources.HttpserverMessages;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.server.PortableConnectionImpl;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.spi.http.HttpExchange;
import javax.xml.ws.spi.http.HttpHandler;

final class PortableHttpHandler
extends HttpHandler {
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String HEAD_METHOD = "HEAD";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server.http");
    private final HttpAdapter adapter;
    private final Executor executor;

    public PortableHttpHandler(@NotNull HttpAdapter adapter, @Nullable Executor executor) {
        assert (adapter != null);
        this.adapter = adapter;
        this.executor = executor;
    }

    public void handle(HttpExchange msg) {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if (this.executor != null) {
                this.executor.execute(new HttpHandlerRunnable(msg));
            } else {
                this.handleExchange(msg);
            }
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleExchange(HttpExchange msg) throws IOException {
        PortableConnectionImpl con = new PortableConnectionImpl(this.adapter, msg);
        try {
            String method;
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Received HTTP request:{0}", msg.getRequestURI());
            }
            if ((method = msg.getRequestMethod()).equals(GET_METHOD) || method.equals(POST_METHOD) || method.equals(HEAD_METHOD) || method.equals(PUT_METHOD) || method.equals(DELETE_METHOD)) {
                this.adapter.handle(con);
            } else {
                logger.warning(HttpserverMessages.UNEXPECTED_HTTP_METHOD(method));
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
                PortableHttpHandler.this.handleExchange(this.msg);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

