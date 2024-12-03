/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter.operations;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.crdt.CRDTDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CRDTTimestampedLong
implements IdentifiedDataSerializable {
    private long value;
    private VectorClock vectorClock;

    public CRDTTimestampedLong() {
    }

    public CRDTTimestampedLong(long value, VectorClock vectorClock) {
        this.value = value;
        this.vectorClock = vectorClock;
    }

    public long getValue() {
        return this.value;
    }

    public VectorClock getVectorClock() {
        return this.vectorClock;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }

    @Override
    public int getFactoryId() {
        return CRDTDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.value);
        out.writeObject(this.vectorClock);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.value = in.readLong();
        this.vectorClock = (VectorClock)in.readObject();
    }
}

