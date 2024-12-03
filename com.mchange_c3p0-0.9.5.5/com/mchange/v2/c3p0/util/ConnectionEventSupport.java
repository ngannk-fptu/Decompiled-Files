/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.util;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

public class ConnectionEventSupport {
    PooledConnection source;
    HashSet mlisteners = new HashSet();

    public ConnectionEventSupport(PooledConnection source) {
        this.source = source;
    }

    public synchronized void addConnectionEventListener(ConnectionEventListener mlistener) {
        this.mlisteners.add(mlistener);
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener mlistener) {
        this.mlisteners.remove(mlistener);
    }

    public synchronized void printListeners() {
        System.err.println(this.mlisteners);
    }

    public synchronized int getListenerCount() {
        return this.mlisteners.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireConnectionClosed() {
        Set mlCopy;
        ConnectionEventSupport connectionEventSupport = this;
        synchronized (connectionEventSupport) {
            mlCopy = (Set)this.mlisteners.clone();
        }
        ConnectionEvent evt = new ConnectionEvent(this.source);
        for (ConnectionEventListener cl : mlCopy) {
            cl.connectionClosed(evt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireConnectionErrorOccurred(SQLException error) {
        Set mlCopy;
        ConnectionEventSupport connectionEventSupport = this;
        synchronized (connectionEventSupport) {
            mlCopy = (Set)this.mlisteners.clone();
        }
        ConnectionEvent evt = new ConnectionEvent(this.source, error);
        for (ConnectionEventListener cl : mlCopy) {
            cl.connectionErrorOccurred(evt);
        }
    }
}

