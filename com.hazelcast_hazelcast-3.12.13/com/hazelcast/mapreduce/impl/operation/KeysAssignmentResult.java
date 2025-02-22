/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Map;

public class KeysAssignmentResult
implements IdentifiedDataSerializable {
    private RequestPartitionResult.ResultState resultState;
    private Map<Object, Address> assignment;

    public KeysAssignmentResult() {
    }

    public KeysAssignmentResult(RequestPartitionResult.ResultState resultState, Map<Object, Address> assignment) {
        this.resultState = resultState;
        this.assignment = assignment;
    }

    public RequestPartitionResult.ResultState getResultState() {
        return this.resultState;
    }

    public Map<Object, Address> getAssignment() {
        return this.assignment;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        SerializationUtil.writeNullableMap(this.assignment, out);
        out.writeInt(this.resultState.ordinal());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.assignment = SerializationUtil.readNullableMap(in);
        this.resultState = RequestPartitionResult.ResultState.byOrdinal(in.readInt());
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }
}

