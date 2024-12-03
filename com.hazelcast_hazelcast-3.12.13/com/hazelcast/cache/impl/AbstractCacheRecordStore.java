/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.Duration
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CacheLoader
 *  javax.cache.integration.CacheLoaderException
 *  javax.cache.integration.CacheWriter
 *  javax.cache.integration.CacheWriterException
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.MutableEntry
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.StorageTypeAwareCacheMergePolicy;
import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEntry;
import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheEntryProcessorEntry;
import com.hazelcast.cache.impl.CacheEventContext;
import com.hazelcast.cache.impl.CacheEventContextUtil;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventDataImpl;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.LatencyTrackingCacheLoader;
import com.hazelcast.cache.impl.LatencyTrackingCacheWriter;
import com.hazelcast.cache.impl.maxsize.impl.EntryCountCacheEvictionChecker;
import com.hazelcast.cache.impl.merge.entry.LazyCacheEntryView;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.cache.impl.record.CacheRecordFactory;
import com.hazelcast.cache.impl.record.CacheRecordMap;
import com.hazelcast.cache.impl.record.SampleableCacheRecordMap;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.config.Config;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.internal.eviction.ClearExpiredRecordsTask;
import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.eviction.EvictionPolicyEvaluatorProvider;
import com.hazelcast.internal.eviction.ExpiredKey;
import com.hazelcast.internal.eviction.impl.evaluator.EvictionPolicyEvaluator;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SamplingEvictionStrategy;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.internal.util.comparators.ValueComparatorUtil;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.wan.impl.CallerProvenance;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.cache.configuration.Factory;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;

public abstract class AbstractCacheRecordStore<R extends CacheRecord, CRM extends SampleableCacheRecordMap<Data, R>>
implements ICacheRecordStore,
EvictionListener<Data, R> {
    public static final String SOURCE_NOT_AVAILABLE = "<NA>";
    protected static final int DEFAULT_INITIAL_CAPACITY = 256;
    protected final int partitionId;
    protected final int partitionCount;
    protected final boolean wanReplicationEnabled;
    protected final boolean persistWanReplicatedData;
    protected final boolean disablePerEntryInvalidationEvents;
    protected final String name;
    protected final NodeEngine nodeEngine;
    protected final CacheConfig cacheConfig;
    protected final SerializationService ss;
    protected final EvictionConfig evictionConfig;
    protected final ValueComparator valueComparator;
    protected final EvictionChecker evictionChecker;
    protected final ObjectNamespace objectNamespace;
    protected final AbstractCacheService cacheService;
    protected final CacheRecordFactory cacheRecordFactory;
    protected final EventJournalConfig eventJournalConfig;
    protected final ClearExpiredRecordsTask clearExpiredRecordsTask;
    protected final SamplingEvictionStrategy<Data, R, CRM> evictionStrategy;
    protected final EvictionPolicyEvaluator<Data, R> evictionPolicyEvaluator;
    protected final Map<CacheEventType, Set<CacheEventData>> batchEvent = new HashMap<CacheEventType, Set<CacheEventData>>();
    protected boolean primary;
    protected boolean eventsEnabled = true;
    protected boolean eventsBatchingEnabled;
    protected CRM records;
    protected CacheLoader cacheLoader;
    protected CacheWriter cacheWriter;
    protected CacheContext cacheContext;
    protected CacheStatisticsImpl statistics;
    protected ExpiryPolicy defaultExpiryPolicy;
    protected Iterator<Map.Entry<Data, R>> expirationIterator;
    protected InvalidationQueue<ExpiredKey> expiredKeys = new InvalidationQueue();
    protected boolean hasEntryWithExpiration;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractCacheRecordStore(String cacheNameWithPrefix, int partitionId, NodeEngine nodeEngine, AbstractCacheService cacheService) {
        this.name = cacheNameWithPrefix;
        this.partitionId = partitionId;
        this.nodeEngine = nodeEngine;
        this.ss = nodeEngine.getSerializationService();
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.cacheService = cacheService;
        this.cacheConfig = cacheService.getCacheConfig(cacheNameWithPrefix);
        if (this.cacheConfig == null) {
            throw new CacheNotExistsException("Cache " + cacheNameWithPrefix + " is already destroyed or not created yet, on " + nodeEngine.getLocalMember());
        }
        Closeable tenantContext = CacheConfigAccessor.getTenantControl(this.cacheConfig).setTenant(true);
        try {
            this.eventJournalConfig = nodeEngine.getConfig().findCacheEventJournalConfig(this.cacheConfig.getName());
            this.evictionConfig = this.cacheConfig.getEvictionConfig();
            if (this.evictionConfig == null) {
                throw new IllegalStateException("Eviction config cannot be null!");
            }
            this.wanReplicationEnabled = cacheService.isWanReplicationEnabled(cacheNameWithPrefix);
            this.disablePerEntryInvalidationEvents = this.cacheConfig.isDisablePerEntryInvalidationEvents();
            this.initializeStatisticsAndFactories(cacheNameWithPrefix);
            this.cacheContext = cacheService.getOrCreateCacheContext(cacheNameWithPrefix);
            this.records = this.createRecordCacheMap();
            this.evictionChecker = this.createCacheEvictionChecker(this.evictionConfig.getSize(), this.evictionConfig.getMaximumSizePolicy());
            this.evictionPolicyEvaluator = this.createEvictionPolicyEvaluator(this.evictionConfig);
            this.evictionStrategy = this.createEvictionStrategy(this.evictionConfig);
            this.objectNamespace = CacheService.getObjectNamespace(cacheNameWithPrefix);
            this.persistWanReplicatedData = this.canPersistWanReplicatedData(this.cacheConfig, nodeEngine);
            this.cacheRecordFactory = new CacheRecordFactory(this.cacheConfig.getInMemoryFormat(), this.ss);
            this.valueComparator = this.getValueComparatorOf(this.cacheConfig.getInMemoryFormat());
            this.clearExpiredRecordsTask = cacheService.getExpirationManager().getTask();
            this.injectDependencies(this.evictionPolicyEvaluator.getEvictionPolicyComparator());
            this.registerResourceIfItIsClosable(this.cacheWriter);
            this.registerResourceIfItIsClosable(this.cacheLoader);
            this.registerResourceIfItIsClosable(this.defaultExpiryPolicy);
            this.init();
        }
        finally {
            try {
                tenantContext.close();
            }
            catch (IOException ex) {
                ExceptionUtil.rethrow(ex);
            }
        }
    }

    private void initializeStatisticsAndFactories(String cacheNameWithPrefix) {
        if (this.cacheConfig.isStatisticsEnabled()) {
            this.statistics = this.cacheService.createCacheStatIfAbsent(cacheNameWithPrefix);
        }
        if (this.cacheConfig.getCacheLoaderFactory() != null) {
            Factory cacheLoaderFactory = this.cacheConfig.getCacheLoaderFactory();
            this.injectDependencies(cacheLoaderFactory);
            this.cacheLoader = (CacheLoader)cacheLoaderFactory.create();
            this.injectDependencies(this.cacheLoader);
        }
        if (this.cacheConfig.getCacheWriterFactory() != null) {
            Factory cacheWriterFactory = this.cacheConfig.getCacheWriterFactory();
            this.injectDependencies(cacheWriterFactory);
            this.cacheWriter = (CacheWriter)cacheWriterFactory.create();
            this.injectDependencies(this.cacheWriter);
        }
        if (this.cacheConfig.getExpiryPolicyFactory() == null) {
            throw new IllegalStateException("Expiry policy factory cannot be null!");
        }
        Factory<ExpiryPolicy> expiryPolicyFactory = this.cacheConfig.getExpiryPolicyFactory();
        this.injectDependencies(expiryPolicyFactory);
        this.defaultExpiryPolicy = (ExpiryPolicy)expiryPolicyFactory.create();
        this.injectDependencies(this.defaultExpiryPolicy);
    }

    protected ValueComparator getValueComparatorOf(InMemoryFormat inMemoryFormat) {
        return ValueComparatorUtil.getValueComparatorOf(inMemoryFormat);
    }

    private boolean canPersistWanReplicatedData(CacheConfig cacheConfig, NodeEngine nodeEngine) {
        boolean persistWanReplicatedData = false;
        WanReplicationRef wanReplicationRef = cacheConfig.getWanReplicationRef();
        if (wanReplicationRef != null) {
            WanConsumerConfig wanConsumerConfig;
            String wanReplicationRefName = wanReplicationRef.getName();
            Config config = nodeEngine.getConfig();
            WanReplicationConfig wanReplicationConfig = config.getWanReplicationConfig(wanReplicationRefName);
            if (wanReplicationConfig != null && (wanConsumerConfig = wanReplicationConfig.getWanConsumerConfig()) != null) {
                persistWanReplicatedData = wanConsumerConfig.isPersistWanReplicatedData();
            }
        }
        return persistWanReplicatedData;
    }

    private boolean persistenceEnabledFor(@Nonnull CallerProvenance provenance) {
        switch (provenance) {
            case WAN: {
                return this.persistWanReplicatedData;
            }
            case NOT_WAN: {
                return true;
            }
        }
        throw new IllegalArgumentException("Unexpected provenance: `" + (Object)((Object)provenance) + "`");
    }

    public void instrument(NodeEngine nodeEngine) {
        StoreLatencyPlugin plugin = ((NodeEngineImpl)nodeEngine).getDiagnostics().getPlugin(StoreLatencyPlugin.class);
        if (plugin == null) {
            return;
        }
        if (this.cacheLoader != null) {
            this.cacheLoader = new LatencyTrackingCacheLoader(this.cacheLoader, plugin, this.cacheConfig.getName());
        }
        if (this.cacheWriter != null) {
            this.cacheWriter = new LatencyTrackingCacheWriter(this.cacheWriter, plugin, this.cacheConfig.getName());
        }
    }

    private boolean isPrimary() {
        Address owner = this.nodeEngine.getPartitionService().getPartition(this.partitionId, false).getOwnerOrNull();
        Address thisAddress = this.nodeEngine.getThisAddress();
        return owner != null && owner.equals(thisAddress);
    }

    private void injectDependencies(Object obj) {
        ManagedContext managedContext = this.ss.getManagedContext();
        managedContext.initialize(obj);
    }

    private void registerResourceIfItIsClosable(Object resource) {
        if (resource instanceof Closeable) {
            this.cacheService.addCacheResource(this.name, (Closeable)resource);
        }
    }

    @Override
    public void init() {
        this.primary = this.isPrimary();
        this.records.setEntryCounting(this.primary);
        this.markExpirable(-1L);
    }

    protected boolean isReadThrough() {
        return this.cacheConfig.isReadThrough();
    }

    protected boolean isWriteThrough() {
        return this.cacheConfig.isWriteThrough();
    }

    protected boolean isStatisticsEnabled() {
        return this.statistics != null;
    }

    protected abstract CRM createRecordCacheMap();

    protected abstract CacheEntryProcessorEntry createCacheEntryProcessorEntry(Data var1, R var2, long var3, int var5);

    protected abstract R createRecord(Object var1, long var2, long var4);

    protected abstract Data valueToData(Object var1);

    protected abstract Object dataToValue(Data var1);

    protected abstract Object recordToValue(R var1);

    protected abstract Data recordToData(R var1);

    protected abstract Data toHeapData(Object var1);

    protected EvictionChecker createCacheEvictionChecker(int size, EvictionConfig.MaxSizePolicy maxSizePolicy) {
        if (maxSizePolicy == null) {
            throw new IllegalArgumentException("Max-Size policy cannot be null");
        }
        if (maxSizePolicy == EvictionConfig.MaxSizePolicy.ENTRY_COUNT) {
            return new EntryCountCacheEvictionChecker(size, (CacheRecordMap)this.records, this.partitionCount);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected EvictionPolicyEvaluator<Data, R> createEvictionPolicyEvaluator(EvictionConfig evictionConfig) {
        ConfigValidator.checkEvictionConfig(evictionConfig, false);
        Closeable tenantContext = CacheConfigAccessor.getTenantControl(this.cacheConfig).setTenant(false);
        try {
            EvictionPolicyEvaluator evictionPolicyEvaluator = EvictionPolicyEvaluatorProvider.getEvictionPolicyEvaluator(evictionConfig, this.nodeEngine.getConfigClassLoader());
            return evictionPolicyEvaluator;
        }
        finally {
            IOUtil.closeResource(tenantContext);
        }
    }

    protected SamplingEvictionStrategy<Data, R, CRM> createEvictionStrategy(EvictionConfig cacheEvictionConfig) {
        return SamplingEvictionStrategy.INSTANCE;
    }

    protected boolean isEvictionEnabled() {
        return this.evictionStrategy != null && this.evictionPolicyEvaluator != null;
    }

    protected boolean isEventsEnabled() {
        return this.eventsEnabled && (this.cacheContext.getCacheEntryListenerCount() > 0 || this.wanReplicationEnabled);
    }

    protected boolean isInvalidationEnabled() {
        return this.primary && this.cacheContext.getInvalidationListenerCount() > 0;
    }

    @Override
    public boolean evictIfRequired() {
        if (!this.isEvictionEnabled()) {
            return false;
        }
        boolean evicted = this.evictionStrategy.evict(this.records, this.evictionPolicyEvaluator, this.evictionChecker, this);
        if (this.isStatisticsEnabled() && evicted && this.primary) {
            this.statistics.increaseCacheEvictions(1L);
        }
        return evicted;
    }

    @Override
    public void sampleAndForceRemoveEntries(int entryCountToRemove) {
        Data dataKey;
        ThreadUtil.assertRunningOnPartitionThread();
        LinkedList keysToRemove = new LinkedList();
        Iterable entries = this.records.sample(entryCountToRemove);
        for (EvictionCandidate entry : entries) {
            keysToRemove.add(entry.getAccessor());
        }
        while ((dataKey = (Data)keysToRemove.poll()) != null) {
            this.forceRemoveRecord(dataKey);
        }
    }

    protected void forceRemoveRecord(Data key) {
        this.removeRecord(key);
    }

    protected Data toData(Object obj) {
        if (obj instanceof Data) {
            return (Data)obj;
        }
        if (obj instanceof CacheRecord) {
            return this.recordToData((CacheRecord)obj);
        }
        return this.valueToData(obj);
    }

    protected Object toValue(Object obj) {
        if (obj instanceof Data) {
            return this.dataToValue((Data)obj);
        }
        if (obj instanceof CacheRecord) {
            return this.recordToValue((CacheRecord)obj);
        }
        return obj;
    }

    protected Object toStorageValue(Object obj) {
        if (obj instanceof Data) {
            if (this.cacheConfig.getInMemoryFormat() == InMemoryFormat.OBJECT) {
                return this.dataToValue((Data)obj);
            }
            return obj;
        }
        if (obj instanceof CacheRecord) {
            return this.recordToValue((CacheRecord)obj);
        }
        return obj;
    }

    public Data toEventData(Object obj) {
        return this.isEventsEnabled() ? this.toHeapData(obj) : null;
    }

    private long getAdjustedExpireTime(Duration duration, long now) {
        return duration.getAdjustedTime(now);
    }

    protected ExpiryPolicy getExpiryPolicy(CacheRecord record, ExpiryPolicy expiryPolicy) {
        if (expiryPolicy != null) {
            return expiryPolicy;
        }
        if (record != null && record.getExpiryPolicy() != null) {
            return (ExpiryPolicy)this.toValue(record.getExpiryPolicy());
        }
        return this.defaultExpiryPolicy;
    }

    protected boolean evictIfExpired(Data key, R record, long now) {
        return this.processExpiredEntry(key, record, now);
    }

    protected boolean processExpiredEntry(Data key, R record, long now) {
        return this.processExpiredEntry(key, record, now, SOURCE_NOT_AVAILABLE);
    }

    protected boolean processExpiredEntry(Data key, R record, long now, String source) {
        return this.processExpiredEntry(key, record, now, source, null);
    }

    protected boolean processExpiredEntry(Data key, R record, long now, String source, String origin) {
        boolean isExpired;
        boolean bl = isExpired = record != null && record.isExpiredAt(now);
        if (!isExpired) {
            return false;
        }
        if (this.isStatisticsEnabled()) {
            this.statistics.increaseCacheExpiries(1L);
        }
        R removedRecord = this.doRemoveRecord(key, source);
        Data keyEventData = this.toEventData(key);
        Data recordEventData = this.toEventData(removedRecord);
        if (removedRecord != null) {
            this.onProcessExpiredEntry(key, removedRecord, removedRecord.getExpirationTime(), now, source, origin);
            if (this.isEventsEnabled()) {
                this.publishEvent(CacheEventContextUtil.createCacheExpiredEvent(keyEventData, recordEventData, -1L, origin, -1));
            }
        }
        return true;
    }

    protected R processExpiredEntry(Data key, R record, long expiryTime, long now, String source) {
        return this.processExpiredEntry(key, record, expiryTime, now, source, null);
    }

    protected R processExpiredEntry(Data key, R record, long expiryTime, long now, String source, String origin) {
        if (!CacheRecordFactory.isExpiredAt(expiryTime, now)) {
            return record;
        }
        if (this.isStatisticsEnabled()) {
            this.statistics.increaseCacheExpiries(1L);
        }
        R removedRecord = this.doRemoveRecord(key, source);
        Data keyEventData = this.toEventData(key);
        Data recordEventData = this.toEventData(removedRecord);
        this.onProcessExpiredEntry(key, removedRecord, expiryTime, now, source, origin);
        if (this.isEventsEnabled()) {
            this.publishEvent(CacheEventContextUtil.createCacheExpiredEvent(keyEventData, recordEventData, -1L, origin, -1));
        }
        return null;
    }

    protected void onProcessExpiredEntry(Data key, R record, long expiryTime, long now, String source, String origin) {
        this.accumulateOrSendExpiredKeysToBackup(key, record);
    }

    protected void accumulateOrSendExpiredKeysToBackup(Data key, R record) {
        if (this.cacheConfig.getTotalBackupCount() == 0) {
            return;
        }
        if (key != null && record != null) {
            this.expiredKeys.offer(new ExpiredKey(this.toHeapData(key), record.getCreationTime()));
        }
        this.clearExpiredRecordsTask.tryToSendBackupExpiryOp(this, true);
    }

    @Override
    public boolean isExpirable() {
        return this.hasEntryWithExpiration || this.getConfig().getExpiryPolicyFactory() != null;
    }

    public R accessRecord(Data key, R record, ExpiryPolicy expiryPolicy, long now) {
        this.onRecordAccess(key, record, this.getExpiryPolicy((CacheRecord)record, expiryPolicy), now);
        return record;
    }

    @Override
    public void onEvict(Data key, R record, boolean wasExpired) {
        if (wasExpired) {
            this.cacheService.eventJournal.writeExpiredEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, record.getValue());
        } else {
            this.cacheService.eventJournal.writeEvictEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, record.getValue());
        }
        this.invalidateEntry(key);
    }

    protected void invalidateEntry(Data key, String source) {
        if (this.isInvalidationEnabled()) {
            if (key == null) {
                this.cacheService.sendInvalidationEvent(this.name, null, source);
            } else if (!this.disablePerEntryInvalidationEvents) {
                this.cacheService.sendInvalidationEvent(this.name, this.toHeapData(key), source);
            }
        }
    }

    protected void invalidateEntry(Data key) {
        this.invalidateEntry(key, SOURCE_NOT_AVAILABLE);
    }

    protected void updateGetAndPutStat(boolean isPutSucceed, boolean getValue, boolean oldValueNull, long start) {
        if (this.isStatisticsEnabled()) {
            if (isPutSucceed) {
                this.statistics.increaseCachePuts(1L);
                this.statistics.addPutTimeNanos(System.nanoTime() - start);
            }
            if (getValue) {
                if (oldValueNull) {
                    this.statistics.increaseCacheMisses(1L);
                } else {
                    this.statistics.increaseCacheHits(1L);
                }
                this.statistics.addGetTimeNanos(System.nanoTime() - start);
            }
        }
    }

    protected long updateAccessDuration(Data key, R record, ExpiryPolicy expiryPolicy, long now) {
        long expiryTime = -1L;
        try {
            Duration expiryDuration = expiryPolicy.getExpiryForAccess();
            if (expiryDuration != null) {
                expiryTime = this.getAdjustedExpireTime(expiryDuration, now);
                record.setExpirationTime(expiryTime);
                if (this.isEventsEnabled()) {
                    CacheEventContext cacheEventContext = CacheEventContextUtil.createBaseEventContext(CacheEventType.EXPIRATION_TIME_UPDATED, this.toEventData(key), this.toEventData(record.getValue()), expiryTime, null, -1);
                    cacheEventContext.setAccessHit(record.getAccessHit());
                    this.publishEvent(cacheEventContext);
                }
            }
        }
        catch (Exception e) {
            EmptyStatement.ignore(e);
        }
        return expiryTime;
    }

    protected long onRecordAccess(Data key, R record, ExpiryPolicy expiryPolicy, long now) {
        record.setAccessTime(now);
        record.incrementAccessHit();
        return this.updateAccessDuration(key, record, expiryPolicy, now);
    }

    protected void updateReplaceStat(boolean result, boolean isHit, long start) {
        if (this.isStatisticsEnabled()) {
            if (result) {
                this.statistics.increaseCachePuts(1L);
                this.statistics.addPutTimeNanos(System.nanoTime() - start);
            }
            if (isHit) {
                this.statistics.increaseCacheHits(1L);
            } else {
                this.statistics.increaseCacheMisses(1L);
            }
        }
    }

    protected void publishEvent(CacheEventContext cacheEventContext) {
        if (this.isEventsEnabled()) {
            cacheEventContext.setCacheName(this.name);
            if (this.eventsBatchingEnabled) {
                CacheEventDataImpl cacheEventData = new CacheEventDataImpl(this.name, cacheEventContext.getEventType(), cacheEventContext.getDataKey(), cacheEventContext.getDataValue(), cacheEventContext.getDataOldValue(), cacheEventContext.isOldValueAvailable());
                Set<CacheEventData> cacheEventDataSet = this.batchEvent.remove((Object)cacheEventContext.getEventType());
                if (cacheEventDataSet == null) {
                    cacheEventDataSet = new HashSet<CacheEventData>();
                    this.batchEvent.put(cacheEventContext.getEventType(), cacheEventDataSet);
                }
                cacheEventDataSet.add(cacheEventData);
            } else {
                this.cacheService.publishEvent(cacheEventContext);
            }
        }
    }

    protected void publishBatchedEvents(String cacheName, CacheEventType cacheEventType, int orderKey) {
        Set<CacheEventData> cacheEventDatas;
        if (this.isEventsEnabled() && (cacheEventDatas = this.batchEvent.remove((Object)cacheEventType)) != null) {
            this.cacheService.publishEvent(cacheName, new CacheEventSet(cacheEventType, cacheEventDatas), orderKey);
        }
    }

    protected boolean compare(Object v1, Object v2) {
        if (v1 == null && v2 == null) {
            return true;
        }
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.equals(v2);
    }

    protected R createRecord(long expiryTime) {
        return this.createRecord(null, Clock.currentTimeMillis(), expiryTime);
    }

    protected R createRecord(Object value, long expiryTime) {
        return this.createRecord(value, Clock.currentTimeMillis(), expiryTime);
    }

    protected R createRecord(Data keyData, Object value, long expirationTime, int completionId) {
        R record = this.createRecord(value, expirationTime);
        if (this.isEventsEnabled()) {
            this.publishEvent(CacheEventContextUtil.createCacheCreatedEvent(this.toEventData(keyData), this.toEventData(value), expirationTime, null, completionId));
        }
        return record;
    }

    protected void onCreateRecordError(Data key, Object value, long expiryTime, long now, boolean disableWriteThrough, int completionId, String origin, R record, Throwable error) {
    }

    protected R createRecord(Data key, Object value, long expiryTime, long now, boolean disableWriteThrough, int completionId, String origin) {
        R record = this.createRecord(value, now, expiryTime);
        try {
            this.doPutRecord(key, record, origin, true);
        }
        catch (Throwable error) {
            this.onCreateRecordError(key, value, expiryTime, now, disableWriteThrough, completionId, origin, record, error);
            throw ExceptionUtil.rethrow(error);
        }
        try {
            if (!disableWriteThrough) {
                this.writeThroughCache(key, value);
            }
        }
        catch (Throwable error) {
            CacheRecord removed = (CacheRecord)this.records.remove(key);
            if (removed != null) {
                this.cacheService.eventJournal.writeRemoveEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, removed.getValue());
            }
            this.onCreateRecordError(key, value, expiryTime, now, disableWriteThrough, completionId, origin, record, error);
            throw ExceptionUtil.rethrow(error);
        }
        if (this.isEventsEnabled()) {
            this.publishEvent(CacheEventContextUtil.createCacheCreatedEvent(this.toEventData(key), this.toEventData(value), expiryTime, origin, completionId));
        }
        return record;
    }

    protected R createRecordWithExpiry(Data key, Object value, long expiryTime, long now, boolean disableWriteThrough, int completionId, String origin) {
        if (!CacheRecordFactory.isExpiredAt(expiryTime, now)) {
            return this.createRecord(key, value, expiryTime, now, disableWriteThrough, completionId, origin);
        }
        if (this.isEventsEnabled()) {
            this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), -1L, origin, completionId));
        }
        return null;
    }

    protected R createRecordWithExpiry(Data key, Object value, long expiryTime, long now, boolean disableWriteThrough, int completionId) {
        return this.createRecordWithExpiry(key, value, expiryTime, now, disableWriteThrough, completionId, SOURCE_NOT_AVAILABLE);
    }

    protected R createRecordWithExpiry(Data key, Object value, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId) {
        return this.createRecordWithExpiry(key, value, expiryPolicy, now, disableWriteThrough, completionId, SOURCE_NOT_AVAILABLE);
    }

    protected R createRecordWithExpiry(Data key, Object value, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, String origin) {
        Duration expiryDuration;
        expiryPolicy = this.getExpiryPolicy(null, expiryPolicy);
        try {
            expiryDuration = expiryPolicy.getExpiryForCreation();
        }
        catch (Exception e) {
            expiryDuration = Duration.ETERNAL;
        }
        long expiryTime = this.getAdjustedExpireTime(expiryDuration, now);
        return this.createRecordWithExpiry(key, value, expiryTime, now, disableWriteThrough, completionId, origin);
    }

    protected void onUpdateRecord(Data key, R record, Object value, Data oldDataValue) {
        this.cacheService.eventJournal.writeUpdateEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, oldDataValue, value);
    }

    protected void onUpdateRecordError(Data key, R record, Object value, Data newDataValue, Data oldDataValue, Throwable error) {
    }

    protected void onUpdateExpiryPolicy(Data key, R record, Data oldDataExpiryPolicy) {
    }

    protected void onUpdateExpiryPolicyError(Data key, R record, Data oldDataExpiryPolicy) {
    }

    protected void updateRecord(Data key, CacheRecord record, long expiryTime, long now, String origin) {
        record.setExpirationTime(expiryTime);
        this.invalidateEntry(key, origin);
    }

    protected void updateRecord(Data key, R record, Object value, long expiryTime, long now, boolean disableWriteThrough, int completionId, String source, String origin) {
        Data dataOldValue = null;
        Data dataValue = null;
        Object recordValue = value;
        try {
            this.updateExpiryTime(record, expiryTime);
            if (CacheRecordFactory.isExpiredAt(expiryTime, now)) {
                if (!disableWriteThrough) {
                    this.writeThroughCache(key, value);
                }
            } else {
                switch (this.cacheConfig.getInMemoryFormat()) {
                    case BINARY: {
                        recordValue = this.toData(value);
                        dataValue = (Data)recordValue;
                        dataOldValue = this.toData(record);
                        break;
                    }
                    case OBJECT: {
                        if (value instanceof Data) {
                            recordValue = this.dataToValue((Data)value);
                            dataValue = (Data)value;
                        } else {
                            dataValue = this.valueToData(value);
                        }
                        dataOldValue = this.toData(record);
                        break;
                    }
                    case NATIVE: {
                        recordValue = this.toData(value);
                        dataValue = (Data)recordValue;
                        dataOldValue = this.toData(record);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)this.cacheConfig.getInMemoryFormat()));
                    }
                }
                if (!disableWriteThrough) {
                    this.writeThroughCache(key, value);
                }
                Data eventDataKey = this.toEventData(key);
                Data eventDataValue = this.toEventData(dataValue);
                Data eventDataOldValue = this.toEventData(dataOldValue);
                Data eventDataExpiryPolicy = this.toEventData(record.getExpiryPolicy());
                this.updateRecordValue(record, recordValue);
                this.onUpdateRecord(key, record, value, dataOldValue);
                this.invalidateEntry(key, source);
                if (this.isEventsEnabled()) {
                    this.publishEvent(CacheEventContextUtil.createCacheUpdatedEvent(eventDataKey, eventDataValue, eventDataOldValue, record.getCreationTime(), record.getExpirationTime(), record.getLastAccessTime(), record.getAccessHit(), origin, completionId, eventDataExpiryPolicy));
                }
            }
        }
        catch (Throwable error) {
            this.onUpdateRecordError(key, record, value, dataValue, dataOldValue, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    private void updateExpiryTime(R record, long expiryTime) {
        if (expiryTime == -1L) {
            return;
        }
        this.markExpirable(expiryTime);
        record.setExpirationTime(expiryTime);
    }

    protected void updateExpiryPolicyOfRecord(Data key, R record, Object expiryPolicy) {
        Object inMemoryExpiryPolicy;
        Data dataOldExpiryPolicy = null;
        switch (this.cacheConfig.getInMemoryFormat()) {
            case OBJECT: {
                inMemoryExpiryPolicy = this.toValue(expiryPolicy);
                dataOldExpiryPolicy = this.toData(this.getExpiryPolicyOrNull(record));
                break;
            }
            case BINARY: 
            case NATIVE: {
                inMemoryExpiryPolicy = this.toData(expiryPolicy);
                dataOldExpiryPolicy = this.toData(this.getExpiryPolicyOrNull(record));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)this.cacheConfig.getInMemoryFormat()));
            }
        }
        try {
            this.onUpdateExpiryPolicy(key, record, dataOldExpiryPolicy);
            record.setExpiryPolicy((Object)inMemoryExpiryPolicy);
        }
        catch (Throwable error) {
            this.onUpdateExpiryPolicyError(key, record, dataOldExpiryPolicy);
            throw ExceptionUtil.rethrow(error);
        }
    }

    protected Object extractExpiryPolicyOfRecord(CacheRecord record) {
        Object policyData = record.getExpiryPolicy();
        if (policyData == null) {
            return null;
        }
        switch (this.cacheConfig.getInMemoryFormat()) {
            case BINARY: 
            case NATIVE: {
                return policyData;
            }
            case OBJECT: {
                return this.toValue(policyData);
            }
        }
        throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)this.cacheConfig.getInMemoryFormat()));
    }

    protected void updateRecordValue(R record, Object recordValue) {
        record.setValue((Object)recordValue);
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, long expiryTime, long now, boolean disableWriteThrough, int completionId, String source, String origin) {
        this.updateRecord(key, record, value, expiryTime, now, disableWriteThrough, completionId, source, origin);
        return this.processExpiredEntry(key, record, expiryTime, now, source) != null;
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, long expiryTime, long now, boolean disableWriteThrough, int completionId) {
        return this.updateRecordWithExpiry(key, value, record, expiryTime, now, disableWriteThrough, completionId, SOURCE_NOT_AVAILABLE);
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, long expiryTime, long now, boolean disableWriteThrough, int completionId, String source) {
        return this.updateRecordWithExpiry(key, value, record, expiryTime, now, disableWriteThrough, completionId, source, null);
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId) {
        return this.updateRecordWithExpiry(key, value, record, expiryPolicy, now, disableWriteThrough, completionId, SOURCE_NOT_AVAILABLE);
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, String source) {
        return this.updateRecordWithExpiry(key, value, record, expiryPolicy, now, disableWriteThrough, completionId, source, null);
    }

    protected boolean updateRecordWithExpiry(Data key, Object value, R record, ExpiryPolicy expiryPolicy, long now, boolean disableWriteThrough, int completionId, String source, String origin) {
        expiryPolicy = this.getExpiryPolicy((CacheRecord)record, expiryPolicy);
        long expiryTime = -1L;
        try {
            Duration expiryDuration = expiryPolicy.getExpiryForUpdate();
            if (expiryDuration != null) {
                expiryTime = this.getAdjustedExpireTime(expiryDuration, now);
            }
        }
        catch (Exception e) {
            EmptyStatement.ignore(e);
        }
        return this.updateRecordWithExpiry(key, value, record, expiryTime, now, disableWriteThrough, completionId, source, origin);
    }

    protected void updateRecordWithExpiry(Data key, CacheRecord record, ExpiryPolicy expiryPolicy, long now, String source) {
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        long expiryTime = -1L;
        try {
            Duration expiryDuration = expiryPolicy.getExpiryForUpdate();
            if (expiryDuration != null) {
                expiryTime = this.getAdjustedExpireTime(expiryDuration, now);
            }
        }
        catch (Exception e) {
            EmptyStatement.ignore(e);
        }
        this.updateRecord(key, record, expiryTime, now, source);
    }

    protected void onDeleteRecord(Data key, R record, boolean deleted) {
    }

    protected boolean deleteRecord(Data key, int completionId) {
        return this.deleteRecord(key, completionId, SOURCE_NOT_AVAILABLE);
    }

    protected boolean deleteRecord(Data key, int completionId, String source) {
        return this.deleteRecord(key, completionId, source, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean deleteRecord(Data key, int completionId, String source, String origin) {
        R removedRecord = null;
        try {
            removedRecord = this.doRemoveRecord(key, source);
            if (this.isEventsEnabled()) {
                Data eventDataKey = this.toEventData(key);
                Data eventDataValue = this.toEventData(removedRecord);
                this.publishEvent(CacheEventContextUtil.createCacheRemovedEvent(eventDataKey, eventDataValue, -1L, origin, completionId));
            }
            this.onDeleteRecord(key, removedRecord, removedRecord != null);
        }
        catch (Throwable throwable) {
            this.onDeleteRecord(key, removedRecord, removedRecord != null);
            throw throwable;
        }
        return removedRecord != null;
    }

    public R readThroughRecord(Data key, long now) {
        Duration expiryDuration;
        Object value = this.readThroughCache(key);
        if (value == null) {
            return null;
        }
        try {
            expiryDuration = this.defaultExpiryPolicy.getExpiryForCreation();
        }
        catch (Exception e) {
            expiryDuration = Duration.ETERNAL;
        }
        long expiryTime = this.getAdjustedExpireTime(expiryDuration, now);
        if (CacheRecordFactory.isExpiredAt(expiryTime, now)) {
            return null;
        }
        return this.createRecord(key, value, expiryTime, -1);
    }

    public Object readThroughCache(Data key) throws CacheLoaderException {
        if (this.isReadThrough() && this.cacheLoader != null) {
            try {
                Object o = this.dataToValue(key);
                return this.cacheLoader.load(o);
            }
            catch (Exception e) {
                if (!(e instanceof CacheLoaderException)) {
                    throw new CacheLoaderException("Exception in CacheLoader during load", (Throwable)e);
                }
                throw (CacheLoaderException)e;
            }
        }
        return null;
    }

    public void writeThroughCache(Data key, Object value) throws CacheWriterException {
        if (this.isWriteThrough() && this.cacheWriter != null) {
            try {
                Object objKey = this.dataToValue(key);
                Object objValue = this.toValue(value);
                this.cacheWriter.write(new CacheEntry<Object, Object>(objKey, objValue));
            }
            catch (Exception e) {
                if (!(e instanceof CacheWriterException)) {
                    throw new CacheWriterException("Exception in CacheWriter during write", (Throwable)e);
                }
                throw (CacheWriterException)e;
            }
        }
    }

    protected void deleteCacheEntry(Data key) {
        this.deleteCacheEntry(key, CallerProvenance.NOT_WAN);
    }

    protected void deleteCacheEntry(Data key, CallerProvenance provenance) {
        if (this.persistenceEnabledFor(provenance) && this.isWriteThrough() && this.cacheWriter != null) {
            try {
                Object objKey = this.dataToValue(key);
                this.cacheWriter.delete(objKey);
            }
            catch (Exception e) {
                if (!(e instanceof CacheWriterException)) {
                    throw new CacheWriterException("Exception in CacheWriter during delete", (Throwable)e);
                }
                throw (CacheWriterException)e;
            }
        }
    }

    @SuppressFBWarnings(value={"WMI_WRONG_MAP_ITERATOR"})
    protected void deleteAllCacheEntry(Set<Data> keys) {
        if (this.isWriteThrough() && this.cacheWriter != null && keys != null && !keys.isEmpty()) {
            Map<Object, Data> keysToDelete = MapUtil.createHashMap(keys.size());
            for (Data data : keys) {
                Object localKeyObj = this.dataToValue(data);
                keysToDelete.put(localKeyObj, data);
            }
            Set keysObject = keysToDelete.keySet();
            try {
                this.cacheWriter.deleteAll(keysObject);
            }
            catch (Exception exception) {
                if (!(exception instanceof CacheWriterException)) {
                    throw new CacheWriterException("Exception in CacheWriter during deleteAll", (Throwable)exception);
                }
                throw (CacheWriterException)exception;
            }
            finally {
                for (Object undeletedKey : keysObject) {
                    Data undeletedKeyData = (Data)keysToDelete.get(undeletedKey);
                    keys.remove(undeletedKeyData);
                }
            }
        }
    }

    protected Map<Data, Object> loadAllCacheEntry(Set<Data> keys) {
        if (this.cacheLoader != null) {
            Map loaded;
            Map<Object, Data> keysToLoad = MapUtil.createHashMap(keys.size());
            for (Data key : keys) {
                Object localKeyObj = this.dataToValue(key);
                keysToLoad.put(localKeyObj, key);
            }
            try {
                loaded = this.cacheLoader.loadAll(keysToLoad.keySet());
            }
            catch (Throwable e) {
                if (!(e instanceof CacheLoaderException)) {
                    throw new CacheLoaderException("Exception in CacheLoader during loadAll", e);
                }
                throw (CacheLoaderException)e;
            }
            Map<Data, Object> result = MapUtil.createHashMap(keysToLoad.size());
            for (Map.Entry entry : keysToLoad.entrySet()) {
                Object keyObj = entry.getKey();
                Object valueObject = loaded.get(keyObj);
                Data keyData = (Data)entry.getValue();
                result.put(keyData, valueObject);
            }
            return result;
        }
        return null;
    }

    @Override
    public CacheRecord getRecord(Data key) {
        return (CacheRecord)this.records.get(key);
    }

    @Override
    public void putRecord(Data key, CacheRecord record, boolean updateJournal) {
        this.evictIfRequired();
        this.doPutRecord(key, record, SOURCE_NOT_AVAILABLE, updateJournal);
    }

    protected R doPutRecord(Data key, R record, String source, boolean updateJournal) {
        this.markExpirable(record.getExpirationTime());
        CacheRecord oldRecord = (CacheRecord)this.records.put((Data)key, record);
        if (updateJournal) {
            if (oldRecord != null) {
                this.cacheService.eventJournal.writeUpdateEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, oldRecord.getValue(), record.getValue());
            } else {
                this.cacheService.eventJournal.writeCreatedEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, record.getValue());
            }
        }
        this.invalidateEntry(key, source);
        return (R)oldRecord;
    }

    @Override
    public CacheRecord removeRecord(Data key) {
        return this.doRemoveRecord(key, SOURCE_NOT_AVAILABLE);
    }

    protected R doRemoveRecord(Data key, String source) {
        CacheRecord removedRecord = (CacheRecord)this.records.remove(key);
        if (removedRecord != null) {
            this.cacheService.eventJournal.writeRemoveEvent(this.eventJournalConfig, this.objectNamespace, this.partitionId, key, removedRecord.getValue());
            this.invalidateEntry(key, source);
        }
        return (R)removedRecord;
    }

    protected void onGet(Data key, ExpiryPolicy expiryPolicy, Object value, R record) {
    }

    protected void onGetError(Data key, ExpiryPolicy expiryPolicy, Object value, R record, Throwable error) {
    }

    @Override
    public Object get(Data key, ExpiryPolicy expiryPolicy) {
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        long now = Clock.currentTimeMillis();
        Object value = null;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = this.processExpiredEntry(key, record, now);
        try {
            if (this.recordNotExistOrExpired(record, isExpired)) {
                if (this.isStatisticsEnabled()) {
                    this.statistics.increaseCacheMisses(1L);
                }
                if ((value = this.readThroughCache(key)) == null) {
                    if (this.isStatisticsEnabled()) {
                        this.statistics.addGetTimeNanos(System.nanoTime() - start);
                    }
                    return null;
                }
                record = this.createRecordWithExpiry(key, value, expiryPolicy, now, true, -1);
            } else {
                value = this.recordToValue(record);
                this.onRecordAccess(key, record, expiryPolicy, now);
                if (this.isStatisticsEnabled()) {
                    this.statistics.increaseCacheHits(1L);
                }
            }
            if (this.isStatisticsEnabled()) {
                this.statistics.addGetTimeNanos(System.nanoTime() - start);
            }
            this.onGet(key, expiryPolicy, value, record);
            return value;
        }
        catch (Throwable error) {
            this.onGetError(key, expiryPolicy, value, record, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public void evictExpiredEntries(int expirationPercentage) {
        long now = Clock.currentTimeMillis();
        int maxIterationCount = this.getMaxIterationCount(this.size(), expirationPercentage);
        int evictedCount = 0;
        int maxRetry = 3;
        for (int loop = 0; loop < maxRetry && (evictedCount += this.evictExpiredInternal(maxIterationCount, now)) < maxIterationCount; ++loop) {
        }
    }

    protected void initExpirationIterator() {
        if (this.expirationIterator == null || !this.expirationIterator.hasNext()) {
            this.expirationIterator = this.records.entrySet().iterator();
        }
    }

    private int evictExpiredInternal(int maxIterationCount, long now) {
        this.initExpirationIterator();
        LinkedList<Map.Entry<Data, R>> records = new LinkedList<Map.Entry<Data, R>>();
        for (int processedCount = 0; this.expirationIterator.hasNext() && processedCount < maxIterationCount; ++processedCount) {
            Map.Entry<Data, R> record = this.expirationIterator.next();
            records.add(record);
        }
        int evictedCount = 0;
        while (!records.isEmpty()) {
            CacheRecord value;
            Map.Entry record = (Map.Entry)records.poll();
            Data key = (Data)record.getKey();
            boolean expired = this.evictIfExpired(key, value = (CacheRecord)record.getValue(), now);
            if (!expired) continue;
            this.accumulateOrSendExpiredKeysToBackup(key, value);
            ++evictedCount;
        }
        return evictedCount;
    }

    private int getMaxIterationCount(int size, int percentage) {
        int defaultMaxIterationCount = 100;
        float oneHundred = 100.0f;
        float maxIterationCount = (float)size * ((float)percentage / 100.0f);
        if (maxIterationCount <= 100.0f) {
            return 100;
        }
        return Math.round(maxIterationCount);
    }

    @Override
    public boolean contains(Data key) {
        long now = Clock.currentTimeMillis();
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean isExpired = this.processExpiredEntry(key, record, now);
        return record != null && !isExpired;
    }

    protected void onPut(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean getValue, boolean disableWriteThrough, R record, Object oldValue, boolean isExpired, boolean isNewPut, boolean isSaveSucceed) {
    }

    protected void onPutError(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean getValue, boolean disableWriteThrough, R record, Object oldValue, boolean wouldBeNewPut, Throwable error) {
    }

    protected Object put(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean getValue, boolean disableWriteThrough, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean isOnNewPut = false;
        Object oldValue = null;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = this.processExpiredEntry(key, record, now, source);
        try {
            boolean isSaveSucceed;
            if (this.recordNotExistOrExpired(record, isExpired)) {
                isOnNewPut = true;
                record = this.createRecordWithExpiry(key, value, expiryPolicy, now, disableWriteThrough, completionId, source);
                isSaveSucceed = record != null;
            } else {
                if (getValue) {
                    oldValue = this.toValue(record);
                }
                isSaveSucceed = this.updateRecordWithExpiry(key, value, record, expiryPolicy, now, disableWriteThrough, completionId, source);
            }
            this.onPut(key, value, expiryPolicy, source, getValue, disableWriteThrough, record, oldValue, isExpired, isOnNewPut, isSaveSucceed);
            this.updateGetAndPutStat(isSaveSucceed, getValue, oldValue == null, start);
            if (getValue) {
                return oldValue;
            }
            return record;
        }
        catch (Throwable error) {
            this.onPutError(key, value, expiryPolicy, source, getValue, disableWriteThrough, record, oldValue, isOnNewPut, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    protected Object put(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean getValue, int completionId) {
        return this.put(key, value, expiryPolicy, source, getValue, false, completionId);
    }

    public R put(Data key, Object value, ExpiryPolicy expiryPolicy, String source, int completionId) {
        return (R)((CacheRecord)this.put(key, value, expiryPolicy, source, false, false, completionId));
    }

    @Override
    public Object getAndPut(Data key, Object value, ExpiryPolicy expiryPolicy, String source, int completionId) {
        return this.put(key, value, expiryPolicy, source, true, false, completionId);
    }

    protected void onPutIfAbsent(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean disableWriteThrough, R record, boolean isExpired, boolean isSaveSucceed) {
    }

    protected void onPutIfAbsentError(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean disableWriteThrough, R record, Throwable error) {
    }

    protected boolean putIfAbsent(Data key, Object value, ExpiryPolicy expiryPolicy, String source, boolean disableWriteThrough, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean saved = false;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = this.processExpiredEntry(key, record, now, source);
        boolean cacheMiss = this.recordNotExistOrExpired(record, isExpired);
        try {
            if (cacheMiss) {
                saved = this.createRecordWithExpiry(key, value, expiryPolicy, now, disableWriteThrough, completionId, source) != null;
            } else if (this.isEventsEnabled()) {
                this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), completionId));
            }
            this.onPutIfAbsent(key, value, expiryPolicy, source, disableWriteThrough, record, isExpired, saved);
            if (this.isStatisticsEnabled()) {
                if (saved) {
                    this.statistics.increaseCachePuts();
                    this.statistics.addPutTimeNanos(System.nanoTime() - start);
                }
                if (cacheMiss) {
                    this.statistics.increaseCacheMisses();
                } else {
                    this.statistics.increaseCacheHits();
                }
            }
            return saved;
        }
        catch (Throwable error) {
            this.onPutIfAbsentError(key, value, expiryPolicy, source, disableWriteThrough, record, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public boolean putIfAbsent(Data key, Object value, ExpiryPolicy expiryPolicy, String source, int completionId) {
        return this.putIfAbsent(key, value, expiryPolicy, source, false, completionId);
    }

    protected void onReplace(Data key, Object oldValue, Object newValue, ExpiryPolicy expiryPolicy, String source, boolean getValue, R record, boolean isExpired, boolean replaced) {
    }

    protected void onReplaceError(Data key, Object oldValue, Object newValue, ExpiryPolicy expiryPolicy, String source, boolean getValue, R record, boolean isExpired, boolean replaced, Throwable error) {
    }

    @Override
    public boolean replace(Data key, Object value, ExpiryPolicy expiryPolicy, String source, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean replaced = false;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = record != null && record.isExpiredAt(now);
        try {
            if (this.recordNotExistOrExpired(record, isExpired)) {
                if (this.isEventsEnabled()) {
                    this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), completionId));
                }
            } else {
                replaced = this.updateRecordWithExpiry(key, value, record, expiryPolicy, now, false, completionId, source);
            }
            this.onReplace(key, null, value, expiryPolicy, source, false, record, isExpired, replaced);
            if (this.isStatisticsEnabled()) {
                if (replaced) {
                    this.statistics.increaseCachePuts(1L);
                    this.statistics.increaseCacheHits(1L);
                    this.statistics.addPutTimeNanos(System.nanoTime() - start);
                } else {
                    this.statistics.increaseCacheMisses(1L);
                }
            }
            return replaced;
        }
        catch (Throwable error) {
            this.onReplaceError(key, null, value, expiryPolicy, source, false, record, isExpired, replaced, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public boolean replace(Data key, Object oldValue, Object newValue, ExpiryPolicy expiryPolicy, String source, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean isHit = false;
        boolean replaced = false;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = record != null && record.isExpiredAt(now);
        try {
            if (record != null && !isExpired) {
                isHit = true;
                Object currentValue = this.toStorageValue(record);
                if (this.compare(currentValue, this.toStorageValue(oldValue))) {
                    replaced = this.updateRecordWithExpiry(key, newValue, record, expiryPolicy, now, false, completionId, source);
                } else {
                    this.onRecordAccess(key, record, expiryPolicy, now);
                }
            }
            if (!replaced && this.isEventsEnabled()) {
                this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), completionId));
            }
            this.onReplace(key, oldValue, newValue, expiryPolicy, source, false, record, isExpired, replaced);
            this.updateReplaceStat(replaced, isHit, start);
            return replaced;
        }
        catch (Throwable error) {
            this.onReplaceError(key, oldValue, newValue, expiryPolicy, source, false, record, isExpired, replaced, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public Object getAndReplace(Data key, Object value, ExpiryPolicy expiryPolicy, String source, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean replaced = false;
        CacheRecord record = (CacheRecord)this.records.get(key);
        expiryPolicy = this.getExpiryPolicy(record, expiryPolicy);
        boolean isExpired = record != null && record.isExpiredAt(now);
        try {
            Object obj = this.toValue(record);
            if (this.recordNotExistOrExpired(record, isExpired)) {
                obj = null;
                if (this.isEventsEnabled()) {
                    this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), completionId));
                }
            } else {
                replaced = this.updateRecordWithExpiry(key, value, record, expiryPolicy, now, false, completionId, source);
            }
            this.onReplace(key, null, value, expiryPolicy, source, false, record, isExpired, replaced);
            if (this.isStatisticsEnabled()) {
                this.statistics.addGetTimeNanos(System.nanoTime() - start);
                if (obj != null) {
                    this.statistics.increaseCacheHits(1L);
                    this.statistics.increaseCachePuts(1L);
                    this.statistics.addPutTimeNanos(System.nanoTime() - start);
                } else {
                    this.statistics.increaseCacheMisses(1L);
                }
            }
            return obj;
        }
        catch (Throwable error) {
            this.onReplaceError(key, null, value, expiryPolicy, source, false, record, isExpired, replaced, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public boolean setExpiryPolicy(Collection<Data> keys, Object expiryPolicy, String source) {
        if (this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("Modifying expiry policy is available when cluster version is at least 3.11");
        }
        ExpiryPolicy expiryPolicyInstance = null;
        if (expiryPolicy instanceof Data) {
            expiryPolicyInstance = (ExpiryPolicy)this.toValue(expiryPolicy);
        }
        boolean atLeastOneKey = false;
        long now = System.currentTimeMillis();
        for (Data key : keys) {
            CacheRecord record = (CacheRecord)this.records.get(key);
            if (record == null || this.processExpiredEntry(key, record, now)) continue;
            this.updateExpiryPolicyOfRecord(key, record, expiryPolicy);
            this.updateRecordWithExpiry(key, record, expiryPolicyInstance, System.currentTimeMillis(), source);
            atLeastOneKey = true;
        }
        return atLeastOneKey;
    }

    @Override
    public Object getExpiryPolicy(Data key) {
        CacheRecord record = this.getRecord(key);
        if (record != null) {
            return this.extractExpiryPolicyOfRecord(record);
        }
        return null;
    }

    protected void onRemove(Data key, Object value, String source, boolean getValue, R record, boolean removed) {
    }

    protected void onRemoveError(Data key, Object value, String source, boolean getValue, R record, boolean removed, Throwable error) {
    }

    @Override
    public boolean remove(Data key, String source, String origin, int completionId) {
        return this.remove(key, source, origin, completionId, CallerProvenance.NOT_WAN);
    }

    @Override
    public boolean remove(Data key, String source, String origin, int completionId, CallerProvenance provenance) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        this.deleteCacheEntry(key, provenance);
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean removed = false;
        try {
            if (this.recordNotExistOrExpired(record, now)) {
                if (this.isEventsEnabled()) {
                    this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), -1L, origin, completionId));
                }
            } else {
                removed = this.deleteRecord(key, completionId, source, origin);
            }
            this.onRemove(key, null, source, false, record, removed);
            if (removed && this.isStatisticsEnabled()) {
                this.statistics.increaseCacheRemovals(1L);
                this.statistics.addRemoveTimeNanos(System.nanoTime() - start);
            }
            return removed;
        }
        catch (Throwable error) {
            this.onRemoveError(key, null, source, false, record, removed, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public InvalidationQueue<ExpiredKey> getExpiredKeysQueue() {
        return this.expiredKeys;
    }

    @Override
    public boolean remove(Data key, Object value, String source, String origin, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = System.nanoTime();
        CacheRecord record = (CacheRecord)this.records.get(key);
        int hitCount = 0;
        boolean removed = false;
        try {
            if (this.recordNotExistOrExpired(record, now)) {
                if (this.isStatisticsEnabled()) {
                    this.statistics.increaseCacheMisses(1L);
                }
            } else {
                ++hitCount;
                if (this.compare(this.toStorageValue(record), this.toStorageValue(value))) {
                    this.deleteCacheEntry(key);
                    removed = this.deleteRecord(key, completionId, source, origin);
                } else {
                    long expiryTime = this.onRecordAccess(key, record, this.defaultExpiryPolicy, now);
                    this.processExpiredEntry(key, record, expiryTime, now, source, origin);
                }
            }
            if (!removed && this.isEventsEnabled()) {
                this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), -1L, origin, completionId));
            }
            this.onRemove(key, value, source, false, record, removed);
            this.updateRemoveStatistics(removed, hitCount, start);
            return removed;
        }
        catch (Throwable error) {
            this.onRemoveError(key, null, source, false, record, removed, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    private void updateRemoveStatistics(boolean result, int hitCount, long start) {
        if (result && this.isStatisticsEnabled()) {
            this.statistics.increaseCacheRemovals(1L);
            this.statistics.addRemoveTimeNanos(System.nanoTime() - start);
            if (hitCount == 1) {
                this.statistics.increaseCacheHits(hitCount);
            } else {
                this.statistics.increaseCacheMisses(1L);
            }
        }
    }

    protected void markExpirable(long expiryTime) {
        if (expiryTime > 0L && expiryTime < Long.MAX_VALUE) {
            this.hasEntryWithExpiration = true;
        }
        if (this.isPrimary() && this.hasEntryWithExpiration) {
            this.cacheService.getExpirationManager().scheduleExpirationTask();
        }
    }

    @Override
    public Object getAndRemove(Data key, String source, int completionId) {
        return this.getAndRemove(key, source, completionId, null);
    }

    public Object getAndRemove(Data key, String source, int completionId, String origin) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        this.deleteCacheEntry(key);
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean removed = false;
        try {
            Object obj;
            if (this.recordNotExistOrExpired(record, now)) {
                obj = null;
                if (this.isEventsEnabled()) {
                    this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(this.toEventData(key), -1L, origin, completionId));
                }
            } else {
                obj = this.toValue(record);
                removed = this.deleteRecord(key, completionId, source, origin);
            }
            this.onRemove(key, null, source, false, record, removed);
            if (this.isStatisticsEnabled()) {
                this.statistics.addGetTimeNanos(System.nanoTime() - start);
                if (obj != null) {
                    this.statistics.increaseCacheHits(1L);
                    this.statistics.increaseCacheRemovals(1L);
                    this.statistics.addRemoveTimeNanos(System.nanoTime() - start);
                } else {
                    this.statistics.increaseCacheMisses(1L);
                }
            }
            return obj;
        }
        catch (Throwable error) {
            this.onRemoveError(key, null, source, false, record, removed, error);
            throw ExceptionUtil.rethrow(error);
        }
    }

    @Override
    public MapEntries getAll(Set<Data> keySet, ExpiryPolicy expiryPolicy) {
        MapEntries result = new MapEntries(keySet.size());
        for (Data key : keySet) {
            Object value = this.get(key, expiryPolicy);
            if (value == null) continue;
            result.add(key, this.toHeapData(value));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAll(Set<Data> keys, int completionId) {
        HashSet<Data> keysToClean;
        long now = Clock.currentTimeMillis();
        HashSet<Data> localKeys = new HashSet<Data>(keys.isEmpty() ? this.records.keySet() : keys);
        try {
            this.deleteAllCacheEntry(localKeys);
            keysToClean = new HashSet<Data>(keys.isEmpty() ? this.records.keySet() : keys);
        }
        catch (Throwable throwable) {
            HashSet<Data> keysToClean2 = new HashSet<Data>(keys.isEmpty() ? this.records.keySet() : keys);
            for (Data key : keysToClean2) {
                this.eventsBatchingEnabled = true;
                CacheRecord record = (CacheRecord)this.records.get(key);
                if (localKeys.contains(key) && record != null) {
                    boolean isExpired = this.processExpiredEntry(key, record, now);
                    if (!isExpired) {
                        this.deleteRecord(key, -1);
                        if (this.isStatisticsEnabled()) {
                            this.statistics.increaseCacheRemovals(1L);
                        }
                    }
                    keys.add(key);
                } else {
                    keys.remove(key);
                }
                this.eventsBatchingEnabled = false;
            }
            int orderKey = keys.hashCode();
            this.publishBatchedEvents(this.name, CacheEventType.REMOVED, orderKey);
            if (this.isEventsEnabled()) {
                this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(completionId));
            }
            throw throwable;
        }
        for (Data key : keysToClean) {
            this.eventsBatchingEnabled = true;
            CacheRecord record = (CacheRecord)this.records.get(key);
            if (localKeys.contains(key) && record != null) {
                boolean isExpired = this.processExpiredEntry(key, record, now);
                if (!isExpired) {
                    this.deleteRecord(key, -1);
                    if (this.isStatisticsEnabled()) {
                        this.statistics.increaseCacheRemovals(1L);
                    }
                }
                keys.add(key);
            } else {
                keys.remove(key);
            }
            this.eventsBatchingEnabled = false;
        }
        int orderKey = keys.hashCode();
        this.publishBatchedEvents(this.name, CacheEventType.REMOVED, orderKey);
        if (this.isEventsEnabled()) {
            this.publishEvent(CacheEventContextUtil.createCacheCompleteEvent(completionId));
        }
    }

    @Override
    public Set<Data> loadAll(Set<Data> keys, boolean replaceExistingValues) {
        Map<Data, Object> loaded = this.loadAllCacheEntry(keys);
        if (loaded == null || loaded.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Data> keysLoaded = SetUtil.createHashSet(loaded.size());
        if (replaceExistingValues) {
            for (Map.Entry<Data, Object> entry : loaded.entrySet()) {
                Data key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) continue;
                this.put(key, value, null, SOURCE_NOT_AVAILABLE, false, true, -1);
                keysLoaded.add(key);
            }
        } else {
            for (Map.Entry<Data, Object> entry : loaded.entrySet()) {
                boolean hasPut;
                Data key = entry.getKey();
                Object value = entry.getValue();
                if (value == null || !(hasPut = this.putIfAbsent(key, value, null, SOURCE_NOT_AVAILABLE, true, -1))) continue;
                keysLoaded.add(key);
            }
        }
        return keysLoaded;
    }

    @Override
    public CacheRecord merge(SplitBrainMergeTypes.CacheMergeTypes mergingEntry, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy, CallerProvenance callerProvenance) {
        boolean disableWriteThrough;
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        this.injectDependencies(mergingEntry);
        this.injectDependencies(mergePolicy);
        boolean merged = false;
        Data key = (Data)mergingEntry.getKey();
        long expiryTime = mergingEntry.getExpirationTime();
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean isExpired = this.processExpiredEntry(key, record, now);
        boolean bl = disableWriteThrough = !this.persistenceEnabledFor(callerProvenance);
        if (record == null || isExpired) {
            Data newValue = mergePolicy.merge(mergingEntry, null);
            if (newValue != null) {
                record = this.createRecordWithExpiry(key, (Object)newValue, expiryTime, now, disableWriteThrough, -1);
                merged = record != null;
            }
        } else {
            Object oldValue = this.ss.toData(record.getValue());
            SplitBrainMergeTypes.CacheMergeTypes existingEntry = MergingValueFactory.createMergingEntry(this.ss, key, oldValue, record);
            Data newValue = mergePolicy.merge(mergingEntry, existingEntry);
            merged = this.updateWithMergingValue(key, oldValue, newValue, record, expiryTime, now, disableWriteThrough);
        }
        if (merged && this.isStatisticsEnabled()) {
            this.statistics.increaseCachePuts(1L);
            this.statistics.addPutTimeNanos(System.nanoTime() - start);
        }
        return merged ? record : null;
    }

    @Override
    public CacheRecord merge(CacheEntryView<Data, Data> cacheEntryView, CacheMergePolicy mergePolicy, String caller, String origin, int completionId, CallerProvenance callerProvenance) {
        boolean disableWriteThrough;
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        boolean merged = false;
        Data key = cacheEntryView.getKey();
        Data value = cacheEntryView.getValue();
        long expiryTime = cacheEntryView.getExpirationTime();
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean isExpired = this.processExpiredEntry(key, record, now);
        boolean bl = disableWriteThrough = !this.persistenceEnabledFor(callerProvenance);
        if (record == null || isExpired) {
            Object newValue = mergePolicy.merge(this.name, this.createCacheEntryView(key, value, cacheEntryView.getCreationTime(), cacheEntryView.getExpirationTime(), cacheEntryView.getLastAccessTime(), cacheEntryView.getAccessHit(), cacheEntryView.getExpiryPolicy(), mergePolicy), null);
            if (newValue != null) {
                record = this.createRecordWithExpiry(key, newValue, expiryTime, now, disableWriteThrough, -1);
                merged = record != null;
            }
        } else {
            Object oldValue = record.getValue();
            Object newValue = mergePolicy.merge(this.name, this.createCacheEntryView(key, value, cacheEntryView.getCreationTime(), cacheEntryView.getExpirationTime(), cacheEntryView.getLastAccessTime(), cacheEntryView.getAccessHit(), cacheEntryView.getExpiryPolicy(), mergePolicy), this.createCacheEntryView(key, oldValue, cacheEntryView.getCreationTime(), record.getExpirationTime(), record.getLastAccessTime(), record.getAccessHit(), record.getExpiryPolicy(), mergePolicy));
            merged = this.updateWithMergingValue(key, oldValue, newValue, record, expiryTime, now, disableWriteThrough);
        }
        if (merged && this.isStatisticsEnabled()) {
            this.statistics.increaseCachePuts(1L);
            this.statistics.addPutTimeNanos(System.nanoTime() - start);
        }
        return merged ? record : null;
    }

    private boolean updateWithMergingValue(Data key, Object existingValue, Object mergingValue, R record, long expiryTime, long now, boolean disableWriteThrough) {
        if (this.valueComparator.isEqual(existingValue, mergingValue, this.ss)) {
            this.updateExpiryTime(record, expiryTime);
            this.processExpiredEntry(key, record, now);
            return true;
        }
        return this.updateRecordWithExpiry(key, mergingValue, record, -1L, now, disableWriteThrough, -1);
    }

    private Object getExpiryPolicyOrNull(R record) {
        if (record != null) {
            return record.getExpiryPolicy();
        }
        return null;
    }

    private CacheEntryView createCacheEntryView(Object key, Object value, long creationTime, long expirationTime, long lastAccessTime, long accessHit, Object expiryPolicy, CacheMergePolicy mergePolicy) {
        SerializationService ss = mergePolicy instanceof StorageTypeAwareCacheMergePolicy ? null : this.ss;
        return new LazyCacheEntryView(key, value, creationTime, expirationTime, lastAccessTime, accessHit, expiryPolicy, ss);
    }

    @Override
    public CacheKeyIterationResult fetchKeys(int tableIndex, int size) {
        return this.records.fetchKeys(tableIndex, size);
    }

    @Override
    public CacheEntryIterationResult fetchEntries(int tableIndex, int size) {
        return this.records.fetchEntries(tableIndex, size);
    }

    @Override
    public Object invoke(Data key, EntryProcessor entryProcessor, Object[] arguments, int completionId) {
        long now = Clock.currentTimeMillis();
        long start = this.isStatisticsEnabled() ? System.nanoTime() : 0L;
        CacheRecord record = (CacheRecord)this.records.get(key);
        boolean isExpired = this.processExpiredEntry(key, record, now);
        if (isExpired) {
            record = null;
        }
        if (this.isStatisticsEnabled()) {
            if (this.recordNotExistOrExpired(record, isExpired)) {
                this.statistics.increaseCacheMisses(1L);
            } else {
                this.statistics.increaseCacheHits(1L);
            }
            this.statistics.addGetTimeNanos(System.nanoTime() - start);
        }
        CacheEntryProcessorEntry entry = this.createCacheEntryProcessorEntry(key, record, now, completionId);
        this.injectDependencies(entryProcessor);
        Object result = entryProcessor.process((MutableEntry)entry, arguments);
        entry.applyChanges();
        return result;
    }

    private boolean recordNotExistOrExpired(R record, boolean isExpired) {
        return record == null || isExpired;
    }

    private boolean recordNotExistOrExpired(R record, long now) {
        return record == null || record.isExpiredAt(now);
    }

    @Override
    public int size() {
        return this.records.size();
    }

    @Override
    public CacheStatisticsImpl getCacheStats() {
        return this.statistics;
    }

    @Override
    public CacheConfig getConfig() {
        return this.cacheConfig;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<Data, CacheRecord> getReadOnlyRecords() {
        return Collections.unmodifiableMap(this.records);
    }

    @Override
    public void clear() {
        this.reset();
        this.destroyEventJournal();
    }

    protected void destroyEventJournal() {
        this.cacheService.eventJournal.destroy(this.objectNamespace, this.partitionId);
    }

    @Override
    public void reset() {
        this.records.clear();
    }

    @Override
    public void close(boolean onShutdown) {
        this.clear();
        this.closeListeners();
    }

    @Override
    public void destroy() {
        this.clear();
        this.closeListeners();
        this.onDestroy();
    }

    @Override
    public void destroyInternals() {
        this.reset();
        this.closeListeners();
        this.onDestroy();
    }

    protected void onDestroy() {
    }

    protected void closeListeners() {
        InternalEventService eventService = (InternalEventService)this.cacheService.getNodeEngine().getEventService();
        Collection<EventRegistration> candidates = eventService.getRegistrations("hz:impl:cacheService", this.name);
        for (EventRegistration eventRegistration : candidates) {
            eventService.close(eventRegistration);
        }
    }

    @Override
    public boolean isWanReplicationEnabled() {
        return this.wanReplicationEnabled;
    }

    @Override
    public ObjectNamespace getObjectNamespace() {
        return this.objectNamespace;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }
}

