/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.map.impl.operation.MapFlushBackupOperation;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;

public class MapFlushOperation
extends MapOperation
implements BackupAwareOperation,
MutatingOperation {
    private long sequence;

    public MapFlushOperation() {
    }

    public MapFlushOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        this.sequence = this.recordStore.softFlush();
    }

    @Override
    public Object getResponse() {
        return this.sequence;
    }

    @Override
    public boolean shouldBackup() {
        MapStoreConfig mapStoreConfig = this.mapContainer.getMapConfig().getMapStoreConfig();
        return mapStoreConfig != null && mapStoreConfig.isEnabled() && mapStoreConfig.getWriteDelaySeconds() > 0;
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        return new MapFlushBackupOperation(this.name);
    }

    @Override
    public int getId() {
        return 50;
    }
}

