/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.spi.impl;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;
import com.hazelcast.internal.dynamicconfig.DynamicConfigListener;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.metricsets.ClassLoadingMetricSet;
import com.hazelcast.internal.metrics.metricsets.FileMetricSet;
import com.hazelcast.internal.metrics.metricsets.GarbageCollectionMetricSet;
import com.hazelcast.internal.metrics.metricsets.OperatingSystemMetricSet;
import com.hazelcast.internal.metrics.metricsets.RuntimeMetricSet;
import com.hazelcast.internal.metrics.metricsets.StatisticsAwareMetricsSet;
import com.hazelcast.internal.metrics.metricsets.ThreadMetricSet;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentClassLoader;
import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.logging.LoggingServiceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.impl.QuorumServiceImpl;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.spi.SharedService;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.ServiceNotFoundException;
import com.hazelcast.spi.impl.PacketDispatcher;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.executionservice.impl.ExecutionServiceImpl;
import com.hazelcast.spi.impl.operationparker.OperationParker;
import com.hazelcast.spi.impl.operationparker.impl.OperationParkerImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyServiceImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.spi.impl.servicemanager.ServiceManager;
import com.hazelcast.spi.impl.servicemanager.impl.ServiceManagerImpl;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.function.Consumer;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.wan.WanReplicationService;
import java.util.Collection;
import java.util.LinkedList;
import javax.annotation.Nonnull;

public class NodeEngineImpl
implements NodeEngine {
    private static final String JET_SERVICE_NAME = "hz:impl:jetService";
    private final Node node;
    private final SerializationService serializationService;
    private final LoggingServiceImpl loggingService;
    private final ILogger logger;
    private final MetricsRegistryImpl metricsRegistry;
    private final ProxyServiceImpl proxyService;
    private final ServiceManagerImpl serviceManager;
    private final ExecutionServiceImpl executionService;
    private final OperationServiceImpl operationService;
    private final EventServiceImpl eventService;
    private final OperationParkerImpl operationParker;
    private final ClusterWideConfigurationService configurationService;
    private final TransactionManagerServiceImpl transactionManagerService;
    private final WanReplicationService wanReplicationService;
    private final Consumer<Packet> packetDispatcher;
    private final QuorumServiceImpl quorumService;
    private final Diagnostics diagnostics;
    private final SplitBrainMergePolicyProvider splitBrainMergePolicyProvider;

    public NodeEngineImpl(Node node) {
        this.node = node;
        try {
            this.serializationService = node.getSerializationService();
            this.loggingService = node.loggingService;
            this.logger = node.getLogger(NodeEngine.class.getName());
            this.metricsRegistry = this.newMetricRegistry(node);
            this.proxyService = new ProxyServiceImpl(this);
            this.serviceManager = new ServiceManagerImpl(this);
            this.executionService = new ExecutionServiceImpl(this);
            this.operationService = new OperationServiceImpl(this);
            this.eventService = new EventServiceImpl(this);
            this.operationParker = new OperationParkerImpl(this);
            UserCodeDeploymentService userCodeDeploymentService = new UserCodeDeploymentService();
            DynamicConfigListener dynamicConfigListener = node.getNodeExtension().createDynamicConfigListener();
            this.configurationService = new ClusterWideConfigurationService(this, dynamicConfigListener);
            ClassLoader configClassLoader = node.getConfigClassLoader();
            if (configClassLoader instanceof UserCodeDeploymentClassLoader) {
                ((UserCodeDeploymentClassLoader)configClassLoader).setUserCodeDeploymentService(userCodeDeploymentService);
            }
            this.transactionManagerService = new TransactionManagerServiceImpl(this);
            this.wanReplicationService = node.getNodeExtension().createService(WanReplicationService.class);
            this.packetDispatcher = new PacketDispatcher(this.logger, this.operationService.getOperationExecutor(), (Consumer<Packet>)this.operationService.getInboundResponseHandlerSupplier().get(), this.operationService.getInvocationMonitor(), this.eventService, this.getJetPacketConsumer(node.getNodeExtension()));
            this.quorumService = new QuorumServiceImpl(this);
            this.diagnostics = this.newDiagnostics();
            this.splitBrainMergePolicyProvider = new SplitBrainMergePolicyProvider(this);
            this.serviceManager.registerService("hz:impl:operationService", this.operationService);
            this.serviceManager.registerService("hz:impl:operationParker", this.operationParker);
            this.serviceManager.registerService("user-code-deployment-service", userCodeDeploymentService);
            this.serviceManager.registerService("configuration-service", this.configurationService);
        }
        catch (Throwable e) {
            try {
                this.shutdown(true);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
            throw ExceptionUtil.rethrow(e);
        }
    }

    private MetricsRegistryImpl newMetricRegistry(Node node) {
        ProbeLevel probeLevel = node.getProperties().getEnum(Diagnostics.METRICS_LEVEL, ProbeLevel.class);
        return new MetricsRegistryImpl(this.getHazelcastInstance().getName(), node.getLogger(MetricsRegistry.class), probeLevel);
    }

    private Diagnostics newDiagnostics() {
        Address address = this.node.getThisAddress();
        String addressString = address.getHost().replace(":", "_") + "_" + address.getPort();
        String name = "diagnostics-" + addressString + "-" + System.currentTimeMillis();
        return new Diagnostics(name, this.loggingService.getLogger(Diagnostics.class), this.getHazelcastInstance().getName(), this.node.getProperties());
    }

    public LoggingService getLoggingService() {
        return this.loggingService;
    }

    public MetricsRegistry getMetricsRegistry() {
        return this.metricsRegistry;
    }

    public void start() {
        RuntimeMetricSet.register(this.metricsRegistry);
        GarbageCollectionMetricSet.register(this.metricsRegistry);
        OperatingSystemMetricSet.register(this.metricsRegistry);
        ThreadMetricSet.register(this.metricsRegistry);
        ClassLoadingMetricSet.register(this.metricsRegistry);
        FileMetricSet.register(this.metricsRegistry);
        if (this.node.getProperties().getBoolean(Diagnostics.METRICS_DISTRIBUTED_DATASTRUCTURES)) {
            new StatisticsAwareMetricsSet(this.serviceManager, this).register(this.metricsRegistry);
        }
        this.metricsRegistry.scanAndRegister(this.node.getNodeExtension().getMemoryStats(), "memory");
        this.metricsRegistry.collectMetrics(this.operationService, this.proxyService, this.eventService, this.operationParker);
        this.serviceManager.start();
        this.proxyService.init();
        this.operationService.start();
        this.quorumService.start();
        this.diagnostics.start();
        this.node.getNodeExtension().registerPlugins(this.diagnostics);
    }

    public Consumer<Packet> getPacketDispatcher() {
        return this.packetDispatcher;
    }

    public Diagnostics getDiagnostics() {
        return this.diagnostics;
    }

    public ClusterWideConfigurationService getConfigurationService() {
        return this.configurationService;
    }

    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public Address getThisAddress() {
        return this.node.getThisAddress();
    }

    @Override
    public Address getMasterAddress() {
        return this.node.getMasterAddress();
    }

    @Override
    public MemberImpl getLocalMember() {
        return this.node.getLocalMember();
    }

    @Override
    public Config getConfig() {
        return this.node.getConfig();
    }

    @Override
    public ClassLoader getConfigClassLoader() {
        return this.node.getConfigClassLoader();
    }

    @Override
    public InternalEventService getEventService() {
        return this.eventService;
    }

    @Override
    public SerializationService getSerializationService() {
        return this.serializationService;
    }

    @Override
    public InternalOperationService getOperationService() {
        return this.operationService;
    }

    @Override
    public InternalExecutionService getExecutionService() {
        return this.executionService;
    }

    @Override
    public InternalPartitionService getPartitionService() {
        return this.node.getPartitionService();
    }

    @Override
    public ClusterService getClusterService() {
        return this.node.getClusterService();
    }

    public ManagementCenterService getManagementCenterService() {
        return this.node.getManagementCenterService();
    }

    @Override
    public InternalProxyService getProxyService() {
        return this.proxyService;
    }

    public OperationParker getOperationParker() {
        return this.operationParker;
    }

    @Override
    public WanReplicationService getWanReplicationService() {
        return this.wanReplicationService;
    }

    @Override
    public QuorumServiceImpl getQuorumService() {
        return this.quorumService;
    }

    @Override
    public TransactionManagerService getTransactionManagerService() {
        return this.transactionManagerService;
    }

    @Override
    public Data toData(Object object) {
        return this.serializationService.toData(object);
    }

    @Override
    public <T> T toObject(Object object) {
        return this.serializationService.toObject(object);
    }

    @Override
    public <T> T toObject(Object object, Class klazz) {
        return this.serializationService.toObject(object, klazz);
    }

    @Override
    public boolean isActive() {
        return this.isRunning();
    }

    @Override
    public boolean isRunning() {
        return this.node.isRunning();
    }

    @Override
    public HazelcastInstance getHazelcastInstance() {
        return this.node.hazelcastInstance;
    }

    @Override
    public ILogger getLogger(String name) {
        return this.loggingService.getLogger(name);
    }

    @Override
    public ILogger getLogger(Class clazz) {
        return this.loggingService.getLogger(clazz);
    }

    @Override
    public HazelcastProperties getProperties() {
        return this.node.getProperties();
    }

    @Override
    public <T> T getService(String serviceName) {
        Object service = this.serviceManager.getService(serviceName);
        if (service == null) {
            if (this.isRunning()) {
                throw new HazelcastException("Service with name '" + serviceName + "' not found!", new ServiceNotFoundException("Service with name '" + serviceName + "' not found!"));
            }
            throw new RetryableHazelcastException("HazelcastInstance[" + this.getThisAddress() + "] is not active!");
        }
        return service;
    }

    @Override
    public <T extends SharedService> T getSharedService(String serviceName) {
        return this.serviceManager.getSharedService(serviceName);
    }

    @Override
    public MemberVersion getVersion() {
        return this.node.getVersion();
    }

    @Override
    public SplitBrainMergePolicyProvider getSplitBrainMergePolicyProvider() {
        return this.splitBrainMergePolicyProvider;
    }

    @Override
    public <S> Collection<S> getServices(Class<S> serviceClass) {
        return this.serviceManager.getServices(serviceClass);
    }

    public Collection<ServiceInfo> getServiceInfos(Class serviceClass) {
        return this.serviceManager.getServiceInfos(serviceClass);
    }

    public Node getNode() {
        return this.node;
    }

    public void onMemberLeft(MemberImpl member) {
        this.operationParker.onMemberLeft(member);
        this.operationService.onMemberLeft(member);
        this.eventService.onMemberLeft(member);
    }

    public void onClientDisconnected(String clientUuid) {
        this.operationParker.onClientDisconnected(clientUuid);
    }

    public void onPartitionMigrate(MigrationInfo migrationInfo) {
        this.operationParker.onPartitionMigrate(migrationInfo);
    }

    public Operation[] getPostJoinOperations() {
        LinkedList<Operation> postJoinOps = new LinkedList<Operation>();
        Collection<PostJoinAwareService> services = this.getServices(PostJoinAwareService.class);
        for (PostJoinAwareService service : services) {
            Operation postJoinOperation = service.getPostJoinOperation();
            if (postJoinOperation == null) continue;
            if (postJoinOperation.getPartitionId() >= 0) {
                this.logger.severe("Post-join operations should not have partition ID set! Service: " + service + ", Operation: " + postJoinOperation);
                continue;
            }
            postJoinOps.add(postJoinOperation);
        }
        return postJoinOps.isEmpty() ? null : postJoinOps.toArray(new Operation[0]);
    }

    public Operation[] getPreJoinOperations() {
        LinkedList<Operation> preJoinOps = new LinkedList<Operation>();
        Collection<PreJoinAwareService> services = this.getServices(PreJoinAwareService.class);
        for (PreJoinAwareService service : services) {
            Operation preJoinOperation = service.getPreJoinOperation();
            if (preJoinOperation == null) continue;
            if (preJoinOperation.getPartitionId() >= 0) {
                this.logger.severe("Pre-join operations operations should not have partition ID set! Service: " + service + ", Operation: " + preJoinOperation);
                continue;
            }
            preJoinOps.add(preJoinOperation);
        }
        return preJoinOps.isEmpty() ? null : preJoinOps.toArray(new Operation[0]);
    }

    public void reset() {
        this.operationParker.reset();
        this.operationService.reset();
    }

    public void shutdown(boolean terminate) {
        this.logger.finest("Shutting down services...");
        if (this.operationParker != null) {
            this.operationParker.shutdown();
        }
        if (this.operationService != null) {
            this.operationService.shutdownInvocations();
        }
        if (this.proxyService != null) {
            this.proxyService.shutdown();
        }
        if (this.serviceManager != null) {
            this.serviceManager.shutdown(terminate);
        }
        if (this.eventService != null) {
            this.eventService.shutdown();
        }
        if (this.operationService != null) {
            this.operationService.shutdownOperationExecutor();
        }
        if (this.wanReplicationService != null) {
            this.wanReplicationService.shutdown();
        }
        if (this.executionService != null) {
            this.executionService.shutdown();
        }
        if (this.metricsRegistry != null) {
            this.metricsRegistry.shutdown();
        }
        if (this.diagnostics != null) {
            this.diagnostics.shutdown();
        }
    }

    @Nonnull
    private Consumer<Packet> getJetPacketConsumer(NodeExtension nodeExtension) {
        if (nodeExtension instanceof JetPacketConsumer) {
            return (JetPacketConsumer)((Object)nodeExtension);
        }
        return new JetPacketConsumer(){

            @Override
            public void accept(Packet packet) {
                throw new UnsupportedOperationException("Jet is not registered on this node");
            }
        };
    }

    public static interface JetPacketConsumer
    extends Consumer<Packet> {
    }
}

