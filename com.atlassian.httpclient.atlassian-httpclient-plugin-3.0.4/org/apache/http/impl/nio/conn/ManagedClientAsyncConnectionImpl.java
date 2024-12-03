/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.impl.nio.conn.HttpPoolEntry;
import org.apache.http.nio.conn.ClientAsyncConnection;
import org.apache.http.nio.conn.ClientAsyncConnectionFactory;
import org.apache.http.nio.conn.ClientAsyncConnectionManager;
import org.apache.http.nio.conn.ManagedClientAsyncConnection;
import org.apache.http.nio.conn.scheme.AsyncScheme;
import org.apache.http.nio.conn.scheme.AsyncSchemeRegistry;
import org.apache.http.nio.conn.scheme.LayeringStrategy;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Deprecated
class ManagedClientAsyncConnectionImpl
implements ManagedClientAsyncConnection {
    private final ClientAsyncConnectionManager manager;
    private final ClientAsyncConnectionFactory connFactory;
    private volatile HttpPoolEntry poolEntry;
    private volatile boolean reusable;
    private volatile long duration;

    ManagedClientAsyncConnectionImpl(ClientAsyncConnectionManager manager, ClientAsyncConnectionFactory connFactory, HttpPoolEntry poolEntry) {
        this.manager = manager;
        this.connFactory = connFactory;
        this.poolEntry = poolEntry;
        this.reusable = true;
        this.duration = Long.MAX_VALUE;
    }

    HttpPoolEntry getPoolEntry() {
        return this.poolEntry;
    }

    HttpPoolEntry detach() {
        HttpPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }

    public ClientAsyncConnectionManager getManager() {
        return this.manager;
    }

    private ClientAsyncConnection getConnection() {
        HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        IOSession session = (IOSession)local.getConnection();
        return (ClientAsyncConnection)session.getAttribute("http.connection");
    }

    private ClientAsyncConnection ensureConnection() {
        HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        IOSession session = (IOSession)local.getConnection();
        return (ClientAsyncConnection)session.getAttribute("http.connection");
    }

    private HttpPoolEntry ensurePoolEntry() {
        HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        return local;
    }

    @Override
    public void close() throws IOException {
        ClientAsyncConnection conn = this.getConnection();
        if (conn != null) {
            conn.close();
        }
    }

    @Override
    public void shutdown() throws IOException {
        ClientAsyncConnection conn = this.getConnection();
        if (conn != null) {
            conn.shutdown();
        }
    }

    @Override
    public boolean isOpen() {
        ClientAsyncConnection conn = this.getConnection();
        return conn != null ? conn.isOpen() : false;
    }

    @Override
    public boolean isStale() {
        return this.isOpen();
    }

    @Override
    public void setSocketTimeout(int timeout) {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.setSocketTimeout(timeout);
    }

    @Override
    public int getSocketTimeout() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getSocketTimeout();
    }

    @Override
    public HttpConnectionMetrics getMetrics() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getMetrics();
    }

    @Override
    public InetAddress getLocalAddress() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getLocalPort();
    }

    @Override
    public InetAddress getRemoteAddress() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getRemoteAddress();
    }

    @Override
    public int getRemotePort() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getRemotePort();
    }

    @Override
    public int getStatus() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getStatus();
    }

    @Override
    public HttpRequest getHttpRequest() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getHttpRequest();
    }

    @Override
    public HttpResponse getHttpResponse() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getHttpResponse();
    }

    @Override
    public HttpContext getContext() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getContext();
    }

    @Override
    public void requestInput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.requestInput();
    }

    @Override
    public void suspendInput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.suspendInput();
    }

    @Override
    public void requestOutput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.requestOutput();
    }

    @Override
    public void suspendOutput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.suspendOutput();
    }

    @Override
    public void submitRequest(HttpRequest request) throws IOException, HttpException {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.submitRequest(request);
    }

    @Override
    public boolean isRequestSubmitted() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.isRequestSubmitted();
    }

    @Override
    public void resetOutput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.resetOutput();
    }

    @Override
    public void resetInput() {
        ClientAsyncConnection conn = this.ensureConnection();
        conn.resetInput();
    }

    @Override
    public boolean isSecure() {
        ClientAsyncConnection conn = this.ensureConnection();
        return conn.getIOSession() instanceof SSLIOSession;
    }

    @Override
    public HttpRoute getRoute() {
        HttpPoolEntry entry = this.ensurePoolEntry();
        return entry.getEffectiveRoute();
    }

    @Override
    public SSLSession getSSLSession() {
        ClientAsyncConnection conn = this.ensureConnection();
        IOSession ioSession = conn.getIOSession();
        return ioSession instanceof SSLIOSession ? ((SSLIOSession)ioSession).getSSLSession() : null;
    }

    @Override
    public Object getState() {
        HttpPoolEntry entry = this.ensurePoolEntry();
        return entry.getState();
    }

    @Override
    public void setState(Object state) {
        HttpPoolEntry entry = this.ensurePoolEntry();
        entry.setState(state);
    }

    @Override
    public void markReusable() {
        this.reusable = true;
    }

    @Override
    public void unmarkReusable() {
        this.reusable = false;
    }

    @Override
    public boolean isMarkedReusable() {
        return this.reusable;
    }

    @Override
    public void setIdleDuration(long duration, TimeUnit unit) {
        this.duration = duration > 0L ? unit.toMillis(duration) : -1L;
    }

    private AsyncSchemeRegistry getSchemeRegistry(HttpContext context) {
        AsyncSchemeRegistry reg = (AsyncSchemeRegistry)context.getAttribute("http.scheme-registry");
        if (reg == null) {
            reg = this.manager.getSchemeRegistry();
        }
        return reg;
    }

    @Override
    public synchronized void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        AsyncScheme scheme;
        LayeringStrategy layeringStrategy;
        HttpPoolEntry entry = this.ensurePoolEntry();
        RouteTracker tracker = entry.getTracker();
        if (tracker.isConnected()) {
            throw new IllegalStateException("Connection already open");
        }
        HttpHost target = route.getTargetHost();
        HttpHost proxy = route.getProxyHost();
        IOSession ioSession = (IOSession)entry.getConnection();
        if (proxy == null && (layeringStrategy = (scheme = this.getSchemeRegistry(context).getScheme(target)).getLayeringStrategy()) != null) {
            ioSession = layeringStrategy.layer(ioSession);
        }
        ClientAsyncConnection conn = this.connFactory.create("http-outgoing-" + entry.getId(), ioSession, params);
        ioSession.setAttribute("http.connection", conn);
        if (proxy == null) {
            tracker.connectTarget(conn.getIOSession() instanceof SSLIOSession);
        } else {
            tracker.connectProxy(proxy, false);
        }
    }

    @Override
    public synchronized void tunnelProxy(HttpHost next, HttpParams params) throws IOException {
        HttpPoolEntry entry = this.ensurePoolEntry();
        RouteTracker tracker = entry.getTracker();
        if (!tracker.isConnected()) {
            throw new IllegalStateException("Connection not open");
        }
        tracker.tunnelProxy(next, false);
    }

    @Override
    public synchronized void tunnelTarget(HttpParams params) throws IOException {
        HttpPoolEntry entry = this.ensurePoolEntry();
        RouteTracker tracker = entry.getTracker();
        if (!tracker.isConnected()) {
            throw new IllegalStateException("Connection not open");
        }
        if (tracker.isTunnelled()) {
            throw new IllegalStateException("Connection is already tunnelled");
        }
        tracker.tunnelTarget(false);
    }

    @Override
    public synchronized void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        HttpPoolEntry entry = this.ensurePoolEntry();
        RouteTracker tracker = entry.getTracker();
        if (!tracker.isConnected()) {
            throw new IllegalStateException("Connection not open");
        }
        if (!tracker.isTunnelled()) {
            throw new IllegalStateException("Protocol layering without a tunnel not supported");
        }
        if (tracker.isLayered()) {
            throw new IllegalStateException("Multiple protocol layering not supported");
        }
        HttpHost target = tracker.getTargetHost();
        AsyncScheme scheme = this.getSchemeRegistry(context).getScheme(target);
        LayeringStrategy layeringStrategy = scheme.getLayeringStrategy();
        if (layeringStrategy == null) {
            throw new IllegalStateException(scheme.getName() + " scheme does not provider support for protocol layering");
        }
        IOSession ioSession = (IOSession)entry.getConnection();
        ClientAsyncConnection conn = (ClientAsyncConnection)ioSession.getAttribute("http.connection");
        conn.upgrade(layeringStrategy.layer(ioSession));
        tracker.layerProtocol(layeringStrategy.isSecure());
    }

    @Override
    public synchronized void releaseConnection() {
        if (this.poolEntry == null) {
            return;
        }
        this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        this.poolEntry = null;
    }

    @Override
    public synchronized void abortConnection() {
        if (this.poolEntry == null) {
            return;
        }
        this.reusable = false;
        IOSession ioSession = (IOSession)this.poolEntry.getConnection();
        ClientAsyncConnection conn = (ClientAsyncConnection)ioSession.getAttribute("http.connection");
        try {
            conn.shutdown();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        this.poolEntry = null;
    }

    public synchronized String toString() {
        return this.poolEntry != null ? this.poolEntry.toString() : "released";
    }
}

