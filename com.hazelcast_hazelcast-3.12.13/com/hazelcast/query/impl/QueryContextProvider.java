/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;

public interface QueryContextProvider {
    public QueryContext obtainContextFor(Indexes var1, int var2);
}

