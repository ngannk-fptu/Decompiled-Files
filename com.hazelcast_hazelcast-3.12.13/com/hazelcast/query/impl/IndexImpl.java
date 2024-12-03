/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.BitmapIndexStore;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.IndexStore;
import com.hazelcast.query.impl.OrderedIndexStore;
import com.hazelcast.query.impl.UnorderedIndexStore;
import com.hazelcast.query.impl.getters.Extractors;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IndexImpl
extends AbstractIndex {
    private final Set<Integer> indexedPartitions = Collections.newSetFromMap(new ConcurrentHashMap());

    public IndexImpl(IndexDefinition definition, InternalSerializationService ss, Extractors extractors, IndexCopyBehavior copyBehavior, PerIndexStats stats) {
        super(definition, ss, extractors, copyBehavior, stats);
    }

    @Override
    protected IndexStore createIndexStore(IndexDefinition definition, PerIndexStats stats) {
        if (definition.getUniqueKey() == null) {
            return definition.isOrdered() ? new OrderedIndexStore(this.copyBehavior) : new UnorderedIndexStore(this.copyBehavior);
        }
        return new BitmapIndexStore(definition, this.ss, this.extractors);
    }

    @Override
    public void clear() {
        super.clear();
        this.indexedPartitions.clear();
    }

    @Override
    public boolean hasPartitionIndexed(int partitionId) {
        return this.indexedPartitions.contains(partitionId);
    }

    @Override
    public boolean allPartitionsIndexed(int ownedPartitionCount) {
        return ownedPartitionCount < 0 || this.indexedPartitions.size() == ownedPartitionCount;
    }

    @Override
    public void markPartitionAsIndexed(int partitionId) {
        assert (!this.indexedPartitions.contains(partitionId));
        this.indexedPartitions.add(partitionId);
    }

    @Override
    public void markPartitionAsUnindexed(int partitionId) {
        this.indexedPartitions.remove(partitionId);
    }
}

