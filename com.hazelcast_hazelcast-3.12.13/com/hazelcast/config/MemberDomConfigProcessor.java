/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractDomConfigProcessor;
import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.config.AliasedDiscoveryConfigUtils;
import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CRDTReplicationConfig;
import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigSections;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.ConsistencyCheckStrategy;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.HotRestartClusterDataRecoveryPolicy;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.HotRestartPersistenceConfig;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListConfig;
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
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MemberAddressProviderConfig;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.OnJoinPermissionOperationName;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PermissionPolicyConfig;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.ProbabilisticQuorumConfigBuilder;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumConfigBuilder;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.config.RecentlyActiveQuorumConfigBuilder;
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
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.config.ServicesConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.config.WANQueueFullBehavior;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanPublisherState;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.spi.ServiceConfigurationParser;
import com.hazelcast.topic.TopicOverloadPolicy;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.function.Function;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class MemberDomConfigProcessor
extends AbstractDomConfigProcessor {
    private static final ILogger LOGGER = Logger.getLogger(MemberDomConfigProcessor.class);
    protected final Config config;

    MemberDomConfigProcessor(boolean domLevel3, Config config) {
        super(domLevel3);
        this.config = config;
    }

    @Override
    public void buildConfig(Node rootNode) throws Exception {
        for (Node node : DomConfigHelper.childElements(rootNode)) {
            String nodeName = DomConfigHelper.cleanNodeName(node);
            if (this.occurrenceSet.contains(nodeName)) {
                throw new InvalidConfigurationException("Duplicate '" + nodeName + "' definition found in the configuration.");
            }
            if (this.handleNode(node, nodeName) || ConfigSections.canOccurMultipleTimes(nodeName)) continue;
            this.occurrenceSet.add(nodeName);
        }
        if (this.occurrenceSet.contains("network") && this.occurrenceSet.contains("advanced-network") && this.config.getAdvancedNetworkConfig().isEnabled()) {
            throw new InvalidConfigurationException("Ambiguous configuration: cannot include both <network> and an enabled <advanced-network> element. Configure network using one of <network> or <advanced-network enabled=\"true\">.");
        }
    }

    private boolean handleNode(Node node, String nodeName) throws Exception {
        if (ConfigSections.INSTANCE_NAME.isEqual(nodeName)) {
            this.handleInstanceName(node);
        } else if (ConfigSections.NETWORK.isEqual(nodeName)) {
            this.handleNetwork(node);
        } else {
            if (ConfigSections.IMPORT.isEqual(nodeName)) {
                throw new HazelcastException("Non-expanded <import> element found");
            }
            if (ConfigSections.GROUP.isEqual(nodeName)) {
                this.handleGroup(node);
            } else if (ConfigSections.PROPERTIES.isEqual(nodeName)) {
                this.fillProperties(node, this.config.getProperties());
            } else if (ConfigSections.WAN_REPLICATION.isEqual(nodeName)) {
                this.handleWanReplication(node);
            } else if (ConfigSections.EXECUTOR_SERVICE.isEqual(nodeName)) {
                this.handleExecutor(node);
            } else if (ConfigSections.DURABLE_EXECUTOR_SERVICE.isEqual(nodeName)) {
                this.handleDurableExecutor(node);
            } else if (ConfigSections.SCHEDULED_EXECUTOR_SERVICE.isEqual(nodeName)) {
                this.handleScheduledExecutor(node);
            } else if (ConfigSections.EVENT_JOURNAL.isEqual(nodeName)) {
                this.handleEventJournal(node);
            } else if (ConfigSections.MERKLE_TREE.isEqual(nodeName)) {
                this.handleMerkleTree(node);
            } else if (ConfigSections.SERVICES.isEqual(nodeName)) {
                this.handleServices(node);
            } else if (ConfigSections.QUEUE.isEqual(nodeName)) {
                this.handleQueue(node);
            } else if (ConfigSections.MAP.isEqual(nodeName)) {
                this.handleMap(node);
            } else if (ConfigSections.MULTIMAP.isEqual(nodeName)) {
                this.handleMultiMap(node);
            } else if (ConfigSections.REPLICATED_MAP.isEqual(nodeName)) {
                this.handleReplicatedMap(node);
            } else if (ConfigSections.LIST.isEqual(nodeName)) {
                this.handleList(node);
            } else if (ConfigSections.SET.isEqual(nodeName)) {
                this.handleSet(node);
            } else if (ConfigSections.TOPIC.isEqual(nodeName)) {
                this.handleTopic(node);
            } else if (ConfigSections.RELIABLE_TOPIC.isEqual(nodeName)) {
                this.handleReliableTopic(node);
            } else if (ConfigSections.CACHE.isEqual(nodeName)) {
                this.handleCache(node);
            } else if (ConfigSections.NATIVE_MEMORY.isEqual(nodeName)) {
                this.fillNativeMemoryConfig(node, this.config.getNativeMemoryConfig());
            } else if (ConfigSections.JOB_TRACKER.isEqual(nodeName)) {
                this.handleJobTracker(node);
            } else if (ConfigSections.SEMAPHORE.isEqual(nodeName)) {
                this.handleSemaphore(node);
            } else if (ConfigSections.LOCK.isEqual(nodeName)) {
                this.handleLock(node);
            } else if (ConfigSections.RINGBUFFER.isEqual(nodeName)) {
                this.handleRingbuffer(node);
            } else if (ConfigSections.ATOMIC_LONG.isEqual(nodeName)) {
                this.handleAtomicLong(node);
            } else if (ConfigSections.ATOMIC_REFERENCE.isEqual(nodeName)) {
                this.handleAtomicReference(node);
            } else if (ConfigSections.COUNT_DOWN_LATCH.isEqual(nodeName)) {
                this.handleCountDownLatchConfig(node);
            } else if (ConfigSections.LISTENERS.isEqual(nodeName)) {
                this.handleListeners(node);
            } else if (ConfigSections.PARTITION_GROUP.isEqual(nodeName)) {
                this.handlePartitionGroup(node);
            } else if (ConfigSections.SERIALIZATION.isEqual(nodeName)) {
                this.handleSerialization(node);
            } else if (ConfigSections.SECURITY.isEqual(nodeName)) {
                this.handleSecurity(node);
            } else if (ConfigSections.MEMBER_ATTRIBUTES.isEqual(nodeName)) {
                this.handleMemberAttributes(node);
            } else if (ConfigSections.LICENSE_KEY.isEqual(nodeName)) {
                this.config.setLicenseKey(this.getTextContent(node));
            } else if (ConfigSections.MANAGEMENT_CENTER.isEqual(nodeName)) {
                this.handleManagementCenterConfig(node);
            } else if (ConfigSections.QUORUM.isEqual(nodeName)) {
                this.handleQuorum(node);
            } else if (ConfigSections.LITE_MEMBER.isEqual(nodeName)) {
                this.handleLiteMember(node);
            } else if (ConfigSections.HOT_RESTART_PERSISTENCE.isEqual(nodeName)) {
                this.handleHotRestartPersistence(node);
            } else if (ConfigSections.USER_CODE_DEPLOYMENT.isEqual(nodeName)) {
                this.handleUserCodeDeployment(node);
            } else if (ConfigSections.CARDINALITY_ESTIMATOR.isEqual(nodeName)) {
                this.handleCardinalityEstimator(node);
            } else if (ConfigSections.FLAKE_ID_GENERATOR.isEqual(nodeName)) {
                this.handleFlakeIdGenerator(node);
            } else if (ConfigSections.CRDT_REPLICATION.isEqual(nodeName)) {
                this.handleCRDTReplication(node);
            } else if (ConfigSections.PN_COUNTER.isEqual(nodeName)) {
                this.handlePNCounter(node);
            } else if (ConfigSections.ADVANCED_NETWORK.isEqual(nodeName)) {
                this.handleAdvancedNetwork(node);
            } else if (ConfigSections.CP_SUBSYSTEM.isEqual(nodeName)) {
                this.handleCPSubsystem(node);
            } else {
                return true;
            }
        }
        return false;
    }

    private void handleInstanceName(Node node) {
        String instanceName = this.getTextContent(node);
        if (instanceName.isEmpty()) {
            throw new InvalidConfigurationException("Instance name in XML configuration is empty");
        }
        this.config.setInstanceName(instanceName);
    }

    private void handleUserCodeDeployment(Node dcRoot) {
        UserCodeDeploymentConfig dcConfig = new UserCodeDeploymentConfig();
        Node attrEnabled = dcRoot.getAttributes().getNamedItem("enabled");
        boolean enabled = DomConfigHelper.getBooleanValue(this.getTextContent(attrEnabled));
        dcConfig.setEnabled(enabled);
        String classCacheModeName = "class-cache-mode";
        String providerModeName = "provider-mode";
        String blacklistPrefixesName = "blacklist-prefixes";
        String whitelistPrefixesName = "whitelist-prefixes";
        String providerFilterName = "provider-filter";
        for (Node n : DomConfigHelper.childElements(dcRoot)) {
            String value;
            String name = DomConfigHelper.cleanNodeName(n);
            if (classCacheModeName.equals(name)) {
                value = this.getTextContent(n);
                UserCodeDeploymentConfig.ClassCacheMode classCacheMode = UserCodeDeploymentConfig.ClassCacheMode.valueOf(value);
                dcConfig.setClassCacheMode(classCacheMode);
                continue;
            }
            if (providerModeName.equals(name)) {
                value = this.getTextContent(n);
                UserCodeDeploymentConfig.ProviderMode providerMode = UserCodeDeploymentConfig.ProviderMode.valueOf(value);
                dcConfig.setProviderMode(providerMode);
                continue;
            }
            if (blacklistPrefixesName.equals(name)) {
                value = this.getTextContent(n);
                dcConfig.setBlacklistedPrefixes(value);
                continue;
            }
            if (whitelistPrefixesName.equals(name)) {
                value = this.getTextContent(n);
                dcConfig.setWhitelistedPrefixes(value);
                continue;
            }
            if (!providerFilterName.equals(name)) continue;
            value = this.getTextContent(n);
            dcConfig.setProviderFilter(value);
        }
        this.config.setUserCodeDeploymentConfig(dcConfig);
    }

    private void handleHotRestartPersistence(Node hrRoot) {
        HotRestartPersistenceConfig hrConfig = new HotRestartPersistenceConfig().setEnabled(DomConfigHelper.getBooleanValue(this.getAttribute(hrRoot, "enabled")));
        String parallelismName = "parallelism";
        String validationTimeoutName = "validation-timeout-seconds";
        String dataLoadTimeoutName = "data-load-timeout-seconds";
        String clusterDataRecoveryPolicyName = "cluster-data-recovery-policy";
        String autoRemoveStaleDataName = "auto-remove-stale-data";
        for (Node n : DomConfigHelper.childElements(hrRoot)) {
            String name = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n);
            if ("base-dir".equals(name)) {
                hrConfig.setBaseDir(new File(value).getAbsoluteFile());
                continue;
            }
            if ("backup-dir".equals(name)) {
                hrConfig.setBackupDir(new File(value).getAbsoluteFile());
                continue;
            }
            if (parallelismName.equals(name)) {
                hrConfig.setParallelism(DomConfigHelper.getIntegerValue(parallelismName, value));
                continue;
            }
            if (validationTimeoutName.equals(name)) {
                hrConfig.setValidationTimeoutSeconds(DomConfigHelper.getIntegerValue(validationTimeoutName, value));
                continue;
            }
            if (dataLoadTimeoutName.equals(name)) {
                hrConfig.setDataLoadTimeoutSeconds(DomConfigHelper.getIntegerValue(dataLoadTimeoutName, value));
                continue;
            }
            if (clusterDataRecoveryPolicyName.equals(name)) {
                hrConfig.setClusterDataRecoveryPolicy(HotRestartClusterDataRecoveryPolicy.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if (!autoRemoveStaleDataName.equals(name)) continue;
            hrConfig.setAutoRemoveStaleData(DomConfigHelper.getBooleanValue(value));
        }
        this.config.setHotRestartPersistenceConfig(hrConfig);
    }

    private void handleCRDTReplication(Node root) {
        CRDTReplicationConfig replicationConfig = new CRDTReplicationConfig();
        String replicationPeriodMillisName = "replication-period-millis";
        String maxConcurrentReplicationTargetsName = "max-concurrent-replication-targets";
        for (Node n : DomConfigHelper.childElements(root)) {
            String name = DomConfigHelper.cleanNodeName(n);
            if ("replication-period-millis".equals(name)) {
                replicationConfig.setReplicationPeriodMillis(DomConfigHelper.getIntegerValue("replication-period-millis", this.getTextContent(n)));
                continue;
            }
            if (!"max-concurrent-replication-targets".equals(name)) continue;
            replicationConfig.setMaxConcurrentReplicationTargets(DomConfigHelper.getIntegerValue("max-concurrent-replication-targets", this.getTextContent(n)));
        }
        this.config.setCRDTReplicationConfig(replicationConfig);
    }

    private void handleLiteMember(Node node) {
        Node attrEnabled = node.getAttributes().getNamedItem("enabled");
        boolean liteMember = attrEnabled != null && DomConfigHelper.getBooleanValue(this.getTextContent(attrEnabled));
        this.config.setLiteMember(liteMember);
    }

    protected void handleQuorum(Node node) {
        QuorumConfig quorumConfig = new QuorumConfig();
        String name = this.getAttribute(node, "name");
        quorumConfig.setName(name);
        this.handleQuorumNode(node, quorumConfig, name);
    }

    protected void handleQuorumNode(Node node, QuorumConfig quorumConfig, String name) {
        Node attrEnabled = node.getAttributes().getNamedItem("enabled");
        boolean enabled = attrEnabled != null && DomConfigHelper.getBooleanValue(this.getTextContent(attrEnabled));
        QuorumConfigBuilder quorumConfigBuilder = null;
        quorumConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = this.getTextContent(n).trim();
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("quorum-size".equals(nodeName)) {
                quorumConfig.setSize(DomConfigHelper.getIntegerValue("quorum-size", value));
                continue;
            }
            if ("quorum-listeners".equals(nodeName)) {
                this.handleQuorumListeners(quorumConfig, n);
                continue;
            }
            if ("quorum-type".equals(nodeName)) {
                quorumConfig.setType(QuorumType.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("quorum-function-class-name".equals(nodeName)) {
                quorumConfig.setQuorumFunctionClassName(value);
                continue;
            }
            if ("recently-active-quorum".equals(nodeName)) {
                quorumConfigBuilder = this.handleRecentlyActiveQuorum(name, n, quorumConfig.getSize());
                continue;
            }
            if (!"probabilistic-quorum".equals(nodeName)) continue;
            quorumConfigBuilder = this.handleProbabilisticQuorum(name, n, quorumConfig.getSize());
        }
        if (quorumConfigBuilder != null) {
            boolean quorumFunctionDefinedByClassName;
            boolean bl = quorumFunctionDefinedByClassName = !StringUtil.isNullOrEmpty(quorumConfig.getQuorumFunctionClassName());
            if (quorumFunctionDefinedByClassName) {
                throw new ConfigurationException("A quorum cannot simultaneously define probabilistic-quorum or recently-active-quorum and a quorum function class name.");
            }
            QuorumConfig constructedConfig = quorumConfigBuilder.build();
            constructedConfig.setSize(quorumConfig.getSize());
            constructedConfig.setType(quorumConfig.getType());
            constructedConfig.setListenerConfigs(quorumConfig.getListenerConfigs());
            quorumConfig = constructedConfig;
        }
        this.config.addQuorumConfig(quorumConfig);
    }

    protected void handleQuorumListeners(QuorumConfig quorumConfig, Node n) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"quorum-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            String listenerClass = this.getTextContent(listenerNode);
            quorumConfig.addListenerConfig(new QuorumListenerConfig(listenerClass));
        }
    }

    private QuorumConfigBuilder handleRecentlyActiveQuorum(String name, Node node, int quorumSize) {
        int heartbeatToleranceMillis = DomConfigHelper.getIntegerValue("heartbeat-tolerance-millis", this.getAttribute(node, "heartbeat-tolerance-millis"), RecentlyActiveQuorumConfigBuilder.DEFAULT_HEARTBEAT_TOLERANCE_MILLIS);
        RecentlyActiveQuorumConfigBuilder quorumConfigBuilder = QuorumConfig.newRecentlyActiveQuorumConfigBuilder(name, quorumSize, heartbeatToleranceMillis);
        return quorumConfigBuilder;
    }

    private QuorumConfigBuilder handleProbabilisticQuorum(String name, Node node, int quorumSize) {
        long acceptableHeartPause = DomConfigHelper.getLongValue("acceptable-heartbeat-pause-millis", this.getAttribute(node, "acceptable-heartbeat-pause-millis"), ProbabilisticQuorumConfigBuilder.DEFAULT_HEARTBEAT_PAUSE_MILLIS);
        double threshold = DomConfigHelper.getDoubleValue("suspicion-threshold", this.getAttribute(node, "suspicion-threshold"), ProbabilisticQuorumConfigBuilder.DEFAULT_PHI_THRESHOLD);
        int maxSampleSize = DomConfigHelper.getIntegerValue("max-sample-size", this.getAttribute(node, "max-sample-size"), ProbabilisticQuorumConfigBuilder.DEFAULT_SAMPLE_SIZE);
        long minStdDeviation = DomConfigHelper.getLongValue("min-std-deviation-millis", this.getAttribute(node, "min-std-deviation-millis"), ProbabilisticQuorumConfigBuilder.DEFAULT_MIN_STD_DEVIATION);
        long heartbeatIntervalMillis = DomConfigHelper.getLongValue("heartbeat-interval-millis", this.getAttribute(node, "heartbeat-interval-millis"), ProbabilisticQuorumConfigBuilder.DEFAULT_HEARTBEAT_INTERVAL_MILLIS);
        ProbabilisticQuorumConfigBuilder quorumConfigBuilder = QuorumConfig.newProbabilisticQuorumConfigBuilder(name, quorumSize).withAcceptableHeartbeatPauseMillis(acceptableHeartPause).withSuspicionThreshold(threshold).withHeartbeatIntervalMillis(heartbeatIntervalMillis).withMinStdDeviationMillis(minStdDeviation).withMaxSampleSize(maxSampleSize);
        return quorumConfigBuilder;
    }

    private void handleServices(Node node) {
        Node attDefaults = node.getAttributes().getNamedItem("enable-defaults");
        boolean enableDefaults = attDefaults == null || DomConfigHelper.getBooleanValue(this.getTextContent(attDefaults));
        ServicesConfig servicesConfig = this.config.getServicesConfig();
        servicesConfig.setEnableDefaults(enableDefaults);
        this.handleServiceNodes(node, servicesConfig);
    }

    protected void handleServiceNodes(Node node, ServicesConfig servicesConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"service".equals(nodeName)) continue;
            ServiceConfig serviceConfig = new ServiceConfig();
            String enabledValue = this.getAttribute(child, "enabled");
            boolean enabled = DomConfigHelper.getBooleanValue(enabledValue);
            serviceConfig.setEnabled(enabled);
            for (Node n : DomConfigHelper.childElements(child)) {
                this.handleServiceNode(n, serviceConfig);
            }
            servicesConfig.addServiceConfig(serviceConfig);
        }
    }

    protected void handleServiceNode(Node n, ServiceConfig serviceConfig) {
        String value = DomConfigHelper.cleanNodeName(n);
        if ("name".equals(value)) {
            String name = this.getTextContent(n);
            serviceConfig.setName(name);
        } else if ("class-name".equals(value)) {
            String className = this.getTextContent(n);
            serviceConfig.setClassName(className);
        } else if ("properties".equals(value)) {
            this.fillProperties(n, serviceConfig.getProperties());
        } else if ("configuration".equals(value)) {
            Node parserNode = n.getAttributes().getNamedItem("parser");
            String parserClass = this.getTextContent(parserNode);
            if (parserNode == null || parserClass == null) {
                throw new InvalidConfigurationException("Parser is required!");
            }
            try {
                ServiceConfigurationParser parser = (ServiceConfigurationParser)ClassLoaderUtil.newInstance(this.config.getClassLoader(), parserClass);
                Object obj = parser.parse((Element)n);
                serviceConfig.setConfigObject(obj);
            }
            catch (Exception e) {
                ExceptionUtil.sneakyThrow(e);
            }
        }
    }

    protected void handleWanReplication(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        WanReplicationConfig wanReplicationConfig = new WanReplicationConfig();
        wanReplicationConfig.setName(name);
        this.handleWanReplicationNode(node, wanReplicationConfig);
    }

    void handleWanReplicationNode(Node node, WanReplicationConfig wanReplicationConfig) {
        for (Node nodeTarget : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(nodeTarget);
            this.handleWanReplicationChild(wanReplicationConfig, nodeTarget, nodeName);
        }
        this.config.addWanReplicationConfig(wanReplicationConfig);
    }

    protected void handleWanReplicationChild(WanReplicationConfig wanReplicationConfig, Node nodeTarget, String nodeName) {
        if ("wan-publisher".equals(nodeName)) {
            WanPublisherConfig publisherConfig = new WanPublisherConfig();
            publisherConfig.setPublisherId(this.getAttribute(nodeTarget, "publisher-id"));
            publisherConfig.setGroupName(this.getAttribute(nodeTarget, "group-name"));
            this.handleWanPublisherNode(wanReplicationConfig, nodeTarget, publisherConfig);
        } else if ("wan-consumer".equals(nodeName)) {
            this.handleWanConsumerNode(wanReplicationConfig, nodeTarget);
        }
    }

    void handleWanPublisherNode(WanReplicationConfig wanReplicationConfig, Node nodeTarget, WanPublisherConfig publisherConfig) {
        for (Node targetChild : DomConfigHelper.childElements(nodeTarget)) {
            this.handleWanPublisherConfig(publisherConfig, targetChild);
        }
        wanReplicationConfig.addWanPublisherConfig(publisherConfig);
    }

    void handleWanConsumerNode(WanReplicationConfig wanReplicationConfig, Node nodeTarget) {
        WanConsumerConfig consumerConfig = new WanConsumerConfig();
        for (Node targetChild : DomConfigHelper.childElements(nodeTarget)) {
            this.handleWanConsumerConfig(consumerConfig, targetChild);
        }
        wanReplicationConfig.setWanConsumerConfig(consumerConfig);
    }

    void handleWanPublisherConfig(WanPublisherConfig publisherConfig, Node targetChild) {
        String targetChildName = DomConfigHelper.cleanNodeName(targetChild);
        if ("class-name".equals(targetChildName)) {
            publisherConfig.setClassName(this.getTextContent(targetChild));
        } else if ("queue-full-behavior".equals(targetChildName)) {
            String queueFullBehavior = this.getTextContent(targetChild);
            publisherConfig.setQueueFullBehavior(WANQueueFullBehavior.valueOf(StringUtil.upperCaseInternal(queueFullBehavior)));
        } else if ("initial-publisher-state".equals(targetChildName)) {
            String initialPublisherState = this.getTextContent(targetChild);
            publisherConfig.setInitialPublisherState(WanPublisherState.valueOf(StringUtil.upperCaseInternal(initialPublisherState)));
        } else if ("queue-capacity".equals(targetChildName)) {
            int queueCapacity = DomConfigHelper.getIntegerValue("queue-capacity", this.getTextContent(targetChild));
            publisherConfig.setQueueCapacity(queueCapacity);
        } else if ("properties".equals(targetChildName)) {
            this.fillProperties(targetChild, publisherConfig.getProperties());
        } else if (AliasedDiscoveryConfigUtils.supports(targetChildName)) {
            this.handleAliasedDiscoveryStrategy(publisherConfig, targetChild, targetChildName);
        } else if ("discovery-strategies".equals(targetChildName)) {
            this.handleDiscoveryStrategies(publisherConfig.getDiscoveryConfig(), targetChild);
        } else if ("wan-sync".equals(targetChildName)) {
            this.handleWanSync(publisherConfig.getWanSyncConfig(), targetChild);
        } else if ("endpoint".equals(targetChildName)) {
            publisherConfig.setEndpoint(this.getTextContent(targetChild));
        }
    }

    private void handleWanSync(WanSyncConfig wanSyncConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"consistency-check-strategy".equals(nodeName)) continue;
            String strategy = this.getTextContent(child);
            wanSyncConfig.setConsistencyCheckStrategy(ConsistencyCheckStrategy.valueOf(StringUtil.upperCaseInternal(strategy)));
        }
    }

    private void handleWanConsumerConfig(WanConsumerConfig consumerConfig, Node targetChild) {
        String targetChildName = DomConfigHelper.cleanNodeName(targetChild);
        if ("class-name".equals(targetChildName)) {
            consumerConfig.setClassName(this.getTextContent(targetChild));
        } else if ("properties".equals(targetChildName)) {
            this.fillProperties(targetChild, consumerConfig.getProperties());
        } else if ("persist-wan-replicated-data".equals(targetChildName)) {
            consumerConfig.setPersistWanReplicatedData(DomConfigHelper.getBooleanValue(this.getTextContent(targetChild)));
        }
    }

    private void handleNetwork(Node node) throws Exception {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("reuse-address".equals(nodeName)) {
                String value = this.getTextContent(child).trim();
                this.config.getNetworkConfig().setReuseAddress(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("port".equals(nodeName)) {
                this.handlePort(child, this.config);
                continue;
            }
            if ("outbound-ports".equals(nodeName)) {
                this.handleOutboundPorts(child);
                continue;
            }
            if ("public-address".equals(nodeName)) {
                String address = this.getTextContent(child);
                this.config.getNetworkConfig().setPublicAddress(address);
                continue;
            }
            if ("join".equals(nodeName)) {
                this.handleJoin(child, false);
                continue;
            }
            if ("interfaces".equals(nodeName)) {
                this.handleInterfaces(child);
                continue;
            }
            if ("symmetric-encryption".equals(nodeName)) {
                this.handleViaReflection(child, this.config.getNetworkConfig(), new SymmetricEncryptionConfig());
                continue;
            }
            if ("ssl".equals(nodeName)) {
                this.handleSSLConfig(child);
                continue;
            }
            if ("socket-interceptor".equals(nodeName)) {
                this.handleSocketInterceptorConfig(child);
                continue;
            }
            if ("member-address-provider".equals(nodeName)) {
                this.handleMemberAddressProvider(child, false);
                continue;
            }
            if ("failure-detector".equals(nodeName)) {
                this.handleFailureDetector(child, false);
                continue;
            }
            if ("rest-api".equals(nodeName)) {
                this.handleRestApi(child);
                continue;
            }
            if (!"memcache-protocol".equals(nodeName)) continue;
            this.handleMemcacheProtocol(child);
        }
    }

    private void handleAdvancedNetwork(Node node) throws Exception {
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            if (!"enabled".equals(att.getNodeName())) continue;
            String value = att.getNodeValue();
            this.config.getAdvancedNetworkConfig().setEnabled(DomConfigHelper.getBooleanValue(value));
        }
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("join".equals(nodeName)) {
                this.handleJoin(child, true);
                continue;
            }
            if ("wan-endpoint-config".equals(nodeName)) {
                this.handleWanEndpointConfig(child);
                continue;
            }
            if ("member-server-socket-endpoint-config".equals(nodeName)) {
                this.handleMemberServerSocketEndpointConfig(child);
                continue;
            }
            if ("client-server-socket-endpoint-config".equals(nodeName)) {
                this.handleClientServerSocketEndpointConfig(child);
                continue;
            }
            if ("wan-server-socket-endpoint-config".equals(nodeName)) {
                this.handleWanServerSocketEndpointConfig(child);
                continue;
            }
            if ("rest-server-socket-endpoint-config".equals(nodeName)) {
                this.handleRestServerSocketEndpointConfig(child);
                continue;
            }
            if ("memcache-server-socket-endpoint-config".equals(nodeName)) {
                this.handleMemcacheServerSocketEndpointConfig(child);
                continue;
            }
            if ("member-address-provider".equals(nodeName)) {
                this.handleMemberAddressProvider(child, true);
                continue;
            }
            if (!"failure-detector".equals(nodeName)) continue;
            this.handleFailureDetector(child, true);
        }
    }

    private void handleEndpointConfig(EndpointConfig endpointConfig, Node node) throws Exception {
        String endpointName = this.getAttribute(node, "name");
        this.handleEndpointConfig(endpointConfig, node, endpointName);
    }

    protected void handleEndpointConfig(EndpointConfig endpointConfig, Node node, String endpointName) throws Exception {
        endpointConfig.setName(endpointName);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            this.handleEndpointConfigCommons(child, nodeName, endpointConfig);
        }
        this.config.getAdvancedNetworkConfig().addWanEndpointConfig(endpointConfig);
    }

    private void handleMemberServerSocketEndpointConfig(Node node) throws Exception {
        ServerSocketEndpointConfig config = new ServerSocketEndpointConfig();
        config.setProtocolType(ProtocolType.MEMBER);
        this.handleServerSocketEndpointConfig(config, node);
    }

    private void handleClientServerSocketEndpointConfig(Node node) throws Exception {
        ServerSocketEndpointConfig config = new ServerSocketEndpointConfig();
        config.setProtocolType(ProtocolType.CLIENT);
        this.handleServerSocketEndpointConfig(config, node);
    }

    protected void handleWanServerSocketEndpointConfig(Node node) throws Exception {
        ServerSocketEndpointConfig config = new ServerSocketEndpointConfig();
        config.setProtocolType(ProtocolType.WAN);
        this.handleServerSocketEndpointConfig(config, node);
    }

    private void handleRestServerSocketEndpointConfig(Node node) throws Exception {
        RestServerEndpointConfig config = new RestServerEndpointConfig();
        this.handleServerSocketEndpointConfig(config, node);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"endpoint-groups".equals(nodeName)) continue;
            for (Node endpointGroup : DomConfigHelper.childElements(child)) {
                this.handleRestEndpointGroup(config, endpointGroup);
            }
        }
    }

    private void handleMemcacheServerSocketEndpointConfig(Node node) throws Exception {
        ServerSocketEndpointConfig config = new ServerSocketEndpointConfig();
        config.setProtocolType(ProtocolType.MEMCACHE);
        this.handleServerSocketEndpointConfig(config, node);
    }

    protected void handleWanEndpointConfig(Node node) throws Exception {
        EndpointConfig config = new EndpointConfig();
        config.setProtocolType(ProtocolType.WAN);
        this.handleEndpointConfig(config, node);
    }

    private void handleServerSocketEndpointConfig(ServerSocketEndpointConfig endpointConfig, Node node) throws Exception {
        String name = this.getAttribute(node, "name");
        this.handleServerSocketEndpointConfig(endpointConfig, node, name);
    }

    protected void handleServerSocketEndpointConfig(ServerSocketEndpointConfig endpointConfig, Node node, String name) throws Exception {
        endpointConfig.setName(name);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("port".equals(nodeName)) {
                this.handlePort(child, endpointConfig);
                continue;
            }
            if ("public-address".equals(nodeName)) {
                String address = this.getTextContent(child);
                endpointConfig.setPublicAddress(address);
                continue;
            }
            if ("reuse-address".equals(nodeName)) {
                String value = this.getTextContent(child).trim();
                endpointConfig.setReuseAddress(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            this.handleEndpointConfigCommons(child, nodeName, endpointConfig);
        }
        this.addEndpointConfig(endpointConfig);
    }

    private void addEndpointConfig(EndpointConfig endpointConfig) {
        switch (endpointConfig.getProtocolType()) {
            case MEMBER: {
                this.ensureServerSocketEndpointConfig(endpointConfig);
                this.config.getAdvancedNetworkConfig().setMemberEndpointConfig((ServerSocketEndpointConfig)endpointConfig);
                break;
            }
            case CLIENT: {
                this.ensureServerSocketEndpointConfig(endpointConfig);
                this.config.getAdvancedNetworkConfig().setClientEndpointConfig((ServerSocketEndpointConfig)endpointConfig);
                break;
            }
            case REST: {
                this.ensureServerSocketEndpointConfig(endpointConfig);
                this.config.getAdvancedNetworkConfig().setRestEndpointConfig((RestServerEndpointConfig)endpointConfig);
                break;
            }
            case WAN: {
                this.config.getAdvancedNetworkConfig().addWanEndpointConfig(endpointConfig);
                break;
            }
            case MEMCACHE: {
                this.config.getAdvancedNetworkConfig().setMemcacheEndpointConfig((ServerSocketEndpointConfig)endpointConfig);
                break;
            }
            default: {
                throw new InvalidConfigurationException("Endpoint config has invalid protocol type " + (Object)((Object)endpointConfig.getProtocolType()));
            }
        }
    }

    private void ensureServerSocketEndpointConfig(EndpointConfig endpointConfig) {
        if (endpointConfig instanceof ServerSocketEndpointConfig) {
            return;
        }
        throw new InvalidConfigurationException("Endpoint configuration of protocol type " + (Object)((Object)endpointConfig.getProtocolType()) + " must be defined in a <server-socket-endpoint-config> element");
    }

    private void handleEndpointConfigCommons(Node node, String nodeName, EndpointConfig endpointConfig) throws Exception {
        if ("outbound-ports".equals(nodeName)) {
            this.handleOutboundPorts(node, endpointConfig);
        } else if ("interfaces".equals(nodeName)) {
            this.handleInterfaces(node, endpointConfig);
        } else if ("ssl".equals(nodeName)) {
            this.handleSSLConfig(node, endpointConfig);
        } else if ("socket-interceptor".equals(nodeName)) {
            this.handleSocketInterceptorConfig(node, endpointConfig);
        } else if ("socket-options".equals(nodeName)) {
            this.handleSocketOptions(node, endpointConfig);
        } else if ("symmetric-encryption".equals(nodeName)) {
            this.handleViaReflection(node, endpointConfig, new SymmetricEncryptionConfig());
        }
    }

    private void handleSocketOptions(Node node, EndpointConfig endpointConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("buffer-direct".equals(nodeName)) {
                endpointConfig.setSocketBufferDirect(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("tcp-no-delay".equals(nodeName)) {
                endpointConfig.setSocketTcpNoDelay(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("keep-alive".equals(nodeName)) {
                endpointConfig.setSocketKeepAlive(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("connect-timeout-seconds".equals(nodeName)) {
                endpointConfig.setSocketConnectTimeoutSeconds(DomConfigHelper.getIntegerValue("connect-timeout-seconds", this.getTextContent(child), 0));
                continue;
            }
            if ("send-buffer-size-kb".equals(nodeName)) {
                endpointConfig.setSocketSendBufferSizeKb(DomConfigHelper.getIntegerValue("send-buffer-size-kb", this.getTextContent(child), 128));
                continue;
            }
            if ("receive-buffer-size-kb".equals(nodeName)) {
                endpointConfig.setSocketRcvBufferSizeKb(DomConfigHelper.getIntegerValue("receive-buffer-size-kb", this.getTextContent(child), 128));
                continue;
            }
            if (!"linger-seconds".equals(nodeName)) continue;
            endpointConfig.setSocketLingerSeconds(DomConfigHelper.getIntegerValue("linger-seconds", this.getTextContent(child), 0));
        }
    }

    protected void handleExecutor(Node node) throws Exception {
        ExecutorConfig executorConfig = new ExecutorConfig();
        this.handleViaReflection(node, this.config, executorConfig);
    }

    protected void handleDurableExecutor(Node node) throws Exception {
        DurableExecutorConfig durableExecutorConfig = new DurableExecutorConfig();
        this.handleViaReflection(node, this.config, durableExecutorConfig);
    }

    protected void handleScheduledExecutor(Node node) {
        ScheduledExecutorConfig scheduledExecutorConfig = new ScheduledExecutorConfig();
        scheduledExecutorConfig.setName(this.getTextContent(node.getAttributes().getNamedItem("name")));
        this.handleScheduledExecutorNode(node, scheduledExecutorConfig);
    }

    void handleScheduledExecutorNode(Node node, ScheduledExecutorConfig scheduledExecutorConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("merge-policy".equals(nodeName)) {
                scheduledExecutorConfig.setMergePolicyConfig(this.createMergePolicyConfig(child));
                continue;
            }
            if ("capacity".equals(nodeName)) {
                scheduledExecutorConfig.setCapacity(Integer.parseInt(this.getTextContent(child)));
                continue;
            }
            if ("durability".equals(nodeName)) {
                scheduledExecutorConfig.setDurability(Integer.parseInt(this.getTextContent(child)));
                continue;
            }
            if ("pool-size".equals(nodeName)) {
                scheduledExecutorConfig.setPoolSize(Integer.parseInt(this.getTextContent(child)));
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            scheduledExecutorConfig.setQuorumName(this.getTextContent(child));
        }
        this.config.addScheduledExecutorConfig(scheduledExecutorConfig);
    }

    protected void handleCardinalityEstimator(Node node) {
        CardinalityEstimatorConfig cardinalityEstimatorConfig = new CardinalityEstimatorConfig();
        cardinalityEstimatorConfig.setName(this.getTextContent(node.getAttributes().getNamedItem("name")));
        this.handleCardinalityEstimatorNode(node, cardinalityEstimatorConfig);
    }

    void handleCardinalityEstimatorNode(Node node, CardinalityEstimatorConfig cardinalityEstimatorConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("merge-policy".equals(nodeName)) {
                MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(child);
                cardinalityEstimatorConfig.setMergePolicyConfig(mergePolicyConfig);
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                cardinalityEstimatorConfig.setBackupCount(Integer.parseInt(this.getTextContent(child)));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                cardinalityEstimatorConfig.setAsyncBackupCount(Integer.parseInt(this.getTextContent(child)));
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            cardinalityEstimatorConfig.setQuorumName(this.getTextContent(child));
        }
        this.config.addCardinalityEstimatorConfig(cardinalityEstimatorConfig);
    }

    protected void handlePNCounter(Node node) throws Exception {
        PNCounterConfig pnCounterConfig = new PNCounterConfig();
        this.handleViaReflection(node, this.config, pnCounterConfig);
    }

    protected void handleFlakeIdGenerator(Node node) {
        String name = this.getAttribute(node, "name");
        FlakeIdGeneratorConfig generatorConfig = new FlakeIdGeneratorConfig(name);
        this.handleFlakeIdGeneratorNode(node, generatorConfig);
    }

    void handleFlakeIdGeneratorNode(Node node, FlakeIdGeneratorConfig generatorConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            String value = this.getTextContent(child).trim();
            if ("prefetch-count".equals(nodeName)) {
                generatorConfig.setPrefetchCount(Integer.parseInt(value));
                continue;
            }
            if ("prefetch-validity-millis".equalsIgnoreCase(nodeName)) {
                generatorConfig.setPrefetchValidityMillis(Long.parseLong(value));
                continue;
            }
            if ("id-offset".equalsIgnoreCase(nodeName)) {
                generatorConfig.setIdOffset(Long.parseLong(value));
                continue;
            }
            if ("node-id-offset".equalsIgnoreCase(nodeName)) {
                generatorConfig.setNodeIdOffset(Long.parseLong(value));
                continue;
            }
            if (!"statistics-enabled".equals(nodeName)) continue;
            generatorConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
        }
        this.config.addFlakeIdGeneratorConfig(generatorConfig);
    }

    private void handleGroup(Node node) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = this.getTextContent(n).trim();
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("name".equals(nodeName)) {
                this.config.getGroupConfig().setName(value);
                continue;
            }
            if (!"password".equals(nodeName)) continue;
            this.config.getGroupConfig().setPassword(value);
        }
    }

    private void handleInterfaces(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        InterfacesConfig interfaces = this.config.getNetworkConfig().getInterfaces();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            if (!"enabled".equals(att.getNodeName())) continue;
            String value = att.getNodeValue();
            interfaces.setEnabled(DomConfigHelper.getBooleanValue(value));
        }
        this.handleInterfacesList(node, interfaces);
    }

    protected void handleInterfacesList(Node node, InterfacesConfig interfaces) {
        for (Node n : DomConfigHelper.childElements(node)) {
            if (!"interface".equals(StringUtil.lowerCaseInternal(DomConfigHelper.cleanNodeName(n)))) continue;
            String value = this.getTextContent(n).trim();
            interfaces.addInterface(value);
        }
    }

    private void handleInterfaces(Node node, EndpointConfig endpointConfig) {
        NamedNodeMap attributes = node.getAttributes();
        InterfacesConfig interfaces = endpointConfig.getInterfaces();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            if (!"enabled".equals(att.getNodeName())) continue;
            String value = att.getNodeValue();
            interfaces.setEnabled(DomConfigHelper.getBooleanValue(value));
        }
        this.handleInterfacesList(node, interfaces);
    }

    protected void handleViaReflection(Node node, Object parent, Object child) throws Exception {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int a = 0; a < attributes.getLength(); ++a) {
                Node att = attributes.item(a);
                MemberDomConfigProcessor.invokeSetter(child, att, att.getNodeValue());
            }
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            if (!(n instanceof Element)) continue;
            MemberDomConfigProcessor.invokeSetter(child, n, this.getTextContent(n).trim());
        }
        MemberDomConfigProcessor.attachChildConfig(parent, child);
    }

    private static void invokeSetter(Object target, Node node, String argument) {
        String coercedArg;
        Method method = MemberDomConfigProcessor.getMethod(target, "set" + MemberDomConfigProcessor.toPropertyName(DomConfigHelper.cleanNodeName(node)), true);
        if (method == null) {
            throw new InvalidConfigurationException("Invalid element/attribute name in the configuration: " + node);
        }
        Class<?> arg = method.getParameterTypes()[0];
        Object object = arg == String.class ? argument : (arg == Integer.TYPE ? Integer.valueOf(argument) : (arg == Long.TYPE ? Long.valueOf(argument) : (coercedArg = arg == Boolean.TYPE ? Boolean.valueOf(DomConfigHelper.getBooleanValue(argument)) : null)));
        if (coercedArg == null) {
            throw new HazelcastException(String.format("Method %s has unsupported argument type %s", method.getName(), arg.getSimpleName()));
        }
        try {
            method.invoke(target, coercedArg);
        }
        catch (Exception e) {
            throw new HazelcastException(e);
        }
    }

    private static void attachChildConfig(Object parent, Object child) throws Exception {
        String targetName = child.getClass().getSimpleName();
        Method attacher = MemberDomConfigProcessor.getMethod(parent, "set" + targetName, false);
        if (attacher == null) {
            attacher = MemberDomConfigProcessor.getMethod(parent, "add" + targetName, false);
        }
        if (attacher == null) {
            throw new HazelcastException(String.format("%s doesn't accept %s as child", parent.getClass().getSimpleName(), targetName));
        }
        attacher.invoke(parent, child);
    }

    private static Method getMethod(Object target, String methodName, boolean requiresArg) {
        Method[] methods;
        for (Method method : methods = target.getClass().getMethods()) {
            Class<?> arg;
            if (!method.getName().equalsIgnoreCase(methodName)) continue;
            if (!requiresArg) {
                return method;
            }
            Class<?>[] args = method.getParameterTypes();
            if (args.length != 1 || (arg = method.getParameterTypes()[0]) != String.class && arg != Integer.TYPE && arg != Long.TYPE && arg != Boolean.TYPE) continue;
            return method;
        }
        return null;
    }

    private static String toPropertyName(String element) {
        String refPropertyName = MemberDomConfigProcessor.handleRefProperty(element);
        if (refPropertyName != null) {
            return refPropertyName;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = element.toCharArray();
        boolean upper = true;
        for (char c : chars) {
            if (c == '_' || c == '-' || c == '.') {
                upper = true;
                continue;
            }
            if (upper) {
                sb.append(Character.toUpperCase(c));
                upper = false;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String handleRefProperty(String element) {
        if (element.equals("quorum-ref")) {
            return "QuorumName";
        }
        return null;
    }

    private void handleJoin(Node node, boolean advancedNetworkConfig) {
        JoinConfig joinConfig = this.joinConfig(advancedNetworkConfig);
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            if ("multicast".equals(name)) {
                this.handleMulticast(child, advancedNetworkConfig);
                continue;
            }
            if ("tcp-ip".equals(name)) {
                this.handleTcpIp(child, advancedNetworkConfig);
                continue;
            }
            if (AliasedDiscoveryConfigUtils.supports(name)) {
                this.handleAliasedDiscoveryStrategy(joinConfig, child, name);
                continue;
            }
            if (!"discovery-strategies".equals(name)) continue;
            this.handleDiscoveryStrategies(joinConfig.getDiscoveryConfig(), child);
        }
        joinConfig.verify();
    }

    protected JoinConfig joinConfig(boolean advancedNetworkConfig) {
        return advancedNetworkConfig ? this.config.getAdvancedNetworkConfig().getJoin() : this.config.getNetworkConfig().getJoin();
    }

    private void handleDiscoveryStrategies(DiscoveryConfig discoveryConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            this.handleDiscoveryStrategiesChild(discoveryConfig, child);
        }
    }

    protected void handleDiscoveryStrategiesChild(DiscoveryConfig discoveryConfig, Node child) {
        String name = DomConfigHelper.cleanNodeName(child);
        if ("discovery-strategy".equals(name)) {
            this.handleDiscoveryStrategy(child, discoveryConfig);
        } else if ("node-filter".equals(name)) {
            this.handleDiscoveryNodeFilter(child, discoveryConfig);
        }
    }

    void handleDiscoveryNodeFilter(Node node, DiscoveryConfig discoveryConfig) {
        NamedNodeMap attributes = node.getAttributes();
        Node att = attributes.getNamedItem("class");
        if (att != null) {
            discoveryConfig.setNodeFilterClass(this.getTextContent(att).trim());
        }
    }

    void handleDiscoveryStrategy(Node node, DiscoveryConfig discoveryConfig) {
        boolean enabled = false;
        String clazz = null;
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("enabled".equals(StringUtil.lowerCaseInternal(att.getNodeName()))) {
                enabled = DomConfigHelper.getBooleanValue(value);
                continue;
            }
            if (!"class".equals(att.getNodeName())) continue;
            clazz = value;
        }
        if (!enabled || clazz == null) {
            return;
        }
        HashMap<String, Comparable> properties = new HashMap<String, Comparable>();
        for (Node child : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(child);
            if (!"properties".equals(name)) continue;
            this.fillProperties(child, properties);
        }
        discoveryConfig.addDiscoveryStrategyConfig(new DiscoveryStrategyConfig(clazz, properties));
    }

    private void handleAliasedDiscoveryStrategy(JoinConfig joinConfig, Node node, String tag) {
        AliasedDiscoveryConfig aliasedDiscoveryConfig = AliasedDiscoveryConfigUtils.getConfigByTag(joinConfig, tag);
        this.updateConfig(aliasedDiscoveryConfig, node);
    }

    private void handleAliasedDiscoveryStrategy(WanPublisherConfig publisherConfig, Node node, String tag) {
        AliasedDiscoveryConfig aliasedDiscoveryConfig = AliasedDiscoveryConfigUtils.getConfigByTag(publisherConfig, tag);
        this.updateConfig(aliasedDiscoveryConfig, node);
    }

    private void updateConfig(AliasedDiscoveryConfig config, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("enabled".equals(StringUtil.lowerCaseInternal(att.getNodeName()))) {
                config.setEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!att.getNodeName().equals("connection-timeout-seconds")) continue;
            config.setProperty("connection-timeout-seconds", value);
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String key = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            config.setProperty(key, value);
        }
    }

    private void handleMulticast(Node node, boolean advancedNetworkConfig) {
        String value;
        JoinConfig join = this.joinConfig(advancedNetworkConfig);
        MulticastConfig multicastConfig = join.getMulticastConfig();
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            value = this.getTextContent(att).trim();
            if ("enabled".equals(StringUtil.lowerCaseInternal(att.getNodeName()))) {
                multicastConfig.setEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!"loopbackmodeenabled".equals(StringUtil.lowerCaseInternal(att.getNodeName())) && !"loopback-mode-enabled".equals(StringUtil.lowerCaseInternal(att.getNodeName()))) continue;
            multicastConfig.setLoopbackModeEnabled(DomConfigHelper.getBooleanValue(value));
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            value = this.getTextContent(n).trim();
            if ("multicast-group".equals(DomConfigHelper.cleanNodeName(n))) {
                multicastConfig.setMulticastGroup(value);
                continue;
            }
            if ("multicast-port".equals(DomConfigHelper.cleanNodeName(n))) {
                multicastConfig.setMulticastPort(Integer.parseInt(value));
                continue;
            }
            if ("multicast-timeout-seconds".equals(DomConfigHelper.cleanNodeName(n))) {
                multicastConfig.setMulticastTimeoutSeconds(Integer.parseInt(value));
                continue;
            }
            if ("multicast-time-to-live-seconds".equals(DomConfigHelper.cleanNodeName(n))) {
                multicastConfig.setMulticastTimeToLive(Integer.parseInt(value));
                continue;
            }
            if ("multicast-time-to-live".equals(DomConfigHelper.cleanNodeName(n))) {
                multicastConfig.setMulticastTimeToLive(Integer.parseInt(value));
                continue;
            }
            if (!"trusted-interfaces".equals(DomConfigHelper.cleanNodeName(n))) continue;
            this.handleTrustedInterfaces(multicastConfig, n);
        }
    }

    protected void handleTrustedInterfaces(MulticastConfig multicastConfig, Node n) {
        for (Node child : DomConfigHelper.childElements(n)) {
            if (!"interface".equals(StringUtil.lowerCaseInternal(DomConfigHelper.cleanNodeName(child)))) continue;
            multicastConfig.addTrustedInterface(this.getTextContent(child).trim());
        }
    }

    private void handleTcpIp(Node node, boolean advancedNetworkConfig) {
        NamedNodeMap attributes = node.getAttributes();
        JoinConfig join = this.joinConfig(advancedNetworkConfig);
        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if (att.getNodeName().equals("enabled")) {
                tcpIpConfig.setEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!att.getNodeName().equals("connection-timeout-seconds")) continue;
            tcpIpConfig.setConnectionTimeoutSeconds(DomConfigHelper.getIntegerValue("connection-timeout-seconds", value));
        }
        HashSet<String> memberTags = new HashSet<String>(Arrays.asList("interface", "member", "members"));
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = this.getTextContent(n).trim();
            if (DomConfigHelper.cleanNodeName(n).equals("member-list")) {
                this.handleMemberList(n, advancedNetworkConfig);
                continue;
            }
            if (DomConfigHelper.cleanNodeName(n).equals("required-member")) {
                if (tcpIpConfig.getRequiredMember() != null) {
                    throw new InvalidConfigurationException("Duplicate required-member definition found in the configuration. ");
                }
                tcpIpConfig.setRequiredMember(value);
                continue;
            }
            if (!memberTags.contains(DomConfigHelper.cleanNodeName(n))) continue;
            tcpIpConfig.addMember(value);
        }
    }

    protected void handleMemberList(Node node, boolean advancedNetworkConfig) {
        JoinConfig join = this.joinConfig(advancedNetworkConfig);
        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (!"member".equals(nodeName)) continue;
            String value = this.getTextContent(n).trim();
            tcpIpConfig.addMember(value);
        }
    }

    protected void handlePort(Node node, Config config) {
        String portStr = this.getTextContent(node).trim();
        NetworkConfig networkConfig = config.getNetworkConfig();
        if (portStr.length() > 0) {
            networkConfig.setPort(Integer.parseInt(portStr));
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("auto-increment".equals(att.getNodeName())) {
                networkConfig.setPortAutoIncrement(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!"port-count".equals(att.getNodeName())) continue;
            int portCount = Integer.parseInt(value);
            networkConfig.setPortCount(portCount);
        }
    }

    protected void handlePort(Node node, ServerSocketEndpointConfig endpointConfig) {
        String portStr = this.getTextContent(node).trim();
        if (portStr.length() > 0) {
            endpointConfig.setPort(Integer.parseInt(portStr));
        }
        this.handlePortAttributes(node, endpointConfig);
    }

    protected void handlePortAttributes(Node node, ServerSocketEndpointConfig endpointConfig) {
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("auto-increment".equals(att.getNodeName())) {
                endpointConfig.setPortAutoIncrement(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!"port-count".equals(att.getNodeName())) continue;
            int portCount = Integer.parseInt(value);
            endpointConfig.setPortCount(portCount);
        }
    }

    protected void handleOutboundPorts(Node child) {
        NetworkConfig networkConfig = this.config.getNetworkConfig();
        for (Node n : DomConfigHelper.childElements(child)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (!"ports".equals(nodeName)) continue;
            String value = this.getTextContent(n);
            networkConfig.addOutboundPortDefinition(value);
        }
    }

    protected void handleOutboundPorts(Node child, EndpointConfig endpointConfig) {
        for (Node n : DomConfigHelper.childElements(child)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (!"ports".equals(nodeName)) continue;
            String value = this.getTextContent(n);
            endpointConfig.addOutboundPortDefinition(value);
        }
    }

    protected void handleLock(Node node) {
        String name = this.getAttribute(node, "name");
        LockConfig lockConfig = new LockConfig();
        lockConfig.setName(name);
        this.handleLockNode(node, lockConfig);
    }

    void handleLockNode(Node node, LockConfig lockConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if (!"quorum-ref".equals(nodeName)) continue;
            lockConfig.setQuorumName(value);
        }
        this.config.addLockConfig(lockConfig);
    }

    protected void handleQueue(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        QueueConfig qConfig = new QueueConfig();
        qConfig.setName(name);
        this.handleQueueNode(node, qConfig);
    }

    void handleQueueNode(Node node, final QueueConfig qConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("max-size".equals(nodeName)) {
                qConfig.setMaxSize(DomConfigHelper.getIntegerValue("max-size", value));
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                qConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                qConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("item-listeners".equals(nodeName)) {
                this.handleItemListeners(n, new Function<ItemListenerConfig, Void>(){

                    @Override
                    public Void apply(ItemListenerConfig itemListenerConfig) {
                        qConfig.addItemListenerConfig(itemListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                qConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("queue-store".equals(nodeName)) {
                QueueStoreConfig queueStoreConfig = this.createQueueStoreConfig(n);
                qConfig.setQueueStoreConfig(queueStoreConfig);
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                qConfig.setQuorumName(value);
                continue;
            }
            if ("empty-queue-ttl".equals(nodeName)) {
                qConfig.setEmptyQueueTtl(DomConfigHelper.getIntegerValue("empty-queue-ttl", value));
                continue;
            }
            if (!"merge-policy".equals(nodeName)) continue;
            MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
            qConfig.setMergePolicyConfig(mergePolicyConfig);
        }
        this.config.addQueueConfig(qConfig);
    }

    protected void handleItemListeners(Node n, Function<ItemListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"item-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            NamedNodeMap attrs = listenerNode.getAttributes();
            boolean incValue = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("include-value")));
            String listenerClass = this.getTextContent(listenerNode);
            configAddFunction.apply(new ItemListenerConfig(listenerClass, incValue));
        }
    }

    protected void handleList(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        ListConfig lConfig = new ListConfig();
        lConfig.setName(name);
        this.handleListNode(node, lConfig);
    }

    void handleListNode(Node node, final ListConfig lConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("max-size".equals(nodeName)) {
                lConfig.setMaxSize(DomConfigHelper.getIntegerValue("max-size", value));
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                lConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                lConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("item-listeners".equals(nodeName)) {
                this.handleItemListeners(n, new Function<ItemListenerConfig, Void>(){

                    @Override
                    public Void apply(ItemListenerConfig itemListenerConfig) {
                        lConfig.addItemListenerConfig(itemListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                lConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                lConfig.setQuorumName(value);
                continue;
            }
            if (!"merge-policy".equals(nodeName)) continue;
            MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
            lConfig.setMergePolicyConfig(mergePolicyConfig);
        }
        this.config.addListConfig(lConfig);
    }

    protected void handleSet(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        SetConfig sConfig = new SetConfig();
        sConfig.setName(name);
        this.handleSetNode(node, sConfig);
    }

    void handleSetNode(Node node, final SetConfig sConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("max-size".equals(nodeName)) {
                sConfig.setMaxSize(DomConfigHelper.getIntegerValue("max-size", value));
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                sConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                sConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("item-listeners".equals(nodeName)) {
                this.handleItemListeners(n, new Function<ItemListenerConfig, Void>(){

                    @Override
                    public Void apply(ItemListenerConfig itemListenerConfig) {
                        sConfig.addItemListenerConfig(itemListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                sConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                sConfig.setQuorumName(value);
                continue;
            }
            if (!"merge-policy".equals(nodeName)) continue;
            MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
            sConfig.setMergePolicyConfig(mergePolicyConfig);
        }
        this.config.addSetConfig(sConfig);
    }

    protected void handleMultiMap(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        MultiMapConfig multiMapConfig = new MultiMapConfig();
        multiMapConfig.setName(name);
        this.handleMultiMapNode(node, multiMapConfig);
    }

    void handleMultiMapNode(Node node, final MultiMapConfig multiMapConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("value-collection-type".equals(nodeName)) {
                multiMapConfig.setValueCollectionType(value);
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                multiMapConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                multiMapConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("entry-listeners".equals(nodeName)) {
                this.handleEntryListeners(n, new Function<EntryListenerConfig, Void>(){

                    @Override
                    public Void apply(EntryListenerConfig entryListenerConfig) {
                        multiMapConfig.addEntryListenerConfig(entryListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                multiMapConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("binary".equals(nodeName)) {
                multiMapConfig.setBinary(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                multiMapConfig.setQuorumName(value);
                continue;
            }
            if (!"merge-policy".equals(nodeName)) continue;
            MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
            multiMapConfig.setMergePolicyConfig(mergePolicyConfig);
        }
        this.config.addMultiMapConfig(multiMapConfig);
    }

    protected void handleEntryListeners(Node n, Function<EntryListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"entry-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            NamedNodeMap attrs = listenerNode.getAttributes();
            boolean incValue = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("include-value")));
            boolean local = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("local")));
            String listenerClass = this.getTextContent(listenerNode);
            configAddFunction.apply(new EntryListenerConfig(listenerClass, local, incValue));
        }
    }

    protected void handleReplicatedMap(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        ReplicatedMapConfig replicatedMapConfig = new ReplicatedMapConfig();
        replicatedMapConfig.setName(name);
        this.handleReplicatedMapNode(node, replicatedMapConfig);
    }

    void handleReplicatedMapNode(Node node, final ReplicatedMapConfig replicatedMapConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("concurrency-level".equals(nodeName)) {
                replicatedMapConfig.setConcurrencyLevel(DomConfigHelper.getIntegerValue("concurrency-level", value));
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                replicatedMapConfig.setInMemoryFormat(InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("replication-delay-millis".equals(nodeName)) {
                replicatedMapConfig.setReplicationDelayMillis(DomConfigHelper.getIntegerValue("replication-delay-millis", value));
                continue;
            }
            if ("async-fillup".equals(nodeName)) {
                replicatedMapConfig.setAsyncFillup(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                replicatedMapConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("entry-listeners".equals(nodeName)) {
                this.handleEntryListeners(n, new Function<EntryListenerConfig, Void>(){

                    @Override
                    public Void apply(EntryListenerConfig entryListenerConfig) {
                        replicatedMapConfig.addEntryListenerConfig(entryListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("merge-policy".equals(nodeName)) {
                MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
                replicatedMapConfig.setMergePolicyConfig(mergePolicyConfig);
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            replicatedMapConfig.setQuorumName(value);
        }
        this.config.addReplicatedMapConfig(replicatedMapConfig);
    }

    protected void handleMap(Node parentNode) {
        String name = this.getAttribute(parentNode, "name");
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(name);
        this.handleMapNode(parentNode, mapConfig);
    }

    void handleMapNode(Node parentNode, final MapConfig mapConfig) {
        for (Node node : DomConfigHelper.childElements(parentNode)) {
            String nodeName = DomConfigHelper.cleanNodeName(node);
            String value = this.getTextContent(node).trim();
            if ("backup-count".equals(nodeName)) {
                mapConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("metadata-policy".equals(nodeName)) {
                mapConfig.setMetadataPolicy(MetadataPolicy.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                mapConfig.setInMemoryFormat(InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                mapConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("eviction-policy".equals(nodeName)) {
                mapConfig.setEvictionPolicy(EvictionPolicy.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("max-size".equals(nodeName)) {
                this.handleMaxSizeConfig(mapConfig, node, value);
                continue;
            }
            if ("eviction-percentage".equals(nodeName)) {
                mapConfig.setEvictionPercentage(DomConfigHelper.getIntegerValue("eviction-percentage", value));
                continue;
            }
            if ("min-eviction-check-millis".equals(nodeName)) {
                mapConfig.setMinEvictionCheckMillis(DomConfigHelper.getLongValue("min-eviction-check-millis", value));
                continue;
            }
            if ("time-to-live-seconds".equals(nodeName)) {
                mapConfig.setTimeToLiveSeconds(DomConfigHelper.getIntegerValue("time-to-live-seconds", value));
                continue;
            }
            if ("max-idle-seconds".equals(nodeName)) {
                mapConfig.setMaxIdleSeconds(DomConfigHelper.getIntegerValue("max-idle-seconds", value));
                continue;
            }
            if ("map-store".equals(nodeName)) {
                MapStoreConfig mapStoreConfig = this.createMapStoreConfig(node);
                mapConfig.setMapStoreConfig(mapStoreConfig);
                continue;
            }
            if ("near-cache".equals(nodeName)) {
                mapConfig.setNearCacheConfig(this.handleNearCacheConfig(node));
                continue;
            }
            if ("merge-policy".equals(nodeName)) {
                MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(node);
                mapConfig.setMergePolicyConfig(mergePolicyConfig);
                continue;
            }
            if ("hot-restart".equals(nodeName)) {
                mapConfig.setHotRestartConfig(this.createHotRestartConfig(node));
                continue;
            }
            if ("read-backup-data".equals(nodeName)) {
                mapConfig.setReadBackupData(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                mapConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("optimize-queries".equals(nodeName)) {
                mapConfig.setOptimizeQueries(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("cache-deserialized-values".equals(nodeName)) {
                CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString(value);
                mapConfig.setCacheDeserializedValues(cacheDeserializedValues);
                continue;
            }
            if ("wan-replication-ref".equals(nodeName)) {
                this.mapWanReplicationRefHandle(node, mapConfig);
                continue;
            }
            if ("indexes".equals(nodeName)) {
                this.mapIndexesHandle(node, mapConfig);
                continue;
            }
            if ("attributes".equals(nodeName)) {
                this.mapAttributesHandle(node, mapConfig);
                continue;
            }
            if ("entry-listeners".equals(nodeName)) {
                this.handleEntryListeners(node, new Function<EntryListenerConfig, Void>(){

                    @Override
                    public Void apply(EntryListenerConfig entryListenerConfig) {
                        mapConfig.addEntryListenerConfig(entryListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("partition-lost-listeners".equals(nodeName)) {
                this.mapPartitionLostListenerHandle(node, mapConfig);
                continue;
            }
            if ("partition-strategy".equals(nodeName)) {
                mapConfig.setPartitioningStrategyConfig(new PartitioningStrategyConfig(value));
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                mapConfig.setQuorumName(value);
                continue;
            }
            if ("query-caches".equals(nodeName)) {
                this.mapQueryCacheHandler(node, mapConfig);
                continue;
            }
            if (!"map-eviction-policy-class-name".equals(nodeName)) continue;
            String className = Preconditions.checkHasText(this.getTextContent(node), "map-eviction-policy-class-name cannot be null or empty");
            try {
                MapEvictionPolicy mapEvictionPolicy = (MapEvictionPolicy)ClassLoaderUtil.newInstance(this.config.getClassLoader(), className);
                mapConfig.setMapEvictionPolicy(mapEvictionPolicy);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        this.config.addMapConfig(mapConfig);
    }

    protected void handleMaxSizeConfig(MapConfig mapConfig, Node node, String value) {
        MaxSizeConfig msc = mapConfig.getMaxSizeConfig();
        Node maxSizePolicy = node.getAttributes().getNamedItem("policy");
        if (maxSizePolicy != null) {
            msc.setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.valueOf(StringUtil.upperCaseInternal(this.getTextContent(maxSizePolicy))));
        }
        msc.setSize(DomConfigHelper.getIntegerValue("max-size", value));
    }

    private NearCacheConfig handleNearCacheConfig(Node node) {
        String name = this.getAttribute(node, "name");
        NearCacheConfig nearCacheConfig = new NearCacheConfig(name);
        Boolean serializeKeys = null;
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            String value = this.getTextContent(child).trim();
            if ("max-size".equals(nodeName)) {
                nearCacheConfig.setMaxSize(Integer.parseInt(value));
                LOGGER.warning("The element <max-size/> for <near-cache/> is deprecated, please use <eviction/> instead!");
                continue;
            }
            if ("time-to-live-seconds".equals(nodeName)) {
                nearCacheConfig.setTimeToLiveSeconds(Integer.parseInt(value));
                continue;
            }
            if ("max-idle-seconds".equals(nodeName)) {
                nearCacheConfig.setMaxIdleSeconds(Integer.parseInt(value));
                continue;
            }
            if ("eviction-policy".equals(nodeName)) {
                nearCacheConfig.setEvictionPolicy(value);
                LOGGER.warning("The element <eviction-policy/> for <near-cache/> is deprecated, please use <eviction/> instead!");
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                nearCacheConfig.setInMemoryFormat(InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("serialize-keys".equals(nodeName)) {
                serializeKeys = Boolean.parseBoolean(value);
                nearCacheConfig.setSerializeKeys(serializeKeys);
                continue;
            }
            if ("invalidate-on-change".equals(nodeName)) {
                nearCacheConfig.setInvalidateOnChange(Boolean.parseBoolean(value));
                continue;
            }
            if ("cache-local-entries".equals(nodeName)) {
                nearCacheConfig.setCacheLocalEntries(Boolean.parseBoolean(value));
                continue;
            }
            if ("local-update-policy".equals(nodeName)) {
                NearCacheConfig.LocalUpdatePolicy policy = NearCacheConfig.LocalUpdatePolicy.valueOf(value);
                nearCacheConfig.setLocalUpdatePolicy(policy);
                continue;
            }
            if (!"eviction".equals(nodeName)) continue;
            nearCacheConfig.setEvictionConfig(this.getEvictionConfig(child, true));
        }
        if (serializeKeys != null && !serializeKeys.booleanValue() && nearCacheConfig.getInMemoryFormat() == InMemoryFormat.NATIVE) {
            LOGGER.warning("The Near Cache doesn't support keys by-reference with NATIVE in-memory-format. This setting will have no effect!");
        }
        return nearCacheConfig;
    }

    private HotRestartConfig createHotRestartConfig(Node node) {
        HotRestartConfig hotRestartConfig = new HotRestartConfig();
        Node attrEnabled = node.getAttributes().getNamedItem("enabled");
        boolean enabled = DomConfigHelper.getBooleanValue(this.getTextContent(attrEnabled));
        hotRestartConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(n);
            if (!"fsync".equals(name)) continue;
            hotRestartConfig.setFsync(DomConfigHelper.getBooleanValue(this.getTextContent(n)));
        }
        return hotRestartConfig;
    }

    protected void handleCache(Node node) {
        String name = this.getAttribute(node, "name");
        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        cacheConfig.setName(name);
        this.handleCacheNode(node, cacheConfig);
    }

    void handleCacheNode(Node node, CacheSimpleConfig cacheConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("key-type".equals(nodeName)) {
                cacheConfig.setKeyType(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("value-type".equals(nodeName)) {
                cacheConfig.setValueType(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                cacheConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("management-enabled".equals(nodeName)) {
                cacheConfig.setManagementEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("read-through".equals(nodeName)) {
                cacheConfig.setReadThrough(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("write-through".equals(nodeName)) {
                cacheConfig.setWriteThrough(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("cache-loader-factory".equals(nodeName)) {
                cacheConfig.setCacheLoaderFactory(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("cache-loader".equals(nodeName)) {
                cacheConfig.setCacheLoader(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("cache-writer-factory".equals(nodeName)) {
                cacheConfig.setCacheWriterFactory(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("cache-writer".equals(nodeName)) {
                cacheConfig.setCacheWriter(this.getAttribute(n, "class-name"));
                continue;
            }
            if ("expiry-policy-factory".equals(nodeName)) {
                cacheConfig.setExpiryPolicyFactoryConfig(this.getExpiryPolicyFactoryConfig(n));
                continue;
            }
            if ("cache-entry-listeners".equals(nodeName)) {
                this.cacheListenerHandle(n, cacheConfig);
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                cacheConfig.setInMemoryFormat(InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                cacheConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                cacheConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if ("wan-replication-ref".equals(nodeName)) {
                this.cacheWanReplicationRefHandle(n, cacheConfig);
                continue;
            }
            if ("eviction".equals(nodeName)) {
                cacheConfig.setEvictionConfig(this.getEvictionConfig(n, false));
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                cacheConfig.setQuorumName(value);
                continue;
            }
            if ("partition-lost-listeners".equals(nodeName)) {
                this.cachePartitionLostListenerHandle(n, cacheConfig);
                continue;
            }
            if ("merge-policy".equals(nodeName)) {
                cacheConfig.setMergePolicy(value);
                continue;
            }
            if ("hot-restart".equals(nodeName)) {
                cacheConfig.setHotRestartConfig(this.createHotRestartConfig(n));
                continue;
            }
            if (!"disable-per-entry-invalidation-events".equals(nodeName)) continue;
            cacheConfig.setDisablePerEntryInvalidationEvents(DomConfigHelper.getBooleanValue(value));
        }
        try {
            ConfigValidator.checkCacheConfig(cacheConfig, null);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
        this.config.addCacheConfig(cacheConfig);
    }

    private CacheSimpleConfig.ExpiryPolicyFactoryConfig getExpiryPolicyFactoryConfig(Node node) {
        String className = this.getAttribute(node, "class-name");
        if (!StringUtil.isNullOrEmpty(className)) {
            return new CacheSimpleConfig.ExpiryPolicyFactoryConfig(className);
        }
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = null;
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (!"timed-expiry-policy-factory".equals(nodeName)) continue;
            timedExpiryPolicyFactoryConfig = this.getTimedExpiryPolicyFactoryConfig(n);
        }
        if (timedExpiryPolicyFactoryConfig == null) {
            throw new InvalidConfigurationException("One of the \"class-name\" or \"timed-expire-policy-factory\" configuration is needed for expiry policy factory configuration");
        }
        return new CacheSimpleConfig.ExpiryPolicyFactoryConfig(timedExpiryPolicyFactoryConfig);
    }

    private CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig getTimedExpiryPolicyFactoryConfig(Node node) {
        String expiryPolicyTypeStr = this.getAttribute(node, "expiry-policy-type");
        String durationAmountStr = this.getAttribute(node, "duration-amount");
        String timeUnitStr = this.getAttribute(node, "time-unit");
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType expiryPolicyType = CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType.valueOf(StringUtil.upperCaseInternal(expiryPolicyTypeStr));
        if (expiryPolicyType != CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType.ETERNAL && (StringUtil.isNullOrEmpty(durationAmountStr) || StringUtil.isNullOrEmpty(timeUnitStr))) {
            throw new InvalidConfigurationException("Both of the \"duration-amount\" or \"time-unit\" attributes are required for expiry policy factory configuration (except \"ETERNAL\" expiry policy type)");
        }
        CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig durationConfig = null;
        if (expiryPolicyType != CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType.ETERNAL) {
            TimeUnit timeUnit;
            long durationAmount;
            try {
                durationAmount = Long.parseLong(durationAmountStr);
            }
            catch (NumberFormatException e) {
                throw new InvalidConfigurationException("Invalid value for duration amount: " + durationAmountStr, e);
            }
            if (durationAmount <= 0L) {
                throw new InvalidConfigurationException("Duration amount must be positive: " + durationAmount);
            }
            try {
                timeUnit = TimeUnit.valueOf(StringUtil.upperCaseInternal(timeUnitStr));
            }
            catch (IllegalArgumentException e) {
                throw new InvalidConfigurationException("Invalid value for time unit: " + timeUnitStr, e);
            }
            durationConfig = new CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig(durationAmount, timeUnit);
        }
        return new CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig(expiryPolicyType, durationConfig);
    }

    private EvictionConfig getEvictionConfig(Node node, boolean isNearCache) {
        EvictionConfig evictionConfig = new EvictionConfig();
        Node size = node.getAttributes().getNamedItem("size");
        Node maxSizePolicy = node.getAttributes().getNamedItem("max-size-policy");
        Node evictionPolicy = node.getAttributes().getNamedItem("eviction-policy");
        Node comparatorClassName = node.getAttributes().getNamedItem("comparator-class-name");
        if (size != null) {
            evictionConfig.setSize(Integer.parseInt(this.getTextContent(size)));
        }
        if (maxSizePolicy != null) {
            evictionConfig.setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.valueOf(StringUtil.upperCaseInternal(this.getTextContent(maxSizePolicy))));
        }
        if (evictionPolicy != null) {
            evictionConfig.setEvictionPolicy(EvictionPolicy.valueOf(StringUtil.upperCaseInternal(this.getTextContent(evictionPolicy))));
        }
        if (comparatorClassName != null) {
            evictionConfig.setComparatorClassName(this.getTextContent(comparatorClassName));
        }
        try {
            ConfigValidator.checkEvictionConfig(evictionConfig, isNearCache);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
        return evictionConfig;
    }

    private void cacheWanReplicationRefHandle(Node n, CacheSimpleConfig cacheConfig) {
        WanReplicationRef wanReplicationRef = new WanReplicationRef();
        String wanName = this.getAttribute(n, "name");
        wanReplicationRef.setName(wanName);
        for (Node wanChild : DomConfigHelper.childElements(n)) {
            String wanChildName = DomConfigHelper.cleanNodeName(wanChild);
            String wanChildValue = this.getTextContent(wanChild);
            if ("merge-policy".equals(wanChildName)) {
                wanReplicationRef.setMergePolicy(wanChildValue);
                continue;
            }
            if ("filters".equals(wanChildName)) {
                this.handleWanFilters(wanChild, wanReplicationRef);
                continue;
            }
            if (!"republishing-enabled".equals(wanChildName)) continue;
            wanReplicationRef.setRepublishingEnabled(DomConfigHelper.getBooleanValue(wanChildValue));
        }
        cacheConfig.setWanReplicationRef(wanReplicationRef);
    }

    protected void handleWanFilters(Node wanChild, WanReplicationRef wanReplicationRef) {
        for (Node filter : DomConfigHelper.childElements(wanChild)) {
            if (!"filter-impl".equals(DomConfigHelper.cleanNodeName(filter))) continue;
            wanReplicationRef.addFilter(this.getTextContent(filter));
        }
    }

    protected void cachePartitionLostListenerHandle(Node n, CacheSimpleConfig cacheConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"partition-lost-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            String listenerClass = this.getTextContent(listenerNode);
            cacheConfig.addCachePartitionLostListenerConfig(new CachePartitionLostListenerConfig(listenerClass));
        }
    }

    protected void cacheListenerHandle(Node n, CacheSimpleConfig cacheSimpleConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"cache-entry-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            this.handleCacheEntryListenerNode(cacheSimpleConfig, listenerNode);
        }
    }

    protected void handleCacheEntryListenerNode(CacheSimpleConfig cacheSimpleConfig, Node listenerNode) {
        CacheSimpleEntryListenerConfig listenerConfig = new CacheSimpleEntryListenerConfig();
        for (Node listenerChildNode : DomConfigHelper.childElements(listenerNode)) {
            if ("cache-entry-listener-factory".equals(DomConfigHelper.cleanNodeName(listenerChildNode))) {
                listenerConfig.setCacheEntryListenerFactory(this.getAttribute(listenerChildNode, "class-name"));
            }
            if (!"cache-entry-event-filter-factory".equals(DomConfigHelper.cleanNodeName(listenerChildNode))) continue;
            listenerConfig.setCacheEntryEventFilterFactory(this.getAttribute(listenerChildNode, "class-name"));
        }
        NamedNodeMap attrs = listenerNode.getAttributes();
        listenerConfig.setOldValueRequired(DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("old-value-required"))));
        listenerConfig.setSynchronous(DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("synchronous"))));
        cacheSimpleConfig.addEntryListenerConfig(listenerConfig);
    }

    protected void mapWanReplicationRefHandle(Node n, MapConfig mapConfig) {
        WanReplicationRef wanReplicationRef = new WanReplicationRef();
        String wanName = this.getAttribute(n, "name");
        wanReplicationRef.setName(wanName);
        this.handleMapWanReplicationRefNode(n, mapConfig, wanReplicationRef);
    }

    void handleMapWanReplicationRefNode(Node n, MapConfig mapConfig, WanReplicationRef wanReplicationRef) {
        for (Node wanChild : DomConfigHelper.childElements(n)) {
            String wanChildName = DomConfigHelper.cleanNodeName(wanChild);
            String wanChildValue = this.getTextContent(wanChild);
            if ("merge-policy".equals(wanChildName)) {
                wanReplicationRef.setMergePolicy(wanChildValue);
                continue;
            }
            if ("republishing-enabled".equals(wanChildName)) {
                wanReplicationRef.setRepublishingEnabled(DomConfigHelper.getBooleanValue(wanChildValue));
                continue;
            }
            if (!"filters".equals(wanChildName)) continue;
            this.handleWanFilters(wanChild, wanReplicationRef);
        }
        mapConfig.setWanReplicationRef(wanReplicationRef);
    }

    protected void mapIndexesHandle(Node n, MapConfig mapConfig) {
        for (Node indexNode : DomConfigHelper.childElements(n)) {
            if (!"index".equals(DomConfigHelper.cleanNodeName(indexNode))) continue;
            NamedNodeMap attrs = indexNode.getAttributes();
            boolean ordered = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("ordered")));
            String attribute = this.getTextContent(indexNode);
            mapConfig.addMapIndexConfig(new MapIndexConfig(attribute, ordered));
        }
    }

    protected void queryCacheIndexesHandle(Node n, QueryCacheConfig queryCacheConfig) {
        for (Node indexNode : DomConfigHelper.childElements(n)) {
            if (!"index".equals(DomConfigHelper.cleanNodeName(indexNode))) continue;
            NamedNodeMap attrs = indexNode.getAttributes();
            boolean ordered = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("ordered")));
            String attribute = this.getTextContent(indexNode);
            queryCacheConfig.addIndexConfig(new MapIndexConfig(attribute, ordered));
        }
    }

    protected void mapAttributesHandle(Node n, MapConfig mapConfig) {
        for (Node extractorNode : DomConfigHelper.childElements(n)) {
            if (!"attribute".equals(DomConfigHelper.cleanNodeName(extractorNode))) continue;
            NamedNodeMap attrs = extractorNode.getAttributes();
            String extractor = this.getTextContent(attrs.getNamedItem("extractor"));
            String name = this.getTextContent(extractorNode);
            mapConfig.addMapAttributeConfig(new MapAttributeConfig(name, extractor));
        }
    }

    protected void mapPartitionLostListenerHandle(Node n, MapConfig mapConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"partition-lost-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            String listenerClass = this.getTextContent(listenerNode);
            mapConfig.addMapPartitionLostListenerConfig(new MapPartitionLostListenerConfig(listenerClass));
        }
    }

    protected void mapQueryCacheHandler(Node n, MapConfig mapConfig) {
        for (Node queryCacheNode : DomConfigHelper.childElements(n)) {
            if (!"query-cache".equals(DomConfigHelper.cleanNodeName(queryCacheNode))) continue;
            NamedNodeMap attrs = queryCacheNode.getAttributes();
            String cacheName = this.getTextContent(attrs.getNamedItem("name"));
            QueryCacheConfig queryCacheConfig = new QueryCacheConfig(cacheName);
            this.handleMapQueryCacheNode(mapConfig, queryCacheNode, queryCacheConfig);
        }
    }

    void handleMapQueryCacheNode(MapConfig mapConfig, Node queryCacheNode, final QueryCacheConfig queryCacheConfig) {
        for (Node childNode : DomConfigHelper.childElements(queryCacheNode)) {
            String nodeName = DomConfigHelper.cleanNodeName(childNode);
            if ("entry-listeners".equals(nodeName)) {
                this.handleEntryListeners(childNode, new Function<EntryListenerConfig, Void>(){

                    @Override
                    public Void apply(EntryListenerConfig entryListenerConfig) {
                        queryCacheConfig.addEntryListenerConfig(entryListenerConfig);
                        return null;
                    }
                });
                continue;
            }
            String textContent = this.getTextContent(childNode);
            if ("include-value".equals(nodeName)) {
                boolean includeValue = DomConfigHelper.getBooleanValue(textContent);
                queryCacheConfig.setIncludeValue(includeValue);
                continue;
            }
            if ("batch-size".equals(nodeName)) {
                int batchSize = DomConfigHelper.getIntegerValue("batch-size", textContent.trim());
                queryCacheConfig.setBatchSize(batchSize);
                continue;
            }
            if ("buffer-size".equals(nodeName)) {
                int bufferSize = DomConfigHelper.getIntegerValue("buffer-size", textContent.trim());
                queryCacheConfig.setBufferSize(bufferSize);
                continue;
            }
            if ("delay-seconds".equals(nodeName)) {
                int delaySeconds = DomConfigHelper.getIntegerValue("delay-seconds", textContent.trim());
                queryCacheConfig.setDelaySeconds(delaySeconds);
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                String value = textContent.trim();
                queryCacheConfig.setInMemoryFormat(InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value)));
                continue;
            }
            if ("coalesce".equals(nodeName)) {
                boolean coalesce = DomConfigHelper.getBooleanValue(textContent);
                queryCacheConfig.setCoalesce(coalesce);
                continue;
            }
            if ("populate".equals(nodeName)) {
                boolean populate = DomConfigHelper.getBooleanValue(textContent);
                queryCacheConfig.setPopulate(populate);
                continue;
            }
            if ("indexes".equals(nodeName)) {
                this.queryCacheIndexesHandle(childNode, queryCacheConfig);
                continue;
            }
            if ("predicate".equals(nodeName)) {
                this.queryCachePredicateHandler(childNode, queryCacheConfig);
                continue;
            }
            if (!"eviction".equals(nodeName)) continue;
            queryCacheConfig.setEvictionConfig(this.getEvictionConfig(childNode, false));
        }
        mapConfig.addQueryCacheConfig(queryCacheConfig);
    }

    protected void queryCachePredicateHandler(Node childNode, QueryCacheConfig queryCacheConfig) {
        NamedNodeMap predicateAttributes = childNode.getAttributes();
        String predicateType = this.getTextContent(predicateAttributes.getNamedItem("type"));
        String textContent = this.getTextContent(childNode);
        PredicateConfig predicateConfig = new PredicateConfig();
        if ("class-name".equals(predicateType)) {
            predicateConfig.setClassName(textContent);
        } else if ("sql".equals(predicateType)) {
            predicateConfig.setSql(textContent);
        }
        queryCacheConfig.setPredicateConfig(predicateConfig);
    }

    private MapStoreConfig createMapStoreConfig(Node node) {
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("enabled".equals(att.getNodeName())) {
                mapStoreConfig.setEnabled(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!"initial-mode".equals(att.getNodeName())) continue;
            MapStoreConfig.InitialLoadMode mode = MapStoreConfig.InitialLoadMode.valueOf(StringUtil.upperCaseInternal(this.getTextContent(att)));
            mapStoreConfig.setInitialLoadMode(mode);
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("class-name".equals(nodeName)) {
                mapStoreConfig.setClassName(this.getTextContent(n).trim());
                continue;
            }
            if ("factory-class-name".equals(nodeName)) {
                mapStoreConfig.setFactoryClassName(this.getTextContent(n).trim());
                continue;
            }
            if ("write-delay-seconds".equals(nodeName)) {
                mapStoreConfig.setWriteDelaySeconds(DomConfigHelper.getIntegerValue("write-delay-seconds", this.getTextContent(n).trim()));
                continue;
            }
            if ("write-batch-size".equals(nodeName)) {
                mapStoreConfig.setWriteBatchSize(DomConfigHelper.getIntegerValue("write-batch-size", this.getTextContent(n).trim()));
                continue;
            }
            if ("write-coalescing".equals(nodeName)) {
                String writeCoalescing = this.getTextContent(n).trim();
                if (StringUtil.isNullOrEmpty(writeCoalescing)) {
                    mapStoreConfig.setWriteCoalescing(true);
                    continue;
                }
                mapStoreConfig.setWriteCoalescing(DomConfigHelper.getBooleanValue(writeCoalescing));
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, mapStoreConfig.getProperties());
        }
        return mapStoreConfig;
    }

    private RingbufferStoreConfig createRingbufferStoreConfig(Node node) {
        RingbufferStoreConfig config = new RingbufferStoreConfig();
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if (!att.getNodeName().equals("enabled")) continue;
            config.setEnabled(DomConfigHelper.getBooleanValue(value));
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("class-name".equals(nodeName)) {
                config.setClassName(this.getTextContent(n).trim());
                continue;
            }
            if ("factory-class-name".equals(nodeName)) {
                config.setFactoryClassName(this.getTextContent(n).trim());
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, config.getProperties());
        }
        return config;
    }

    protected MergePolicyConfig createMergePolicyConfig(Node node) {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
        String policyString = this.getTextContent(node).trim();
        mergePolicyConfig.setPolicy(policyString);
        String att = this.getAttribute(node, "batch-size");
        if (att != null) {
            mergePolicyConfig.setBatchSize(DomConfigHelper.getIntegerValue("batch-size", att));
        }
        return mergePolicyConfig;
    }

    private QueueStoreConfig createQueueStoreConfig(Node node) {
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig();
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if (!att.getNodeName().equals("enabled")) continue;
            queueStoreConfig.setEnabled(DomConfigHelper.getBooleanValue(value));
        }
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("class-name".equals(nodeName)) {
                queueStoreConfig.setClassName(this.getTextContent(n).trim());
                continue;
            }
            if ("factory-class-name".equals(nodeName)) {
                queueStoreConfig.setFactoryClassName(this.getTextContent(n).trim());
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, queueStoreConfig.getProperties());
        }
        return queueStoreConfig;
    }

    private void handleSSLConfig(Node node) {
        SSLConfig sslConfig = this.parseSslConfig(node);
        this.config.getNetworkConfig().setSSLConfig(sslConfig);
    }

    private void handleSSLConfig(Node node, EndpointConfig endpointConfig) {
        SSLConfig sslConfig = this.parseSslConfig(node);
        endpointConfig.setSSLConfig(sslConfig);
    }

    private void handleMcMutualAuthConfig(Node node) {
        MCMutualAuthConfig mcMutualAuthConfig = new MCMutualAuthConfig();
        NamedNodeMap attributes = node.getAttributes();
        Node enabledNode = attributes.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode).trim());
        mcMutualAuthConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("factory-class-name".equals(nodeName)) {
                mcMutualAuthConfig.setFactoryClassName(this.getTextContent(n).trim());
                continue;
            }
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(n, mcMutualAuthConfig.getProperties());
        }
        this.config.getManagementCenterConfig().setMutualAuthConfig(mcMutualAuthConfig);
    }

    private void handleMemberAddressProvider(Node node, boolean advancedNetworkConfig) {
        MemberAddressProviderConfig memberAddressProviderConfig = this.memberAddressProviderConfig(advancedNetworkConfig);
        Node enabledNode = node.getAttributes().getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
        memberAddressProviderConfig.setEnabled(enabled);
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (nodeName.equals("class-name")) {
                String className = this.getTextContent(n);
                memberAddressProviderConfig.setClassName(className);
                continue;
            }
            if (!nodeName.equals("properties")) continue;
            this.fillProperties(n, memberAddressProviderConfig.getProperties());
        }
    }

    private MemberAddressProviderConfig memberAddressProviderConfig(boolean advancedNetworkConfig) {
        return advancedNetworkConfig ? this.config.getAdvancedNetworkConfig().getMemberAddressProviderConfig() : this.config.getNetworkConfig().getMemberAddressProviderConfig();
    }

    private void handleFailureDetector(Node node, boolean advancedNetworkConfig) {
        if (!node.hasChildNodes()) {
            return;
        }
        for (Node child : DomConfigHelper.childElements(node)) {
            if (!DomConfigHelper.cleanNodeName(child).equals("icmp")) {
                throw new IllegalStateException("Unsupported child under failure-detector");
            }
            Node enabledNode = child.getAttributes().getNamedItem("enabled");
            boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
            IcmpFailureDetectorConfig icmpFailureDetectorConfig = new IcmpFailureDetectorConfig();
            icmpFailureDetectorConfig.setEnabled(enabled);
            for (Node n : DomConfigHelper.childElements(child)) {
                String nodeName = DomConfigHelper.cleanNodeName(n);
                if (nodeName.equals("ttl")) {
                    int ttl = Integer.parseInt(this.getTextContent(n));
                    icmpFailureDetectorConfig.setTtl(ttl);
                    continue;
                }
                if (nodeName.equals("timeout-milliseconds")) {
                    int timeout = Integer.parseInt(this.getTextContent(n));
                    icmpFailureDetectorConfig.setTimeoutMilliseconds(timeout);
                    continue;
                }
                if (nodeName.equals("parallel-mode")) {
                    boolean mode = Boolean.parseBoolean(this.getTextContent(n));
                    icmpFailureDetectorConfig.setParallelMode(mode);
                    continue;
                }
                if (nodeName.equals("fail-fast-on-startup")) {
                    boolean failOnStartup = Boolean.parseBoolean(this.getTextContent(n));
                    icmpFailureDetectorConfig.setFailFastOnStartup(failOnStartup);
                    continue;
                }
                if (nodeName.equals("max-attempts")) {
                    int attempts = Integer.parseInt(this.getTextContent(n));
                    icmpFailureDetectorConfig.setMaxAttempts(attempts);
                    continue;
                }
                if (!nodeName.equals("interval-milliseconds")) continue;
                int interval = Integer.parseInt(this.getTextContent(n));
                icmpFailureDetectorConfig.setIntervalMilliseconds(interval);
            }
            if (advancedNetworkConfig) {
                this.config.getAdvancedNetworkConfig().setIcmpFailureDetectorConfig(icmpFailureDetectorConfig);
                continue;
            }
            this.config.getNetworkConfig().setIcmpFailureDetectorConfig(icmpFailureDetectorConfig);
        }
    }

    private void handleSocketInterceptorConfig(Node node) {
        SocketInterceptorConfig socketInterceptorConfig = this.parseSocketInterceptorConfig(node);
        this.config.getNetworkConfig().setSocketInterceptorConfig(socketInterceptorConfig);
    }

    private void handleSocketInterceptorConfig(Node node, EndpointConfig endpointConfig) {
        SocketInterceptorConfig socketInterceptorConfig = this.parseSocketInterceptorConfig(node);
        endpointConfig.setSocketInterceptorConfig(socketInterceptorConfig);
    }

    protected void handleTopic(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        TopicConfig tConfig = new TopicConfig();
        tConfig.setName(name);
        this.handleTopicNode(node, tConfig);
    }

    void handleTopicNode(Node node, final TopicConfig tConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if (nodeName.equals("global-ordering-enabled")) {
                tConfig.setGlobalOrderingEnabled(DomConfigHelper.getBooleanValue(this.getTextContent(n)));
                continue;
            }
            if ("message-listeners".equals(nodeName)) {
                this.handleMessageListeners(n, new Function<ListenerConfig, Void>(){

                    @Override
                    public Void apply(ListenerConfig listenerConfig) {
                        tConfig.addMessageListenerConfig(listenerConfig);
                        return null;
                    }
                });
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                tConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(this.getTextContent(n)));
                continue;
            }
            if (!"multi-threading-enabled".equals(nodeName)) continue;
            tConfig.setMultiThreadingEnabled(DomConfigHelper.getBooleanValue(this.getTextContent(n)));
        }
        this.config.addTopicConfig(tConfig);
    }

    protected void handleReliableTopic(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        ReliableTopicConfig topicConfig = new ReliableTopicConfig(name);
        this.handleReliableTopicNode(node, topicConfig);
    }

    void handleReliableTopicNode(Node node, final ReliableTopicConfig topicConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            if ("read-batch-size".equals(nodeName)) {
                String batchSize = this.getTextContent(n);
                topicConfig.setReadBatchSize(DomConfigHelper.getIntegerValue("read-batch-size", batchSize));
                continue;
            }
            if ("statistics-enabled".equals(nodeName)) {
                topicConfig.setStatisticsEnabled(DomConfigHelper.getBooleanValue(this.getTextContent(n)));
                continue;
            }
            if ("topic-overload-policy".equals(nodeName)) {
                TopicOverloadPolicy topicOverloadPolicy = TopicOverloadPolicy.valueOf(StringUtil.upperCaseInternal(this.getTextContent(n)));
                topicConfig.setTopicOverloadPolicy(topicOverloadPolicy);
                continue;
            }
            if (!"message-listeners".equals(nodeName)) continue;
            this.handleMessageListeners(n, new Function<ListenerConfig, Void>(){

                @Override
                public Void apply(ListenerConfig listenerConfig) {
                    topicConfig.addMessageListenerConfig(listenerConfig);
                    return null;
                }
            });
        }
        this.config.addReliableTopicConfig(topicConfig);
    }

    void handleMessageListeners(Node n, Function<ListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            if (!"message-listener".equals(DomConfigHelper.cleanNodeName(listenerNode))) continue;
            configAddFunction.apply(new ListenerConfig(this.getTextContent(listenerNode)));
        }
    }

    private void handleJobTracker(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        JobTrackerConfig jConfig = new JobTrackerConfig();
        jConfig.setName(name);
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("max-thread-size".equals(nodeName)) {
                jConfig.setMaxThreadSize(DomConfigHelper.getIntegerValue("max-thread-size", value));
                continue;
            }
            if ("queue-size".equals(nodeName)) {
                jConfig.setQueueSize(DomConfigHelper.getIntegerValue("queue-size", value));
                continue;
            }
            if ("retry-count".equals(nodeName)) {
                jConfig.setRetryCount(DomConfigHelper.getIntegerValue("retry-count", value));
                continue;
            }
            if ("chunk-size".equals(nodeName)) {
                jConfig.setChunkSize(DomConfigHelper.getIntegerValue("chunk-size", value));
                continue;
            }
            if ("communicate-stats".equals(nodeName)) {
                jConfig.setCommunicateStats(value.length() == 0 ? true : Boolean.parseBoolean(value));
                continue;
            }
            if (!"topology-changed-strategy".equals(nodeName)) continue;
            TopologyChangedStrategy topologyChangedStrategy = JobTrackerConfig.DEFAULT_TOPOLOGY_CHANGED_STRATEGY;
            for (TopologyChangedStrategy temp : TopologyChangedStrategy.values()) {
                if (!temp.name().equals(value)) continue;
                topologyChangedStrategy = temp;
            }
            jConfig.setTopologyChangedStrategy(topologyChangedStrategy);
        }
        this.config.addJobTrackerConfig(jConfig);
    }

    protected void handleSemaphore(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        SemaphoreConfig sConfig = new SemaphoreConfig();
        sConfig.setName(name);
        this.handleSemaphoreNode(node, sConfig);
    }

    void handleSemaphoreNode(Node node, SemaphoreConfig sConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("initial-permits".equals(nodeName)) {
                sConfig.setInitialPermits(DomConfigHelper.getIntegerValue("initial-permits", value));
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                sConfig.setBackupCount(DomConfigHelper.getIntegerValue("backup-count", value));
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                sConfig.setAsyncBackupCount(DomConfigHelper.getIntegerValue("async-backup-count", value));
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            sConfig.setQuorumName(value);
        }
        this.config.addSemaphoreConfig(sConfig);
    }

    protected void handleEventJournal(Node node) throws Exception {
        EventJournalConfig journalConfig = new EventJournalConfig();
        this.handleViaReflection(node, this.config, journalConfig);
        this.config.addEventJournalConfig(journalConfig);
    }

    protected void handleMerkleTree(Node node) throws Exception {
        MerkleTreeConfig merkleTreeConfig = new MerkleTreeConfig();
        this.handleViaReflection(node, this.config, merkleTreeConfig);
        this.config.addMerkleTreeConfig(merkleTreeConfig);
    }

    protected void handleRingbuffer(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        RingbufferConfig rbConfig = new RingbufferConfig(name);
        this.handleRingBufferNode(node, rbConfig);
    }

    void handleRingBufferNode(Node node, RingbufferConfig rbConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("capacity".equals(nodeName)) {
                int capacity = DomConfigHelper.getIntegerValue("capacity", value);
                rbConfig.setCapacity(capacity);
                continue;
            }
            if ("backup-count".equals(nodeName)) {
                int backupCount = DomConfigHelper.getIntegerValue("backup-count", value);
                rbConfig.setBackupCount(backupCount);
                continue;
            }
            if ("async-backup-count".equals(nodeName)) {
                int asyncBackupCount = DomConfigHelper.getIntegerValue("async-backup-count", value);
                rbConfig.setAsyncBackupCount(asyncBackupCount);
                continue;
            }
            if ("time-to-live-seconds".equals(nodeName)) {
                int timeToLiveSeconds = DomConfigHelper.getIntegerValue("time-to-live-seconds", value);
                rbConfig.setTimeToLiveSeconds(timeToLiveSeconds);
                continue;
            }
            if ("in-memory-format".equals(nodeName)) {
                InMemoryFormat inMemoryFormat = InMemoryFormat.valueOf(StringUtil.upperCaseInternal(value));
                rbConfig.setInMemoryFormat(inMemoryFormat);
                continue;
            }
            if ("ringbuffer-store".equals(nodeName)) {
                RingbufferStoreConfig ringbufferStoreConfig = this.createRingbufferStoreConfig(n);
                rbConfig.setRingbufferStoreConfig(ringbufferStoreConfig);
                continue;
            }
            if ("quorum-ref".equals(nodeName)) {
                rbConfig.setQuorumName(value);
                continue;
            }
            if (!"merge-policy".equals(nodeName)) continue;
            MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
            rbConfig.setMergePolicyConfig(mergePolicyConfig);
        }
        this.config.addRingBufferConfig(rbConfig);
    }

    protected void handleAtomicLong(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        AtomicLongConfig atomicLongConfig = new AtomicLongConfig(name);
        this.handleAtomicLongNode(node, atomicLongConfig);
    }

    void handleAtomicLongNode(Node node, AtomicLongConfig atomicLongConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("merge-policy".equals(nodeName)) {
                MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
                atomicLongConfig.setMergePolicyConfig(mergePolicyConfig);
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            atomicLongConfig.setQuorumName(value);
        }
        this.config.addAtomicLongConfig(atomicLongConfig);
    }

    protected void handleAtomicReference(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        AtomicReferenceConfig atomicReferenceConfig = new AtomicReferenceConfig(name);
        this.handleAtomicReferenceNode(node, atomicReferenceConfig);
    }

    void handleAtomicReferenceNode(Node node, AtomicReferenceConfig atomicReferenceConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if ("merge-policy".equals(nodeName)) {
                MergePolicyConfig mergePolicyConfig = this.createMergePolicyConfig(n);
                atomicReferenceConfig.setMergePolicyConfig(mergePolicyConfig);
                continue;
            }
            if (!"quorum-ref".equals(nodeName)) continue;
            atomicReferenceConfig.setQuorumName(value);
        }
        this.config.addAtomicReferenceConfig(atomicReferenceConfig);
    }

    protected void handleCountDownLatchConfig(Node node) {
        Node attName = node.getAttributes().getNamedItem("name");
        String name = this.getTextContent(attName);
        CountDownLatchConfig countDownLatchConfig = new CountDownLatchConfig(name);
        this.handleCountDownLatchNode(node, countDownLatchConfig);
    }

    void handleCountDownLatchNode(Node node, CountDownLatchConfig countDownLatchConfig) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(n);
            String value = this.getTextContent(n).trim();
            if (!"quorum-ref".equals(nodeName)) continue;
            countDownLatchConfig.setQuorumName(value);
        }
        this.config.addCountDownLatchConfig(countDownLatchConfig);
    }

    protected void handleListeners(Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            if (!"listener".equals(DomConfigHelper.cleanNodeName(child))) continue;
            String listenerClass = this.getTextContent(child);
            this.config.addListenerConfig(new ListenerConfig(listenerClass));
        }
    }

    private void handlePartitionGroup(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node enabledNode = attributes.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
        this.config.getPartitionGroupConfig().setEnabled(enabled);
        Node groupTypeNode = attributes.getNamedItem("group-type");
        PartitionGroupConfig.MemberGroupType groupType = groupTypeNode != null ? PartitionGroupConfig.MemberGroupType.valueOf(StringUtil.upperCaseInternal(this.getTextContent(groupTypeNode))) : PartitionGroupConfig.MemberGroupType.PER_MEMBER;
        this.config.getPartitionGroupConfig().setGroupType(groupType);
        for (Node child : DomConfigHelper.childElements(node)) {
            if (!"member-group".equals(DomConfigHelper.cleanNodeName(child))) continue;
            this.handleMemberGroup(child, this.config);
        }
    }

    protected void handleMemberGroup(Node node, Config config) {
        MemberGroupConfig memberGroupConfig = new MemberGroupConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            if (!"interface".equals(DomConfigHelper.cleanNodeName(child))) continue;
            String value = this.getTextContent(child);
            memberGroupConfig.addInterface(value);
        }
        config.getPartitionGroupConfig().addMemberGroupConfig(memberGroupConfig);
    }

    private void handleSerialization(Node node) {
        SerializationConfig serializationConfig = this.parseSerialization(node);
        this.config.setSerializationConfig(serializationConfig);
    }

    private void handleManagementCenterConfig(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node enabledNode = attrs.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
        Node intervalNode = attrs.getNamedItem("update-interval");
        int interval = intervalNode != null ? DomConfigHelper.getIntegerValue("update-interval", this.getTextContent(intervalNode)) : 3;
        ManagementCenterConfig managementCenterConfig = this.config.getManagementCenterConfig();
        managementCenterConfig.setEnabled(enabled);
        managementCenterConfig.setUpdateInterval(interval);
        Node scriptingEnabledNode = attrs.getNamedItem("scripting-enabled");
        if (scriptingEnabledNode != null) {
            managementCenterConfig.setScriptingEnabled(DomConfigHelper.getBooleanValue(this.getTextContent(scriptingEnabledNode)));
        }
        this.handleManagementCenterChildElements(node, managementCenterConfig);
    }

    private void handleManagementCenterChildElements(Node node, ManagementCenterConfig managementCenterConfig) {
        boolean isComplexType = false;
        List<String> complexTypeElements = Arrays.asList("url", "mutual-auth");
        for (Node c : DomConfigHelper.childElements(node)) {
            if (!complexTypeElements.contains(c.getNodeName())) continue;
            isComplexType = true;
            break;
        }
        if (!isComplexType) {
            String url = this.getTextContent(node);
            managementCenterConfig.setUrl("".equals(url) ? null : url);
        } else {
            for (Node child : DomConfigHelper.childElements(node)) {
                if ("url".equals(DomConfigHelper.cleanNodeName(child))) {
                    String url = this.getTextContent(child);
                    managementCenterConfig.setUrl(url);
                    continue;
                }
                if (!"mutual-auth".equals(DomConfigHelper.cleanNodeName(child))) continue;
                this.handleMcMutualAuthConfig(child);
            }
        }
    }

    private void handleSecurity(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node enabledNode = attributes.getNamedItem("enabled");
        boolean enabled = enabledNode != null && DomConfigHelper.getBooleanValue(this.getTextContent(enabledNode));
        this.config.getSecurityConfig().setEnabled(enabled);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("member-credentials-factory".equals(nodeName)) {
                this.handleCredentialsFactory(child);
                continue;
            }
            if ("member-login-modules".equals(nodeName)) {
                this.handleLoginModules(child, true, this.config);
                continue;
            }
            if ("client-login-modules".equals(nodeName)) {
                this.handleLoginModules(child, false, this.config);
                continue;
            }
            if ("client-permission-policy".equals(nodeName)) {
                this.handlePermissionPolicy(child);
                continue;
            }
            if ("client-permissions".equals(nodeName)) {
                this.handleSecurityPermissions(child);
                continue;
            }
            if ("security-interceptors".equals(nodeName)) {
                this.handleSecurityInterceptors(child);
                continue;
            }
            if (!"client-block-unmapped-actions".equals(nodeName)) continue;
            this.config.getSecurityConfig().setClientBlockUnmappedActions(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
        }
    }

    private void handleSecurityInterceptors(Node node) {
        SecurityConfig cfg = this.config.getSecurityConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            this.handleSecurityInterceptorsChild(cfg, child);
        }
    }

    protected void handleSecurityInterceptorsChild(SecurityConfig cfg, Node child) {
        String nodeName = DomConfigHelper.cleanNodeName(child);
        if ("interceptor".equals(nodeName)) {
            NamedNodeMap attrs = child.getAttributes();
            Node classNameNode = attrs.getNamedItem("class-name");
            String className = this.getTextContent(classNameNode);
            cfg.addSecurityInterceptorConfig(new SecurityInterceptorConfig(className));
        }
    }

    protected void handleMemberAttributes(Node node) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(n);
            if (!"attribute".equals(name)) continue;
            String value = this.getTextContent(n);
            String attributeName = this.getTextContent(n.getAttributes().getNamedItem("name"));
            this.handleMemberAttributesNode(n, attributeName, value);
        }
    }

    void handleMemberAttributesNode(Node n, String attributeName, String value) {
        String attributeType = this.getTextContent(n.getAttributes().getNamedItem("type"));
        if ("string".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setStringAttribute(attributeName, value);
        } else if ("boolean".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setBooleanAttribute(attributeName, Boolean.parseBoolean(value));
        } else if ("byte".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setByteAttribute(attributeName, Byte.parseByte(value));
        } else if ("double".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setDoubleAttribute(attributeName, Double.parseDouble(value));
        } else if ("float".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setFloatAttribute(attributeName, Float.parseFloat(value));
        } else if ("int".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setIntAttribute(attributeName, Integer.parseInt(value));
        } else if ("long".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setLongAttribute(attributeName, Long.parseLong(value));
        } else if ("short".equals(attributeType)) {
            this.config.getMemberAttributeConfig().setShortAttribute(attributeName, Short.parseShort(value));
        } else {
            this.config.getMemberAttributeConfig().setStringAttribute(attributeName, value);
        }
    }

    private void handleCredentialsFactory(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node classNameNode = attrs.getNamedItem("class-name");
        String className = this.getTextContent(classNameNode);
        SecurityConfig cfg = this.config.getSecurityConfig();
        CredentialsFactoryConfig credentialsFactoryConfig = new CredentialsFactoryConfig(className);
        cfg.setMemberCredentialsConfig(credentialsFactoryConfig);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(child, credentialsFactoryConfig.getProperties());
            break;
        }
    }

    protected void handleLoginModules(Node node, boolean member, Config config) {
        SecurityConfig cfg = config.getSecurityConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"login-module".equals(nodeName)) continue;
            LoginModuleConfig lm = this.handleLoginModule(child);
            if (member) {
                cfg.addMemberLoginModuleConfig(lm);
                continue;
            }
            cfg.addClientLoginModuleConfig(lm);
        }
    }

    LoginModuleConfig handleLoginModule(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node classNameNode = attrs.getNamedItem("class-name");
        String className = this.getTextContent(classNameNode);
        Node usageNode = attrs.getNamedItem("usage");
        LoginModuleConfig.LoginModuleUsage usage = usageNode != null ? LoginModuleConfig.LoginModuleUsage.get(this.getTextContent(usageNode)) : LoginModuleConfig.LoginModuleUsage.REQUIRED;
        LoginModuleConfig moduleConfig = new LoginModuleConfig(className, usage);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(child, moduleConfig.getProperties());
            break;
        }
        return moduleConfig;
    }

    private void handlePermissionPolicy(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Node classNameNode = attrs.getNamedItem("class-name");
        String className = this.getTextContent(classNameNode);
        SecurityConfig cfg = this.config.getSecurityConfig();
        PermissionPolicyConfig policyConfig = new PermissionPolicyConfig(className);
        cfg.setClientPolicyConfig(policyConfig);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"properties".equals(nodeName)) continue;
            this.fillProperties(child, policyConfig.getProperties());
            break;
        }
    }

    protected void handleSecurityPermissions(Node node) {
        String onJoinOp = this.getAttribute(node, "on-join-operation");
        if (onJoinOp != null) {
            OnJoinPermissionOperationName onJoinPermissionOperation = OnJoinPermissionOperationName.valueOf(StringUtil.upperCaseInternal(onJoinOp));
            this.config.getSecurityConfig().setOnJoinPermissionOperation(onJoinPermissionOperation);
        }
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            PermissionConfig.PermissionType type = PermissionConfig.PermissionType.getType(nodeName);
            if (type == null) {
                throw new InvalidConfigurationException("Security permission type is not valid " + nodeName);
            }
            this.handleSecurityPermission(child, type);
        }
    }

    void handleSecurityPermission(Node node, PermissionConfig.PermissionType type) {
        SecurityConfig cfg = this.config.getSecurityConfig();
        NamedNodeMap attrs = node.getAttributes();
        Node nameNode = attrs.getNamedItem("name");
        String name = nameNode != null ? this.getTextContent(nameNode) : "*";
        Node principalNode = attrs.getNamedItem("principal");
        String principal = principalNode != null ? this.getTextContent(principalNode) : "*";
        PermissionConfig permConfig = new PermissionConfig(type, name, principal);
        cfg.addClientPermissionConfig(permConfig);
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("endpoints".equals(nodeName)) {
                this.handleSecurityPermissionEndpoints(child, permConfig);
                continue;
            }
            if (!"actions".equals(nodeName)) continue;
            this.handleSecurityPermissionActions(child, permConfig);
        }
    }

    void handleSecurityPermissionEndpoints(Node node, PermissionConfig permConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"endpoint".equals(nodeName)) continue;
            permConfig.addEndpoint(this.getTextContent(child).trim());
        }
    }

    void handleSecurityPermissionActions(Node node, PermissionConfig permConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"action".equals(nodeName)) continue;
            permConfig.addAction(this.getTextContent(child).trim());
        }
    }

    private void handleMemcacheProtocol(Node node) {
        MemcacheProtocolConfig memcacheProtocolConfig = new MemcacheProtocolConfig();
        this.config.getNetworkConfig().setMemcacheProtocolConfig(memcacheProtocolConfig);
        boolean enabled = DomConfigHelper.getBooleanValue(this.getAttribute(node, "enabled"));
        memcacheProtocolConfig.setEnabled(enabled);
    }

    private void handleRestApi(Node node) {
        RestApiConfig restApiConfig = new RestApiConfig();
        this.config.getNetworkConfig().setRestApiConfig(restApiConfig);
        boolean enabled = DomConfigHelper.getBooleanValue(this.getAttribute(node, "enabled"));
        restApiConfig.setEnabled(enabled);
        this.handleRestApiEndpointGroups(node);
    }

    protected void handleRestApiEndpointGroups(Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"endpoint-group".equals(nodeName)) continue;
            String name = this.getAttribute(child, "name");
            this.handleEndpointGroup(child, name);
        }
    }

    private void handleRestEndpointGroup(RestServerEndpointConfig config, Node node) {
        RestEndpointGroup endpointGroup;
        boolean enabled = DomConfigHelper.getBooleanValue(this.getAttribute(node, "enabled"));
        String name = this.extractName(node);
        try {
            endpointGroup = RestEndpointGroup.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Wrong name attribute value was provided in endpoint-group element: " + name + "\nAllowed values: " + Arrays.toString((Object[])RestEndpointGroup.values()));
        }
        if (enabled) {
            config.enableGroups(endpointGroup);
        } else {
            config.disableGroups(endpointGroup);
        }
    }

    protected String extractName(Node node) {
        return this.getAttribute(node, "name");
    }

    void handleEndpointGroup(Node node, String name) {
        RestEndpointGroup endpointGroup;
        boolean enabled = DomConfigHelper.getBooleanValue(this.getAttribute(node, "enabled"));
        try {
            endpointGroup = RestEndpointGroup.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Wrong name attribute value was provided in endpoint-group element: " + name + "\nAllowed values: " + Arrays.toString((Object[])RestEndpointGroup.values()));
        }
        RestApiConfig restApiConfig = this.config.getNetworkConfig().getRestApiConfig();
        if (enabled) {
            restApiConfig.enableGroups(endpointGroup);
        } else {
            restApiConfig.disableGroups(endpointGroup);
        }
    }

    private void handleCPSubsystem(Node node) {
        CPSubsystemConfig cpSubsystemConfig = this.config.getCPSubsystemConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("raft-algorithm".equals(nodeName)) {
                this.handleRaftAlgorithm(cpSubsystemConfig.getRaftAlgorithmConfig(), child);
                continue;
            }
            if ("semaphores".equals(nodeName)) {
                this.handleCPSemaphores(cpSubsystemConfig, child);
                continue;
            }
            if ("locks".equals(nodeName)) {
                this.handleFencedLocks(cpSubsystemConfig, child);
                continue;
            }
            String value = this.getTextContent(child).trim();
            if ("cp-member-count".equals(nodeName)) {
                cpSubsystemConfig.setCPMemberCount(Integer.parseInt(value));
                continue;
            }
            if ("group-size".equals(nodeName)) {
                cpSubsystemConfig.setGroupSize(Integer.parseInt(value));
                continue;
            }
            if ("session-time-to-live-seconds".equals(nodeName)) {
                cpSubsystemConfig.setSessionTimeToLiveSeconds(Integer.parseInt(value));
                continue;
            }
            if ("session-heartbeat-interval-seconds".equals(nodeName)) {
                cpSubsystemConfig.setSessionHeartbeatIntervalSeconds(Integer.parseInt(value));
                continue;
            }
            if ("missing-cp-member-auto-removal-seconds".equals(nodeName)) {
                cpSubsystemConfig.setMissingCPMemberAutoRemovalSeconds(Integer.parseInt(value));
                continue;
            }
            if (!"fail-on-indeterminate-operation-state".equals(nodeName)) continue;
            cpSubsystemConfig.setFailOnIndeterminateOperationState(Boolean.parseBoolean(value));
        }
    }

    private void handleRaftAlgorithm(RaftAlgorithmConfig raftAlgorithmConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            String value = this.getTextContent(child).trim();
            if ("leader-election-timeout-in-millis".equals(nodeName)) {
                raftAlgorithmConfig.setLeaderElectionTimeoutInMillis(Long.parseLong(value));
                continue;
            }
            if ("leader-heartbeat-period-in-millis".equals(nodeName)) {
                raftAlgorithmConfig.setLeaderHeartbeatPeriodInMillis(Long.parseLong(value));
                continue;
            }
            if ("max-missed-leader-heartbeat-count".equals(nodeName)) {
                raftAlgorithmConfig.setMaxMissedLeaderHeartbeatCount(Integer.parseInt(value));
                continue;
            }
            if ("append-request-max-entry-count".equals(nodeName)) {
                raftAlgorithmConfig.setAppendRequestMaxEntryCount(Integer.parseInt(value));
                continue;
            }
            if ("commit-index-advance-count-to-snapshot".equals(nodeName)) {
                raftAlgorithmConfig.setCommitIndexAdvanceCountToSnapshot(Integer.parseInt(value));
                continue;
            }
            if ("uncommitted-entry-count-to-reject-new-appends".equals(nodeName)) {
                raftAlgorithmConfig.setUncommittedEntryCountToRejectNewAppends(Integer.parseInt(value));
                continue;
            }
            if (!"append-request-backoff-timeout-in-millis".equals(nodeName)) continue;
            raftAlgorithmConfig.setAppendRequestBackoffTimeoutInMillis(Long.parseLong(value));
        }
    }

    void handleCPSemaphores(CPSubsystemConfig cpSubsystemConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            CPSemaphoreConfig cpSemaphoreConfig = new CPSemaphoreConfig();
            for (Node subChild : DomConfigHelper.childElements(child)) {
                String nodeName = DomConfigHelper.cleanNodeName(subChild);
                String value = this.getTextContent(subChild).trim();
                if ("name".equals(nodeName)) {
                    cpSemaphoreConfig.setName(value);
                    continue;
                }
                if (!"jdk-compatible".equals(nodeName)) continue;
                cpSemaphoreConfig.setJDKCompatible(Boolean.parseBoolean(value));
            }
            cpSubsystemConfig.addSemaphoreConfig(cpSemaphoreConfig);
        }
    }

    void handleFencedLocks(CPSubsystemConfig cpSubsystemConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            FencedLockConfig lockConfig = new FencedLockConfig();
            for (Node subChild : DomConfigHelper.childElements(child)) {
                String nodeName = DomConfigHelper.cleanNodeName(subChild);
                String value = this.getTextContent(subChild).trim();
                if ("name".equals(nodeName)) {
                    lockConfig.setName(value);
                    continue;
                }
                if (!"lock-acquire-limit".equals(nodeName)) continue;
                lockConfig.setLockAcquireLimit(Integer.parseInt(value));
            }
            cpSubsystemConfig.addLockConfig(lockConfig);
        }
    }
}

