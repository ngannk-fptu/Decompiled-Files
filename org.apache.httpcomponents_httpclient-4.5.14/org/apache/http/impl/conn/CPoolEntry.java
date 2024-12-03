/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.http.HttpClientConnection
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.pool.PoolEntry
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.PoolEntry;

@Contract(threading=ThreadingBehavior.SAFE)
class CPoolEntry
extends PoolEntry<HttpRoute, ManagedHttpClientConnection> {
    private final Log log;
    private volatile boolean routeComplete;

    public CPoolEntry(Log log, String id, HttpRoute route, ManagedHttpClientConnection conn, long timeToLive, TimeUnit timeUnit) {
        super(id, (Object)route, (Object)conn, timeToLive, timeUnit);
        this.log = log;
    }

    public void markRouteComplete() {
        this.routeComplete = true;
    }

    public boolean isRouteComplete() {
        return this.routeComplete;
    }

    public void closeConnection() throws IOException {
        HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        conn.close();
    }

    public void shutdownConnection() throws IOException {
        HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        conn.shutdown();
    }

    public boolean isExpired(long now) {
        boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug((Object)("Connection " + (Object)((Object)this) + " expired @ " + new Date(this.getExpiry())));
        }
        return expired;
    }

    public boolean isClosed() {
        HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        return !conn.isOpen();
    }

    public void close() {
        try {
            this.closeConnection();
        }
        catch (IOException ex) {
            this.log.debug((Object)"I/O error closing connection", (Throwable)ex);
        }
    }
}

