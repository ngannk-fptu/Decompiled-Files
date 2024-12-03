/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.HotRestartPersistenceConfig;
import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.ServicesConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.dynamicconfig.AggregatingMap;
import com.hazelcast.internal.dynamicconfig.ConfigurationService;
import com.hazelcast.internal.dynamicconfig.DynamicCPSubsystemConfig;
import com.hazelcast.internal.dynamicconfig.DynamicSecurityConfig;
import com.hazelcast.internal.dynamicconfig.EmptyConfigurationService;
import com.hazelcast.internal.dynamicconfig.search.ConfigSearch;
import com.hazelcast.internal.dynamicconfig.search.ConfigSupplier;
import com.hazelcast.internal.dynamicconfig.search.Searcher;
import com.hazelcast.security.SecurityService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.StringUtil;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;

public class DynamicConfigurationAwareConfig
extends Config {
    private final ConfigSupplier<MapConfig> mapConfigOrNullConfigSupplier = new ConfigSupplier<MapConfig>(){

        @Override
        public MapConfig getDynamicConfig(@Nonnull ConfigurationService configurationService, @Nonnull String name) {
            return configurationService.findMapConfig(name);
        }

        @Override
        public MapConfig getStaticConfig(@Nonnull Config staticConfig, @Nonnull String name) {
            return staticConfig.getMapConfigOrNull(name);
        }

        @Override
        public Map<String, MapConfig> getStaticConfigs(@Nonnull Config staticConfig) {
            return staticConfig.getMapConfigs();
        }
    };
    private final ConfigSupplier<EventJournalConfig> mapEventJournalConfigSupplier = new ConfigSupplier<EventJournalConfig>(){

        @Override
        public EventJournalConfig getDynamicConfig(@Nonnull ConfigurationService configurationService, @Nonnull String name) {
            return configurationService.findMapEventJournalConfig(name);
        }

        @Override
        public EventJournalConfig getStaticConfig(@Nonnull Config staticConfig, @Nonnull String name) {
            return staticConfig.getMapEventJournalConfig(name);
        }

        @Override
        public Map<String, EventJournalConfig> getStaticConfigs(@Nonnull Config staticConfig) {
            return staticConfig.getMapEventJournalConfigs();
        }
    };
    private final ConfigSupplier<EventJournalConfig> cacheEventJournalConfigSupplier = new ConfigSupplier<EventJournalConfig>(){

        @Override
        public EventJournalConfig getDynamicConfig(@Nonnull ConfigurationService configurationService, @Nonnull String name) {
            return configurationService.findCacheEventJournalConfig(name);
        }

        @Override
        public EventJournalConfig getStaticConfig(@Nonnull Config staticConfig, @Nonnull String name) {
            return staticConfig.getCacheEventJournalConfig(name);
        }

        @Override
        public Map<String, EventJournalConfig> getStaticConfigs(@Nonnull Config staticConfig) {
            return staticConfig.getCacheEventJournalConfigs();
        }
    };
    private final ConfigSupplier<MerkleTreeConfig> mapMerkleTreeConfigSupplier = new ConfigSupplier<MerkleTreeConfig>(){

        @Override
        public MerkleTreeConfig getDynamicConfig(@Nonnull ConfigurationService configurationService, @Nonnull String name) {
            return configurationService.findMapMerkleTreeConfig(name);
        }

        @Override
        public MerkleTreeConfig getStaticConfig(@Nonnull Config staticConfig, @Nonnull String name) {
            return staticConfig.getMapMerkleTreeConfig(name);
        }

        @Override
        public Map<String, MerkleTreeConfig> getStaticConfigs(@Nonnull Config staticConfig) {
            return staticConfig.getMapMerkleTreeConfigs();
        }
    };
    private final Config staticConfig;
    private final ConfigPatternMatcher configPatternMatcher;
    private final boolean isStaticFirst;
    private final DynamicCPSubsystemConfig dynamicCPSubsystemConfig;
    private volatile ConfigurationService configurationService = new EmptyConfigurationService();
    private volatile DynamicSecurityConfig dynamicSecurityConfig;
    private volatile Searcher configSearcher;

    public DynamicConfigurationAwareConfig(Config staticConfig, HazelcastProperties properties) {
        assert (!(staticConfig instanceof DynamicConfigurationAwareConfig)) : "A static Config object is required";
        this.staticConfig = staticConfig;
        this.configPatternMatcher = staticConfig.getConfigPatternMatcher();
        this.isStaticFirst = !properties.getBoolean(GroupProperty.SEARCH_DYNAMIC_CONFIG_FIRST);
        this.dynamicSecurityConfig = new DynamicSecurityConfig(staticConfig.getSecurityConfig(), null);
        this.dynamicCPSubsystemConfig = new DynamicCPSubsystemConfig(staticConfig.getCPSubsystemConfig());
        this.configSearcher = this.initConfigSearcher();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.staticConfig.getClassLoader();
    }

    @Override
    public Config setClassLoader(ClassLoader classLoader) {
        return this.staticConfig.setClassLoader(classLoader);
    }

    @Override
    public ConfigPatternMatcher getConfigPatternMatcher() {
        return this.staticConfig.getConfigPatternMatcher();
    }

    @Override
    public void setConfigPatternMatcher(ConfigPatternMatcher configPatternMatcher) {
        this.staticConfig.setConfigPatternMatcher(configPatternMatcher);
    }

    @Override
    public String getProperty(String name) {
        return this.staticConfig.getProperty(name);
    }

    @Override
    public Config setProperty(String name, String value) {
        return this.staticConfig.setProperty(name, value);
    }

    @Override
    public MemberAttributeConfig getMemberAttributeConfig() {
        return this.staticConfig.getMemberAttributeConfig();
    }

    @Override
    public void setMemberAttributeConfig(MemberAttributeConfig memberAttributeConfig) {
        this.staticConfig.setMemberAttributeConfig(memberAttributeConfig);
    }

    @Override
    public Properties getProperties() {
        return this.staticConfig.getProperties();
    }

    @Override
    public Config setProperties(Properties properties) {
        return this.staticConfig.setProperties(properties);
    }

    @Override
    public String getInstanceName() {
        return this.staticConfig.getInstanceName();
    }

    @Override
    public Config setInstanceName(String instanceName) {
        return this.staticConfig.setInstanceName(instanceName);
    }

    @Override
    public GroupConfig getGroupConfig() {
        return this.staticConfig.getGroupConfig();
    }

    @Override
    public Config setGroupConfig(GroupConfig groupConfig) {
        return this.staticConfig.setGroupConfig(groupConfig);
    }

    @Override
    public NetworkConfig getNetworkConfig() {
        return this.staticConfig.getNetworkConfig();
    }

    @Override
    public Config setNetworkConfig(NetworkConfig networkConfig) {
        return this.staticConfig.setNetworkConfig(networkConfig);
    }

    @Override
    public AdvancedNetworkConfig getAdvancedNetworkConfig() {
        return this.staticConfig.getAdvancedNetworkConfig();
    }

    @Override
    public Config setAdvancedNetworkConfig(AdvancedNetworkConfig advancedNetworkConfig) {
        return this.staticConfig.setAdvancedNetworkConfig(advancedNetworkConfig);
    }

    @Override
    public MapConfig findMapConfig(String name) {
        return this.getMapConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public MapConfig getMapConfig(String name) {
        return this.getMapConfigInternal(name, name);
    }

    @Override
    public MapConfig getMapConfigOrNull(String name) {
        return this.getMapConfigOrNullInternal(name);
    }

    private MapConfig getMapConfigOrNullInternal(String name) {
        return this.getMapConfigOrNullInternal(name, name);
    }

    private MapConfig getMapConfigOrNullInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, this.mapConfigOrNullConfigSupplier);
    }

    private MapConfig getMapConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(MapConfig.class));
    }

    @Override
    public Config addMapConfig(MapConfig mapConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getMapConfigs(), mapConfig.getName(), mapConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(mapConfig);
        }
        return this;
    }

    public <T> boolean checkStaticConfigDoesNotExist(Map<String, T> staticConfigurations, String configName, T newConfig) {
        T existingConfiguration = staticConfigurations.get(configName);
        if (existingConfiguration != null && !existingConfiguration.equals(newConfig)) {
            throw new ConfigurationException("Cannot add a new dynamic configuration " + newConfig + " as static configuration already contains " + existingConfiguration);
        }
        return existingConfiguration == null;
    }

    public Config getStaticConfig() {
        return this.staticConfig;
    }

    @Override
    public Map<String, MapConfig> getMapConfigs() {
        Map<String, MapConfig> staticMapConfigs = this.staticConfig.getMapConfigs();
        Map<String, MapConfig> dynamicMapConfigs = this.configurationService.getMapConfigs();
        return AggregatingMap.aggregate(staticMapConfigs, dynamicMapConfigs);
    }

    @Override
    public Config setMapConfigs(Map<String, MapConfig> mapConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public CacheSimpleConfig findCacheConfig(String name) {
        return this.getCacheConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public CacheSimpleConfig findCacheConfigOrNull(String name) {
        CacheSimpleConfig cacheConfig = this.getCacheConfigInternal(name, null);
        if (cacheConfig == null) {
            return null;
        }
        return cacheConfig.getAsReadOnly();
    }

    @Override
    public CacheSimpleConfig getCacheConfig(String name) {
        return this.getCacheConfigInternal(name, name);
    }

    private CacheSimpleConfig getCacheConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(CacheSimpleConfig.class));
    }

    @Override
    public Config addCacheConfig(CacheSimpleConfig cacheConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getCacheConfigs(), cacheConfig.getName(), cacheConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(cacheConfig);
        }
        return this;
    }

    @Override
    public Map<String, CacheSimpleConfig> getCacheConfigs() {
        Map<String, CacheSimpleConfig> staticConfigs = this.staticConfig.getCacheConfigs();
        Map<String, CacheSimpleConfig> dynamicConfigs = this.configurationService.getCacheSimpleConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setCacheConfigs(Map<String, CacheSimpleConfig> cacheConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public QueueConfig findQueueConfig(String name) {
        return this.getQueueConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public QueueConfig getQueueConfig(String name) {
        return this.getQueueConfigInternal(name, name);
    }

    private QueueConfig getQueueConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(QueueConfig.class));
    }

    @Override
    public Config addQueueConfig(QueueConfig queueConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getQueueConfigs(), queueConfig.getName(), queueConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(queueConfig);
        }
        return this;
    }

    @Override
    public Map<String, QueueConfig> getQueueConfigs() {
        Map<String, QueueConfig> staticQueueConfigs = this.staticConfig.getQueueConfigs();
        Map<String, QueueConfig> dynamicQueueConfigs = this.configurationService.getQueueConfigs();
        return AggregatingMap.aggregate(staticQueueConfigs, dynamicQueueConfigs);
    }

    @Override
    public Config setQueueConfigs(Map<String, QueueConfig> queueConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public LockConfig findLockConfig(String name) {
        return this.getLockConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public LockConfig getLockConfig(String name) {
        return this.getLockConfigInternal(name, name);
    }

    private LockConfig getLockConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(LockConfig.class));
    }

    @Override
    public Config addLockConfig(LockConfig lockConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getLockConfigs(), lockConfig.getName(), lockConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(lockConfig);
        }
        return this;
    }

    @Override
    public Map<String, LockConfig> getLockConfigs() {
        Map<String, LockConfig> staticLockConfigs = this.staticConfig.getLockConfigs();
        Map<String, LockConfig> dynamiclockConfigs = this.configurationService.getLockConfigs();
        return AggregatingMap.aggregate(staticLockConfigs, dynamiclockConfigs);
    }

    @Override
    public Config setLockConfigs(Map<String, LockConfig> lockConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ListConfig findListConfig(String name) {
        return this.getListConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public ListConfig getListConfig(String name) {
        return this.getListConfigInternal(name, name);
    }

    private ListConfig getListConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(ListConfig.class));
    }

    @Override
    public Config addListConfig(ListConfig listConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getListConfigs(), listConfig.getName(), listConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(listConfig);
        }
        return this;
    }

    @Override
    public Map<String, ListConfig> getListConfigs() {
        Map<String, ListConfig> staticListConfigs = this.staticConfig.getListConfigs();
        Map<String, ListConfig> dynamicListConfigs = this.configurationService.getListConfigs();
        return AggregatingMap.aggregate(staticListConfigs, dynamicListConfigs);
    }

    @Override
    public Config setListConfigs(Map<String, ListConfig> listConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SetConfig findSetConfig(String name) {
        return this.getSetConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public SetConfig getSetConfig(String name) {
        return this.getSetConfigInternal(name, name);
    }

    private SetConfig getSetConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(SetConfig.class));
    }

    @Override
    public Config addSetConfig(SetConfig setConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getSetConfigs(), setConfig.getName(), setConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(setConfig);
        }
        return this;
    }

    @Override
    public Map<String, SetConfig> getSetConfigs() {
        Map<String, SetConfig> staticSetConfigs = this.staticConfig.getSetConfigs();
        Map<String, SetConfig> dynamicSetConfigs = this.configurationService.getSetConfigs();
        return AggregatingMap.aggregate(staticSetConfigs, dynamicSetConfigs);
    }

    @Override
    public Config setSetConfigs(Map<String, SetConfig> setConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public MultiMapConfig findMultiMapConfig(String name) {
        return this.getMultiMapConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public MultiMapConfig getMultiMapConfig(String name) {
        return this.getMultiMapConfigInternal(name, name);
    }

    private MultiMapConfig getMultiMapConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(MultiMapConfig.class));
    }

    @Override
    public Config addMultiMapConfig(MultiMapConfig multiMapConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getMultiMapConfigs(), multiMapConfig.getName(), multiMapConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(multiMapConfig);
        }
        return this;
    }

    @Override
    public Map<String, MultiMapConfig> getMultiMapConfigs() {
        Map<String, MultiMapConfig> staticConfigs = this.staticConfig.getMultiMapConfigs();
        Map<String, MultiMapConfig> dynamicConfigs = this.configurationService.getMultiMapConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setMultiMapConfigs(Map<String, MultiMapConfig> multiMapConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ReplicatedMapConfig findReplicatedMapConfig(String name) {
        return this.getReplicatedMapConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public ReplicatedMapConfig getReplicatedMapConfig(String name) {
        return this.getReplicatedMapConfigInternal(name, name);
    }

    private ReplicatedMapConfig getReplicatedMapConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(ReplicatedMapConfig.class));
    }

    @Override
    public Config addReplicatedMapConfig(ReplicatedMapConfig replicatedMapConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getReplicatedMapConfigs(), replicatedMapConfig.getName(), replicatedMapConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(replicatedMapConfig);
        }
        return this;
    }

    @Override
    public Map<String, ReplicatedMapConfig> getReplicatedMapConfigs() {
        Map<String, ReplicatedMapConfig> staticConfigs = this.staticConfig.getReplicatedMapConfigs();
        Map<String, ReplicatedMapConfig> dynamicConfigs = this.configurationService.getReplicatedMapConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setReplicatedMapConfigs(Map<String, ReplicatedMapConfig> replicatedMapConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public RingbufferConfig findRingbufferConfig(String name) {
        return this.getRingbufferConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public RingbufferConfig getRingbufferConfig(String name) {
        return this.getRingbufferConfigInternal(name, name);
    }

    private RingbufferConfig getRingbufferConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(RingbufferConfig.class));
    }

    @Override
    public Config addRingBufferConfig(RingbufferConfig ringbufferConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getRingbufferConfigs(), ringbufferConfig.getName(), ringbufferConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(ringbufferConfig);
        }
        return this;
    }

    @Override
    public Map<String, RingbufferConfig> getRingbufferConfigs() {
        Map<String, RingbufferConfig> staticConfigs = this.staticConfig.getRingbufferConfigs();
        Map<String, RingbufferConfig> dynamicConfigs = this.configurationService.getRingbufferConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setRingbufferConfigs(Map<String, RingbufferConfig> ringbufferConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public AtomicLongConfig findAtomicLongConfig(String name) {
        return this.getAtomicLongConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public AtomicLongConfig getAtomicLongConfig(String name) {
        return this.getAtomicLongConfigInternal(name, name);
    }

    @Override
    public Config addAtomicLongConfig(AtomicLongConfig atomicLongConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getAtomicLongConfigs(), atomicLongConfig.getName(), atomicLongConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(atomicLongConfig);
        }
        return this;
    }

    @Override
    public Map<String, AtomicLongConfig> getAtomicLongConfigs() {
        Map<String, AtomicLongConfig> staticConfigs = this.staticConfig.getAtomicLongConfigs();
        Map<String, AtomicLongConfig> dynamicConfigs = this.configurationService.getAtomicLongConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setAtomicLongConfigs(Map<String, AtomicLongConfig> atomicLongConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    private AtomicLongConfig getAtomicLongConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(AtomicLongConfig.class));
    }

    @Override
    public AtomicReferenceConfig findAtomicReferenceConfig(String name) {
        return this.getAtomicReferenceConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public AtomicReferenceConfig getAtomicReferenceConfig(String name) {
        return this.getAtomicReferenceConfigInternal(name, name);
    }

    @Override
    public Config addAtomicReferenceConfig(AtomicReferenceConfig atomicReferenceConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getAtomicReferenceConfigs(), atomicReferenceConfig.getName(), atomicReferenceConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(atomicReferenceConfig);
        }
        return this;
    }

    @Override
    public Map<String, AtomicReferenceConfig> getAtomicReferenceConfigs() {
        Map<String, AtomicReferenceConfig> staticConfigs = this.staticConfig.getAtomicReferenceConfigs();
        Map<String, AtomicReferenceConfig> dynamicConfigs = this.configurationService.getAtomicReferenceConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setAtomicReferenceConfigs(Map<String, AtomicReferenceConfig> atomicReferenceConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    private AtomicReferenceConfig getAtomicReferenceConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(AtomicReferenceConfig.class));
    }

    @Override
    public CountDownLatchConfig findCountDownLatchConfig(String name) {
        return this.getCountDownLatchConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public CountDownLatchConfig getCountDownLatchConfig(String name) {
        return this.getCountDownLatchConfigInternal(name, name);
    }

    @Override
    public Config addCountDownLatchConfig(CountDownLatchConfig countDownLatchConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getCountDownLatchConfigs(), countDownLatchConfig.getName(), countDownLatchConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(countDownLatchConfig);
        }
        return this;
    }

    @Override
    public Map<String, CountDownLatchConfig> getCountDownLatchConfigs() {
        Map<String, CountDownLatchConfig> staticConfigs = this.staticConfig.getCountDownLatchConfigs();
        Map<String, CountDownLatchConfig> dynamicConfigs = this.configurationService.getCountDownLatchConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setCountDownLatchConfigs(Map<String, CountDownLatchConfig> countDownLatchConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    private CountDownLatchConfig getCountDownLatchConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(CountDownLatchConfig.class));
    }

    @Override
    public TopicConfig findTopicConfig(String name) {
        return this.getTopicConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public TopicConfig getTopicConfig(String name) {
        return this.getTopicConfigInternal(name, name);
    }

    private TopicConfig getTopicConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(TopicConfig.class));
    }

    @Override
    public Config addTopicConfig(TopicConfig topicConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getTopicConfigs(), topicConfig.getName(), topicConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(topicConfig);
        }
        return this;
    }

    @Override
    public Map<String, TopicConfig> getTopicConfigs() {
        Map<String, TopicConfig> staticConfigs = this.staticConfig.getTopicConfigs();
        Map<String, TopicConfig> dynamicConfigs = this.configurationService.getTopicConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setTopicConfigs(Map<String, TopicConfig> mapTopicConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ReliableTopicConfig findReliableTopicConfig(String name) {
        return this.getReliableTopicConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public ReliableTopicConfig getReliableTopicConfig(String name) {
        return this.getReliableTopicConfigInternal(name, name);
    }

    private ReliableTopicConfig getReliableTopicConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(ReliableTopicConfig.class));
    }

    @Override
    public Map<String, ReliableTopicConfig> getReliableTopicConfigs() {
        Map<String, ReliableTopicConfig> staticConfigs = this.staticConfig.getReliableTopicConfigs();
        Map<String, ReliableTopicConfig> dynamicConfigs = this.configurationService.getReliableTopicConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config addReliableTopicConfig(ReliableTopicConfig reliableTopicConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getReliableTopicConfigs(), reliableTopicConfig.getName(), reliableTopicConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(reliableTopicConfig);
        }
        return this;
    }

    @Override
    public Config setReliableTopicConfigs(Map<String, ReliableTopicConfig> reliableTopicConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ExecutorConfig findExecutorConfig(String name) {
        return this.getExecutorConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public ExecutorConfig getExecutorConfig(String name) {
        return this.getExecutorConfigInternal(name, name);
    }

    private ExecutorConfig getExecutorConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(ExecutorConfig.class));
    }

    @Override
    public Config addExecutorConfig(ExecutorConfig executorConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getExecutorConfigs(), executorConfig.getName(), executorConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(executorConfig);
        }
        return this;
    }

    @Override
    public Map<String, ExecutorConfig> getExecutorConfigs() {
        Map<String, ExecutorConfig> staticConfigs = this.staticConfig.getExecutorConfigs();
        Map<String, ExecutorConfig> dynamicConfigs = this.configurationService.getExecutorConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setExecutorConfigs(Map<String, ExecutorConfig> executorConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public DurableExecutorConfig findDurableExecutorConfig(String name) {
        return this.getDurableExecutorConfigInternal(name, "default");
    }

    @Override
    public DurableExecutorConfig getDurableExecutorConfig(String name) {
        return this.getDurableExecutorConfigInternal(name, name);
    }

    private DurableExecutorConfig getDurableExecutorConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(DurableExecutorConfig.class));
    }

    @Override
    public Config addDurableExecutorConfig(DurableExecutorConfig durableExecutorConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getDurableExecutorConfigs(), durableExecutorConfig.getName(), durableExecutorConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(durableExecutorConfig);
        }
        return this;
    }

    @Override
    public Map<String, DurableExecutorConfig> getDurableExecutorConfigs() {
        Map<String, DurableExecutorConfig> staticConfigs = this.staticConfig.getDurableExecutorConfigs();
        Map<String, DurableExecutorConfig> dynamicConfigs = this.configurationService.getDurableExecutorConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setDurableExecutorConfigs(Map<String, DurableExecutorConfig> durableExecutorConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ScheduledExecutorConfig findScheduledExecutorConfig(String name) {
        return this.getScheduledExecutorConfigInternal(name, "default");
    }

    @Override
    public ScheduledExecutorConfig getScheduledExecutorConfig(String name) {
        return this.getScheduledExecutorConfigInternal(name, name);
    }

    private ScheduledExecutorConfig getScheduledExecutorConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(ScheduledExecutorConfig.class));
    }

    @Override
    public Map<String, ScheduledExecutorConfig> getScheduledExecutorConfigs() {
        Map<String, ScheduledExecutorConfig> staticConfigs = this.staticConfig.getScheduledExecutorConfigs();
        Map<String, ScheduledExecutorConfig> dynamicConfigs = this.configurationService.getScheduledExecutorConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config addScheduledExecutorConfig(ScheduledExecutorConfig scheduledExecutorConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getScheduledExecutorConfigs(), scheduledExecutorConfig.getName(), scheduledExecutorConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(scheduledExecutorConfig);
        }
        return this;
    }

    @Override
    public Config setScheduledExecutorConfigs(Map<String, ScheduledExecutorConfig> scheduledExecutorConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public CardinalityEstimatorConfig findCardinalityEstimatorConfig(String name) {
        return this.getCardinalityEstimatorConfigInternal(name, "default");
    }

    @Override
    public CardinalityEstimatorConfig getCardinalityEstimatorConfig(String name) {
        return this.getCardinalityEstimatorConfigInternal(name, name);
    }

    private CardinalityEstimatorConfig getCardinalityEstimatorConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(CardinalityEstimatorConfig.class));
    }

    @Override
    public Config addCardinalityEstimatorConfig(CardinalityEstimatorConfig cardinalityEstimatorConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getCardinalityEstimatorConfigs(), cardinalityEstimatorConfig.getName(), cardinalityEstimatorConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(cardinalityEstimatorConfig);
        }
        return this;
    }

    @Override
    public Map<String, CardinalityEstimatorConfig> getCardinalityEstimatorConfigs() {
        Map<String, CardinalityEstimatorConfig> staticConfigs = this.staticConfig.getCardinalityEstimatorConfigs();
        Map<String, CardinalityEstimatorConfig> dynamicConfigs = this.configurationService.getCardinalityEstimatorConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setCardinalityEstimatorConfigs(Map<String, CardinalityEstimatorConfig> cardinalityEstimatorConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PNCounterConfig findPNCounterConfig(String name) {
        return this.getPNCounterConfigInternal(name, "default");
    }

    @Override
    public PNCounterConfig getPNCounterConfig(String name) {
        return this.getPNCounterConfigInternal(name, name);
    }

    private PNCounterConfig getPNCounterConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(PNCounterConfig.class));
    }

    @Override
    public Config addPNCounterConfig(PNCounterConfig pnCounterConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getPNCounterConfigs(), pnCounterConfig.getName(), pnCounterConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(pnCounterConfig);
        }
        return this;
    }

    @Override
    public Map<String, PNCounterConfig> getPNCounterConfigs() {
        Map<String, PNCounterConfig> staticConfigs = this.staticConfig.getPNCounterConfigs();
        Map<String, PNCounterConfig> dynamicConfigs = this.configurationService.getPNCounterConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setPNCounterConfigs(Map<String, PNCounterConfig> pnCounterConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SemaphoreConfig findSemaphoreConfig(String name) {
        return this.getSemaphoreConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public SemaphoreConfig getSemaphoreConfig(String name) {
        return this.getSemaphoreConfigInternal(name, name);
    }

    private SemaphoreConfig getSemaphoreConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(SemaphoreConfig.class));
    }

    @Override
    public Config addSemaphoreConfig(SemaphoreConfig semaphoreConfig) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getSemaphoreConfigsAsMap(), semaphoreConfig.getName(), semaphoreConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(semaphoreConfig);
        }
        return this;
    }

    @Override
    public Collection<SemaphoreConfig> getSemaphoreConfigs() {
        Collection<SemaphoreConfig> staticConfigs = this.staticConfig.getSemaphoreConfigs();
        Map<String, SemaphoreConfig> semaphoreConfigs = this.configurationService.getSemaphoreConfigs();
        ArrayList<SemaphoreConfig> aggregated = new ArrayList<SemaphoreConfig>(staticConfigs);
        aggregated.addAll(semaphoreConfigs.values());
        return aggregated;
    }

    @Override
    public Map<String, SemaphoreConfig> getSemaphoreConfigsAsMap() {
        Map<String, SemaphoreConfig> staticConfigs = this.staticConfig.getSemaphoreConfigsAsMap();
        Map<String, SemaphoreConfig> dynamicConfigs = this.configurationService.getSemaphoreConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setSemaphoreConfigs(Map<String, SemaphoreConfig> semaphoreConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public EventJournalConfig findCacheEventJournalConfig(String name) {
        return this.getCacheEventJournalConfigInternal(name, "default");
    }

    @Override
    public EventJournalConfig getCacheEventJournalConfig(String name) {
        return this.getCacheEventJournalConfigInternal(name, name);
    }

    private EventJournalConfig getCacheEventJournalConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, this.cacheEventJournalConfigSupplier);
    }

    @Override
    public Map<String, EventJournalConfig> getCacheEventJournalConfigs() {
        Map<String, EventJournalConfig> staticConfigs = this.staticConfig.getCacheEventJournalConfigs();
        Map<String, EventJournalConfig> dynamicConfigs = this.configurationService.getCacheEventJournalConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public EventJournalConfig findMapEventJournalConfig(String name) {
        return this.getMapEventJournalConfigInternal(name, "default");
    }

    @Override
    public EventJournalConfig getMapEventJournalConfig(String name) {
        return this.getMapEventJournalConfigInternal(name, name);
    }

    private EventJournalConfig getMapEventJournalConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, this.mapEventJournalConfigSupplier);
    }

    @Override
    public Map<String, EventJournalConfig> getMapEventJournalConfigs() {
        Map<String, EventJournalConfig> staticConfigs = this.staticConfig.getMapEventJournalConfigs();
        Map<String, EventJournalConfig> dynamicConfigs = this.configurationService.getMapEventJournalConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config addEventJournalConfig(EventJournalConfig eventJournalConfig) {
        Map<String, EventJournalConfig> staticConfigs;
        String mapName = eventJournalConfig.getMapName();
        String cacheName = eventJournalConfig.getCacheName();
        if (StringUtil.isNullOrEmpty(mapName) && StringUtil.isNullOrEmpty(cacheName)) {
            throw new IllegalArgumentException("Event journal config should have non-empty map name and/or cache name");
        }
        boolean staticConfigDoesNotExist = false;
        if (!StringUtil.isNullOrEmpty(mapName)) {
            staticConfigs = this.staticConfig.getMapEventJournalConfigs();
            staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(staticConfigs, mapName, eventJournalConfig);
        }
        if (!StringUtil.isNullOrEmpty(cacheName)) {
            staticConfigs = this.staticConfig.getCacheEventJournalConfigs();
            staticConfigDoesNotExist |= this.checkStaticConfigDoesNotExist(staticConfigs, cacheName, eventJournalConfig);
        }
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(eventJournalConfig);
        }
        return this;
    }

    @Override
    public Config setMapEventJournalConfigs(Map<String, EventJournalConfig> eventJournalConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Config setCacheEventJournalConfigs(Map<String, EventJournalConfig> eventJournalConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public MerkleTreeConfig findMapMerkleTreeConfig(String name) {
        return this.getMapMerkleTreeConfigInternal(name, "default");
    }

    @Override
    public MerkleTreeConfig getMapMerkleTreeConfig(String name) {
        return this.getMapMerkleTreeConfigInternal(name, name);
    }

    private MerkleTreeConfig getMapMerkleTreeConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, this.mapMerkleTreeConfigSupplier);
    }

    @Override
    public Config addMerkleTreeConfig(MerkleTreeConfig merkleTreeConfig) {
        String mapName = merkleTreeConfig.getMapName();
        if (StringUtil.isNullOrEmpty(mapName)) {
            throw new IllegalArgumentException("Merkle tree config must define a map name");
        }
        Map<String, MerkleTreeConfig> staticConfigs = this.staticConfig.getMapMerkleTreeConfigs();
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(staticConfigs, mapName, merkleTreeConfig);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(merkleTreeConfig);
        }
        return this;
    }

    @Override
    public Map<String, MerkleTreeConfig> getMapMerkleTreeConfigs() {
        Map<String, MerkleTreeConfig> staticConfigs = this.staticConfig.getMapMerkleTreeConfigs();
        Map<String, MerkleTreeConfig> dynamicConfigs = this.configurationService.getMapMerkleTreeConfigs();
        return AggregatingMap.aggregate(staticConfigs, dynamicConfigs);
    }

    @Override
    public Config setMapMerkleTreeConfigs(Map<String, MerkleTreeConfig> merkleTreeConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Map<String, FlakeIdGeneratorConfig> getFlakeIdGeneratorConfigs() {
        Map<String, FlakeIdGeneratorConfig> staticMapConfigs = this.staticConfig.getFlakeIdGeneratorConfigs();
        Map<String, FlakeIdGeneratorConfig> dynamicMapConfigs = this.configurationService.getFlakeIdGeneratorConfigs();
        return AggregatingMap.aggregate(staticMapConfigs, dynamicMapConfigs);
    }

    @Override
    public FlakeIdGeneratorConfig findFlakeIdGeneratorConfig(String name) {
        return this.getFlakeIdGeneratorConfigInternal(name, "default").getAsReadOnly();
    }

    @Override
    public FlakeIdGeneratorConfig getFlakeIdGeneratorConfig(String name) {
        return this.getFlakeIdGeneratorConfigInternal(name, name);
    }

    private FlakeIdGeneratorConfig getFlakeIdGeneratorConfigInternal(String name, String fallbackName) {
        return this.configSearcher.getConfig(name, fallbackName, ConfigSearch.supplierFor(FlakeIdGeneratorConfig.class));
    }

    @Override
    public Config addFlakeIdGeneratorConfig(FlakeIdGeneratorConfig config) {
        boolean staticConfigDoesNotExist = this.checkStaticConfigDoesNotExist(this.staticConfig.getFlakeIdGeneratorConfigs(), config.getName(), config);
        if (staticConfigDoesNotExist) {
            this.configurationService.broadcastConfig(config);
        }
        return this;
    }

    @Override
    public Config setFlakeIdGeneratorConfigs(Map<String, FlakeIdGeneratorConfig> map) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public WanReplicationConfig getWanReplicationConfig(String name) {
        return this.staticConfig.getWanReplicationConfig(name);
    }

    @Override
    public Config addWanReplicationConfig(WanReplicationConfig wanReplicationConfig) {
        return this.staticConfig.addWanReplicationConfig(wanReplicationConfig);
    }

    @Override
    public Map<String, WanReplicationConfig> getWanReplicationConfigs() {
        return this.staticConfig.getWanReplicationConfigs();
    }

    @Override
    public Config setWanReplicationConfigs(Map<String, WanReplicationConfig> wanReplicationConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public JobTrackerConfig findJobTrackerConfig(String name) {
        return this.staticConfig.findJobTrackerConfig(name);
    }

    @Override
    public JobTrackerConfig getJobTrackerConfig(String name) {
        return this.staticConfig.getJobTrackerConfig(name);
    }

    @Override
    public Config addJobTrackerConfig(JobTrackerConfig jobTrackerConfig) {
        return this.staticConfig.addJobTrackerConfig(jobTrackerConfig);
    }

    @Override
    public Map<String, JobTrackerConfig> getJobTrackerConfigs() {
        return this.staticConfig.getJobTrackerConfigs();
    }

    @Override
    public Config setJobTrackerConfigs(Map<String, JobTrackerConfig> jobTrackerConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Map<String, QuorumConfig> getQuorumConfigs() {
        return this.staticConfig.getQuorumConfigs();
    }

    @Override
    public QuorumConfig getQuorumConfig(String name) {
        return this.staticConfig.getQuorumConfig(name);
    }

    @Override
    public QuorumConfig findQuorumConfig(String name) {
        return this.staticConfig.findQuorumConfig(name);
    }

    @Override
    public Config setQuorumConfigs(Map<String, QuorumConfig> quorumConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Config addQuorumConfig(QuorumConfig quorumConfig) {
        return this.staticConfig.addQuorumConfig(quorumConfig);
    }

    @Override
    public ManagementCenterConfig getManagementCenterConfig() {
        return this.staticConfig.getManagementCenterConfig();
    }

    @Override
    public Config setManagementCenterConfig(ManagementCenterConfig managementCenterConfig) {
        return this.staticConfig.setManagementCenterConfig(managementCenterConfig);
    }

    @Override
    public ServicesConfig getServicesConfig() {
        return this.staticConfig.getServicesConfig();
    }

    @Override
    public Config setServicesConfig(ServicesConfig servicesConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SecurityConfig getSecurityConfig() {
        return this.dynamicSecurityConfig;
    }

    @Override
    public Config setSecurityConfig(SecurityConfig securityConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Config addListenerConfig(ListenerConfig listenerConfig) {
        return this.staticConfig.addListenerConfig(listenerConfig);
    }

    @Override
    public List<ListenerConfig> getListenerConfigs() {
        return this.staticConfig.getListenerConfigs();
    }

    @Override
    public Config setListenerConfigs(List<ListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SerializationConfig getSerializationConfig() {
        return this.staticConfig.getSerializationConfig();
    }

    @Override
    public Config setSerializationConfig(SerializationConfig serializationConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PartitionGroupConfig getPartitionGroupConfig() {
        return this.staticConfig.getPartitionGroupConfig();
    }

    @Override
    public Config setPartitionGroupConfig(PartitionGroupConfig partitionGroupConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public HotRestartPersistenceConfig getHotRestartPersistenceConfig() {
        return this.staticConfig.getHotRestartPersistenceConfig();
    }

    @Override
    public Config setHotRestartPersistenceConfig(HotRestartPersistenceConfig hrConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public CRDTReplicationConfig getCRDTReplicationConfig() {
        return this.staticConfig.getCRDTReplicationConfig();
    }

    @Override
    public Config setCRDTReplicationConfig(CRDTReplicationConfig crdtReplicationConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ManagedContext getManagedContext() {
        return this.staticConfig.getManagedContext();
    }

    @Override
    public Config setManagedContext(ManagedContext managedContext) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
        return this.staticConfig.getUserContext();
    }

    @Override
    public Config setUserContext(ConcurrentMap<String, Object> userContext) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public NativeMemoryConfig getNativeMemoryConfig() {
        return this.staticConfig.getNativeMemoryConfig();
    }

    @Override
    public Config setNativeMemoryConfig(NativeMemoryConfig nativeMemoryConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public URL getConfigurationUrl() {
        return this.staticConfig.getConfigurationUrl();
    }

    @Override
    public Config setConfigurationUrl(URL configurationUrl) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public File getConfigurationFile() {
        return this.staticConfig.getConfigurationFile();
    }

    @Override
    public Config setConfigurationFile(File configurationFile) {
        return this.staticConfig.setConfigurationFile(configurationFile);
    }

    @Override
    public String getLicenseKey() {
        return this.staticConfig.getLicenseKey();
    }

    @Override
    public Config setLicenseKey(String licenseKey) {
        return this.staticConfig.setLicenseKey(licenseKey);
    }

    @Override
    public boolean isLiteMember() {
        return this.staticConfig.isLiteMember();
    }

    @Override
    public Config setLiteMember(boolean liteMember) {
        return this.staticConfig.setLiteMember(liteMember);
    }

    @Override
    public UserCodeDeploymentConfig getUserCodeDeploymentConfig() {
        return this.staticConfig.getUserCodeDeploymentConfig();
    }

    @Override
    public Config setUserCodeDeploymentConfig(UserCodeDeploymentConfig userCodeDeploymentConfig) {
        return this.staticConfig.setUserCodeDeploymentConfig(userCodeDeploymentConfig);
    }

    @Override
    public String toString() {
        return this.staticConfig.toString();
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.configSearcher = this.initConfigSearcher();
    }

    public void onSecurityServiceUpdated(SecurityService securityService) {
        this.dynamicSecurityConfig = new DynamicSecurityConfig(this.staticConfig.getSecurityConfig(), securityService);
    }

    private Searcher initConfigSearcher() {
        return ConfigSearch.searcherFor(this.staticConfig, this.configurationService, this.configPatternMatcher, this.isStaticFirst);
    }

    @Override
    public CPSubsystemConfig getCPSubsystemConfig() {
        return this.dynamicCPSubsystemConfig;
    }

    @Override
    public Config setCPSubsystemConfig(CPSubsystemConfig cpSubsystemConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
}

