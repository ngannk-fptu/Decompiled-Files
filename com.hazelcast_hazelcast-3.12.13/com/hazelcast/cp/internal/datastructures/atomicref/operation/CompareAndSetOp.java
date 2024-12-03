/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.AbstractAtomicRefOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CompareAndSetOp
extends AbstractAtomicRefOp
implements IdentifiedDataSerializable {
    private Data expectedValue;
    private Data newValue;

    public CompareAndSetOp() {
    }

    public CompareAndSetOp(String name, Data expectedValue, Data newValue) {
        super(name);
        this.expectedValue = expectedValue;
        this.newValue = newValue;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicRef ref = this.getAtomicRef(groupId);
        boolean contains = ref.contains(this.expectedValue);
        if (contains) {
            ref.set(this.newValue);
        }
        return contains;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.expectedValue);
        out.writeData(this.newValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.expectedValue = in.readData();
        this.newValue = in.readData();
    }
}

