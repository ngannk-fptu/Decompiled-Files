/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.AbstractNamedOperation;

public class CacheClearBackupOperation
extends AbstractNamedOperation
implements BackupOperation,
ServiceNamespaceAware,
IdentifiedDataSerializable {
    private transient ICacheRecordStore cache;

    public CacheClearBackupOperation() {
    }

    public CacheClearBackupOperation(String name) {
        super(name);
    }

    @Override
    public void beforeRun() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        try {
            this.cache = service.getOrCreateRecordStore(this.name, this.getPartitionId());
        }
        catch (CacheNotExistsException e) {
            this.getLogger().finest("Error while getting a cache", e);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() throws Exception {
        if (this.cache != null) {
            this.cache.clear();
        }
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

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }
}

