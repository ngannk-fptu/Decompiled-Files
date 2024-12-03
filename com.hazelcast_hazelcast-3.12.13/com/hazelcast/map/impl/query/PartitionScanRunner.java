/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Metadata;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.PagingPredicateAccessor;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntriesSegment;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.SortingUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PartitionScanRunner {
    protected final MapServiceContext mapServiceContext;
    protected final NodeEngine nodeEngine;
    protected final ILogger logger;
    protected final InternalSerializationService serializationService;
    protected final IPartitionService partitionService;
    protected final OperationService operationService;
    protected final ClusterService clusterService;

    public PartitionScanRunner(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.serializationService = (InternalSerializationService)this.nodeEngine.getSerializationService();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.logger = this.nodeEngine.getLogger(this.getClass());
        this.operationService = this.nodeEngine.getOperationService();
        this.clusterService = this.nodeEngine.getClusterService();
    }

    public void run(String mapName, Predicate predicate, int partitionId, Result result) {
        PagingPredicate pagingPredicate = predicate instanceof PagingPredicate ? (PagingPredicate)predicate : null;
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(mapName);
        RecordStore recordStore = partitionContainer.getRecordStore(mapName);
        Iterator<Record> iterator = recordStore.loadAwareIterator(this.getNow(), false);
        Map.Entry<Integer, Map.Entry> nearestAnchorEntry = PagingPredicateAccessor.getNearestAnchorEntry(pagingPredicate);
        boolean useCachedValues = this.isUseCachedDeserializedValuesEnabled(mapContainer, partitionId);
        Extractors extractors = this.mapServiceContext.getExtractors(mapName);
        LazyMapEntry queryEntry = new LazyMapEntry();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            Data key = (Data)this.toData(record.getKey());
            Metadata metadata = this.getMetadataFromRecord(recordStore, record);
            Object value = this.toData(useCachedValues ? Records.getValueOrCachedValue(record, this.serializationService) : record.getValue());
            if (value == null) continue;
            queryEntry.init(this.serializationService, key, value, extractors);
            queryEntry.setMetadata(metadata);
            boolean valid = predicate.apply(queryEntry);
            if (!valid || !SortingUtil.compareAnchor(pagingPredicate, queryEntry, nearestAnchorEntry)) continue;
            result.add(queryEntry);
            queryEntry = new LazyMapEntry();
        }
        result.orderAndLimit(pagingPredicate, nearestAnchorEntry);
    }

    protected Metadata getMetadataFromRecord(RecordStore recordStore, Record record) {
        return record.getMetadata();
    }

    public QueryableEntriesSegment run(String mapName, Predicate predicate, int partitionId, int tableIndex, int fetchSize) {
        int lastIndex = tableIndex;
        LinkedList<QueryableEntry> resultList = new LinkedList<QueryableEntry>();
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        RecordStore recordStore = partitionContainer.getRecordStore(mapName);
        Extractors extractors = this.mapServiceContext.getExtractors(mapName);
        while (resultList.size() < fetchSize && lastIndex >= 0) {
            MapEntriesWithCursor cursor = recordStore.fetchEntries(lastIndex, fetchSize - resultList.size());
            lastIndex = cursor.getNextTableIndexToReadFrom();
            List entries = cursor.getBatch();
            if (entries.isEmpty()) break;
            for (Map.Entry entry : entries) {
                LazyMapEntry queryEntry = new LazyMapEntry((Data)entry.getKey(), entry.getValue(), this.serializationService, extractors);
                if (!predicate.apply(queryEntry)) continue;
                resultList.add(queryEntry);
            }
        }
        return new QueryableEntriesSegment(resultList, lastIndex);
    }

    protected boolean isUseCachedDeserializedValuesEnabled(MapContainer mapContainer, int partitionId) {
        CacheDeserializedValues cacheDeserializedValues = mapContainer.getMapConfig().getCacheDeserializedValues();
        switch (cacheDeserializedValues) {
            case NEVER: {
                return false;
            }
            case ALWAYS: {
                return true;
            }
        }
        return mapContainer.getIndexes(partitionId).haveAtLeastOneIndex();
    }

    protected <T> Object toData(T input) {
        return input;
    }

    protected long getNow() {
        return Clock.currentTimeMillis();
    }
}

