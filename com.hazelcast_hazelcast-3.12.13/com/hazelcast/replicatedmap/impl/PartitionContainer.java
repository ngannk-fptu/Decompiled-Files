/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.DataReplicatedRecordStore;
import com.hazelcast.replicatedmap.impl.record.ObjectReplicatedRecordStorage;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PartitionContainer {
    private final ConcurrentHashMap<String, ReplicatedRecordStore> replicatedRecordStores = this.initReplicatedRecordStoreMapping();
    private final ConstructorFunction<String, ReplicatedRecordStore> constructor = this.buildConstructorFunction();
    private final ReplicatedMapService service;
    private final int partitionId;

    public PartitionContainer(ReplicatedMapService service, int partitionId) {
        this.service = service;
        this.partitionId = partitionId;
    }

    private ConcurrentHashMap<String, ReplicatedRecordStore> initReplicatedRecordStoreMapping() {
        return new ConcurrentHashMap<String, ReplicatedRecordStore>();
    }

    private ConstructorFunction<String, ReplicatedRecordStore> buildConstructorFunction() {
        return new ConstructorFunction<String, ReplicatedRecordStore>(){

            @Override
            public ReplicatedRecordStore createNew(String name) {
                ReplicatedMapConfig replicatedMapConfig = PartitionContainer.this.service.getReplicatedMapConfig(name);
                InMemoryFormat inMemoryFormat = replicatedMapConfig.getInMemoryFormat();
                switch (inMemoryFormat) {
                    case OBJECT: {
                        return new ObjectReplicatedRecordStorage(name, PartitionContainer.this.service, PartitionContainer.this.partitionId);
                    }
                    case BINARY: {
                        return new DataReplicatedRecordStore(name, PartitionContainer.this.service, PartitionContainer.this.partitionId);
                    }
                    case NATIVE: {
                        throw new IllegalStateException("Native memory not yet supported for replicated map");
                    }
                }
                throw new IllegalStateException("Unsupported in memory format: " + (Object)((Object)inMemoryFormat));
            }
        };
    }

    public boolean isEmpty() {
        return this.replicatedRecordStores.isEmpty();
    }

    public ConcurrentMap<String, ReplicatedRecordStore> getStores() {
        return this.replicatedRecordStores;
    }

    public ReplicatedRecordStore getOrCreateRecordStore(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.replicatedRecordStores, name, this.replicatedRecordStores, this.constructor);
    }

    public ReplicatedRecordStore getRecordStore(String name) {
        return this.replicatedRecordStores.get(name);
    }

    public void shutdown() {
        for (ReplicatedRecordStore replicatedRecordStore : this.replicatedRecordStores.values()) {
            replicatedRecordStore.destroy();
        }
        this.replicatedRecordStores.clear();
    }

    public void destroy(String name) {
        ReplicatedRecordStore replicatedRecordStore = this.replicatedRecordStores.remove(name);
        if (replicatedRecordStore != null) {
            replicatedRecordStore.destroy();
        }
    }
}

