/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.Index;

public interface InternalIndex
extends Index {
    public Comparable canonicalizeQueryArgumentScalar(Comparable var1);

    public boolean hasPartitionIndexed(int var1);

    public boolean allPartitionsIndexed(int var1);

    public void markPartitionAsIndexed(int var1);

    public void markPartitionAsUnindexed(int var1);

    public PerIndexStats getPerIndexStats();
}

