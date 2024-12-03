/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;

public class SQLBindings {
    private final String sql;
    private final ImmutableList<Object> bindings;

    public SQLBindings(String sql, ImmutableList<Object> bindings) {
        this.sql = sql;
        this.bindings = bindings;
    }

    public String getSQL() {
        return this.sql;
    }

    public ImmutableList<Object> getBindings() {
        return this.bindings;
    }
}

