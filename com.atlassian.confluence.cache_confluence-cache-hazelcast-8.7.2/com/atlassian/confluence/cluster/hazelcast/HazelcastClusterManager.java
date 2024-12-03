/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.confluence.cluster.ClusterConfig
 *  com.atlassian.confluence.cluster.ClusterException
 *  com.atlassian.confluence.cluster.ClusterInformation
 *  com.atlassian.confluence.cluster.ClusterInvariants
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeExecution
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.cluster.ClusteredLock
 *  com.atlassian.confluence.cluster.EmptyClusterInformation
 *  com.atlassian.confluence.cluster.NoSuchClusterNodeException
 *  com.atlassian.confluence.cluster.NodeStatus
 *  com.atlassian.confluence.cluster.safety.ClusterPanicEvent
 *  com.atlassian.confluence.concurrent.Lock
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.core.SynchronizationManager
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.impl.cluster.event.ClusterEventService
 *  com.atlassian.confluence.impl.metrics.ConfluenceMicrometer
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.core.task.FifoBuffer
 *  com.atlassian.hazelcast.micrometer.HazelcastBinder
 *  com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Throwables
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.hazelcast.config.Config
 *  com.hazelcast.core.Hazelcast
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IExecutorService
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.IQueue
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.OutOfMemoryHandler
 *  com.hazelcast.instance.HazelcastInstanceProxy
 *  com.hazelcast.internal.serialization.InternalSerializationService
 *  com.hazelcast.spi.impl.SerializationServiceSupport
 *  io.micrometer.core.instrument.MeterRegistry
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.cache.hazelcast.DefaultHazelcastHelper;
import com.atlassian.confluence.cluster.ClusterConfig;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterInvariants;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.ClusteredLock;
import com.atlassian.confluence.cluster.EmptyClusterInformation;
import com.atlassian.confluence.cluster.NoSuchClusterNodeException;
import com.atlassian.confluence.cluster.NodeStatus;
import com.atlassian.confluence.cluster.hazelcast.CollectClusterInvariants;
import com.atlassian.confluence.cluster.hazelcast.CollectNodeStatus;
import com.atlassian.confluence.cluster.hazelcast.ConfluenceHazelcastConfigBuilder;
import com.atlassian.confluence.cluster.hazelcast.DualLock;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterEventWrapper;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterInformation;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusterNodeInformation;
import com.atlassian.confluence.cluster.hazelcast.HazelcastClusteredFifoBuffer;
import com.atlassian.confluence.cluster.hazelcast.HazelcastDualLock;
import com.atlassian.confluence.cluster.hazelcast.HazelcastUtils;
import com.atlassian.confluence.cluster.hazelcast.JvmDualLock;
import com.atlassian.confluence.cluster.hazelcast.MeteredDualLock;
import com.atlassian.confluence.cluster.hazelcast.shareddata.HazelcastSharedDataSupport;
import com.atlassian.confluence.cluster.safety.ClusterPanicEvent;
import com.atlassian.confluence.concurrent.Lock;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.impl.cluster.event.ClusterEventService;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.DefaultClusterJoinManager;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.SharedSecretClusterAuthenticator;
import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.task.FifoBuffer;
import com.atlassian.hazelcast.micrometer.HazelcastBinder;
import com.atlassian.hazelcast.serialization.OsgiSafeStreamSerializer;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class HazelcastClusterManager
implements ClusterManager,
ClusterLockService,
com.google.common.base.Supplier<HazelcastInstance>,
com.atlassian.util.concurrent.Supplier<HazelcastInstance>,
Supplier<HazelcastInstance> {
    private static final Logger log = LoggerFactory.getLogger(HazelcastClusterManager.class);
    private static final String FIFO_BUFFER_PREFIX = "confluence.fifo.buffer.";
    private static final String LOCK_CACHE_NAME = "com.atlassian.confluence.lock-cache";
    private static final String LEGACY_PUBLISHED_MAP_NAME = "legacy";
    private final Function<String, JvmDualLock> nonClusterLock = CacheBuilder.newBuilder().build(CacheLoader.from(JvmDualLock::new));
    private final ConcurrentMap<String, Serializable> publishMap = Maps.newConcurrentMap();
    private final AtomicBoolean clusterIsRunning = new AtomicBoolean(false);
    private final ApplicationConfig applicationConfig;
    private final ClassLoader classLoader;
    private final String configResourceName;
    private final SynchronizationManager synchronizationManager;
    private final OsgiSafeStreamSerializer osgiSafeStreamSerializer;
    private final OutOfMemoryHandler outOfMemoryHandler;
    private final MeterRegistry micrometerRegistry;
    private final MBeanServer mbeanServer;
    private final HazelcastSharedDataSupport sharedDataSupport;
    private HazelcastInstance instance;
    private Config instanceConfig;
    private static final Duration EVENT_PUBLISH_TIMEOUT = Duration.ofSeconds(Integer.getInteger("confluence.clusterEvent.timeout", 10).intValue());

    public HazelcastClusterManager(ApplicationConfig applicationConfig, ClassLoader classLoader, String configResourceName, SynchronizationManager synchronizationManager, OsgiSafeStreamSerializer osgiSafeStreamSerializer, OutOfMemoryHandler outOfMemoryHandler, MeterRegistry micrometerRegistry, MBeanServer mbeanServer) {
        this.applicationConfig = Objects.requireNonNull(applicationConfig);
        this.classLoader = Objects.requireNonNull(classLoader);
        this.configResourceName = Objects.requireNonNull(configResourceName);
        this.synchronizationManager = Objects.requireNonNull(synchronizationManager);
        this.osgiSafeStreamSerializer = Objects.requireNonNull(osgiSafeStreamSerializer);
        this.outOfMemoryHandler = outOfMemoryHandler;
        this.micrometerRegistry = micrometerRegistry;
        this.mbeanServer = mbeanServer;
        this.sharedDataSupport = new HazelcastSharedDataSupport(this.getClass().getSimpleName(), this);
    }

    @PostConstruct
    public void initOutOfmemoryhandler() {
        Hazelcast.setOutOfMemoryHandler((OutOfMemoryHandler)Objects.requireNonNull(this.outOfMemoryHandler));
    }

    public boolean isClusterSupported() {
        return true;
    }

    public long getClusterUptime() {
        if (!this.isClustered()) {
            return System.currentTimeMillis() - GeneralUtil.getSystemStartupTime();
        }
        if (!(this.get() instanceof HazelcastInstanceProxy)) {
            throw new UnsupportedOperationException(String.format("Method expects HazelcastInstance implementation class %s but found %s instead", HazelcastInstanceProxy.class, this.get().getClass()));
        }
        HazelcastInstanceProxy instanceProxy = (HazelcastInstanceProxy)this.get();
        return instanceProxy.getOriginal().node.getClusterService().getClusterClock().getClusterUpTime();
    }

    private <K extends Serializable, V extends Serializable> @NonNull Map<K, V> getSharedMap() {
        return this.sharedDataSupport.getSharedData(LEGACY_PUBLISHED_MAP_NAME).getMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClustered() {
        ApplicationConfig applicationConfig = this.applicationConfig;
        synchronized (applicationConfig) {
            Object isClustered = this.applicationConfig.getProperty((Object)"confluence.cluster");
            return "true".equals(isClustered);
        }
    }

    @Deprecated(since="8.2", forRemoval=true)
    public ClusterInformation getClusterInformation() {
        return null == this.instance ? new EmptyClusterInformation() : new HazelcastClusterInformation(this.instance);
    }

    @Deprecated(since="8.2", forRemoval=true)
    public <T> FifoBuffer<T> getFifoBuffer(String name) {
        if (!this.isClustered()) {
            return null;
        }
        Preconditions.checkState((null != this.instance ? 1 : 0) != 0, (Object)"Cluster not started.");
        IQueue queue = this.instance.getQueue(FIFO_BUFFER_PREFIX + Objects.requireNonNull(name));
        return new HazelcastClusteredFifoBuffer(queue);
    }

    public void publishEvent(final ConfluenceEvent event) {
        if (!this.isClustered()) {
            return;
        }
        if (event instanceof HazelcastClusterEventWrapper && ((HazelcastClusterEventWrapper)event).getEvent() instanceof ClusterPanicEvent) {
            this.publishEventImmediately(event);
        } else {
            this.synchronizationManager.runOnSuccessfulCommit(new Runnable(){

                @Override
                public void run() {
                    HazelcastClusterManager.this.publishEventImmediately(event);
                }

                public String toString() {
                    return "Sending remote event: " + event;
                }
            });
        }
    }

    public void publishEventImmediately(ConfluenceEvent event) {
        if (!this.isClustered()) {
            return;
        }
        HazelcastClusterManager.getClusterEventService().ifPresent(eventService -> {
            try {
                eventService.publishEventToCluster((Object)event).get(EVENT_PUBLISH_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while publishing event to cluster", (Throwable)e);
            }
            catch (ExecutionException e) {
                log.error("Error while publishing event to cluster", e.getCause());
            }
            catch (TimeoutException e) {
                log.error("Timeout while publishing event to cluster", (Throwable)e);
            }
        });
    }

    private static Optional<ClusterEventService> getClusterEventService() {
        if (ContainerManager.isContainerSetup()) {
            return Optional.of((ClusterEventService)ContainerManager.getComponent((String)"clusterEventService", ClusterEventService.class));
        }
        return Optional.empty();
    }

    public ClusterNodeInformation getThisNodeInformation() {
        return null == this.instance ? null : HazelcastClusterManager.getLocalMemberInfo(this.instance);
    }

    private static HazelcastClusterNodeInformation getLocalMemberInfo(HazelcastInstance instance) {
        return new HazelcastClusterNodeInformation(instance.getCluster().getLocalMember());
    }

    public Collection<ClusterNodeInformation> getAllNodesInformation() {
        return this.isClustered() ? Lists.newArrayList((Iterable)Collections2.transform((Collection)this.instance.getCluster().getMembers(), HazelcastClusterNodeInformation::new)) : Collections.emptySet();
    }

    public void configure(ClusterConfig clusterConfig) {
        this.instanceConfig = new ConfluenceHazelcastConfigBuilder(this.applicationConfig, this.classLoader, this.osgiSafeStreamSerializer, ConfluenceSystemProperties.isDevMode(), new DefaultHazelcastHelper(this), this::bindMicrometer, new DefaultClusterJoinManager(new SharedSecretClusterAuthenticator(clusterConfig.getClusterName(), this.getSharedSecret()), this.isNodeAuthEnabled())).createHazelcastConfig(clusterConfig, this.configResourceName);
        log.info("Configuring Hazelcast with instanceName [{}], join configuration {}, network interfaces {} and local port {}", new Object[]{this.instanceConfig.getInstanceName(), clusterConfig.getJoinConfig(), this.instanceConfig.getNetworkConfig().getInterfaces().getInterfaces(), this.instanceConfig.getNetworkConfig().getPort()});
    }

    private void bindMicrometer(Config hazelcastConfig) {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            new HazelcastBinder(this.micrometerRegistry, this.mbeanServer).bind(hazelcastConfig);
        }
    }

    public boolean isConfigured() {
        return null != this.instanceConfig;
    }

    public void reconfigure(ClusterConfig config) {
        this.stopCluster();
        this.configure(config);
        this.startCluster();
    }

    @PreDestroy
    public void stopCluster() {
        this.clusterIsRunning.set(false);
        if (null != this.instance) {
            log.info("Shutting down the cluster");
            this.instance.shutdown();
            this.instance = null;
            this.instanceConfig = null;
        }
    }

    public void startCluster() {
        Preconditions.checkState((!this.clusterIsRunning.get() ? 1 : 0) != 0, (Object)"Cluster already running");
        Preconditions.checkState((null != this.instanceConfig ? 1 : 0) != 0, (Object)"Cannot start cluster until it has been configured.");
        if (null == this.instance) {
            log.info("Starting the cluster.");
            this.instance = this.createHazelcastInstance();
            this.osgiSafeStreamSerializer.setSerializationService((InternalSerializationService)((SerializationServiceSupport)this.instance).getSerializationService());
            log.info("Confluence cluster node identifier is [{}]", (Object)HazelcastClusterManager.getLocalMemberInfo(this.instance).getAnonymizedNodeIdentifier());
            String nodeName = HazelcastClusterManager.getHumanReadableClusterNodeName();
            if (nodeName != null) {
                log.info("Confluence cluster node name is [{}]", (Object)nodeName);
                HazelcastUtils.setConfiguredMemberName(this.instance.getCluster().getLocalMember(), nodeName);
            }
            this.clusterIsRunning.set(true);
        } else {
            log.warn("Ignoring a duplicate request to start the cluster.");
        }
    }

    private static @Nullable String getHumanReadableClusterNodeName() {
        if (ConfluenceSystemProperties.isUseHostnameAsHumanReadableClusterNodeName()) {
            return System.getenv("HOSTNAME");
        }
        return ConfluenceSystemProperties.getHumanReadableClusterNodeName();
    }

    @VisibleForTesting
    protected HazelcastInstance createHazelcastInstance() {
        return Hazelcast.newHazelcastInstance((Config)this.instanceConfig);
    }

    @Deprecated
    public Map<Integer, NodeStatus> getNodeStatuses() {
        if (!this.isClustered()) {
            return Collections.emptyMap();
        }
        IExecutorService svc = this.instance.getExecutorService("cluster-manager-executor");
        TreeMap result = Maps.newTreeMap();
        Map futures = svc.submitToAllMembers((Callable)new CollectNodeStatus(this.instance.getCluster().getLocalMember().getUuid()));
        for (Map.Entry entry : futures.entrySet()) {
            try {
                result.put(HazelcastClusterNodeInformation.generateId((Member)entry.getKey()), (NodeStatus)((Future)entry.getValue()).get());
            }
            catch (InterruptedException | ExecutionException ex) {
                log.warn("Ignoring error getting node status from {}", entry.getKey(), (Object)ex);
            }
        }
        return result;
    }

    public Map<ClusterNodeInformation, NodeStatus> getNodeStatusMap() {
        ImmutableMap.Builder result = ImmutableMap.builder();
        for (Map.Entry<ClusterNodeInformation, CompletionStage<NodeStatus>> resultEntry : this.getNodeStatusMapAsync().entrySet()) {
            ClusterNodeInformation nodeInfo = resultEntry.getKey();
            try {
                NodeStatus nodeStatus = resultEntry.getValue().toCompletableFuture().get(5L, TimeUnit.SECONDS);
                result.put((Object)nodeInfo, (Object)nodeStatus);
            }
            catch (InterruptedException ex) {
                log.warn("Thead interrupted whilst getting node status from {}", (Object)nodeInfo, (Object)ex);
                Thread.currentThread().interrupt();
                break;
            }
            catch (ExecutionException ex) {
                throw new RuntimeException("Failure when getting node status information from " + nodeInfo, ex.getCause());
            }
            catch (TimeoutException ex) {
                log.warn("Timed out waiting for node status information from {}", (Object)nodeInfo, (Object)ex);
            }
        }
        return result.build();
    }

    public Map<ClusterNodeInformation, CompletionStage<NodeStatus>> getNodeStatusMapAsync() {
        if (!this.isClustered()) {
            return Collections.emptyMap();
        }
        return this.submitToAllNodes(new CollectNodeStatus(HazelcastClusterManager.getLocalMemberInfo(this.instance).getAnonymizedNodeIdentifier()), "cluster-manager-executor").stream().collect(Collectors.toMap(e -> Objects.requireNonNull(e.getClusterNode()), e -> e.getCompletionStage()));
    }

    public ClusterInvariants getClusterInvariants() throws ClusterException {
        if (!this.isClustered()) {
            return null;
        }
        LinkedHashSet otherMembers = Sets.newLinkedHashSet((Iterable)this.instance.getCluster().getMembers());
        otherMembers.remove(this.instance.getCluster().getLocalMember());
        if (otherMembers.isEmpty()) {
            return null;
        }
        IExecutorService svc = this.instance.getExecutorService("cluster-manager-executor");
        Map futures = svc.submitToMembers((Callable)new CollectClusterInvariants(this.instance.getCluster().getLocalMember().getUuid()), (Collection)otherMembers);
        for (Map.Entry entry : futures.entrySet()) {
            try {
                return (ClusterInvariants)((Future)entry.getValue()).get();
            }
            catch (InterruptedException | ExecutionException ex) {
                log.warn("Ignoring error getting cluster invariants from {}", entry.getKey(), (Object)ex);
            }
        }
        throw new ClusterException("Failed to get invariants from cluster.");
    }

    public DualLock getLockForName(@NonNull String key) {
        if (this.isClustered()) {
            IMap lockCache = this.instance.getMap(LOCK_CACHE_NAME);
            if (ConfluenceMicrometer.isMicrometerEnabled()) {
                return new MeteredDualLock(new HazelcastDualLock((IMap<String, Serializable>)lockCache, key), this.micrometerRegistry, key);
            }
            return new HazelcastDualLock((IMap<String, Serializable>)lockCache, key);
        }
        return (DualLock)this.nonClusterLock.apply((Object)key);
    }

    public ClusteredLock getClusteredLock(String key) {
        return this.getLockForName(Objects.requireNonNull(key));
    }

    public Lock getLock(String name) {
        return this.getLockForName(Objects.requireNonNull(name));
    }

    @Override
    public HazelcastInstance get() {
        return this.getHazelcastInstance();
    }

    HazelcastInstance getHazelcastInstance() {
        return this.instance;
    }

    public <T> CompletionStage<T> submitToKeyOwner(Callable<T> task, String serviceName, Object key) {
        if (this.isClustered()) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    IExecutorService executorService = this.instance.getExecutorService(serviceName);
                    Future future = executorService.submitToKeyOwner(task, key);
                    return future.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    Throwables.propagateIfInstanceOf((Throwable)e.getCause(), CompletionException.class);
                    throw new CompletionException(e.getCause() == null ? e : e.getCause());
                }
            });
        }
        return this.completableFuture(task);
    }

    public <T> ClusterNodeExecution<T> submitToNode(@Nullable String nodeId, Callable<T> task, String serviceName) throws NoSuchClusterNodeException {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(serviceName, "task");
        if (nodeId == null) {
            return new ClusterNodeExecution(this.getThisNodeInformation(), this.completableFuture(task));
        }
        if (!this.isClustered()) {
            throw new NoSuchClusterNodeException("Invalid node ID: " + nodeId + ", instance not clustered");
        }
        Member member = this.instance.getCluster().getMembers().stream().filter(m -> Objects.equals(nodeId, HazelcastUtils.getMemberId(m))).findFirst().orElseThrow(() -> new NoSuchClusterNodeException("Invalid node ID: " + nodeId));
        IExecutorService svc = this.instance.getExecutorService(serviceName);
        return new ClusterNodeExecution((ClusterNodeInformation)new HazelcastClusterNodeInformation(member), this.completableFuture(svc.submitToMember(task, member)::get));
    }

    public <T> List<ClusterNodeExecution<T>> submitToAllNodes(Callable<T> task, String serviceName) {
        if (this.isClustered()) {
            IExecutorService svc = this.instance.getExecutorService(serviceName);
            Map futures = svc.submitToAllMembers(task);
            return futures.entrySet().stream().map(e -> new ClusterNodeExecution((ClusterNodeInformation)new HazelcastClusterNodeInformation((Member)e.getKey()), this.completableFuture(((Future)e.getValue())::get))).collect(Collectors.toList());
        }
        return Collections.singletonList(new ClusterNodeExecution(null, this.completableFuture(task)));
    }

    private <T> CompletableFuture<T> completableFuture(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public String getSharedSecret() {
        return Optional.ofNullable((String)this.applicationConfig.getProperty((Object)"confluence.cluster.authentication.secret")).orElse("");
    }

    public boolean isNodeAuthEnabled() {
        Object flag = this.applicationConfig.getProperty((Object)"confluence.cluster.authentication.enabled");
        return flag instanceof Boolean ? (Boolean)flag : Boolean.valueOf((String)flag);
    }
}

