/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.pool.PoolEntry;

@Contract(threading=ThreadingBehavior.SAFE)
class CPoolEntry
extends PoolEntry<HttpRoute, ManagedNHttpClientConnection> {
    private final Log log;
    private volatile int socketTimeout;
    private volatile boolean routeComplete;

    public CPoolEntry(Log log, String id, HttpRoute route, ManagedNHttpClientConnection conn, long timeToLive, TimeUnit timeUnit) {
        super(id, route, conn, timeToLive, timeUnit);
        this.log = log;
    }

    public boolean isRouteComplete() {
        return this.routeComplete;
    }

    public void markRouteComplete() {
        this.routeComplete = true;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void closeConnection() throws IOException {
        ManagedNHttpClientConnection conn = (ManagedNHttpClientConnection)this.getConnection();
        conn.close();
    }

    public void shutdownConnection() throws IOException {
        ManagedNHttpClientConnection conn = (ManagedNHttpClientConnection)this.getConnection();
        conn.shutdown();
    }

    @Override
    public boolean isExpired(long now) {
        boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug("Connection " + this + " expired @ " + new Date(this.getExpiry()));
        }
        return expired;
    }

    @Override
    public boolean isClosed() {
        ManagedNHttpClientConnection conn = (ManagedNHttpClientConnection)this.getConnection();
        return !conn.isOpen();
    }

    @Override
    public void close() {
        try {
            this.closeConnection();
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
}

