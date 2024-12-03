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

public class PassThroughMergePolicy
implements MapMergePolicy,
IdentifiedDataSerializable {
    @Override
    public Object merge(String mapName, EntryView mergingEntry, EntryView existingEntry) {
        return mergingEntry == null ? existingEntry.getValue() : mergingEntry.getValue();
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
        return 107;
    }
}

