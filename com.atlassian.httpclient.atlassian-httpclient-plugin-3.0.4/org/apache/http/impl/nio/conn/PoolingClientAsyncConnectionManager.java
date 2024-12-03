/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.nio.conn.AsyncSchemeRegistryFactory;
import org.apache.http.impl.nio.conn.DefaultClientAsyncConnectionFactory;
import org.apache.http.impl.nio.conn.HttpNIOConnPool;
import org.apache.http.impl.nio.conn.HttpPoolEntry;
import org.apache.http.impl.nio.conn.ManagedClientAsyncConnectionImpl;
import org.apache.http.nio.conn.ClientAsyncConnectionFactory;
import org.apache.http.nio.conn.ClientAsyncConnectionManager;
import org.apache.http.nio.conn.ManagedClientAsyncConnection;
import org.apache.http.nio.conn.scheme.AsyncSchemeRegistry;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;

@Deprecated
public class PoolingClientAsyncConnectionManager
implements ClientAsyncConnectionManager,
ConnPoolControl<HttpRoute> {
    private final Log log = LogFactory.getLog(this.getClass());
    private final ConnectingIOReactor ioReactor;
    private final HttpNIOConnPool pool;
    private final AsyncSchemeRegistry schemeRegistry;
    private final ClientAsyncConnectionFactory connFactory;

    public PoolingClientAsyncConnectionManager(ConnectingIOReactor ioReactor, AsyncSchemeRegistry schemeRegistry, long timeToLive, TimeUnit timeUnit) {
        Args.notNull(ioReactor, "I/O reactor");
        Args.notNull(schemeRegistry, "Scheme registory");
        Args.notNull(timeUnit, "Time unit");
        this.ioReactor = ioReactor;
        this.pool = new HttpNIOConnPool(this.log, ioReactor, schemeRegistry, timeToLive, timeUnit);
        this.schemeRegistry = schemeRegistry;
        this.connFactory = this.createClientAsyncConnectionFactory();
    }

    public PoolingClientAsyncConnectionManager(ConnectingIOReactor ioReactor, AsyncSchemeRegistry schemeRegistry) throws IOReactorException {
        this(ioReactor, schemeRegistry, -1L, TimeUnit.MILLISECONDS);
    }

    public PoolingClientAsyncConnectionManager(ConnectingIOReactor ioReactor) throws IOReactorException {
        this(ioReactor, AsyncSchemeRegistryFactory.createDefault());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
        }
        finally {
            super.finalize();
        }
    }

    protected ClientAsyncConnectionFactory createClientAsyncConnectionFactory() {
        return new DefaultClientAsyncConnectionFactory();
    }

    @Override
    public AsyncSchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    @Override
    public void execute(IOEventDispatch eventDispatch) throws IOException {
        this.ioReactor.execute(eventDispatch);
    }

    @Override
    public IOReactorStatus getStatus() {
        return this.ioReactor.getStatus();
    }

    @Override
    public void shutdown(long waitMs) throws IOException {
        this.log.debug("Connection manager is shutting down");
        this.pool.shutdown(waitMs);
        this.log.debug("Connection manager shut down");
    }

    @Override
    public void shutdown() throws IOException {
        this.log.debug("Connection manager is shutting down");
        this.pool.shutdown(2000L);
        this.log.debug("Connection manager shut down");
    }

    private String format(HttpRoute route, Object state) {
        StringBuilder buf = new StringBuilder();
        buf.append("[route: ").append(route).append("]");
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    private String formatStats(HttpRoute route) {
        StringBuilder buf = new StringBuilder();
        PoolStats totals = this.pool.getTotalStats();
        PoolStats stats = this.pool.getStats(route);
        buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
        buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
        buf.append(" of ").append(stats.getMax()).append("; ");
        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
        buf.append(" of ").append(totals.getMax()).append("]");
        return buf.toString();
    }

    private String format(HttpPoolEntry entry) {
        StringBuilder buf = new StringBuilder();
        buf.append("[id: ").append(entry.getId()).append("]");
        buf.append("[route: ").append(entry.getRoute()).append("]");
        Object state = entry.getState();
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }

    @Override
    public Future<ManagedClientAsyncConnection> leaseConnection(HttpRoute route, Object state, long connectTimeout, TimeUnit timeUnit, FutureCallback<ManagedClientAsyncConnection> callback) {
        Args.notNull(route, "HTTP route");
        Args.notNull(timeUnit, "Time unit");
        if (this.log.isDebugEnabled()) {
            this.log.debug("Connection request: " + this.format(route, state) + this.formatStats(route));
        }
        BasicFuture<ManagedClientAsyncConnection> future = new BasicFuture<ManagedClientAsyncConnection>(callback);
        this.pool.lease(route, state, connectTimeout, timeUnit, new InternalPoolEntryCallback(future));
        return future;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseConnection(ManagedClientAsyncConnection conn, long keepalive, TimeUnit timeUnit) {
        Args.notNull(conn, "HTTP connection");
        if (!(conn instanceof ManagedClientAsyncConnectionImpl)) {
            throw new IllegalArgumentException("Connection class mismatch, connection not obtained from this manager");
        }
        Args.notNull(timeUnit, "Time unit");
        ManagedClientAsyncConnectionImpl managedConn = (ManagedClientAsyncConnectionImpl)conn;
        ClientAsyncConnectionManager manager = managedConn.getManager();
        if (manager != null && manager != this) {
            throw new IllegalArgumentException("Connection not obtained from this manager");
        }
        if (this.pool.isShutdown()) {
            return;
        }
        ManagedClientAsyncConnectionImpl managedClientAsyncConnectionImpl = managedConn;
        synchronized (managedClientAsyncConnectionImpl) {
            HttpPoolEntry entry = managedConn.getPoolEntry();
            if (entry == null) {
                return;
            }
            try {
                block16: {
                    if (managedConn.isOpen() && !managedConn.isMarkedReusable()) {
                        try {
                            managedConn.shutdown();
                        }
                        catch (IOException iox) {
                            if (!this.log.isDebugEnabled()) break block16;
                            this.log.debug("I/O exception shutting down released connection", iox);
                        }
                    }
                }
                if (managedConn.isOpen()) {
                    entry.updateExpiry(keepalive, timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
                    if (this.log.isDebugEnabled()) {
                        String s = keepalive > 0L ? "for " + keepalive + " " + (Object)((Object)timeUnit) : "indefinitely";
                        this.log.debug("Connection " + this.format(entry) + " can be kept alive " + s);
                    }
                    managedConn.setSocketTimeout(0);
                }
            }
            finally {
                this.pool.release(managedConn.detach(), managedConn.isMarkedReusable());
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection released: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
            }
        }
    }

    @Override
    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }

    @Override
    public PoolStats getStats(HttpRoute route) {
        return this.pool.getStats(route);
    }

    @Override
    public void setMaxTotal(int max) {
        this.pool.setMaxTotal(max);
    }

    @Override
    public void setDefaultMaxPerRoute(int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }

    @Override
    public void setMaxPerRoute(HttpRoute route, int max) {
        this.pool.setMaxPerRoute(route, max);
    }

    @Override
    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }

    @Override
    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }

    @Override
    public int getMaxPerRoute(HttpRoute route) {
        return this.pool.getMaxPerRoute(route);
    }

    public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Closing connections idle longer than " + idleTimeout + " " + (Object)((Object)timeUnit));
        }
        this.pool.closeIdle(idleTimeout, timeUnit);
    }

    public void closeExpiredConnections() {
        this.log.debug("Closing expired connections");
        this.pool.closeExpired();
    }

    class InternalPoolEntryCallback
    implements FutureCallback<HttpPoolEntry> {
        private final BasicFuture<ManagedClientAsyncConnection> future;

        public InternalPoolEntryCallback(BasicFuture<ManagedClientAsyncConnection> future) {
            this.future = future;
        }

        @Override
        public void completed(HttpPoolEntry entry) {
            ManagedClientAsyncConnectionImpl conn;
            if (PoolingClientAsyncConnectionManager.this.log.isDebugEnabled()) {
                PoolingClientAsyncConnectionManager.this.log.debug("Connection leased: " + PoolingClientAsyncConnectionManager.this.format(entry) + PoolingClientAsyncConnectionManager.this.formatStats((HttpRoute)entry.getRoute()));
            }
            if (!this.future.completed(conn = new ManagedClientAsyncConnectionImpl(PoolingClientAsyncConnectionManager.this, PoolingClientAsyncConnectionManager.this.connFactory, entry))) {
                PoolingClientAsyncConnectionManager.this.pool.release(entry, true);
            }
        }

        @Override
        public void failed(Exception ex) {
            if (PoolingClientAsyncConnectionManager.this.log.isDebugEnabled()) {
                PoolingClientAsyncConnectionManager.this.log.debug("Connection request failed", ex);
            }
            this.future.failed(ex);
        }

        @Override
        public void cancelled() {
            PoolingClientAsyncConnectionManager.this.log.debug("Connection request cancelled");
            this.future.cancel(true);
        }
    }
}

