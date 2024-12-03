/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.merge;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class LatestUpdateMapMergePolicy
implements MapMergePolicy,
IdentifiedDataSerializable {
    @Override
    public Object merge(String mapName, EntryView mergingEntry, EntryView existingEntry) {
        if (mergingEntry.getLastUpdateTime() >= existingEntry.getLastUpdateTime()) {
            return mergingEntry.getValue();
        }
        return existingEntry.getValue();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 106;
    }
}

