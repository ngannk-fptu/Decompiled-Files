/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.merge;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy;

public final class LatestUpdateMapMergePolicy
implements ReplicatedMapMergePolicy,
IdentifiedDataSerializable {
    public static final LatestUpdateMapMergePolicy INSTANCE = new LatestUpdateMapMergePolicy();

    private LatestUpdateMapMergePolicy() {
    }

    @Override
    public Object merge(String mapName, ReplicatedMapEntryView mergingEntry, ReplicatedMapEntryView existingEntry) {
        if (mergingEntry.getLastUpdateTime() >= existingEntry.getLastUpdateTime()) {
            return mergingEntry.getValue();
        }
        return existingEntry.getValue();
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    public void writeData(ObjectDataOutput out) {
    }

    @Override
    public void readData(ObjectDataInput in) {
    }
}

