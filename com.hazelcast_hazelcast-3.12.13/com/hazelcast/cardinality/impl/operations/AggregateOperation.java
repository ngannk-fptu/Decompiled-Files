/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.operations.AggregateBackupOperation;
import com.hazelcast.cardinality.impl.operations.CardinalityEstimatorBackupAwareOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AggregateOperation
extends CardinalityEstimatorBackupAwareOperation
implements MutatingOperation {
    private long hash;

    public AggregateOperation() {
    }

    public AggregateOperation(String name, long hash) {
        super(name);
        this.hash = hash;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void run() throws Exception {
        this.getCardinalityEstimatorContainer().add(this.hash);
    }

    @Override
    public Operation getBackupOperation() {
        return new AggregateBackupOperation(this.name, this.hash);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.hash);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.hash = in.readLong();
    }
}

