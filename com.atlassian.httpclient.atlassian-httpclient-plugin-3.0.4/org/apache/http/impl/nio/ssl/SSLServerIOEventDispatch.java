/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.SSLIOSession;
import org.apache.http.impl.nio.reactor.SSLSetupHandler;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServerIOTarget;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class SSLServerIOEventDispatch
extends DefaultServerIOEventDispatch {
    private final SSLContext sslContext;
    private final SSLSetupHandler sslHandler;

    public SSLServerIOEventDispatch(NHttpServiceHandler handler, SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        super(handler, params);
        Args.notNull(sslContext, "SSL context");
        Args.notNull(params, "HTTP parameters");
        this.sslContext = sslContext;
        this.sslHandler = sslHandler;
    }

    public SSLServerIOEventDispatch(NHttpServiceHandler handler, SSLContext sslContext, HttpParams params) {
        this(handler, sslContext, null, params);
    }

    protected SSLIOSession createSSLIOSession(IOSession session, SSLContext sslContext, SSLSetupHandler sslHandler) {
        return new SSLIOSession(session, sslContext, sslHandler);
    }

    protected NHttpServerIOTarget createSSLConnection(SSLIOSession sslioSession) {
        return super.createConnection(sslioSession);
    }

    @Override
    protected NHttpServerIOTarget createConnection(IOSession session) {
        SSLIOSession sslioSession = this.createSSLIOSession(session, this.sslContext, this.sslHandler);
        session.setAttribute("http.session.ssl", sslioSession);
        NHttpServerIOTarget conn = this.createSSLConnection(sslioSession);
        try {
            sslioSession.initialize();
        }
        catch (SSLException ex) {
            this.handler.exception((NHttpServerConnection)conn, ex);
            sslioSession.shutdown();
        }
        return conn;
    }

    @Override
    public void onConnected(NHttpServerIOTarget conn) {
        int timeout = HttpConnectionParams.getSoTimeout(this.params);
        conn.setSocketTimeout(timeout);
        this.handler.connected(conn);
    }
}

