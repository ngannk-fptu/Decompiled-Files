/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.Result;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntriesSegment;
import java.util.Collection;

public interface PartitionScanExecutor {
    public void execute(String var1, Predicate var2, Collection<Integer> var3, Result var4);

    public QueryableEntriesSegment execute(String var1, Predicate var2, int var3, int var4, int var5);
}

