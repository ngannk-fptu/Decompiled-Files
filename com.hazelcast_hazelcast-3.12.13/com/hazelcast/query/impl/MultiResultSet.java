/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Map;
import java.util.Set;

public interface MultiResultSet
extends Set<QueryableEntry> {
    public void addResultSet(Map<Data, QueryableEntry> var1);
}

