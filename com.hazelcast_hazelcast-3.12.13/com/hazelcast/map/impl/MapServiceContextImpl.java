/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.eviction.ExpirationManager;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.internal.util.LocalRetryableExecution;
import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.internal.util.comparators.ValueComparatorUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.InterceptorRegistry;
import com.hazelcast.map.impl.InternalMapPartitionLostListenerAdapter;
import com.hazelcast.map.impl.JsonMetadataInitializer;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.ListenerAdapters;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapKeyLoader;
import com.hazelcast.map.impl.MapListenerFlagOperator;
import com.hazelcast.map.impl.MapPartitionLostEventFilter;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapStoreWrapper;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.PartitioningStrategyFactory;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.event.MapEventPublisherImpl;
import com.hazelcast.map.impl.eviction.MapClearExpiredRecordsTask;
import com.hazelcast.map.impl.journal.MapEventJournal;
import com.hazelcast.map.impl.journal.RingbufferMapEventJournalImpl;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.map.impl.operation.BaseRemoveOperation;
import com.hazelcast.map.impl.operation.GetOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.MapOperationProviders;
import com.hazelcast.map.impl.operation.MapPartitionDestroyOperation;
import com.hazelcast.map.impl.query.AccumulationExecutor;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.map.impl.query.AggregationResultProcessor;
import com.hazelcast.map.impl.query.CallerRunsAccumulationExecutor;
import com.hazelcast.map.impl.query.CallerRunsPartitionScanExecutor;
import com.hazelcast.map.impl.query.ParallelAccumulationExecutor;
import com.hazelcast.map.impl.query.ParallelPartitionScanExecutor;
import com.hazelcast.map.impl.query.PartitionScanExecutor;
import com.hazelcast.map.impl.query.PartitionScanRunner;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryEngineImpl;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultProcessor;
import com.hazelcast.map.impl.query.QueryRunner;
import com.hazelcast.map.impl.query.ResultProcessorRegistry;
import com.hazelcast.map.impl.querycache.NodeQueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.DefaultRecordStore;
import com.hazelcast.map.impl.recordstore.EventJournalWriterRecordStoreMutationObserver;
import com.hazelcast.map.impl.recordstore.JsonMetadataRecordStoreMutationObserver;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.map.merge.MergePolicyProvider;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataType;
import com.hazelcast.query.impl.DefaultIndexProvider;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexProvider;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.predicates.QueryOptimizer;
import com.hazelcast.query.impl.predicates.QueryOptimizerFactory;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.executor.ManagedExecutorService;
import com.hazelcast.util.function.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class MapServiceContextImpl
implements MapServiceContext {
    protected static final long DESTROY_TIMEOUT_SECONDS = 30L;
    protected final ConcurrentMap<String, MapContainer> mapContainers = new ConcurrentHashMap<String, MapContainer>();
    protected final AtomicReference<Collection<Integer>> ownedPartitions = new AtomicReference();
    protected final IndexProvider indexProvider = new DefaultIndexProvider();
    protected final ContextMutexFactory contextMutexFactory = new ContextMutexFactory();
    protected final AtomicInteger writeBehindQueueItemCounter = new AtomicInteger(0);
    protected final NodeEngine nodeEngine;
    protected final InternalSerializationService serializationService;
    protected final ConstructorFunction<String, MapContainer> mapConstructor;
    protected final PartitionContainer[] partitionContainers;
    protected final MapClearExpiredRecordsTask clearExpiredRecordsTask;
    protected final ExpirationManager expirationManager;
    protected final MapNearCacheManager mapNearCacheManager;
    protected final LocalMapStatsProvider localMapStatsProvider;
    protected final MergePolicyProvider mergePolicyProvider;
    protected final QueryEngine queryEngine;
    protected final QueryRunner mapQueryRunner;
    protected final PartitionScanRunner partitionScanRunner;
    protected final QueryOptimizer queryOptimizer;
    protected final PartitioningStrategyFactory partitioningStrategyFactory;
    protected final QueryCacheContext queryCacheContext;
    protected final MapEventJournal eventJournal;
    protected final MapEventPublisher mapEventPublisher;
    protected final EventService eventService;
    protected final MapOperationProviders operationProviders;
    protected final ResultProcessorRegistry resultProcessorRegistry;
    protected ILogger logger;
    protected MapService mapService;

    MapServiceContextImpl(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.mapConstructor = this.createMapConstructor();
        this.queryCacheContext = new NodeQueryCacheContext(this);
        this.partitionContainers = this.createPartitionContainers();
        this.clearExpiredRecordsTask = new MapClearExpiredRecordsTask(this.partitionContainers, nodeEngine);
        this.expirationManager = new ExpirationManager(this.clearExpiredRecordsTask, nodeEngine);
        this.mapNearCacheManager = this.createMapNearCacheManager();
        this.localMapStatsProvider = this.createLocalMapStatsProvider();
        this.mergePolicyProvider = new MergePolicyProvider(nodeEngine);
        this.mapEventPublisher = this.createMapEventPublisherSupport();
        this.eventJournal = this.createEventJournal();
        this.queryOptimizer = QueryOptimizerFactory.newOptimizer(nodeEngine.getProperties());
        this.resultProcessorRegistry = this.createResultProcessorRegistry(this.serializationService);
        this.partitionScanRunner = this.createPartitionScanRunner();
        this.queryEngine = this.createMapQueryEngine();
        this.mapQueryRunner = this.createMapQueryRunner(nodeEngine, this.queryOptimizer, this.resultProcessorRegistry, this.partitionScanRunner);
        this.eventService = nodeEngine.getEventService();
        this.operationProviders = this.createOperationProviders();
        this.partitioningStrategyFactory = new PartitioningStrategyFactory(nodeEngine.getConfigClassLoader());
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    ConstructorFunction<String, MapContainer> createMapConstructor() {
        return new ConstructorFunction<String, MapContainer>(){

            @Override
            public MapContainer createNew(String mapName) {
                MapServiceContext mapServiceContext = MapServiceContextImpl.this.getService().getMapServiceContext();
                return new MapContainer(mapName, MapServiceContextImpl.this.nodeEngine.getConfig(), mapServiceContext);
            }
        };
    }

    MapNearCacheManager createMapNearCacheManager() {
        return new MapNearCacheManager(this);
    }

    MapOperationProviders createOperationProviders() {
        return new MapOperationProviders(this);
    }

    MapEventPublisherImpl createMapEventPublisherSupport() {
        return new MapEventPublisherImpl(this);
    }

    private MapEventJournal createEventJournal() {
        return new RingbufferMapEventJournalImpl(this.getNodeEngine(), this);
    }

    protected LocalMapStatsProvider createLocalMapStatsProvider() {
        return new LocalMapStatsProvider(this);
    }

    private QueryEngineImpl createMapQueryEngine() {
        return new QueryEngineImpl(this);
    }

    private PartitionScanRunner createPartitionScanRunner() {
        return new PartitionScanRunner(this);
    }

    protected QueryRunner createMapQueryRunner(NodeEngine nodeEngine, QueryOptimizer queryOptimizer, ResultProcessorRegistry resultProcessorRegistry, PartitionScanRunner partitionScanRunner) {
        PartitionScanExecutor partitionScanExecutor;
        boolean parallelEvaluation = nodeEngine.getProperties().getBoolean(GroupProperty.QUERY_PREDICATE_PARALLEL_EVALUATION);
        if (parallelEvaluation) {
            int opTimeoutInMillis = nodeEngine.getProperties().getInteger(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS);
            ManagedExecutorService queryExecutorService = nodeEngine.getExecutionService().getExecutor("hz:query");
            partitionScanExecutor = new ParallelPartitionScanExecutor(partitionScanRunner, queryExecutorService, opTimeoutInMillis);
        } else {
            partitionScanExecutor = new CallerRunsPartitionScanExecutor(partitionScanRunner);
        }
        return new QueryRunner(this, queryOptimizer, partitionScanExecutor, resultProcessorRegistry);
    }

    private ResultProcessorRegistry createResultProcessorRegistry(SerializationService ss) {
        ResultProcessorRegistry registry = new ResultProcessorRegistry();
        registry.registerProcessor(QueryResult.class, this.createQueryResultProcessor(ss));
        registry.registerProcessor(AggregationResult.class, this.createAggregationResultProcessor(ss));
        return registry;
    }

    private QueryResultProcessor createQueryResultProcessor(SerializationService ss) {
        return new QueryResultProcessor(ss);
    }

    private AggregationResultProcessor createAggregationResultProcessor(SerializationService ss) {
        AccumulationExecutor accumulationExecutor;
        boolean parallelAccumulation = this.nodeEngine.getProperties().getBoolean(GroupProperty.AGGREGATION_ACCUMULATION_PARALLEL_EVALUATION);
        int opTimeoutInMillis = this.nodeEngine.getProperties().getInteger(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS);
        if (parallelAccumulation) {
            ManagedExecutorService queryExecutorService = this.nodeEngine.getExecutionService().getExecutor("hz:query");
            accumulationExecutor = new ParallelAccumulationExecutor(queryExecutorService, ss, opTimeoutInMillis);
        } else {
            accumulationExecutor = new CallerRunsAccumulationExecutor(ss);
        }
        return new AggregationResultProcessor(accumulationExecutor, this.serializationService);
    }

    private PartitionContainer[] createPartitionContainers() {
        int partitionCount = this.nodeEngine.getPartitionService().getPartitionCount();
        return new PartitionContainer[partitionCount];
    }

    @Override
    public MapContainer getMapContainer(String mapName) {
        return ConcurrencyUtil.getOrPutSynchronized(this.mapContainers, mapName, this.contextMutexFactory, this.mapConstructor);
    }

    @Override
    public Map<String, MapContainer> getMapContainers() {
        return this.mapContainers;
    }

    @Override
    public PartitionContainer getPartitionContainer(int partitionId) {
        assert (partitionId != -1) : "Cannot be called with GENERIC_PARTITION_ID";
        return this.partitionContainers[partitionId];
    }

    @Override
    public void initPartitionsContainers() {
        int partitionCount = this.nodeEngine.getPartitionService().getPartitionCount();
        for (int i = 0; i < partitionCount; ++i) {
            this.partitionContainers[i] = this.createPartitionContainer(this.getService(), i);
        }
    }

    protected PartitionContainer createPartitionContainer(MapService service, int partitionId) {
        return new PartitionContainer(service, partitionId);
    }

    protected void removeAllRecordStoresOfAllMaps(boolean onShutdown, boolean onRecordStoreDestroy) {
        for (PartitionContainer partitionContainer : this.partitionContainers) {
            if (partitionContainer == null) continue;
            this.removeRecordStoresFromPartitionMatchingWith(MapServiceContextImpl.allRecordStores(), partitionContainer.getPartitionId(), onShutdown, onRecordStoreDestroy);
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

    @Override
    public void removeRecordStoresFromPartitionMatchingWith(Predicate<RecordStore> predicate, int partitionId, boolean onShutdown, boolean onRecordStoreDestroy) {
        PartitionContainer container = this.partitionContainers[partitionId];
        if (container == null) {
            return;
        }
        Iterator partitionIterator = container.getMaps().values().iterator();
        while (partitionIterator.hasNext()) {
            RecordStore partition = (RecordStore)partitionIterator.next();
            if (!predicate.test(partition)) continue;
            partition.clearPartition(onShutdown, onRecordStoreDestroy);
            partitionIterator.remove();
        }
    }

    @Override
    public MapService getService() {
        return this.mapService;
    }

    @Override
    public void setService(MapService mapService) {
        this.mapService = mapService;
    }

    @Override
    public void destroyMapStores() {
        for (MapContainer mapContainer : this.mapContainers.values()) {
            MapStoreWrapper store = mapContainer.getMapStoreContext().getMapStoreWrapper();
            if (store == null) continue;
            store.destroy();
        }
    }

    @Override
    public void flushMaps() {
        for (MapContainer mapContainer : this.mapContainers.values()) {
            mapContainer.getMapStoreContext().stop();
        }
        for (PartitionContainer partitionContainer : this.partitionContainers) {
            for (String mapName : this.mapContainers.keySet()) {
                RecordStore recordStore = partitionContainer.getExistingRecordStore(mapName);
                if (recordStore == null) continue;
                MapDataStore<Data, Object> mapDataStore = recordStore.getMapDataStore();
                mapDataStore.hardFlush();
            }
        }
    }

    @Override
    public void destroyMap(String mapName) {
        this.mapNearCacheManager.destroyNearCache(mapName);
        this.nodeEngine.getEventService().deregisterAllListeners("hz:impl:mapService", mapName);
        MapContainer mapContainer = (MapContainer)this.mapContainers.get(mapName);
        if (mapContainer == null) {
            return;
        }
        this.nodeEngine.getWanReplicationService().removeWanEventCounters("hz:impl:mapService", mapName);
        mapContainer.getMapStoreContext().stop();
        this.localMapStatsProvider.destroyLocalMapStatsImpl(mapContainer.getName());
        this.destroyPartitionsAndMapContainer(mapContainer);
    }

    private void destroyPartitionsAndMapContainer(MapContainer mapContainer) {
        ArrayList<LocalRetryableExecution> executions = new ArrayList<LocalRetryableExecution>();
        for (PartitionContainer container : this.partitionContainers) {
            MapPartitionDestroyOperation op = new MapPartitionDestroyOperation(container, mapContainer);
            executions.add(InvocationUtil.executeLocallyWithRetry(this.nodeEngine, op));
        }
        for (LocalRetryableExecution execution : executions) {
            try {
                if (execution.awaitCompletion(30L, TimeUnit.SECONDS)) continue;
                this.logger.warning("Map partition was not destroyed in expected time, possible leak");
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.nodeEngine.getLogger(this.getClass()).warning(e);
            }
        }
    }

    @Override
    public void reset() {
        this.removeAllRecordStoresOfAllMaps(false, false);
        this.mapNearCacheManager.reset();
    }

    @Override
    public void shutdown() {
        this.removeAllRecordStoresOfAllMaps(true, false);
        this.mapNearCacheManager.shutdown();
        this.mapContainers.clear();
        this.expirationManager.onShutdown();
    }

    @Override
    public RecordStore getRecordStore(int partitionId, String mapName) {
        return this.getPartitionContainer(partitionId).getRecordStore(mapName);
    }

    @Override
    public RecordStore getRecordStore(int partitionId, String mapName, boolean skipLoadingOnCreate) {
        return this.getPartitionContainer(partitionId).getRecordStore(mapName, skipLoadingOnCreate);
    }

    @Override
    public RecordStore getExistingRecordStore(int partitionId, String mapName) {
        return this.getPartitionContainer(partitionId).getExistingRecordStore(mapName);
    }

    @Override
    public Collection<Integer> getOwnedPartitions() {
        Collection<Integer> partitions = this.ownedPartitions.get();
        if (partitions == null) {
            this.reloadOwnedPartitions();
            partitions = this.ownedPartitions.get();
        }
        return partitions;
    }

    @Override
    public void reloadOwnedPartitions() {
        List<Integer> partitions;
        Set<Integer> newSet;
        Collection<Integer> expected;
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        while (!this.ownedPartitions.compareAndSet(expected = this.ownedPartitions.get(), newSet = Collections.unmodifiableSet(new LinkedHashSet<Integer>(partitions = partitionService.getMemberPartitions(this.nodeEngine.getThisAddress()))))) {
        }
    }

    @Override
    public AtomicInteger getWriteBehindQueueItemCounter() {
        return this.writeBehindQueueItemCounter;
    }

    @Override
    public ExpirationManager getExpirationManager() {
        return this.expirationManager;
    }

    @Override
    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public MergePolicyProvider getMergePolicyProvider() {
        return this.mergePolicyProvider;
    }

    @Override
    public Object getMergePolicy(String name) {
        MapContainer mapContainer = this.getMapContainer(name);
        MergePolicyConfig mergePolicyConfig = mapContainer.getMapConfig().getMergePolicyConfig();
        return this.mergePolicyProvider.getMergePolicy(mergePolicyConfig.getPolicy());
    }

    @Override
    public MapEventPublisher getMapEventPublisher() {
        return this.mapEventPublisher;
    }

    @Override
    public MapEventJournal getEventJournal() {
        return this.eventJournal;
    }

    @Override
    public QueryEngine getQueryEngine(String mapName) {
        return this.queryEngine;
    }

    @Override
    public QueryRunner getMapQueryRunner(String name) {
        return this.mapQueryRunner;
    }

    @Override
    public QueryOptimizer getQueryOptimizer() {
        return this.queryOptimizer;
    }

    @Override
    public LocalMapStatsProvider getLocalMapStatsProvider() {
        return this.localMapStatsProvider;
    }

    @Override
    public Object toObject(Object data) {
        return this.serializationService.toObject(data);
    }

    @Override
    public Data toData(Object object, PartitioningStrategy partitionStrategy) {
        return this.serializationService.toData(object, partitionStrategy);
    }

    @Override
    public Data toData(Object object) {
        return this.serializationService.toData(object, DataType.HEAP);
    }

    @Override
    public void interceptAfterGet(String mapName, Object value) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        List<MapInterceptor> interceptors = mapContainer.getInterceptorRegistry().getInterceptors();
        if (!interceptors.isEmpty()) {
            value = this.toObject(value);
            for (MapInterceptor interceptor : interceptors) {
                interceptor.afterGet(value);
            }
        }
    }

    @Override
    public Object interceptPut(String mapName, Object oldValue, Object newValue) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        List<MapInterceptor> interceptors = mapContainer.getInterceptorRegistry().getInterceptors();
        Object result = null;
        if (!interceptors.isEmpty()) {
            result = this.toObject(newValue);
            oldValue = this.toObject(oldValue);
            for (MapInterceptor interceptor : interceptors) {
                Object temp = interceptor.interceptPut(oldValue, result);
                if (temp == null) continue;
                result = temp;
            }
        }
        return result == null ? newValue : result;
    }

    @Override
    public void interceptAfterPut(String mapName, Object newValue) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        List<MapInterceptor> interceptors = mapContainer.getInterceptorRegistry().getInterceptors();
        if (!interceptors.isEmpty()) {
            newValue = this.toObject(newValue);
            for (MapInterceptor interceptor : interceptors) {
                interceptor.afterPut(newValue);
            }
        }
    }

    @Override
    public MapClearExpiredRecordsTask getClearExpiredRecordsTask() {
        return this.clearExpiredRecordsTask;
    }

    @Override
    public Object interceptRemove(String mapName, Object value) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        List<MapInterceptor> interceptors = mapContainer.getInterceptorRegistry().getInterceptors();
        Object result = null;
        if (!interceptors.isEmpty()) {
            result = this.toObject(value);
            for (MapInterceptor interceptor : interceptors) {
                Object temp = interceptor.interceptRemove(result);
                if (temp == null) continue;
                result = temp;
            }
        }
        return result == null ? value : result;
    }

    @Override
    public void interceptAfterRemove(String mapName, Object value) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        InterceptorRegistry interceptorRegistry = mapContainer.getInterceptorRegistry();
        List<MapInterceptor> interceptors = interceptorRegistry.getInterceptors();
        if (!interceptors.isEmpty()) {
            value = this.toObject(value);
            for (MapInterceptor interceptor : interceptors) {
                interceptor.afterRemove(value);
            }
        }
    }

    @Override
    public void addInterceptor(String id, String mapName, MapInterceptor interceptor) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        mapContainer.getInterceptorRegistry().register(id, interceptor);
    }

    @Override
    public String generateInterceptorId(String mapName, MapInterceptor interceptor) {
        return interceptor.getClass().getName() + interceptor.hashCode();
    }

    @Override
    public void removeInterceptor(String mapName, String id) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        mapContainer.getInterceptorRegistry().deregister(id);
    }

    @Override
    public Object interceptGet(String mapName, Object value) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        InterceptorRegistry interceptorRegistry = mapContainer.getInterceptorRegistry();
        List<MapInterceptor> interceptors = interceptorRegistry.getInterceptors();
        Object result = null;
        if (!interceptors.isEmpty()) {
            result = this.toObject(value);
            for (MapInterceptor interceptor : interceptors) {
                Object temp = interceptor.interceptGet(result);
                if (temp == null) continue;
                result = temp;
            }
        }
        return result == null ? value : result;
    }

    @Override
    public boolean hasInterceptor(String mapName) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        return !mapContainer.getInterceptorRegistry().getInterceptors().isEmpty();
    }

    @Override
    public String addLocalEventListener(Object listener, String mapName) {
        EventRegistration registration = this.addListenerInternal(listener, TrueEventFilter.INSTANCE, mapName, true);
        return registration.getId();
    }

    @Override
    public String addLocalEventListener(Object listener, EventFilter eventFilter, String mapName) {
        EventRegistration registration = this.addListenerInternal(listener, eventFilter, mapName, true);
        return registration.getId();
    }

    @Override
    public String addLocalPartitionLostListener(MapPartitionLostListener listener, String mapName) {
        InternalMapPartitionLostListenerAdapter listenerAdapter = new InternalMapPartitionLostListenerAdapter(listener);
        MapPartitionLostEventFilter filter = new MapPartitionLostEventFilter();
        EventRegistration registration = this.eventService.registerLocalListener("hz:impl:mapService", mapName, filter, listenerAdapter);
        return registration.getId();
    }

    @Override
    public String addEventListener(Object listener, EventFilter eventFilter, String mapName) {
        EventRegistration registration = this.addListenerInternal(listener, eventFilter, mapName, false);
        return registration.getId();
    }

    @Override
    public String addPartitionLostListener(MapPartitionLostListener listener, String mapName) {
        InternalMapPartitionLostListenerAdapter listenerAdapter = new InternalMapPartitionLostListenerAdapter(listener);
        MapPartitionLostEventFilter filter = new MapPartitionLostEventFilter();
        EventRegistration registration = this.eventService.registerListener("hz:impl:mapService", mapName, filter, listenerAdapter);
        return registration.getId();
    }

    private EventRegistration addListenerInternal(Object listener, EventFilter filter, String mapName, boolean local) {
        ListenerAdapter listenerAdaptor = ListenerAdapters.createListenerAdapter(listener);
        if (!(filter instanceof EventListenerFilter)) {
            int enabledListeners = MapListenerFlagOperator.setAndGetListenerFlags(listenerAdaptor);
            filter = new EventListenerFilter(enabledListeners, filter);
        }
        if (local) {
            return this.eventService.registerLocalListener("hz:impl:mapService", mapName, filter, listenerAdaptor);
        }
        return this.eventService.registerListener("hz:impl:mapService", mapName, filter, listenerAdaptor);
    }

    @Override
    public boolean removeEventListener(String mapName, String registrationId) {
        return this.eventService.deregisterListener("hz:impl:mapService", mapName, registrationId);
    }

    @Override
    public boolean removePartitionLostListener(String mapName, String registrationId) {
        return this.eventService.deregisterListener("hz:impl:mapService", mapName, registrationId);
    }

    @Override
    public MapOperationProvider getMapOperationProvider(String name) {
        return this.operationProviders.getOperationProvider(name);
    }

    @Override
    public MapOperationProvider getMapOperationProvider(MapConfig mapConfig) {
        return this.operationProviders.getOperationProvider(mapConfig);
    }

    @Override
    public IndexProvider getIndexProvider(MapConfig mapConfig) {
        return this.indexProvider;
    }

    @Override
    public Extractors getExtractors(String mapName) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        return mapContainer.getExtractors();
    }

    @Override
    public void incrementOperationStats(long startTime, LocalMapStatsImpl localMapStats, String mapName, Operation operation) {
        long durationNanos = System.nanoTime() - startTime;
        if (operation instanceof BasePutOperation) {
            localMapStats.incrementPutLatencyNanos(durationNanos);
        } else if (operation instanceof BaseRemoveOperation) {
            localMapStats.incrementRemoveLatencyNanos(durationNanos);
        } else if (operation instanceof GetOperation) {
            localMapStats.incrementGetLatencyNanos(durationNanos);
        }
    }

    @Override
    public RecordStore createRecordStore(MapContainer mapContainer, int partitionId, MapKeyLoader keyLoader) {
        assert (partitionId != -1) : "Cannot be called with GENERIC_PARTITION_ID";
        ILogger logger = this.nodeEngine.getLogger(DefaultRecordStore.class);
        return new DefaultRecordStore(mapContainer, partitionId, keyLoader, logger);
    }

    @Override
    public boolean removeMapContainer(MapContainer mapContainer) {
        return this.mapContainers.remove(mapContainer.getName(), mapContainer);
    }

    @Override
    public PartitioningStrategy getPartitioningStrategy(String mapName, PartitioningStrategyConfig config) {
        return this.partitioningStrategyFactory.getPartitioningStrategy(mapName, config);
    }

    @Override
    public void removePartitioningStrategyFromCache(String mapName) {
        this.partitioningStrategyFactory.removePartitioningStrategyFromCache(mapName);
    }

    @Override
    public PartitionContainer[] getPartitionContainers() {
        return this.partitionContainers;
    }

    @Override
    public void onClusterStateChange(ClusterState newState) {
        this.expirationManager.onClusterStateChange(newState);
    }

    @Override
    public PartitionScanRunner getPartitionScanRunner() {
        return this.partitionScanRunner;
    }

    @Override
    public ResultProcessorRegistry getResultProcessorRegistry() {
        return this.resultProcessorRegistry;
    }

    @Override
    public MapNearCacheManager getMapNearCacheManager() {
        return this.mapNearCacheManager;
    }

    @Override
    public String addListenerAdapter(ListenerAdapter listenerAdaptor, EventFilter eventFilter, String mapName) {
        EventRegistration registration = this.getNodeEngine().getEventService().registerListener("hz:impl:mapService", mapName, eventFilter, listenerAdaptor);
        return registration.getId();
    }

    @Override
    public String addLocalListenerAdapter(ListenerAdapter adapter, String mapName) {
        EventService eventService = this.getNodeEngine().getEventService();
        EventRegistration registration = eventService.registerLocalListener("hz:impl:mapService", mapName, adapter);
        return registration.getId();
    }

    @Override
    public QueryCacheContext getQueryCacheContext() {
        return this.queryCacheContext;
    }

    @Override
    public IndexCopyBehavior getIndexCopyBehavior() {
        return this.nodeEngine.getProperties().getEnum(GroupProperty.INDEX_COPY_BEHAVIOR, IndexCopyBehavior.class);
    }

    @Override
    public Collection<RecordStoreMutationObserver<Record>> createRecordStoreMutationObservers(String mapName, int partitionId) {
        LinkedList<RecordStoreMutationObserver<Record>> observers = new LinkedList<RecordStoreMutationObserver<Record>>();
        this.addEventJournalUpdaterObserver(observers, mapName, partitionId);
        this.addMetadataInitializerObserver(observers, mapName, partitionId);
        return observers;
    }

    protected void addMetadataInitializerObserver(Collection<RecordStoreMutationObserver<Record>> observers, String mapName, int partitionId) {
        MapContainer mapContainer = this.getMapContainer(mapName);
        MetadataPolicy policy = mapContainer.getMapConfig().getMetadataPolicy();
        if (policy == MetadataPolicy.CREATE_ON_UPDATE) {
            JsonMetadataRecordStoreMutationObserver observer = new JsonMetadataRecordStoreMutationObserver(this.serializationService, JsonMetadataInitializer.INSTANCE);
            observers.add(observer);
        }
    }

    private void addEventJournalUpdaterObserver(Collection<RecordStoreMutationObserver<Record>> observers, String mapName, int partitionId) {
        EventJournalWriterRecordStoreMutationObserver observer = new EventJournalWriterRecordStoreMutationObserver(this.getEventJournal(), this.getMapContainer(mapName), partitionId);
        observers.add(observer);
    }

    @Override
    public ValueComparator getValueComparatorOf(InMemoryFormat inMemoryFormat) {
        return ValueComparatorUtil.getValueComparatorOf(inMemoryFormat);
    }
}

