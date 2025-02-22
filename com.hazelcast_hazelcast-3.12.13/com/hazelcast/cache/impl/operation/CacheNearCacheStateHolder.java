/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEventHandler;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.operation.CacheReplicationOperation;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CacheNearCacheStateHolder
implements IdentifiedDataSerializable {
    private UUID partitionUuid;
    private List<Object> cacheNameSequencePairs = Collections.emptyList();
    private CacheReplicationOperation cacheReplicationOperation;

    public CacheNearCacheStateHolder() {
    }

    public CacheNearCacheStateHolder(CacheReplicationOperation cacheReplicationOperation) {
        this.cacheReplicationOperation = cacheReplicationOperation;
    }

    void prepare(CachePartitionSegment segment, Collection<ServiceNamespace> namespaces) {
        ICacheService cacheService = segment.getCacheService();
        MetaDataGenerator metaData = this.getPartitionMetaDataGenerator(cacheService);
        int partitionId = segment.getPartitionId();
        this.partitionUuid = metaData.getOrCreateUuid(partitionId);
        this.cacheNameSequencePairs = new ArrayList<Object>(namespaces.size());
        for (ServiceNamespace namespace : namespaces) {
            ObjectNamespace ns = (ObjectNamespace)namespace;
            String cacheName = ns.getObjectName();
            this.cacheNameSequencePairs.add(cacheName);
            this.cacheNameSequencePairs.add(metaData.currentSequence(cacheName, partitionId));
        }
    }

    private MetaDataGenerator getPartitionMetaDataGenerator(ICacheService cacheService) {
        CacheEventHandler cacheEventHandler = ((AbstractCacheService)cacheService).getCacheEventHandler();
        return cacheEventHandler.getMetaDataGenerator();
    }

    public void applyState() {
        CacheService cacheService = (CacheService)this.cacheReplicationOperation.getService();
        MetaDataGenerator metaDataGenerator = this.getPartitionMetaDataGenerator(cacheService);
        int partitionId = this.cacheReplicationOperation.getPartitionId();
        if (this.partitionUuid != null) {
            metaDataGenerator.setUuid(partitionId, this.partitionUuid);
        }
        int i = 0;
        while (i < this.cacheNameSequencePairs.size()) {
            String cacheName = (String)this.cacheNameSequencePairs.get(i++);
            long sequence = (Long)this.cacheNameSequencePairs.get(i++);
            metaDataGenerator.setCurrentSequence(cacheName, partitionId, sequence);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        boolean nullUuid = this.partitionUuid == null;
        out.writeBoolean(nullUuid);
        if (!nullUuid) {
            out.writeLong(this.partitionUuid.getMostSignificantBits());
            out.writeLong(this.partitionUuid.getLeastSignificantBits());
        }
        out.writeInt(this.cacheNameSequencePairs.size());
        for (Object item : this.cacheNameSequencePairs) {
            out.writeObject(item);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        boolean nullUuid = in.readBoolean();
        this.partitionUuid = nullUuid ? null : new UUID(in.readLong(), in.readLong());
        int size = in.readInt();
        this.cacheNameSequencePairs = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            this.cacheNameSequencePairs.add(in.readObject());
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 54;
    }
}

