/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collection;
import java.util.Map;

public interface Result<T extends Result>
extends IdentifiedDataSerializable {
    public Collection<Integer> getPartitionIds();

    public void setPartitionIds(Collection<Integer> var1);

    public void combine(T var1);

    public void onCombineFinished();

    public void add(QueryableEntry var1);

    public T createSubResult();

    public void orderAndLimit(PagingPredicate var1, Map.Entry<Integer, Map.Entry> var2);

    public void completeConstruction(Collection<Integer> var1);
}

