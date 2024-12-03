/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.util.ClosableResource
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v1.util.ClosableResource;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.PooledConnection;

abstract class AbstractC3P0PooledConnection
implements PooledConnection,
ClosableResource {
    final Object inInternalUseLock = new Object();

    AbstractC3P0PooledConnection() {
    }

    abstract Connection getPhysicalConnection();

    abstract void initStatementCache(GooGooStatementCache var1);

    abstract void closeMaybeCheckedOut(boolean var1) throws SQLException;
}

