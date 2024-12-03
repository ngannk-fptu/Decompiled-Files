/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.eviction.ExpirationManager;
import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapKeyLoader;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContextEventListenerSupport;
import com.hazelcast.map.impl.MapServiceContextInterceptorSupport;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.eviction.MapClearExpiredRecordsTask;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.query.PartitionScanRunner;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.map.impl.query.ResultProcessorRegistry;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.map.merge.MergePolicyProvider;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexProvider;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.predicates.QueryOptimizer;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Predicate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface MapServiceContext
extends MapServiceContextInterceptorSupport,
MapServiceContextEventListenerSupport {
    public Object toObject(Object var1);

    public Data toData(Object var1, PartitioningStrategy var2);

    public Data toData(Object var1);

    public MapContainer getMapContainer(String var1);

    public Map<String, MapContainer> getMapContainers();

    public PartitionContainer getPartitionContainer(int var1);

    public void initPartitionsContainers();

    public void removeRecordStoresFromPartitionMatchingWith(Predicate<RecordStore> var1, int var2, boolean var3, boolean var4);

    public MapService getService();

    public void destroyMapStores();

    public void flushMaps();

    public void destroyMap(String var1);

    public void reset();

    public void shutdown();

    public RecordStore createRecordStore(MapContainer var1, int var2, MapKeyLoader var3);

    public RecordStore getRecordStore(int var1, String var2);

    public RecordStore getRecordStore(int var1, String var2, boolean var3);

    public RecordStore getExistingRecordStore(int var1, String var2);

    public Collection<Integer> getOwnedPartitions();

    public void reloadOwnedPartitions();

    public AtomicInteger getWriteBehindQueueItemCounter();

    public ExpirationManager getExpirationManager();

    public void setService(MapService var1);

    public NodeEngine getNodeEngine();

    public MergePolicyProvider getMergePolicyProvider();

    public Object getMergePolicy(String var1);

    public MapEventPublisher getMapEventPublisher();

    public MapEventJournal getEventJournal();

    public QueryEngine getQueryEngine(String var1);

    public QueryRunner getMapQueryRunner(String var1);

    public QueryOptimizer getQueryOptimizer();

    public LocalMapStatsProvider getLocalMapStatsProvider();

    public MapClearExpiredRecordsTask getClearExpiredRecordsTask();

    public MapOperationProvider getMapOperationProvider(String var1);

    public MapOperationProvider getMapOperationProvider(MapConfig var1);

    public IndexProvider getIndexProvider(MapConfig var1);

    public Extractors getExtractors(String var1);

    public void incrementOperationStats(long var1, LocalMapStatsImpl var3, String var4, Operation var5);

    public boolean removeMapContainer(MapContainer var1);

    public PartitioningStrategy getPartitioningStrategy(String var1, PartitioningStrategyConfig var2);

    public void removePartitioningStrategyFromCache(String var1);

    public PartitionContainer[] getPartitionContainers();

    public void onClusterStateChange(ClusterState var1);

    public PartitionScanRunner getPartitionScanRunner();

    public ResultProcessorRegistry getResultProcessorRegistry();

    public MapNearCacheManager getMapNearCacheManager();

    public QueryCacheContext getQueryCacheContext();

    public String addListenerAdapter(ListenerAdapter var1, EventFilter var2, String var3);

    public String addLocalListenerAdapter(ListenerAdapter var1, String var2);

    public IndexCopyBehavior getIndexCopyBehavior();

    public Collection<RecordStoreMutationObserver<Record>> createRecordStoreMutationObservers(String var1, int var2);

    public ValueComparator getValueComparatorOf(InMemoryFormat var1);
}

