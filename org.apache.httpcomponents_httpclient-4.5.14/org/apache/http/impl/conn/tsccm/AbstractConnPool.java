/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.IdleConnectionHandler;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.impl.conn.tsccm.BasicPoolEntryRef;
import org.apache.http.impl.conn.tsccm.PoolEntryRequest;
import org.apache.http.util.Args;

@Deprecated
public abstract class AbstractConnPool {
    private final Log log = LogFactory.getLog(this.getClass());
    protected final Lock poolLock;
    protected Set<BasicPoolEntry> leasedConnections = new HashSet<BasicPoolEntry>();
    protected int numConnections;
    protected volatile boolean isShutDown;
    protected Set<BasicPoolEntryRef> issuedConnections;
    protected ReferenceQueue<Object> refQueue;
    protected IdleConnectionHandler idleConnHandler = new IdleConnectionHandler();

    protected AbstractConnPool() {
        this.poolLock = new ReentrantLock();
    }

    public void enableConnectionGC() throws IllegalStateException {
    }

    public final BasicPoolEntry getEntry(HttpRoute route, Object state, long timeout, TimeUnit timeUnit) throws ConnectionPoolTimeoutException, InterruptedException {
        return this.requestPoolEntry(route, state).getPoolEntry(timeout, timeUnit);
    }

    public abstract PoolEntryRequest requestPoolEntry(HttpRoute var1, Object var2);

    public abstract void freeEntry(BasicPoolEntry var1, boolean var2, long var3, TimeUnit var5);

    public void handleReference(Reference<?> ref) {
    }

    protected abstract void handleLostEntry(HttpRoute var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
        Args.notNull((Object)((Object)timeUnit), (String)"Time unit");
        this.poolLock.lock();
        try {
            this.idleConnHandler.closeIdleConnections(timeUnit.toMillis(idletime));
        }
        finally {
            this.poolLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void closeExpiredConnections() {
        this.poolLock.lock();
        try {
            this.idleConnHandler.closeExpiredConnections();
        }
        finally {
            this.poolLock.unlock();
        }
    }

    public abstract void deleteClosedConnections();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        this.poolLock.lock();
        try {
            if (this.isShutDown) {
                return;
            }
            Iterator<BasicPoolEntry> iter = this.leasedConnections.iterator();
            while (iter.hasNext()) {
                BasicPoolEntry entry = iter.next();
                iter.remove();
                this.closeConnection(entry.getConnection());
            }
            this.idleConnHandler.removeAll();
            this.isShutDown = true;
        }
        finally {
            this.poolLock.unlock();
        }
    }

    protected void closeConnection(OperatedClientConnection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (IOException ex) {
                this.log.debug((Object)"I/O error closing connection", (Throwable)ex);
            }
        }
    }
}

