/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapAdapter<K, V>
extends HashMap<K, V>
implements IdentifiedDataSerializable {
    public HashMapAdapter(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public HashMapAdapter(int initialCapacity) {
        super(initialCapacity);
    }

    public HashMapAdapter() {
    }

    public HashMapAdapter(Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 22;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        SerializationUtil.writeMap(this, out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        Map map = SerializationUtil.readMap(in);
        this.putAll(map);
    }
}

