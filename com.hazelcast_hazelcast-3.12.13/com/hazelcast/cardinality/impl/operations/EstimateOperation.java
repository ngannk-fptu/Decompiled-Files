/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.operations.AbstractCardinalityEstimatorOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class EstimateOperation
extends AbstractCardinalityEstimatorOperation
implements ReadonlyOperation {
    private long estimate;

    public EstimateOperation() {
    }

    public EstimateOperation(String name) {
        super(name);
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void run() throws Exception {
        this.estimate = this.getCardinalityEstimatorContainer().estimate();
    }

    @Override
    public Object getResponse() {
        return this.estimate;
    }
}

