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

public final class PutIfAbsentMapMergePolicy
implements ReplicatedMapMergePolicy,
IdentifiedDataSerializable {
    public static final PutIfAbsentMapMergePolicy INSTANCE = new PutIfAbsentMapMergePolicy();

    private PutIfAbsentMapMergePolicy() {
    }

    @Override
    public Object merge(String mapName, ReplicatedMapEntryView mergingEntry, ReplicatedMapEntryView existingEntry) {
        if (existingEntry.getValue() == null) {
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
        return 29;
    }

    @Override
    public void writeData(ObjectDataOutput out) {
    }

    @Override
    public void readData(ObjectDataInput in) {
    }
}

