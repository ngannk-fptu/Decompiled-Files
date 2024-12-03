/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Set;

public interface Index {
    public String getName();

    public String[] getComponents();

    public boolean isOrdered();

    public String getUniqueKey();

    public TypeConverter getConverter();

    public void putEntry(QueryableEntry var1, Object var2, OperationSource var3);

    public void removeEntry(Data var1, Object var2, OperationSource var3);

    public boolean isEvaluateOnly();

    public boolean canEvaluate(Class<? extends Predicate> var1);

    public Set<QueryableEntry> evaluate(Predicate var1);

    public Set<QueryableEntry> getRecords(Comparable var1);

    public Set<QueryableEntry> getRecords(Comparable[] var1);

    public Set<QueryableEntry> getRecords(Comparable var1, boolean var2, Comparable var3, boolean var4);

    public Set<QueryableEntry> getRecords(Comparison var1, Comparable var2);

    public void clear();

    public void destroy();

    public static enum OperationSource {
        USER,
        SYSTEM;

    }
}

