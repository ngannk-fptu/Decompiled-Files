/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.reactor.SSLIOSession;
import org.apache.http.impl.nio.reactor.SSLSetupHandler;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientHandler;
import org.apache.http.nio.NHttpClientIOTarget;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class SSLClientIOEventDispatch
extends DefaultClientIOEventDispatch {
    private final SSLContext sslContext;
    private final SSLSetupHandler sslHandler;

    public SSLClientIOEventDispatch(NHttpClientHandler handler, SSLContext sslContext, SSLSetupHandler sslHandler, HttpParams params) {
        super(handler, params);
        Args.notNull(sslContext, "SSL context");
        Args.notNull(params, "HTTP parameters");
        this.sslContext = sslContext;
        this.sslHandler = sslHandler;
    }

    public SSLClientIOEventDispatch(NHttpClientHandler handler, SSLContext sslContext, HttpParams params) {
        this(handler, sslContext, null, params);
    }

    protected SSLIOSession createSSLIOSession(IOSession session, SSLContext sslContext, SSLSetupHandler sslHandler) {
        return new SSLIOSession(session, sslContext, sslHandler);
    }

    protected NHttpClientIOTarget createSSLConnection(SSLIOSession sslioSession) {
        return super.createConnection(sslioSession);
    }

    @Override
    protected NHttpClientIOTarget createConnection(IOSession session) {
        SSLIOSession sslioSession = this.createSSLIOSession(session, this.sslContext, this.sslHandler);
        session.setAttribute("http.session.ssl", sslioSession);
        NHttpClientIOTarget conn = this.createSSLConnection(sslioSession);
        try {
            sslioSession.initialize();
        }
        catch (SSLException ex) {
            this.handler.exception((NHttpClientConnection)conn, ex);
            sslioSession.shutdown();
        }
        return conn;
    }

    @Override
    public void onConnected(NHttpClientIOTarget conn) {
        int timeout = HttpConnectionParams.getSoTimeout(this.params);
        conn.setSocketTimeout(timeout);
        Object attachment = conn.getContext().getAttribute("http.session.attachment");
        this.handler.connected(conn, attachment);
    }
}

