/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.MinimalPartitionService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public final class RepairingHandler {
    private final int partitionCount;
    private final boolean serializeKeys;
    private final ILogger logger;
    private final String localUuid;
    private final String name;
    private final NearCache nearCache;
    private final SerializationService serializationService;
    private final MinimalPartitionService partitionService;
    private final MetaDataContainer[] metaDataContainers;

    public RepairingHandler(ILogger logger, String localUuid, String name, NearCache nearCache, SerializationService serializationService, MinimalPartitionService partitionService) {
        this.logger = logger;
        this.localUuid = localUuid;
        this.name = name;
        this.nearCache = nearCache;
        this.serializeKeys = nearCache.isSerializeKeys();
        this.serializationService = serializationService;
        this.partitionService = partitionService;
        this.partitionCount = partitionService.getPartitionCount();
        this.metaDataContainers = RepairingHandler.createMetadataContainers(this.partitionCount);
    }

    private static MetaDataContainer[] createMetadataContainers(int partitionCount) {
        MetaDataContainer[] metaData = new MetaDataContainer[partitionCount];
        for (int partition = 0; partition < partitionCount; ++partition) {
            metaData[partition] = new MetaDataContainer();
        }
        return metaData;
    }

    public MetaDataContainer getMetaDataContainer(int partition) {
        return this.metaDataContainers[partition];
    }

    public void handle(Data key, String sourceUuid, UUID partitionUuid, long sequence) {
        if (!this.localUuid.equals(sourceUuid)) {
            if (key == null) {
                this.nearCache.clear();
            } else {
                this.nearCache.invalidate(this.serializeKeys ? key : this.serializationService.toObject(key));
            }
        }
        int partitionId = this.getPartitionIdOrDefault(key);
        this.checkOrRepairUuid(partitionId, partitionUuid);
        this.checkOrRepairSequence(partitionId, sequence, false);
    }

    private int getPartitionIdOrDefault(Data key) {
        if (key == null) {
            return this.partitionService.getPartitionId(this.name);
        }
        return this.partitionService.getPartitionId(key);
    }

    public void handle(Collection<Data> keys, Collection<String> sourceUuids, Collection<UUID> partitionUuids, Collection<Long> sequences) {
        Iterator<Data> keyIterator = keys.iterator();
        Iterator<Long> sequenceIterator = sequences.iterator();
        Iterator<UUID> partitionUuidIterator = partitionUuids.iterator();
        Iterator<String> sourceUuidsIterator = sourceUuids.iterator();
        while (keyIterator.hasNext() && sourceUuidsIterator.hasNext() && partitionUuidIterator.hasNext() && sequenceIterator.hasNext()) {
            this.handle(keyIterator.next(), sourceUuidsIterator.next(), partitionUuidIterator.next(), sequenceIterator.next());
        }
    }

    public String getName() {
        return this.name;
    }

    public void updateLastKnownStaleSequence(MetaDataContainer metaData, int partition) {
        long lastReceivedSequence;
        long lastKnownStaleSequence;
        do {
            lastReceivedSequence = metaData.getSequence();
        } while ((lastKnownStaleSequence = metaData.getStaleSequence()) < lastReceivedSequence && !metaData.casStaleSequence(lastKnownStaleSequence, lastReceivedSequence));
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("%s:[map=%s,partition=%d,lowerSequencesStaleThan=%d,lastReceivedSequence=%d]", "Stale sequences updated", this.name, partition, metaData.getStaleSequence(), metaData.getSequence()));
        }
    }

    public void checkOrRepairUuid(int partition, UUID newUuid) {
        UUID prevUuid;
        assert (newUuid != null);
        MetaDataContainer metaData = this.getMetaDataContainer(partition);
        while ((prevUuid = metaData.getUuid()) == null || !prevUuid.equals(newUuid)) {
            if (!metaData.casUuid(prevUuid, newUuid)) continue;
            metaData.resetSequence();
            metaData.resetStaleSequence();
            if (!this.logger.isFinestEnabled()) break;
            this.logger.finest(String.format("%s:[name=%s,partition=%d,prevUuid=%s,newUuid=%s]", "Invalid UUID, lost remote partition data unexpectedly", this.name, partition, prevUuid, newUuid));
            break;
        }
    }

    public void checkOrRepairSequence(int partition, long nextSequence, boolean viaAntiEntropy) {
        long currentSequence;
        assert (nextSequence > 0L);
        MetaDataContainer metaData = this.getMetaDataContainer(partition);
        while ((currentSequence = metaData.getSequence()) < nextSequence) {
            if (!metaData.casSequence(currentSequence, nextSequence)) continue;
            long sequenceDiff = nextSequence - currentSequence;
            if (!viaAntiEntropy && sequenceDiff <= 1L) break;
            long missCount = viaAntiEntropy ? sequenceDiff : sequenceDiff - 1L;
            long totalMissCount = metaData.addAndGetMissedSequenceCount(missCount);
            if (!this.logger.isFinestEnabled()) break;
            this.logger.finest(String.format("%s:[map=%s,partition=%d,currentSequence=%d,nextSequence=%d,totalMissCount=%d]", "Invalid sequence", this.name, partition, currentSequence, nextSequence, totalMissCount));
            break;
        }
    }

    public String toString() {
        return "RepairingHandler{name='" + this.name + '\'' + ", localUuid='" + this.localUuid + '\'' + '}';
    }

    public void initUuid(int partitionID, UUID partitionUuid) {
        MetaDataContainer metaData = this.getMetaDataContainer(partitionID);
        metaData.setUuid(partitionUuid);
    }

    public void initSequence(int partitionID, long partitionSequence) {
        MetaDataContainer metaData = this.getMetaDataContainer(partitionID);
        metaData.setSequence(partitionSequence);
    }
}

