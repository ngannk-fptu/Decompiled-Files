/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.jmx.ClientEngineMBean;
import com.hazelcast.internal.jmx.EventServiceMBean;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagedExecutorServiceMBean;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.jmx.NetworkingServiceMBean;
import com.hazelcast.internal.jmx.NodeMBean;
import com.hazelcast.internal.jmx.OperationServiceMBean;
import com.hazelcast.internal.jmx.PartitionServiceMBean;
import com.hazelcast.internal.jmx.ProxyServiceMBean;
import com.hazelcast.internal.jmx.WanPublisherMBean;
import com.hazelcast.monitor.LocalWanPublisherStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.wan.WanReplicationService;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ManagedDescription(value="HazelcastInstance")
public class InstanceMBean
extends HazelcastMBean<HazelcastInstanceImpl> {
    private static final int INITIAL_CAPACITY = 3;
    final Config config;
    final Cluster cluster;
    private NodeMBean nodeMBean;
    private NetworkingServiceMBean networkingServiceMBean;
    private EventServiceMBean eventServiceMBean;
    private OperationServiceMBean operationServiceMBean;
    private ProxyServiceMBean proxyServiceMBean;
    private ClientEngineMBean clientEngineMBean;
    private ManagedExecutorServiceMBean systemExecutorMBean;
    private ManagedExecutorServiceMBean asyncExecutorMBean;
    private ManagedExecutorServiceMBean scheduledExecutorMBean;
    private ManagedExecutorServiceMBean clientExecutorMBean;
    private ManagedExecutorServiceMBean clientQueryExecutorMBean;
    private ManagedExecutorServiceMBean clientBlockingExecutorMBean;
    private ManagedExecutorServiceMBean queryExecutorMBean;
    private ManagedExecutorServiceMBean ioExecutorMBean;
    private ManagedExecutorServiceMBean offloadableExecutorMBean;
    private PartitionServiceMBean partitionServiceMBean;

    protected InstanceMBean(HazelcastInstanceImpl hazelcastInstance, ManagementService managementService) {
        super(hazelcastInstance, managementService);
        this.createProperties(hazelcastInstance);
        this.config = hazelcastInstance.getConfig();
        this.cluster = hazelcastInstance.getCluster();
        Node node = hazelcastInstance.node;
        InternalExecutionService executionService = node.nodeEngine.getExecutionService();
        InternalOperationService operationService = node.nodeEngine.getOperationService();
        this.createMBeans(hazelcastInstance, managementService, node, executionService, operationService);
        this.registerMBeans();
        this.registerWanPublisherMBeans(node.nodeEngine.getWanReplicationService());
    }

    private void registerWanPublisherMBeans(WanReplicationService wanReplicationService) {
        Map wanStats = wanReplicationService.getStats();
        if (wanStats == null) {
            return;
        }
        for (Map.Entry replicationStatsEntry : wanStats.entrySet()) {
            String wanReplicationName = replicationStatsEntry.getKey();
            LocalWanStats localWanStats = (LocalWanStats)replicationStatsEntry.getValue();
            Map<String, LocalWanPublisherStats> publisherStats = localWanStats.getLocalWanPublisherStats();
            for (String targetGroupName : publisherStats.keySet()) {
                InstanceMBean.register(new WanPublisherMBean(wanReplicationService, wanReplicationName, targetGroupName, this.service));
            }
        }
    }

    private void createMBeans(HazelcastInstanceImpl hazelcastInstance, ManagementService managementService, Node node, ExecutionService executionService, InternalOperationService operationService) {
        this.nodeMBean = new NodeMBean(hazelcastInstance, node, managementService);
        this.networkingServiceMBean = new NetworkingServiceMBean(hazelcastInstance, node.networkingService, this.service);
        this.eventServiceMBean = new EventServiceMBean(hazelcastInstance, node.nodeEngine.getEventService(), this.service);
        this.operationServiceMBean = new OperationServiceMBean(hazelcastInstance, operationService, this.service);
        this.proxyServiceMBean = new ProxyServiceMBean(hazelcastInstance, node.nodeEngine.getProxyService(), this.service);
        this.partitionServiceMBean = new PartitionServiceMBean(hazelcastInstance, node.partitionService, this.service);
        this.clientEngineMBean = new ClientEngineMBean(hazelcastInstance, node.clientEngine, this.service);
        this.systemExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:system"), this.service);
        this.asyncExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:async"), this.service);
        this.scheduledExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:scheduled"), this.service);
        this.clientExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:client"), this.service);
        this.clientQueryExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:client-query"), this.service);
        this.clientBlockingExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:client-blocking-tasks"), this.service);
        this.queryExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:query"), this.service);
        this.ioExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:io"), this.service);
        this.offloadableExecutorMBean = new ManagedExecutorServiceMBean(hazelcastInstance, executionService.getExecutor("hz:offloadable"), this.service);
    }

    private void registerMBeans() {
        InstanceMBean.register(this.operationServiceMBean);
        InstanceMBean.register(this.nodeMBean);
        InstanceMBean.register(this.networkingServiceMBean);
        InstanceMBean.register(this.eventServiceMBean);
        InstanceMBean.register(this.proxyServiceMBean);
        InstanceMBean.register(this.partitionServiceMBean);
        InstanceMBean.register(this.clientEngineMBean);
        InstanceMBean.register(this.systemExecutorMBean);
        InstanceMBean.register(this.asyncExecutorMBean);
        InstanceMBean.register(this.scheduledExecutorMBean);
        InstanceMBean.register(this.clientExecutorMBean);
        InstanceMBean.register(this.clientQueryExecutorMBean);
        InstanceMBean.register(this.clientBlockingExecutorMBean);
        InstanceMBean.register(this.queryExecutorMBean);
        InstanceMBean.register(this.ioExecutorMBean);
        InstanceMBean.register(this.offloadableExecutorMBean);
    }

    private void createProperties(HazelcastInstanceImpl hazelcastInstance) {
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance"));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        properties.put("name", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    public PartitionServiceMBean getPartitionServiceMBean() {
        return this.partitionServiceMBean;
    }

    public ManagedExecutorServiceMBean getSystemExecutorMBean() {
        return this.systemExecutorMBean;
    }

    public ManagedExecutorServiceMBean getAsyncExecutorMBean() {
        return this.asyncExecutorMBean;
    }

    public ManagedExecutorServiceMBean getScheduledExecutorMBean() {
        return this.scheduledExecutorMBean;
    }

    public ManagedExecutorServiceMBean getClientExecutorMBean() {
        return this.clientExecutorMBean;
    }

    public ManagedExecutorServiceMBean getClientQueryExecutorMBean() {
        return this.clientQueryExecutorMBean;
    }

    public ManagedExecutorServiceMBean getClientBlockingExecutorMBean() {
        return this.clientBlockingExecutorMBean;
    }

    public ManagedExecutorServiceMBean getQueryExecutorMBean() {
        return this.queryExecutorMBean;
    }

    public ManagedExecutorServiceMBean getIoExecutorMBean() {
        return this.ioExecutorMBean;
    }

    public ManagedExecutorServiceMBean getOffloadableExecutorMBean() {
        return this.offloadableExecutorMBean;
    }

    public OperationServiceMBean getOperationServiceMBean() {
        return this.operationServiceMBean;
    }

    public ProxyServiceMBean getProxyServiceMBean() {
        return this.proxyServiceMBean;
    }

    public ClientEngineMBean getClientEngineMBean() {
        return this.clientEngineMBean;
    }

    public NetworkingServiceMBean getNetworkingServiceMBean() {
        return this.networkingServiceMBean;
    }

    public EventServiceMBean getEventServiceMBean() {
        return this.eventServiceMBean;
    }

    public NodeMBean getNodeMBean() {
        return this.nodeMBean;
    }

    public HazelcastInstance getHazelcastInstance() {
        return (HazelcastInstance)this.managedObject;
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the Instance")
    public String getName() {
        return ((HazelcastInstanceImpl)this.managedObject).getName();
    }

    @ManagedAnnotation(value="version")
    @ManagedDescription(value="The Hazelcast version")
    public String getVersion() {
        return ((HazelcastInstanceImpl)this.managedObject).node.getBuildInfo().getVersion();
    }

    @ManagedAnnotation(value="build")
    @ManagedDescription(value="The Hazelcast build")
    public String getBuild() {
        return ((HazelcastInstanceImpl)this.managedObject).node.getBuildInfo().getBuild();
    }

    @ManagedAnnotation(value="config")
    @ManagedDescription(value="String representation of config")
    public String getConfig() {
        return this.config.toString();
    }

    @ManagedAnnotation(value="configSource")
    @ManagedDescription(value="The source of config")
    public String getConfigSource() {
        File configurationFile = this.config.getConfigurationFile();
        if (configurationFile != null) {
            return configurationFile.getAbsolutePath();
        }
        URL configurationUrl = this.config.getConfigurationUrl();
        if (configurationUrl != null) {
            return configurationUrl.toString();
        }
        return null;
    }

    @ManagedAnnotation(value="groupName")
    @ManagedDescription(value="Group Name")
    public String getGroupName() {
        return this.config.getGroupConfig().getName();
    }

    @ManagedAnnotation(value="port")
    @ManagedDescription(value="Network Port")
    public int getPort() {
        return ConfigAccessor.getActiveMemberNetworkConfig(this.config).getPort();
    }

    @ManagedAnnotation(value="clusterTime")
    @ManagedDescription(value="Cluster-wide Time")
    public long getClusterTime() {
        return this.cluster.getClusterTime();
    }

    @ManagedAnnotation(value="memberCount")
    @ManagedDescription(value="size of the cluster")
    public int getMemberCount() {
        return this.cluster.getMembers().size();
    }

    @ManagedAnnotation(value="Members")
    @ManagedDescription(value="List of Members")
    public List<String> getMembers() {
        Set<Member> members = this.cluster.getMembers();
        ArrayList<String> list = new ArrayList<String>(members.size());
        for (Member member : members) {
            list.add(member.getSocketAddress().toString());
        }
        return list;
    }

    @ManagedAnnotation(value="running")
    @ManagedDescription(value="Running state")
    public boolean isRunning() {
        return ((HazelcastInstanceImpl)this.managedObject).getLifecycleService().isRunning();
    }

    @ManagedAnnotation(value="shutdown", operation=true)
    @ManagedDescription(value="Shutdown the Node")
    public void shutdown() {
        ((HazelcastInstanceImpl)this.managedObject).getLifecycleService().shutdown();
    }
}

