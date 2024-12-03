/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CacheNearCacheStateHolder;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.util.MapUtil;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CacheReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private final List<CacheConfig> configs = new ArrayList<CacheConfig>();
    private final Map<String, Map<Data, CacheRecord>> data = new HashMap<String, Map<Data, CacheRecord>>();
    private final CacheNearCacheStateHolder nearCacheStateHolder = new CacheNearCacheStateHolder(this);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void prepare(CachePartitionSegment segment, Collection<ServiceNamespace> namespaces, int replicaIndex) {
        for (ServiceNamespace namespace : namespaces) {
            CacheConfig cacheConfig;
            ObjectNamespace ns = (ObjectNamespace)namespace;
            ICacheRecordStore recordStore = segment.getRecordStore(ns.getObjectName());
            if (recordStore == null || (cacheConfig = recordStore.getConfig()).getTotalBackupCount() < replicaIndex) continue;
            Closeable tenantContext = CacheConfigAccessor.getTenantControl(cacheConfig).setTenant(false);
            try {
                this.storeRecordsToReplicate(recordStore);
            }
            finally {
                IOUtil.closeResource(tenantContext);
            }
        }
        this.configs.addAll(segment.getCacheConfigs());
        this.nearCacheStateHolder.prepare(segment, namespaces);
    }

    protected void storeRecordsToReplicate(ICacheRecordStore recordStore) {
        this.data.put(recordStore.getName(), recordStore.getReadOnlyRecords());
    }

    @Override
    public void beforeRun() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        for (CacheConfig config : this.configs) {
            service.putCacheConfigIfAbsent(config);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        for (Map.Entry<String, Map<Data, CacheRecord>> entry : this.data.entrySet()) {
            try (Closeable tenantContext = CacheConfigAccessor.getTenantControl(service.getCacheConfig(entry.getKey())).setTenant(true);){
                ICacheRecordStore cache = service.getOrCreateRecordStore(entry.getKey(), this.getPartitionId());
                cache.reset();
                Map<Data, CacheRecord> map = entry.getValue();
                Iterator<Map.Entry<Data, CacheRecord>> iterator = map.entrySet().iterator();
                while (iterator.hasNext() && !cache.evictIfRequired()) {
                    Map.Entry<Data, CacheRecord> next = iterator.next();
                    Data key = next.getKey();
                    CacheRecord record = next.getValue();
                    iterator.remove();
                    cache.putRecord(key, record, false);
                }
            }
        }
        this.data.clear();
        if (this.getReplicaIndex() == 0) {
            this.nearCacheStateHolder.applyState();
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        int confSize = this.configs.size();
        out.writeInt(confSize);
        for (CacheConfig config : this.configs) {
            out.writeObject(config);
        }
        int count = this.data.size();
        out.writeInt(count);
        for (Map.Entry<String, Map<Data, CacheRecord>> entry : this.data.entrySet()) {
            Map<Data, CacheRecord> cacheMap = entry.getValue();
            int subCount = cacheMap.size();
            out.writeInt(subCount);
            out.writeUTF(entry.getKey());
            for (Map.Entry<Data, CacheRecord> e : cacheMap.entrySet()) {
                Data key = e.getKey();
                CacheRecord record = e.getValue();
                out.writeData(key);
                out.writeObject(record);
            }
            out.writeData(null);
        }
        this.nearCacheStateHolder.writeData(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int confSize = in.readInt();
        for (int i = 0; i < confSize; ++i) {
            CacheConfig config = (CacheConfig)in.readObject();
            this.configs.add(config);
        }
        int count = in.readInt();
        for (int i = 0; i < count; ++i) {
            Data key;
            int subCount = in.readInt();
            String name = in.readUTF();
            Map<Data, CacheRecord> m = MapUtil.createHashMap(subCount);
            this.data.put(name, m);
            for (int j = 0; j < subCount + 1 && (key = in.readData()) != null && key.dataSize() != 0; ++j) {
                CacheRecord record = (CacheRecord)in.readObject();
                m.put(key, record);
            }
        }
        this.nearCacheStateHolder.readData(in);
    }

    public boolean isEmpty() {
        return this.configs.isEmpty() && this.data.isEmpty();
    }

    Collection<CacheConfig> getConfigs() {
        return Collections.unmodifiableCollection(this.configs);
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 45;
    }
}

