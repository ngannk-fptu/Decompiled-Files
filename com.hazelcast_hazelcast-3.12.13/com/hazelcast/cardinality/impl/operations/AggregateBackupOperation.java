/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.CardinalityEstimatorContainer;
import com.hazelcast.cardinality.impl.operations.AbstractCardinalityEstimatorOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class AggregateBackupOperation
extends AbstractCardinalityEstimatorOperation
implements BackupOperation {
    private long hash;

    public AggregateBackupOperation() {
    }

    public AggregateBackupOperation(String name, long hash) {
        super(name);
        this.hash = hash;
    }

    @Override
    public void run() throws Exception {
        CardinalityEstimatorContainer container = this.getCardinalityEstimatorContainer();
        container.add(this.hash);
    }

    @Override
    public int getId() {
        return 2;
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

