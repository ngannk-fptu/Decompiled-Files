/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnPartitionOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.io.IOException;

public class GetAllScheduledOnPartitionOperationFactory
implements OperationFactory {
    private String schedulerName;

    public GetAllScheduledOnPartitionOperationFactory() {
    }

    public GetAllScheduledOnPartitionOperationFactory(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 26;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.schedulerName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.schedulerName = in.readUTF();
    }

    @Override
    public Operation createOperation() {
        return new GetAllScheduledOnPartitionOperation(this.schedulerName);
    }
}

