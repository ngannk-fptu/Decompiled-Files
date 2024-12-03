/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.ConfigPatternMatcher;
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
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.config.XmlConfigLocator;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.config.YamlConfigLocator;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.matcher.MatchingPointConfigPatternMatcher;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.config.ConfigUtils;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.jsm.HazelcastRuntimePermission;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.function.BiConsumer;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Config {
    private static final ILogger LOGGER = Logger.getLogger(Config.class);
    private URL configurationUrl;
    private File configurationFile;
    private ClassLoader classLoader;
    private Properties properties = new Properties();
    private String instanceName;
    private GroupConfig groupConfig = new GroupConfig();
    private NetworkConfig networkConfig = new NetworkConfig();
    private ConfigPatternMatcher configPatternMatcher = new MatchingPointConfigPatternMatcher();
    private final Map<String, MapConfig> mapConfigs = new ConcurrentHashMap<String, MapConfig>();
    private final Map<String, CacheSimpleConfig> cacheConfigs = new ConcurrentHashMap<String, CacheSimpleConfig>();
    private final Map<String, TopicConfig> topicConfigs = new ConcurrentHashMap<String, TopicConfig>();
    private final Map<String, ReliableTopicConfig> reliableTopicConfigs = new ConcurrentHashMap<String, ReliableTopicConfig>();
    private final Map<String, QueueConfig> queueConfigs = new ConcurrentHashMap<String, QueueConfig>();
    private final Map<String, LockConfig> lockConfigs = new ConcurrentHashMap<String, LockConfig>();
    private final Map<String, MultiMapConfig> multiMapConfigs = new ConcurrentHashMap<String, MultiMapConfig>();
    private final Map<String, ListConfig> listConfigs = new ConcurrentHashMap<String, ListConfig>();
    private final Map<String, SetConfig> setConfigs = new ConcurrentHashMap<String, SetConfig>();
    private final Map<String, ExecutorConfig> executorConfigs = new ConcurrentHashMap<String, ExecutorConfig>();
    private final Map<String, DurableExecutorConfig> durableExecutorConfigs = new ConcurrentHashMap<String, DurableExecutorConfig>();
    private final Map<String, ScheduledExecutorConfig> scheduledExecutorConfigs = new ConcurrentHashMap<String, ScheduledExecutorConfig>();
    private final Map<String, SemaphoreConfig> semaphoreConfigs = new ConcurrentHashMap<String, SemaphoreConfig>();
    private final Map<String, CountDownLatchConfig> countDownLatchConfigs = new ConcurrentHashMap<String, CountDownLatchConfig>();
    private final Map<String, ReplicatedMapConfig> replicatedMapConfigs = new ConcurrentHashMap<String, ReplicatedMapConfig>();
    private final Map<String, WanReplicationConfig> wanReplicationConfigs = new ConcurrentHashMap<String, WanReplicationConfig>();
    private final Map<String, JobTrackerConfig> jobTrackerConfigs = new ConcurrentHashMap<String, JobTrackerConfig>();
    private final Map<String, QuorumConfig> quorumConfigs = new ConcurrentHashMap<String, QuorumConfig>();
    private final Map<String, RingbufferConfig> ringbufferConfigs = new ConcurrentHashMap<String, RingbufferConfig>();
    private final Map<String, CardinalityEstimatorConfig> cardinalityEstimatorConfigs = new ConcurrentHashMap<String, CardinalityEstimatorConfig>();
    private final Map<String, EventJournalConfig> mapEventJournalConfigs = new ConcurrentHashMap<String, EventJournalConfig>();
    private final Map<String, EventJournalConfig> cacheEventJournalConfigs = new ConcurrentHashMap<String, EventJournalConfig>();
    private final Map<String, MerkleTreeConfig> mapMerkleTreeConfigs = new ConcurrentHashMap<String, MerkleTreeConfig>();
    private final Map<String, FlakeIdGeneratorConfig> flakeIdGeneratorConfigMap = new ConcurrentHashMap<String, FlakeIdGeneratorConfig>();
    private final Map<String, AtomicLongConfig> atomicLongConfigs = new ConcurrentHashMap<String, AtomicLongConfig>();
    private final Map<String, AtomicReferenceConfig> atomicReferenceConfigs = new ConcurrentHashMap<String, AtomicReferenceConfig>();
    private final Map<String, PNCounterConfig> pnCounterConfigs = new ConcurrentHashMap<String, PNCounterConfig>();
    private AdvancedNetworkConfig advancedNetworkConfig = new AdvancedNetworkConfig();
    private ServicesConfig servicesConfig = new ServicesConfig();
    private SecurityConfig securityConfig = new SecurityConfig();
    private final List<ListenerConfig> listenerConfigs = new LinkedList<ListenerConfig>();
    private PartitionGroupConfig partitionGroupConfig = new PartitionGroupConfig();
    private ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
    private SerializationConfig serializationConfig = new SerializationConfig();
    private ManagedContext managedContext;
    private ConcurrentMap<String, Object> userContext = new ConcurrentHashMap<String, Object>();
    private MemberAttributeConfig memberAttributeConfig = new MemberAttributeConfig();
    private NativeMemoryConfig nativeMemoryConfig = new NativeMemoryConfig();
    private HotRestartPersistenceConfig hotRestartPersistenceConfig = new HotRestartPersistenceConfig();
    private UserCodeDeploymentConfig userCodeDeploymentConfig = new UserCodeDeploymentConfig();
    private CRDTReplicationConfig crdtReplicationConfig = new CRDTReplicationConfig();
    private String licenseKey;
    private boolean liteMember;
    private CPSubsystemConfig cpSubsystemConfig = new CPSubsystemConfig();

    public Config() {
    }

    public Config(String instanceName) {
        this.instanceName = instanceName;
    }

    public static Config load() {
        XmlConfigLocator xmlConfigLocator = new XmlConfigLocator();
        YamlConfigLocator yamlConfigLocator = new YamlConfigLocator();
        if (yamlConfigLocator.locateFromSystemProperty()) {
            return new YamlConfigBuilder(yamlConfigLocator).build();
        }
        if (xmlConfigLocator.locateFromSystemProperty()) {
            return new XmlConfigBuilder(xmlConfigLocator).build();
        }
        if (xmlConfigLocator.locateInWorkDirOrOnClasspath()) {
            return new XmlConfigBuilder(xmlConfigLocator).build();
        }
        if (yamlConfigLocator.locateInWorkDirOrOnClasspath()) {
            return new YamlConfigBuilder(yamlConfigLocator).build();
        }
        xmlConfigLocator.locateDefault();
        return new XmlConfigBuilder(xmlConfigLocator).build();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Config setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public ConfigPatternMatcher getConfigPatternMatcher() {
        return this.configPatternMatcher;
    }

    public void setConfigPatternMatcher(ConfigPatternMatcher configPatternMatcher) {
        if (configPatternMatcher == null) {
            throw new IllegalArgumentException("ConfigPatternMatcher is not allowed to be null!");
        }
        this.configPatternMatcher = configPatternMatcher;
    }

    public String getProperty(String name) {
        String value = this.properties.getProperty(name);
        return value != null ? value : System.getProperty(name);
    }

    public Config setProperty(String name, String value) {
        this.properties.put(name, value);
        return this;
    }

    public MemberAttributeConfig getMemberAttributeConfig() {
        return this.memberAttributeConfig;
    }

    public void setMemberAttributeConfig(MemberAttributeConfig memberAttributeConfig) {
        this.memberAttributeConfig = memberAttributeConfig;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Config setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public Config setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public GroupConfig getGroupConfig() {
        return this.groupConfig;
    }

    public Config setGroupConfig(GroupConfig groupConfig) {
        this.groupConfig = groupConfig;
        return this;
    }

    public NetworkConfig getNetworkConfig() {
        return this.networkConfig;
    }

    public Config setNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        return this;
    }

    public MapConfig findMapConfig(String name) {
        MapConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getMapConfig("default").getAsReadOnly();
    }

    public MapConfig getMapConfigOrNull(String name) {
        name = StringPartitioningStrategy.getBaseName(name);
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapConfigs, name);
    }

    public MapConfig getMapConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.mapConfigs, name, MapConfig.class);
    }

    public Config addMapConfig(MapConfig mapConfig) {
        this.mapConfigs.put(mapConfig.getName(), mapConfig);
        return this;
    }

    public Map<String, MapConfig> getMapConfigs() {
        return this.mapConfigs;
    }

    public Config setMapConfigs(Map<String, MapConfig> mapConfigs) {
        this.mapConfigs.clear();
        this.mapConfigs.putAll(mapConfigs);
        for (Map.Entry<String, MapConfig> entry : this.mapConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public CacheSimpleConfig findCacheConfig(String name) {
        CacheSimpleConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cacheConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getCacheConfig("default").getAsReadOnly();
    }

    public CacheSimpleConfig findCacheConfigOrNull(String name) {
        name = StringPartitioningStrategy.getBaseName(name);
        return ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cacheConfigs, name);
    }

    public CacheSimpleConfig getCacheConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.cacheConfigs, name, CacheSimpleConfig.class);
    }

    public Config addCacheConfig(CacheSimpleConfig cacheConfig) {
        this.cacheConfigs.put(cacheConfig.getName(), cacheConfig);
        return this;
    }

    public Map<String, CacheSimpleConfig> getCacheConfigs() {
        return this.cacheConfigs;
    }

    public Config setCacheConfigs(Map<String, CacheSimpleConfig> cacheConfigs) {
        this.cacheConfigs.clear();
        this.cacheConfigs.putAll(cacheConfigs);
        for (Map.Entry<String, CacheSimpleConfig> entry : this.cacheConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public QueueConfig findQueueConfig(String name) {
        QueueConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.queueConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getQueueConfig("default").getAsReadOnly();
    }

    public QueueConfig getQueueConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.queueConfigs, name, QueueConfig.class);
    }

    public Config addQueueConfig(QueueConfig queueConfig) {
        this.queueConfigs.put(queueConfig.getName(), queueConfig);
        return this;
    }

    public Map<String, QueueConfig> getQueueConfigs() {
        return this.queueConfigs;
    }

    public Config setQueueConfigs(Map<String, QueueConfig> queueConfigs) {
        this.queueConfigs.clear();
        this.queueConfigs.putAll(queueConfigs);
        for (Map.Entry<String, QueueConfig> entry : queueConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public LockConfig findLockConfig(String name) {
        LockConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.lockConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getLockConfig("default").getAsReadOnly();
    }

    public LockConfig getLockConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.lockConfigs, name, LockConfig.class);
    }

    public Config addLockConfig(LockConfig lockConfig) {
        this.lockConfigs.put(lockConfig.getName(), lockConfig);
        return this;
    }

    public Map<String, LockConfig> getLockConfigs() {
        return this.lockConfigs;
    }

    public Config setLockConfigs(Map<String, LockConfig> lockConfigs) {
        this.lockConfigs.clear();
        this.lockConfigs.putAll(lockConfigs);
        for (Map.Entry<String, LockConfig> entry : lockConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public ListConfig findListConfig(String name) {
        ListConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.listConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getListConfig("default").getAsReadOnly();
    }

    public ListConfig getListConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.listConfigs, name, ListConfig.class);
    }

    public Config addListConfig(ListConfig listConfig) {
        this.listConfigs.put(listConfig.getName(), listConfig);
        return this;
    }

    public Map<String, ListConfig> getListConfigs() {
        return this.listConfigs;
    }

    public Config setListConfigs(Map<String, ListConfig> listConfigs) {
        this.listConfigs.clear();
        this.listConfigs.putAll(listConfigs);
        for (Map.Entry<String, ListConfig> entry : listConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public SetConfig findSetConfig(String name) {
        SetConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.setConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getSetConfig("default").getAsReadOnly();
    }

    public SetConfig getSetConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.setConfigs, name, SetConfig.class);
    }

    public Config addSetConfig(SetConfig setConfig) {
        this.setConfigs.put(setConfig.getName(), setConfig);
        return this;
    }

    public Map<String, SetConfig> getSetConfigs() {
        return this.setConfigs;
    }

    public Config setSetConfigs(Map<String, SetConfig> setConfigs) {
        this.setConfigs.clear();
        this.setConfigs.putAll(setConfigs);
        for (Map.Entry<String, SetConfig> entry : setConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public MultiMapConfig findMultiMapConfig(String name) {
        MultiMapConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.multiMapConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getMultiMapConfig("default").getAsReadOnly();
    }

    public MultiMapConfig getMultiMapConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.multiMapConfigs, name, MultiMapConfig.class);
    }

    public Config addMultiMapConfig(MultiMapConfig multiMapConfig) {
        this.multiMapConfigs.put(multiMapConfig.getName(), multiMapConfig);
        return this;
    }

    public Map<String, MultiMapConfig> getMultiMapConfigs() {
        return this.multiMapConfigs;
    }

    public Config setMultiMapConfigs(Map<String, MultiMapConfig> multiMapConfigs) {
        this.multiMapConfigs.clear();
        this.multiMapConfigs.putAll(multiMapConfigs);
        for (Map.Entry<String, MultiMapConfig> entry : this.multiMapConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public ReplicatedMapConfig findReplicatedMapConfig(String name) {
        ReplicatedMapConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.replicatedMapConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getReplicatedMapConfig("default").getAsReadOnly();
    }

    public ReplicatedMapConfig getReplicatedMapConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.replicatedMapConfigs, name, ReplicatedMapConfig.class);
    }

    public Config addReplicatedMapConfig(ReplicatedMapConfig replicatedMapConfig) {
        this.replicatedMapConfigs.put(replicatedMapConfig.getName(), replicatedMapConfig);
        return this;
    }

    public Map<String, ReplicatedMapConfig> getReplicatedMapConfigs() {
        return this.replicatedMapConfigs;
    }

    public Config setReplicatedMapConfigs(Map<String, ReplicatedMapConfig> replicatedMapConfigs) {
        this.replicatedMapConfigs.clear();
        this.replicatedMapConfigs.putAll(replicatedMapConfigs);
        for (Map.Entry<String, ReplicatedMapConfig> entry : this.replicatedMapConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public RingbufferConfig findRingbufferConfig(String name) {
        RingbufferConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.ringbufferConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getRingbufferConfig("default").getAsReadOnly();
    }

    public RingbufferConfig getRingbufferConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.ringbufferConfigs, name, RingbufferConfig.class);
    }

    public Config addRingBufferConfig(RingbufferConfig ringbufferConfig) {
        this.ringbufferConfigs.put(ringbufferConfig.getName(), ringbufferConfig);
        return this;
    }

    public Map<String, RingbufferConfig> getRingbufferConfigs() {
        return this.ringbufferConfigs;
    }

    public Config setRingbufferConfigs(Map<String, RingbufferConfig> ringbufferConfigs) {
        this.ringbufferConfigs.clear();
        this.ringbufferConfigs.putAll(ringbufferConfigs);
        for (Map.Entry<String, RingbufferConfig> entry : ringbufferConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public AtomicLongConfig findAtomicLongConfig(String name) {
        AtomicLongConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.atomicLongConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getAtomicLongConfig("default").getAsReadOnly();
    }

    public AtomicLongConfig getAtomicLongConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.atomicLongConfigs, name, AtomicLongConfig.class);
    }

    public Config addAtomicLongConfig(AtomicLongConfig atomicLongConfig) {
        this.atomicLongConfigs.put(atomicLongConfig.getName(), atomicLongConfig);
        return this;
    }

    public Map<String, AtomicLongConfig> getAtomicLongConfigs() {
        return this.atomicLongConfigs;
    }

    public Config setAtomicLongConfigs(Map<String, AtomicLongConfig> atomicLongConfigs) {
        this.atomicLongConfigs.clear();
        this.atomicLongConfigs.putAll(atomicLongConfigs);
        for (Map.Entry<String, AtomicLongConfig> entry : atomicLongConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public AtomicReferenceConfig findAtomicReferenceConfig(String name) {
        AtomicReferenceConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.atomicReferenceConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getAtomicReferenceConfig("default").getAsReadOnly();
    }

    public AtomicReferenceConfig getAtomicReferenceConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.atomicReferenceConfigs, name, AtomicReferenceConfig.class);
    }

    public Config addAtomicReferenceConfig(AtomicReferenceConfig atomicReferenceConfig) {
        this.atomicReferenceConfigs.put(atomicReferenceConfig.getName(), atomicReferenceConfig);
        return this;
    }

    public Map<String, AtomicReferenceConfig> getAtomicReferenceConfigs() {
        return this.atomicReferenceConfigs;
    }

    public Config setAtomicReferenceConfigs(Map<String, AtomicReferenceConfig> atomicReferenceConfigs) {
        this.atomicReferenceConfigs.clear();
        this.atomicReferenceConfigs.putAll(atomicReferenceConfigs);
        for (Map.Entry<String, AtomicReferenceConfig> entry : atomicReferenceConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public CountDownLatchConfig findCountDownLatchConfig(String name) {
        CountDownLatchConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.countDownLatchConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getCountDownLatchConfig("default").getAsReadOnly();
    }

    public CountDownLatchConfig getCountDownLatchConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.countDownLatchConfigs, name, CountDownLatchConfig.class);
    }

    public Config addCountDownLatchConfig(CountDownLatchConfig countDownLatchConfig) {
        this.countDownLatchConfigs.put(countDownLatchConfig.getName(), countDownLatchConfig);
        return this;
    }

    public Map<String, CountDownLatchConfig> getCountDownLatchConfigs() {
        return this.countDownLatchConfigs;
    }

    public Config setCountDownLatchConfigs(Map<String, CountDownLatchConfig> countDownLatchConfigs) {
        this.countDownLatchConfigs.clear();
        this.countDownLatchConfigs.putAll(countDownLatchConfigs);
        for (Map.Entry<String, CountDownLatchConfig> entry : countDownLatchConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public TopicConfig findTopicConfig(String name) {
        TopicConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.topicConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getTopicConfig("default").getAsReadOnly();
    }

    public TopicConfig getTopicConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.topicConfigs, name, TopicConfig.class);
    }

    public Config addTopicConfig(TopicConfig topicConfig) {
        this.topicConfigs.put(topicConfig.getName(), topicConfig);
        return this;
    }

    public ReliableTopicConfig findReliableTopicConfig(String name) {
        ReliableTopicConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.reliableTopicConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getReliableTopicConfig("default").getAsReadOnly();
    }

    public ReliableTopicConfig getReliableTopicConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.reliableTopicConfigs, name, ReliableTopicConfig.class);
    }

    public Map<String, ReliableTopicConfig> getReliableTopicConfigs() {
        return this.reliableTopicConfigs;
    }

    public Config addReliableTopicConfig(ReliableTopicConfig topicConfig) {
        this.reliableTopicConfigs.put(topicConfig.getName(), topicConfig);
        return this;
    }

    public Config setReliableTopicConfigs(Map<String, ReliableTopicConfig> reliableTopicConfigs) {
        this.reliableTopicConfigs.clear();
        this.reliableTopicConfigs.putAll(reliableTopicConfigs);
        for (Map.Entry<String, ReliableTopicConfig> entry : reliableTopicConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, TopicConfig> getTopicConfigs() {
        return this.topicConfigs;
    }

    public Config setTopicConfigs(Map<String, TopicConfig> topicConfigs) {
        this.topicConfigs.clear();
        this.topicConfigs.putAll(topicConfigs);
        for (Map.Entry<String, TopicConfig> entry : this.topicConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public ExecutorConfig findExecutorConfig(String name) {
        ExecutorConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.executorConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getExecutorConfig("default").getAsReadOnly();
    }

    public DurableExecutorConfig findDurableExecutorConfig(String name) {
        DurableExecutorConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.durableExecutorConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getDurableExecutorConfig("default").getAsReadOnly();
    }

    public ScheduledExecutorConfig findScheduledExecutorConfig(String name) {
        ScheduledExecutorConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.scheduledExecutorConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getScheduledExecutorConfig("default").getAsReadOnly();
    }

    public CardinalityEstimatorConfig findCardinalityEstimatorConfig(String name) {
        CardinalityEstimatorConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cardinalityEstimatorConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getCardinalityEstimatorConfig("default").getAsReadOnly();
    }

    public PNCounterConfig findPNCounterConfig(String name) {
        PNCounterConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.pnCounterConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getPNCounterConfig("default").getAsReadOnly();
    }

    public ExecutorConfig getExecutorConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.executorConfigs, name, ExecutorConfig.class);
    }

    public DurableExecutorConfig getDurableExecutorConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.durableExecutorConfigs, name, DurableExecutorConfig.class);
    }

    public ScheduledExecutorConfig getScheduledExecutorConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.scheduledExecutorConfigs, name, ScheduledExecutorConfig.class);
    }

    public CardinalityEstimatorConfig getCardinalityEstimatorConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.cardinalityEstimatorConfigs, name, CardinalityEstimatorConfig.class);
    }

    public PNCounterConfig getPNCounterConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.pnCounterConfigs, name, PNCounterConfig.class);
    }

    public Config addExecutorConfig(ExecutorConfig executorConfig) {
        this.executorConfigs.put(executorConfig.getName(), executorConfig);
        return this;
    }

    public Config addDurableExecutorConfig(DurableExecutorConfig durableExecutorConfig) {
        this.durableExecutorConfigs.put(durableExecutorConfig.getName(), durableExecutorConfig);
        return this;
    }

    public Config addScheduledExecutorConfig(ScheduledExecutorConfig scheduledExecutorConfig) {
        this.scheduledExecutorConfigs.put(scheduledExecutorConfig.getName(), scheduledExecutorConfig);
        return this;
    }

    public Config addCardinalityEstimatorConfig(CardinalityEstimatorConfig cardinalityEstimatorConfig) {
        this.cardinalityEstimatorConfigs.put(cardinalityEstimatorConfig.getName(), cardinalityEstimatorConfig);
        return this;
    }

    public Config addPNCounterConfig(PNCounterConfig pnCounterConfig) {
        this.pnCounterConfigs.put(pnCounterConfig.getName(), pnCounterConfig);
        return this;
    }

    public Map<String, ExecutorConfig> getExecutorConfigs() {
        return this.executorConfigs;
    }

    public Config setExecutorConfigs(Map<String, ExecutorConfig> executorConfigs) {
        this.executorConfigs.clear();
        this.executorConfigs.putAll(executorConfigs);
        for (Map.Entry<String, ExecutorConfig> entry : executorConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, DurableExecutorConfig> getDurableExecutorConfigs() {
        return this.durableExecutorConfigs;
    }

    public Config setDurableExecutorConfigs(Map<String, DurableExecutorConfig> durableExecutorConfigs) {
        this.durableExecutorConfigs.clear();
        this.durableExecutorConfigs.putAll(durableExecutorConfigs);
        for (Map.Entry<String, DurableExecutorConfig> entry : durableExecutorConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, ScheduledExecutorConfig> getScheduledExecutorConfigs() {
        return this.scheduledExecutorConfigs;
    }

    public Config setScheduledExecutorConfigs(Map<String, ScheduledExecutorConfig> scheduledExecutorConfigs) {
        this.scheduledExecutorConfigs.clear();
        this.scheduledExecutorConfigs.putAll(scheduledExecutorConfigs);
        for (Map.Entry<String, ScheduledExecutorConfig> entry : scheduledExecutorConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, CardinalityEstimatorConfig> getCardinalityEstimatorConfigs() {
        return this.cardinalityEstimatorConfigs;
    }

    public Config setCardinalityEstimatorConfigs(Map<String, CardinalityEstimatorConfig> cardinalityEstimatorConfigs) {
        this.cardinalityEstimatorConfigs.clear();
        this.cardinalityEstimatorConfigs.putAll(cardinalityEstimatorConfigs);
        for (Map.Entry<String, CardinalityEstimatorConfig> entry : cardinalityEstimatorConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, PNCounterConfig> getPNCounterConfigs() {
        return this.pnCounterConfigs;
    }

    public Config setPNCounterConfigs(Map<String, PNCounterConfig> pnCounterConfigs) {
        this.pnCounterConfigs.clear();
        this.pnCounterConfigs.putAll(pnCounterConfigs);
        for (Map.Entry<String, PNCounterConfig> entry : pnCounterConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public SemaphoreConfig findSemaphoreConfig(String name) {
        SemaphoreConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.semaphoreConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getSemaphoreConfig("default").getAsReadOnly();
    }

    public SemaphoreConfig getSemaphoreConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.semaphoreConfigs, name, SemaphoreConfig.class);
    }

    public Config addSemaphoreConfig(SemaphoreConfig semaphoreConfig) {
        this.semaphoreConfigs.put(semaphoreConfig.getName(), semaphoreConfig);
        return this;
    }

    public Collection<SemaphoreConfig> getSemaphoreConfigs() {
        return this.semaphoreConfigs.values();
    }

    public Map<String, SemaphoreConfig> getSemaphoreConfigsAsMap() {
        return this.semaphoreConfigs;
    }

    public Config setSemaphoreConfigs(Map<String, SemaphoreConfig> semaphoreConfigs) {
        this.semaphoreConfigs.clear();
        this.semaphoreConfigs.putAll(semaphoreConfigs);
        for (Map.Entry<String, SemaphoreConfig> entry : this.semaphoreConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public WanReplicationConfig getWanReplicationConfig(String name) {
        return this.wanReplicationConfigs.get(name);
    }

    public Config addWanReplicationConfig(WanReplicationConfig wanReplicationConfig) {
        this.wanReplicationConfigs.put(wanReplicationConfig.getName(), wanReplicationConfig);
        return this;
    }

    public Map<String, WanReplicationConfig> getWanReplicationConfigs() {
        return this.wanReplicationConfigs;
    }

    public Config setWanReplicationConfigs(Map<String, WanReplicationConfig> wanReplicationConfigs) {
        this.wanReplicationConfigs.clear();
        this.wanReplicationConfigs.putAll(wanReplicationConfigs);
        for (Map.Entry<String, WanReplicationConfig> entry : this.wanReplicationConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public JobTrackerConfig findJobTrackerConfig(String name) {
        JobTrackerConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.jobTrackerConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getJobTrackerConfig("default").getAsReadOnly();
    }

    public JobTrackerConfig getJobTrackerConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.jobTrackerConfigs, name, JobTrackerConfig.class);
    }

    public Config addJobTrackerConfig(JobTrackerConfig jobTrackerConfig) {
        this.jobTrackerConfigs.put(jobTrackerConfig.getName(), jobTrackerConfig);
        return this;
    }

    public Map<String, JobTrackerConfig> getJobTrackerConfigs() {
        return this.jobTrackerConfigs;
    }

    public Config setJobTrackerConfigs(Map<String, JobTrackerConfig> jobTrackerConfigs) {
        this.jobTrackerConfigs.clear();
        this.jobTrackerConfigs.putAll(jobTrackerConfigs);
        for (Map.Entry<String, JobTrackerConfig> entry : this.jobTrackerConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, QuorumConfig> getQuorumConfigs() {
        return this.quorumConfigs;
    }

    public QuorumConfig getQuorumConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.quorumConfigs, name, QuorumConfig.class);
    }

    public QuorumConfig findQuorumConfig(String name) {
        QuorumConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.quorumConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config;
        }
        return this.getQuorumConfig("default");
    }

    public Config setQuorumConfigs(Map<String, QuorumConfig> quorumConfigs) {
        this.quorumConfigs.clear();
        this.quorumConfigs.putAll(quorumConfigs);
        for (Map.Entry<String, QuorumConfig> entry : this.quorumConfigs.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Config addQuorumConfig(QuorumConfig quorumConfig) {
        this.quorumConfigs.put(quorumConfig.getName(), quorumConfig);
        return this;
    }

    public ManagementCenterConfig getManagementCenterConfig() {
        return this.managementCenterConfig;
    }

    public Config setManagementCenterConfig(ManagementCenterConfig managementCenterConfig) {
        this.managementCenterConfig = managementCenterConfig;
        return this;
    }

    public ServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }

    public Config setServicesConfig(ServicesConfig servicesConfig) {
        this.servicesConfig = servicesConfig;
        return this;
    }

    public SecurityConfig getSecurityConfig() {
        return this.securityConfig;
    }

    public Config setSecurityConfig(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
        return this;
    }

    public Config addListenerConfig(ListenerConfig listenerConfig) {
        this.getListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<ListenerConfig> getListenerConfigs() {
        return this.listenerConfigs;
    }

    public Config setListenerConfigs(List<ListenerConfig> listenerConfigs) {
        this.listenerConfigs.clear();
        this.listenerConfigs.addAll(listenerConfigs);
        return this;
    }

    public EventJournalConfig findMapEventJournalConfig(String name) {
        EventJournalConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapEventJournalConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getMapEventJournalConfig("default").getAsReadOnly();
    }

    public EventJournalConfig findCacheEventJournalConfig(String name) {
        EventJournalConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.cacheEventJournalConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getCacheEventJournalConfig("default").getAsReadOnly();
    }

    public EventJournalConfig getMapEventJournalConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.mapEventJournalConfigs, name, EventJournalConfig.class, new BiConsumer<EventJournalConfig, String>(){

            @Override
            public void accept(EventJournalConfig eventJournalConfig, String name) {
                eventJournalConfig.setMapName(name);
                if ("default".equals(name)) {
                    eventJournalConfig.setEnabled(false);
                }
            }
        });
    }

    public EventJournalConfig getCacheEventJournalConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.cacheEventJournalConfigs, name, EventJournalConfig.class, new BiConsumer<EventJournalConfig, String>(){

            @Override
            public void accept(EventJournalConfig eventJournalConfig, String name) {
                eventJournalConfig.setCacheName(name);
                if ("default".equals(name)) {
                    eventJournalConfig.setEnabled(false);
                }
            }
        });
    }

    public Config addEventJournalConfig(EventJournalConfig eventJournalConfig) {
        String mapName = eventJournalConfig.getMapName();
        String cacheName = eventJournalConfig.getCacheName();
        if (StringUtil.isNullOrEmpty(mapName) && StringUtil.isNullOrEmpty(cacheName)) {
            throw new IllegalArgumentException("Event journal config should have either map name or cache name non-empty");
        }
        if (!StringUtil.isNullOrEmpty(mapName)) {
            this.mapEventJournalConfigs.put(mapName, eventJournalConfig);
        }
        if (!StringUtil.isNullOrEmpty(cacheName)) {
            this.cacheEventJournalConfigs.put(cacheName, eventJournalConfig);
        }
        return this;
    }

    public MerkleTreeConfig findMapMerkleTreeConfig(String name) {
        MerkleTreeConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.mapMerkleTreeConfigs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config.getAsReadOnly();
        }
        return this.getMapMerkleTreeConfig("default").getAsReadOnly();
    }

    public MerkleTreeConfig getMapMerkleTreeConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.mapMerkleTreeConfigs, name, MerkleTreeConfig.class, new BiConsumer<MerkleTreeConfig, String>(){

            @Override
            public void accept(MerkleTreeConfig merkleTreeConfig, String name) {
                merkleTreeConfig.setMapName(name);
                if ("default".equals(name)) {
                    merkleTreeConfig.setEnabled(false);
                }
            }
        });
    }

    public Config addMerkleTreeConfig(MerkleTreeConfig merkleTreeConfig) {
        String mapName = merkleTreeConfig.getMapName();
        if (StringUtil.isNullOrEmpty(mapName)) {
            throw new IllegalArgumentException("Merkle tree config must define a map name");
        }
        this.mapMerkleTreeConfigs.put(mapName, merkleTreeConfig);
        return this;
    }

    public Map<String, FlakeIdGeneratorConfig> getFlakeIdGeneratorConfigs() {
        return this.flakeIdGeneratorConfigMap;
    }

    public FlakeIdGeneratorConfig findFlakeIdGeneratorConfig(String name) {
        String baseName = StringPartitioningStrategy.getBaseName(name);
        FlakeIdGeneratorConfig config = ConfigUtils.lookupByPattern(this.configPatternMatcher, this.flakeIdGeneratorConfigMap, baseName);
        if (config != null) {
            return config;
        }
        return this.getFlakeIdGeneratorConfig("default");
    }

    public FlakeIdGeneratorConfig getFlakeIdGeneratorConfig(String name) {
        return ConfigUtils.getConfig(this.configPatternMatcher, this.flakeIdGeneratorConfigMap, name, FlakeIdGeneratorConfig.class, new BiConsumer<FlakeIdGeneratorConfig, String>(){

            @Override
            public void accept(FlakeIdGeneratorConfig flakeIdGeneratorConfig, String name) {
                flakeIdGeneratorConfig.setName(name);
            }
        });
    }

    public Config addFlakeIdGeneratorConfig(FlakeIdGeneratorConfig config) {
        this.flakeIdGeneratorConfigMap.put(config.getName(), config);
        return this;
    }

    public Config setFlakeIdGeneratorConfigs(Map<String, FlakeIdGeneratorConfig> map) {
        this.flakeIdGeneratorConfigMap.clear();
        this.flakeIdGeneratorConfigMap.putAll(map);
        for (Map.Entry<String, FlakeIdGeneratorConfig> entry : map.entrySet()) {
            entry.getValue().setName(entry.getKey());
        }
        return this;
    }

    public Map<String, EventJournalConfig> getMapEventJournalConfigs() {
        return this.mapEventJournalConfigs;
    }

    public Map<String, EventJournalConfig> getCacheEventJournalConfigs() {
        return this.cacheEventJournalConfigs;
    }

    public Config setMapEventJournalConfigs(Map<String, EventJournalConfig> eventJournalConfigs) {
        this.mapEventJournalConfigs.clear();
        this.mapEventJournalConfigs.putAll(eventJournalConfigs);
        for (Map.Entry<String, EventJournalConfig> entry : eventJournalConfigs.entrySet()) {
            entry.getValue().setMapName(entry.getKey());
        }
        return this;
    }

    public Config setCacheEventJournalConfigs(Map<String, EventJournalConfig> eventJournalConfigs) {
        this.cacheEventJournalConfigs.clear();
        this.cacheEventJournalConfigs.putAll(eventJournalConfigs);
        for (Map.Entry<String, EventJournalConfig> entry : eventJournalConfigs.entrySet()) {
            entry.getValue().setCacheName(entry.getKey());
        }
        return this;
    }

    public Map<String, MerkleTreeConfig> getMapMerkleTreeConfigs() {
        return this.mapMerkleTreeConfigs;
    }

    public Config setMapMerkleTreeConfigs(Map<String, MerkleTreeConfig> merkleTreeConfigs) {
        this.mapMerkleTreeConfigs.clear();
        this.mapMerkleTreeConfigs.putAll(merkleTreeConfigs);
        for (Map.Entry<String, MerkleTreeConfig> entry : merkleTreeConfigs.entrySet()) {
            entry.getValue().setMapName(entry.getKey());
        }
        return this;
    }

    public SerializationConfig getSerializationConfig() {
        return this.serializationConfig;
    }

    public Config setSerializationConfig(SerializationConfig serializationConfig) {
        this.serializationConfig = serializationConfig;
        return this;
    }

    public PartitionGroupConfig getPartitionGroupConfig() {
        return this.partitionGroupConfig;
    }

    public Config setPartitionGroupConfig(PartitionGroupConfig partitionGroupConfig) {
        this.partitionGroupConfig = partitionGroupConfig;
        return this;
    }

    public HotRestartPersistenceConfig getHotRestartPersistenceConfig() {
        return this.hotRestartPersistenceConfig;
    }

    public Config setHotRestartPersistenceConfig(HotRestartPersistenceConfig hrConfig) {
        Preconditions.checkNotNull(hrConfig, "Hot restart config cannot be null!");
        this.hotRestartPersistenceConfig = hrConfig;
        return this;
    }

    public CRDTReplicationConfig getCRDTReplicationConfig() {
        return this.crdtReplicationConfig;
    }

    public Config setCRDTReplicationConfig(CRDTReplicationConfig crdtReplicationConfig) {
        Preconditions.checkNotNull(crdtReplicationConfig, "The CRDT replication config cannot be null!");
        this.crdtReplicationConfig = crdtReplicationConfig;
        return this;
    }

    public ManagedContext getManagedContext() {
        return this.managedContext;
    }

    public Config setManagedContext(ManagedContext managedContext) {
        this.managedContext = managedContext;
        return this;
    }

    public ConcurrentMap<String, Object> getUserContext() {
        return this.userContext;
    }

    public Config setUserContext(ConcurrentMap<String, Object> userContext) {
        if (userContext == null) {
            throw new IllegalArgumentException("userContext can't be null");
        }
        this.userContext = userContext;
        return this;
    }

    public NativeMemoryConfig getNativeMemoryConfig() {
        return this.nativeMemoryConfig;
    }

    public Config setNativeMemoryConfig(NativeMemoryConfig nativeMemoryConfig) {
        this.nativeMemoryConfig = nativeMemoryConfig;
        return this;
    }

    public URL getConfigurationUrl() {
        return this.configurationUrl;
    }

    public Config setConfigurationUrl(URL configurationUrl) {
        this.configurationUrl = configurationUrl;
        return this;
    }

    public File getConfigurationFile() {
        return this.configurationFile;
    }

    public Config setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
        return this;
    }

    public String getLicenseKey() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new HazelcastRuntimePermission("com.hazelcast.config.Config.getLicenseKey"));
        }
        return this.licenseKey;
    }

    public Config setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
        return this;
    }

    public boolean isLiteMember() {
        return this.liteMember;
    }

    public Config setLiteMember(boolean liteMember) {
        this.liteMember = liteMember;
        return this;
    }

    public UserCodeDeploymentConfig getUserCodeDeploymentConfig() {
        return this.userCodeDeploymentConfig;
    }

    public Config setUserCodeDeploymentConfig(UserCodeDeploymentConfig userCodeDeploymentConfig) {
        this.userCodeDeploymentConfig = userCodeDeploymentConfig;
        return this;
    }

    public AdvancedNetworkConfig getAdvancedNetworkConfig() {
        return this.advancedNetworkConfig;
    }

    public Config setAdvancedNetworkConfig(AdvancedNetworkConfig advancedNetworkConfig) {
        this.advancedNetworkConfig = advancedNetworkConfig;
        return this;
    }

    public CPSubsystemConfig getCPSubsystemConfig() {
        return this.cpSubsystemConfig;
    }

    public Config setCPSubsystemConfig(CPSubsystemConfig cpSubsystemConfig) {
        this.cpSubsystemConfig = cpSubsystemConfig;
        return this;
    }

    public String toString() {
        return "Config{groupConfig=" + this.groupConfig + ", properties=" + this.properties + ", networkConfig=" + this.networkConfig + ", mapConfigs=" + this.mapConfigs + ", topicConfigs=" + this.topicConfigs + ", reliableTopicConfigs=" + this.reliableTopicConfigs + ", queueConfigs=" + this.queueConfigs + ", multiMapConfigs=" + this.multiMapConfigs + ", executorConfigs=" + this.executorConfigs + ", semaphoreConfigs=" + this.semaphoreConfigs + ", countDownLatchConfigs=" + this.countDownLatchConfigs + ", ringbufferConfigs=" + this.ringbufferConfigs + ", atomicLongConfigs=" + this.atomicLongConfigs + ", atomicReferenceConfigs=" + this.atomicReferenceConfigs + ", wanReplicationConfigs=" + this.wanReplicationConfigs + ", listenerConfigs=" + this.listenerConfigs + ", mapEventJournalConfigs=" + this.mapEventJournalConfigs + ", cacheEventJournalConfigs=" + this.cacheEventJournalConfigs + ", partitionGroupConfig=" + this.partitionGroupConfig + ", managementCenterConfig=" + this.managementCenterConfig + ", securityConfig=" + this.securityConfig + ", liteMember=" + this.liteMember + ", crdtReplicationConfig=" + this.crdtReplicationConfig + ", advancedNetworkConfig=" + this.advancedNetworkConfig + ", cpSubsystemConfig=" + this.cpSubsystemConfig + '}';
    }
}

