/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong;

import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RaftAtomicLongSnapshot
implements IdentifiedDataSerializable {
    private Map<String, Long> longs = Collections.emptyMap();
    private Set<String> destroyed = Collections.emptySet();

    public RaftAtomicLongSnapshot() {
    }

    public RaftAtomicLongSnapshot(Map<String, Long> longs, Set<String> destroyed) {
        this.longs = longs;
        this.destroyed = destroyed;
    }

    public Iterable<Map.Entry<String, Long>> getLongs() {
        return this.longs.entrySet();
    }

    public Set<String> getDestroyed() {
        return this.destroyed;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.longs.size());
        for (Map.Entry<String, Long> entry : this.longs.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeLong(entry.getValue());
        }
        out.writeInt(this.destroyed.size());
        for (String name : this.destroyed) {
            out.writeUTF(name);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        String name;
        int i;
        int len = in.readInt();
        this.longs = new HashMap<String, Long>(len);
        for (i = 0; i < len; ++i) {
            name = in.readUTF();
            long value = in.readLong();
            this.longs.put(name, value);
        }
        len = in.readInt();
        this.destroyed = new HashSet<String>(len);
        for (i = 0; i < len; ++i) {
            name = in.readUTF();
            this.destroyed.add(name);
        }
    }

    @Override
    public int getFactoryId() {
        return RaftAtomicLongDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    public String toString() {
        return "RaftAtomicLongSnapshot{longs=" + this.longs + ", destroyed=" + this.destroyed + '}';
    }
}

