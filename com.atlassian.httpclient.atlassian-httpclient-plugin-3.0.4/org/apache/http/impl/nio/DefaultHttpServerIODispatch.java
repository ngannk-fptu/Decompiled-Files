/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpRequestFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestParserFactory;
import org.apache.http.impl.nio.reactor.AbstractIODispatch;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpServerEventHandler;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpServerIODispatch<H extends NHttpServerEventHandler>
extends AbstractIODispatch<DefaultNHttpServerConnection> {
    private final H handler;
    private final NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connectionFactory;

    public static <T extends NHttpServerEventHandler> DefaultHttpServerIODispatch<T> create(T handler, SSLContext sslContext, ConnectionConfig config) {
        return sslContext == null ? new DefaultHttpServerIODispatch<T>(handler, config) : new DefaultHttpServerIODispatch<T>(handler, sslContext, config);
    }

    public static <T extends NHttpServerEventHandler> DefaultHttpServerIODispatch<T> create(T eventHandler, SSLContext sslContext, ConnectionConfig config, HttpRequestFactory httpRequestFactory) {
        DefaultHttpRequestParserFactory httpRequestParserFactory = new DefaultHttpRequestParserFactory(null, httpRequestFactory);
        return sslContext == null ? new DefaultHttpServerIODispatch<T>(eventHandler, new DefaultNHttpServerConnectionFactory(null, httpRequestParserFactory, null, config)) : new DefaultHttpServerIODispatch<T>(eventHandler, new SSLNHttpServerConnectionFactory(sslContext, null, httpRequestParserFactory, null, config));
    }

    public static <T extends NHttpServerEventHandler> DefaultHttpServerIODispatch<T> create(T handler, SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        return sslContext == null ? new DefaultHttpServerIODispatch<T>(handler, config) : new DefaultHttpServerIODispatch<T>(handler, sslContext, sslHandler, config);
    }

    public DefaultHttpServerIODispatch(H handler, NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connFactory) {
        this.handler = (NHttpServerEventHandler)Args.notNull(handler, "HTTP server handler");
        this.connectionFactory = Args.notNull(connFactory, "HTTP server connection factory");
    }

    @Deprecated
    public DefaultHttpServerIODispatch(H handler, HttpParams params) {
        this(handler, new DefaultNHttpServerConnectionFactory(params));
    }

    @Deprecated
    public DefaultHttpServerIODispatch(H handler, SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        this(handler, new SSLNHttpServerConnectionFactory(sslContext, sslHandler, params));
    }

    @Deprecated
    public DefaultHttpServerIODispatch(H handler, SSLContext sslContext, HttpParams params) {
        this(handler, sslContext, null, params);
    }

    public DefaultHttpServerIODispatch(H handler, ConnectionConfig config) {
        this(handler, new DefaultNHttpServerConnectionFactory(config));
    }

    public DefaultHttpServerIODispatch(H handler, SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(handler, new SSLNHttpServerConnectionFactory(sslContext, sslHandler, config));
    }

    public DefaultHttpServerIODispatch(H handler, SSLContext sslContext, ConnectionConfig config) {
        this(handler, new SSLNHttpServerConnectionFactory(sslContext, null, config));
    }

    @Override
    protected DefaultNHttpServerConnection createConnection(IOSession session) {
        return this.connectionFactory.createConnection(session);
    }

    public NHttpConnectionFactory<? extends DefaultNHttpServerConnection> getConnectionFactory() {
        return this.connectionFactory;
    }

    public H getHandler() {
        return this.handler;
    }

    @Override
    protected void onConnected(DefaultNHttpServerConnection conn) {
        try {
            this.handler.connected(conn);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }

    @Override
    protected void onClosed(DefaultNHttpServerConnection conn) {
        this.handler.closed(conn);
    }

    @Override
    protected void onException(DefaultNHttpServerConnection conn, IOException ex) {
        this.handler.exception(conn, ex);
    }

    @Override
    protected void onInputReady(DefaultNHttpServerConnection conn) {
        conn.consumeInput((NHttpServerEventHandler)this.handler);
    }

    @Override
    protected void onOutputReady(DefaultNHttpServerConnection conn) {
        conn.produceOutput((NHttpServerEventHandler)this.handler);
    }

    @Override
    protected void onTimeout(DefaultNHttpServerConnection conn) {
        try {
            this.handler.timeout(conn);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }
}

