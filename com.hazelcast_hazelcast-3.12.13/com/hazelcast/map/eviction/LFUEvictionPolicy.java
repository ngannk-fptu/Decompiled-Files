/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.eviction;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class LFUEvictionPolicy
extends MapEvictionPolicy
implements IdentifiedDataSerializable {
    public static final LFUEvictionPolicy INSTANCE = new LFUEvictionPolicy();

    @Override
    public int compare(EntryView entryView1, EntryView entryView2) {
        long hits2;
        long hits1 = entryView1.getHits();
        return hits1 < (hits2 = entryView2.getHits()) ? -1 : (hits1 == hits2 ? 0 : 1);
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass().equals(obj.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

