/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class CollectionBackupAwareOperation
extends CollectionOperation
implements BackupAwareOperation {
    protected CollectionBackupAwareOperation() {
    }

    protected CollectionBackupAwareOperation(String name) {
        super(name);
    }

    @Override
    public int getSyncBackupCount() {
        return this.getOrCreateContainer().getConfig().getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return this.getOrCreateContainer().getConfig().getAsyncBackupCount();
    }
}

