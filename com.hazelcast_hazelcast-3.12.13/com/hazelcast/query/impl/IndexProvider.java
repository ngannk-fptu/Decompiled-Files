/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.getters.Extractors;

public interface IndexProvider {
    public InternalIndex createIndex(IndexDefinition var1, Extractors var2, InternalSerializationService var3, IndexCopyBehavior var4, PerIndexStats var5);
}

