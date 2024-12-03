/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class ResultSegment
implements IdentifiedDataSerializable {
    private Result result;
    private int nextTableIndexToReadFrom;

    public ResultSegment() {
    }

    public ResultSegment(Result result, int nextTableIndexToReadFrom) {
        this.result = result;
        this.nextTableIndexToReadFrom = nextTableIndexToReadFrom;
    }

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getNextTableIndexToReadFrom() {
        return this.nextTableIndexToReadFrom;
    }

    public void setNextTableIndexToReadFrom(int nextTableIndexToReadFrom) {
        this.nextTableIndexToReadFrom = nextTableIndexToReadFrom;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 139;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.result);
        out.writeInt(this.nextTableIndexToReadFrom);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.result = (Result)in.readObject();
        this.nextTableIndexToReadFrom = in.readInt();
    }
}

