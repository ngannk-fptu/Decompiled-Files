/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CacheClearBackupOperation;
import com.hazelcast.cache.impl.operation.PartitionWideCacheOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.partition.IPartitionService;
import javax.cache.CacheException;

public class CacheClearOperation
extends PartitionWideCacheOperation
implements BackupAwareOperation,
ServiceNamespaceAware,
MutatingOperation {
    private transient ICacheRecordStore cache;

    public CacheClearOperation() {
    }

    public CacheClearOperation(String name) {
        super(name);
    }

    @Override
    public void beforeRun() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        this.cache = service.getRecordStore(this.name, this.getPartitionId());
    }

    @Override
    public void run() {
        if (this.cache == null) {
            return;
        }
        try {
            this.cache.clear();
            this.response = new CacheClearResponse(Boolean.TRUE);
        }
        catch (CacheException e) {
            this.response = new CacheClearResponse((Object)e);
        }
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        CacheService cacheService = (CacheService)this.getService();
        int partitionId = this.getPartitionId();
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        if (partitionService.getPartitionId(this.name) == partitionId) {
            cacheService.sendInvalidationEvent(this.name, null, "<NA>");
        }
        cacheService.getCacheEventHandler().resetPartitionMetaData(this.name, partitionId);
    }

    @Override
    public int getId() {
        return 15;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public final int getSyncBackupCount() {
        return this.cache != null ? this.cache.getConfig().getBackupCount() : 0;
    }

    @Override
    public final int getAsyncBackupCount() {
        return this.cache != null ? this.cache.getConfig().getAsyncBackupCount() : 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new CacheClearBackupOperation(this.name);
    }

    @Override
    public ObjectNamespace getServiceNamespace() {
        ICacheRecordStore recordStore = this.cache;
        if (recordStore == null) {
            ICacheService service = (ICacheService)this.getService();
            recordStore = service.getOrCreateRecordStore(this.name, this.getPartitionId());
        }
        return recordStore.getObjectNamespace();
    }
}

