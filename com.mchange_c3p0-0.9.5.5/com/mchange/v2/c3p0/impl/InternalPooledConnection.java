/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import javax.sql.PooledConnection;

interface InternalPooledConnection
extends PooledConnection {
    public void initStatementCache(GooGooStatementCache var1);

    public GooGooStatementCache getStatementCache();

    public int getConnectionStatus();
}

