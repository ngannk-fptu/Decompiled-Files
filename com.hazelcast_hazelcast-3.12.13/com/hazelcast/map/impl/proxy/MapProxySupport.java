/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl.proxy;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.concurrent.lock.LockProxySupport;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EntryRemovingProcessor;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.operation.AddIndexOperation;
import com.hazelcast.map.impl.operation.AddInterceptorOperation;
import com.hazelcast.map.impl.operation.AwaitMapFlushOperation;
import com.hazelcast.map.impl.operation.IsEmptyOperationFactory;
import com.hazelcast.map.impl.operation.IsKeyLoadFinishedOperation;
import com.hazelcast.map.impl.operation.IsPartitionLoadedOperationFactory;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.RemoveInterceptorOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.BinaryOperationFactory;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.IterableUtil;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.MutableLong;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.TimeUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

abstract class MapProxySupport<K, V>
extends AbstractDistributedObject<MapService>
implements IMap<K, V>,
InitializingObject {
    protected static final String NULL_KEY_IS_NOT_ALLOWED = "Null key is not allowed!";
    protected static final String NULL_KEYS_ARE_NOT_ALLOWED = "Null keys collection is not allowed!";
    protected static final String NULL_VALUE_IS_NOT_ALLOWED = "Null value is not allowed!";
    protected static final String NULL_PREDICATE_IS_NOT_ALLOWED = "Predicate should not be null!";
    protected static final String NULL_LISTENER_IS_NOT_ALLOWED = "Null listener is not allowed!";
    protected static final String NULL_AGGREGATOR_IS_NOT_ALLOWED = "Aggregator should not be null!";
    protected static final String NULL_PROJECTION_IS_NOT_ALLOWED = "Projection should not be null!";
    private static final int INITIAL_WAIT_LOAD_SLEEP_MILLIS = 10;
    private static final int MAXIMAL_WAIT_LOAD_SLEEP_MILLIS = 1000;
    private static final HazelcastProperty MAP_PUT_ALL_BATCH_SIZE = new HazelcastProperty("hazelcast.map.put.all.batch.size", 0);
    private static final HazelcastProperty MAP_PUT_ALL_INITIAL_SIZE_FACTOR = new HazelcastProperty("hazelcast.map.put.all.initial.size.factor", 0);
    protected final String name;
    protected final LocalMapStatsImpl localMapStats;
    protected final LockProxySupport lockSupport;
    protected final PartitioningStrategy partitionStrategy;
    protected final MapServiceContext mapServiceContext;
    protected final IPartitionService partitionService;
    protected final Address thisAddress;
    protected final OperationService operationService;
    protected final SerializationService serializationService;
    protected final boolean statisticsEnabled;
    protected final MapConfig mapConfig;
    protected MapOperationProvider operationProvider;
    private final int putAllBatchSize;
    private final float putAllInitialSizeFactor;

    protected MapProxySupport(String name, MapService service, NodeEngine nodeEngine, MapConfig mapConfig) {
        super(nodeEngine, service);
        this.name = name;
        HazelcastProperties properties = nodeEngine.getProperties();
        this.mapServiceContext = service.getMapServiceContext();
        this.mapConfig = mapConfig;
        this.partitionStrategy = this.mapServiceContext.getPartitioningStrategy(mapConfig.getName(), mapConfig.getPartitioningStrategyConfig());
        this.localMapStats = this.mapServiceContext.getLocalMapStatsProvider().getLocalMapStatsImpl(name);
        this.partitionService = this.getNodeEngine().getPartitionService();
        this.lockSupport = new LockProxySupport(MapService.getObjectNamespace(name), LockServiceImpl.getMaxLeaseTimeInMillis(properties));
        this.operationProvider = this.mapServiceContext.getMapOperationProvider(mapConfig);
        this.operationService = nodeEngine.getOperationService();
        this.serializationService = nodeEngine.getSerializationService();
        this.thisAddress = nodeEngine.getClusterService().getThisAddress();
        this.statisticsEnabled = mapConfig.isStatisticsEnabled();
        this.putAllBatchSize = properties.getInteger(MAP_PUT_ALL_BATCH_SIZE);
        this.putAllInitialSizeFactor = properties.getFloat(MAP_PUT_ALL_INITIAL_SIZE_FACTOR);
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public void initialize() {
        this.initializeListeners();
        this.initializeIndexes();
        this.initializeMapStoreLoad();
    }

    private void initializeListeners() {
        MapListener listener;
        for (EntryListenerConfig entryListenerConfig : this.mapConfig.getEntryListenerConfigs()) {
            listener = (MapListener)this.initializeListener(entryListenerConfig);
            if (listener == null) continue;
            if (entryListenerConfig.isLocal()) {
                this.addLocalEntryListenerInternal(listener);
                continue;
            }
            this.addEntryListenerInternal(listener, null, entryListenerConfig.isIncludeValue());
        }
        for (MapPartitionLostListenerConfig mapPartitionLostListenerConfig : this.mapConfig.getPartitionLostListenerConfigs()) {
            listener = (MapPartitionLostListener)this.initializeListener(mapPartitionLostListenerConfig);
            if (listener == null) continue;
            this.addPartitionLostListenerInternal((MapPartitionLostListener)listener);
        }
    }

    private <T extends EventListener> T initializeListener(ListenerConfig listenerConfig) {
        T listener = this.getListenerImplOrNull(listenerConfig);
        if (listener instanceof HazelcastInstanceAware) {
            ((HazelcastInstanceAware)listener).setHazelcastInstance(this.getNodeEngine().getHazelcastInstance());
        }
        return listener;
    }

    private <T extends EventListener> T getListenerImplOrNull(ListenerConfig listenerConfig) {
        EventListener implementation = listenerConfig.getImplementation();
        if (implementation != null) {
            if (implementation instanceof EntryListenerConfig.MapListenerToEntryListenerAdapter) {
                return (T)((EntryListenerConfig.MapListenerToEntryListenerAdapter)implementation).getMapListener();
            }
            return (T)implementation;
        }
        String className = listenerConfig.getClassName();
        if (className != null) {
            try {
                ClassLoader configClassLoader = this.getNodeEngine().getConfigClassLoader();
                return (T)((EventListener)ClassLoaderUtil.newInstance(configClassLoader, className));
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        return null;
    }

    private void initializeIndexes() {
        for (MapIndexConfig index : this.mapConfig.getMapIndexConfigs()) {
            if (index.getAttribute() == null) continue;
            this.addIndex(index.getAttribute(), index.isOrdered());
        }
    }

    private void initializeMapStoreLoad() {
        MapStoreConfig.InitialLoadMode initialLoadMode;
        MapStoreConfig mapStoreConfig = this.mapConfig.getMapStoreConfig();
        if (mapStoreConfig != null && mapStoreConfig.isEnabled() && MapStoreConfig.InitialLoadMode.EAGER.equals((Object)(initialLoadMode = mapStoreConfig.getInitialLoadMode()))) {
            this.waitUntilLoaded();
        }
    }

    public PartitioningStrategy getPartitionStrategy() {
        return this.partitionStrategy;
    }

    public MapOperationProvider getOperationProvider() {
        return this.operationProvider;
    }

    public void setOperationProvider(MapOperationProvider operationProvider) {
        this.operationProvider = operationProvider;
    }

    public int getTotalBackupCount() {
        return this.mapConfig.getBackupCount() + this.mapConfig.getAsyncBackupCount();
    }

    protected QueryEngine getMapQueryEngine() {
        return this.mapServiceContext.getQueryEngine(this.name);
    }

    protected boolean isMapStoreEnabled() {
        MapStoreConfig mapStoreConfig = this.mapConfig.getMapStoreConfig();
        return mapStoreConfig != null && mapStoreConfig.isEnabled();
    }

    protected Object getInternal(Object key) {
        Data fromBackup;
        Data keyData = this.toDataWithStrategy(key);
        if (this.mapConfig.isReadBackupData() && (fromBackup = this.readBackupDataOrNull(keyData)) != null) {
            return fromBackup;
        }
        MapOperation operation = this.operationProvider.createGetOperation(this.name, keyData);
        operation.setThreadId(ThreadUtil.getThreadId());
        return this.invokeOperation(keyData, operation);
    }

    private Data readBackupDataOrNull(Data key) {
        int partitionId = this.partitionService.getPartitionId(key);
        IPartition partition = this.partitionService.getPartition(partitionId, false);
        if (!partition.isOwnerOrBackup(this.thisAddress)) {
            return null;
        }
        PartitionContainer partitionContainer = this.mapServiceContext.getPartitionContainer(partitionId);
        RecordStore recordStore = partitionContainer.getExistingRecordStore(this.name);
        if (recordStore == null) {
            return null;
        }
        return recordStore.readBackupData(key);
    }

    protected InternalCompletableFuture<Data> getAsyncInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation operation = this.operationProvider.createGetOperation(this.name, keyData);
        try {
            long startTimeNanos = System.nanoTime();
            InternalCompletableFuture<Data> future = this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setResultDeserialized(false).invoke();
            if (this.statisticsEnabled) {
                future.andThen(new IncrementStatsExecutionCallback(operation, startTimeNanos), ConcurrencyUtil.CALLER_RUNS);
            }
            return future;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected Data putInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        long timeInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(ttl, ttlUnit);
        long maxIdleInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit);
        MapOperation operation = this.operationProvider.createPutOperation(this.name, keyData, value, timeInMillis, maxIdleInMillis);
        return (Data)this.invokeOperation(keyData, operation);
    }

    protected boolean tryPutInternal(Object key, Data value, long timeout, TimeUnit timeunit) {
        Data keyData = this.toDataWithStrategy(key);
        long timeInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(timeout, timeunit);
        MapOperation operation = this.operationProvider.createTryPutOperation(this.name, keyData, value, timeInMillis);
        return (Boolean)this.invokeOperation(keyData, operation);
    }

    protected Data putIfAbsentInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        long timeInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(ttl, ttlUnit);
        long maxIdleInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit);
        MapOperation operation = this.operationProvider.createPutIfAbsentOperation(this.name, keyData, value, timeInMillis, maxIdleInMillis);
        return (Data)this.invokeOperation(keyData, operation);
    }

    protected void putTransientInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        long timeInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(ttl, ttlUnit);
        long maxIdleInMillis = TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit);
        MapOperation operation = this.operationProvider.createPutTransientOperation(this.name, keyData, value, timeInMillis, maxIdleInMillis);
        this.invokeOperation(keyData, operation);
    }

    private Object invokeOperation(Data key, MapOperation operation) {
        int partitionId = this.partitionService.getPartitionId(key);
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            Object result;
            if (this.statisticsEnabled) {
                long startTimeNanos = System.nanoTime();
                InternalCompletableFuture future = this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setResultDeserialized(false).invoke();
                result = future.get();
                this.mapServiceContext.incrementOperationStats(startTimeNanos, this.localMapStats, this.name, operation);
            } else {
                InternalCompletableFuture future = this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setResultDeserialized(false).invoke();
                result = future.get();
            }
            return result;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected InternalCompletableFuture<Data> putAsyncInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation operation = this.operationProvider.createPutOperation(this.name, keyData, value, TimeUtil.timeInMsOrOneIfResultIsZero(ttl, ttlUnit), TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit));
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            long startTimeNanos = System.nanoTime();
            InternalCompletableFuture<Data> future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            if (this.statisticsEnabled) {
                future.andThen(new IncrementStatsExecutionCallback(operation, startTimeNanos), ConcurrencyUtil.CALLER_RUNS);
            }
            return future;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected InternalCompletableFuture<Data> setAsyncInternal(Object key, Data value, long ttl, TimeUnit timeunit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation operation = this.operationProvider.createSetOperation(this.name, keyData, value, TimeUtil.timeInMsOrOneIfResultIsZero(ttl, timeunit), TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit));
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            return this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected boolean replaceInternal(Object key, Data expect, Data update) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createReplaceIfSameOperation(this.name, keyData, expect, update);
        return (Boolean)this.invokeOperation(keyData, operation);
    }

    protected Data replaceInternal(Object key, Data value) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createReplaceOperation(this.name, keyData, value);
        return (Data)this.invokeOperation(keyData, operation);
    }

    protected void setInternal(Object key, Data value, long ttl, TimeUnit timeunit, long maxIdle, TimeUnit maxIdleUnit) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createSetOperation(this.name, keyData, value, TimeUtil.timeInMsOrOneIfResultIsZero(ttl, timeunit), TimeUtil.timeInMsOrOneIfResultIsZero(maxIdle, maxIdleUnit));
        this.invokeOperation(keyData, operation);
    }

    protected boolean evictInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createEvictOperation(this.name, keyData, false);
        return (Boolean)this.invokeOperation(keyData, operation);
    }

    protected void evictAllInternal() {
        try {
            MapOperation operation = this.operationProvider.createEvictAllOperation(this.name);
            BinaryOperationFactory factory = new BinaryOperationFactory(operation, this.getNodeEngine());
            Map<Integer, Object> resultMap = this.operationService.invokeOnAllPartitions("hz:impl:mapService", factory);
            int evictedCount = 0;
            for (Object object : resultMap.values()) {
                evictedCount += ((Integer)object).intValue();
            }
            if (evictedCount > 0) {
                this.publishMapEvent(evictedCount, EntryEventType.EVICT_ALL);
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected void loadAllInternal(boolean replaceExistingValues) {
        int mapNamePartition = this.partitionService.getPartitionId(this.name);
        MapOperation operation = this.operationProvider.createLoadMapOperation(this.name, replaceExistingValues);
        InternalCompletableFuture loadMapFuture = this.operationService.invokeOnPartition("hz:impl:mapService", operation, mapNamePartition);
        try {
            loadMapFuture.get();
            this.waitUntilLoaded();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected void loadInternal(Set<K> keys, Iterable<Data> dataKeys, boolean replaceExistingValues) {
        if (dataKeys == null) {
            dataKeys = this.convertToData(keys);
        }
        Map<Integer, List<Data>> partitionIdToKeys = this.getPartitionIdToKeysMap(dataKeys);
        Set<Map.Entry<Integer, List<Data>>> entries = partitionIdToKeys.entrySet();
        for (Map.Entry entry : entries) {
            Integer partitionId = (Integer)entry.getKey();
            List correspondingKeys = (List)entry.getValue();
            Operation operation = this.createLoadAllOperation(correspondingKeys, replaceExistingValues);
            this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
        }
        this.waitUntilLoaded();
    }

    protected Iterable<Data> convertToData(Iterable<K> keys) {
        return IterableUtil.map(IterableUtil.nullToEmpty(keys), new KeyToData());
    }

    private Operation createLoadAllOperation(List<Data> keys, boolean replaceExistingValues) {
        return this.operationProvider.createLoadAllOperation(this.name, keys, replaceExistingValues);
    }

    protected Data removeInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createRemoveOperation(this.name, keyData, false);
        return (Data)this.invokeOperation(keyData, operation);
    }

    protected void deleteInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createDeleteOperation(this.name, keyData, false);
        this.invokeOperation(keyData, operation);
    }

    protected boolean removeInternal(Object key, Data value) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createRemoveIfSameOperation(this.name, keyData, value);
        return (Boolean)this.invokeOperation(keyData, operation);
    }

    protected boolean tryRemoveInternal(Object key, long timeout, TimeUnit timeunit) {
        Data keyData = this.toDataWithStrategy(key);
        MapOperation operation = this.operationProvider.createTryRemoveOperation(this.name, keyData, TimeUtil.timeInMsOrOneIfResultIsZero(timeout, timeunit));
        return (Boolean)this.invokeOperation(keyData, operation);
    }

    protected void removeAllInternal(Predicate predicate) {
        try {
            if (predicate instanceof PartitionPredicate) {
                PartitionPredicate partitionPredicate = (PartitionPredicate)predicate;
                OperationFactory operation = this.operationProvider.createPartitionWideEntryWithPredicateOperationFactory(this.name, EntryRemovingProcessor.ENTRY_REMOVING_PROCESSOR, partitionPredicate.getTarget());
                Data partitionKey = this.toDataWithStrategy(partitionPredicate.getPartitionKey());
                int partitionId = this.partitionService.getPartitionId(partitionKey);
                this.operationService.invokeOnPartitions("hz:impl:mapService", operation, Collections.singletonList(partitionId));
            } else {
                OperationFactory operation = this.operationProvider.createPartitionWideEntryWithPredicateOperationFactory(this.name, EntryRemovingProcessor.ENTRY_REMOVING_PROCESSOR, predicate);
                this.operationService.invokeOnAllPartitions("hz:impl:mapService", operation);
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected boolean setTtlInternal(Object key, long ttl, TimeUnit timeUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("Modifying TTL is available when cluster version is 3.11 or higher");
        }
        long ttlInMillis = timeUnit.toMillis(ttl);
        Object keyData = this.serializationService.toData(key);
        MapOperation operation = this.operationProvider.createSetTtlOperation(this.name, (Data)keyData, ttlInMillis);
        return (Boolean)this.invokeOperation((Data)keyData, operation);
    }

    protected InternalCompletableFuture<Data> removeAsyncInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation operation = this.operationProvider.createRemoveOperation(this.name, keyData, false);
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            long startTimeNanos = System.nanoTime();
            InternalCompletableFuture<Data> future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            if (this.statisticsEnabled) {
                future.andThen(new IncrementStatsExecutionCallback(operation, startTimeNanos), ConcurrencyUtil.CALLER_RUNS);
            }
            return future;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected boolean containsKeyInternal(Object key) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation containsKeyOperation = this.operationProvider.createContainsKeyOperation(this.name, keyData);
        containsKeyOperation.setThreadId(ThreadUtil.getThreadId());
        containsKeyOperation.setServiceName("hz:impl:mapService");
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", containsKeyOperation, partitionId);
            Object object = future.get();
            this.incrementOtherOperationsStat();
            return (Boolean)this.toObject(object);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public void waitUntilLoaded() {
        try {
            IsKeyLoadFinishedOperation op;
            InternalCompletableFuture loadingFuture;
            int mapNamesPartitionId = this.partitionService.getPartitionId(this.name);
            int sleepDurationMillis = 10;
            while (!((Boolean)(loadingFuture = this.operationService.invokeOnPartition("hz:impl:mapService", op = new IsKeyLoadFinishedOperation(this.name), mapNamesPartitionId)).get()).booleanValue()) {
                TimeUnit.MILLISECONDS.sleep(sleepDurationMillis);
                sleepDurationMillis = sleepDurationMillis * 2 < 1000 ? sleepDurationMillis * 2 : 1000;
            }
            IsPartitionLoadedOperationFactory opFactory = new IsPartitionLoadedOperationFactory(this.name);
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", opFactory);
            this.waitAllTrue(results, opFactory);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private void waitAllTrue(Map<Integer, Object> results, OperationFactory operationFactory) throws InterruptedException {
        Iterator<Map.Entry<Integer, Object>> iterator = results.entrySet().iterator();
        boolean isFinished = false;
        HashSet<Integer> retrySet = new HashSet<Integer>();
        while (!isFinished) {
            while (iterator.hasNext()) {
                Map.Entry<Integer, Object> entry = iterator.next();
                if (Boolean.TRUE.equals(entry.getValue())) {
                    iterator.remove();
                    continue;
                }
                retrySet.add(entry.getKey());
            }
            if (retrySet.size() > 0) {
                results = this.retryPartitions(retrySet, operationFactory);
                iterator = results.entrySet().iterator();
                TimeUnit.SECONDS.sleep(1L);
                retrySet.clear();
                continue;
            }
            isFinished = true;
        }
    }

    private Map<Integer, Object> retryPartitions(Collection<Integer> partitions, OperationFactory operationFactory) {
        try {
            return this.operationService.invokeOnPartitions("hz:impl:mapService", operationFactory, partitions);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public int size() {
        try {
            OperationFactory sizeOperationFactory = this.operationProvider.createMapSizeOperationFactory(this.name);
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", sizeOperationFactory);
            this.incrementOtherOperationsStat();
            long total = 0L;
            for (Object result : results.values()) {
                Integer size = (Integer)this.toObject(result);
                total += (long)size.intValue();
            }
            return MapUtil.toIntSize(total);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public boolean containsValueInternal(Data dataValue) {
        try {
            OperationFactory operationFactory = this.operationProvider.createContainsValueOperationFactory(this.name, dataValue);
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", operationFactory);
            this.incrementOtherOperationsStat();
            for (Object result : results.values()) {
                Boolean contains = (Boolean)this.toObject(result);
                if (!contains.booleanValue()) continue;
                return true;
            }
            return false;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            IsEmptyOperationFactory factory = new IsEmptyOperationFactory(this.name);
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", factory);
            this.incrementOtherOperationsStat();
            for (Object result : results.values()) {
                if (((Boolean)this.toObject(result)).booleanValue()) continue;
                return false;
            }
            return true;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected void incrementOtherOperationsStat() {
        if (this.statisticsEnabled) {
            this.localMapStats.incrementOtherOperations();
        }
    }

    protected void getAllInternal(Set<K> keys, List<Data> dataKeys, List<Object> resultingKeyValuePairs) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        if (dataKeys.isEmpty()) {
            this.toDataCollectionWithNonNullKeyValidation(keys, dataKeys);
        }
        Collection<Integer> partitions = this.getPartitionsForKeys(dataKeys);
        try {
            OperationFactory operationFactory = this.operationProvider.createGetAllOperationFactory(this.name, dataKeys);
            long startTimeNanos = System.nanoTime();
            Map responses = this.operationService.invokeOnPartitions("hz:impl:mapService", operationFactory, partitions);
            for (Object response : responses.values()) {
                MapEntries entries = (MapEntries)this.toObject(response);
                for (int i = 0; i < entries.size(); ++i) {
                    resultingKeyValuePairs.add(entries.getKey(i));
                    resultingKeyValuePairs.add(entries.getValue(i));
                }
            }
            this.localMapStats.incrementGetLatencyNanos(dataKeys.size(), System.nanoTime() - startTimeNanos);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private Collection<Integer> getPartitionsForKeys(Collection<Data> keys) {
        int partitions = this.partitionService.getPartitionCount();
        int capacity = Math.min(partitions, keys.size());
        Set<Integer> partitionIds = SetUtil.createHashSet(capacity);
        Iterator<Data> iterator = keys.iterator();
        while (iterator.hasNext() && partitionIds.size() < partitions) {
            Data key = iterator.next();
            partitionIds.add(this.partitionService.getPartitionId(key));
        }
        return partitionIds;
    }

    private Map<Integer, List<Data>> getPartitionIdToKeysMap(Iterable<Data> keys) {
        if (keys == null) {
            return Collections.emptyMap();
        }
        HashMap<Integer, List<Data>> idToKeys = new HashMap<Integer, List<Data>>();
        for (Data key : keys) {
            int partitionId = this.partitionService.getPartitionId(key);
            ArrayList<Data> keyList = (ArrayList<Data>)idToKeys.get(partitionId);
            if (keyList == null) {
                keyList = new ArrayList<Data>();
                idToKeys.put(partitionId, keyList);
            }
            keyList.add(key);
        }
        return idToKeys;
    }

    private boolean isPutAllUseBatching(int mapSize) {
        return this.putAllBatchSize > 0 && mapSize > this.putAllBatchSize * this.getNodeEngine().getClusterService().getSize();
    }

    private int getPutAllInitialSize(boolean useBatching, int mapSize, int partitionCount) {
        if (mapSize == 1) {
            return 1;
        }
        if (useBatching) {
            return this.putAllBatchSize;
        }
        if (this.putAllInitialSizeFactor < 1.0f) {
            return (int)Math.ceil((double)(20.0f * (float)mapSize / (float)partitionCount) / Math.log10(mapSize));
        }
        return (int)Math.ceil(this.putAllInitialSizeFactor * (float)mapSize / (float)partitionCount);
    }

    @SuppressFBWarnings(value={"DM_NUMBER_CTOR"}, justification="we need a shared counter object for each member per partition")
    protected void putAllInternal(Map<?, ?> map) {
        try {
            int mapSize = map.size();
            if (mapSize == 0) {
                return;
            }
            boolean useBatching = this.isPutAllUseBatching(mapSize);
            int partitionCount = this.partitionService.getPartitionCount();
            int initialSize = this.getPutAllInitialSize(useBatching, mapSize, partitionCount);
            Map<Address, List<Integer>> memberPartitionsMap = this.partitionService.getMemberPartitionsMap();
            MutableLong[] counterPerMember = null;
            Address[] addresses = null;
            if (useBatching) {
                counterPerMember = new MutableLong[partitionCount];
                addresses = new Address[partitionCount];
                for (Map.Entry<Address, List<Integer>> addressListEntry : memberPartitionsMap.entrySet()) {
                    MutableLong mutableLong = new MutableLong();
                    Address address = addressListEntry.getKey();
                    for (int partitionId : addressListEntry.getValue()) {
                        counterPerMember[partitionId] = mutableLong;
                        addresses[partitionId] = address;
                    }
                }
            }
            MapEntries[] entriesPerPartition = new MapEntries[partitionCount];
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                long currentSize;
                Preconditions.checkNotNull(entry.getKey(), NULL_KEY_IS_NOT_ALLOWED);
                Preconditions.checkNotNull(entry.getValue(), NULL_VALUE_IS_NOT_ALLOWED);
                Data keyData = this.toDataWithStrategy(entry.getKey());
                int partitionId = this.partitionService.getPartitionId(keyData);
                MapEntries entries = entriesPerPartition[partitionId];
                if (entries == null) {
                    entriesPerPartition[partitionId] = entries = new MapEntries(initialSize);
                }
                entries.add(keyData, this.toData(entry.getValue()));
                if (!useBatching || (currentSize = ++counterPerMember[partitionId].value) % (long)this.putAllBatchSize != 0L) continue;
                List<Integer> partitions = memberPartitionsMap.get(addresses[partitionId]);
                this.invokePutAllOperation(partitions, entriesPerPartition);
            }
            for (Map.Entry<Object, Object> entry : memberPartitionsMap.entrySet()) {
                this.invokePutAllOperation((List)entry.getValue(), entriesPerPartition);
            }
            this.finalizePutAll(map);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void invokePutAllOperation(List<Integer> memberPartitions, MapEntries[] entriesPerPartition) throws Exception {
        int size = memberPartitions.size();
        int[] partitions = new int[size];
        int index = 0;
        for (Integer partitionId : memberPartitions) {
            if (entriesPerPartition[partitionId] == null) continue;
            partitions[index++] = partitionId;
        }
        if (index == 0) {
            return;
        }
        if (index < size) {
            partitions = Arrays.copyOf(partitions, index);
            size = index;
        }
        index = 0;
        MapEntries[] entries = new MapEntries[size];
        long totalSize = 0L;
        for (int partitionId : partitions) {
            int batchSize = entriesPerPartition[partitionId].size();
            assert (this.putAllBatchSize == 0 || batchSize <= this.putAllBatchSize);
            entries[index++] = entriesPerPartition[partitionId];
            totalSize += (long)batchSize;
            entriesPerPartition[partitionId] = null;
        }
        if (totalSize == 0L) {
            return;
        }
        this.invokePutAllOperationFactory(totalSize, partitions, entries);
    }

    protected void invokePutAllOperationFactory(long size, int[] partitions, MapEntries[] entries) throws Exception {
        OperationFactory factory = this.operationProvider.createPutAllOperationFactory(this.name, partitions, entries);
        long startTimeNanos = System.nanoTime();
        this.operationService.invokeOnPartitions("hz:impl:mapService", factory, partitions);
        this.localMapStats.incrementPutLatencyNanos(size, System.nanoTime() - startTimeNanos);
    }

    protected void finalizePutAll(Map<?, ?> map) {
    }

    @Override
    public void flush() {
        try {
            MapOperation mapFlushOperation = this.operationProvider.createMapFlushOperation(this.name);
            BinaryOperationFactory operationFactory = new BinaryOperationFactory(mapFlushOperation, this.getNodeEngine());
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", operationFactory);
            ArrayList futures = new ArrayList();
            for (Map.Entry<Integer, Object> entry : results.entrySet()) {
                Integer partitionId = entry.getKey();
                Long count = (Long)entry.getValue();
                if (count == 0L) continue;
                AwaitMapFlushOperation operation = new AwaitMapFlushOperation(this.name, count);
                futures.add(this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId));
            }
            for (Future future : futures) {
                future.get();
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public void clearInternal() {
        try {
            MapOperation clearOperation = this.operationProvider.createClearOperation(this.name);
            clearOperation.setServiceName("hz:impl:mapService");
            BinaryOperationFactory factory = new BinaryOperationFactory(clearOperation, this.getNodeEngine());
            Map<Integer, Object> resultMap = this.operationService.invokeOnAllPartitions("hz:impl:mapService", factory);
            int clearedCount = 0;
            for (Object object : resultMap.values()) {
                clearedCount += ((Integer)object).intValue();
            }
            if (clearedCount > 0) {
                this.publishMapEvent(clearedCount, EntryEventType.CLEAR_ALL);
            }
            this.incrementOtherOperationsStat();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public String addMapInterceptorInternal(MapInterceptor interceptor) {
        NodeEngine nodeEngine = this.getNodeEngine();
        String id = this.mapServiceContext.generateInterceptorId(this.name, interceptor);
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        for (Member member : members) {
            try {
                AddInterceptorOperation op = new AddInterceptorOperation(id, interceptor, this.name);
                InternalCompletableFuture future = this.operationService.invokeOnTarget("hz:impl:mapService", op, member.getAddress());
                future.get();
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
        return id;
    }

    public void removeMapInterceptorInternal(String id) {
        NodeEngine nodeEngine = this.getNodeEngine();
        this.mapServiceContext.removeInterceptor(this.name, id);
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        for (Member member : members) {
            try {
                if (member.localMember()) continue;
                RemoveInterceptorOperation op = new RemoveInterceptorOperation(this.name, id);
                InternalCompletableFuture future = this.operationService.invokeOnTarget("hz:impl:mapService", op, member.getAddress());
                future.get();
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
    }

    public String addLocalEntryListenerInternal(Object listener) {
        return this.mapServiceContext.addLocalEventListener(listener, this.name);
    }

    public String addLocalEntryListenerInternal(Object listener, Predicate predicate, Data key, boolean includeValue) {
        QueryEventFilter eventFilter = new QueryEventFilter(includeValue, key, predicate);
        return this.mapServiceContext.addLocalEventListener(listener, eventFilter, this.name);
    }

    protected String addEntryListenerInternal(Object listener, Data key, boolean includeValue) {
        EntryEventFilter eventFilter = new EntryEventFilter(includeValue, key);
        return this.mapServiceContext.addEventListener(listener, eventFilter, this.name);
    }

    protected String addEntryListenerInternal(Object listener, Predicate predicate, Data key, boolean includeValue) {
        QueryEventFilter eventFilter = new QueryEventFilter(includeValue, key, predicate);
        return this.mapServiceContext.addEventListener(listener, eventFilter, this.name);
    }

    protected boolean removeEntryListenerInternal(String id) {
        return this.mapServiceContext.removeEventListener(this.name, id);
    }

    protected String addPartitionLostListenerInternal(MapPartitionLostListener listener) {
        return this.mapServiceContext.addPartitionLostListener(listener, this.name);
    }

    protected boolean removePartitionLostListenerInternal(String id) {
        return this.mapServiceContext.removePartitionLostListener(this.name, id);
    }

    protected EntryView getEntryViewInternal(Data key) {
        int partitionId = this.partitionService.getPartitionId(key);
        MapOperation operation = this.operationProvider.createGetEntryViewOperation(this.name, key);
        operation.setThreadId(ThreadUtil.getThreadId());
        operation.setServiceName("hz:impl:mapService");
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            return (EntryView)this.toObject(future.get());
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public Data executeOnKeyInternal(Object key, EntryProcessor entryProcessor) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(keyData);
        MapOperation operation = this.operationProvider.createEntryOperation(this.name, keyData, entryProcessor);
        operation.setThreadId(ThreadUtil.getThreadId());
        MapProxySupport.validateEntryProcessorForSingleKeyProcessing(entryProcessor);
        try {
            InternalCompletableFuture future = this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setResultDeserialized(false).invoke();
            return (Data)future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private static void validateEntryProcessorForSingleKeyProcessing(EntryProcessor entryProcessor) {
        EntryBackupProcessor backupProcessor;
        if (entryProcessor instanceof ReadOnly && (backupProcessor = entryProcessor.getBackupProcessor()) != null) {
            throw new IllegalArgumentException("EntryProcessor.getBackupProcessor() should be null for a ReadOnly EntryProcessor");
        }
    }

    public ICompletableFuture<Map<K, Object>> submitToKeysInternal(Set<K> keys, Set<Data> dataKeys, EntryProcessor entryProcessor) {
        if (dataKeys.isEmpty()) {
            this.toDataCollectionWithNonNullKeyValidation(keys, dataKeys);
        }
        Collection<Integer> partitionsForKeys = this.getPartitionsForKeys(dataKeys);
        OperationFactory operationFactory = this.operationProvider.createMultipleEntryOperationFactory(this.name, dataKeys, entryProcessor);
        final SimpleCompletableFuture<Map<K, Object>> resultFuture = new SimpleCompletableFuture<Map<K, Object>>(this.getNodeEngine());
        ExecutionCallback<Map<Integer, Object>> partialCallback = new ExecutionCallback<Map<Integer, Object>>(){

            @Override
            public void onResponse(Map<Integer, Object> response) {
                Map result = null;
                try {
                    result = MapUtil.createHashMap(response.size());
                    for (Object object : response.values()) {
                        MapEntries mapEntries = (MapEntries)object;
                        mapEntries.putAllToMap(MapProxySupport.this.serializationService, result);
                    }
                }
                catch (Throwable e) {
                    resultFuture.setResult(e);
                }
                resultFuture.setResult(result);
            }

            @Override
            public void onFailure(Throwable t) {
                resultFuture.setResult(t);
            }
        };
        this.operationService.invokeOnPartitionsAsync("hz:impl:mapService", operationFactory, partitionsForKeys).andThen(partialCallback);
        return resultFuture;
    }

    public InternalCompletableFuture<Object> executeOnKeyInternal(Object key, EntryProcessor entryProcessor, ExecutionCallback<Object> callback) {
        Data keyData = this.toDataWithStrategy(key);
        int partitionId = this.partitionService.getPartitionId(key);
        MapOperation operation = this.operationProvider.createEntryOperation(this.name, keyData, entryProcessor);
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            if (callback == null) {
                return this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            }
            return this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setExecutionCallback(new MapExecutionCallbackAdapter(callback)).invoke();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    public void executeOnEntriesInternal(EntryProcessor entryProcessor, Predicate predicate, List<Data> result) {
        try {
            Map<Integer, Object> results;
            if (predicate instanceof PartitionPredicate) {
                PartitionPredicate partitionPredicate = (PartitionPredicate)predicate;
                Data key = this.toData(partitionPredicate.getPartitionKey());
                int partitionId = this.partitionService.getPartitionId(key);
                this.handleHazelcastInstanceAwareParams(partitionPredicate.getTarget());
                OperationFactory operation = this.operationProvider.createPartitionWideEntryWithPredicateOperationFactory(this.name, entryProcessor, partitionPredicate.getTarget());
                results = this.operationService.invokeOnPartitions("hz:impl:mapService", operation, Collections.singletonList(partitionId));
            } else {
                OperationFactory operation = this.operationProvider.createPartitionWideEntryWithPredicateOperationFactory(this.name, entryProcessor, predicate);
                results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", operation);
            }
            for (Object object : results.values()) {
                if (object == null) continue;
                MapEntries mapEntries = (MapEntries)object;
                for (int i = 0; i < mapEntries.size(); ++i) {
                    result.add(mapEntries.getKey(i));
                    result.add(mapEntries.getValue(i));
                }
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected <T> T toObject(Object object) {
        return this.serializationService.toObject(object);
    }

    protected Data toDataWithStrategy(Object object) {
        return this.serializationService.toData(object, this.partitionStrategy);
    }

    protected Data toData(Object object, PartitioningStrategy partitioningStrategy) {
        return this.serializationService.toData(object, partitioningStrategy);
    }

    @Override
    public void addIndex(String attribute, boolean ordered) {
        MapIndexConfig.validateIndexAttribute(attribute);
        try {
            AddIndexOperation addIndexOperation = new AddIndexOperation(this.name, attribute, ordered);
            this.operationService.invokeOnAllPartitions("hz:impl:mapService", new BinaryOperationFactory(addIndexOperation, this.getNodeEngine()));
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public LocalMapStats getLocalMapStats() {
        if (!this.mapConfig.isStatisticsEnabled()) {
            return LocalMapStatsProvider.EMPTY_LOCAL_MAP_STATS;
        }
        return this.mapServiceContext.getLocalMapStatsProvider().createLocalMapStats(this.name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean preDestroy() {
        try {
            QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
            SubscriberContext subscriberContext = queryCacheContext.getSubscriberContext();
            QueryCacheEndToEndProvider provider = subscriberContext.getEndToEndQueryCacheProvider();
            provider.destroyAllQueryCaches(this.name);
        }
        finally {
            super.preDestroy();
        }
        return true;
    }

    protected void toDataCollectionWithNonNullKeyValidation(Set<K> keys, Collection<Data> dataKeys) {
        for (K key : keys) {
            Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
            dataKeys.add(this.toDataWithStrategy(key));
        }
    }

    private void publishMapEvent(int numberOfAffectedEntries, EntryEventType eventType) {
        MapEventPublisher mapEventPublisher = this.mapServiceContext.getMapEventPublisher();
        mapEventPublisher.publishMapEvent(this.thisAddress, this.name, eventType, numberOfAffectedEntries);
    }

    protected <T extends Result> T executeQueryInternal(Predicate predicate, IterationType iterationType, Target target) {
        return this.executeQueryInternal(predicate, null, null, iterationType, target);
    }

    protected <T extends Result> T executeQueryInternal(Predicate predicate, Aggregator aggregator, Projection projection, IterationType iterationType, Target target) {
        QueryEngine queryEngine = this.getMapQueryEngine();
        Predicate userPredicate = predicate;
        if (predicate instanceof PartitionPredicate) {
            PartitionPredicate partitionPredicate = (PartitionPredicate)predicate;
            Data key = this.toData(partitionPredicate.getPartitionKey());
            int partitionId = this.partitionService.getPartitionId(key);
            userPredicate = partitionPredicate.getTarget();
            target = Target.createPartitionTarget(partitionId);
        }
        this.handleHazelcastInstanceAwareParams(userPredicate);
        Query query = Query.of().mapName(this.getName()).predicate(userPredicate).iterationType(iterationType).aggregator(aggregator).projection(projection).build();
        return queryEngine.execute(query, target);
    }

    protected void handleHazelcastInstanceAwareParams(Object ... objects) {
        for (Object object : objects) {
            if (!(object instanceof HazelcastInstanceAware)) continue;
            ((HazelcastInstanceAware)object).setHazelcastInstance(this.getNodeEngine().getHazelcastInstance());
        }
    }

    @SerializableByConvention
    private class KeyToData
    implements IFunction<K, Data> {
        private KeyToData() {
        }

        @Override
        public Data apply(K key) {
            return MapProxySupport.this.toDataWithStrategy(key);
        }
    }

    private class MapExecutionCallbackAdapter
    implements ExecutionCallback<Object> {
        private final ExecutionCallback<Object> executionCallback;

        MapExecutionCallbackAdapter(ExecutionCallback<Object> executionCallback) {
            this.executionCallback = executionCallback;
        }

        @Override
        public void onResponse(Object response) {
            this.executionCallback.onResponse(MapProxySupport.this.toObject(response));
        }

        @Override
        public void onFailure(Throwable t) {
            this.executionCallback.onFailure(t);
        }
    }

    private class IncrementStatsExecutionCallback<T>
    implements ExecutionCallback<T> {
        private final MapOperation operation;
        private final long startTime;

        IncrementStatsExecutionCallback(MapOperation operation, long startTime) {
            this.operation = operation;
            this.startTime = startTime;
        }

        @Override
        public void onResponse(T response) {
            MapProxySupport.this.mapServiceContext.incrementOperationStats(this.startTime, MapProxySupport.this.localMapStats, MapProxySupport.this.name, this.operation);
        }

        @Override
        public void onFailure(Throwable t) {
        }
    }
}

