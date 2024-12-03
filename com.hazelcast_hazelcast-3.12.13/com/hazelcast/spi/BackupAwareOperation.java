/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;

public interface BackupAwareOperation
extends PartitionAwareOperation {
    public boolean shouldBackup();

    public int getSyncBackupCount();

    public int getAsyncBackupCount();

    public Operation getBackupOperation();
}

