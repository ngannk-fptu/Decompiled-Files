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

public class RandomEvictionPolicy
extends MapEvictionPolicy
implements IdentifiedDataSerializable {
    public static final RandomEvictionPolicy INSTANCE = new RandomEvictionPolicy();

    @Override
    public int compare(EntryView entryView1, EntryView entryView2) {
        return 0;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 11;
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

