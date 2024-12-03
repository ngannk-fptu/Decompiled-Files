/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.ClusterVersionListener;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.config.ConfigUtils;
import com.hazelcast.internal.dynamicconfig.AddDynamicConfigOperationSupplier;
import com.hazelcast.internal.dynamicconfig.ConfigCheckMode;
import com.hazelcast.internal.dynamicconfig.ConfigurationService;
import com.hazelcast.internal.dynamicconfig.DynamicConfigListener;
import com.hazelcast.internal.dynamicconfig.DynamicConfigPreJoinOperation;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.function.Supplier;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class ClusterWideConfigurationService
implements PreJoinAwareService,
CoreService,
ClusterVersionListener,
ManagedService,
ConfigurationService,
SplitBrainHandlerService {
    public static final String SERVICE_NAME = "configuration-service";
    public static final int CONFIG_PUBLISH_MAX_ATTEMPT_COUNT = 100;
    static final Map<Class<? extends IdentifiedDataSerializable>, Version> CONFIG_TO_VERSION;
    private static final boolean IGNORE_CONFLICTING_CONFIGS_WORKAROUND;
    private final DynamicConfigListener listener;
    private final Object journalMutex = new Object();
    private NodeEngine nodeEngine;
    private final ConcurrentMap<String, MapConfig> mapConfigs = new ConcurrentHashMap<String, MapConfig>();
    private final ConcurrentMap<String, MultiMapConfig> multiMapConfigs = new ConcurrentHashMap<String, MultiMapConfig>();
    private final ConcurrentMap<String, CardinalityEstimatorConfig> cardinalityEstimatorConfigs = new ConcurrentHashMap<String, CardinalityEstimatorConfig>();
    private final ConcurrentMap<String, PNCounterConfig> pnCounterConfigs = new ConcurrentHashMap<String, PNCounterConfig>();
    private final ConcurrentMap<String, RingbufferConfig> ringbufferConfigs = new ConcurrentHashMap<String, RingbufferConfig>();
    private final ConcurrentMap<String, AtomicLongConfig> atomicLongConfigs = new ConcurrentHashMap<String, AtomicLongConfig>();
    private final ConcurrentMap<String, AtomicReferenceConfig> atomicReferenceConfigs = new ConcurrentHashMap<String, AtomicReferenceConfig>();
    private final ConcurrentMap<String, CountDownLatchConfig> countDownLatchConfigs = new ConcurrentHashMap<String, CountDownLatchConfig>();
    private final ConcurrentMap<String, LockConfig> lockConfigs = new ConcurrentHashMap<String, LockConfig>();
    private final ConcurrentMap<String, ListConfig> listConfigs = new ConcurrentHashMap<String, ListConfig>();
    private final ConcurrentMap<String, SetConfig> setConfigs = new ConcurrentHashMap<String, SetConfig>();
    private final ConcurrentMap<String, ReplicatedMapConfig> replicatedMapConfigs = new ConcurrentHashMap<String, ReplicatedMapConfig>();
    private final ConcurrentMap<String, TopicConfig> topicConfigs = new ConcurrentHashMap<String, TopicConfig>();
    private final ConcurrentMap<String, ExecutorConfig> executorConfigs = new ConcurrentHashMap<String, ExecutorConfig>();
    private final ConcurrentMap<String, DurableExecutorConfig> durableExecutorConfigs = new ConcurrentHashMap<String, DurableExecutorConfig>();
    private final ConcurrentMap<String, ScheduledExecutorConfig> scheduledExecutorConfigs = new ConcurrentHashMap<String, ScheduledExecutorConfig>();
    private final ConcurrentMap<String, SemaphoreConfig> semaphoreConfigs = new ConcurrentHashMap<String, SemaphoreConfig>();
    private final ConcurrentMap<String, QueueConfig> queueConfigs = new ConcurrentHashMap<String, QueueConfig>();
    private final ConcurrentMap<String, ReliableTopicConfig> reliableTopicConfigs = new ConcurrentHashMap<String, ReliableTopicConfig>();
    private final ConcurrentMap<String, CacheSimpleConfig> cacheSimpleConfigs = new ConcurrentHashMap<String, CacheSimpleConfig>();
    private final ConcurrentMap<String, EventJournalConfig> cacheEventJournalConfigs = new ConcurrentHashMap<String, EventJournalConfig>();
    private final ConcurrentMap<String, EventJournalConfig> mapEventJournalConfigs = new ConcurrentHashMap<String, EventJournalConfig>();
    private final ConcurrentMap<String, MerkleTreeConfig> mapMerkleTreeConfigs = new ConcurrentHashMap<String, MerkleTreeConfig>();
    private final ConcurrentMap<String, FlakeIdGeneratorConfig> flakeIdGeneratorConfigs = new ConcurrentHashMap<String, FlakeIdGeneratorConfig>();
    private final ConfigPatternMatcher configPatternMatcher;
    private final ILogger logger;
    private final Map<?, ? extends IdentifiedDataSerializable>[] allConfigurations = new Map[]{this.mapConfigs, this.multiMapConfigs, this.cardinalityEstimatorConfigs, this.ringbufferConfigs, this.lockConfigs, this.listConfigs, this.setConfigs, this.atomicLongConfigs, this.atomicReferenceConfigs, this.countDownLatchConfigs, this.replicatedMapConfigs, this.topicConfigs, this.executorConfigs, this.durableExecutorConfigs, this.scheduledExecutorConfigs, this.semaphoreConfigs, this.queueConfigs, this.reliableTopicConfigs, this.cacheSimpleConfigs, this.cacheEventJournalConfigs, this.mapEventJournalConfigs, this.mapMerkleTreeConfigs, this.flakeIdGeneratorConfigs, this.pnCounterConfigs};
    private volatile Version version;

    public ClusterWideConfigurationService(NodeEngine nodeEngine, DynamicConfigListener dynamicConfigListener) {
        this.nodeEngine = nodeEngine;
        this.listener = dynamicConfigListener;
        this.configPatternMatcher = nodeEngine.getConfig().getConfigPatternMatcher();
        this.logger = nodeEngine.getLogger(ClusterWideConfigurationService.class);
    }

    @Override
    public Operation getPreJoinOperation() {
        IdentifiedDataSerializable[] allConfigurations = this.collectAllDynamicConfigs();
        if (this.noConfigurationExist(allConfigurations)) {
            return null;
        }
        return new DynamicConfigPreJoinOperation(allConfigurations, ConfigCheckMode.WARNING);
    }

    private boolean noConfigurationExist(IdentifiedDataSerializable[] configurations) {
        return configurations.length == 0;
    }

    private IdentifiedDataSerializable[] collectAllDynamicConfigs() {
        ArrayList<? extends IdentifiedDataSerializable> all = new ArrayList<IdentifiedDataSerializable>();
        for (Map<?, IdentifiedDataSerializable> map : this.allConfigurations) {
            Collection<? extends IdentifiedDataSerializable> values = map.values();
            all.addAll(values);
        }
        return all.toArray(new IdentifiedDataSerializable[0]);
    }

    @Override
    public void onClusterVersionChange(Version newVersion) {
        this.version = newVersion;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.listener.onServiceInitialized(this);
    }

    @Override
    public void reset() {
        for (Map<?, IdentifiedDataSerializable> map : this.allConfigurations) {
            map.clear();
        }
    }

    @Override
    public void shutdown(boolean terminate) {
    }

    @Override
    public void broadcastConfig(IdentifiedDataSerializable config) {
        ICompletableFuture<Object> future = this.broadcastConfigAsync(config);
        try {
            future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExceptionUtil.rethrow(e);
        }
        catch (ExecutionException e) {
            ExceptionUtil.rethrow(e);
        }
    }

    public ICompletableFuture<Object> broadcastConfigAsync(IdentifiedDataSerializable config) {
        this.checkConfigVersion(config);
        IdentifiedDataSerializable clonedConfig = this.cloneConfig(config);
        ClusterService clusterService = this.nodeEngine.getClusterService();
        return InvocationUtil.invokeOnStableClusterSerial(this.nodeEngine, new AddDynamicConfigOperationSupplier(clusterService, clonedConfig), 100);
    }

    private void checkConfigVersion(IdentifiedDataSerializable config) {
        Version currentClusterVersion = this.version;
        Class<?> configClass = config.getClass();
        Version introducedIn = CONFIG_TO_VERSION.get(configClass);
        if (currentClusterVersion.isLessThan(introducedIn)) {
            throw new UnsupportedOperationException(String.format("Config '%s' is available since version '%s'. Current cluster version '%s' does not allow dynamically adding '%1$s'.", configClass.getSimpleName(), introducedIn.toString(), currentClusterVersion.toString()));
        }
    }

    private IdentifiedDataSerializable cloneConfig(IdentifiedDataSerializable config) {
        SerializationService serializationService = this.nodeEngine.getSerializationService();
        Object data = serializationService.toData(config);
        return (IdentifiedDataSerializable)serializationService.toObject(data);
    }

    public void registerConfigLocally(IdentifiedDataSerializable newConfig, ConfigCheckMode configCheckMode) {
        IdentifiedDataSerializable currentConfig = null;
        if (newConfig instanceof MultiMapConfig) {
            MultiMapConfig multiMapConfig = (MultiMapConfig)newConfig;
            currentConfig = this.multiMapConfigs.putIfAbsent(multiMapConfig.getName(), multiMapConfig);
        } else if (newConfig instanceof MapConfig) {
            MapConfig newMapConfig = (MapConfig)newConfig;
            currentConfig = this.mapConfigs.putIfAbsent(newMapConfig.getName(), newMapConfig);
            if (currentConfig == null) {
                this.listener.onConfigRegistered(newMapConfig);
            }
        } else if (newConfig instanceof CardinalityEstimatorConfig) {
            CardinalityEstimatorConfig cardinalityEstimatorConfig = (CardinalityEstimatorConfig)newConfig;
            currentConfig = this.cardinalityEstimatorConfigs.putIfAbsent(cardinalityEstimatorConfig.getName(), cardinalityEstimatorConfig);
        } else if (newConfig instanceof RingbufferConfig) {
            RingbufferConfig ringbufferConfig = (RingbufferConfig)newConfig;
            currentConfig = this.ringbufferConfigs.putIfAbsent(ringbufferConfig.getName(), ringbufferConfig);
        } else if (newConfig instanceof LockConfig) {
            LockConfig lockConfig = (LockConfig)newConfig;
            currentConfig = this.lockConfigs.putIfAbsent(lockConfig.getName(), lockConfig);
        } else if (newConfig instanceof AtomicLongConfig) {
            AtomicLongConfig atomicLongConfig = (AtomicLongConfig)newConfig;
            currentConfig = this.atomicLongConfigs.putIfAbsent(atomicLongConfig.getName(), atomicLongConfig);
        } else if (newConfig instanceof AtomicReferenceConfig) {
            AtomicReferenceConfig atomicReferenceConfig = (AtomicReferenceConfig)newConfig;
            currentConfig = this.atomicReferenceConfigs.putIfAbsent(atomicReferenceConfig.getName(), atomicReferenceConfig);
        } else if (newConfig instanceof CountDownLatchConfig) {
            CountDownLatchConfig countDownLatchConfig = (CountDownLatchConfig)newConfig;
            currentConfig = this.countDownLatchConfigs.putIfAbsent(countDownLatchConfig.getName(), countDownLatchConfig);
        } else if (newConfig instanceof ListConfig) {
            ListConfig listConfig = (ListConfig)newConfig;
            currentConfig = this.listConfigs.putIfAbsent(listConfig.getName(), listConfig);
        } else if (newConfig instanceof SetConfig) {
            SetConfig setConfig = (SetConfig)newConfig;
            currentConfig = this.setConfigs.putIfAbsent(setConfig.getName(), setConfig);
        } else if (newConfig instanceof ReplicatedMapConfig) {
            ReplicatedMapConfig replicatedMapConfig = (ReplicatedMapConfig)newConfig;
            currentConfig = this.replicatedMapConfigs.putIfAbsent(replicatedMapConfig.getName(), replicatedMapConfig);
        } else if (newConfig instanceof TopicConfig) {
            TopicConfig topicConfig = (TopicConfig)newConfig;
            currentConfig = this.topicConfigs.putIfAbsent(topicConfig.getName(), topicConfig);
        } else if (newConfig instanceof ExecutorConfig) {
            ExecutorConfig executorConfig = (ExecutorConfig)newConfig;
            currentConfig = this.executorConfigs.putIfAbsent(executorConfig.getName(), executorConfig);
        } else if (newConfig instanceof DurableExecutorConfig) {
            DurableExecutorConfig durableExecutorConfig = (DurableExecutorConfig)newConfig;
            currentConfig = this.durableExecutorConfigs.putIfAbsent(durableExecutorConfig.getName(), durableExecutorConfig);
        } else if (newConfig instanceof ScheduledExecutorConfig) {
            ScheduledExecutorConfig scheduledExecutorConfig = (ScheduledExecutorConfig)newConfig;
            currentConfig = this.scheduledExecutorConfigs.putIfAbsent(scheduledExecutorConfig.getName(), scheduledExecutorConfig);
        } else if (newConfig instanceof QueueConfig) {
            QueueConfig queueConfig = (QueueConfig)newConfig;
            currentConfig = this.queueConfigs.putIfAbsent(queueConfig.getName(), queueConfig);
        } else if (newConfig instanceof ReliableTopicConfig) {
            ReliableTopicConfig reliableTopicConfig = (ReliableTopicConfig)newConfig;
            currentConfig = this.reliableTopicConfigs.putIfAbsent(reliableTopicConfig.getName(), reliableTopicConfig);
        } else if (newConfig instanceof CacheSimpleConfig) {
            CacheSimpleConfig cacheSimpleConfig = (CacheSimpleConfig)newConfig;
            currentConfig = this.cacheSimpleConfigs.putIfAbsent(cacheSimpleConfig.getName(), cacheSimpleConfig);
            if (currentConfig == null) {
                this.listener.onConfigRegistered(cacheSimpleConfig);
            }
        } else if (newConfig instanceof EventJournalConfig) {
            EventJournalConfig eventJournalConfig = (EventJournalConfig)newConfig;
            this.registerEventJournalConfig(eventJournalConfig, configCheckMode);
        } else if (newConfig instanceof MerkleTreeConfig) {
            MerkleTreeConfig config = (MerkleTreeConfig)newConfig;
            currentConfig = this.mapMerkleTreeConfigs.putIfAbsent(config.getMapName(), config);
        } else if (newConfig instanceof SemaphoreConfig) {
            SemaphoreConfig semaphoreConfig = (SemaphoreConfig)newConfig;
            currentConfig = this.semaphoreConfigs.putIfAbsent(semaphoreConfig.getName(), semaphoreConfig);
        } else if (newConfig instanceof FlakeIdGeneratorConfig) {
            FlakeIdGeneratorConfig config = (FlakeIdGeneratorConfig)newConfig;
            currentConfig = this.flakeIdGeneratorConfigs.putIfAbsent(config.getName(), config);
        } else if (newConfig instanceof PNCounterConfig) {
            PNCounterConfig config = (PNCounterConfig)newConfig;
            currentConfig = this.pnCounterConfigs.putIfAbsent(config.getName(), config);
        } else {
            throw new UnsupportedOperationException("Unsupported config type: " + newConfig);
        }
        this.checkCurrentConfigNullOrEqual(configCheckMode, currentConfig, newConfig);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerEventJournalConfig(EventJournalConfig eventJournalConfig, ConfigCheckMode configCheckMode) {
        String mapName = eventJournalConfig.getMapName();
        String cacheName = eventJournalConfig.getCacheName();
        Object object = this.journalMutex;
        synchronized (object) {
            EventJournalConfig currentMapJournalConfig = null;
            if (mapName != null) {
                currentMapJournalConfig = this.mapEventJournalConfigs.putIfAbsent(mapName, eventJournalConfig);
                this.checkCurrentConfigNullOrEqual(configCheckMode, currentMapJournalConfig, eventJournalConfig);
            }
            if (cacheName != null) {
                EventJournalConfig currentCacheJournalConfig = this.cacheEventJournalConfigs.putIfAbsent(cacheName, eventJournalConfig);
                try {
                    this.checkCurrentConfigNullOrEqual(configCheckMode, currentCacheJournalConfig, eventJournalConfig);
                }
                catch (ConfigurationException e) {
                    if (mapName != null && currentMapJournalConfig == null) {
                        this.mapEventJournalConfigs.remove(mapName);
                    }
                    throw e;
                }
            }
        }
    }

    private void checkCurrentConfigNullOrEqual(ConfigCheckMode checkMode, Object currentConfig, Object newConfig) {
        if (IGNORE_CONFLICTING_CONFIGS_WORKAROUND) {
            return;
        }
        if (currentConfig == null) {
            return;
        }
        if (!currentConfig.equals(newConfig)) {
            String message = "Cannot add a dynamic configuration '" + newConfig + "' as there is already a conflicting configuration '" + currentConfig + "'";
            switch (checkMode) {
                case THROW_EXCEPTION: {
                    throw new ConfigurationException(message);
                }
                case WARNING: {
                    this.logger.warning(message);
                    break;
                }
                case SILENT: {
                    this.logger.finest(message);
                    break;
                }
                default: {
                    throw new UnsupportedOperationException("Unknown consistency check mode: " + (Object)((Object)checkMode));
                }
            }
        }
    }

    @Override
    public MultiMapConfig findMultiMapConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.multiMapConfigs, name);
    }

    public ConcurrentMap<String, MultiMapConfig> getMultiMapConfigs() {
        return this.multiMapConfigs;
    }

    @Override
    public MapConfig findMapConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapConfigs, name);
    }

    @Override
    public Map<String, MapConfig> getMapConfigs() {
        return this.mapConfigs;
    }

    @Override
    public TopicConfig findTopicConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.topicConfigs, name);
    }

    public ConcurrentMap<String, TopicConfig> getTopicConfigs() {
        return this.topicConfigs;
    }

    @Override
    public CardinalityEstimatorConfig findCardinalityEstimatorConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cardinalityEstimatorConfigs, name);
    }

    public ConcurrentMap<String, CardinalityEstimatorConfig> getCardinalityEstimatorConfigs() {
        return this.cardinalityEstimatorConfigs;
    }

    @Override
    public PNCounterConfig findPNCounterConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.pnCounterConfigs, name);
    }

    public ConcurrentMap<String, PNCounterConfig> getPNCounterConfigs() {
        return this.pnCounterConfigs;
    }

    @Override
    public ExecutorConfig findExecutorConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.executorConfigs, name);
    }

    public ConcurrentMap<String, ExecutorConfig> getExecutorConfigs() {
        return this.executorConfigs;
    }

    @Override
    public ScheduledExecutorConfig findScheduledExecutorConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.scheduledExecutorConfigs, name);
    }

    public ConcurrentMap<String, ScheduledExecutorConfig> getScheduledExecutorConfigs() {
        return this.scheduledExecutorConfigs;
    }

    @Override
    public DurableExecutorConfig findDurableExecutorConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.durableExecutorConfigs, name);
    }

    public ConcurrentMap<String, DurableExecutorConfig> getDurableExecutorConfigs() {
        return this.durableExecutorConfigs;
    }

    @Override
    public SemaphoreConfig findSemaphoreConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.semaphoreConfigs, name);
    }

    public ConcurrentMap<String, SemaphoreConfig> getSemaphoreConfigs() {
        return this.semaphoreConfigs;
    }

    @Override
    public RingbufferConfig findRingbufferConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.ringbufferConfigs, name);
    }

    public ConcurrentMap<String, RingbufferConfig> getRingbufferConfigs() {
        return this.ringbufferConfigs;
    }

    @Override
    public AtomicLongConfig findAtomicLongConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.atomicLongConfigs, name);
    }

    @Override
    public Map<String, AtomicLongConfig> getAtomicLongConfigs() {
        return this.atomicLongConfigs;
    }

    @Override
    public AtomicReferenceConfig findAtomicReferenceConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.atomicReferenceConfigs, name);
    }

    @Override
    public CountDownLatchConfig findCountDownLatchConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.countDownLatchConfigs, name);
    }

    @Override
    public Map<String, AtomicReferenceConfig> getAtomicReferenceConfigs() {
        return this.atomicReferenceConfigs;
    }

    @Override
    public Map<String, CountDownLatchConfig> getCountDownLatchConfigs() {
        return this.countDownLatchConfigs;
    }

    @Override
    public LockConfig findLockConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.lockConfigs, name);
    }

    @Override
    public Map<String, LockConfig> getLockConfigs() {
        return this.lockConfigs;
    }

    @Override
    public ListConfig findListConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.listConfigs, name);
    }

    public ConcurrentMap<String, ListConfig> getListConfigs() {
        return this.listConfigs;
    }

    @Override
    public QueueConfig findQueueConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.queueConfigs, name);
    }

    @Override
    public Map<String, QueueConfig> getQueueConfigs() {
        return this.queueConfigs;
    }

    @Override
    public SetConfig findSetConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.setConfigs, name);
    }

    public ConcurrentMap<String, SetConfig> getSetConfigs() {
        return this.setConfigs;
    }

    @Override
    public ReplicatedMapConfig findReplicatedMapConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.replicatedMapConfigs, name);
    }

    public ConcurrentMap<String, ReplicatedMapConfig> getReplicatedMapConfigs() {
        return this.replicatedMapConfigs;
    }

    @Override
    public ReliableTopicConfig findReliableTopicConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.reliableTopicConfigs, name);
    }

    public ConcurrentMap<String, ReliableTopicConfig> getReliableTopicConfigs() {
        return this.reliableTopicConfigs;
    }

    @Override
    public CacheSimpleConfig findCacheSimpleConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cacheSimpleConfigs, name);
    }

    @Override
    public Map<String, CacheSimpleConfig> getCacheSimpleConfigs() {
        return this.cacheSimpleConfigs;
    }

    @Override
    public EventJournalConfig findCacheEventJournalConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cacheEventJournalConfigs, name);
    }

    @Override
    public Map<String, EventJournalConfig> getCacheEventJournalConfigs() {
        return this.cacheEventJournalConfigs;
    }

    @Override
    public EventJournalConfig findMapEventJournalConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapEventJournalConfigs, name);
    }

    @Override
    public Map<String, EventJournalConfig> getMapEventJournalConfigs() {
        return this.mapEventJournalConfigs;
    }

    @Override
    public MerkleTreeConfig findMapMerkleTreeConfig(String name) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapMerkleTreeConfigs, name);
    }

    @Override
    public Map<String, MerkleTreeConfig> getMapMerkleTreeConfigs() {
        return this.mapMerkleTreeConfigs;
    }

    @Override
    public FlakeIdGeneratorConfig findFlakeIdGeneratorConfig(String baseName) {
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.flakeIdGeneratorConfigs, baseName);
    }

    @Override
    public Map<String, FlakeIdGeneratorConfig> getFlakeIdGeneratorConfigs() {
        return this.flakeIdGeneratorConfigs;
    }

    @Override
    public Runnable prepareMergeRunnable() {
        if (this.version.isLessOrEqual(Versions.V3_8)) {
            return null;
        }
        IdentifiedDataSerializable[] allConfigurations = this.collectAllDynamicConfigs();
        if (this.noConfigurationExist(allConfigurations)) {
            return null;
        }
        return new Merger(this.nodeEngine, new DynamicConfigPreJoinOperation(allConfigurations, ConfigCheckMode.SILENT));
    }

    private static Map<Class<? extends IdentifiedDataSerializable>, Version> initializeConfigToVersionMap() {
        HashMap<Class<MerkleTreeConfig>, Version> configToVersion = new HashMap<Class<MerkleTreeConfig>, Version>();
        configToVersion.put(MapConfig.class, Versions.V3_9);
        configToVersion.put(MultiMapConfig.class, Versions.V3_9);
        configToVersion.put(CardinalityEstimatorConfig.class, Versions.V3_9);
        configToVersion.put(RingbufferConfig.class, Versions.V3_9);
        configToVersion.put(LockConfig.class, Versions.V3_9);
        configToVersion.put(ListConfig.class, Versions.V3_9);
        configToVersion.put(SetConfig.class, Versions.V3_9);
        configToVersion.put(ReplicatedMapConfig.class, Versions.V3_9);
        configToVersion.put(TopicConfig.class, Versions.V3_9);
        configToVersion.put(ExecutorConfig.class, Versions.V3_9);
        configToVersion.put(DurableExecutorConfig.class, Versions.V3_9);
        configToVersion.put(ScheduledExecutorConfig.class, Versions.V3_9);
        configToVersion.put(SemaphoreConfig.class, Versions.V3_9);
        configToVersion.put(QueueConfig.class, Versions.V3_9);
        configToVersion.put(ReliableTopicConfig.class, Versions.V3_9);
        configToVersion.put(CacheSimpleConfig.class, Versions.V3_9);
        configToVersion.put(EventJournalConfig.class, Versions.V3_9);
        configToVersion.put(AtomicLongConfig.class, Versions.V3_10);
        configToVersion.put(AtomicReferenceConfig.class, Versions.V3_10);
        configToVersion.put(CountDownLatchConfig.class, Versions.V3_10);
        configToVersion.put(FlakeIdGeneratorConfig.class, Versions.V3_10);
        configToVersion.put(PNCounterConfig.class, Versions.V3_10);
        configToVersion.put(MerkleTreeConfig.class, Versions.V3_11);
        return Collections.unmodifiableMap(configToVersion);
    }

    static {
        IGNORE_CONFLICTING_CONFIGS_WORKAROUND = Boolean.getBoolean("hazelcast.dynamicconfig.ignore.conflicts");
        CONFIG_TO_VERSION = ClusterWideConfigurationService.initializeConfigToVersionMap();
    }

    public static class Merger
    implements Runnable {
        private final NodeEngine nodeEngine;
        private final Operation replicationOperation;

        public Merger(NodeEngine nodeEngine, Operation replicationOperation) {
            this.nodeEngine = nodeEngine;
            this.replicationOperation = replicationOperation;
        }

        @Override
        public void run() {
            try {
                ICompletableFuture<Object> future = InvocationUtil.invokeOnStableClusterSerial(this.nodeEngine, (Supplier<? extends Operation>)new Supplier<Operation>(){

                    @Override
                    public Operation get() {
                        return replicationOperation;
                    }
                }, 100);
                FutureUtil.waitForever(Collections.singleton(future), FutureUtil.RETHROW_EVERYTHING);
            }
            catch (Exception e) {
                throw new HazelcastException("Error while merging configurations", e);
            }
        }
    }
}

