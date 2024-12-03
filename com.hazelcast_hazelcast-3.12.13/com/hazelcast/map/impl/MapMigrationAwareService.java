/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.operation.MapReplicationOperation;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.publisher.AccumulatorSweeper;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.function.Predicate;
import java.util.Collection;
import java.util.Iterator;

class MapMigrationAwareService
implements FragmentedMigrationAwareService {
    protected final PartitionContainer[] containers;
    protected final MapServiceContext mapServiceContext;
    protected final SerializationService serializationService;

    MapMigrationAwareService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.serializationService = mapServiceContext.getNodeEngine().getSerializationService();
        this.containers = mapServiceContext.getPartitionContainers();
    }

    @Override
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent event) {
        return this.containers[event.getPartitionId()].getAllNamespaces(event.getReplicaIndex());
    }

    @Override
    public boolean isKnownServiceNamespace(ServiceNamespace namespace) {
        return namespace instanceof ObjectNamespace && "hz:impl:mapService".equals(namespace.getServiceName());
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
        if (MapMigrationAwareService.isLocalPromotion(event)) {
            this.clearNonGlobalIndexes(event);
            this.populateIndexes(event, TargetIndexes.NON_GLOBAL);
        }
        this.flushAndRemoveQueryCaches(event);
    }

    private void flushAndRemoveQueryCaches(PartitionMigrationEvent event) {
        int partitionId = event.getPartitionId();
        QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
        PublisherContext publisherContext = queryCacheContext.getPublisherContext();
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            AccumulatorSweeper.flushAccumulator(publisherContext, partitionId);
            AccumulatorSweeper.removeAccumulator(publisherContext, partitionId);
            return;
        }
        if (MapMigrationAwareService.isLocalPromotion(event)) {
            AccumulatorSweeper.removeAccumulator(publisherContext, partitionId);
            AccumulatorSweeper.sendEndOfSequenceEvents(publisherContext, partitionId);
            return;
        }
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        int partitionId = event.getPartitionId();
        MapReplicationOperation operation = new MapReplicationOperation(this.containers[partitionId], partitionId, event.getReplicaIndex());
        operation.setService(this.mapServiceContext.getService());
        operation.setNodeEngine(this.mapServiceContext.getNodeEngine());
        return operation;
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event, Collection<ServiceNamespace> namespaces) {
        assert (this.assertAllKnownNamespaces(namespaces));
        int partitionId = event.getPartitionId();
        MapReplicationOperation operation = new MapReplicationOperation(this.containers[partitionId], namespaces, partitionId, event.getReplicaIndex());
        operation.setService(this.mapServiceContext.getService());
        operation.setNodeEngine(this.mapServiceContext.getNodeEngine());
        return operation;
    }

    private boolean assertAllKnownNamespaces(Collection<ServiceNamespace> namespaces) {
        for (ServiceNamespace namespace : namespaces) {
            assert (this.isKnownServiceNamespace(namespace)) : namespace + " is not a MapService namespace!";
        }
        return true;
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.populateIndexes(event, TargetIndexes.GLOBAL);
        } else {
            this.depopulateIndexes(event);
        }
        if (MigrationEndpoint.SOURCE == event.getMigrationEndpoint()) {
            this.removeRecordStoresHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(event.getPartitionId());
        for (RecordStore recordStore : partitionContainer.getAllRecordStores()) {
            recordStore.startLoading();
        }
        this.mapServiceContext.reloadOwnedPartitions();
        this.removeOrRegenerateNearCacheUuid(event);
    }

    private void removeOrRegenerateNearCacheUuid(PartitionMigrationEvent event) {
        if (MigrationEndpoint.SOURCE == event.getMigrationEndpoint()) {
            this.getMetaDataGenerator().removeUuidAndSequence(event.getPartitionId());
            return;
        }
        if (MigrationEndpoint.DESTINATION == event.getMigrationEndpoint() && event.getNewReplicaIndex() != 0) {
            this.getMetaDataGenerator().regenerateUuid(event.getPartitionId());
            return;
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (MigrationEndpoint.DESTINATION == event.getMigrationEndpoint()) {
            this.removeRecordStoresHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
            this.getMetaDataGenerator().removeUuidAndSequence(event.getPartitionId());
        }
        this.mapServiceContext.reloadOwnedPartitions();
    }

    private void clearNonGlobalIndexes(PartitionMigrationEvent event) {
        PartitionContainer container = this.mapServiceContext.getPartitionContainer(event.getPartitionId());
        for (RecordStore recordStore : container.getMaps().values()) {
            MapContainer mapContainer = this.mapServiceContext.getMapContainer(recordStore.getName());
            Indexes indexes = mapContainer.getIndexes(event.getPartitionId());
            if (!indexes.haveAtLeastOneIndex() || indexes.isGlobal()) continue;
            indexes.clearAll();
        }
    }

    private void removeRecordStoresHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        if (thresholdReplicaIndex < 0) {
            this.mapServiceContext.removeRecordStoresFromPartitionMatchingWith(MapMigrationAwareService.allRecordStores(), partitionId, false, true);
        } else {
            this.mapServiceContext.removeRecordStoresFromPartitionMatchingWith(MapMigrationAwareService.lesserBackupMapsThen(thresholdReplicaIndex), partitionId, false, true);
        }
    }

    private static Predicate<RecordStore> allRecordStores() {
        return new Predicate<RecordStore>(){

            @Override
            public boolean test(RecordStore recordStore) {
                return true;
            }
        };
    }

    private static Predicate<RecordStore> lesserBackupMapsThen(final int backupCount) {
        return new Predicate<RecordStore>(){

            @Override
            public boolean test(RecordStore recordStore) {
                return recordStore.getMapContainer().getTotalBackupCount() < backupCount;
            }
        };
    }

    private MetaDataGenerator getMetaDataGenerator() {
        return this.mapServiceContext.getMapNearCacheManager().getInvalidator().getMetaDataGenerator();
    }

    private void populateIndexes(PartitionMigrationEvent event, TargetIndexes targetIndexes) {
        assert (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION);
        assert (targetIndexes != null);
        if (event.getNewReplicaIndex() != 0) {
            return;
        }
        long now = this.getNow();
        PartitionContainer container = this.mapServiceContext.getPartitionContainer(event.getPartitionId());
        for (RecordStore recordStore : container.getMaps().values()) {
            MapContainer mapContainer = this.mapServiceContext.getMapContainer(recordStore.getName());
            Indexes indexes = mapContainer.getIndexes(event.getPartitionId());
            indexes.createIndexesFromRecordedDefinitions();
            if (!indexes.haveAtLeastOneIndex() || indexes.isGlobal() && targetIndexes == TargetIndexes.NON_GLOBAL || !indexes.isGlobal() && targetIndexes == TargetIndexes.GLOBAL) continue;
            InternalIndex[] indexesSnapshot = indexes.getIndexes();
            Iterator<Record> iterator = recordStore.iterator(now, false);
            while (iterator.hasNext()) {
                Record record = iterator.next();
                Data key = record.getKey();
                Object value = Records.getValueOrCachedValue(record, this.serializationService);
                if (value == null) continue;
                QueryableEntry queryEntry = mapContainer.newQueryEntry(key, value);
                indexes.putEntry(queryEntry, null, Index.OperationSource.SYSTEM);
            }
            Indexes.markPartitionAsIndexed(event.getPartitionId(), indexesSnapshot);
        }
    }

    private void depopulateIndexes(PartitionMigrationEvent event) {
        assert (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE);
        assert (event.getNewReplicaIndex() != 0) : "Invalid migration event: " + event;
        if (event.getCurrentReplicaIndex() != 0) {
            return;
        }
        long now = this.getNow();
        PartitionContainer container = this.mapServiceContext.getPartitionContainer(event.getPartitionId());
        for (RecordStore recordStore : container.getMaps().values()) {
            MapContainer mapContainer = this.mapServiceContext.getMapContainer(recordStore.getName());
            Indexes indexes = mapContainer.getIndexes(event.getPartitionId());
            if (!indexes.haveAtLeastOneIndex()) continue;
            InternalIndex[] indexesSnapshot = indexes.getIndexes();
            Iterator<Record> iterator = recordStore.iterator(now, false);
            while (iterator.hasNext()) {
                Record record = iterator.next();
                Data key = record.getKey();
                Object value = Records.getValueOrCachedValue(record, this.serializationService);
                indexes.removeEntry(key, value, Index.OperationSource.SYSTEM);
            }
            Indexes.markPartitionAsUnindexed(event.getPartitionId(), indexesSnapshot);
        }
    }

    private static boolean isLocalPromotion(PartitionMigrationEvent event) {
        return event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION && event.getCurrentReplicaIndex() > 0 && event.getNewReplicaIndex() == 0;
    }

    protected long getNow() {
        return Clock.currentTimeMillis();
    }

    private static enum TargetIndexes {
        GLOBAL,
        NON_GLOBAL;

    }
}

