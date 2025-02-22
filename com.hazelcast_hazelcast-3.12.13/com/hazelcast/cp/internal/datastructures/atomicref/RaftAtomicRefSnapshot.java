/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref;

import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicReferenceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RaftAtomicRefSnapshot
implements IdentifiedDataSerializable {
    private Map<String, Data> refs = Collections.emptyMap();
    private Set<String> destroyed = Collections.emptySet();

    public RaftAtomicRefSnapshot() {
    }

    public RaftAtomicRefSnapshot(Map<String, Data> refs, Set<String> destroyed) {
        this.refs = refs;
        this.destroyed = destroyed;
    }

    public Iterable<Map.Entry<String, Data>> getRefs() {
        return this.refs.entrySet();
    }

    public Set<String> getDestroyed() {
        return this.destroyed;
    }

    @Override
    public int getFactoryId() {
        return RaftAtomicReferenceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.refs.size());
        for (Map.Entry<String, Data> entry : this.refs.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeData(entry.getValue());
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
        this.refs = new HashMap<String, Data>(len);
        for (i = 0; i < len; ++i) {
            name = in.readUTF();
            Data value = in.readData();
            this.refs.put(name, value);
        }
        len = in.readInt();
        this.destroyed = new HashSet<String>(len);
        for (i = 0; i < len; ++i) {
            name = in.readUTF();
            this.destroyed.add(name);
        }
    }

    public String toString() {
        return "RaftAtomicRefSnapshot{refs=" + this.refs + ", destroyed=" + this.destroyed + '}';
    }
}

