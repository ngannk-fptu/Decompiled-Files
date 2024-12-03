/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.PooledConnection;
import javax.sql.StatementEvent;
import javax.sql.StatementEventListener;

public class StatementEventSupport {
    PooledConnection source;
    HashSet mlisteners = new HashSet();

    public StatementEventSupport(PooledConnection source) {
        this.source = source;
    }

    public synchronized void addStatementEventListener(StatementEventListener mlistener) {
        this.mlisteners.add(mlistener);
    }

    public synchronized void removeStatementEventListener(StatementEventListener mlistener) {
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
    public void fireStatementClosed(PreparedStatement ps) {
        Set mlCopy;
        StatementEventSupport statementEventSupport = this;
        synchronized (statementEventSupport) {
            mlCopy = (Set)this.mlisteners.clone();
        }
        StatementEvent evt = new StatementEvent(this.source, ps);
        for (StatementEventListener cl : mlCopy) {
            cl.statementClosed(evt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireStatementErrorOccurred(PreparedStatement ps, SQLException error) {
        Set mlCopy;
        StatementEventSupport statementEventSupport = this;
        synchronized (statementEventSupport) {
            mlCopy = (Set)this.mlisteners.clone();
        }
        StatementEvent evt = new StatementEvent(this.source, ps, error);
        for (StatementEventListener cl : mlCopy) {
            cl.statementErrorOccurred(evt);
        }
    }
}

