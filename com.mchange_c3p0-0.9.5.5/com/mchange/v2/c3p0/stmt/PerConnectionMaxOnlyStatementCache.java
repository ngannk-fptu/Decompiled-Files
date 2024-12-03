/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.async.AsynchronousRunner
 */
package com.mchange.v2.c3p0.stmt;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import java.sql.Connection;

public final class PerConnectionMaxOnlyStatementCache
extends GooGooStatementCache {
    int max_statements_per_connection;
    GooGooStatementCache.DeathmarchConnectionStatementManager dcsm;

    public PerConnectionMaxOnlyStatementCache(AsynchronousRunner blockingTaskAsyncRunner, AsynchronousRunner deferredStatementDestroyer, int max_statements_per_connection) {
        super(blockingTaskAsyncRunner, deferredStatementDestroyer);
        this.max_statements_per_connection = max_statements_per_connection;
    }

    @Override
    protected GooGooStatementCache.ConnectionStatementManager createConnectionStatementManager() {
        this.dcsm = new GooGooStatementCache.DeathmarchConnectionStatementManager();
        return this.dcsm;
    }

    @Override
    void addStatementToDeathmarches(Object pstmt, Connection physicalConnection) {
        this.dcsm.getDeathmarch(physicalConnection).deathmarchStatement(pstmt);
    }

    @Override
    void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection) {
        this.dcsm.getDeathmarch(physicalConnection).undeathmarchStatement(pstmt);
    }

    @Override
    boolean prepareAssimilateNewStatement(Connection pcon) {
        int cxn_stmt_count = this.dcsm.getNumStatementsForConnection(pcon);
        return cxn_stmt_count < this.max_statements_per_connection || cxn_stmt_count == this.max_statements_per_connection && this.dcsm.getDeathmarch(pcon).cullNext();
    }
}

