/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class CacheRemoveAllBackupOperation
extends AbstractNamedOperation
implements BackupOperation,
ServiceNamespaceAware,
IdentifiedDataSerializable {
    private Set<Data> keys;
    private transient ICacheRecordStore cache;

    public CacheRemoveAllBackupOperation() {
    }

    public CacheRemoveAllBackupOperation(String name, Set<Data> keys) {
        super(name);
        this.keys = keys;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 35;
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
    public void run() throws Exception {
        if (this.cache == null) {
            return;
        }
        if (this.keys != null) {
            for (Data key : this.keys) {
                this.cache.removeRecord(key);
            }
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
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
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
}

