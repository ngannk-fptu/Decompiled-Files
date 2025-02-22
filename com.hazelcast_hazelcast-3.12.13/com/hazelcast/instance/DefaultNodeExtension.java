/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.hotrestart.HotRestartService;
import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.hotrestart.NoOpHotRestartService;
import com.hazelcast.hotrestart.NoopInternalHotRestartService;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.TextCommandServiceImpl;
import com.hazelcast.internal.cluster.ClusterStateListener;
import com.hazelcast.internal.cluster.ClusterVersionListener;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.VersionMismatchException;
import com.hazelcast.internal.diagnostics.BuildInfoPlugin;
import com.hazelcast.internal.diagnostics.ConfigPropertiesPlugin;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.EventQueuePlugin;
import com.hazelcast.internal.diagnostics.InvocationPlugin;
import com.hazelcast.internal.diagnostics.MemberHazelcastInstanceInfoPlugin;
import com.hazelcast.internal.diagnostics.MemberHeartbeatPlugin;
import com.hazelcast.internal.diagnostics.MetricsPlugin;
import com.hazelcast.internal.diagnostics.NetworkingImbalancePlugin;
import com.hazelcast.internal.diagnostics.OperationHeartbeatPlugin;
import com.hazelcast.internal.diagnostics.OperationThreadSamplerPlugin;
import com.hazelcast.internal.diagnostics.OverloadedConnectionsPlugin;
import com.hazelcast.internal.diagnostics.PendingInvocationsPlugin;
import com.hazelcast.internal.diagnostics.SlowOperationPlugin;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.internal.diagnostics.SystemLogPlugin;
import com.hazelcast.internal.diagnostics.SystemPropertiesPlugin;
import com.hazelcast.internal.dynamicconfig.DynamicConfigListener;
import com.hazelcast.internal.dynamicconfig.EmptyDynamicConfigListener;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.management.ManagementCenterConnectionFactory;
import com.hazelcast.internal.management.TimedMemberStateFactory;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceConstructor;
import com.hazelcast.memory.DefaultMemoryStats;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.MemberSocketInterceptor;
import com.hazelcast.nio.tcp.DefaultChannelInitializerProvider;
import com.hazelcast.nio.tcp.PacketDecoder;
import com.hazelcast.nio.tcp.PacketEncoder;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.partition.strategy.DefaultPartitioningStrategy;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.security.SecurityService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceManager;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.ByteArrayProcessor;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.PhoneHome;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.function.Supplier;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import com.hazelcast.wan.WanReplicationService;
import com.hazelcast.wan.impl.WanReplicationServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@PrivateApi
public class DefaultNodeExtension
implements NodeExtension {
    protected final Node node;
    protected final ILogger logger;
    protected final ILogger systemLogger;
    protected final List<ClusterVersionListener> clusterVersionListeners = new CopyOnWriteArrayList<ClusterVersionListener>();
    protected PhoneHome phoneHome;
    private final MemoryStats memoryStats = new DefaultMemoryStats();

    public DefaultNodeExtension(Node node) {
        this.node = node;
        this.logger = node.getLogger(NodeExtension.class);
        this.systemLogger = node.getLogger("com.hazelcast.system");
        this.checkSecurityAllowed();
        this.createAndSetPhoneHome();
    }

    private void checkSecurityAllowed() {
        SecurityConfig securityConfig = this.node.getConfig().getSecurityConfig();
        if (securityConfig != null && securityConfig.isEnabled() && !BuildInfoProvider.getBuildInfo().isEnterprise()) {
            throw new IllegalStateException("Security requires Hazelcast Enterprise Edition");
        }
        SymmetricEncryptionConfig symmetricEncryptionConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.node.getConfig()).getSymmetricEncryptionConfig();
        if (symmetricEncryptionConfig != null && symmetricEncryptionConfig.isEnabled() && !BuildInfoProvider.getBuildInfo().isEnterprise()) {
            throw new IllegalStateException("Symmetric Encryption requires Hazelcast Enterprise Edition");
        }
    }

    @Override
    public void beforeStart() {
    }

    @Override
    public void printNodeInfo() {
        BuildInfo buildInfo = this.node.getBuildInfo();
        String build = buildInfo.getBuild();
        String revision = buildInfo.getRevision();
        if (!revision.isEmpty()) {
            build = build + " - " + revision;
        }
        this.systemLogger.info("Hazelcast " + buildInfo.getVersion() + " (" + build + ") starting at " + this.node.getThisAddress());
        this.systemLogger.info("Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.");
        this.systemLogger.fine("Configured Hazelcast Serialization version: " + buildInfo.getSerializationVersion());
    }

    @Override
    public void beforeJoin() {
    }

    @Override
    public void afterStart() {
    }

    @Override
    public boolean isStartCompleted() {
        return this.node.getClusterService().isJoined();
    }

    @Override
    public SecurityContext getSecurityContext() {
        this.logger.warning("Security features are only available on Hazelcast Enterprise!");
        return null;
    }

    @Override
    public InternalSerializationService createSerializationService() {
        InternalSerializationService ss;
        try {
            Config config = this.node.getConfig();
            ClassLoader configClassLoader = this.node.getConfigClassLoader();
            HazelcastInstanceImpl hazelcastInstance = this.node.hazelcastInstance;
            PartitioningStrategy partitioningStrategy = this.getPartitioningStrategy(configClassLoader);
            DefaultSerializationServiceBuilder builder = new DefaultSerializationServiceBuilder();
            SerializationConfig serializationConfig = config.getSerializationConfig() != null ? config.getSerializationConfig() : new SerializationConfig();
            byte version = (byte)this.node.getProperties().getInteger(GroupProperty.SERIALIZATION_VERSION);
            ss = (InternalSerializationService)builder.setClassLoader(configClassLoader).setConfig(serializationConfig).setManagedContext(hazelcastInstance.managedContext).setPartitioningStrategy(partitioningStrategy).setHazelcastInstance(hazelcastInstance).setVersion(version).setNotActiveExceptionSupplier(new Supplier<RuntimeException>(){

                @Override
                public RuntimeException get() {
                    return new HazelcastInstanceNotActiveException();
                }
            }).build();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        return ss;
    }

    @Override
    public SecurityService getSecurityService() {
        return null;
    }

    protected PartitioningStrategy getPartitioningStrategy(ClassLoader configClassLoader) throws Exception {
        String partitioningStrategyClassName = this.node.getProperties().getString(GroupProperty.PARTITIONING_STRATEGY_CLASS);
        if (partitioningStrategyClassName != null && partitioningStrategyClassName.length() > 0) {
            return (PartitioningStrategy)ClassLoaderUtil.newInstance(configClassLoader, partitioningStrategyClassName);
        }
        return new DefaultPartitioningStrategy();
    }

    @Override
    public <T> T createService(Class<T> clazz) {
        if (WanReplicationService.class.isAssignableFrom(clazz)) {
            return (T)new WanReplicationServiceImpl(this.node);
        }
        if (ICacheService.class.isAssignableFrom(clazz)) {
            return (T)new CacheService();
        }
        if (MapService.class.isAssignableFrom(clazz)) {
            return this.createMapService();
        }
        throw new IllegalArgumentException("Unknown service class: " + clazz);
    }

    private <T> T createMapService() {
        ConstructorFunction<NodeEngine, MapService> constructor = MapServiceConstructor.getDefaultMapServiceConstructor();
        NodeEngineImpl nodeEngine = this.node.getNodeEngine();
        return (T)constructor.createNew(nodeEngine);
    }

    @Override
    public Map<String, Object> createExtensionServices() {
        return Collections.emptyMap();
    }

    @Override
    public MemberSocketInterceptor getSocketInterceptor(EndpointQualifier endpointQualifier) {
        this.logger.warning("SocketInterceptor feature is only available on Hazelcast Enterprise!");
        return null;
    }

    @Override
    public InboundHandler[] createInboundHandlers(EndpointQualifier qualifier, TcpIpConnection connection, IOService ioService) {
        NodeEngineImpl nodeEngine = this.node.nodeEngine;
        PacketDecoder decoder = new PacketDecoder(connection, nodeEngine.getPacketDispatcher());
        return new InboundHandler[]{decoder};
    }

    @Override
    public OutboundHandler[] createOutboundHandlers(EndpointQualifier qualifier, TcpIpConnection connection, IOService ioService) {
        return new OutboundHandler[]{new PacketEncoder()};
    }

    @Override
    public ChannelInitializerProvider createChannelInitializerProvider(IOService ioService) {
        DefaultChannelInitializerProvider provider = new DefaultChannelInitializerProvider(ioService, this.node.getConfig());
        provider.init();
        return provider;
    }

    @Override
    public void onThreadStart(Thread thread) {
    }

    @Override
    public void onThreadStop(Thread thread) {
    }

    @Override
    public MemoryStats getMemoryStats() {
        return this.memoryStats;
    }

    @Override
    public void beforeShutdown() {
    }

    @Override
    public void shutdown() {
        this.logger.info("Destroying node NodeExtension.");
        if (this.phoneHome != null) {
            this.phoneHome.shutdown();
        }
    }

    @Override
    public void validateJoinRequest(JoinMessage joinMessage) {
        MemberVersion memberVersion = joinMessage.getMemberVersion();
        Version clusterVersion = this.node.getClusterService().getClusterVersion();
        if (!memberVersion.asVersion().equals(clusterVersion)) {
            String msg = "Joining node's version " + memberVersion + " is not compatible with cluster version " + clusterVersion;
            if (clusterVersion.getMajor() != memberVersion.getMajor()) {
                msg = msg + " (Rolling Member Upgrades are only supported for the same major version)";
            }
            if (clusterVersion.getMinor() > memberVersion.getMinor()) {
                msg = msg + " (Rolling Member Upgrades are only supported for the next minor version)";
            }
            if (!BuildInfoProvider.getBuildInfo().isEnterprise()) {
                msg = msg + " (Rolling Member Upgrades are only supported in Hazelcast Enterprise)";
            }
            throw new VersionMismatchException(msg);
        }
    }

    @Override
    public void beforeClusterStateChange(ClusterState currState, ClusterState requestedState, boolean isTransient) {
    }

    @Override
    public void onClusterStateChange(ClusterState newState, boolean isTransient) {
        ServiceManager serviceManager = this.node.getNodeEngine().getServiceManager();
        List<ClusterStateListener> listeners = serviceManager.getServices(ClusterStateListener.class);
        for (ClusterStateListener listener : listeners) {
            listener.onClusterStateChange(newState);
        }
    }

    @Override
    public void afterClusterStateChange(ClusterState oldState, ClusterState newState, boolean isTransient) {
    }

    @Override
    public void onPartitionStateChange() {
        if (this.node.clientEngine.getPartitionListenerService() != null) {
            this.node.clientEngine.getPartitionListenerService().onPartitionStateChange();
        }
    }

    @Override
    public void onMemberListChange() {
    }

    @Override
    public void onInitialClusterState(ClusterState initialState) {
    }

    @Override
    public void onClusterVersionChange(Version newVersion) {
        if (!this.node.getVersion().asVersion().isEqualTo(newVersion)) {
            this.systemLogger.info("Cluster version set to " + newVersion);
        }
        ServiceManager serviceManager = this.node.getNodeEngine().getServiceManager();
        List<ClusterVersionListener> listeners = serviceManager.getServices(ClusterVersionListener.class);
        for (ClusterVersionListener listener : listeners) {
            listener.onClusterVersionChange(newVersion);
        }
        for (ClusterVersionListener listener : this.clusterVersionListeners) {
            listener.onClusterVersionChange(newVersion);
        }
    }

    @Override
    public boolean isNodeVersionCompatibleWith(Version clusterVersion) {
        Preconditions.checkNotNull(clusterVersion);
        return this.node.getVersion().asVersion().equals(clusterVersion);
    }

    @Override
    public boolean registerListener(Object listener) {
        if (listener instanceof HazelcastInstanceAware) {
            ((HazelcastInstanceAware)listener).setHazelcastInstance(this.node.hazelcastInstance);
        }
        if (listener instanceof ClusterVersionListener) {
            ClusterVersionListener clusterVersionListener = (ClusterVersionListener)listener;
            this.clusterVersionListeners.add(clusterVersionListener);
            clusterVersionListener.onClusterVersionChange(this.getClusterOrNodeVersion());
            return true;
        }
        return false;
    }

    @Override
    public HotRestartService getHotRestartService() {
        return new NoOpHotRestartService();
    }

    @Override
    public InternalHotRestartService getInternalHotRestartService() {
        return new NoopInternalHotRestartService();
    }

    @Override
    public String createMemberUuid(Address address) {
        return UuidUtil.createMemberUuid(address);
    }

    @Override
    public ByteArrayProcessor createMulticastInputProcessor(IOService ioService) {
        return null;
    }

    @Override
    public ByteArrayProcessor createMulticastOutputProcessor(IOService ioService) {
        return null;
    }

    private Version getClusterOrNodeVersion() {
        if (this.node.getClusterService() != null && !this.node.getClusterService().getClusterVersion().isUnknown()) {
            return this.node.getClusterService().getClusterVersion();
        }
        String overriddenClusterVersion = this.node.getProperties().getString(GroupProperty.INIT_CLUSTER_VERSION);
        return overriddenClusterVersion != null ? MemberVersion.of(overriddenClusterVersion).asVersion() : this.node.getVersion().asVersion();
    }

    @Override
    public TimedMemberStateFactory createTimedMemberStateFactory(HazelcastInstanceImpl instance) {
        return new TimedMemberStateFactory(instance);
    }

    @Override
    public DynamicConfigListener createDynamicConfigListener() {
        return new EmptyDynamicConfigListener();
    }

    @Override
    public void registerPlugins(Diagnostics diagnostics) {
        NodeEngineImpl nodeEngine = this.node.nodeEngine;
        diagnostics.register(new BuildInfoPlugin(nodeEngine));
        diagnostics.register(new SystemPropertiesPlugin(nodeEngine));
        diagnostics.register(new ConfigPropertiesPlugin(nodeEngine));
        diagnostics.register(new OverloadedConnectionsPlugin(nodeEngine));
        diagnostics.register(new EventQueuePlugin(nodeEngine, ((EventServiceImpl)nodeEngine.getEventService()).getEventExecutor()));
        diagnostics.register(new PendingInvocationsPlugin(nodeEngine));
        diagnostics.register(new MetricsPlugin(nodeEngine));
        diagnostics.register(new SlowOperationPlugin(nodeEngine));
        diagnostics.register(new InvocationPlugin(nodeEngine));
        diagnostics.register(new MemberHazelcastInstanceInfoPlugin(nodeEngine));
        diagnostics.register(new SystemLogPlugin(nodeEngine));
        diagnostics.register(new StoreLatencyPlugin(nodeEngine));
        diagnostics.register(new MemberHeartbeatPlugin(nodeEngine));
        diagnostics.register(new NetworkingImbalancePlugin(nodeEngine));
        diagnostics.register(new OperationHeartbeatPlugin(nodeEngine));
        diagnostics.register(new OperationThreadSamplerPlugin(nodeEngine));
    }

    @Override
    public ManagementCenterConnectionFactory getManagementCenterConnectionFactory() {
        return null;
    }

    @Override
    public ManagementService createJMXManagementService(HazelcastInstanceImpl instance) {
        return new ManagementService(instance);
    }

    @Override
    public TextCommandService createTextCommandService() {
        return new TextCommandServiceImpl(this.node);
    }

    @Override
    public void sendPhoneHome() {
        this.phoneHome.check(this.node);
    }

    @Override
    public void scheduleClusterVersionAutoUpgrade() {
    }

    @Override
    public boolean isClientFailoverSupported() {
        return false;
    }

    protected void createAndSetPhoneHome() {
        this.phoneHome = new PhoneHome(this.node);
    }

    public void setLicenseKey(String licenseKey) {
    }
}

