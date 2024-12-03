/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SocketInterceptorConfig;
import com.hazelcast.core.Client;
import com.hazelcast.core.Member;
import com.hazelcast.cp.CPMember;
import com.hazelcast.crdt.pncounter.PNCounterService;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorService;
import com.hazelcast.hotrestart.HotRestartService;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.management.TimedMemberState;
import com.hazelcast.internal.management.TimedMemberStateFactoryHelper;
import com.hazelcast.internal.management.dto.AdvancedNetworkStatsDTO;
import com.hazelcast.internal.management.dto.ClientEndPointDTO;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.LocalMemoryStats;
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.monitor.LocalOperationStats;
import com.hazelcast.monitor.LocalPNCounterStats;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.monitor.impl.HotRestartStateImpl;
import com.hazelcast.monitor.impl.LocalCacheStatsImpl;
import com.hazelcast.monitor.impl.LocalMemoryStatsImpl;
import com.hazelcast.monitor.impl.LocalOperationStatsImpl;
import com.hazelcast.monitor.impl.MemberPartitionStateImpl;
import com.hazelcast.monitor.impl.MemberStateImpl;
import com.hazelcast.monitor.impl.NodeStateImpl;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.topic.impl.TopicService;
import com.hazelcast.topic.impl.reliable.ReliableTopicService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.SetUtil;
import com.hazelcast.wan.WanReplicationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TimedMemberStateFactory {
    private static final int INITIAL_PARTITION_SAFETY_CHECK_DELAY = 15;
    private static final int PARTITION_SAFETY_CHECK_PERIOD = 60;
    protected final HazelcastInstanceImpl instance;
    private final boolean cacheServiceEnabled;
    private volatile boolean memberStateSafe = true;

    public TimedMemberStateFactory(HazelcastInstanceImpl instance) {
        this.instance = instance;
        if (instance.node.getProperties().get("hazelcast.mc.max.visible.instance.count") != null) {
            instance.node.loggingService.getLogger(this.getClass()).warning("hazelcast.mc.max.visible.instance.count property is removed.");
        }
        this.cacheServiceEnabled = this.isCacheServiceEnabled();
    }

    private boolean isCacheServiceEnabled() {
        NodeEngineImpl nodeEngine = this.instance.node.nodeEngine;
        Collection<ServiceInfo> serviceInfos = nodeEngine.getServiceInfos(CacheService.class);
        return !serviceInfos.isEmpty();
    }

    public void init() {
        this.instance.node.nodeEngine.getExecutionService().scheduleWithRepetition(new Runnable(){

            @Override
            public void run() {
                TimedMemberStateFactory.this.memberStateSafe = TimedMemberStateFactory.this.instance.getPartitionService().isLocalMemberSafe();
            }
        }, 15L, 60L, TimeUnit.SECONDS);
    }

    public TimedMemberState createTimedMemberState() {
        MemberStateImpl memberState = new MemberStateImpl();
        Collection<StatisticsAwareService> services = this.instance.node.nodeEngine.getServices(StatisticsAwareService.class);
        TimedMemberState timedMemberState = new TimedMemberState();
        this.createMemberState(memberState, services);
        timedMemberState.setMaster(this.instance.node.isMaster());
        timedMemberState.setMemberList(new ArrayList<String>());
        Set<Member> memberSet = this.instance.getCluster().getMembers();
        for (Member member : memberSet) {
            MemberImpl memberImpl = (MemberImpl)member;
            Address address = memberImpl.getAddress();
            timedMemberState.getMemberList().add(address.getHost() + ":" + address.getPort());
        }
        timedMemberState.setMemberState(memberState);
        GroupConfig groupConfig = this.instance.getConfig().getGroupConfig();
        timedMemberState.setClusterName(groupConfig.getName());
        SSLConfig sslConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.instance.getConfig()).getSSLConfig();
        timedMemberState.setSslEnabled(sslConfig != null && sslConfig.isEnabled());
        timedMemberState.setLite(this.instance.node.isLiteMember());
        SocketInterceptorConfig interceptorConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.instance.getConfig()).getSocketInterceptorConfig();
        timedMemberState.setSocketInterceptorEnabled(interceptorConfig != null && interceptorConfig.isEnabled());
        ManagementCenterConfig managementCenterConfig = this.instance.node.getConfig().getManagementCenterConfig();
        timedMemberState.setScriptingEnabled(managementCenterConfig.isScriptingEnabled());
        return timedMemberState;
    }

    protected LocalMemoryStats getMemoryStats() {
        return new LocalMemoryStatsImpl(this.instance.getMemoryStats());
    }

    protected LocalOperationStats getOperationStats() {
        return new LocalOperationStatsImpl(this.instance.node);
    }

    private void createMemberState(MemberStateImpl memberState, Collection<StatisticsAwareService> services) {
        Node node = this.instance.node;
        Collection<Client> clients = this.instance.node.clientEngine.getClients();
        Set<ClientEndPointDTO> serializableClientEndPoints = SetUtil.createHashSet(clients.size());
        for (Client client : clients) {
            serializableClientEndPoints.add(new ClientEndPointDTO(client));
        }
        memberState.setClients(serializableClientEndPoints);
        memberState.setUuid(node.getThisUuid());
        memberState.setCpMemberUuid(this.getLocalCPMemberUuidSafely());
        Address thisAddress = node.getThisAddress();
        memberState.setAddress(thisAddress.getHost() + ":" + thisAddress.getPort());
        memberState.setEndpoints(node.getLocalMember().getAddressMap());
        TimedMemberStateFactoryHelper.registerJMXBeans(this.instance, memberState);
        MemberPartitionStateImpl memberPartitionState = (MemberPartitionStateImpl)memberState.getMemberPartitionState();
        InternalPartitionService partitionService = node.getPartitionService();
        IPartition[] partitions = partitionService.getPartitions();
        List<Integer> partitionList = memberPartitionState.getPartitions();
        for (IPartition partition : partitions) {
            if (!partition.isLocal()) continue;
            partitionList.add(partition.getPartitionId());
        }
        memberPartitionState.setMigrationQueueSize(partitionService.getMigrationQueueSize());
        memberPartitionState.setMemberStateSafe(this.memberStateSafe);
        memberState.setLocalMemoryStats(this.getMemoryStats());
        memberState.setOperationStats(this.getOperationStats());
        TimedMemberStateFactoryHelper.createRuntimeProps(memberState);
        this.createMemState(memberState, services);
        this.createNodeState(memberState);
        this.createHotRestartState(memberState);
        this.createClusterHotRestartStatus(memberState);
        this.createWanSyncState(memberState);
        memberState.setClientStats(node.clientEngine.getClientStatistics());
        AggregateEndpointManager aggregateEndpointManager = node.getNetworkingService().getAggregateEndpointManager();
        memberState.setInboundNetworkStats(new AdvancedNetworkStatsDTO(aggregateEndpointManager.getInboundNetworkStats()));
        memberState.setOutboundNetworkStats(new AdvancedNetworkStatsDTO(aggregateEndpointManager.getOutboundNetworkStats()));
    }

    private String getLocalCPMemberUuidSafely() {
        if (this.instance.getConfig().getCPSubsystemConfig().getCPMemberCount() == 0) {
            return null;
        }
        try {
            CPMember localCPMember = this.instance.getCPSubsystem().getLocalCPMember();
            return localCPMember != null ? localCPMember.getUuid() : null;
        }
        catch (UnsupportedOperationException e) {
            EmptyStatement.ignore(e);
            return null;
        }
    }

    private void createHotRestartState(MemberStateImpl memberState) {
        HotRestartService hotRestartService = this.instance.node.getNodeExtension().getHotRestartService();
        boolean hotBackupEnabled = hotRestartService.isHotBackupEnabled();
        String hotBackupDirectory = hotRestartService.getBackupDirectory();
        HotRestartStateImpl state = new HotRestartStateImpl(hotRestartService.getBackupTaskStatus(), hotBackupEnabled, hotBackupDirectory);
        memberState.setHotRestartState(state);
    }

    private void createClusterHotRestartStatus(MemberStateImpl memberState) {
        ClusterHotRestartStatusDTO state = this.instance.node.getNodeExtension().getInternalHotRestartService().getCurrentClusterHotRestartStatus();
        memberState.setClusterHotRestartStatus(state);
    }

    protected void createNodeState(MemberStateImpl memberState) {
        Node node = this.instance.node;
        ClusterServiceImpl cluster = this.instance.node.clusterService;
        NodeStateImpl nodeState = new NodeStateImpl(cluster.getClusterState(), node.getState(), cluster.getClusterVersion(), node.getVersion());
        memberState.setNodeState(nodeState);
    }

    private void createWanSyncState(MemberStateImpl memberState) {
        WanReplicationService wanReplicationService = this.instance.node.nodeEngine.getWanReplicationService();
        WanSyncState wanSyncState = wanReplicationService.getWanSyncState();
        if (wanSyncState != null) {
            memberState.setWanSyncState(wanSyncState);
        }
    }

    private void createMemState(MemberStateImpl memberState, Collection<StatisticsAwareService> services) {
        int count = 0;
        Config config = this.instance.getConfig();
        for (StatisticsAwareService service : services) {
            if (service instanceof MapService) {
                count = this.handleMap(memberState, count, config, ((MapService)service).getStats());
                continue;
            }
            if (service instanceof MultiMapService) {
                count = this.handleMultimap(memberState, count, config, ((MultiMapService)service).getStats());
                continue;
            }
            if (service instanceof QueueService) {
                count = this.handleQueue(memberState, count, config, ((QueueService)service).getStats());
                continue;
            }
            if (service instanceof TopicService) {
                count = this.handleTopic(memberState, count, config, ((TopicService)service).getStats());
                continue;
            }
            if (service instanceof ReliableTopicService) {
                count = this.handleReliableTopic(memberState, count, config, ((ReliableTopicService)service).getStats());
                continue;
            }
            if (service instanceof DistributedExecutorService) {
                count = this.handleExecutorService(memberState, count, config, ((DistributedExecutorService)service).getStats());
                continue;
            }
            if (service instanceof ReplicatedMapService) {
                count = this.handleReplicatedMap(memberState, count, config, ((ReplicatedMapService)service).getStats());
                continue;
            }
            if (service instanceof PNCounterService) {
                count = this.handlePNCounter(memberState, count, config, ((PNCounterService)service).getStats());
                continue;
            }
            if (!(service instanceof FlakeIdGeneratorService)) continue;
            count = this.handleFlakeIdGenerator(memberState, count, config, ((FlakeIdGeneratorService)service).getStats());
        }
        WanReplicationService wanReplicationService = this.instance.node.nodeEngine.getWanReplicationService();
        Map<String, LocalWanStats> wanStats = wanReplicationService.getStats();
        if (wanStats != null) {
            count = this.handleWan(memberState, count, wanStats);
        }
        if (this.cacheServiceEnabled) {
            ICacheService cacheService = this.getCacheService();
            for (CacheConfig cacheConfig : cacheService.getCacheConfigs()) {
                CacheStatistics statistics;
                if (!cacheConfig.isStatisticsEnabled() || (statistics = cacheService.getStatistics(cacheConfig.getNameWithPrefix())) == null) continue;
                count = this.handleCache(memberState, count, cacheConfig, statistics);
            }
        }
    }

    private int handleFlakeIdGenerator(MemberStateImpl memberState, int count, Config config, Map<String, LocalFlakeIdGeneratorStats> flakeIdstats) {
        for (Map.Entry<String, LocalFlakeIdGeneratorStats> entry : flakeIdstats.entrySet()) {
            String name = entry.getKey();
            if (!config.findFlakeIdGeneratorConfig(name).isStatisticsEnabled()) continue;
            LocalFlakeIdGeneratorStats stats = entry.getValue();
            memberState.putLocalFlakeIdStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleExecutorService(MemberStateImpl memberState, int count, Config config, Map<String, LocalExecutorStats> executorServices) {
        for (Map.Entry<String, LocalExecutorStats> entry : executorServices.entrySet()) {
            String name = entry.getKey();
            if (!config.findExecutorConfig(name).isStatisticsEnabled()) continue;
            LocalExecutorStats stats = entry.getValue();
            memberState.putLocalExecutorStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleMultimap(MemberStateImpl memberState, int count, Config config, Map<String, LocalMultiMapStats> multiMaps) {
        for (Map.Entry<String, LocalMultiMapStats> entry : multiMaps.entrySet()) {
            String name = entry.getKey();
            if (!config.findMultiMapConfig(name).isStatisticsEnabled()) continue;
            LocalMultiMapStats stats = entry.getValue();
            memberState.putLocalMultiMapStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleReplicatedMap(MemberStateImpl memberState, int count, Config config, Map<String, LocalReplicatedMapStats> replicatedMaps) {
        for (Map.Entry<String, LocalReplicatedMapStats> entry : replicatedMaps.entrySet()) {
            String name = entry.getKey();
            if (!config.findReplicatedMapConfig(name).isStatisticsEnabled()) continue;
            LocalReplicatedMapStats stats = entry.getValue();
            memberState.putLocalReplicatedMapStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handlePNCounter(MemberStateImpl memberState, int count, Config config, Map<String, LocalPNCounterStats> counters) {
        for (Map.Entry<String, LocalPNCounterStats> entry : counters.entrySet()) {
            String name = entry.getKey();
            if (!config.findPNCounterConfig(name).isStatisticsEnabled()) continue;
            LocalPNCounterStats stats = entry.getValue();
            memberState.putLocalPNCounterStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleReliableTopic(MemberStateImpl memberState, int count, Config config, Map<String, LocalTopicStats> topics) {
        for (Map.Entry<String, LocalTopicStats> entry : topics.entrySet()) {
            String name = entry.getKey();
            if (!config.findReliableTopicConfig(name).isStatisticsEnabled()) continue;
            LocalTopicStats stats = entry.getValue();
            memberState.putLocalReliableTopicStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleTopic(MemberStateImpl memberState, int count, Config config, Map<String, LocalTopicStats> topics) {
        for (Map.Entry<String, LocalTopicStats> entry : topics.entrySet()) {
            String name = entry.getKey();
            if (!config.findTopicConfig(name).isStatisticsEnabled()) continue;
            LocalTopicStats stats = entry.getValue();
            memberState.putLocalTopicStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleQueue(MemberStateImpl memberState, int count, Config config, Map<String, LocalQueueStats> queues) {
        for (Map.Entry<String, LocalQueueStats> entry : queues.entrySet()) {
            String name = entry.getKey();
            if (!config.findQueueConfig(name).isStatisticsEnabled()) continue;
            LocalQueueStats stats = entry.getValue();
            memberState.putLocalQueueStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleMap(MemberStateImpl memberState, int count, Config config, Map<String, LocalMapStats> maps) {
        for (Map.Entry<String, LocalMapStats> entry : maps.entrySet()) {
            String name = entry.getKey();
            if (!config.findMapConfig(name).isStatisticsEnabled()) continue;
            LocalMapStats stats = entry.getValue();
            memberState.putLocalMapStats(name, stats);
            ++count;
        }
        return count;
    }

    private int handleWan(MemberStateImpl memberState, int count, Map<String, LocalWanStats> wans) {
        for (Map.Entry<String, LocalWanStats> entry : wans.entrySet()) {
            String schemeName = entry.getKey();
            LocalWanStats stats = entry.getValue();
            memberState.putLocalWanStats(schemeName, stats);
            ++count;
        }
        return count;
    }

    private int handleCache(MemberStateImpl memberState, int count, CacheConfig config, CacheStatistics cacheStatistics) {
        memberState.putLocalCacheStats(config.getNameWithPrefix(), new LocalCacheStatsImpl(cacheStatistics));
        return ++count;
    }

    private ICacheService getCacheService() {
        return (ICacheService)this.instance.node.nodeEngine.getService("hz:impl:cacheService");
    }
}

