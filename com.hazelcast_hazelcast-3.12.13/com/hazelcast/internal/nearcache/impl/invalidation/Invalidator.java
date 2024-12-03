/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.IFunction;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.internal.nearcache.impl.invalidation.SingleNearCacheInvalidation;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.UUID;

public abstract class Invalidator {
    protected final int partitionCount;
    protected final String serviceName;
    protected final ILogger logger;
    protected final NodeEngine nodeEngine;
    protected final EventService eventService;
    protected final MetaDataGenerator metaDataGenerator;
    protected final IPartitionService partitionService;
    protected final IFunction<EventRegistration, Boolean> eventFilter;

    public Invalidator(String serviceName, IFunction<EventRegistration, Boolean> eventFilter, NodeEngine nodeEngine) {
        this.serviceName = serviceName;
        this.eventFilter = eventFilter;
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.partitionService = nodeEngine.getPartitionService();
        this.eventService = nodeEngine.getEventService();
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.metaDataGenerator = new MetaDataGenerator(this.partitionCount);
    }

    protected abstract void invalidateInternal(Invalidation var1, int var2);

    public final void invalidateKey(Data key, String dataStructureName, String sourceUuid) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Preconditions.checkNotNull(sourceUuid, "sourceUuid cannot be null");
        Invalidation invalidation = this.newKeyInvalidation(key, dataStructureName, sourceUuid);
        this.invalidateInternal(invalidation, this.getPartitionId(key));
    }

    public final void invalidateAllKeys(String dataStructureName, String sourceUuid) {
        Preconditions.checkNotNull(sourceUuid, "sourceUuid cannot be null");
        int orderKey = this.getPartitionId(dataStructureName);
        Invalidation invalidation = this.newClearInvalidation(dataStructureName, sourceUuid);
        this.sendImmediately(invalidation, orderKey);
    }

    public final MetaDataGenerator getMetaDataGenerator() {
        return this.metaDataGenerator;
    }

    public final void resetPartitionMetaData(String dataStructureName, int partitionId) {
        MetaDataGenerator metaDataGenerator = this.getMetaDataGenerator();
        metaDataGenerator.regenerateUuid(partitionId);
        metaDataGenerator.resetSequence(dataStructureName, partitionId);
    }

    private Invalidation newKeyInvalidation(Data key, String dataStructureName, String sourceUuid) {
        int partitionId = this.getPartitionId(key);
        return this.newInvalidation(key, dataStructureName, sourceUuid, partitionId);
    }

    private Invalidation newClearInvalidation(String dataStructureName, String sourceUuid) {
        int partitionId = this.getPartitionId(dataStructureName);
        return this.newInvalidation(null, dataStructureName, sourceUuid, partitionId);
    }

    protected Invalidation newInvalidation(Data key, String dataStructureName, String sourceUuid, int partitionId) {
        long sequence = this.metaDataGenerator.nextSequence(dataStructureName, partitionId);
        UUID partitionUuid = this.metaDataGenerator.getOrCreateUuid(partitionId);
        return new SingleNearCacheInvalidation(ToHeapDataConverter.toHeapData(key), dataStructureName, sourceUuid, partitionUuid, sequence);
    }

    private int getPartitionId(Data o) {
        return this.partitionService.getPartitionId(o);
    }

    private int getPartitionId(Object o) {
        return this.partitionService.getPartitionId(o);
    }

    protected final void sendImmediately(Invalidation invalidation, int orderKey) {
        String dataStructureName = invalidation.getName();
        Collection<EventRegistration> registrations = this.eventService.getRegistrations(this.serviceName, dataStructureName);
        for (EventRegistration registration : registrations) {
            if (!this.eventFilter.apply(registration).booleanValue()) continue;
            this.eventService.publishEvent(this.serviceName, registration, (Object)invalidation, orderKey);
        }
    }

    public void destroy(String dataStructureName, String sourceUuid) {
        this.invalidateAllKeys(dataStructureName, sourceUuid);
        this.metaDataGenerator.destroyMetaDataFor(dataStructureName);
    }

    public void reset() {
    }

    public void shutdown() {
    }
}

