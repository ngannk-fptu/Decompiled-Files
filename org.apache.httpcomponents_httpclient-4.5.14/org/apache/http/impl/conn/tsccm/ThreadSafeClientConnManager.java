/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpParams
 *  org.apache.http.util.Args
 *  org.apache.http.util.Asserts
 */
package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.AbstractPoolEntry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.tsccm.AbstractConnPool;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.impl.conn.tsccm.BasicPooledConnAdapter;
import org.apache.http.impl.conn.tsccm.ConnPoolByRoute;
import org.apache.http.impl.conn.tsccm.PoolEntryRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class ThreadSafeClientConnManager
implements ClientConnectionManager {
    private final Log log;
    protected final SchemeRegistry schemeRegistry;
    protected final AbstractConnPool connectionPool;
    protected final ConnPoolByRoute pool;
    protected final ClientConnectionOperator connOperator;
    protected final ConnPerRouteBean connPerRoute;

    public ThreadSafeClientConnManager(SchemeRegistry schreg) {
        this(schreg, -1L, TimeUnit.MILLISECONDS);
    }

    public ThreadSafeClientConnManager() {
        this(SchemeRegistryFactory.createDefault());
    }

    public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit) {
        this(schreg, connTTL, connTTLTimeUnit, new ConnPerRouteBean());
    }

    public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit, ConnPerRouteBean connPerRoute) {
        Args.notNull((Object)schreg, (String)"Scheme registry");
        this.log = LogFactory.getLog(this.getClass());
        this.schemeRegistry = schreg;
        this.connPerRoute = connPerRoute;
        this.connOperator = this.createConnectionOperator(schreg);
        this.pool = this.createConnectionPool(connTTL, connTTLTimeUnit);
        this.connectionPool = this.pool;
    }

    @Deprecated
    public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
        Args.notNull((Object)schreg, (String)"Scheme registry");
        this.log = LogFactory.getLog(this.getClass());
        this.schemeRegistry = schreg;
        this.connPerRoute = new ConnPerRouteBean();
        this.connOperator = this.createConnectionOperator(schreg);
        this.pool = (ConnPoolByRoute)this.createConnectionPool(params);
        this.connectionPool = this.pool;
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

    @Deprecated
    protected AbstractConnPool createConnectionPool(HttpParams params) {
        return new ConnPoolByRoute(this.connOperator, params);
    }

    protected ConnPoolByRoute createConnectionPool(long connTTL, TimeUnit connTTLTimeUnit) {
        return new ConnPoolByRoute(this.connOperator, this.connPerRoute, 20, connTTL, connTTLTimeUnit);
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        return new DefaultClientConnectionOperator(schreg);
    }

    @Override
    public SchemeRegistry getSchemeRegistry() {
        return this.schemeRegistry;
    }

    @Override
    public ClientConnectionRequest requestConnection(final HttpRoute route, Object state) {
        final PoolEntryRequest poolRequest = this.pool.requestPoolEntry(route, state);
        return new ClientConnectionRequest(){

            @Override
            public void abortRequest() {
                poolRequest.abortRequest();
            }

            @Override
            public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException {
                Args.notNull((Object)route, (String)"Route");
                if (ThreadSafeClientConnManager.this.log.isDebugEnabled()) {
                    ThreadSafeClientConnManager.this.log.debug((Object)("Get connection: " + route + ", timeout = " + timeout));
                }
                BasicPoolEntry entry = poolRequest.getPoolEntry(timeout, timeUnit);
                return new BasicPooledConnAdapter(ThreadSafeClientConnManager.this, (AbstractPoolEntry)entry);
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
        Args.check((boolean)(conn instanceof BasicPooledConnAdapter), (String)"Connection class mismatch, connection not obtained from this manager");
        BasicPooledConnAdapter hca = (BasicPooledConnAdapter)conn;
        if (hca.getPoolEntry() != null) {
            Asserts.check((hca.getManager() == this ? 1 : 0) != 0, (String)"Connection not obtained from this manager");
        }
        BasicPooledConnAdapter basicPooledConnAdapter = hca;
        synchronized (basicPooledConnAdapter) {
            BasicPoolEntry entry = (BasicPoolEntry)hca.getPoolEntry();
            if (entry == null) {
                return;
            }
            try {
                if (hca.isOpen() && !hca.isMarkedReusable()) {
                    hca.shutdown();
                }
            }
            catch (IOException iox) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Exception shutting down released connection.", (Throwable)iox);
                }
            }
            finally {
                boolean reusable = hca.isMarkedReusable();
                if (this.log.isDebugEnabled()) {
                    if (reusable) {
                        this.log.debug((Object)"Released connection is reusable.");
                    } else {
                        this.log.debug((Object)"Released connection is not reusable.");
                    }
                }
                hca.detach();
                this.pool.freeEntry(entry, reusable, validDuration, timeUnit);
            }
        }
    }

    @Override
    public void shutdown() {
        this.log.debug((Object)"Shutting down");
        this.pool.shutdown();
    }

    public int getConnectionsInPool(HttpRoute route) {
        return this.pool.getConnectionsInPool(route);
    }

    public int getConnectionsInPool() {
        return this.pool.getConnectionsInPool();
    }

    @Override
    public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Closing connections idle longer than " + idleTimeout + " " + (Object)((Object)timeUnit)));
        }
        this.pool.closeIdleConnections(idleTimeout, timeUnit);
    }

    @Override
    public void closeExpiredConnections() {
        this.log.debug((Object)"Closing expired connections");
        this.pool.closeExpiredConnections();
    }

    public int getMaxTotal() {
        return this.pool.getMaxTotalConnections();
    }

    public void setMaxTotal(int max) {
        this.pool.setMaxTotalConnections(max);
    }

    public int getDefaultMaxPerRoute() {
        return this.connPerRoute.getDefaultMaxPerRoute();
    }

    public void setDefaultMaxPerRoute(int max) {
        this.connPerRoute.setDefaultMaxPerRoute(max);
    }

    public int getMaxForRoute(HttpRoute route) {
        return this.connPerRoute.getMaxForRoute(route);
    }

    public void setMaxForRoute(HttpRoute route, int max) {
        this.connPerRoute.setMaxForRoute(route, max);
    }
}

