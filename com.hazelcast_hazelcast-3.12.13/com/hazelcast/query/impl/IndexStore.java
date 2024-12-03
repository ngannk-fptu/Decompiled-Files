/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Set;

public interface IndexStore {
    public Comparable canonicalizeQueryArgumentScalar(Comparable var1);

    public void insert(Object var1, QueryableEntry var2, IndexOperationStats var3);

    public void update(Object var1, Object var2, QueryableEntry var3, IndexOperationStats var4);

    public void remove(Object var1, Data var2, Object var3, IndexOperationStats var4);

    public void clear();

    public void destroy();

    public boolean isEvaluateOnly();

    public boolean canEvaluate(Class<? extends Predicate> var1);

    public Set<QueryableEntry> evaluate(Predicate var1, TypeConverter var2);

    public Set<QueryableEntry> getRecords(Comparable var1);

    public Set<QueryableEntry> getRecords(Set<Comparable> var1);

    public Set<QueryableEntry> getRecords(Comparison var1, Comparable var2);

    public Set<QueryableEntry> getRecords(Comparable var1, boolean var2, Comparable var3, boolean var4);
}

