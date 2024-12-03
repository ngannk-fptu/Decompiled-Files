/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConsistencyCheckStrategy;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.eviction.LFUEvictionPolicy;
import com.hazelcast.map.eviction.LRUEvictionPolicy;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.map.eviction.RandomEvictionPolicy;
import com.hazelcast.map.impl.InterceptorRegistry;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.eviction.EvictionChecker;
import com.hazelcast.map.impl.eviction.Evictor;
import com.hazelcast.map.impl.eviction.EvictorImpl;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.MapStoreContextFactory;
import com.hazelcast.map.impl.query.QueryEntryFactory;
import com.hazelcast.map.impl.record.DataRecordFactory;
import com.hazelcast.map.impl.record.ObjectRecordFactory;
import com.hazelcast.map.impl.record.RecordFactory;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MemoryInfoAccessor;
import com.hazelcast.util.RuntimeMemoryInfoAccessor;
import com.hazelcast.wan.WanReplicationPublisher;
import com.hazelcast.wan.WanReplicationService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapContainer {
    protected final boolean addEventPublishingEnabled;
    protected final String name;
    protected final String quorumName;
    protected final Indexes globalIndexes;
    protected final Extractors extractors;
    protected final MapStoreContext mapStoreContext;
    protected final ObjectNamespace objectNamespace;
    protected final MapServiceContext mapServiceContext;
    protected final QueryEntryFactory queryEntryFactory;
    protected final EventJournalConfig eventJournalConfig;
    protected final PartitioningStrategy partitioningStrategy;
    protected final InternalSerializationService serializationService;
    protected final InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
    protected final IFunction<Object, Data> toDataFunction = new ObjectToData();
    protected final ConstructorFunction<Void, RecordFactory> recordFactoryConstructor;
    protected final AtomicInteger invalidationListenerCount = new AtomicInteger();
    protected Object wanMergePolicy;
    protected WanReplicationPublisher wanReplicationPublisher;
    protected volatile Evictor evictor;
    protected volatile MapConfig mapConfig;
    private boolean persistWanReplicatedData;

    public MapContainer(String name, Config config, MapServiceContext mapServiceContext) {
        this.name = name;
        this.mapConfig = config.findMapConfig(name);
        this.eventJournalConfig = config.findMapEventJournalConfig(name);
        this.mapServiceContext = mapServiceContext;
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        this.partitioningStrategy = this.createPartitioningStrategy();
        this.quorumName = this.mapConfig.getQuorumName();
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.recordFactoryConstructor = this.createRecordFactoryConstructor(this.serializationService);
        this.objectNamespace = MapService.getObjectNamespace(name);
        this.initWanReplication(nodeEngine);
        ClassLoader classloader = mapServiceContext.getNodeEngine().getConfigClassLoader();
        this.extractors = Extractors.newBuilder(this.serializationService).setMapAttributeConfigs(this.mapConfig.getMapAttributeConfigs()).setClassLoader(classloader).build();
        this.queryEntryFactory = new QueryEntryFactory(this.mapConfig.getCacheDeserializedValues(), this.serializationService, this.extractors);
        this.globalIndexes = this.shouldUseGlobalIndex(this.mapConfig) ? this.createIndexes(true) : null;
        this.addEventPublishingEnabled = nodeEngine.getProperties().getBoolean(GroupProperty.MAP_LOAD_ALL_PUBLISHES_ADDED_EVENT);
        this.mapStoreContext = MapStoreContextFactory.createMapStoreContext(this);
        this.mapStoreContext.start();
        this.initEvictor();
    }

    public Indexes createIndexes(boolean global) {
        return Indexes.newBuilder(this.serializationService, this.mapServiceContext.getIndexCopyBehavior()).global(global).extractors(this.extractors).statsEnabled(this.mapConfig.isStatisticsEnabled()).indexProvider(this.mapServiceContext.getIndexProvider(this.mapConfig)).usesCachedQueryableEntries(this.mapConfig.getCacheDeserializedValues() != CacheDeserializedValues.NEVER).build();
    }

    public boolean isAddEventPublishingEnabled() {
        return this.addEventPublishingEnabled;
    }

    public void initEvictor() {
        MapEvictionPolicy mapEvictionPolicy = this.getMapEvictionPolicy();
        if (mapEvictionPolicy == null) {
            this.evictor = Evictor.NULL_EVICTOR;
        } else {
            MemoryInfoAccessor memoryInfoAccessor = MapContainer.getMemoryInfoAccessor();
            EvictionChecker evictionChecker = new EvictionChecker(memoryInfoAccessor, this.mapServiceContext);
            NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
            IPartitionService partitionService = nodeEngine.getPartitionService();
            int batchSize = nodeEngine.getProperties().getInteger(GroupProperty.MAP_EVICTION_BATCH_SIZE);
            this.evictor = new EvictorImpl(mapEvictionPolicy, evictionChecker, partitionService, batchSize);
        }
    }

    public MapEvictionPolicy getMapEvictionPolicy() {
        MapEvictionPolicy mapEvictionPolicy = this.mapConfig.getMapEvictionPolicy();
        if (mapEvictionPolicy != null) {
            return mapEvictionPolicy;
        }
        EvictionPolicy evictionPolicy = this.mapConfig.getEvictionPolicy();
        if (evictionPolicy == null) {
            return null;
        }
        switch (evictionPolicy) {
            case LRU: {
                return LRUEvictionPolicy.INSTANCE;
            }
            case LFU: {
                return LFUEvictionPolicy.INSTANCE;
            }
            case RANDOM: {
                return RandomEvictionPolicy.INSTANCE;
            }
            case NONE: {
                return null;
            }
        }
        throw new IllegalArgumentException("Not known eviction policy: " + (Object)((Object)evictionPolicy));
    }

    protected boolean shouldUseGlobalIndex(MapConfig mapConfig) {
        return !mapConfig.getInMemoryFormat().equals((Object)InMemoryFormat.NATIVE);
    }

    protected static MemoryInfoAccessor getMemoryInfoAccessor() {
        MemoryInfoAccessor pluggedMemoryInfoAccessor = MapContainer.getPluggedMemoryInfoAccessor();
        return pluggedMemoryInfoAccessor != null ? pluggedMemoryInfoAccessor : new RuntimeMemoryInfoAccessor();
    }

    private static MemoryInfoAccessor getPluggedMemoryInfoAccessor() {
        String memoryInfoAccessorImpl = System.getProperty("hazelcast.memory.info.accessor.impl");
        if (memoryInfoAccessorImpl == null) {
            return null;
        }
        try {
            return (MemoryInfoAccessor)ClassLoaderUtil.newInstance(null, memoryInfoAccessorImpl);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    ConstructorFunction<Void, RecordFactory> createRecordFactoryConstructor(final SerializationService serializationService) {
        return new ConstructorFunction<Void, RecordFactory>(){

            @Override
            public RecordFactory createNew(Void notUsedArg) {
                switch (MapContainer.this.mapConfig.getInMemoryFormat()) {
                    case BINARY: {
                        return new DataRecordFactory(MapContainer.this.mapConfig, serializationService, MapContainer.this.partitioningStrategy);
                    }
                    case OBJECT: {
                        return new ObjectRecordFactory(MapContainer.this.mapConfig, serializationService);
                    }
                }
                throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)MapContainer.this.mapConfig.getInMemoryFormat()));
            }
        };
    }

    public void initWanReplication(NodeEngine nodeEngine) {
        WanConsumerConfig wanConsumerConfig;
        WanReplicationRef wanReplicationRef = this.mapConfig.getWanReplicationRef();
        if (wanReplicationRef == null) {
            return;
        }
        String wanReplicationRefName = wanReplicationRef.getName();
        Config config = nodeEngine.getConfig();
        if (!config.findMapMerkleTreeConfig(this.name).isEnabled() && this.hasPublisherWithMerkleTreeSync(config, wanReplicationRefName)) {
            throw new InvalidConfigurationException("Map " + this.name + " has disabled merkle trees but the WAN replication scheme " + wanReplicationRefName + " has publishers that use merkle trees. Please enable merkle trees for the map.");
        }
        WanReplicationService wanReplicationService = nodeEngine.getWanReplicationService();
        this.wanReplicationPublisher = wanReplicationService.getWanReplicationPublisher(wanReplicationRefName);
        this.wanMergePolicy = this.mapServiceContext.getMergePolicyProvider().getMergePolicy(wanReplicationRef.getMergePolicy());
        WanReplicationConfig wanReplicationConfig = config.getWanReplicationConfig(wanReplicationRefName);
        if (wanReplicationConfig != null && (wanConsumerConfig = wanReplicationConfig.getWanConsumerConfig()) != null) {
            this.persistWanReplicatedData = wanConsumerConfig.isPersistWanReplicatedData();
        }
    }

    private boolean hasPublisherWithMerkleTreeSync(Config config, String wanReplicationRefName) {
        WanReplicationConfig replicationConfig = config.getWanReplicationConfig(wanReplicationRefName);
        if (replicationConfig != null) {
            for (WanPublisherConfig publisherConfig : replicationConfig.getWanPublisherConfigs()) {
                if (publisherConfig.getWanSyncConfig() == null || !ConsistencyCheckStrategy.MERKLE_TREES.equals((Object)publisherConfig.getWanSyncConfig().getConsistencyCheckStrategy())) continue;
                return true;
            }
        }
        return false;
    }

    private PartitioningStrategy createPartitioningStrategy() {
        return this.mapServiceContext.getPartitioningStrategy(this.mapConfig.getName(), this.mapConfig.getPartitioningStrategyConfig());
    }

    public Indexes getIndexes() {
        return this.globalIndexes;
    }

    public Indexes getIndexes(int partitionId) {
        if (this.globalIndexes != null) {
            return this.globalIndexes;
        }
        return this.mapServiceContext.getPartitionContainer(partitionId).getIndexes(this.name);
    }

    public boolean isGlobalIndexEnabled() {
        return this.globalIndexes != null;
    }

    public WanReplicationPublisher getWanReplicationPublisher() {
        return this.wanReplicationPublisher;
    }

    public Object getWanMergePolicy() {
        return this.wanMergePolicy;
    }

    public boolean isWanReplicationEnabled() {
        return this.wanReplicationPublisher != null && this.wanMergePolicy != null;
    }

    public boolean isWanRepublishingEnabled() {
        return this.isWanReplicationEnabled() && this.mapConfig.getWanReplicationRef().isRepublishingEnabled();
    }

    public void checkWanReplicationQueues() {
        if (this.isWanReplicationEnabled()) {
            this.wanReplicationPublisher.checkWanReplicationQueues();
        }
    }

    public int getTotalBackupCount() {
        return this.getBackupCount() + this.getAsyncBackupCount();
    }

    public int getBackupCount() {
        return this.mapConfig.getBackupCount();
    }

    public int getAsyncBackupCount() {
        return this.mapConfig.getAsyncBackupCount();
    }

    public PartitioningStrategy getPartitioningStrategy() {
        return this.partitioningStrategy;
    }

    public MapServiceContext getMapServiceContext() {
        return this.mapServiceContext;
    }

    public MapStoreContext getMapStoreContext() {
        return this.mapStoreContext;
    }

    public MapConfig getMapConfig() {
        return this.mapConfig;
    }

    public void setMapConfig(MapConfig mapConfig) {
        this.mapConfig = mapConfig;
    }

    public EventJournalConfig getEventJournalConfig() {
        return this.eventJournalConfig;
    }

    public String getName() {
        return this.name;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public IFunction<Object, Data> toData() {
        return this.toDataFunction;
    }

    public ConstructorFunction<Void, RecordFactory> getRecordFactoryConstructor() {
        return this.recordFactoryConstructor;
    }

    public QueryableEntry newQueryEntry(Data key, Object value) {
        return this.queryEntryFactory.newEntry(key, value);
    }

    public Evictor getEvictor() {
        return this.evictor;
    }

    public void setEvictor(Evictor evictor) {
        this.evictor = evictor;
    }

    public Extractors getExtractors() {
        return this.extractors;
    }

    public boolean hasInvalidationListener() {
        return this.invalidationListenerCount.get() > 0;
    }

    public void increaseInvalidationListenerCount() {
        this.invalidationListenerCount.incrementAndGet();
    }

    public void decreaseInvalidationListenerCount() {
        this.invalidationListenerCount.decrementAndGet();
    }

    public InterceptorRegistry getInterceptorRegistry() {
        return this.interceptorRegistry;
    }

    public void onDestroy() {
    }

    public boolean shouldCloneOnEntryProcessing(int partitionId) {
        return this.getIndexes(partitionId).haveAtLeastOneIndex() && InMemoryFormat.OBJECT.equals((Object)this.mapConfig.getInMemoryFormat());
    }

    public ObjectNamespace getObjectNamespace() {
        return this.objectNamespace;
    }

    public Map<String, Boolean> getIndexDefinitions() {
        HashMap<String, Boolean> definitions = new HashMap<String, Boolean>();
        if (this.isGlobalIndexEnabled()) {
            for (InternalIndex index : this.globalIndexes.getIndexes()) {
                definitions.put(index.getName(), index.isOrdered());
            }
        } else {
            for (PartitionContainer container : this.mapServiceContext.getPartitionContainers()) {
                for (InternalIndex index : container.getIndexes(this.name).getIndexes()) {
                    definitions.put(index.getName(), index.isOrdered());
                }
            }
        }
        return definitions;
    }

    public boolean isPersistWanReplicatedData() {
        return this.persistWanReplicatedData;
    }

    @SerializableByConvention
    private class ObjectToData
    implements IFunction<Object, Data> {
        private ObjectToData() {
        }

        @Override
        public Data apply(Object input) {
            SerializationService ss = MapContainer.this.mapStoreContext.getSerializationService();
            return ss.toData(input, MapContainer.this.partitioningStrategy);
        }
    }
}

