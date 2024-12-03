/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.sql.filter.FilterResultSet
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.sql.filter.FilterResultSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

final class SnatchFromSetResultSet
extends FilterResultSet {
    Set activeResultSets;

    SnatchFromSetResultSet(Set activeResultSets) {
        this.activeResultSets = activeResultSets;
    }

    public synchronized void setInner(ResultSet inner) {
        this.inner = inner;
        this.activeResultSets.add(inner);
    }

    public synchronized void close() throws SQLException {
        this.inner.close();
        this.activeResultSets.remove(this.inner);
        this.inner = null;
    }
}

