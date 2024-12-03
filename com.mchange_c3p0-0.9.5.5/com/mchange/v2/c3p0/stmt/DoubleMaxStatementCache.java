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

public final class DoubleMaxStatementCache
extends GooGooStatementCache {
    int max_statements;
    GooGooStatementCache.Deathmarch globalDeathmarch = new GooGooStatementCache.Deathmarch(this);
    int max_statements_per_connection;
    GooGooStatementCache.DeathmarchConnectionStatementManager dcsm;

    public DoubleMaxStatementCache(AsynchronousRunner blockingTaskAsyncRunner, AsynchronousRunner deferredStatementDestroyer, int max_statements, int max_statements_per_connection) {
        super(blockingTaskAsyncRunner, deferredStatementDestroyer);
        this.max_statements = max_statements;
        this.max_statements_per_connection = max_statements_per_connection;
    }

    @Override
    protected GooGooStatementCache.ConnectionStatementManager createConnectionStatementManager() {
        this.dcsm = new GooGooStatementCache.DeathmarchConnectionStatementManager(this);
        return this.dcsm;
    }

    @Override
    void addStatementToDeathmarches(Object pstmt, Connection physicalConnection) {
        this.globalDeathmarch.deathmarchStatement(pstmt);
        this.dcsm.getDeathmarch(physicalConnection).deathmarchStatement(pstmt);
    }

    @Override
    void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection) {
        this.globalDeathmarch.undeathmarchStatement(pstmt);
        this.dcsm.getDeathmarch(physicalConnection).undeathmarchStatement(pstmt);
    }

    @Override
    boolean prepareAssimilateNewStatement(Connection pcon) {
        int cxn_stmt_count = this.dcsm.getNumStatementsForConnection(pcon);
        if (cxn_stmt_count < this.max_statements_per_connection) {
            int global_size = this.countCachedStatements();
            return global_size < this.max_statements || global_size == this.max_statements && this.globalDeathmarch.cullNext();
        }
        return cxn_stmt_count == this.max_statements_per_connection && this.dcsm.getDeathmarch(pcon).cullNext();
    }
}

