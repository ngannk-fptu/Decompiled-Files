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

abstract class SetManagedResultSet
extends FilterResultSet {
    Set activeResultSets;

    SetManagedResultSet(Set activeResultSets) {
        this.activeResultSets = activeResultSets;
    }

    SetManagedResultSet(ResultSet inner, Set activeResultSets) {
        super(inner);
        this.activeResultSets = activeResultSets;
    }

    public synchronized void setInner(ResultSet inner) {
        this.inner = inner;
        this.activeResultSets.add(inner);
    }

    public synchronized void close() throws SQLException {
        if (this.inner != null) {
            this.inner.close();
            this.activeResultSets.remove(this.inner);
            this.inner = null;
        }
    }
}

