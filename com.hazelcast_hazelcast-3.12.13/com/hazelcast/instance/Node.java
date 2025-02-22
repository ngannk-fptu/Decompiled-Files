/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.ClientEngineImpl;
import com.hazelcast.client.impl.NoOpClientEngine;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.Joiner;
import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.AliasedDiscoveryConfigUtils;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MemberAttributeConfig;
import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.instance.AddressPicker;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.NodeContext;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.instance.NodeShutdownHelper;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.DiscoveryJoiner;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.MulticastJoiner;
import com.hazelcast.internal.cluster.impl.MulticastService;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.diagnostics.HealthMonitor;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.networking.ServerSocketRegistry;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentClassLoader;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.logging.LoggingServiceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.security.Credentials;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.security.SecurityService;
import com.hazelcast.spi.GracefulShutdownAwareService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryMode;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyServiceImpl;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@PrivateApi
public class Node {
    private static final int THREAD_SLEEP_DURATION_MS = 500;
    private static final String GRACEFUL_SHUTDOWN_EXECUTOR_NAME = "hz:graceful-shutdown";
    public final HazelcastInstanceImpl hazelcastInstance;
    public final DynamicConfigurationAwareConfig config;
    public final NodeEngineImpl nodeEngine;
    public final ClientEngine clientEngine;
    public final InternalPartitionServiceImpl partitionService;
    public final ClusterServiceImpl clusterService;
    public final MulticastService multicastService;
    public final DiscoveryService discoveryService;
    public final TextCommandService textCommandService;
    public final LoggingServiceImpl loggingService;
    public final NetworkingService networkingService;
    public final Address address;
    public final SecurityContext securityContext;
    private final ILogger logger;
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final NodeShutdownHookThread shutdownHookThread;
    private final InternalSerializationService serializationService;
    private final ClassLoader configClassLoader;
    private final NodeExtension nodeExtension;
    private final HazelcastProperties properties;
    private final BuildInfo buildInfo;
    private final HealthMonitor healthMonitor;
    private final Joiner joiner;
    private ManagementCenterService managementCenterService;
    private volatile NodeState state;
    private final MemberVersion version;

    public Node(HazelcastInstanceImpl hazelcastInstance, Config staticConfig, NodeContext nodeContext) {
        this.properties = new HazelcastProperties(staticConfig);
        DynamicConfigurationAwareConfig config = new DynamicConfigurationAwareConfig(staticConfig, this.properties);
        this.hazelcastInstance = hazelcastInstance;
        this.config = config;
        this.configClassLoader = Node.getConfigClassloader(config);
        String policy = this.properties.getString(GroupProperty.SHUTDOWNHOOK_POLICY);
        this.shutdownHookThread = new NodeShutdownHookThread("hz.ShutdownThread", policy);
        this.buildInfo = BuildInfoProvider.getBuildInfo();
        this.version = MemberVersion.of(this.buildInfo.getVersion());
        String loggingType = this.properties.getString(GroupProperty.LOGGING_TYPE);
        this.loggingService = new LoggingServiceImpl(config.getGroupConfig().getName(), loggingType, this.buildInfo);
        ConfigValidator.checkAdvancedNetworkConfig(config);
        AddressPicker addressPicker = nodeContext.createAddressPicker(this);
        try {
            addressPicker.pickAddress();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrow(e);
        }
        ServerSocketRegistry serverSocketRegistry = new ServerSocketRegistry(addressPicker.getServerSocketChannels(), !config.getAdvancedNetworkConfig().isEnabled());
        ILogger tmpLogger = null;
        try {
            boolean liteMember = config.isLiteMember();
            this.address = addressPicker.getPublicAddress(EndpointQualifier.MEMBER);
            this.nodeExtension = nodeContext.createNodeExtension(this);
            Map<String, Object> memberAttributes = this.findMemberAttributes(config.getMemberAttributeConfig().asReadOnly());
            MemberImpl localMember = new MemberImpl.Builder(addressPicker.getPublicAddressMap()).version(this.version).localMember(true).uuid(this.nodeExtension.createMemberUuid(this.address)).attributes(memberAttributes).liteMember(liteMember).instance(hazelcastInstance).build();
            this.loggingService.setThisMember(localMember);
            this.logger = tmpLogger = this.loggingService.getLogger(Node.class.getName());
            this.nodeExtension.printNodeInfo();
            this.logGroupPasswordInfo();
            this.nodeExtension.beforeStart();
            this.serializationService = this.nodeExtension.createSerializationService();
            this.securityContext = config.getSecurityConfig().isEnabled() ? this.nodeExtension.getSecurityContext() : null;
            this.nodeEngine = new NodeEngineImpl(this);
            config.setConfigurationService(this.nodeEngine.getConfigurationService());
            config.onSecurityServiceUpdated(this.getSecurityService());
            MetricsRegistry metricsRegistry = this.nodeEngine.getMetricsRegistry();
            metricsRegistry.collectMetrics(this.nodeExtension);
            this.networkingService = nodeContext.createNetworkingService(this, serverSocketRegistry);
            this.healthMonitor = new HealthMonitor(this);
            this.clientEngine = this.hasClientServerSocket() ? new ClientEngineImpl(this) : new NoOpClientEngine();
            JoinConfig joinConfig = ConfigAccessor.getActiveMemberNetworkConfig(this.config).getJoin();
            DiscoveryConfig discoveryConfig = joinConfig.getDiscoveryConfig().getAsReadOnly();
            List<DiscoveryStrategyConfig> aliasedDiscoveryConfigs = AliasedDiscoveryConfigUtils.createDiscoveryStrategyConfigs(joinConfig);
            this.discoveryService = this.createDiscoveryService(discoveryConfig, aliasedDiscoveryConfigs, localMember);
            this.clusterService = new ClusterServiceImpl(this, localMember);
            this.partitionService = new InternalPartitionServiceImpl(this);
            this.textCommandService = this.nodeExtension.createTextCommandService();
            this.multicastService = MulticastService.createMulticastService(addressPicker.getBindAddress(EndpointQualifier.MEMBER), this, config, this.logger);
            this.joiner = nodeContext.createJoiner(this);
        }
        catch (Throwable e) {
            try {
                if (tmpLogger == null) {
                    tmpLogger = Logger.getLogger(Node.class);
                }
                tmpLogger.severe("Node creation failed", e);
            }
            catch (Exception e1) {
                EmptyStatement.ignore(e1);
            }
            serverSocketRegistry.destroy();
            try {
                this.shutdownServices(true);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
            throw ExceptionUtil.rethrow(e);
        }
    }

    private boolean hasClientServerSocket() {
        if (!this.config.getAdvancedNetworkConfig().isEnabled()) {
            return true;
        }
        Map<EndpointQualifier, EndpointConfig> endpointConfigs = this.config.getAdvancedNetworkConfig().getEndpointConfigs();
        EndpointConfig clientEndpointConfig = endpointConfigs.get(EndpointQualifier.CLIENT);
        return clientEndpointConfig != null;
    }

    private static ClassLoader getConfigClassloader(Config config) {
        ClassLoader classLoader;
        UserCodeDeploymentConfig userCodeDeploymentConfig = config.getUserCodeDeploymentConfig();
        if (userCodeDeploymentConfig.isEnabled()) {
            ClassLoader parent = config.getClassLoader();
            final ClassLoader theParent = parent == null ? Node.class.getClassLoader() : parent;
            classLoader = AccessController.doPrivileged(new PrivilegedAction<UserCodeDeploymentClassLoader>(){

                @Override
                public UserCodeDeploymentClassLoader run() {
                    return new UserCodeDeploymentClassLoader(theParent);
                }
            });
        } else {
            classLoader = config.getClassLoader();
        }
        return classLoader;
    }

    public DiscoveryService createDiscoveryService(DiscoveryConfig discoveryConfig, List<DiscoveryStrategyConfig> aliasedDiscoveryConfigs, Member localMember) {
        DiscoveryServiceProvider factory = discoveryConfig.getDiscoveryServiceProvider();
        if (factory == null) {
            factory = new DefaultDiscoveryServiceProvider();
        }
        ILogger logger = this.getLogger(DiscoveryService.class);
        DiscoveryServiceSettings settings = new DiscoveryServiceSettings().setConfigClassLoader(this.configClassLoader).setLogger(logger).setDiscoveryMode(DiscoveryMode.Member).setDiscoveryConfig(discoveryConfig).setAliasedDiscoveryConfigs(aliasedDiscoveryConfigs).setDiscoveryNode(new SimpleDiscoveryNode(localMember.getAddress(), localMember.getAttributes()));
        return factory.newDiscoveryService(settings);
    }

    private void initializeListeners(Config config) {
        for (ListenerConfig listenerCfg : config.getListenerConfigs()) {
            EventListener listener = listenerCfg.getImplementation();
            if (listener == null) {
                try {
                    listener = ClassLoaderUtil.newInstance(this.configClassLoader, listenerCfg.getClassName());
                }
                catch (Exception e) {
                    this.logger.severe(e);
                }
            }
            if (listener instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)listener)).setHazelcastInstance(this.hazelcastInstance);
            }
            boolean known = false;
            if (listener instanceof DistributedObjectListener) {
                ProxyServiceImpl proxyService = (ProxyServiceImpl)this.nodeEngine.getProxyService();
                proxyService.addProxyListener((DistributedObjectListener)listener);
                known = true;
            }
            if (listener instanceof MembershipListener) {
                this.clusterService.addMembershipListener((MembershipListener)listener);
                known = true;
            }
            if (listener instanceof MigrationListener) {
                this.partitionService.addMigrationListener((MigrationListener)listener);
                known = true;
            }
            if (listener instanceof PartitionLostListener) {
                this.partitionService.addPartitionLostListener((PartitionLostListener)listener);
                known = true;
            }
            if (listener instanceof LifecycleListener) {
                this.hazelcastInstance.lifecycleService.addLifecycleListener((LifecycleListener)listener);
                known = true;
            }
            if (listener instanceof ClientListener) {
                String serviceName = "hz:core:clientEngine";
                this.nodeEngine.getEventService().registerLocalListener(serviceName, serviceName, listener);
                known = true;
            }
            if (listener instanceof InternalMigrationListener) {
                InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.nodeEngine.getPartitionService();
                partitionService.setInternalMigrationListener((InternalMigrationListener)listener);
                known = true;
            }
            if (this.nodeExtension.registerListener(listener)) {
                known = true;
            }
            if (listener == null || known) continue;
            String error = "Unknown listener type: " + listener.getClass();
            IllegalArgumentException t = new IllegalArgumentException(error);
            this.logger.warning(error, t);
        }
    }

    public ManagementCenterService getManagementCenterService() {
        return this.managementCenterService;
    }

    public InternalSerializationService getSerializationService() {
        return this.serializationService;
    }

    public ClusterServiceImpl getClusterService() {
        return this.clusterService;
    }

    public InternalPartitionService getPartitionService() {
        return this.partitionService;
    }

    public Address getMasterAddress() {
        return this.clusterService.getMasterAddress();
    }

    public Address getThisAddress() {
        return this.address;
    }

    public MemberImpl getLocalMember() {
        return this.clusterService.getLocalMember();
    }

    public boolean isMaster() {
        return this.clusterService.isMaster();
    }

    public SecurityService getSecurityService() {
        return this.nodeExtension.getSecurityService();
    }

    void start() {
        this.nodeEngine.start();
        this.initializeListeners(this.config);
        this.hazelcastInstance.lifecycleService.fireLifecycleEvent(LifecycleEvent.LifecycleState.STARTING);
        this.clusterService.sendLocalMembershipEvent();
        this.networkingService.start();
        JoinConfig join = ConfigAccessor.getActiveMemberNetworkConfig(this.config).getJoin();
        if (join.getMulticastConfig().isEnabled()) {
            Thread multicastServiceThread = new Thread((Runnable)this.multicastService, ThreadUtil.createThreadName(this.hazelcastInstance.getName(), "MulticastThread"));
            multicastServiceThread.start();
        }
        if (this.properties.getBoolean(GroupProperty.DISCOVERY_SPI_ENABLED) || Node.isAnyAliasedConfigEnabled(join)) {
            this.discoveryService.start();
            this.mergeEnvironmentProvidedMemberMetadata();
        }
        if (this.properties.getBoolean(GroupProperty.SHUTDOWNHOOK_ENABLED)) {
            this.logger.finest("Adding ShutdownHook");
            Runtime.getRuntime().addShutdownHook(this.shutdownHookThread);
        }
        this.state = NodeState.ACTIVE;
        this.nodeExtension.beforeJoin();
        this.join();
        int clusterSize = this.clusterService.getSize();
        if (ConfigAccessor.getActiveMemberNetworkConfig(this.config).isPortAutoIncrement() && this.address.getPort() >= ConfigAccessor.getActiveMemberNetworkConfig(this.config).getPort() + clusterSize) {
            this.logger.warning("Config seed port is " + ConfigAccessor.getActiveMemberNetworkConfig(this.config).getPort() + " and cluster size is " + clusterSize + ". Some of the ports seem occupied!");
        }
        try {
            this.managementCenterService = new ManagementCenterService(this.hazelcastInstance);
        }
        catch (Exception e) {
            this.logger.warning("ManagementCenterService could not be constructed!", e);
        }
        this.nodeExtension.afterStart();
        this.nodeExtension.sendPhoneHome();
        this.healthMonitor.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown(boolean terminate) {
        long start = Clock.currentTimeMillis();
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("We are being asked to shutdown when state = " + (Object)((Object)this.state));
        }
        if (!this.setShuttingDown()) {
            this.waitIfAlreadyShuttingDown();
            return;
        }
        if (!terminate) {
            int maxWaitSeconds = this.properties.getSeconds(GroupProperty.GRACEFUL_SHUTDOWN_MAX_WAIT);
            this.callGracefulShutdownAwareServices(maxWaitSeconds);
        } else {
            this.logger.warning("Terminating forcefully...");
        }
        this.clusterService.resetJoinState();
        try {
            if (this.properties.getBoolean(GroupProperty.SHUTDOWNHOOK_ENABLED)) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHookThread);
            }
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
        try {
            this.discoveryService.destroy();
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
        try {
            this.shutdownServices(terminate);
            this.state = NodeState.SHUT_DOWN;
            this.logger.info("Hazelcast Shutdown is completed in " + (Clock.currentTimeMillis() - start) + " ms.");
        }
        finally {
            if (this.state != NodeState.SHUT_DOWN) {
                this.shuttingDown.compareAndSet(true, false);
            }
        }
    }

    private void callGracefulShutdownAwareServices(final int maxWaitSeconds) {
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor(GRACEFUL_SHUTDOWN_EXECUTOR_NAME);
        Collection<GracefulShutdownAwareService> services = this.nodeEngine.getServices(GracefulShutdownAwareService.class);
        ArrayList futures = new ArrayList(services.size());
        for (final GracefulShutdownAwareService service : services) {
            Future<?> future = executor.submit(new Runnable(){

                @Override
                public void run() {
                    try {
                        boolean success = service.onShutdown(maxWaitSeconds, TimeUnit.SECONDS);
                        if (success) {
                            Node.this.logger.fine("Graceful shutdown completed for " + service);
                        } else {
                            Node.this.logger.warning("Graceful shutdown failed for " + service);
                        }
                    }
                    catch (Throwable e) {
                        Node.this.logger.severe("Graceful shutdown failed for " + service, e);
                    }
                }

                public String toString() {
                    return "Graceful shutdown task for service [" + service.toString() + "]";
                }
            });
            futures.add(future);
        }
        try {
            FutureUtil.waitWithDeadline(futures, maxWaitSeconds, TimeUnit.SECONDS, FutureUtil.RETHROW_EVERYTHING);
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
    }

    private void shutdownServices(boolean terminate) {
        if (this.nodeExtension != null) {
            this.nodeExtension.beforeShutdown();
        }
        if (this.managementCenterService != null) {
            this.managementCenterService.shutdown();
        }
        if (this.textCommandService != null) {
            this.textCommandService.stop();
        }
        if (this.multicastService != null) {
            this.logger.info("Shutting down multicast service...");
            this.multicastService.stop();
        }
        if (this.networkingService != null) {
            this.logger.info("Shutting down connection manager...");
            this.networkingService.shutdown();
        }
        if (this.nodeEngine != null) {
            this.logger.info("Shutting down node engine...");
            this.nodeEngine.shutdown(terminate);
        }
        if (this.securityContext != null) {
            this.securityContext.destroy();
        }
        if (this.serializationService != null) {
            this.logger.finest("Destroying serialization service...");
            this.serializationService.dispose();
        }
        if (this.nodeExtension != null) {
            this.nodeExtension.shutdown();
        }
        if (this.healthMonitor != null) {
            this.healthMonitor.stop();
        }
    }

    private void mergeEnvironmentProvidedMemberMetadata() {
        MemberImpl localMember = this.getLocalMember();
        Map<String, Object> metadata = this.discoveryService.discoverLocalMetadata();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Byte) {
                localMember.setByteAttribute(entry.getKey(), (Byte)value);
                continue;
            }
            if (value instanceof Short) {
                localMember.setShortAttribute(entry.getKey(), (Short)value);
                continue;
            }
            if (value instanceof Integer) {
                localMember.setIntAttribute(entry.getKey(), (Integer)value);
                continue;
            }
            if (value instanceof Long) {
                localMember.setLongAttribute(entry.getKey(), (Long)value);
                continue;
            }
            if (value instanceof Float) {
                localMember.setFloatAttribute(entry.getKey(), ((Float)value).floatValue());
                continue;
            }
            if (value instanceof Double) {
                localMember.setDoubleAttribute(entry.getKey(), (Double)value);
                continue;
            }
            if (value instanceof Boolean) {
                localMember.setBooleanAttribute(entry.getKey(), (Boolean)value);
                continue;
            }
            localMember.setStringAttribute(entry.getKey(), value.toString());
        }
    }

    public boolean setShuttingDown() {
        if (this.shuttingDown.compareAndSet(false, true)) {
            this.state = NodeState.PASSIVE;
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return !this.shuttingDown.get();
    }

    private void waitIfAlreadyShuttingDown() {
        if (!this.shuttingDown.get()) {
            return;
        }
        this.logger.info("Node is already shutting down... Waiting for shutdown process to complete...");
        while (this.state != NodeState.SHUT_DOWN && this.shuttingDown.get()) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.logger.warning("Interrupted while waiting for shutdown!");
                return;
            }
        }
        if (this.state != NodeState.SHUT_DOWN) {
            throw new IllegalStateException("Node failed to shutdown!");
        }
    }

    public void changeNodeStateToActive() {
        ClusterState clusterState = this.clusterService.getClusterState();
        if (clusterState == ClusterState.PASSIVE) {
            throw new IllegalStateException("This method can be called only when cluster-state is not " + (Object)((Object)clusterState));
        }
        this.state = NodeState.ACTIVE;
    }

    public void changeNodeStateToPassive() {
        ClusterState clusterState = this.clusterService.getClusterState();
        if (clusterState != ClusterState.PASSIVE) {
            throw new IllegalStateException("This method can be called only when cluster-state is " + (Object)((Object)clusterState));
        }
        this.state = NodeState.PASSIVE;
    }

    public void reset() {
        this.state = NodeState.ACTIVE;
        this.clusterService.resetJoinState();
        this.joiner.reset();
    }

    public LoggingService getLoggingService() {
        return this.loggingService;
    }

    public ILogger getLogger(String name) {
        return this.loggingService.getLogger(name);
    }

    public ILogger getLogger(Class clazz) {
        return this.loggingService.getLogger(clazz);
    }

    public HazelcastProperties getProperties() {
        return this.properties;
    }

    public TextCommandService getTextCommandService() {
        return this.textCommandService;
    }

    public NetworkingService getNetworkingService() {
        return this.networkingService;
    }

    public EndpointManager getEndpointManager() {
        return this.getEndpointManager(EndpointQualifier.MEMBER);
    }

    public <T extends Connection> EndpointManager<T> getEndpointManager(EndpointQualifier qualifier) {
        return this.networkingService.getEndpointManager(qualifier);
    }

    public ClassLoader getConfigClassLoader() {
        return this.configClassLoader;
    }

    public NodeEngineImpl getNodeEngine() {
        return this.nodeEngine;
    }

    public ClientEngine getClientEngine() {
        return this.clientEngine;
    }

    public NodeExtension getNodeExtension() {
        return this.nodeExtension;
    }

    public DiscoveryService getDiscoveryService() {
        return this.discoveryService;
    }

    public SplitBrainJoinMessage createSplitBrainJoinMessage() {
        MemberImpl localMember = this.getLocalMember();
        boolean liteMember = localMember.isLiteMember();
        Collection<Address> memberAddresses = this.clusterService.getMemberAddresses();
        int dataMemberCount = this.clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR);
        Version clusterVersion = this.clusterService.getClusterVersion();
        int memberListVersion = this.clusterService.getMembershipManager().getMemberListVersion();
        return new SplitBrainJoinMessage(4, this.buildInfo.getBuildNumber(), this.version, this.address, localMember.getUuid(), liteMember, this.createConfigCheck(), memberAddresses, dataMemberCount, clusterVersion, memberListVersion);
    }

    public JoinRequest createJoinRequest(boolean withCredentials) {
        Credentials credentials = withCredentials && this.securityContext != null ? this.securityContext.getCredentialsFactory().newCredentials() : null;
        Set<String> excludedMemberUuids = this.nodeExtension.getInternalHotRestartService().getExcludedMemberUuids();
        MemberImpl localMember = this.getLocalMember();
        return new JoinRequest(4, this.buildInfo.getBuildNumber(), this.version, this.address, localMember.getUuid(), localMember.isLiteMember(), this.createConfigCheck(), credentials, localMember.getAttributes(), excludedMemberUuids, localMember.getAddressMap());
    }

    public ConfigCheck createConfigCheck() {
        String joinerType = this.joiner == null ? "" : this.joiner.getType();
        return new ConfigCheck(this.config, joinerType);
    }

    public void join() {
        if (this.clusterService.isJoined()) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Calling join on already joined node. ", new Exception("stacktrace"));
            } else {
                this.logger.warning("Calling join on already joined node. ");
            }
            return;
        }
        if (this.joiner == null) {
            this.logger.warning("No join method is enabled! Starting standalone.");
            ClusterJoinManager clusterJoinManager = this.clusterService.getClusterJoinManager();
            clusterJoinManager.setThisMemberAsMaster();
            return;
        }
        try {
            this.clusterService.resetJoinState();
            this.joiner.join();
        }
        catch (Throwable e) {
            this.logger.severe("Error while joining the cluster!", e);
        }
        if (!this.clusterService.isJoined()) {
            this.logger.severe("Could not join cluster. Shutting down now!");
            NodeShutdownHelper.shutdownNodeByFiringEvents(this, true);
        }
    }

    public Joiner getJoiner() {
        return this.joiner;
    }

    Joiner createJoiner() {
        JoinConfig join = ConfigAccessor.getActiveMemberNetworkConfig(this.config).getJoin();
        join.verify();
        if (this.properties.getBoolean(GroupProperty.DISCOVERY_SPI_ENABLED) || Node.isAnyAliasedConfigEnabled(join)) {
            this.logger.info("Activating Discovery SPI Joiner");
            return new DiscoveryJoiner(this, this.discoveryService, this.usePublicAddress(join));
        }
        if (join.getMulticastConfig().isEnabled() && this.multicastService != null) {
            this.logger.info("Creating MulticastJoiner");
            return new MulticastJoiner(this);
        }
        if (join.getTcpIpConfig().isEnabled()) {
            this.logger.info("Creating TcpIpJoiner");
            return new TcpIpJoiner(this);
        }
        if (join.getAwsConfig().isEnabled()) {
            this.logger.info("Creating AWSJoiner");
            return this.createAwsJoiner();
        }
        return null;
    }

    private static boolean isAnyAliasedConfigEnabled(JoinConfig join) {
        return !AliasedDiscoveryConfigUtils.createDiscoveryStrategyConfigs(join).isEmpty();
    }

    private boolean usePublicAddress(JoinConfig join) {
        return this.properties.getBoolean(GroupProperty.DISCOVERY_SPI_PUBLIC_IP_ENABLED) || AliasedDiscoveryConfigUtils.allUsePublicAddress(AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(join));
    }

    private Joiner createAwsJoiner() {
        try {
            Class<?> clazz = Class.forName("com.hazelcast.cluster.impl.TcpIpJoinerOverAWS");
            Constructor<?> constructor = clazz.getConstructor(Node.class);
            return (Joiner)constructor.newInstance(this);
        }
        catch (ClassNotFoundException e) {
            String message = "Your Hazelcast network configuration has AWS discovery enabled, but there is no Hazelcast AWS module on a classpath. " + StringUtil.LINE_SEPARATOR + "Hint: If you are using Maven then add this dependency into your pom.xml:" + StringUtil.LINE_SEPARATOR + "<dependency>" + StringUtil.LINE_SEPARATOR + "    <groupId>com.hazelcast</groupId>" + StringUtil.LINE_SEPARATOR + "    <artifactId>hazelcast-aws</artifactId>" + StringUtil.LINE_SEPARATOR + "    <version>insert hazelcast-aws version</version>" + StringUtil.LINE_SEPARATOR + "</dependency>" + StringUtil.LINE_SEPARATOR + " See https://github.com/hazelcast/hazelcast-aws for additional details";
            throw new ConfigurationException(message, e);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    public String getThisUuid() {
        return this.clusterService.getThisUuid();
    }

    public Config getConfig() {
        return this.config;
    }

    public NodeState getState() {
        return this.state;
    }

    public MemberVersion getVersion() {
        return this.version;
    }

    public boolean isLiteMember() {
        return this.getLocalMember().isLiteMember();
    }

    public String toString() {
        return "Node[" + this.hazelcastInstance.getName() + "]";
    }

    public BuildInfo getBuildInfo() {
        return this.buildInfo;
    }

    private Map<String, Object> findMemberAttributes(MemberAttributeConfig attributeConfig) {
        HashMap<String, Object> attributes = new HashMap<String, Object>(attributeConfig.getAttributes());
        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("hazelcast.member.attribute.")) continue;
            String shortKey = key.substring("hazelcast.member.attribute.".length());
            String value = properties.getProperty(key);
            attributes.put(shortKey, value);
        }
        return attributes;
    }

    private void logGroupPasswordInfo() {
        String password = this.config.getGroupConfig().getPassword();
        if (!(this.config.getSecurityConfig().isEnabled() || StringUtil.isNullOrEmpty(password) || "dev-pass".equals(password))) {
            this.logger.info("A non-empty group password is configured for the Hazelcast member. Since version 3.8.2, members with the same group name, but with different group passwords (that do not use authentication) form a cluster. The group password configuration will be removed completely in a future release.");
        }
    }

    public class NodeShutdownHookThread
    extends Thread {
        private final ShutdownHookPolicy policy;

        NodeShutdownHookThread(String name, String policy) {
            super(name);
            this.policy = ShutdownHookPolicy.valueOf(policy);
        }

        @Override
        public void run() {
            try {
                if (Node.this.isRunning()) {
                    Node.this.logger.info("Running shutdown hook... Current state: " + (Object)((Object)Node.this.state));
                    switch (this.policy) {
                        case TERMINATE: {
                            Node.this.hazelcastInstance.getLifecycleService().terminate();
                            break;
                        }
                        case GRACEFUL: {
                            Node.this.hazelcastInstance.getLifecycleService().shutdown();
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Unimplemented shutdown hook policy: " + (Object)((Object)this.policy));
                        }
                    }
                }
            }
            catch (Exception e) {
                Node.this.logger.warning(e);
            }
        }
    }

    private static enum ShutdownHookPolicy {
        TERMINATE,
        GRACEFUL;

    }
}

