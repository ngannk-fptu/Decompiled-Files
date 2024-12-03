/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncClientExchangeHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

abstract class AbstractClientExchangeHandler
implements HttpAsyncClientExchangeHandler {
    private static final AtomicLong COUNTER = new AtomicLong(1L);
    protected final Log log;
    private final long id;
    private final HttpClientContext localContext;
    private final NHttpClientConnectionManager connmgr;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final ConnectionKeepAliveStrategy keepaliveStrategy;
    private final AtomicReference<Future<NHttpClientConnection>> connectionFutureRef;
    private final AtomicReference<NHttpClientConnection> managedConnRef;
    private final AtomicReference<HttpRoute> routeRef;
    private final AtomicReference<RouteTracker> routeTrackerRef;
    private final AtomicBoolean routeEstablished;
    private final AtomicReference<Long> validDurationRef;
    private final AtomicReference<HttpRequestWrapper> requestRef;
    private final AtomicReference<HttpResponse> responseRef;
    private final AtomicBoolean completed;
    private final AtomicBoolean closed;

    AbstractClientExchangeHandler(Log log, HttpClientContext localContext, NHttpClientConnectionManager connmgr, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy) {
        this.log = log;
        this.id = COUNTER.getAndIncrement();
        this.localContext = localContext;
        this.connmgr = connmgr;
        this.connReuseStrategy = connReuseStrategy;
        this.keepaliveStrategy = keepaliveStrategy;
        this.connectionFutureRef = new AtomicReference<Object>(null);
        this.managedConnRef = new AtomicReference<Object>(null);
        this.routeRef = new AtomicReference<Object>(null);
        this.routeTrackerRef = new AtomicReference<Object>(null);
        this.routeEstablished = new AtomicBoolean(false);
        this.validDurationRef = new AtomicReference<Object>(null);
        this.requestRef = new AtomicReference<Object>(null);
        this.responseRef = new AtomicReference<Object>(null);
        this.completed = new AtomicBoolean(false);
        this.closed = new AtomicBoolean(false);
    }

    final long getId() {
        return this.id;
    }

    final boolean isCompleted() {
        return this.completed.get();
    }

    final void markCompleted() {
        this.completed.set(true);
    }

    final void markConnectionNonReusable() {
        this.validDurationRef.set(null);
    }

    final boolean isRouteEstablished() {
        return this.routeEstablished.get();
    }

    final HttpRoute getRoute() {
        return this.routeRef.get();
    }

    final void setRoute(HttpRoute route) {
        this.routeRef.set(route);
    }

    final HttpRequestWrapper getCurrentRequest() {
        return this.requestRef.get();
    }

    final void setCurrentRequest(HttpRequestWrapper request) {
        this.requestRef.set(request);
    }

    final HttpResponse getCurrentResponse() {
        return this.responseRef.get();
    }

    final void setCurrentResponse(HttpResponse response) {
        this.responseRef.set(response);
    }

    final HttpRoute getActualRoute() {
        RouteTracker routeTracker = this.routeTrackerRef.get();
        return routeTracker != null ? routeTracker.toRoute() : null;
    }

    final void verifytRoute() {
        if (!this.routeEstablished.get() && this.routeTrackerRef.get() == null) {
            NHttpClientConnection managedConn = this.managedConnRef.get();
            Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
            boolean routeComplete = this.connmgr.isRouteComplete(managedConn);
            this.routeEstablished.set(routeComplete);
            if (!routeComplete) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + this.id + "] Start connection routing");
                }
                HttpRoute route = this.routeRef.get();
                this.routeTrackerRef.set(new RouteTracker(route));
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + this.id + "] Connection route already established");
            }
        }
    }

    final void onRouteToTarget() throws IOException {
        NHttpClientConnection managedConn = this.managedConnRef.get();
        Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
        HttpRoute route = this.routeRef.get();
        Asserts.check(route != null, "Inconsistent state: HTTP route is null");
        RouteTracker routeTracker = this.routeTrackerRef.get();
        Asserts.check(routeTracker != null, "Inconsistent state: HTTP route tracker");
        this.connmgr.startRoute(managedConn, route, this.localContext);
        routeTracker.connectTarget(route.isSecure());
    }

    final void onRouteToProxy() throws IOException {
        NHttpClientConnection managedConn = this.managedConnRef.get();
        Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
        HttpRoute route = this.routeRef.get();
        Asserts.check(route != null, "Inconsistent state: HTTP route is null");
        RouteTracker routeTracker = this.routeTrackerRef.get();
        Asserts.check(routeTracker != null, "Inconsistent state: HTTP route tracker");
        this.connmgr.startRoute(managedConn, route, this.localContext);
        HttpHost proxy = route.getProxyHost();
        routeTracker.connectProxy(proxy, false);
    }

    final void onRouteUpgrade() throws IOException {
        NHttpClientConnection managedConn = this.managedConnRef.get();
        Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
        HttpRoute route = this.routeRef.get();
        Asserts.check(route != null, "Inconsistent state: HTTP route is null");
        RouteTracker routeTracker = this.routeTrackerRef.get();
        Asserts.check(routeTracker != null, "Inconsistent state: HTTP route tracker");
        this.connmgr.upgrade(managedConn, route, this.localContext);
        routeTracker.layerProtocol(route.isSecure());
    }

    final void onRouteTunnelToTarget() {
        RouteTracker routeTracker = this.routeTrackerRef.get();
        Asserts.check(routeTracker != null, "Inconsistent state: HTTP route tracker");
        routeTracker.tunnelTarget(false);
    }

    final void onRouteComplete() {
        NHttpClientConnection managedConn = this.managedConnRef.get();
        Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
        HttpRoute route = this.routeRef.get();
        Asserts.check(route != null, "Inconsistent state: HTTP route is null");
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.id + "] route completed");
        }
        this.connmgr.routeComplete(managedConn, route, this.localContext);
        this.routeEstablished.set(true);
        this.routeTrackerRef.set(null);
    }

    final NHttpClientConnection getConnection() {
        return this.managedConnRef.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void releaseConnection() {
        NHttpClientConnection localConn = this.managedConnRef.getAndSet(null);
        if (localConn != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + this.id + "] releasing connection");
            }
            localConn.getContext().removeAttribute("http.nio.exchange-handler");
            Long validDuration = this.validDurationRef.get();
            if (validDuration != null) {
                Object userToken = this.localContext.getUserToken();
                this.connmgr.releaseConnection(localConn, userToken, validDuration, TimeUnit.MILLISECONDS);
            } else {
                try {
                    localConn.close();
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("[exchange: " + this.id + "] connection discarded");
                    }
                }
                catch (IOException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("[exchange: " + this.id + "] " + ex.getMessage(), ex);
                    }
                }
                finally {
                    this.connmgr.releaseConnection(localConn, null, 0L, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void discardConnection() {
        NHttpClientConnection localConn = this.managedConnRef.getAndSet(null);
        if (localConn != null) {
            try {
                localConn.shutdown();
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + this.id + "] connection aborted");
                }
            }
            catch (IOException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("[exchange: " + this.id + "] " + ex.getMessage(), ex);
                }
            }
            finally {
                this.connmgr.releaseConnection(localConn, null, 0L, TimeUnit.MILLISECONDS);
            }
        }
    }

    final boolean manageConnectionPersistence() {
        boolean keepAlive;
        HttpResponse response = this.responseRef.get();
        Asserts.check(response != null, "Inconsistent state: HTTP response");
        NHttpClientConnection managedConn = this.managedConnRef.get();
        Asserts.check(managedConn != null, "Inconsistent state: managed connection is null");
        boolean bl = keepAlive = managedConn.isOpen() && this.connReuseStrategy.keepAlive(response, this.localContext);
        if (keepAlive) {
            long validDuration = this.keepaliveStrategy.getKeepAliveDuration(response, this.localContext);
            if (this.log.isDebugEnabled()) {
                String s = validDuration > 0L ? "for " + validDuration + " " + (Object)((Object)TimeUnit.MILLISECONDS) : "indefinitely";
                this.log.debug("[exchange: " + this.id + "] Connection can be kept alive " + s);
            }
            this.validDurationRef.set(validDuration);
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + this.id + "] Connection cannot be kept alive");
            }
            this.validDurationRef.set(null);
        }
        return keepAlive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void connectionAllocated(NHttpClientConnection managedConn) {
        try {
            HttpContext context;
            if (this.log.isDebugEnabled()) {
                this.log.debug("[exchange: " + this.id + "] Connection allocated: " + managedConn);
            }
            this.connectionFutureRef.set(null);
            this.managedConnRef.set(managedConn);
            if (this.closed.get()) {
                this.discardConnection();
                return;
            }
            if (this.connmgr.isRouteComplete(managedConn)) {
                this.routeEstablished.set(true);
                this.routeTrackerRef.set(null);
            }
            HttpContext httpContext = context = managedConn.getContext();
            synchronized (httpContext) {
                context.setAttribute("http.nio.exchange-handler", this);
                if (managedConn.isStale()) {
                    this.failed(new ConnectionClosedException("Connection closed"));
                } else {
                    managedConn.requestOutput();
                }
            }
        }
        catch (ConnectionShutdownException runex) {
            this.failed(runex);
        }
        catch (RuntimeException runex) {
            this.failed(runex);
            throw runex;
        }
    }

    private void connectionRequestFailed(Exception ex) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.id + "] connection request failed");
        }
        this.connectionFutureRef.set(null);
        this.failed(ex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void connectionRequestCancelled() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.id + "] Connection request cancelled");
        }
        this.connectionFutureRef.set(null);
        try {
            this.executionCancelled();
        }
        finally {
            this.close();
        }
    }

    final void requestConnection() {
        HttpRoute route = this.routeRef.get();
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.id + "] Request connection for " + route);
        }
        this.discardConnection();
        this.validDurationRef.set(null);
        this.routeTrackerRef.set(null);
        this.routeEstablished.set(false);
        Object userToken = this.localContext.getUserToken();
        RequestConfig config = this.localContext.getRequestConfig();
        this.connectionFutureRef.set(this.connmgr.requestConnection(route, userToken, config.getConnectTimeout(), config.getConnectionRequestTimeout(), TimeUnit.MILLISECONDS, new FutureCallback<NHttpClientConnection>(){

            @Override
            public void completed(NHttpClientConnection managedConn) {
                AbstractClientExchangeHandler.this.connectionAllocated(managedConn);
            }

            @Override
            public void failed(Exception ex) {
                AbstractClientExchangeHandler.this.connectionRequestFailed(ex);
            }

            @Override
            public void cancelled() {
                AbstractClientExchangeHandler.this.connectionRequestCancelled();
            }
        }));
    }

    abstract void start() throws HttpException, IOException;

    abstract void releaseResources();

    abstract void executionFailed(Exception var1);

    abstract boolean executionCancelled();

    @Override
    public final void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.discardConnection();
            this.releaseResources();
        }
    }

    @Override
    public final boolean isDone() {
        return this.completed.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void failed(Exception ex) {
        if (this.closed.compareAndSet(false, true)) {
            try {
                this.executionFailed(ex);
            }
            finally {
                this.discardConnection();
                this.releaseResources();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final boolean cancel() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.id + "] Cancelled");
        }
        if (this.closed.compareAndSet(false, true)) {
            try {
                Future connectionFuture = this.connectionFutureRef.getAndSet(null);
                if (connectionFuture != null) {
                    connectionFuture.cancel(true);
                }
                boolean bl = this.executionCancelled();
                return bl;
            }
            finally {
                this.discardConnection();
                this.releaseResources();
            }
        }
        return false;
    }
}

