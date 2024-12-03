/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.net.InetAddress;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.impl.nio.conn.CPoolEntry;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.protocol.HttpContext;

class CPoolProxy
implements ManagedNHttpClientConnection {
    private volatile CPoolEntry poolEntry;

    CPoolProxy(CPoolEntry entry) {
        this.poolEntry = entry;
    }

    CPoolEntry getPoolEntry() {
        return this.poolEntry;
    }

    CPoolEntry detach() {
        CPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }

    ManagedNHttpClientConnection getConnection() {
        CPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return (ManagedNHttpClientConnection)local.getConnection();
    }

    ManagedNHttpClientConnection getValidConnection() {
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn == null) {
            throw new ConnectionShutdownException();
        }
        return conn;
    }

    @Override
    public void close() throws IOException {
        CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.closeConnection();
        }
    }

    @Override
    public void shutdown() throws IOException {
        CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.shutdownConnection();
        }
    }

    @Override
    public HttpConnectionMetrics getMetrics() {
        return this.getValidConnection().getMetrics();
    }

    @Override
    public void requestInput() {
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            conn.requestInput();
        }
    }

    @Override
    public void suspendInput() {
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            conn.suspendInput();
        }
    }

    @Override
    public void requestOutput() {
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            conn.requestOutput();
        }
    }

    @Override
    public void suspendOutput() {
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            conn.suspendOutput();
        }
    }

    @Override
    public InetAddress getLocalAddress() {
        return this.getValidConnection().getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return this.getValidConnection().getLocalPort();
    }

    @Override
    public InetAddress getRemoteAddress() {
        return this.getValidConnection().getRemoteAddress();
    }

    @Override
    public int getRemotePort() {
        return this.getValidConnection().getRemotePort();
    }

    @Override
    public boolean isOpen() {
        CPoolEntry local = this.poolEntry;
        return local != null ? !local.isClosed() : false;
    }

    @Override
    public boolean isStale() {
        ManagedNHttpClientConnection conn = this.getConnection();
        return conn != null ? !conn.isOpen() : false;
    }

    @Override
    public void setSocketTimeout(int i) {
        this.getValidConnection().setSocketTimeout(i);
    }

    @Override
    public int getSocketTimeout() {
        return this.getValidConnection().getSocketTimeout();
    }

    @Override
    public void submitRequest(HttpRequest request) throws IOException, HttpException {
        this.getValidConnection().submitRequest(request);
    }

    @Override
    public boolean isRequestSubmitted() {
        return this.getValidConnection().isRequestSubmitted();
    }

    @Override
    public void resetOutput() {
        this.getValidConnection().resetOutput();
    }

    @Override
    public void resetInput() {
        this.getValidConnection().resetInput();
    }

    @Override
    public int getStatus() {
        return this.getValidConnection().getStatus();
    }

    @Override
    public HttpRequest getHttpRequest() {
        return this.getValidConnection().getHttpRequest();
    }

    @Override
    public HttpResponse getHttpResponse() {
        return this.getValidConnection().getHttpResponse();
    }

    @Override
    public HttpContext getContext() {
        return this.getValidConnection().getContext();
    }

    public static NHttpClientConnection newProxy(CPoolEntry poolEntry) {
        return new CPoolProxy(poolEntry);
    }

    private static CPoolProxy getProxy(NHttpClientConnection conn) {
        if (!CPoolProxy.class.isInstance(conn)) {
            throw new IllegalStateException("Unexpected connection proxy class: " + conn.getClass());
        }
        return (CPoolProxy)CPoolProxy.class.cast(conn);
    }

    public static CPoolEntry getPoolEntry(NHttpClientConnection proxy) {
        CPoolEntry entry = CPoolProxy.getProxy(proxy).getPoolEntry();
        if (entry == null) {
            throw new ConnectionShutdownException();
        }
        return entry;
    }

    public static CPoolEntry detach(NHttpClientConnection proxy) {
        return CPoolProxy.getProxy(proxy).detach();
    }

    @Override
    public String getId() {
        return this.getValidConnection().getId();
    }

    @Override
    public void bind(IOSession ioSession) {
        this.getValidConnection().bind(ioSession);
    }

    @Override
    public IOSession getIOSession() {
        return this.getValidConnection().getIOSession();
    }

    @Override
    public SSLSession getSSLSession() {
        return this.getValidConnection().getSSLSession();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CPoolProxy{");
        ManagedNHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            sb.append(conn);
        } else {
            sb.append("detached");
        }
        sb.append('}');
        return sb.toString();
    }
}

