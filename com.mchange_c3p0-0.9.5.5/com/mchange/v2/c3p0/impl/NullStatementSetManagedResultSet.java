/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.impl.SetManagedResultSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

final class NullStatementSetManagedResultSet
extends SetManagedResultSet {
    NullStatementSetManagedResultSet(Set activeResultSets) {
        super(activeResultSets);
    }

    NullStatementSetManagedResultSet(ResultSet inner, Set activeResultSets) {
        super(inner, activeResultSets);
    }

    public Statement getStatement() {
        return null;
    }
}

