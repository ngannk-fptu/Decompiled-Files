/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class RequestPartitionResult
implements IdentifiedDataSerializable {
    private ResultState resultState;
    private int partitionId;

    public RequestPartitionResult() {
    }

    public RequestPartitionResult(ResultState resultState, int partitionId) {
        this.resultState = resultState;
        this.partitionId = partitionId;
    }

    public ResultState getResultState() {
        return this.resultState;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.resultState.ordinal());
        out.writeInt(this.partitionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.resultState = ResultState.byOrdinal(in.readInt());
        this.partitionId = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    public String toString() {
        return "RequestPartitionResult{resultState=" + (Object)((Object)this.resultState) + ", partitionId=" + this.partitionId + '}';
    }

    public static enum ResultState {
        SUCCESSFUL,
        NO_SUPERVISOR,
        CHECK_STATE_FAILED,
        NO_MORE_PARTITIONS;


        public static ResultState byOrdinal(int ordinal) {
            for (ResultState resultState : ResultState.values()) {
                if (ordinal != resultState.ordinal()) continue;
                return resultState;
            }
            return null;
        }
    }
}

