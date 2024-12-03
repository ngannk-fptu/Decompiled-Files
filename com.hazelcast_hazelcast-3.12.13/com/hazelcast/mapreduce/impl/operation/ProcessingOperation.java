/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public abstract class ProcessingOperation
extends Operation
implements IdentifiedDataSerializable {
    private transient String name;
    private transient String jobId;

    public ProcessingOperation() {
    }

    public ProcessingOperation(String name, String jobId) {
        this.name = name;
        this.jobId = jobId;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    public String getName() {
        return this.name;
    }

    public String getJobId() {
        return this.jobId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
        out.writeUTF(this.jobId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
        this.jobId = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

