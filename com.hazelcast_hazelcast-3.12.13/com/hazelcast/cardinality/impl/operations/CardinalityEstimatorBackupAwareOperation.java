/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.operations;

import com.hazelcast.cardinality.impl.operations.AbstractCardinalityEstimatorOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class CardinalityEstimatorBackupAwareOperation
extends AbstractCardinalityEstimatorOperation
implements BackupAwareOperation {
    protected boolean shouldBackup = true;

    public CardinalityEstimatorBackupAwareOperation() {
    }

    public CardinalityEstimatorBackupAwareOperation(String name) {
        super(name);
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public int getSyncBackupCount() {
        return this.getCardinalityEstimatorContainer().getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return this.getCardinalityEstimatorContainer().getAsyncBackupCount();
    }
}

