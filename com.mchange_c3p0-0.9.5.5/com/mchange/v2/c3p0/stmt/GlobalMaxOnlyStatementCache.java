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

public final class GlobalMaxOnlyStatementCache
extends GooGooStatementCache {
    int max_statements;
    GooGooStatementCache.Deathmarch globalDeathmarch = new GooGooStatementCache.Deathmarch(this);

    public GlobalMaxOnlyStatementCache(AsynchronousRunner blockingTaskAsyncRunner, AsynchronousRunner deferredStatementDestroyer, int max_statements) {
        super(blockingTaskAsyncRunner, deferredStatementDestroyer);
        this.max_statements = max_statements;
    }

    @Override
    protected GooGooStatementCache.ConnectionStatementManager createConnectionStatementManager() {
        return new GooGooStatementCache.SimpleConnectionStatementManager();
    }

    @Override
    void addStatementToDeathmarches(Object pstmt, Connection physicalConnection) {
        this.globalDeathmarch.deathmarchStatement(pstmt);
    }

    @Override
    void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection) {
        this.globalDeathmarch.undeathmarchStatement(pstmt);
    }

    @Override
    boolean prepareAssimilateNewStatement(Connection pcon) {
        int global_size = this.countCachedStatements();
        return global_size < this.max_statements || global_size == this.max_statements && this.globalDeathmarch.cullNext();
    }
}

