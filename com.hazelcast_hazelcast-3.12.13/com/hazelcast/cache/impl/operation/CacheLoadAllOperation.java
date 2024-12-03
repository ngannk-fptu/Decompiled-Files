/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CachePutAllBackupOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.cache.CacheException;

public class CacheLoadAllOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
BackupAwareOperation,
MutatingOperation,
ServiceNamespaceAware {
    private Set<Data> keys;
    private boolean replaceExistingValues;
    private boolean shouldBackup;
    private transient Map<Data, CacheRecord> backupRecords;
    private transient ICacheRecordStore cache;
    private Object response;

    public CacheLoadAllOperation(String name, Set<Data> keys, boolean replaceExistingValues) {
        super(name);
        this.keys = keys;
        this.replaceExistingValues = replaceExistingValues;
    }

    public CacheLoadAllOperation() {
    }

    @Override
    public void run() throws Exception {
        int partitionId = this.getPartitionId();
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        HashSet<Data> filteredKeys = null;
        if (this.keys != null) {
            filteredKeys = new HashSet<Data>();
            for (Data k : this.keys) {
                if (partitionService.getPartitionId(k) != partitionId) continue;
                filteredKeys.add(k);
            }
        }
        if (filteredKeys == null || filteredKeys.isEmpty()) {
            return;
        }
        try {
            ICacheService service = (ICacheService)this.getService();
            this.cache = service.getOrCreateRecordStore(this.name, partitionId);
            Set<Data> keysLoaded = this.cache.loadAll(filteredKeys, this.replaceExistingValues);
            int loadedKeyCount = keysLoaded.size();
            if (loadedKeyCount > 0) {
                this.backupRecords = MapUtil.createHashMap(loadedKeyCount);
                for (Data key : keysLoaded) {
                    CacheRecord record = this.cache.getRecord(key);
                    if (record == null) continue;
                    this.backupRecords.put(key, record);
                }
                this.shouldBackup = !this.backupRecords.isEmpty();
            }
        }
        catch (CacheException e) {
            this.response = new CacheClearResponse((Object)e);
        }
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public Operation getBackupOperation() {
        return new CachePutAllBackupOperation(this.name, this.backupRecords);
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public final int getSyncBackupCount() {
        return this.cache.getConfig().getBackupCount();
    }

    @Override
    public final int getAsyncBackupCount() {
        return this.cache.getConfig().getAsyncBackupCount();
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
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.replaceExistingValues);
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
        this.replaceExistingValues = in.readBoolean();
        if (in.readBoolean()) {
            int size = in.readInt();
            this.keys = SetUtil.createHashSet(size);
            for (int i = 0; i < size; ++i) {
                this.keys.add(in.readData());
            }
        }
    }
}

