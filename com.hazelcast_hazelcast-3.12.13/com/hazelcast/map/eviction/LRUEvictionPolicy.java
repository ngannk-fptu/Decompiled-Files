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

public class LRUEvictionPolicy
extends MapEvictionPolicy
implements IdentifiedDataSerializable {
    public static final LRUEvictionPolicy INSTANCE = new LRUEvictionPolicy();

    @Override
    public int compare(EntryView entryView1, EntryView entryView2) {
        long lastAccessTime2;
        long lastAccessTime1 = entryView1.getLastAccessTime();
        return lastAccessTime1 < (lastAccessTime2 = entryView2.getLastAccessTime()) ? -1 : (lastAccessTime1 == lastAccessTime2 ? 0 : 1);
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 13;
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

