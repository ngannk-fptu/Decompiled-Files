/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.nio.conn.CPoolEntry;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.pool.AbstractNIOConnPool;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.pool.SocketAddressResolver;
import org.apache.http.nio.reactor.ConnectingIOReactor;

@Contract(threading=ThreadingBehavior.SAFE)
class CPool
extends AbstractNIOConnPool<HttpRoute, ManagedNHttpClientConnection, CPoolEntry> {
    private final Log log = LogFactory.getLog(CPool.class);
    private final long timeToLive;
    private final TimeUnit timeUnit;

    public CPool(ConnectingIOReactor ioReactor, NIOConnFactory<HttpRoute, ManagedNHttpClientConnection> connFactory, SocketAddressResolver<HttpRoute> addressResolver, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit timeUnit) {
        super(ioReactor, connFactory, addressResolver, defaultMaxPerRoute, maxTotal);
        this.timeToLive = timeToLive;
        this.timeUnit = timeUnit;
    }

    @Override
    protected CPoolEntry createEntry(HttpRoute route, ManagedNHttpClientConnection conn) {
        CPoolEntry entry = new CPoolEntry(this.log, conn.getId(), route, conn, this.timeToLive, this.timeUnit);
        entry.setSocketTimeout(conn.getSocketTimeout());
        return entry;
    }

    @Override
    protected void onLease(CPoolEntry entry) {
        NHttpClientConnection conn = (NHttpClientConnection)entry.getConnection();
        conn.setSocketTimeout(entry.getSocketTimeout());
    }

    @Override
    protected void onRelease(CPoolEntry entry) {
        NHttpClientConnection conn = (NHttpClientConnection)entry.getConnection();
        conn.setSocketTimeout(0);
    }
}

