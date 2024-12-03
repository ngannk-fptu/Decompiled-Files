/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheEventContextUtil;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CacheRemoveAllBackupOperation;
import com.hazelcast.cache.impl.operation.PartitionWideCacheOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.cache.CacheException;

public class CacheRemoveAllOperation
extends PartitionWideCacheOperation
implements BackupAwareOperation,
MutatingOperation,
ServiceNamespaceAware {
    private Set<Data> keys;
    private int completionId;
    private transient Set<Data> filteredKeys = new HashSet<Data>();
    private transient ICacheService service;
    private transient ICacheRecordStore cache;

    public CacheRemoveAllOperation() {
    }

    public CacheRemoveAllOperation(String name, Set<Data> keys, int completionId) {
        super(name);
        this.keys = keys;
        this.completionId = completionId;
    }

    @Override
    public void beforeRun() throws Exception {
        this.service = (ICacheService)this.getService();
        this.cache = this.service.getRecordStore(this.name, this.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        if (this.cache == null) {
            this.service.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.completionId).setCacheName(this.name));
            return;
        }
        this.filterKeys();
        try {
            if (this.keys == null) {
                this.cache.removeAll(this.filteredKeys, this.completionId);
            } else if (!this.filteredKeys.isEmpty()) {
                this.cache.removeAll(this.filteredKeys, this.completionId);
            } else {
                this.service.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.completionId).setCacheName(this.name));
            }
            this.response = new CacheClearResponse(Boolean.TRUE);
        }
        catch (CacheException e) {
            this.response = new CacheClearResponse((Object)e);
        }
    }

    private void filterKeys() {
        if (this.keys == null) {
            return;
        }
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        for (Data k : this.keys) {
            if (partitionService.getPartitionId(k) != this.getPartitionId()) continue;
            this.filteredKeys.add(k);
        }
    }

    @Override
    public boolean shouldBackup() {
        return !this.filteredKeys.isEmpty();
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
        return new CacheRemoveAllBackupOperation(this.name, this.filteredKeys);
    }

    @Override
    public ObjectNamespace getServiceNamespace() {
        return this.cache != null ? this.cache.getObjectNamespace() : CacheService.getObjectNamespace(this.name);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.completionId);
        out.writeBoolean(this.keys != null);
        if (this.keys != null) {
            out.writeInt(this.keys.size());
            for (Data key : this.keys) {
                out.writeData(key);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.completionId = in.readInt();
        boolean isKeysNotNull = in.readBoolean();
        if (isKeysNotNull) {
            int size = in.readInt();
            this.keys = SetUtil.createHashSet(size);
            for (int i = 0; i < size; ++i) {
                Data key = in.readData();
                this.keys.add(key);
            }
        }
    }

    @Override
    public int getId() {
        return 34;
    }
}

