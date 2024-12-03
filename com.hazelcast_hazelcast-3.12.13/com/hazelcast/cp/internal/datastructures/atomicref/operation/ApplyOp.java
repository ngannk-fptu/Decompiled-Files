/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.AbstractAtomicRefOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class ApplyOp
extends AbstractAtomicRefOp
implements IdentifiedDataSerializable {
    private Data function;
    private ReturnValueType returnValueType;
    private boolean alter;

    public ApplyOp() {
    }

    public ApplyOp(String name, Data function, ReturnValueType returnValueType, boolean alter) {
        super(name);
        Preconditions.checkNotNull(function);
        Preconditions.checkNotNull(returnValueType);
        this.function = function;
        this.returnValueType = returnValueType;
        this.alter = alter;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicRef ref = this.getAtomicRef(groupId);
        Data currentData = ref.get();
        Data newData = this.callFunction(currentData);
        if (this.alter) {
            ref.set(newData);
        }
        if (this.returnValueType == ReturnValueType.NO_RETURN_VALUE) {
            return null;
        }
        return this.returnValueType == ReturnValueType.RETURN_OLD_VALUE ? currentData : newData;
    }

    private Data callFunction(Data currentData) {
        NodeEngine nodeEngine = this.getNodeEngine();
        IFunction func = (IFunction)nodeEngine.toObject(this.function);
        Object input = nodeEngine.toObject(currentData);
        Object output = func.apply(input);
        return nodeEngine.toData(output);
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.function);
        out.writeUTF(this.returnValueType.name());
        out.writeBoolean(this.alter);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.function = in.readData();
        this.returnValueType = ReturnValueType.valueOf(in.readUTF());
        this.alter = in.readBoolean();
    }

    public static enum ReturnValueType {
        NO_RETURN_VALUE(0),
        RETURN_OLD_VALUE(1),
        RETURN_NEW_VALUE(2);

        private final int value;

        private ReturnValueType(int value) {
            this.value = value;
        }

        public static ReturnValueType fromValue(int value) {
            switch (value) {
                case 0: {
                    return NO_RETURN_VALUE;
                }
                case 1: {
                    return RETURN_OLD_VALUE;
                }
                case 2: {
                    return RETURN_NEW_VALUE;
                }
            }
            throw new IllegalArgumentException("No " + ReturnValueType.class + " for value: " + value);
        }

        public int value() {
            return this.value;
        }
    }
}

