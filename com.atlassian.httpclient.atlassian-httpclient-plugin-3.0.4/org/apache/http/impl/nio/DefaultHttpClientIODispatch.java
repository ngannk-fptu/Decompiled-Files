/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import javax.net.ssl.SSLContext;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.DefaultNHttpClientConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpClientConnectionFactory;
import org.apache.http.impl.nio.reactor.AbstractIODispatch;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpClientIODispatch<H extends NHttpClientEventHandler>
extends AbstractIODispatch<DefaultNHttpClientConnection> {
    private final H handler;
    private final NHttpConnectionFactory<? extends DefaultNHttpClientConnection> connectionFactory;

    public static <T extends NHttpClientEventHandler> DefaultHttpClientIODispatch<T> create(T handler, SSLContext sslContext, ConnectionConfig config) {
        return sslContext == null ? new DefaultHttpClientIODispatch<T>(handler, config) : new DefaultHttpClientIODispatch<T>(handler, sslContext, config);
    }

    public static <T extends NHttpClientEventHandler> DefaultHttpClientIODispatch<T> create(T handler, SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        return sslContext == null ? new DefaultHttpClientIODispatch<T>(handler, config) : new DefaultHttpClientIODispatch<T>(handler, sslContext, sslHandler, config);
    }

    public DefaultHttpClientIODispatch(H handler, NHttpConnectionFactory<? extends DefaultNHttpClientConnection> connFactory) {
        this.handler = (NHttpClientEventHandler)Args.notNull(handler, "HTTP client handler");
        this.connectionFactory = Args.notNull(connFactory, "HTTP client connection factory");
    }

    @Deprecated
    public DefaultHttpClientIODispatch(H handler, HttpParams params) {
        this(handler, new DefaultNHttpClientConnectionFactory(params));
    }

    @Deprecated
    public DefaultHttpClientIODispatch(H handler, SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        this(handler, new SSLNHttpClientConnectionFactory(sslContext, sslHandler, params));
    }

    @Deprecated
    public DefaultHttpClientIODispatch(H handler, SSLContext sslContext, HttpParams params) {
        this(handler, sslContext, null, params);
    }

    public DefaultHttpClientIODispatch(H handler, ConnectionConfig config) {
        this(handler, new DefaultNHttpClientConnectionFactory(config));
    }

    public DefaultHttpClientIODispatch(H handler, SSLContext sslContext, SSLSetupHandler sslHandler, ConnectionConfig config) {
        this(handler, new SSLNHttpClientConnectionFactory(sslContext, sslHandler, config));
    }

    public DefaultHttpClientIODispatch(H handler, SSLContext sslContext, ConnectionConfig config) {
        this(handler, new SSLNHttpClientConnectionFactory(sslContext, null, config));
    }

    @Override
    protected DefaultNHttpClientConnection createConnection(IOSession session) {
        return this.connectionFactory.createConnection(session);
    }

    public NHttpConnectionFactory<? extends DefaultNHttpClientConnection> getConnectionFactory() {
        return this.connectionFactory;
    }

    public H getHandler() {
        return this.handler;
    }

    @Override
    protected void onConnected(DefaultNHttpClientConnection conn) {
        Object attachment = conn.getContext().getAttribute("http.session.attachment");
        try {
            this.handler.connected(conn, attachment);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }

    @Override
    protected void onClosed(DefaultNHttpClientConnection conn) {
        this.handler.closed(conn);
    }

    @Override
    protected void onException(DefaultNHttpClientConnection conn, IOException ex) {
        this.handler.exception(conn, ex);
    }

    @Override
    protected void onInputReady(DefaultNHttpClientConnection conn) {
        conn.consumeInput((NHttpClientEventHandler)this.handler);
    }

    @Override
    protected void onOutputReady(DefaultNHttpClientConnection conn) {
        conn.produceOutput((NHttpClientEventHandler)this.handler);
    }

    @Override
    protected void onTimeout(DefaultNHttpClientConnection conn) {
        try {
            this.handler.timeout(conn);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }
}

