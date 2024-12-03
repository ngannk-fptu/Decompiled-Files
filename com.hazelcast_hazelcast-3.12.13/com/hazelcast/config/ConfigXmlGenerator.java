/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AdvancedNetworkConfig;
import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.config.AliasedDiscoveryConfigUtils;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.HotRestartPersistenceConfig;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.MCMutualAuthConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NativeMemoryConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PermissionPolicyConfig;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.config.RestServerEndpointConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SecurityInterceptorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.config.ServicesConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigXmlGenerator {
    protected static final String MASK_FOR_SENSITIVE_DATA = "****";
    private static final int INDENT = 5;
    private static final ILogger LOGGER = Logger.getLogger(ConfigXmlGenerator.class);
    private final boolean formatted;
    private final boolean maskSensitiveFields;

    public ConfigXmlGenerator() {
        this(true);
    }

    public ConfigXmlGenerator(boolean formatted) {
        this(formatted, true);
    }

    public ConfigXmlGenerator(boolean formatted, boolean maskSensitiveFields) {
        this.formatted = formatted;
        this.maskSensitiveFields = maskSensitiveFields;
    }

    public String generate(Config config) {
        Preconditions.isNotNull(config, "Config");
        StringBuilder xml = new StringBuilder();
        XmlGenerator gen = new XmlGenerator(xml);
        xml.append("<hazelcast ").append("xmlns=\"http://www.hazelcast.com/schema/config\"\n").append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n").append("xsi:schemaLocation=\"http://www.hazelcast.com/schema/config ").append("http://www.hazelcast.com/schema/config/hazelcast-config-3.12.xsd\">");
        gen.open("group", new Object[0]).node("name", config.getGroupConfig().getName(), new Object[0]).node("password", this.getOrMaskValue(config.getGroupConfig().getPassword()), new Object[0]).close().node("license-key", this.getOrMaskValue(config.getLicenseKey()), new Object[0]).node("instance-name", config.getInstanceName(), new Object[0]);
        this.manCenterXmlGenerator(gen, config);
        gen.appendProperties(config.getProperties());
        ConfigXmlGenerator.securityXmlGenerator(gen, config);
        ConfigXmlGenerator.wanReplicationXmlGenerator(gen, config);
        this.networkConfigXmlGenerator(gen, config);
        this.advancedNetworkConfigXmlGenerator(gen, config);
        ConfigXmlGenerator.mapConfigXmlGenerator(gen, config);
        ConfigXmlGenerator.replicatedMapConfigXmlGenerator(gen, config);
        ConfigXmlGenerator.cacheConfigXmlGenerator(gen, config);
        ConfigXmlGenerator.queueXmlGenerator(gen, config);
        ConfigXmlGenerator.multiMapXmlGenerator(gen, config);
        ConfigXmlGenerator.collectionXmlGenerator(gen, "list", config.getListConfigs().values());
        ConfigXmlGenerator.collectionXmlGenerator(gen, "set", config.getSetConfigs().values());
        ConfigXmlGenerator.topicXmlGenerator(gen, config);
        ConfigXmlGenerator.semaphoreXmlGenerator(gen, config);
        ConfigXmlGenerator.lockXmlGenerator(gen, config);
        ConfigXmlGenerator.countDownLatchXmlGenerator(gen, config);
        ConfigXmlGenerator.ringbufferXmlGenerator(gen, config);
        ConfigXmlGenerator.atomicLongXmlGenerator(gen, config);
        ConfigXmlGenerator.atomicReferenceXmlGenerator(gen, config);
        ConfigXmlGenerator.executorXmlGenerator(gen, config);
        ConfigXmlGenerator.durableExecutorXmlGenerator(gen, config);
        ConfigXmlGenerator.scheduledExecutorXmlGenerator(gen, config);
        ConfigXmlGenerator.eventJournalXmlGenerator(gen, config);
        ConfigXmlGenerator.merkleTreeXmlGenerator(gen, config);
        ConfigXmlGenerator.partitionGroupXmlGenerator(gen, config);
        ConfigXmlGenerator.cardinalityEstimatorXmlGenerator(gen, config);
        ConfigXmlGenerator.listenerXmlGenerator(gen, config);
        ConfigXmlGenerator.serializationXmlGenerator(gen, config);
        ConfigXmlGenerator.reliableTopicXmlGenerator(gen, config);
        ConfigXmlGenerator.liteMemberXmlGenerator(gen, config);
        ConfigXmlGenerator.nativeMemoryXmlGenerator(gen, config);
        ConfigXmlGenerator.servicesXmlGenerator(gen, config);
        ConfigXmlGenerator.hotRestartXmlGenerator(gen, config);
        ConfigXmlGenerator.flakeIdGeneratorXmlGenerator(gen, config);
        ConfigXmlGenerator.crdtReplicationXmlGenerator(gen, config);
        ConfigXmlGenerator.pnCounterXmlGenerator(gen, config);
        ConfigXmlGenerator.quorumXmlGenerator(gen, config);
        ConfigXmlGenerator.cpSubsystemConfig(gen, config);
        ConfigXmlGenerator.userCodeDeploymentConfig(gen, config);
        xml.append("</hazelcast>");
        String xmlString = xml.toString();
        return this.formatted ? StringUtil.formatXml(xmlString, 5) : xmlString;
    }

    private String getOrMaskValue(String value) {
        return this.maskSensitiveFields ? MASK_FOR_SENSITIVE_DATA : value;
    }

    private void manCenterXmlGenerator(XmlGenerator gen, Config config) {
        if (config.getManagementCenterConfig() != null) {
            ManagementCenterConfig mcConfig = config.getManagementCenterConfig();
            gen.open("management-center", "enabled", mcConfig.isEnabled(), "scripting-enabled", mcConfig.isScriptingEnabled(), "update-interval", mcConfig.getUpdateInterval());
            gen.node("url", mcConfig.getUrl(), new Object[0]);
            if (mcConfig.getUrl() != null) {
                this.mcMutualAuthConfigXmlGenerator(gen, config.getManagementCenterConfig());
            }
            gen.close();
        }
    }

    private static void collectionXmlGenerator(XmlGenerator gen, String type, Collection<? extends CollectionConfig> configs) {
        if (CollectionUtil.isNotEmpty(configs)) {
            for (CollectionConfig collectionConfig : configs) {
                gen.open(type, "name", collectionConfig.getName()).node("statistics-enabled", collectionConfig.isStatisticsEnabled(), new Object[0]).node("max-size", collectionConfig.getMaxSize(), new Object[0]).node("backup-count", collectionConfig.getBackupCount(), new Object[0]).node("async-backup-count", collectionConfig.getAsyncBackupCount(), new Object[0]).node("quorum-ref", collectionConfig.getQuorumName(), new Object[0]);
                ConfigXmlGenerator.appendItemListenerConfigs(gen, collectionConfig.getItemListenerConfigs());
                MergePolicyConfig mergePolicyConfig = collectionConfig.getMergePolicyConfig();
                gen.node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
            }
        }
    }

    private static void replicatedMapConfigXmlGenerator(XmlGenerator gen, Config config) {
        for (ReplicatedMapConfig r : config.getReplicatedMapConfigs().values()) {
            MergePolicyConfig mergePolicyConfig = r.getMergePolicyConfig();
            gen.open("replicatedmap", "name", r.getName()).node("in-memory-format", (Object)r.getInMemoryFormat(), new Object[0]).node("concurrency-level", r.getConcurrencyLevel(), new Object[0]).node("replication-delay-millis", r.getReplicationDelayMillis(), new Object[0]).node("async-fillup", r.isAsyncFillup(), new Object[0]).node("statistics-enabled", r.isStatisticsEnabled(), new Object[0]).node("quorum-ref", r.getQuorumName(), new Object[0]).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize());
            if (!r.getListenerConfigs().isEmpty()) {
                gen.open("entry-listeners", new Object[0]);
                for (ListenerConfig lc : r.getListenerConfigs()) {
                    gen.node("entry-listener", ConfigXmlGenerator.classNameOrImplClass(lc.getClassName(), lc.getImplementation()), "include-value", lc.isIncludeValue(), "local", lc.isLocal());
                }
                gen.close();
            }
            gen.close();
        }
    }

    private static void listenerXmlGenerator(XmlGenerator gen, Config config) {
        if (config.getListenerConfigs().isEmpty()) {
            return;
        }
        gen.open("listeners", new Object[0]);
        for (ListenerConfig lc : config.getListenerConfigs()) {
            gen.node("listener", ConfigXmlGenerator.classNameOrImplClass(lc.getClassName(), lc.getImplementation()), new Object[0]);
        }
        gen.close();
    }

    private static void eventJournalXmlGenerator(XmlGenerator gen, Config config) {
        Collection<EventJournalConfig> mapJournalConfigs = config.getMapEventJournalConfigs().values();
        Collection<EventJournalConfig> cacheJournalConfigs = config.getCacheEventJournalConfigs().values();
        for (EventJournalConfig c : mapJournalConfigs) {
            gen.open("event-journal", "enabled", c.isEnabled()).node("mapName", c.getMapName(), new Object[0]).node("capacity", c.getCapacity(), new Object[0]).node("time-to-live-seconds", c.getTimeToLiveSeconds(), new Object[0]).close();
        }
        for (EventJournalConfig c : cacheJournalConfigs) {
            gen.open("event-journal", "enabled", c.isEnabled()).node("cacheName", c.getCacheName(), new Object[0]).node("capacity", c.getCapacity(), new Object[0]).node("time-to-live-seconds", c.getTimeToLiveSeconds(), new Object[0]).close();
        }
    }

    private static void securityXmlGenerator(XmlGenerator gen, Config config) {
        List<SecurityInterceptorConfig> sic;
        SecurityConfig c = config.getSecurityConfig();
        if (c == null) {
            return;
        }
        gen.open("security", "enabled", c.isEnabled()).node("client-block-unmapped-actions", c.getClientBlockUnmappedActions(), new Object[0]);
        PermissionPolicyConfig ppc = c.getClientPolicyConfig();
        if (ppc.getClassName() != null) {
            gen.open("client-permission-policy", "class-name", ppc.getClassName()).appendProperties(ppc.getProperties()).close();
        }
        ConfigXmlGenerator.appendLoginModules(gen, "client-login-modules", c.getClientLoginModuleConfigs());
        ConfigXmlGenerator.appendLoginModules(gen, "member-login-modules", c.getMemberLoginModuleConfigs());
        CredentialsFactoryConfig cfc = c.getMemberCredentialsConfig();
        if (cfc.getClassName() != null) {
            gen.open("member-credentials-factory", "class-name", cfc.getClassName()).appendProperties(cfc.getProperties()).close();
        }
        if (!(sic = c.getSecurityInterceptorConfigs()).isEmpty()) {
            gen.open("security-interceptors", new Object[0]);
            for (SecurityInterceptorConfig s : sic) {
                gen.open("interceptor", "class-name", s.getClassName()).close();
            }
            gen.close();
        }
        ConfigXmlGenerator.appendSecurityPermissions(gen, "client-permissions", c.getClientPermissionConfigs(), new Object[]{"on-join-operation", c.getOnJoinPermissionOperation()});
        gen.close();
    }

    private static void appendSecurityPermissions(XmlGenerator gen, String tag, Set<PermissionConfig> cpc, Object ... attributes) {
        List<PermissionConfig.PermissionType> clusterPermTypes = Arrays.asList(PermissionConfig.PermissionType.ALL, PermissionConfig.PermissionType.CONFIG, PermissionConfig.PermissionType.TRANSACTION);
        if (!cpc.isEmpty()) {
            gen.open(tag, attributes);
            for (PermissionConfig p : cpc) {
                if (clusterPermTypes.contains((Object)p.getType())) {
                    gen.open(p.getType().getNodeName(), "principal", p.getPrincipal());
                } else {
                    gen.open(p.getType().getNodeName(), "principal", p.getPrincipal(), "name", p.getName());
                }
                if (!p.getEndpoints().isEmpty()) {
                    gen.open("endpoints", new Object[0]);
                    for (String endpoint : p.getEndpoints()) {
                        gen.node("endpoint", endpoint, new Object[0]);
                    }
                    gen.close();
                }
                if (!p.getActions().isEmpty()) {
                    gen.open("actions", new Object[0]);
                    for (String action : p.getActions()) {
                        gen.node("action", action, new Object[0]);
                    }
                    gen.close();
                }
                gen.close();
            }
            gen.close();
        }
    }

    private static void appendLoginModules(XmlGenerator gen, String tag, List<LoginModuleConfig> loginModuleConfigs) {
        if (!loginModuleConfigs.isEmpty()) {
            gen.open(tag, new Object[0]);
            for (LoginModuleConfig lm : loginModuleConfigs) {
                ArrayList<String> attrs = new ArrayList<String>();
                attrs.add("class-name");
                attrs.add(lm.getClassName());
                if (lm.getUsage() != null) {
                    attrs.add("usage");
                    attrs.add(lm.getUsage().name());
                }
                gen.open("login-module", attrs.toArray()).appendProperties(lm.getProperties()).close();
            }
            gen.close();
        }
    }

    private static void serializationXmlGenerator(XmlGenerator gen, Config config) {
        SerializationConfig c = config.getSerializationConfig();
        if (c == null) {
            return;
        }
        gen.open("serialization", new Object[0]).node("portable-version", c.getPortableVersion(), new Object[0]).node("use-native-byte-order", c.isUseNativeByteOrder(), new Object[0]).node("byte-order", c.getByteOrder(), new Object[0]).node("enable-compression", c.isEnableCompression(), new Object[0]).node("enable-shared-object", c.isEnableSharedObject(), new Object[0]).node("allow-unsafe", c.isAllowUnsafe(), new Object[0]);
        Map<Integer, String> dsfClasses = c.getDataSerializableFactoryClasses();
        Map<Integer, DataSerializableFactory> dsfImpls = c.getDataSerializableFactories();
        if (!MapUtil.isNullOrEmpty(dsfClasses) || !MapUtil.isNullOrEmpty(dsfImpls)) {
            gen.open("data-serializable-factories", new Object[0]);
            ConfigXmlGenerator.appendSerializationFactory(gen, "data-serializable-factory", dsfClasses);
            ConfigXmlGenerator.appendSerializationFactory(gen, "data-serializable-factory", dsfImpls);
            gen.close();
        }
        Map<Integer, String> portableClasses = c.getPortableFactoryClasses();
        Map<Integer, PortableFactory> portableImpls = c.getPortableFactories();
        if (!MapUtil.isNullOrEmpty(portableClasses) || !MapUtil.isNullOrEmpty(portableImpls)) {
            gen.open("portable-factories", new Object[0]);
            ConfigXmlGenerator.appendSerializationFactory(gen, "portable-factory", portableClasses);
            ConfigXmlGenerator.appendSerializationFactory(gen, "portable-factory", portableImpls);
            gen.close();
        }
        Collection<SerializerConfig> serializers = c.getSerializerConfigs();
        GlobalSerializerConfig globalSerializerConfig = c.getGlobalSerializerConfig();
        if (CollectionUtil.isNotEmpty(serializers) || globalSerializerConfig != null) {
            gen.open("serializers", new Object[0]);
            if (globalSerializerConfig != null) {
                gen.node("global-serializer", ConfigXmlGenerator.classNameOrImplClass(globalSerializerConfig.getClassName(), globalSerializerConfig.getImplementation()), "override-java-serialization", globalSerializerConfig.isOverrideJavaSerialization());
            }
            if (CollectionUtil.isNotEmpty(serializers)) {
                for (SerializerConfig serializer : serializers) {
                    gen.node("serializer", null, "type-class", ConfigXmlGenerator.classNameOrClass(serializer.getTypeClassName(), serializer.getTypeClass()), "class-name", ConfigXmlGenerator.classNameOrImplClass(serializer.getClassName(), serializer.getImplementation()));
                }
            }
            gen.close();
        }
        gen.node("check-class-def-errors", c.isCheckClassDefErrors(), new Object[0]);
        JavaSerializationFilterConfig javaSerializationFilterConfig = c.getJavaSerializationFilterConfig();
        if (javaSerializationFilterConfig != null) {
            gen.open("java-serialization-filter", "defaults-disabled", javaSerializationFilterConfig.isDefaultsDisabled());
            ConfigXmlGenerator.appendFilterList(gen, "blacklist", javaSerializationFilterConfig.getBlacklist());
            ConfigXmlGenerator.appendFilterList(gen, "whitelist", javaSerializationFilterConfig.getWhitelist());
            gen.close();
        }
        gen.close();
    }

    private static String classNameOrClass(String className, Class clazz) {
        return !StringUtil.isNullOrEmpty(className) ? className : (clazz != null ? clazz.getName() : null);
    }

    private static String classNameOrImplClass(String className, Object impl) {
        return !StringUtil.isNullOrEmpty(className) ? className : (impl != null ? impl.getClass().getName() : null);
    }

    private static void partitionGroupXmlGenerator(XmlGenerator gen, Config config) {
        PartitionGroupConfig pg = config.getPartitionGroupConfig();
        if (pg == null) {
            return;
        }
        gen.open("partition-group", new Object[]{"enabled", pg.isEnabled(), "group-type", pg.getGroupType()});
        Collection<MemberGroupConfig> configs = pg.getMemberGroupConfigs();
        if (CollectionUtil.isNotEmpty(configs)) {
            for (MemberGroupConfig mgConfig : configs) {
                gen.open("member-group", new Object[0]);
                for (String iface : mgConfig.getInterfaces()) {
                    gen.node("interface", iface, new Object[0]);
                }
                gen.close();
            }
        }
        gen.close();
    }

    private static void executorXmlGenerator(XmlGenerator gen, Config config) {
        for (ExecutorConfig ex : config.getExecutorConfigs().values()) {
            gen.open("executor-service", "name", ex.getName()).node("statistics-enabled", ex.isStatisticsEnabled(), new Object[0]).node("pool-size", ex.getPoolSize(), new Object[0]).node("queue-capacity", ex.getQueueCapacity(), new Object[0]).node("quorum-ref", ex.getQuorumName(), new Object[0]).close();
        }
    }

    private static void durableExecutorXmlGenerator(XmlGenerator gen, Config config) {
        for (DurableExecutorConfig ex : config.getDurableExecutorConfigs().values()) {
            gen.open("durable-executor-service", "name", ex.getName()).node("pool-size", ex.getPoolSize(), new Object[0]).node("durability", ex.getDurability(), new Object[0]).node("capacity", ex.getCapacity(), new Object[0]).node("quorum-ref", ex.getQuorumName(), new Object[0]).close();
        }
    }

    private static void scheduledExecutorXmlGenerator(XmlGenerator gen, Config config) {
        for (ScheduledExecutorConfig ex : config.getScheduledExecutorConfigs().values()) {
            MergePolicyConfig mergePolicyConfig = ex.getMergePolicyConfig();
            gen.open("scheduled-executor-service", "name", ex.getName()).node("pool-size", ex.getPoolSize(), new Object[0]).node("durability", ex.getDurability(), new Object[0]).node("capacity", ex.getCapacity(), new Object[0]).node("quorum-ref", ex.getQuorumName(), new Object[0]).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
        }
    }

    private static void cardinalityEstimatorXmlGenerator(XmlGenerator gen, Config config) {
        for (CardinalityEstimatorConfig ex : config.getCardinalityEstimatorConfigs().values()) {
            MergePolicyConfig mergePolicyConfig = ex.getMergePolicyConfig();
            gen.open("cardinality-estimator", "name", ex.getName()).node("backup-count", ex.getBackupCount(), new Object[0]).node("async-backup-count", ex.getAsyncBackupCount(), new Object[0]).node("quorum-ref", ex.getQuorumName(), new Object[0]).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
        }
    }

    private static void pnCounterXmlGenerator(XmlGenerator gen, Config config) {
        for (PNCounterConfig counterConfig : config.getPNCounterConfigs().values()) {
            gen.open("pn-counter", "name", counterConfig.getName()).node("replica-count", counterConfig.getReplicaCount(), new Object[0]).node("quorum-ref", counterConfig.getQuorumName(), new Object[0]).node("statistics-enabled", counterConfig.isStatisticsEnabled(), new Object[0]).close();
        }
    }

    private static void semaphoreXmlGenerator(XmlGenerator gen, Config config) {
        for (SemaphoreConfig sc : config.getSemaphoreConfigs()) {
            gen.open("semaphore", "name", sc.getName()).node("initial-permits", sc.getInitialPermits(), new Object[0]).node("backup-count", sc.getBackupCount(), new Object[0]).node("async-backup-count", sc.getAsyncBackupCount(), new Object[0]).node("quorum-ref", sc.getQuorumName(), new Object[0]).close();
        }
    }

    private static void countDownLatchXmlGenerator(XmlGenerator gen, Config config) {
        for (CountDownLatchConfig lc : config.getCountDownLatchConfigs().values()) {
            gen.open("count-down-latch", "name", lc.getName()).node("quorum-ref", lc.getQuorumName(), new Object[0]).close();
        }
    }

    private static void topicXmlGenerator(XmlGenerator gen, Config config) {
        for (TopicConfig t : config.getTopicConfigs().values()) {
            gen.open("topic", "name", t.getName()).node("statistics-enabled", t.isStatisticsEnabled(), new Object[0]).node("global-ordering-enabled", t.isGlobalOrderingEnabled(), new Object[0]);
            if (!t.getMessageListenerConfigs().isEmpty()) {
                gen.open("message-listeners", new Object[0]);
                for (ListenerConfig lc : t.getMessageListenerConfigs()) {
                    gen.node("message-listener", ConfigXmlGenerator.classNameOrImplClass(lc.getClassName(), lc.getImplementation()), new Object[0]);
                }
                gen.close();
            }
            gen.node("multi-threading-enabled", t.isMultiThreadingEnabled(), new Object[0]);
            gen.close();
        }
    }

    private static void reliableTopicXmlGenerator(XmlGenerator gen, Config config) {
        for (ReliableTopicConfig t : config.getReliableTopicConfigs().values()) {
            gen.open("reliable-topic", "name", t.getName()).node("statistics-enabled", t.isStatisticsEnabled(), new Object[0]).node("read-batch-size", t.getReadBatchSize(), new Object[0]).node("topic-overload-policy", (Object)t.getTopicOverloadPolicy(), new Object[0]);
            if (!t.getMessageListenerConfigs().isEmpty()) {
                gen.open("message-listeners", new Object[0]);
                for (ListenerConfig lc : t.getMessageListenerConfigs()) {
                    gen.node("message-listener", ConfigXmlGenerator.classNameOrImplClass(lc.getClassName(), lc.getImplementation()), new Object[0]);
                }
                gen.close();
            }
            gen.close();
        }
    }

    private static void multiMapXmlGenerator(XmlGenerator gen, Config config) {
        for (MultiMapConfig mm : config.getMultiMapConfigs().values()) {
            gen.open("multimap", "name", mm.getName()).node("backup-count", mm.getBackupCount(), new Object[0]).node("async-backup-count", mm.getAsyncBackupCount(), new Object[0]).node("statistics-enabled", mm.isStatisticsEnabled(), new Object[0]).node("binary", mm.isBinary(), new Object[0]).node("quorum-ref", mm.getQuorumName(), new Object[0]).node("value-collection-type", (Object)mm.getValueCollectionType(), new Object[0]);
            ConfigXmlGenerator.entryListenerConfigXmlGenerator(gen, mm.getEntryListenerConfigs());
            MergePolicyConfig mergePolicyConfig = mm.getMergePolicyConfig();
            gen.node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
        }
    }

    private static void queueXmlGenerator(XmlGenerator gen, Config config) {
        Collection<QueueConfig> qCfgs = config.getQueueConfigs().values();
        for (QueueConfig q : qCfgs) {
            gen.open("queue", "name", q.getName()).node("statistics-enabled", q.isStatisticsEnabled(), new Object[0]).node("max-size", q.getMaxSize(), new Object[0]).node("backup-count", q.getBackupCount(), new Object[0]).node("async-backup-count", q.getAsyncBackupCount(), new Object[0]).node("empty-queue-ttl", q.getEmptyQueueTtl(), new Object[0]);
            ConfigXmlGenerator.appendItemListenerConfigs(gen, q.getItemListenerConfigs());
            QueueStoreConfig storeConfig = q.getQueueStoreConfig();
            if (storeConfig != null) {
                gen.open("queue-store", "enabled", storeConfig.isEnabled()).node("class-name", storeConfig.getClassName(), new Object[0]).node("factory-class-name", storeConfig.getFactoryClassName(), new Object[0]).appendProperties(storeConfig.getProperties()).close();
            }
            MergePolicyConfig mergePolicyConfig = q.getMergePolicyConfig();
            gen.node("quorum-ref", q.getQuorumName(), new Object[0]).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
        }
    }

    private static void lockXmlGenerator(XmlGenerator gen, Config config) {
        for (LockConfig c : config.getLockConfigs().values()) {
            gen.open("lock", "name", c.getName()).node("quorum-ref", c.getQuorumName(), new Object[0]).close();
        }
    }

    private static void ringbufferXmlGenerator(XmlGenerator gen, Config config) {
        Collection<RingbufferConfig> configs = config.getRingbufferConfigs().values();
        for (RingbufferConfig rbConfig : configs) {
            gen.open("ringbuffer", "name", rbConfig.getName()).node("capacity", rbConfig.getCapacity(), new Object[0]).node("time-to-live-seconds", rbConfig.getTimeToLiveSeconds(), new Object[0]).node("backup-count", rbConfig.getBackupCount(), new Object[0]).node("async-backup-count", rbConfig.getAsyncBackupCount(), new Object[0]).node("quorum-ref", rbConfig.getQuorumName(), new Object[0]).node("in-memory-format", (Object)rbConfig.getInMemoryFormat(), new Object[0]);
            RingbufferStoreConfig storeConfig = rbConfig.getRingbufferStoreConfig();
            if (storeConfig != null) {
                gen.open("ringbuffer-store", "enabled", storeConfig.isEnabled()).node("class-name", storeConfig.getClassName(), new Object[0]).node("factory-class-name", storeConfig.getFactoryClassName(), new Object[0]).appendProperties(storeConfig.getProperties());
                gen.close();
            }
            MergePolicyConfig mergePolicyConfig = rbConfig.getMergePolicyConfig();
            gen.node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).close();
        }
    }

    private static void atomicLongXmlGenerator(XmlGenerator gen, Config config) {
        Collection<AtomicLongConfig> configs = config.getAtomicLongConfigs().values();
        for (AtomicLongConfig atomicLongConfig : configs) {
            MergePolicyConfig mergePolicyConfig = atomicLongConfig.getMergePolicyConfig();
            gen.open("atomic-long", "name", atomicLongConfig.getName()).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).node("quorum-ref", atomicLongConfig.getQuorumName(), new Object[0]).close();
        }
    }

    private static void atomicReferenceXmlGenerator(XmlGenerator gen, Config config) {
        Collection<AtomicReferenceConfig> configs = config.getAtomicReferenceConfigs().values();
        for (AtomicReferenceConfig atomicReferenceConfig : configs) {
            MergePolicyConfig mergePolicyConfig = atomicReferenceConfig.getMergePolicyConfig();
            gen.open("atomic-reference", "name", atomicReferenceConfig.getName()).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).node("quorum-ref", atomicReferenceConfig.getQuorumName(), new Object[0]).close();
        }
    }

    private static void wanReplicationXmlGenerator(XmlGenerator gen, Config config) {
        for (WanReplicationConfig wan : config.getWanReplicationConfigs().values()) {
            gen.open("wan-replication", "name", wan.getName());
            for (WanPublisherConfig p : wan.getWanPublisherConfigs()) {
                ConfigXmlGenerator.wanReplicationPublisherXmlGenerator(gen, p);
            }
            WanConsumerConfig consumerConfig = wan.getWanConsumerConfig();
            if (consumerConfig != null) {
                ConfigXmlGenerator.wanReplicationConsumerGenerator(gen, consumerConfig);
            }
            gen.close();
        }
    }

    private static void wanReplicationConsumerGenerator(XmlGenerator gen, WanConsumerConfig consumerConfig) {
        gen.open("wan-consumer", new Object[0]);
        String consumerClassName = ConfigXmlGenerator.classNameOrImplClass(consumerConfig.getClassName(), consumerConfig.getImplementation());
        if (consumerClassName != null) {
            gen.node("class-name", consumerClassName, new Object[0]);
        }
        gen.node("persist-wan-replicated-data", consumerConfig.isPersistWanReplicatedData(), new Object[0]).appendProperties(consumerConfig.getProperties()).close();
    }

    private static void wanReplicationPublisherXmlGenerator(XmlGenerator gen, WanPublisherConfig p) {
        String publisherId = !StringUtil.isNullOrEmptyAfterTrim(p.getPublisherId()) ? p.getPublisherId() : "";
        gen.open("wan-publisher", "group-name", p.getGroupName(), "publisher-id", publisherId).node("class-name", p.getClassName(), new Object[0]).node("queue-full-behavior", (Object)p.getQueueFullBehavior(), new Object[0]).node("initial-publisher-state", (Object)p.getInitialPublisherState(), new Object[0]).node("queue-capacity", p.getQueueCapacity(), new Object[0]).appendProperties(p.getProperties());
        if (p.getEndpoint() != null) {
            gen.node("endpoint", p.getEndpoint(), new Object[0]);
        }
        ConfigXmlGenerator.wanReplicationSyncGenerator(gen, p.getWanSyncConfig());
        ConfigXmlGenerator.aliasedDiscoveryConfigsGenerator(gen, AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(p));
        ConfigXmlGenerator.discoveryStrategyConfigXmlGenerator(gen, p.getDiscoveryConfig());
        gen.close();
    }

    private static void wanReplicationSyncGenerator(XmlGenerator gen, WanSyncConfig c) {
        gen.open("wan-sync", new Object[0]).node("consistency-check-strategy", (Object)c.getConsistencyCheckStrategy(), new Object[0]).close();
    }

    private static void merkleTreeXmlGenerator(XmlGenerator gen, Config config) {
        Collection<MerkleTreeConfig> mapMerkleTreeConfigs = config.getMapMerkleTreeConfigs().values();
        for (MerkleTreeConfig c : mapMerkleTreeConfigs) {
            gen.open("merkle-tree", "enabled", c.isEnabled()).node("mapName", c.getMapName(), new Object[0]).node("depth", c.getDepth(), new Object[0]).close();
        }
    }

    private void networkConfigXmlGenerator(XmlGenerator gen, Config config) {
        if (config.getAdvancedNetworkConfig().isEnabled()) {
            return;
        }
        NetworkConfig netCfg = config.getNetworkConfig();
        gen.open("network", new Object[0]).node("public-address", netCfg.getPublicAddress(), new Object[0]).node("port", netCfg.getPort(), "port-count", netCfg.getPortCount(), "auto-increment", netCfg.isPortAutoIncrement()).node("reuse-address", netCfg.isReuseAddress(), new Object[0]);
        Collection<String> outboundPortDefinitions = netCfg.getOutboundPortDefinitions();
        if (CollectionUtil.isNotEmpty(outboundPortDefinitions)) {
            gen.open("outbound-ports", new Object[0]);
            for (String def : outboundPortDefinitions) {
                gen.node("ports", def, new Object[0]);
            }
            gen.close();
        }
        JoinConfig join = netCfg.getJoin();
        gen.open("join", new Object[0]);
        ConfigXmlGenerator.multicastConfigXmlGenerator(gen, join);
        ConfigXmlGenerator.tcpConfigXmlGenerator(gen, join);
        ConfigXmlGenerator.aliasedDiscoveryConfigsGenerator(gen, AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(join));
        ConfigXmlGenerator.discoveryStrategyConfigXmlGenerator(gen, join.getDiscoveryConfig());
        gen.close();
        ConfigXmlGenerator.interfacesConfigXmlGenerator(gen, netCfg.getInterfaces());
        this.sslConfigXmlGenerator(gen, netCfg.getSSLConfig());
        ConfigXmlGenerator.socketInterceptorConfigXmlGenerator(gen, netCfg.getSocketInterceptorConfig());
        this.symmetricEncInterceptorConfigXmlGenerator(gen, netCfg.getSymmetricEncryptionConfig());
        ConfigXmlGenerator.memberAddressProviderConfigXmlGenerator(gen, netCfg.getMemberAddressProviderConfig());
        ConfigXmlGenerator.failureDetectorConfigXmlGenerator(gen, netCfg.getIcmpFailureDetectorConfig());
        ConfigXmlGenerator.restApiXmlGenerator(gen, netCfg);
        ConfigXmlGenerator.memcacheProtocolXmlGenerator(gen, netCfg);
        gen.close();
    }

    private void advancedNetworkConfigXmlGenerator(XmlGenerator gen, Config config) {
        AdvancedNetworkConfig netCfg = config.getAdvancedNetworkConfig();
        if (!netCfg.isEnabled()) {
            return;
        }
        gen.open("advanced-network", "enabled", netCfg.isEnabled());
        JoinConfig join = netCfg.getJoin();
        gen.open("join", new Object[0]);
        ConfigXmlGenerator.multicastConfigXmlGenerator(gen, join);
        ConfigXmlGenerator.tcpConfigXmlGenerator(gen, join);
        ConfigXmlGenerator.aliasedDiscoveryConfigsGenerator(gen, AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(join));
        ConfigXmlGenerator.discoveryStrategyConfigXmlGenerator(gen, join.getDiscoveryConfig());
        gen.close();
        ConfigXmlGenerator.failureDetectorConfigXmlGenerator(gen, netCfg.getIcmpFailureDetectorConfig());
        ConfigXmlGenerator.memberAddressProviderConfigXmlGenerator(gen, netCfg.getMemberAddressProviderConfig());
        for (EndpointConfig endpointConfig : netCfg.getEndpointConfigs().values()) {
            this.endpointConfigXmlGenerator(gen, endpointConfig);
        }
        gen.close();
    }

    private void endpointConfigXmlGenerator(XmlGenerator gen, EndpointConfig endpointConfig) {
        if (endpointConfig.getName() != null) {
            gen.open(this.endpointConfigElementName(endpointConfig), "name", endpointConfig.getName());
        } else {
            gen.open(this.endpointConfigElementName(endpointConfig), new Object[0]);
        }
        Collection<String> outboundPortDefinitions = endpointConfig.getOutboundPortDefinitions();
        if (CollectionUtil.isNotEmpty(outboundPortDefinitions)) {
            gen.open("outbound-ports", new Object[0]);
            for (String def : outboundPortDefinitions) {
                gen.node("ports", def, new Object[0]);
            }
            gen.close();
        }
        ConfigXmlGenerator.interfacesConfigXmlGenerator(gen, endpointConfig.getInterfaces());
        this.sslConfigXmlGenerator(gen, endpointConfig.getSSLConfig());
        ConfigXmlGenerator.socketInterceptorConfigXmlGenerator(gen, endpointConfig.getSocketInterceptorConfig());
        this.symmetricEncInterceptorConfigXmlGenerator(gen, endpointConfig.getSymmetricEncryptionConfig());
        if (endpointConfig instanceof RestServerEndpointConfig) {
            RestServerEndpointConfig rsec = (RestServerEndpointConfig)endpointConfig;
            gen.open("endpoint-groups", new Object[0]);
            for (RestEndpointGroup group : RestEndpointGroup.values()) {
                gen.node("endpoint-group", null, "name", group.name(), "enabled", rsec.isGroupEnabled(group));
            }
            gen.close();
        }
        gen.open("socket-options", new Object[0]);
        gen.node("buffer-direct", endpointConfig.isSocketBufferDirect(), new Object[0]);
        gen.node("tcp-no-delay", endpointConfig.isSocketTcpNoDelay(), new Object[0]);
        gen.node("keep-alive", endpointConfig.isSocketKeepAlive(), new Object[0]);
        gen.node("connect-timeout-seconds", endpointConfig.getSocketConnectTimeoutSeconds(), new Object[0]);
        gen.node("send-buffer-size-kb", endpointConfig.getSocketSendBufferSizeKb(), new Object[0]);
        gen.node("receive-buffer-size-kb", endpointConfig.getSocketRcvBufferSizeKb(), new Object[0]);
        gen.node("linger-seconds", endpointConfig.getSocketLingerSeconds(), new Object[0]);
        gen.close();
        if (endpointConfig instanceof ServerSocketEndpointConfig) {
            ServerSocketEndpointConfig serverSocketEndpointConfig = (ServerSocketEndpointConfig)endpointConfig;
            gen.node("port", serverSocketEndpointConfig.getPort(), "port-count", serverSocketEndpointConfig.getPortCount(), "auto-increment", serverSocketEndpointConfig.isPortAutoIncrement()).node("public-address", serverSocketEndpointConfig.getPublicAddress(), new Object[0]).node("reuse-address", serverSocketEndpointConfig.isReuseAddress(), new Object[0]);
        }
        gen.close();
    }

    private String endpointConfigElementName(EndpointConfig endpointConfig) {
        if (endpointConfig instanceof ServerSocketEndpointConfig) {
            switch (endpointConfig.getProtocolType()) {
                case REST: {
                    return "rest-server-socket-endpoint-config";
                }
                case WAN: {
                    return "wan-server-socket-endpoint-config";
                }
                case CLIENT: {
                    return "client-server-socket-endpoint-config";
                }
                case MEMBER: {
                    return "member-server-socket-endpoint-config";
                }
                case MEMCACHE: {
                    return "memcache-server-socket-endpoint-config";
                }
            }
            throw new IllegalStateException("Not recognised protocol type");
        }
        return "wan-endpoint-config";
    }

    private static void mapConfigXmlGenerator(XmlGenerator gen, Config config) {
        Collection<MapConfig> mapConfigs = config.getMapConfigs().values();
        for (MapConfig m : mapConfigs) {
            String cacheDeserializedVal = m.getCacheDeserializedValues() != null ? m.getCacheDeserializedValues().name().replaceAll("_", "-") : null;
            MergePolicyConfig mergePolicyConfig = m.getMergePolicyConfig();
            gen.open("map", "name", m.getName()).node("in-memory-format", (Object)m.getInMemoryFormat(), new Object[0]).node("statistics-enabled", m.isStatisticsEnabled(), new Object[0]).node("cache-deserialized-values", cacheDeserializedVal, new Object[0]).node("backup-count", m.getBackupCount(), new Object[0]).node("async-backup-count", m.getAsyncBackupCount(), new Object[0]).node("time-to-live-seconds", m.getTimeToLiveSeconds(), new Object[0]).node("max-idle-seconds", m.getMaxIdleSeconds(), new Object[0]).node("eviction-policy", (Object)m.getEvictionPolicy(), new Object[0]).node("max-size", m.getMaxSizeConfig().getSize(), new Object[]{"policy", m.getMaxSizeConfig().getMaxSizePolicy()}).node("eviction-percentage", m.getEvictionPercentage(), new Object[0]).node("min-eviction-check-millis", m.getMinEvictionCheckMillis(), new Object[0]).node("merge-policy", mergePolicyConfig.getPolicy(), "batch-size", mergePolicyConfig.getBatchSize()).node("quorum-ref", m.getQuorumName(), new Object[0]).node("read-backup-data", m.isReadBackupData(), new Object[0]).node("metadata-policy", (Object)m.getMetadataPolicy(), new Object[0]);
            ConfigXmlGenerator.appendHotRestartConfig(gen, m.getHotRestartConfig());
            ConfigXmlGenerator.mapStoreConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.mapNearCacheConfigXmlGenerator(gen, m.getNearCacheConfig());
            ConfigXmlGenerator.wanReplicationConfigXmlGenerator(gen, m.getWanReplicationRef());
            ConfigXmlGenerator.mapIndexConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.mapAttributeConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.entryListenerConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.mapPartitionLostListenerConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.mapPartitionStrategyConfigXmlGenerator(gen, m);
            ConfigXmlGenerator.mapQueryCachesConfigXmlGenerator(gen, m);
            gen.close();
        }
    }

    private static void appendHotRestartConfig(XmlGenerator gen, HotRestartConfig m) {
        gen.open("hot-restart", "enabled", m != null && m.isEnabled()).node("fsync", m != null && m.isFsync(), new Object[0]).close();
    }

    private static void cacheConfigXmlGenerator(XmlGenerator gen, Config config) {
        for (CacheSimpleConfig c : config.getCacheConfigs().values()) {
            gen.open("cache", "name", c.getName());
            if (c.getKeyType() != null) {
                gen.node("key-type", null, "class-name", c.getKeyType());
            }
            if (c.getValueType() != null) {
                gen.node("value-type", null, "class-name", c.getValueType());
            }
            gen.node("statistics-enabled", c.isStatisticsEnabled(), new Object[0]).node("management-enabled", c.isManagementEnabled(), new Object[0]).node("read-through", c.isReadThrough(), new Object[0]).node("write-through", c.isWriteThrough(), new Object[0]);
            ConfigXmlGenerator.checkAndFillCacheLoaderFactoryConfigXml(gen, c.getCacheLoaderFactory());
            ConfigXmlGenerator.checkAndFillCacheLoaderConfigXml(gen, c.getCacheLoader());
            ConfigXmlGenerator.checkAndFillCacheWriterFactoryConfigXml(gen, c.getCacheWriterFactory());
            ConfigXmlGenerator.checkAndFillCacheWriterConfigXml(gen, c.getCacheWriter());
            ConfigXmlGenerator.cacheExpiryPolicyFactoryConfigXmlGenerator(gen, c.getExpiryPolicyFactoryConfig());
            gen.open("cache-entry-listeners", new Object[0]);
            for (CacheSimpleEntryListenerConfig el : c.getCacheEntryListeners()) {
                gen.open("cache-entry-listener", "old-value-required", el.isOldValueRequired(), "synchronous", el.isSynchronous()).node("cache-entry-listener-factory", null, "class-name", el.getCacheEntryListenerFactory()).node("cache-entry-event-filter-factory", null, "class-name", el.getCacheEntryEventFilterFactory()).close();
            }
            gen.close().node("in-memory-format", (Object)c.getInMemoryFormat(), new Object[0]).node("backup-count", c.getBackupCount(), new Object[0]).node("async-backup-count", c.getAsyncBackupCount(), new Object[0]);
            ConfigXmlGenerator.evictionConfigXmlGenerator(gen, c.getEvictionConfig());
            ConfigXmlGenerator.wanReplicationConfigXmlGenerator(gen, c.getWanReplicationRef());
            gen.node("quorum-ref", c.getQuorumName(), new Object[0]);
            ConfigXmlGenerator.cachePartitionLostListenerConfigXmlGenerator(gen, c.getPartitionLostListenerConfigs());
            gen.node("merge-policy", c.getMergePolicy(), new Object[0]);
            ConfigXmlGenerator.appendHotRestartConfig(gen, c.getHotRestartConfig());
            gen.node("disable-per-entry-invalidation-events", c.isDisablePerEntryInvalidationEvents(), new Object[0]).close();
        }
    }

    private static void checkAndFillCacheWriterFactoryConfigXml(XmlGenerator gen, String cacheWriter) {
        if (StringUtil.isNullOrEmpty(cacheWriter)) {
            return;
        }
        gen.node("cache-writer-factory", null, "class-name", cacheWriter);
    }

    private static void checkAndFillCacheWriterConfigXml(XmlGenerator gen, String cacheWriter) {
        if (StringUtil.isNullOrEmpty(cacheWriter)) {
            return;
        }
        gen.node("cache-writer", null, "class-name", cacheWriter);
    }

    private static void checkAndFillCacheLoaderFactoryConfigXml(XmlGenerator gen, String cacheLoader) {
        if (StringUtil.isNullOrEmpty(cacheLoader)) {
            return;
        }
        gen.node("cache-loader-factory", null, "class-name", cacheLoader);
    }

    private static void checkAndFillCacheLoaderConfigXml(XmlGenerator gen, String cacheLoader) {
        if (StringUtil.isNullOrEmpty(cacheLoader)) {
            return;
        }
        gen.node("cache-loader", null, "class-name", cacheLoader);
    }

    private static void cacheExpiryPolicyFactoryConfigXmlGenerator(XmlGenerator gen, CacheSimpleConfig.ExpiryPolicyFactoryConfig config) {
        if (config == null) {
            return;
        }
        if (!StringUtil.isNullOrEmpty(config.getClassName())) {
            gen.node("expiry-policy-factory", null, "class-name", config.getClassName());
        } else {
            CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedConfig = config.getTimedExpiryPolicyFactoryConfig();
            if (timedConfig != null && timedConfig.getExpiryPolicyType() != null && timedConfig.getDurationConfig() != null) {
                CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig duration = timedConfig.getDurationConfig();
                gen.open("expiry-policy-factory", new Object[0]).node("timed-expiry-policy-factory", null, new Object[]{"expiry-policy-type", timedConfig.getExpiryPolicyType(), "duration-amount", duration.getDurationAmount(), "time-unit", duration.getTimeUnit().name()}).close();
            }
        }
    }

    private static void cachePartitionLostListenerConfigXmlGenerator(XmlGenerator gen, List<CachePartitionLostListenerConfig> configs) {
        if (configs.isEmpty()) {
            return;
        }
        gen.open("partition-lost-listeners", new Object[0]);
        for (CachePartitionLostListenerConfig c : configs) {
            gen.node("partition-lost-listener", ConfigXmlGenerator.classNameOrImplClass(c.getClassName(), c.getImplementation()), new Object[0]);
        }
        gen.close();
    }

    private static void mapPartitionStrategyConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        if (m.getPartitioningStrategyConfig() != null) {
            PartitioningStrategyConfig psc = m.getPartitioningStrategyConfig();
            gen.node("partition-strategy", ConfigXmlGenerator.classNameOrImplClass(psc.getPartitioningStrategyClass(), psc.getPartitioningStrategy()), new Object[0]);
        }
    }

    private static void mapQueryCachesConfigXmlGenerator(XmlGenerator gen, MapConfig mapConfig) {
        List<QueryCacheConfig> queryCacheConfigs = mapConfig.getQueryCacheConfigs();
        if (queryCacheConfigs != null && !queryCacheConfigs.isEmpty()) {
            gen.open("query-caches", new Object[0]);
            for (QueryCacheConfig queryCacheConfig : queryCacheConfigs) {
                gen.open("query-cache", "name", queryCacheConfig.getName());
                gen.node("include-value", queryCacheConfig.isIncludeValue(), new Object[0]);
                gen.node("in-memory-format", (Object)queryCacheConfig.getInMemoryFormat(), new Object[0]);
                gen.node("populate", queryCacheConfig.isPopulate(), new Object[0]);
                gen.node("coalesce", queryCacheConfig.isCoalesce(), new Object[0]);
                gen.node("delay-seconds", queryCacheConfig.getDelaySeconds(), new Object[0]);
                gen.node("batch-size", queryCacheConfig.getBatchSize(), new Object[0]);
                gen.node("buffer-size", queryCacheConfig.getBufferSize(), new Object[0]);
                ConfigXmlGenerator.evictionConfigXmlGenerator(gen, queryCacheConfig.getEvictionConfig());
                ConfigXmlGenerator.mapIndexConfigXmlGenerator(gen, queryCacheConfig.getIndexConfigs());
                ConfigXmlGenerator.mapQueryCachePredicateConfigXmlGenerator(gen, queryCacheConfig);
                ConfigXmlGenerator.entryListenerConfigXmlGenerator(gen, queryCacheConfig.getEntryListenerConfigs());
                gen.close();
            }
            gen.close();
        }
    }

    private static void mapQueryCachePredicateConfigXmlGenerator(XmlGenerator gen, QueryCacheConfig queryCacheConfig) {
        PredicateConfig predicateConfig = queryCacheConfig.getPredicateConfig();
        String type = predicateConfig.getClassName() != null ? "class-name" : "sql";
        String content = predicateConfig.getClassName() != null ? predicateConfig.getClassName() : predicateConfig.getSql();
        gen.node("predicate", content, "type", type);
    }

    private static void entryListenerConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        ConfigXmlGenerator.entryListenerConfigXmlGenerator(gen, m.getEntryListenerConfigs());
    }

    private static void entryListenerConfigXmlGenerator(XmlGenerator gen, List<EntryListenerConfig> entryListenerConfigs) {
        if (!entryListenerConfigs.isEmpty()) {
            gen.open("entry-listeners", new Object[0]);
            for (EntryListenerConfig lc : entryListenerConfigs) {
                gen.node("entry-listener", ConfigXmlGenerator.classNameOrImplClass(lc.getClassName(), lc.getImplementation()), "include-value", lc.isIncludeValue(), "local", lc.isLocal());
            }
            gen.close();
        }
    }

    private static void mapPartitionLostListenerConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        if (!m.getPartitionLostListenerConfigs().isEmpty()) {
            gen.open("partition-lost-listeners", new Object[0]);
            for (MapPartitionLostListenerConfig c : m.getPartitionLostListenerConfigs()) {
                gen.node("partition-lost-listener", ConfigXmlGenerator.classNameOrImplClass(c.getClassName(), c.getImplementation()), new Object[0]);
            }
            gen.close();
        }
    }

    private static void mapIndexConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        ConfigXmlGenerator.mapIndexConfigXmlGenerator(gen, m.getMapIndexConfigs());
    }

    private static void mapIndexConfigXmlGenerator(XmlGenerator gen, List<MapIndexConfig> mapIndexConfigs) {
        if (!mapIndexConfigs.isEmpty()) {
            gen.open("indexes", new Object[0]);
            for (MapIndexConfig indexCfg : mapIndexConfigs) {
                gen.node("index", indexCfg.getAttribute(), "ordered", indexCfg.isOrdered());
            }
            gen.close();
        }
    }

    private static void mapAttributeConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        if (!m.getMapAttributeConfigs().isEmpty()) {
            gen.open("attributes", new Object[0]);
            for (MapAttributeConfig attributeCfg : m.getMapAttributeConfigs()) {
                gen.node("attribute", attributeCfg.getName(), "extractor", attributeCfg.getExtractor());
            }
            gen.close();
        }
    }

    private static void wanReplicationConfigXmlGenerator(XmlGenerator gen, WanReplicationRef wan) {
        if (wan != null) {
            List<String> filters;
            gen.open("wan-replication-ref", "name", wan.getName());
            String mergePolicy = wan.getMergePolicy();
            if (!StringUtil.isNullOrEmpty(mergePolicy)) {
                gen.node("merge-policy", mergePolicy, new Object[0]);
            }
            if (CollectionUtil.isNotEmpty(filters = wan.getFilters())) {
                gen.open("filters", new Object[0]);
                for (String f : filters) {
                    gen.node("filter-impl", f, new Object[0]);
                }
                gen.close();
            }
            gen.node("republishing-enabled", wan.isRepublishingEnabled(), new Object[0]).close();
        }
    }

    private static void mapStoreConfigXmlGenerator(XmlGenerator gen, MapConfig m) {
        if (m.getMapStoreConfig() != null) {
            MapStoreConfig s = m.getMapStoreConfig();
            String clazz = s.getImplementation() != null ? s.getImplementation().getClass().getName() : s.getClassName();
            String factoryClass = s.getFactoryImplementation() != null ? s.getFactoryImplementation().getClass().getName() : s.getFactoryClassName();
            MapStoreConfig.InitialLoadMode initialMode = s.getInitialLoadMode();
            gen.open("map-store", "enabled", s.isEnabled(), "initial-mode", initialMode.toString()).node("class-name", clazz, new Object[0]).node("factory-class-name", factoryClass, new Object[0]).node("write-delay-seconds", s.getWriteDelaySeconds(), new Object[0]).node("write-batch-size", s.getWriteBatchSize(), new Object[0]).appendProperties(s.getProperties()).close();
        }
    }

    private static void mapNearCacheConfigXmlGenerator(XmlGenerator gen, NearCacheConfig n) {
        if (n != null) {
            if (n.getName() != null) {
                gen.open("near-cache", "name", n.getName());
            } else {
                gen.open("near-cache", new Object[0]);
            }
            gen.node("in-memory-format", (Object)n.getInMemoryFormat(), new Object[0]).node("invalidate-on-change", n.isInvalidateOnChange(), new Object[0]).node("time-to-live-seconds", n.getTimeToLiveSeconds(), new Object[0]).node("max-idle-seconds", n.getMaxIdleSeconds(), new Object[0]).node("serialize-keys", n.isSerializeKeys(), new Object[0]).node("cache-local-entries", n.isCacheLocalEntries(), new Object[0]).node("max-size", n.getMaxSize(), new Object[0]).node("eviction-policy", n.getEvictionPolicy(), new Object[0]);
            ConfigXmlGenerator.evictionConfigXmlGenerator(gen, n.getEvictionConfig());
            gen.close();
        }
    }

    private static void evictionConfigXmlGenerator(XmlGenerator gen, EvictionConfig e) {
        if (e == null) {
            return;
        }
        String comparatorClassName = !StringUtil.isNullOrEmpty(e.getComparatorClassName()) ? e.getComparatorClassName() : null;
        gen.node("eviction", null, new Object[]{"size", e.getSize(), "max-size-policy", e.getMaximumSizePolicy(), "eviction-policy", e.getEvictionPolicy(), "comparator-class-name", comparatorClassName});
    }

    private static void multicastConfigXmlGenerator(XmlGenerator gen, JoinConfig join) {
        MulticastConfig mcConfig = join.getMulticastConfig();
        gen.open("multicast", "enabled", mcConfig.isEnabled(), "loopbackModeEnabled", mcConfig.isLoopbackModeEnabled()).node("multicast-group", mcConfig.getMulticastGroup(), new Object[0]).node("multicast-port", mcConfig.getMulticastPort(), new Object[0]).node("multicast-timeout-seconds", mcConfig.getMulticastTimeoutSeconds(), new Object[0]).node("multicast-time-to-live", mcConfig.getMulticastTimeToLive(), new Object[0]);
        if (!mcConfig.getTrustedInterfaces().isEmpty()) {
            gen.open("trusted-interfaces", new Object[0]);
            for (String trustedInterface : mcConfig.getTrustedInterfaces()) {
                gen.node("interface", trustedInterface, new Object[0]);
            }
            gen.close();
        }
        gen.close();
    }

    private static void tcpConfigXmlGenerator(XmlGenerator gen, JoinConfig join) {
        TcpIpConfig c = join.getTcpIpConfig();
        gen.open("tcp-ip", "enabled", c.isEnabled(), "connection-timeout-seconds", c.getConnectionTimeoutSeconds()).open("member-list", new Object[0]);
        for (String m : c.getMembers()) {
            gen.node("member", m, new Object[0]);
        }
        gen.close().node("required-member", c.getRequiredMember(), new Object[0]).close();
    }

    private static void aliasedDiscoveryConfigsGenerator(XmlGenerator gen, List<AliasedDiscoveryConfig<?>> configs) {
        if (configs == null) {
            return;
        }
        for (AliasedDiscoveryConfig<?> c : configs) {
            gen.open(AliasedDiscoveryConfigUtils.tagFor(c), "enabled", c.isEnabled());
            if (c.isUsePublicIp()) {
                gen.node("use-public-ip", "true", new Object[0]);
            }
            for (String key : c.getProperties().keySet()) {
                gen.node(key, c.getProperties().get(key), new Object[0]);
            }
            gen.close();
        }
    }

    private static void discoveryStrategyConfigXmlGenerator(XmlGenerator gen, DiscoveryConfig c) {
        Collection<DiscoveryStrategyConfig> configs;
        if (c == null) {
            return;
        }
        gen.open("discovery-strategies", new Object[0]);
        String nodeFilterClass = ConfigXmlGenerator.classNameOrImplClass(c.getNodeFilterClass(), c.getNodeFilter());
        if (nodeFilterClass != null) {
            gen.node("node-filter", null, "class", nodeFilterClass);
        }
        if (CollectionUtil.isNotEmpty(configs = c.getDiscoveryStrategyConfigs())) {
            for (DiscoveryStrategyConfig config : configs) {
                gen.open("discovery-strategy", "class", ConfigXmlGenerator.classNameOrImplClass(config.getClassName(), config.getDiscoveryStrategyFactory()), "enabled", "true").appendProperties(config.getProperties()).close();
            }
        }
        gen.close();
    }

    private static void interfacesConfigXmlGenerator(XmlGenerator gen, InterfacesConfig interfaces) {
        gen.open("interfaces", "enabled", interfaces.isEnabled());
        for (String i : interfaces.getInterfaces()) {
            gen.node("interface", i, new Object[0]);
        }
        gen.close();
    }

    private void sslConfigXmlGenerator(XmlGenerator gen, SSLConfig ssl) {
        gen.open("ssl", "enabled", ssl != null && ssl.isEnabled());
        if (ssl != null) {
            Properties props = new Properties();
            props.putAll((Map<?, ?>)ssl.getProperties());
            if (this.maskSensitiveFields && props.containsKey("trustStorePassword")) {
                props.setProperty("trustStorePassword", MASK_FOR_SENSITIVE_DATA);
            }
            if (this.maskSensitiveFields && props.containsKey("keyStorePassword")) {
                props.setProperty("keyStorePassword", MASK_FOR_SENSITIVE_DATA);
            }
            gen.node("factory-class-name", ConfigXmlGenerator.classNameOrImplClass(ssl.getFactoryClassName(), ssl.getFactoryImplementation()), new Object[0]).appendProperties(props);
        }
        gen.close();
    }

    private void mcMutualAuthConfigXmlGenerator(XmlGenerator gen, ManagementCenterConfig mcConfig) {
        MCMutualAuthConfig mutualAuthConfig = mcConfig.getMutualAuthConfig();
        gen.open("mutual-auth", "enabled", mutualAuthConfig != null && mutualAuthConfig.isEnabled());
        if (mutualAuthConfig != null) {
            Properties props = new Properties();
            props.putAll((Map<?, ?>)mutualAuthConfig.getProperties());
            if (this.maskSensitiveFields && props.containsKey("trustStorePassword")) {
                props.setProperty("trustStorePassword", MASK_FOR_SENSITIVE_DATA);
            }
            if (this.maskSensitiveFields && props.containsKey("keyStorePassword")) {
                props.setProperty("keyStorePassword", MASK_FOR_SENSITIVE_DATA);
            }
            gen.node("factory-class-name", ConfigXmlGenerator.classNameOrImplClass(mutualAuthConfig.getFactoryClassName(), mutualAuthConfig.getFactoryImplementation()), new Object[0]).appendProperties(props);
        }
        gen.close();
    }

    private static void socketInterceptorConfigXmlGenerator(XmlGenerator gen, SocketInterceptorConfig socket) {
        gen.open("socket-interceptor", "enabled", socket != null && socket.isEnabled());
        if (socket != null) {
            gen.node("class-name", ConfigXmlGenerator.classNameOrImplClass(socket.getClassName(), socket.getImplementation()), new Object[0]).appendProperties(socket.getProperties());
        }
        gen.close();
    }

    private void symmetricEncInterceptorConfigXmlGenerator(XmlGenerator gen, SymmetricEncryptionConfig sec) {
        if (sec == null) {
            return;
        }
        gen.open("symmetric-encryption", "enabled", sec.isEnabled()).node("algorithm", sec.getAlgorithm(), new Object[0]).node("salt", this.getOrMaskValue(sec.getSalt()), new Object[0]).node("password", this.getOrMaskValue(sec.getPassword()), new Object[0]).node("iteration-count", sec.getIterationCount(), new Object[0]).close();
    }

    private static void memberAddressProviderConfigXmlGenerator(XmlGenerator gen, MemberAddressProviderConfig memberAddressProviderConfig) {
        if (memberAddressProviderConfig == null) {
            return;
        }
        String className = ConfigXmlGenerator.classNameOrImplClass(memberAddressProviderConfig.getClassName(), memberAddressProviderConfig.getImplementation());
        if (StringUtil.isNullOrEmpty(className)) {
            return;
        }
        gen.open("member-address-provider", "enabled", memberAddressProviderConfig.isEnabled()).node("class-name", className, new Object[0]).appendProperties(memberAddressProviderConfig.getProperties()).close();
    }

    private static void failureDetectorConfigXmlGenerator(XmlGenerator gen, IcmpFailureDetectorConfig icmpFailureDetectorConfig) {
        if (icmpFailureDetectorConfig == null) {
            return;
        }
        gen.open("failure-detector", new Object[0]);
        gen.open("icmp", "enabled", icmpFailureDetectorConfig.isEnabled()).node("ttl", icmpFailureDetectorConfig.getTtl(), new Object[0]).node("interval-milliseconds", icmpFailureDetectorConfig.getIntervalMilliseconds(), new Object[0]).node("max-attempts", icmpFailureDetectorConfig.getMaxAttempts(), new Object[0]).node("timeout-milliseconds", icmpFailureDetectorConfig.getTimeoutMilliseconds(), new Object[0]).node("fail-fast-on-startup", icmpFailureDetectorConfig.isFailFastOnStartup(), new Object[0]).node("parallel-mode", icmpFailureDetectorConfig.isParallelMode(), new Object[0]).close();
        gen.close();
    }

    private static void hotRestartXmlGenerator(XmlGenerator gen, Config config) {
        HotRestartPersistenceConfig hrCfg = config.getHotRestartPersistenceConfig();
        if (hrCfg == null) {
            gen.node("hot-restart-persistence", "enabled", "false");
            return;
        }
        gen.open("hot-restart-persistence", "enabled", hrCfg.isEnabled()).node("base-dir", hrCfg.getBaseDir().getAbsolutePath(), new Object[0]);
        if (hrCfg.getBackupDir() != null) {
            gen.node("backup-dir", hrCfg.getBackupDir().getAbsolutePath(), new Object[0]);
        }
        gen.node("parallelism", hrCfg.getParallelism(), new Object[0]).node("validation-timeout-seconds", hrCfg.getValidationTimeoutSeconds(), new Object[0]).node("data-load-timeout-seconds", hrCfg.getDataLoadTimeoutSeconds(), new Object[0]).node("cluster-data-recovery-policy", (Object)hrCfg.getClusterDataRecoveryPolicy(), new Object[0]).node("auto-remove-stale-data", hrCfg.isAutoRemoveStaleData(), new Object[0]).close();
    }

    private static void flakeIdGeneratorXmlGenerator(XmlGenerator gen, Config config) {
        for (FlakeIdGeneratorConfig m : config.getFlakeIdGeneratorConfigs().values()) {
            gen.open("flake-id-generator", "name", m.getName()).node("prefetch-count", m.getPrefetchCount(), new Object[0]).node("prefetch-validity-millis", m.getPrefetchValidityMillis(), new Object[0]).node("id-offset", m.getIdOffset(), new Object[0]).node("node-id-offset", m.getNodeIdOffset(), new Object[0]).node("statistics-enabled", m.isStatisticsEnabled(), new Object[0]);
            gen.close();
        }
    }

    private static void crdtReplicationXmlGenerator(XmlGenerator gen, Config config) {
        CRDTReplicationConfig replicationConfig = config.getCRDTReplicationConfig();
        gen.open("crdt-replication", new Object[0]);
        if (replicationConfig != null) {
            gen.node("replication-period-millis", replicationConfig.getReplicationPeriodMillis(), new Object[0]).node("max-concurrent-replication-targets", replicationConfig.getMaxConcurrentReplicationTargets(), new Object[0]);
        }
        gen.close();
    }

    private static void quorumXmlGenerator(XmlGenerator gen, Config config) {
        for (QuorumConfig quorumConfig : config.getQuorumConfigs().values()) {
            gen.open("quorum", "name", quorumConfig.getName(), "enabled", quorumConfig.isEnabled()).node("quorum-size", quorumConfig.getSize(), new Object[0]).node("quorum-type", (Object)quorumConfig.getType(), new Object[0]);
            if (!quorumConfig.getListenerConfigs().isEmpty()) {
                gen.open("quorum-listeners", new Object[0]);
                for (QuorumListenerConfig listenerConfig : quorumConfig.getListenerConfigs()) {
                    gen.node("quorum-listener", ConfigXmlGenerator.classNameOrImplClass(listenerConfig.getClassName(), listenerConfig.getImplementation()), new Object[0]);
                }
                gen.close();
            }
            ConfigXmlGenerator.handleQuorumFunction(gen, quorumConfig);
            gen.close();
        }
    }

    private static void cpSubsystemConfig(XmlGenerator gen, Config config) {
        CPSubsystemConfig cpSubsystemConfig = config.getCPSubsystemConfig();
        gen.open("cp-subsystem", new Object[0]).node("cp-member-count", cpSubsystemConfig.getCPMemberCount(), new Object[0]).node("group-size", cpSubsystemConfig.getGroupSize(), new Object[0]).node("session-time-to-live-seconds", cpSubsystemConfig.getSessionTimeToLiveSeconds(), new Object[0]).node("session-heartbeat-interval-seconds", cpSubsystemConfig.getSessionHeartbeatIntervalSeconds(), new Object[0]).node("missing-cp-member-auto-removal-seconds", cpSubsystemConfig.getMissingCPMemberAutoRemovalSeconds(), new Object[0]).node("fail-on-indeterminate-operation-state", cpSubsystemConfig.isFailOnIndeterminateOperationState(), new Object[0]);
        RaftAlgorithmConfig raftAlgorithmConfig = cpSubsystemConfig.getRaftAlgorithmConfig();
        gen.open("raft-algorithm", new Object[0]).node("leader-election-timeout-in-millis", raftAlgorithmConfig.getLeaderElectionTimeoutInMillis(), new Object[0]).node("leader-heartbeat-period-in-millis", raftAlgorithmConfig.getLeaderHeartbeatPeriodInMillis(), new Object[0]).node("max-missed-leader-heartbeat-count", raftAlgorithmConfig.getMaxMissedLeaderHeartbeatCount(), new Object[0]).node("append-request-max-entry-count", raftAlgorithmConfig.getAppendRequestMaxEntryCount(), new Object[0]).node("commit-index-advance-count-to-snapshot", raftAlgorithmConfig.getCommitIndexAdvanceCountToSnapshot(), new Object[0]).node("uncommitted-entry-count-to-reject-new-appends", raftAlgorithmConfig.getUncommittedEntryCountToRejectNewAppends(), new Object[0]).node("append-request-backoff-timeout-in-millis", raftAlgorithmConfig.getAppendRequestBackoffTimeoutInMillis(), new Object[0]).close();
        gen.open("semaphores", new Object[0]);
        for (CPSemaphoreConfig semaphoreConfig : cpSubsystemConfig.getSemaphoreConfigs().values()) {
            gen.open("cp-semaphore", new Object[0]).node("name", semaphoreConfig.getName(), new Object[0]).node("jdk-compatible", semaphoreConfig.isJDKCompatible(), new Object[0]).close();
        }
        gen.close().open("locks", new Object[0]);
        for (FencedLockConfig lockConfig : cpSubsystemConfig.getLockConfigs().values()) {
            gen.open("fenced-lock", new Object[0]).node("name", lockConfig.getName(), new Object[0]).node("lock-acquire-limit", lockConfig.getLockAcquireLimit(), new Object[0]).close();
        }
        gen.close().close();
    }

    private static void userCodeDeploymentConfig(XmlGenerator gen, Config config) {
        UserCodeDeploymentConfig ucdConfig = config.getUserCodeDeploymentConfig();
        gen.open("user-code-deployment", "enabled", ucdConfig.isEnabled()).node("class-cache-mode", (Object)ucdConfig.getClassCacheMode(), new Object[0]).node("provider-mode", (Object)ucdConfig.getProviderMode(), new Object[0]).node("blacklist-prefixes", ucdConfig.getBlacklistedPrefixes(), new Object[0]).node("whitelist-prefixes", ucdConfig.getWhitelistedPrefixes(), new Object[0]).node("provider-filter", ucdConfig.getProviderFilter(), new Object[0]).close();
    }

    private static void handleQuorumFunction(XmlGenerator gen, QuorumConfig quorumConfig) {
        if (quorumConfig.getQuorumFunctionImplementation() instanceof ProbabilisticQuorumFunction) {
            ProbabilisticQuorumFunction qf = (ProbabilisticQuorumFunction)quorumConfig.getQuorumFunctionImplementation();
            long acceptableHeartbeatPause = qf.getAcceptableHeartbeatPauseMillis();
            double threshold = qf.getSuspicionThreshold();
            int maxSampleSize = qf.getMaxSampleSize();
            long minStdDeviation = qf.getMinStdDeviationMillis();
            long firstHeartbeatEstimate = qf.getHeartbeatIntervalMillis();
            gen.open("probabilistic-quorum", "acceptable-heartbeat-pause-millis", acceptableHeartbeatPause, "suspicion-threshold", threshold, "max-sample-size", maxSampleSize, "min-std-deviation-millis", minStdDeviation, "heartbeat-interval-millis", firstHeartbeatEstimate);
            gen.close();
        } else if (quorumConfig.getQuorumFunctionImplementation() instanceof RecentlyActiveQuorumFunction) {
            RecentlyActiveQuorumFunction qf = (RecentlyActiveQuorumFunction)quorumConfig.getQuorumFunctionImplementation();
            gen.open("recently-active-quorum", "heartbeat-tolerance-millis", qf.getHeartbeatToleranceMillis());
            gen.close();
        } else {
            gen.node("quorum-function-class-name", ConfigXmlGenerator.classNameOrImplClass(quorumConfig.getQuorumFunctionClassName(), quorumConfig.getQuorumFunctionImplementation()), new Object[0]);
        }
    }

    private static void nativeMemoryXmlGenerator(XmlGenerator gen, Config config) {
        NativeMemoryConfig nativeMemoryConfig = config.getNativeMemoryConfig();
        if (nativeMemoryConfig == null) {
            gen.node("native-memory", null, "enabled", "false");
            return;
        }
        gen.open("native-memory", new Object[]{"enabled", nativeMemoryConfig.isEnabled(), "allocator-type", nativeMemoryConfig.getAllocatorType()}).node("size", null, new Object[]{"unit", nativeMemoryConfig.getSize().getUnit(), "value", nativeMemoryConfig.getSize().getValue()}).node("min-block-size", nativeMemoryConfig.getMinBlockSize(), new Object[0]).node("page-size", nativeMemoryConfig.getPageSize(), new Object[0]).node("metadata-space-percentage", Float.valueOf(nativeMemoryConfig.getMetadataSpacePercentage()), new Object[0]).close();
    }

    private static void servicesXmlGenerator(XmlGenerator gen, Config config) {
        ServicesConfig c = config.getServicesConfig();
        if (c == null) {
            return;
        }
        gen.open("services", "enable-defaults", c.isEnableDefaults());
        if (CollectionUtil.isNotEmpty(c.getServiceConfigs())) {
            for (ServiceConfig serviceConfig : c.getServiceConfigs()) {
                gen.open("service", "enabled", serviceConfig.isEnabled()).node("name", serviceConfig.getName(), new Object[0]).node("class-name", ConfigXmlGenerator.classNameOrImplClass(serviceConfig.getClassName(), serviceConfig.getImplementation()), new Object[0]).appendProperties(serviceConfig.getProperties()).close();
            }
        }
        gen.close();
    }

    private static void liteMemberXmlGenerator(XmlGenerator gen, Config config) {
        gen.node("lite-member", null, "enabled", config.isLiteMember());
    }

    private static void restApiXmlGenerator(XmlGenerator gen, NetworkConfig config) {
        RestApiConfig c = config.getRestApiConfig();
        if (c == null) {
            return;
        }
        gen.open("rest-api", "enabled", c.isEnabled());
        for (RestEndpointGroup group : RestEndpointGroup.values()) {
            gen.node("endpoint-group", null, "name", group.name(), "enabled", c.isGroupEnabled(group));
        }
        gen.close();
    }

    private static void memcacheProtocolXmlGenerator(XmlGenerator gen, NetworkConfig config) {
        MemcacheProtocolConfig c = config.getMemcacheProtocolConfig();
        if (c == null) {
            return;
        }
        gen.node("memcache-protocol", null, "enabled", c.isEnabled());
    }

    private static void appendItemListenerConfigs(XmlGenerator gen, Collection<ItemListenerConfig> configs) {
        if (CollectionUtil.isNotEmpty(configs)) {
            gen.open("item-listeners", new Object[0]);
            for (ItemListenerConfig lc : configs) {
                gen.node("item-listener", lc.getClassName(), "include-value", lc.isIncludeValue());
            }
            gen.close();
        }
    }

    private static void appendSerializationFactory(XmlGenerator gen, String elementName, Map<Integer, ?> factoryMap) {
        if (MapUtil.isNullOrEmpty(factoryMap)) {
            return;
        }
        for (Map.Entry<Integer, ?> factory : factoryMap.entrySet()) {
            Object value = factory.getValue();
            String className = value instanceof String ? (String)value : value.getClass().getName();
            gen.node(elementName, className, "factory-id", factory.getKey().toString());
        }
    }

    private static void appendFilterList(XmlGenerator gen, String listName, ClassFilter classFilterList) {
        if (classFilterList.isEmpty()) {
            return;
        }
        gen.open(listName, new Object[0]);
        for (String className : classFilterList.getClasses()) {
            gen.node("class", className, new Object[0]);
        }
        for (String packageName : classFilterList.getPackages()) {
            gen.node("package", packageName, new Object[0]);
        }
        for (String prefix : classFilterList.getPrefixes()) {
            gen.node("prefix", prefix, new Object[0]);
        }
        gen.close();
    }

    public static final class XmlGenerator {
        private static final int CAPACITY = 64;
        private final StringBuilder xml;
        private final ArrayDeque<String> openNodes = new ArrayDeque();

        public XmlGenerator(StringBuilder xml) {
            this.xml = xml;
        }

        public XmlGenerator open(String name, Object ... attributes) {
            XmlGenerator.appendOpenNode(this.xml, name, attributes);
            this.openNodes.addLast(name);
            return this;
        }

        public XmlGenerator node(String name, Object contents, Object ... attributes) {
            XmlGenerator.appendNode(this.xml, name, contents, attributes);
            return this;
        }

        public XmlGenerator close() {
            XmlGenerator.appendCloseNode(this.xml, this.openNodes.pollLast());
            return this;
        }

        public XmlGenerator appendLabels(Set<String> labels) {
            if (!labels.isEmpty()) {
                this.open("client-labels", new Object[0]);
                for (String label : labels) {
                    this.node("label", label, new Object[0]);
                }
                this.close();
            }
            return this;
        }

        public XmlGenerator appendProperties(Properties props) {
            if (!props.isEmpty()) {
                this.open("properties", new Object[0]);
                Set<Object> keys = props.keySet();
                for (Object key : keys) {
                    this.node("property", props.getProperty(key.toString()), "name", key.toString());
                }
                this.close();
            }
            return this;
        }

        public XmlGenerator appendProperties(Map<String, ? extends Comparable> props) {
            if (!MapUtil.isNullOrEmpty(props)) {
                this.open("properties", new Object[0]);
                for (Map.Entry<String, ? extends Comparable> entry : props.entrySet()) {
                    this.node("property", entry.getValue(), "name", entry.getKey());
                }
                this.close();
            }
            return this;
        }

        private static void appendOpenNode(StringBuilder xml, String name, Object ... attributes) {
            xml.append('<').append(name);
            XmlGenerator.appendAttributes(xml, attributes);
            xml.append('>');
        }

        private static void appendCloseNode(StringBuilder xml, String name) {
            xml.append("</").append(name).append('>');
        }

        private static void appendNode(StringBuilder xml, String name, Object contents, Object ... attributes) {
            if (contents != null || attributes.length > 0) {
                xml.append('<').append(name);
                XmlGenerator.appendAttributes(xml, attributes);
                if (contents != null) {
                    xml.append('>');
                    XmlGenerator.escapeXml(contents, xml);
                    xml.append("</").append(name).append('>');
                } else {
                    xml.append("/>");
                }
            }
        }

        private static void appendAttributes(StringBuilder xml, Object ... attributes) {
            int i = 0;
            while (i < attributes.length) {
                Object attributeValue;
                Object attributeName = attributes[i++];
                if ((attributeValue = attributes[i++]) == null) continue;
                xml.append(" ").append(attributeName).append("=\"");
                XmlGenerator.escapeXmlAttr(attributeValue, xml);
                xml.append("\"");
            }
        }

        private static void escapeXml(Object o, StringBuilder appendTo) {
            if (o == null) {
                appendTo.append("null");
                return;
            }
            String s = o.toString();
            int length = s.length();
            appendTo.ensureCapacity(appendTo.length() + length + 64);
            for (int i = 0; i < length; ++i) {
                char ch = s.charAt(i);
                if (ch == '<') {
                    appendTo.append("&lt;");
                    continue;
                }
                if (ch == '&') {
                    appendTo.append("&amp;");
                    continue;
                }
                appendTo.append(ch);
            }
        }

        private static void escapeXmlAttr(Object o, StringBuilder appendTo) {
            if (o == null) {
                appendTo.append("null");
                return;
            }
            String s = o.toString();
            int length = s.length();
            appendTo.ensureCapacity(appendTo.length() + length + 64);
            block6: for (int i = 0; i < length; ++i) {
                char ch = s.charAt(i);
                switch (ch) {
                    case '\"': {
                        appendTo.append("&quot;");
                        continue block6;
                    }
                    case '\'': {
                        appendTo.append("&#39;");
                        continue block6;
                    }
                    case '&': {
                        appendTo.append("&amp;");
                        continue block6;
                    }
                    case '<': {
                        appendTo.append("&lt;");
                        continue block6;
                    }
                    default: {
                        appendTo.append(ch);
                    }
                }
            }
        }
    }
}

