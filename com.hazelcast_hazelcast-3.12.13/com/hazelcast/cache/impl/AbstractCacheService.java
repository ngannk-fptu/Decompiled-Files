/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.cache.CacheException
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.event.CacheEntryListener
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEntryCountResolver;
import com.hazelcast.cache.impl.CacheEntryListenerProvider;
import com.hazelcast.cache.impl.CacheEventContext;
import com.hazelcast.cache.impl.CacheEventHandler;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheMXBeanImpl;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CachePartitionEventData;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.cache.impl.CacheSplitBrainHandlerService;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cache.impl.CacheStatisticsMXBeanImpl;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.MXBeanUtil;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.eviction.CacheClearExpiredRecordsTask;
import com.hazelcast.cache.impl.journal.CacheEventJournal;
import com.hazelcast.cache.impl.journal.RingbufferCacheEventJournalImpl;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.cache.impl.operation.AddCacheConfigOperationSupplier;
import com.hazelcast.cache.impl.operation.OnJoinCacheOperation;
import com.hazelcast.cache.impl.tenantcontrol.CacheDestroyEventContext;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterStateListener;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.internal.eviction.ExpirationManager;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import com.hazelcast.spi.tenantcontrol.TenantControlFactory;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.ServiceLoader;
import com.hazelcast.wan.WanReplicationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.cache.CacheException;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryListener;

public abstract class AbstractCacheService
implements ICacheService,
PreJoinAwareService,
PartitionAwareService,
QuorumAwareService,
SplitBrainHandlerService,
ClusterStateListener {
    public static final String TENANT_CONTROL_FACTORY = "com.hazelcast.spi.tenantcontrol.TenantControlFactory";
    private static final String SETUP_REF = "setupRef";
    protected final ConcurrentMap<String, CacheConfig> configs = new ConcurrentHashMap<String, CacheConfig>();
    protected final ConcurrentMap<String, CacheContext> cacheContexts = new ConcurrentHashMap<String, CacheContext>();
    protected final ConcurrentMap<String, CacheStatisticsImpl> statistics = new ConcurrentHashMap<String, CacheStatisticsImpl>();
    protected final ConcurrentMap<String, Set<Closeable>> resources = new ConcurrentHashMap<String, Set<Closeable>>();
    protected final ConcurrentMap<String, Closeable> closeableListeners = new ConcurrentHashMap<String, Closeable>();
    protected final ConcurrentMap<String, CacheOperationProvider> operationProviderCache = new ConcurrentHashMap<String, CacheOperationProvider>();
    protected final ConstructorFunction<String, CacheContext> cacheContextsConstructorFunction = new ConstructorFunction<String, CacheContext>(){

        @Override
        public CacheContext createNew(String name) {
            return new CacheContext();
        }
    };
    protected final ConstructorFunction<String, CacheStatisticsImpl> cacheStatisticsConstructorFunction = new ConstructorFunction<String, CacheStatisticsImpl>(){

        @Override
        public CacheStatisticsImpl createNew(String name) {
            return new CacheStatisticsImpl(Clock.currentTimeMillis(), CacheEntryCountResolver.createEntryCountResolver(AbstractCacheService.this.getOrCreateCacheContext(name)));
        }
    };
    protected final ContextMutexFactory cacheResourcesMutexFactory = new ContextMutexFactory();
    protected final ConstructorFunction<String, Set<Closeable>> cacheResourcesConstructorFunction = new ConstructorFunction<String, Set<Closeable>>(){

        @Override
        public Set<Closeable> createNew(String name) {
            return Collections.newSetFromMap(new ConcurrentHashMap());
        }
    };
    protected ILogger logger;
    protected NodeEngine nodeEngine;
    protected CachePartitionSegment[] segments;
    protected CacheEventHandler cacheEventHandler;
    protected RingbufferCacheEventJournalImpl eventJournal;
    protected CacheMergePolicyProvider mergePolicyProvider;
    protected CacheSplitBrainHandlerService splitBrainHandlerService;
    protected CacheClearExpiredRecordsTask clearExpiredRecordsTask;
    protected ExpirationManager expirationManager;

    @Override
    public final void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.segments = new CachePartitionSegment[partitionCount];
        for (int i = 0; i < partitionCount; ++i) {
            this.segments[i] = this.newPartitionSegment(i);
        }
        this.clearExpiredRecordsTask = new CacheClearExpiredRecordsTask(this.segments, nodeEngine);
        this.expirationManager = new ExpirationManager(this.clearExpiredRecordsTask, nodeEngine);
        this.cacheEventHandler = new CacheEventHandler(nodeEngine);
        this.splitBrainHandlerService = new CacheSplitBrainHandlerService(nodeEngine, this.segments);
        this.logger = nodeEngine.getLogger(this.getClass());
        this.eventJournal = new RingbufferCacheEventJournalImpl(nodeEngine);
        this.mergePolicyProvider = new CacheMergePolicyProvider(nodeEngine);
        this.postInit(nodeEngine, properties);
    }

    public CacheMergePolicyProvider getMergePolicyProvider() {
        return this.mergePolicyProvider;
    }

    public Object getMergePolicy(String name) {
        CacheConfig cacheConfig = (CacheConfig)this.configs.get(name);
        String mergePolicyName = cacheConfig.getMergePolicy();
        return this.mergePolicyProvider.getMergePolicy(mergePolicyName);
    }

    public ConcurrentMap<String, CacheConfig> getConfigs() {
        return this.configs;
    }

    protected void postInit(NodeEngine nodeEngine, Properties properties) {
    }

    protected abstract CachePartitionSegment newPartitionSegment(int var1);

    protected abstract ICacheRecordStore createNewRecordStore(String var1, int var2);

    @Override
    public void reset() {
        this.reset(false);
    }

    private void reset(boolean onShutdown) {
        for (String objectName : this.configs.keySet()) {
            this.deleteCache(objectName, null, false);
        }
        CachePartitionSegment[] partitionSegments = this.segments;
        for (CachePartitionSegment partitionSegment : partitionSegments) {
            if (partitionSegment == null) continue;
            if (onShutdown) {
                partitionSegment.shutdown();
                continue;
            }
            partitionSegment.reset();
            partitionSegment.init();
        }
        for (String objectName : this.configs.keySet()) {
            this.sendInvalidationEvent(objectName, null, "<NA>");
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        if (!terminate) {
            this.expirationManager.onShutdown();
            this.cacheEventHandler.shutdown();
            this.reset(true);
        }
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public CachePartitionSegment[] getPartitionSegments() {
        return this.segments;
    }

    @Override
    public DistributedObject createDistributedObject(String cacheNameWithPrefix) {
        try {
            if (cacheNameWithPrefix.equals(SETUP_REF)) {
                CacheSimpleConfig cacheSimpleConfig = new CacheSimpleConfig();
                cacheSimpleConfig.setName(SETUP_REF);
                CacheConfig cacheConfig = new CacheConfig(cacheSimpleConfig);
                cacheConfig.setManagerPrefix("/hz/");
                return new CacheProxy(cacheConfig, this.nodeEngine, this);
            }
            CacheConfig cacheConfig = this.getCacheConfig(cacheNameWithPrefix);
            if (cacheConfig == null) {
                String cacheName = cacheNameWithPrefix.substring("/hz/".length());
                cacheConfig = this.findCacheConfig(cacheName);
                if (cacheConfig == null) {
                    throw new CacheNotExistsException("Couldn't find cache config with name " + cacheNameWithPrefix);
                }
                cacheConfig.setManagerPrefix("/hz/");
            }
            ConfigValidator.checkCacheConfig(cacheConfig, this.mergePolicyProvider);
            Object mergePolicy = this.mergePolicyProvider.getMergePolicy(cacheConfig.getMergePolicy());
            MergePolicyValidator.checkMergePolicySupportsInMemoryFormat(cacheConfig.getName(), mergePolicy, cacheConfig.getInMemoryFormat(), true, this.logger);
            if (this.putCacheConfigIfAbsent(cacheConfig) == null) {
                this.createCacheConfigOnAllMembers(PreJoinCacheConfig.of(cacheConfig));
            }
            return new CacheProxy(cacheConfig, this.nodeEngine, this);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        this.deleteCache(objectName, null, true);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearCachesHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
        this.initPartitionReplica(event.getPartitionId());
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearCachesHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
        this.initPartitionReplica(event.getPartitionId());
    }

    private void clearCachesHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        if (thresholdReplicaIndex == -1) {
            this.clearPartitionReplica(partitionId);
            return;
        }
        CachePartitionSegment segment = this.segments[partitionId];
        segment.clearHavingLesserBackupCountThan(thresholdReplicaIndex);
    }

    private void initPartitionReplica(int partitionId) {
        this.segments[partitionId].init();
    }

    private void clearPartitionReplica(int partitionId) {
        this.segments[partitionId].reset();
    }

    @Override
    public ICacheRecordStore getOrCreateRecordStore(String cacheNameWithPrefix, int partitionId) {
        return this.segments[partitionId].getOrCreateRecordStore(cacheNameWithPrefix);
    }

    @Override
    public ICacheRecordStore getRecordStore(String cacheNameWithPrefix, int partitionId) {
        return this.segments[partitionId].getRecordStore(cacheNameWithPrefix);
    }

    @Override
    public CachePartitionSegment getSegment(int partitionId) {
        return this.segments[partitionId];
    }

    protected void destroySegments(CacheConfig cacheConfig) {
        String name = cacheConfig.getNameWithPrefix();
        for (CachePartitionSegment segment : this.segments) {
            segment.deleteRecordStore(name, true);
        }
    }

    protected void closeSegments(String name) {
        for (CachePartitionSegment segment : this.segments) {
            segment.deleteRecordStore(name, false);
        }
    }

    @Override
    public void deleteCache(String cacheNameWithPrefix, String callerUuid, boolean destroy) {
        CacheConfig config = this.deleteCacheConfig(cacheNameWithPrefix);
        if (config == null) {
            return;
        }
        if (destroy) {
            this.cacheEventHandler.destroy(cacheNameWithPrefix, "<NA>");
            this.destroySegments(config);
        } else {
            this.closeSegments(cacheNameWithPrefix);
        }
        WanReplicationService wanService = this.nodeEngine.getWanReplicationService();
        wanService.removeWanEventCounters("hz:impl:cacheService", cacheNameWithPrefix);
        this.cacheContexts.remove(cacheNameWithPrefix);
        this.operationProviderCache.remove(cacheNameWithPrefix);
        this.deregisterAllListener(cacheNameWithPrefix);
        this.setStatisticsEnabled(config, cacheNameWithPrefix, false);
        this.setManagementEnabled(config, cacheNameWithPrefix, false);
        this.deleteCacheStat(cacheNameWithPrefix);
        this.deleteCacheResources(cacheNameWithPrefix);
    }

    @Override
    public CacheConfig putCacheConfigIfAbsent(CacheConfig config) {
        CacheConfig cacheConfig = PreJoinCacheConfig.asCacheConfig(config);
        CacheConfig localConfig = this.configs.putIfAbsent(cacheConfig.getNameWithPrefix(), cacheConfig);
        if (localConfig == null) {
            if (cacheConfig.isStatisticsEnabled()) {
                this.setStatisticsEnabled(cacheConfig, cacheConfig.getNameWithPrefix(), true);
            }
            if (cacheConfig.isManagementEnabled()) {
                this.setManagementEnabled(cacheConfig, cacheConfig.getNameWithPrefix(), true);
            }
        }
        if (localConfig == null) {
            this.logger.info("Added cache config: " + cacheConfig);
        }
        return localConfig;
    }

    @Override
    public CacheConfig deleteCacheConfig(String cacheNameWithPrefix) {
        CacheConfig config = (CacheConfig)this.configs.remove(cacheNameWithPrefix);
        if (config != null) {
            CacheConfigAccessor.getTenantControl(config).unregister();
            this.logger.info("Removed cache config: " + config);
        }
        return config;
    }

    @Override
    public ExpirationManager getExpirationManager() {
        return this.expirationManager;
    }

    @Override
    public CacheStatisticsImpl createCacheStatIfAbsent(String cacheNameWithPrefix) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statistics, cacheNameWithPrefix, this.cacheStatisticsConstructorFunction);
    }

    public CacheContext getCacheContext(String name) {
        return (CacheContext)this.cacheContexts.get(name);
    }

    @Override
    public CacheContext getOrCreateCacheContext(String cacheNameWithPrefix) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.cacheContexts, cacheNameWithPrefix, this.cacheContextsConstructorFunction);
    }

    @Override
    public void deleteCacheStat(String cacheNameWithPrefix) {
        this.statistics.remove(cacheNameWithPrefix);
    }

    @Override
    public void setStatisticsEnabled(CacheConfig cacheConfig, String cacheNameWithPrefix, boolean enabled) {
        CacheConfig cacheConfig2 = cacheConfig = cacheConfig != null ? cacheConfig : (CacheConfig)this.configs.get(cacheNameWithPrefix);
        if (cacheConfig != null) {
            String cacheManagerName = cacheConfig.getUriString();
            cacheConfig.setStatisticsEnabled(enabled);
            if (enabled) {
                CacheStatisticsImpl cacheStatistics = this.createCacheStatIfAbsent(cacheNameWithPrefix);
                CacheStatisticsMXBeanImpl mxBean = new CacheStatisticsMXBeanImpl(cacheStatistics);
                MXBeanUtil.registerCacheObject(mxBean, cacheManagerName, cacheConfig.getName(), true);
            } else {
                MXBeanUtil.unregisterCacheObject(cacheManagerName, cacheConfig.getName(), true);
                this.deleteCacheStat(cacheNameWithPrefix);
            }
        }
    }

    @Override
    public void setManagementEnabled(CacheConfig cacheConfig, String cacheNameWithPrefix, boolean enabled) {
        CacheConfig cacheConfig2 = cacheConfig = cacheConfig != null ? cacheConfig : (CacheConfig)this.configs.get(cacheNameWithPrefix);
        if (cacheConfig != null) {
            String cacheManagerName = cacheConfig.getUriString();
            cacheConfig.setManagementEnabled(enabled);
            if (enabled) {
                CacheMXBeanImpl mxBean = new CacheMXBeanImpl(cacheConfig);
                MXBeanUtil.registerCacheObject(mxBean, cacheManagerName, cacheConfig.getName(), false);
            } else {
                MXBeanUtil.unregisterCacheObject(cacheManagerName, cacheConfig.getName(), false);
                this.deleteCacheStat(cacheNameWithPrefix);
            }
        }
    }

    @Override
    public CacheConfig getCacheConfig(String cacheNameWithPrefix) {
        return (CacheConfig)this.configs.get(cacheNameWithPrefix);
    }

    @Override
    public CacheConfig findCacheConfig(String simpleName) {
        if (simpleName == null) {
            return null;
        }
        CacheSimpleConfig cacheSimpleConfig = this.nodeEngine.getConfig().findCacheConfigOrNull(simpleName);
        if (cacheSimpleConfig == null) {
            return null;
        }
        try {
            CacheConfig cacheConfig = new CacheConfig(cacheSimpleConfig).setName(simpleName);
            this.setTenantControl(cacheConfig);
            return cacheConfig;
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void setTenantControl(CacheConfig cacheConfig) {
        TenantControlFactory tenantControlFactory;
        block4: {
            if (!TenantControl.NOOP_TENANT_CONTROL.equals(CacheConfigAccessor.getTenantControl(cacheConfig))) {
                return;
            }
            tenantControlFactory = null;
            try {
                tenantControlFactory = ServiceLoader.load(TenantControlFactory.class, TENANT_CONTROL_FACTORY, this.nodeEngine.getConfigClassLoader());
            }
            catch (Exception e) {
                if (!this.logger.isFinestEnabled()) break block4;
                this.logger.finest("Could not load service provider for TenantControl", e);
            }
        }
        if (tenantControlFactory == null) {
            tenantControlFactory = TenantControlFactory.NOOP_TENANT_CONTROL_FACTORY;
        }
        CacheConfigAccessor.setTenantControl(cacheConfig, tenantControlFactory.saveCurrentTenant(new CacheDestroyEventContext(cacheConfig.getName())));
    }

    @Override
    public Collection<CacheConfig> getCacheConfigs() {
        return this.configs.values();
    }

    public Object toObject(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof Data) {
            return this.nodeEngine.toObject(data);
        }
        return data;
    }

    public Data toData(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Data) {
            return (Data)object;
        }
        return this.nodeEngine.getSerializationService().toData(object);
    }

    @Override
    public void publishEvent(CacheEventContext cacheEventContext) {
        this.cacheEventHandler.publishEvent(cacheEventContext);
    }

    @Override
    public void publishEvent(String cacheNameWithPrefix, CacheEventSet eventSet, int orderKey) {
        this.cacheEventHandler.publishEvent(cacheNameWithPrefix, eventSet, orderKey);
    }

    @Override
    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public void dispatchEvent(Object event, CacheEventListener listener) {
        listener.handleEvent(event);
    }

    @Override
    public String registerListener(String cacheNameWithPrefix, CacheEventListener listener, boolean isLocal) {
        return this.registerListenerInternal(cacheNameWithPrefix, listener, null, isLocal);
    }

    @Override
    public String registerListener(String cacheNameWithPrefix, CacheEventListener listener, EventFilter eventFilter, boolean isLocal) {
        return this.registerListenerInternal(cacheNameWithPrefix, listener, eventFilter, isLocal);
    }

    protected String registerListenerInternal(String cacheNameWithPrefix, CacheEventListener listener, EventFilter eventFilter, boolean isLocal) {
        CacheEntryListener cacheEntryListener;
        EventService eventService = this.getNodeEngine().getEventService();
        EventRegistration reg = isLocal ? (eventFilter == null ? eventService.registerLocalListener("hz:impl:cacheService", cacheNameWithPrefix, listener) : eventService.registerLocalListener("hz:impl:cacheService", cacheNameWithPrefix, eventFilter, listener)) : (eventFilter == null ? eventService.registerListener("hz:impl:cacheService", cacheNameWithPrefix, listener) : eventService.registerListener("hz:impl:cacheService", cacheNameWithPrefix, eventFilter, listener));
        String id = reg.getId();
        if (listener instanceof Closeable) {
            this.closeableListeners.put(id, (Closeable)((Object)listener));
        } else if (listener instanceof CacheEntryListenerProvider && (cacheEntryListener = ((CacheEntryListenerProvider)((Object)listener)).getCacheEntryListener()) instanceof Closeable) {
            this.closeableListeners.put(id, (Closeable)cacheEntryListener);
        }
        return id;
    }

    @Override
    public boolean deregisterListener(String cacheNameWithPrefix, String registrationId) {
        EventService eventService = this.getNodeEngine().getEventService();
        boolean result = eventService.deregisterListener("hz:impl:cacheService", cacheNameWithPrefix, registrationId);
        Closeable listener = (Closeable)this.closeableListeners.remove(registrationId);
        if (listener != null) {
            IOUtil.closeResource(listener);
        }
        return result;
    }

    @Override
    public void deregisterAllListener(String cacheNameWithPrefix) {
        EventService eventService = this.getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:impl:cacheService", cacheNameWithPrefix);
        if (registrations != null) {
            for (EventRegistration registration : registrations) {
                Closeable listener = (Closeable)this.closeableListeners.remove(registration.getId());
                if (listener == null) continue;
                IOUtil.closeResource(listener);
            }
        }
        eventService.deregisterAllListeners("hz:impl:cacheService", cacheNameWithPrefix);
        CacheContext cacheContext = (CacheContext)this.cacheContexts.get(cacheNameWithPrefix);
        if (cacheContext != null) {
            cacheContext.resetCacheEntryListenerCount();
            cacheContext.resetInvalidationListenerCount();
        }
    }

    @Override
    public CacheStatisticsImpl getStatistics(String cacheNameWithPrefix) {
        return (CacheStatisticsImpl)this.statistics.get(cacheNameWithPrefix);
    }

    @Override
    public CacheOperationProvider getCacheOperationProvider(String cacheNameWithPrefix, InMemoryFormat inMemoryFormat) {
        if (InMemoryFormat.NATIVE.equals((Object)inMemoryFormat)) {
            throw new IllegalArgumentException("Native memory is available only in Hazelcast Enterprise.Make sure you have Hazelcast Enterprise JARs on your classpath!");
        }
        CacheOperationProvider cacheOperationProvider = (CacheOperationProvider)this.operationProviderCache.get(cacheNameWithPrefix);
        if (cacheOperationProvider != null) {
            return cacheOperationProvider;
        }
        cacheOperationProvider = this.createOperationProvider(cacheNameWithPrefix, inMemoryFormat);
        CacheOperationProvider current = this.operationProviderCache.putIfAbsent(cacheNameWithPrefix, cacheOperationProvider);
        return current == null ? cacheOperationProvider : current;
    }

    protected abstract CacheOperationProvider createOperationProvider(String var1, InMemoryFormat var2);

    public void addCacheResource(String cacheNameWithPrefix, Closeable resource) {
        Set<Closeable> cacheResources = ConcurrencyUtil.getOrPutSynchronized(this.resources, cacheNameWithPrefix, this.cacheResourcesMutexFactory, this.cacheResourcesConstructorFunction);
        cacheResources.add(resource);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deleteCacheResources(String name) {
        Set cacheResources;
        try (ContextMutexFactory.Mutex mutex = this.cacheResourcesMutexFactory.mutexFor(name);){
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                cacheResources = (Set)this.resources.remove(name);
            }
        }
        if (cacheResources != null) {
            for (Closeable resource : cacheResources) {
                IOUtil.closeResource(resource);
            }
            cacheResources.clear();
        }
    }

    @Override
    public Operation getPreJoinOperation() {
        OnJoinCacheOperation preJoinCacheOperation = new OnJoinCacheOperation();
        for (Map.Entry cacheConfigEntry : this.configs.entrySet()) {
            PreJoinCacheConfig cacheConfig = new PreJoinCacheConfig((CacheConfig)cacheConfigEntry.getValue(), false);
            preJoinCacheOperation.addCacheConfig(cacheConfig);
        }
        return preJoinCacheOperation;
    }

    protected void publishCachePartitionLostEvent(String cacheName, int partitionId) {
        LinkedList<EventRegistration> registrations = new LinkedList<EventRegistration>();
        for (EventRegistration registration : this.getRegistrations(cacheName)) {
            if (!(registration.getFilter() instanceof CachePartitionLostEventFilter)) continue;
            registrations.add(registration);
        }
        if (registrations.isEmpty()) {
            return;
        }
        Member member = this.nodeEngine.getLocalMember();
        CachePartitionEventData eventData = new CachePartitionEventData(cacheName, partitionId, member);
        EventService eventService = this.nodeEngine.getEventService();
        eventService.publishEvent("hz:impl:cacheService", registrations, (Object)eventData, partitionId);
    }

    Collection<EventRegistration> getRegistrations(String cacheName) {
        EventService eventService = this.nodeEngine.getEventService();
        return eventService.getRegistrations("hz:impl:cacheService", cacheName);
    }

    @Override
    public void onPartitionLost(IPartitionLostEvent partitionLostEvent) {
        int partitionId = partitionLostEvent.getPartitionId();
        for (CacheConfig config : this.getCacheConfigs()) {
            String cacheName = config.getName();
            if (config.getTotalBackupCount() > partitionLostEvent.getLostReplicaIndex()) continue;
            this.publishCachePartitionLostEvent(cacheName, partitionId);
        }
    }

    public void cacheEntryListenerRegistered(String name, CacheEntryListenerConfiguration cacheEntryListenerConfiguration) {
        CacheConfig cacheConfig = this.getCacheConfig(name);
        if (cacheConfig == null) {
            throw new IllegalStateException("CacheConfig does not exist for cache " + name);
        }
        cacheConfig.addCacheEntryListenerConfiguration(cacheEntryListenerConfiguration);
    }

    public void cacheEntryListenerDeregistered(String name, CacheEntryListenerConfiguration cacheEntryListenerConfiguration) {
        CacheConfig cacheConfig = this.getCacheConfig(name);
        if (cacheConfig == null) {
            throw new IllegalStateException("CacheConfig does not exist for cache " + name);
        }
        cacheConfig.removeCacheEntryListenerConfiguration(cacheEntryListenerConfiguration);
    }

    @Override
    public String getQuorumName(String cacheName) {
        CacheConfig cacheConfig = (CacheConfig)this.configs.get(cacheName);
        if (cacheConfig == null) {
            return null;
        }
        return cacheConfig.getQuorumName();
    }

    @Override
    public String addInvalidationListener(String cacheNameWithPrefix, CacheEventListener listener, boolean localOnly) {
        EventService eventService = this.nodeEngine.getEventService();
        EventRegistration registration = localOnly ? eventService.registerLocalListener("hz:impl:cacheService", cacheNameWithPrefix, listener) : eventService.registerListener("hz:impl:cacheService", cacheNameWithPrefix, listener);
        return registration.getId();
    }

    @Override
    public void sendInvalidationEvent(String cacheNameWithPrefix, Data key, String sourceUuid) {
        this.cacheEventHandler.sendInvalidationEvent(cacheNameWithPrefix, key, sourceUuid);
    }

    @Override
    public Runnable prepareMergeRunnable() {
        return this.splitBrainHandlerService.prepareMergeRunnable();
    }

    public CacheEventHandler getCacheEventHandler() {
        return this.cacheEventHandler;
    }

    @Override
    public CacheEventJournal getEventJournal() {
        return this.eventJournal;
    }

    @Override
    public <K, V> void createCacheConfigOnAllMembers(PreJoinCacheConfig<K, V> cacheConfig) {
        ICompletableFuture future = this.createCacheConfigOnAllMembersAsync(cacheConfig);
        FutureUtil.waitForever(Collections.singleton(future), FutureUtil.RETHROW_EVERYTHING);
    }

    public <K, V> ICompletableFuture createCacheConfigOnAllMembersAsync(PreJoinCacheConfig<K, V> cacheConfig) {
        return InvocationUtil.invokeOnStableClusterSerial(this.getNodeEngine(), new AddCacheConfigOperationSupplier(cacheConfig), 100);
    }

    @Override
    public void onClusterStateChange(ClusterState newState) {
        ExpirationManager expManager = this.expirationManager;
        if (expManager != null) {
            expManager.onClusterStateChange(newState);
        }
    }
}

