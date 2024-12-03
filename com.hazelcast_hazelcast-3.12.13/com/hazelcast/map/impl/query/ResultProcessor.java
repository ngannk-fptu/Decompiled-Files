/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collection;

public interface ResultProcessor<T extends Result> {
    public T populateResult(Query var1, long var2);

    public T populateResult(Query var1, long var2, Collection<QueryableEntry> var4, Collection<Integer> var5);
}

