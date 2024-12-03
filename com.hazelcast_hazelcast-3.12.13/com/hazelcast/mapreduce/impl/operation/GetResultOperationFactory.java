/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.operation.GetResultOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.io.IOException;

public class GetResultOperationFactory
implements OperationFactory {
    private final String name;
    private final String jobId;

    public GetResultOperationFactory() {
        this.name = "";
        this.jobId = "";
    }

    public GetResultOperationFactory(String name, String jobId) {
        this.name = name;
        this.jobId = jobId;
    }

    @Override
    public Operation createOperation() {
        return new GetResultOperation(this.name, this.jobId);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException("local factory only");
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException("local factory only");
    }

    @Override
    public int getFactoryId() {
        throw new UnsupportedOperationException("local factory only");
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("local factory only");
    }
}

