/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.GlobalSerializerConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MemberDomConfigProcessor;
import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.OnJoinPermissionOperationName;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SecurityInterceptorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.ServerSocketEndpointConfig;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.config.ServicesConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.FencedLockConfig;
import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.function.Function;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class YamlMemberDomConfigProcessor
extends MemberDomConfigProcessor {
    YamlMemberDomConfigProcessor(boolean domLevel3, Config config) {
        super(domLevel3, config);
    }

    @Override
    protected void handleSecurityInterceptorsChild(SecurityConfig cfg, Node child) {
        String className = child.getTextContent();
        cfg.addSecurityInterceptorConfig(new SecurityInterceptorConfig(className));
    }

    @Override
    protected void handleSecurityPermissions(Node node) {
        String onJoinOp = this.getAttribute(node, "on-join-operation");
        if (onJoinOp != null) {
            OnJoinPermissionOperationName onJoinPermissionOperation = OnJoinPermissionOperationName.valueOf(StringUtil.upperCaseInternal(onJoinOp));
            this.config.getSecurityConfig().setOnJoinPermissionOperation(onJoinPermissionOperation);
        }
        Iterable<Node> nodes = DomConfigHelper.childElements(node);
        for (Node child : nodes) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("on-join-operation".equals(nodeName)) continue;
            PermissionConfig.PermissionType type = PermissionConfig.PermissionType.getType(nodeName = "all".equals(nodeName) ? nodeName + "-permissions" : nodeName + "-permission");
            if (type == null) {
                throw new InvalidConfigurationException("Security permission type is not valid " + nodeName);
            }
            if (PermissionConfig.PermissionType.CONFIG == type || PermissionConfig.PermissionType.ALL == type || PermissionConfig.PermissionType.TRANSACTION == type) {
                this.handleSecurityPermission(child, type);
                continue;
            }
            this.handleSecurityPermissionGroup(child, type);
        }
    }

    private void handleSecurityPermissionGroup(Node node, PermissionConfig.PermissionType type) {
        for (Node permissionNode : DomConfigHelper.childElements(node)) {
            this.handleSecurityPermission(permissionNode, type);
        }
    }

    @Override
    void handleSecurityPermissionActions(Node node, PermissionConfig permConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            permConfig.addAction(this.getTextContent(child).trim());
        }
    }

    @Override
    void handleSecurityPermissionEndpoints(Node node, PermissionConfig permConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            permConfig.addEndpoint(this.getTextContent(child).trim());
        }
    }

    @Override
    protected void handleLoginModules(Node node, boolean member, Config config) {
        SecurityConfig cfg = config.getSecurityConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            LoginModuleConfig lm = this.handleLoginModule(child);
            if (member) {
                cfg.addMemberLoginModuleConfig(lm);
                continue;
            }
            cfg.addClientLoginModuleConfig(lm);
        }
    }

    @Override
    protected void handleTrustedInterfaces(MulticastConfig multicastConfig, Node n) {
        YamlSequence yamlNode = W3cDomUtil.getWrappedYamlSequence(n);
        for (YamlNode interfaceNode : yamlNode.children()) {
            String trustedInterface = (String)YamlUtil.asScalar(interfaceNode).nodeValue();
            multicastConfig.addTrustedInterface(trustedInterface);
        }
        super.handleTrustedInterfaces(multicastConfig, n);
    }

    @Override
    protected void handleWanReplication(Node node) {
        for (Node wanReplicationNode : DomConfigHelper.childElements(node)) {
            WanReplicationConfig wanReplicationConfig = new WanReplicationConfig();
            wanReplicationConfig.setName(wanReplicationNode.getNodeName());
            this.handleWanReplicationNode(wanReplicationNode, wanReplicationConfig);
        }
    }

    @Override
    protected void handleWanReplicationChild(WanReplicationConfig wanReplicationConfig, Node nodeTarget, String nodeName) {
        if ("wan-publisher".equals(nodeName)) {
            for (Node publisherNode : DomConfigHelper.childElements(nodeTarget)) {
                WanPublisherConfig publisherConfig = new WanPublisherConfig();
                String groupNameOrPublisherId = publisherNode.getNodeName();
                Node groupNameAttr = publisherNode.getAttributes().getNamedItem("group-name");
                String groupName = groupNameAttr != null ? groupNameAttr.getTextContent() : groupNameOrPublisherId;
                String publisherId = groupNameAttr != null ? groupNameOrPublisherId : null;
                publisherConfig.setPublisherId(publisherId);
                publisherConfig.setGroupName(groupName);
                this.handleWanPublisherNode(wanReplicationConfig, publisherNode, publisherConfig);
            }
        } else if ("wan-consumer".equals(nodeName)) {
            this.handleWanConsumerNode(wanReplicationConfig, nodeTarget);
        }
    }

    @Override
    protected void handlePort(Node node, Config config) {
        NetworkConfig networkConfig = config.getNetworkConfig();
        NamedNodeMap attributes = node.getAttributes();
        for (int a = 0; a < attributes.getLength(); ++a) {
            int portCount;
            Node att = attributes.item(a);
            String value = this.getTextContent(att).trim();
            if ("port".equals(att.getNodeName())) {
                portCount = Integer.parseInt(value);
                networkConfig.setPort(portCount);
                continue;
            }
            if ("auto-increment".equals(att.getNodeName())) {
                networkConfig.setPortAutoIncrement(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if (!"port-count".equals(att.getNodeName())) continue;
            portCount = Integer.parseInt(value);
            networkConfig.setPortCount(portCount);
        }
    }

    @Override
    protected void handleSemaphore(Node node) {
        for (Node semaphoreNode : DomConfigHelper.childElements(node)) {
            SemaphoreConfig sConfig = new SemaphoreConfig();
            sConfig.setName(semaphoreNode.getNodeName());
            this.handleSemaphoreNode(semaphoreNode, sConfig);
        }
    }

    @Override
    protected void handleQueue(Node node) {
        for (Node queueNode : DomConfigHelper.childElements(node)) {
            QueueConfig queueConfig = new QueueConfig();
            queueConfig.setName(queueNode.getNodeName());
            this.handleQueueNode(queueNode, queueConfig);
        }
    }

    @Override
    protected void handleList(Node node) {
        for (Node listNode : DomConfigHelper.childElements(node)) {
            ListConfig listConfig = new ListConfig();
            listConfig.setName(listNode.getNodeName());
            this.handleListNode(listNode, listConfig);
        }
    }

    @Override
    protected void handleSet(Node node) {
        for (Node setNode : DomConfigHelper.childElements(node)) {
            SetConfig setConfig = new SetConfig();
            setConfig.setName(setNode.getNodeName());
            this.handleSetNode(setNode, setConfig);
        }
    }

    @Override
    protected void handleLock(Node node) {
        for (Node lockNode : DomConfigHelper.childElements(node)) {
            LockConfig lockConfig = new LockConfig();
            lockConfig.setName(lockNode.getNodeName());
            this.handleLockNode(lockNode, lockConfig);
        }
    }

    @Override
    protected void handleReliableTopic(Node node) {
        for (Node topicNode : DomConfigHelper.childElements(node)) {
            ReliableTopicConfig topicConfig = new ReliableTopicConfig();
            topicConfig.setName(topicNode.getNodeName());
            this.handleReliableTopicNode(topicNode, topicConfig);
        }
    }

    @Override
    protected void handleTopic(Node node) {
        for (Node topicNode : DomConfigHelper.childElements(node)) {
            TopicConfig topicConfig = new TopicConfig();
            topicConfig.setName(topicNode.getNodeName());
            this.handleTopicNode(topicNode, topicConfig);
        }
    }

    @Override
    protected void handleRingbuffer(Node node) {
        for (Node rbNode : DomConfigHelper.childElements(node)) {
            RingbufferConfig ringBufferConfig = new RingbufferConfig();
            ringBufferConfig.setName(rbNode.getNodeName());
            this.handleRingBufferNode(rbNode, ringBufferConfig);
        }
    }

    @Override
    protected void handleAtomicLong(Node node) {
        for (Node atomicLongNode : DomConfigHelper.childElements(node)) {
            AtomicLongConfig atomicLongConfig = new AtomicLongConfig();
            atomicLongConfig.setName(atomicLongNode.getNodeName());
            this.handleAtomicLongNode(atomicLongNode, atomicLongConfig);
        }
    }

    @Override
    protected void handleAtomicReference(Node node) {
        for (Node atomicReferenceNode : DomConfigHelper.childElements(node)) {
            AtomicReferenceConfig atomicReferenceConfig = new AtomicReferenceConfig();
            atomicReferenceConfig.setName(atomicReferenceNode.getNodeName());
            this.handleAtomicReferenceNode(atomicReferenceNode, atomicReferenceConfig);
        }
    }

    @Override
    protected void handleCountDownLatchConfig(Node node) {
        for (Node countDownLatchNode : DomConfigHelper.childElements(node)) {
            CountDownLatchConfig countDownLatchConfig = new CountDownLatchConfig();
            countDownLatchConfig.setName(countDownLatchNode.getNodeName());
            this.handleCountDownLatchNode(countDownLatchNode, countDownLatchConfig);
        }
    }

    @Override
    protected void handleMap(Node parentNode) {
        for (Node mapNode : DomConfigHelper.childElements(parentNode)) {
            MapConfig mapConfig = new MapConfig();
            mapConfig.setName(mapNode.getNodeName());
            this.handleMapNode(mapNode, mapConfig);
        }
    }

    @Override
    protected void handleCache(Node parentNode) {
        for (Node cacheNode : DomConfigHelper.childElements(parentNode)) {
            CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
            cacheConfig.setName(cacheNode.getNodeName());
            this.handleCacheNode(cacheNode, cacheConfig);
        }
    }

    @Override
    protected void handleQuorum(Node node) {
        for (Node quorumNode : DomConfigHelper.childElements(node)) {
            QuorumConfig quorumConfig = new QuorumConfig();
            String quorumName = quorumNode.getNodeName();
            quorumConfig.setName(quorumName);
            this.handleQuorumNode(quorumNode, quorumConfig, quorumName);
        }
    }

    @Override
    protected void handleEventJournal(Node node) throws Exception {
        for (Node typeNode : DomConfigHelper.childElements(node)) {
            String nodeName = typeNode.getNodeName().toLowerCase();
            if ("map".equals(nodeName)) {
                this.handleMapEventJournal(typeNode);
                continue;
            }
            if ("cache".equals(nodeName)) {
                this.handleCacheEventJournal(typeNode);
                continue;
            }
            throw new ConfigurationException("Mapping name should either be 'map' or 'cache', but " + nodeName + " found");
        }
    }

    private void handleMapEventJournal(Node mapNode) throws Exception {
        for (Node journalNode : DomConfigHelper.childElements(mapNode)) {
            EventJournalConfig journalConfig = new EventJournalConfig();
            journalConfig.setMapName(journalNode.getNodeName());
            this.handleViaReflection(journalNode, this.config, journalConfig);
        }
    }

    private void handleCacheEventJournal(Node cacheNode) throws Exception {
        for (Node journalNode : DomConfigHelper.childElements(cacheNode)) {
            EventJournalConfig journalConfig = new EventJournalConfig();
            journalConfig.setCacheName(journalNode.getNodeName());
            this.handleViaReflection(journalNode, this.config, journalConfig);
        }
    }

    @Override
    protected void handleMerkleTree(Node node) throws Exception {
        for (Node typeNode : DomConfigHelper.childElements(node)) {
            String nodeName = typeNode.getNodeName().toLowerCase();
            if ("map".equals(nodeName)) {
                this.handleMapMerkleTree(typeNode);
                continue;
            }
            throw new ConfigurationException("Mapping name should be 'map', but " + nodeName + " found");
        }
    }

    private void handleMapMerkleTree(Node mapNode) throws Exception {
        for (Node journalNode : DomConfigHelper.childElements(mapNode)) {
            MerkleTreeConfig merkleTreeConfig = new MerkleTreeConfig();
            merkleTreeConfig.setMapName(journalNode.getNodeName());
            this.handleViaReflection(journalNode, this.config, merkleTreeConfig);
        }
    }

    @Override
    protected void handleFlakeIdGenerator(Node node) {
        for (Node genNode : DomConfigHelper.childElements(node)) {
            FlakeIdGeneratorConfig genConfig = new FlakeIdGeneratorConfig();
            genConfig.setName(genNode.getNodeName());
            this.handleFlakeIdGeneratorNode(genNode, genConfig);
        }
    }

    @Override
    protected void handleExecutor(Node node) throws Exception {
        for (Node executorNode : DomConfigHelper.childElements(node)) {
            ExecutorConfig executorConfig = new ExecutorConfig();
            executorConfig.setName(executorNode.getNodeName());
            this.handleViaReflection(executorNode, this.config, executorConfig);
        }
    }

    @Override
    protected void handleDurableExecutor(Node node) throws Exception {
        for (Node executorNode : DomConfigHelper.childElements(node)) {
            DurableExecutorConfig executorConfig = new DurableExecutorConfig();
            executorConfig.setName(executorNode.getNodeName());
            this.handleViaReflection(executorNode, this.config, executorConfig);
        }
    }

    @Override
    protected void handleScheduledExecutor(Node node) {
        for (Node executorNode : DomConfigHelper.childElements(node)) {
            ScheduledExecutorConfig executorConfig = new ScheduledExecutorConfig();
            executorConfig.setName(executorNode.getNodeName());
            this.handleScheduledExecutorNode(executorNode, executorConfig);
        }
    }

    @Override
    protected void handleCardinalityEstimator(Node node) {
        for (Node estimatorNode : DomConfigHelper.childElements(node)) {
            CardinalityEstimatorConfig estimatorConfig = new CardinalityEstimatorConfig();
            estimatorConfig.setName(estimatorNode.getNodeName());
            this.handleCardinalityEstimatorNode(estimatorNode, estimatorConfig);
        }
    }

    @Override
    protected void handlePNCounter(Node node) throws Exception {
        for (Node counterNode : DomConfigHelper.childElements(node)) {
            PNCounterConfig counterConfig = new PNCounterConfig();
            counterConfig.setName(counterNode.getNodeName());
            this.handleViaReflection(counterNode, this.config, counterConfig);
        }
    }

    @Override
    protected void handleMultiMap(Node node) {
        for (Node multiMapNode : DomConfigHelper.childElements(node)) {
            MultiMapConfig multiMapConfig = new MultiMapConfig();
            multiMapConfig.setName(multiMapNode.getNodeName());
            this.handleMultiMapNode(multiMapNode, multiMapConfig);
        }
    }

    @Override
    protected void handleReplicatedMap(Node node) {
        for (Node replicatedMapNode : DomConfigHelper.childElements(node)) {
            ReplicatedMapConfig replicatedMapConfig = new ReplicatedMapConfig();
            replicatedMapConfig.setName(replicatedMapNode.getNodeName());
            this.handleReplicatedMapNode(replicatedMapNode, replicatedMapConfig);
        }
    }

    @Override
    protected void mapWanReplicationRefHandle(Node n, MapConfig mapConfig) {
        for (Node mapNode : DomConfigHelper.childElements(n)) {
            WanReplicationRef wanReplicationRef = new WanReplicationRef();
            wanReplicationRef.setName(mapNode.getNodeName());
            this.handleMapWanReplicationRefNode(mapNode, mapConfig, wanReplicationRef);
        }
    }

    @Override
    protected void handleWanFilters(Node wanChild, WanReplicationRef wanReplicationRef) {
        for (Node filter : DomConfigHelper.childElements(wanChild)) {
            wanReplicationRef.addFilter(this.getTextContent(filter));
        }
    }

    @Override
    protected void handleMaxSizeConfig(MapConfig mapConfig, Node node, String value) {
        MaxSizeConfig msc = mapConfig.getMaxSizeConfig();
        NamedNodeMap attributes = node.getAttributes();
        Node maxSizePolicy = attributes.getNamedItem("policy");
        if (maxSizePolicy != null) {
            msc.setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.valueOf(StringUtil.upperCaseInternal(this.getTextContent(maxSizePolicy))));
        }
        msc.setSize(DomConfigHelper.getIntegerValue("max-size", this.getTextContent(attributes.getNamedItem("max-size"))));
    }

    @Override
    protected void mapIndexesHandle(Node n, MapConfig mapConfig) {
        for (Node indexNode : DomConfigHelper.childElements(n)) {
            NamedNodeMap attrs = indexNode.getAttributes();
            boolean ordered = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("ordered")));
            String attribute = indexNode.getNodeName();
            mapConfig.addMapIndexConfig(new MapIndexConfig(attribute, ordered));
        }
    }

    @Override
    protected void mapAttributesHandle(Node n, MapConfig mapConfig) {
        for (Node extractorNode : DomConfigHelper.childElements(n)) {
            NamedNodeMap attrs = extractorNode.getAttributes();
            String extractor = this.getTextContent(attrs.getNamedItem("extractor"));
            String name = extractorNode.getNodeName();
            mapConfig.addMapAttributeConfig(new MapAttributeConfig(name, extractor));
        }
    }

    @Override
    protected void mapQueryCacheHandler(Node n, MapConfig mapConfig) {
        for (Node queryCacheNode : DomConfigHelper.childElements(n)) {
            String cacheName = queryCacheNode.getNodeName();
            QueryCacheConfig queryCacheConfig = new QueryCacheConfig(cacheName);
            this.handleMapQueryCacheNode(mapConfig, queryCacheNode, queryCacheConfig);
        }
    }

    @Override
    protected void queryCachePredicateHandler(Node childNode, QueryCacheConfig queryCacheConfig) {
        NamedNodeMap predicateAttributes = childNode.getAttributes();
        Node classNameNode = predicateAttributes.getNamedItem("class-name");
        Node sqlNode = predicateAttributes.getNamedItem("sql");
        if (classNameNode != null && sqlNode != null) {
            throw new InvalidConfigurationException("Both class-name and sql is defined for the predicate of map " + childNode.getParentNode().getParentNode().getNodeName());
        }
        if (classNameNode == null && sqlNode == null) {
            throw new InvalidConfigurationException("Either class-name and sql should be defined for the predicate of map " + childNode.getParentNode().getParentNode().getNodeName());
        }
        PredicateConfig predicateConfig = new PredicateConfig();
        if (classNameNode != null) {
            predicateConfig.setClassName(this.getTextContent(classNameNode));
        } else if (sqlNode != null) {
            predicateConfig.setSql(this.getTextContent(sqlNode));
        }
        queryCacheConfig.setPredicateConfig(predicateConfig);
    }

    @Override
    protected void queryCacheIndexesHandle(Node n, QueryCacheConfig queryCacheConfig) {
        for (Node indexNode : DomConfigHelper.childElements(n)) {
            NamedNodeMap attrs = indexNode.getAttributes();
            boolean ordered = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("ordered")));
            String attribute = indexNode.getNodeName();
            queryCacheConfig.addIndexConfig(new MapIndexConfig(attribute, ordered));
        }
    }

    @Override
    protected void handleMemberGroup(Node node, Config config) {
        for (Node memberGroupNode : DomConfigHelper.childElements(node)) {
            MemberGroupConfig memberGroupConfig = new MemberGroupConfig();
            for (Node interfacesNode : DomConfigHelper.childElements(memberGroupNode)) {
                memberGroupConfig.addInterface(interfacesNode.getNodeValue().trim());
            }
            config.getPartitionGroupConfig().addMemberGroupConfig(memberGroupConfig);
        }
    }

    @Override
    protected MergePolicyConfig createMergePolicyConfig(Node node) {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
        String policyString = this.getTextContent(node.getAttributes().getNamedItem("class-name"));
        mergePolicyConfig.setPolicy(policyString);
        String att = this.getAttribute(node, "batch-size");
        if (att != null) {
            mergePolicyConfig.setBatchSize(DomConfigHelper.getIntegerValue("batch-size", att));
        }
        return mergePolicyConfig;
    }

    @Override
    protected void mapPartitionLostListenerHandle(Node n, MapConfig mapConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            String listenerClass = listenerNode.getNodeValue();
            mapConfig.addMapPartitionLostListenerConfig(new MapPartitionLostListenerConfig(listenerClass));
        }
    }

    @Override
    protected void cachePartitionLostListenerHandle(Node n, CacheSimpleConfig cacheConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            String listenerClass = listenerNode.getNodeValue();
            cacheConfig.addCachePartitionLostListenerConfig(new CachePartitionLostListenerConfig(listenerClass));
        }
    }

    @Override
    protected void cacheListenerHandle(Node n, CacheSimpleConfig cacheSimpleConfig) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            this.handleCacheEntryListenerNode(cacheSimpleConfig, listenerNode);
        }
    }

    @Override
    protected void handleItemListeners(Node n, Function<ItemListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            NamedNodeMap attrs = listenerNode.getAttributes();
            boolean incValue = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("include-value")));
            String listenerClass = this.getTextContent(attrs.getNamedItem("class-name"));
            configAddFunction.apply(new ItemListenerConfig(listenerClass, incValue));
        }
    }

    @Override
    protected void handleEntryListeners(Node n, Function<EntryListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            NamedNodeMap attrs = listenerNode.getAttributes();
            boolean incValue = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("include-value")));
            boolean local = DomConfigHelper.getBooleanValue(this.getTextContent(attrs.getNamedItem("local")));
            String listenerClass = this.getTextContent(attrs.getNamedItem("class-name"));
            configAddFunction.apply(new EntryListenerConfig(listenerClass, local, incValue));
        }
    }

    @Override
    void handleMessageListeners(Node n, Function<ListenerConfig, Void> configAddFunction) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            String listenerClass = listenerNode.getNodeValue().trim();
            configAddFunction.apply(new ListenerConfig(listenerClass));
        }
    }

    @Override
    protected void handleQuorumListeners(QuorumConfig quorumConfig, Node n) {
        for (Node listenerNode : DomConfigHelper.childElements(n)) {
            String listenerClass = listenerNode.getNodeValue().trim();
            quorumConfig.addListenerConfig(new QuorumListenerConfig(listenerClass));
        }
    }

    @Override
    protected void handleServiceNodes(Node node, ServicesConfig servicesConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if ("enable-defaults".equals(nodeName)) continue;
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setName(nodeName);
            String enabledValue = this.getAttribute(child, "enabled");
            boolean enabled = DomConfigHelper.getBooleanValue(enabledValue);
            serviceConfig.setEnabled(enabled);
            for (Node n : DomConfigHelper.childElements(child)) {
                this.handleServiceNode(n, serviceConfig);
            }
            servicesConfig.addServiceConfig(serviceConfig);
        }
    }

    @Override
    protected void fillProperties(Node node, Map<String, Comparable> properties) {
        YamlMapping propertiesMapping = W3cDomUtil.getWrappedYamlMapping(node);
        for (YamlNode propNode : propertiesMapping.children()) {
            YamlScalar propScalar = YamlUtil.asScalar(propNode);
            String key = propScalar.nodeName();
            String value = propScalar.nodeValue().toString();
            properties.put(key, (Comparable)((Object)value));
        }
    }

    @Override
    protected void fillProperties(Node node, Properties properties) {
        YamlMapping propertiesMapping = W3cDomUtil.getWrappedYamlMapping(node);
        for (YamlNode propNode : propertiesMapping.children()) {
            YamlScalar propScalar = YamlUtil.asScalar(propNode);
            String key = propScalar.nodeName();
            String value = propScalar.nodeValue().toString();
            properties.put(key, value);
        }
    }

    @Override
    protected void handleDiscoveryStrategiesChild(DiscoveryConfig discoveryConfig, Node child) {
        String name = DomConfigHelper.cleanNodeName(child);
        if ("discovery-strategies".equals(name)) {
            NodeList strategies = child.getChildNodes();
            for (int i = 0; i < strategies.getLength(); ++i) {
                Node strategy = strategies.item(i);
                this.handleDiscoveryStrategy(strategy, discoveryConfig);
            }
        } else if ("node-filter".equals(name)) {
            this.handleDiscoveryNodeFilter(child, discoveryConfig);
        }
    }

    @Override
    protected SerializationConfig parseSerialization(Node node) {
        SerializationConfig serializationConfig = new SerializationConfig();
        for (Node child : DomConfigHelper.childElements(node)) {
            String value;
            String name = DomConfigHelper.cleanNodeName(child);
            if ("portable-version".equals(name)) {
                value = this.getTextContent(child);
                serializationConfig.setPortableVersion(DomConfigHelper.getIntegerValue(name, value));
                continue;
            }
            if ("check-class-def-errors".equals(name)) {
                value = this.getTextContent(child);
                serializationConfig.setCheckClassDefErrors(DomConfigHelper.getBooleanValue(value));
                continue;
            }
            if ("use-native-byte-order".equals(name)) {
                serializationConfig.setUseNativeByteOrder(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("byte-order".equals(name)) {
                value = this.getTextContent(child);
                ByteOrder byteOrder = null;
                if (ByteOrder.BIG_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.BIG_ENDIAN;
                } else if (ByteOrder.LITTLE_ENDIAN.toString().equals(value)) {
                    byteOrder = ByteOrder.LITTLE_ENDIAN;
                }
                serializationConfig.setByteOrder(byteOrder != null ? byteOrder : ByteOrder.BIG_ENDIAN);
                continue;
            }
            if ("enable-compression".equals(name)) {
                serializationConfig.setEnableCompression(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("enable-shared-object".equals(name)) {
                serializationConfig.setEnableSharedObject(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("allow-unsafe".equals(name)) {
                serializationConfig.setAllowUnsafe(DomConfigHelper.getBooleanValue(this.getTextContent(child)));
                continue;
            }
            if ("data-serializable-factories".equals(name)) {
                this.fillDataSerializableFactories(child, serializationConfig);
                continue;
            }
            if ("portable-factories".equals(name)) {
                this.fillPortableFactories(child, serializationConfig);
                continue;
            }
            if ("serializers".equals(name)) {
                this.fillSerializers(child, serializationConfig);
                continue;
            }
            if ("global-serializer".equals(name)) {
                this.fillGlobalSerializer(child, serializationConfig);
                continue;
            }
            if (!"java-serialization-filter".equals(name)) continue;
            this.fillJavaSerializationFilter(child, serializationConfig);
        }
        return serializationConfig;
    }

    private void fillGlobalSerializer(Node child, SerializationConfig serializationConfig) {
        GlobalSerializerConfig globalSerializerConfig = new GlobalSerializerConfig();
        String attrClassName = this.getAttribute(child, "class-name");
        String attrOverrideJavaSerialization = this.getAttribute(child, "override-java-serialization");
        boolean overrideJavaSerialization = attrOverrideJavaSerialization != null && DomConfigHelper.getBooleanValue(attrOverrideJavaSerialization.trim());
        globalSerializerConfig.setClassName(attrClassName);
        globalSerializerConfig.setOverrideJavaSerialization(overrideJavaSerialization);
        serializationConfig.setGlobalSerializerConfig(globalSerializerConfig);
    }

    @Override
    protected void fillSerializers(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            SerializerConfig serializerConfig = new SerializerConfig();
            String typeClassName = this.getAttribute(child, "type-class");
            String className = this.getAttribute(child, "class-name");
            serializerConfig.setTypeClassName(typeClassName);
            serializerConfig.setClassName(className);
            serializationConfig.addSerializerConfig(serializerConfig);
        }
    }

    @Override
    protected void fillDataSerializableFactories(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            NamedNodeMap attributes = child.getAttributes();
            Node factoryIdNode = attributes.getNamedItem("factory-id");
            Node classNameNode = attributes.getNamedItem("class-name");
            if (factoryIdNode == null) {
                throw new IllegalArgumentException("'factory-id' attribute of 'data-serializable-factory' is required!");
            }
            if (classNameNode == null) {
                throw new IllegalArgumentException("'class-name' attribute of 'data-serializable-factory' is required!");
            }
            int factoryId = Integer.parseInt(this.getTextContent(factoryIdNode));
            String className = this.getTextContent(classNameNode);
            serializationConfig.addDataSerializableFactoryClass(factoryId, className);
        }
    }

    @Override
    protected void fillPortableFactories(Node node, SerializationConfig serializationConfig) {
        for (Node child : DomConfigHelper.childElements(node)) {
            NamedNodeMap attributes = child.getAttributes();
            Node factoryIdNode = attributes.getNamedItem("factory-id");
            Node classNameNode = attributes.getNamedItem("class-name");
            if (factoryIdNode == null) {
                throw new IllegalArgumentException("'factory-id' attribute of 'portable-factory' is required!");
            }
            if (classNameNode == null) {
                throw new IllegalArgumentException("'class-name' attribute of 'portable-factory' is required!");
            }
            int factoryId = Integer.parseInt(this.getTextContent(factoryIdNode));
            String className = this.getTextContent(classNameNode);
            serializationConfig.addPortableFactoryClass(factoryId, className);
        }
    }

    @Override
    protected ClassFilter parseClassFilterList(Node node) {
        ClassFilter list = new ClassFilter();
        for (Node typeNode : DomConfigHelper.childElements(node)) {
            String name = DomConfigHelper.cleanNodeName(typeNode);
            if ("class".equals(name)) {
                for (Node classNode : DomConfigHelper.childElements(typeNode)) {
                    list.addClasses(this.getTextContent(classNode));
                }
                continue;
            }
            if ("package".equals(name)) {
                for (Node packageNode : DomConfigHelper.childElements(typeNode)) {
                    list.addPackages(this.getTextContent(packageNode));
                }
                continue;
            }
            if (!"prefix".equals(name)) continue;
            for (Node prefixNode : DomConfigHelper.childElements(typeNode)) {
                list.addPrefixes(this.getTextContent(prefixNode));
            }
        }
        return list;
    }

    @Override
    protected void handleMemberAttributes(Node node) {
        for (Node n : DomConfigHelper.childElements(node)) {
            String attributeValue = this.getTextContent(n.getAttributes().getNamedItem("value"));
            String attributeName = n.getNodeName();
            this.handleMemberAttributesNode(n, attributeName, attributeValue);
        }
    }

    @Override
    protected void handleOutboundPorts(Node child) {
        NetworkConfig networkConfig = this.config.getNetworkConfig();
        for (Node n : DomConfigHelper.childElements(child)) {
            String value = this.getTextContent(n);
            networkConfig.addOutboundPortDefinition(value);
        }
    }

    @Override
    protected void handleOutboundPorts(Node child, EndpointConfig endpointConfig) {
        for (Node n : DomConfigHelper.childElements(child)) {
            String value = this.getTextContent(n);
            endpointConfig.addOutboundPortDefinition(value);
        }
    }

    @Override
    protected void handleInterfacesList(Node node, InterfacesConfig interfaces) {
        for (Node interfacesNode : DomConfigHelper.childElements(node)) {
            if (!"interfaces".equals(StringUtil.lowerCaseInternal(DomConfigHelper.cleanNodeName(interfacesNode)))) continue;
            for (Node interfaceNode : DomConfigHelper.childElements(interfacesNode)) {
                String value = this.getTextContent(interfaceNode).trim();
                interfaces.addInterface(value);
            }
        }
    }

    @Override
    protected void handleListeners(Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String listenerClass = this.getTextContent(child);
            this.config.addListenerConfig(new ListenerConfig(listenerClass));
        }
    }

    @Override
    protected void handleMemberList(Node node, boolean advancedNetworkConfig) {
        JoinConfig join = this.joinConfig(advancedNetworkConfig);
        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = this.getTextContent(n).trim();
            tcpIpConfig.addMember(value);
        }
    }

    @Override
    protected void handleRestApiEndpointGroups(Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            String nodeName = DomConfigHelper.cleanNodeName(child);
            if (!"endpoint-groups".equals(nodeName)) continue;
            for (Node groupNode : DomConfigHelper.childElements(child)) {
                String groupName = groupNode.getNodeName();
                this.handleEndpointGroup(groupNode, groupName);
            }
        }
    }

    @Override
    protected String extractName(Node node) {
        return node.getNodeName();
    }

    @Override
    protected void handlePort(Node node, ServerSocketEndpointConfig endpointConfig) {
        String portStr;
        Node portNode = node.getAttributes().getNamedItem("port");
        if (portNode != null && (portStr = portNode.getNodeValue().trim()).length() > 0) {
            endpointConfig.setPort(Integer.parseInt(portStr));
        }
        this.handlePortAttributes(node, endpointConfig);
    }

    @Override
    protected void handleWanServerSocketEndpointConfig(Node node) throws Exception {
        for (Node wanEndpointNode : DomConfigHelper.childElements(node)) {
            ServerSocketEndpointConfig config = new ServerSocketEndpointConfig();
            config.setProtocolType(ProtocolType.WAN);
            String name = wanEndpointNode.getNodeName();
            this.handleServerSocketEndpointConfig(config, wanEndpointNode, name);
        }
    }

    @Override
    protected void handleWanEndpointConfig(Node node) throws Exception {
        for (Node wanEndpointNode : DomConfigHelper.childElements(node)) {
            EndpointConfig config = new EndpointConfig();
            config.setProtocolType(ProtocolType.WAN);
            String endpointName = wanEndpointNode.getNodeName().trim();
            this.handleEndpointConfig(config, wanEndpointNode, endpointName);
        }
    }

    @Override
    void handleCPSemaphores(CPSubsystemConfig cpSubsystemConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            CPSemaphoreConfig cpSemaphoreConfig = new CPSemaphoreConfig();
            cpSemaphoreConfig.setName(child.getNodeName());
            for (Node subChild : DomConfigHelper.childElements(child)) {
                String nodeName = DomConfigHelper.cleanNodeName(subChild);
                String value = this.getTextContent(subChild).trim();
                if (!"jdk-compatible".equals(nodeName)) continue;
                cpSemaphoreConfig.setJDKCompatible(Boolean.parseBoolean(value));
            }
            cpSubsystemConfig.addSemaphoreConfig(cpSemaphoreConfig);
        }
    }

    @Override
    void handleFencedLocks(CPSubsystemConfig cpSubsystemConfig, Node node) {
        for (Node child : DomConfigHelper.childElements(node)) {
            FencedLockConfig lockConfig = new FencedLockConfig();
            lockConfig.setName(child.getNodeName());
            for (Node subChild : DomConfigHelper.childElements(child)) {
                String nodeName = DomConfigHelper.cleanNodeName(subChild);
                String value = this.getTextContent(subChild).trim();
                if (!"lock-acquire-limit".equals(nodeName)) continue;
                lockConfig.setLockAcquireLimit(Integer.parseInt(value));
            }
            cpSubsystemConfig.addLockConfig(lockConfig);
        }
    }
}

