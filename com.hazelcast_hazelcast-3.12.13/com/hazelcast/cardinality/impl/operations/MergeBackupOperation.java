/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.cardinality.impl.operations.AbstractCardinalityEstimatorOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class MergeBackupOperation
extends AbstractCardinalityEstimatorOperation {
    private HyperLogLog value;

    public MergeBackupOperation() {
    }

    public MergeBackupOperation(String name, HyperLogLog value) {
        super(name);
        this.value = value;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void run() throws Exception {
        this.getCardinalityEstimatorContainer().setValue(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.value = (HyperLogLog)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.value);
    }
}

