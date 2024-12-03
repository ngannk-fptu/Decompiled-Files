/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.client.impl.ClientServiceProxy;
import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.internal.CPSubsystemImpl;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.instance.HazelcastInstanceCacheManager;
import com.hazelcast.instance.HazelcastManagedContext;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeContext;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@PrivateApi
public class HazelcastInstanceImpl
implements HazelcastInstance,
SerializationServiceSupport {
    public final Node node;
    final ConcurrentMap<String, Object> userContext = new ConcurrentHashMap<String, Object>();
    final ILogger logger;
    final String name;
    final ManagementService managementService;
    final LifecycleServiceImpl lifecycleService;
    final CPSubsystemImpl cpSubsystem;
    final ManagedContext managedContext;
    final HazelcastInstanceCacheManager hazelcastCacheManager;

    protected HazelcastInstanceImpl(String name, Config config, NodeContext nodeContext) {
        this.name = name;
        this.lifecycleService = new LifecycleServiceImpl(this);
        this.cpSubsystem = new CPSubsystemImpl(this);
        ManagedContext configuredManagedContext = config.getManagedContext();
        this.managedContext = new HazelcastManagedContext(this, configuredManagedContext);
        this.userContext.putAll(config.getUserContext());
        this.node = this.createNode(config, nodeContext);
        try {
            this.logger = this.node.getLogger(this.getClass().getName());
            this.node.start();
            if (!this.node.isRunning()) {
                throw new IllegalStateException("Node failed to start!");
            }
            this.managementService = this.node.getNodeExtension().createJMXManagementService(this);
            this.initManagedContext(configuredManagedContext);
            this.hazelcastCacheManager = new HazelcastInstanceCacheManager(this);
            ClassLoader classLoader = this.node.getConfigClassLoader();
            if (classLoader instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)classLoader)).setHazelcastInstance(this);
            }
        }
        catch (Throwable e) {
            try {
                this.node.shutdown(true);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
            throw ExceptionUtil.rethrow(e);
        }
    }

    protected Node createNode(Config config, NodeContext nodeContext) {
        return new Node(this, config, nodeContext);
    }

    private void initManagedContext(ManagedContext configuredManagedContext) {
        if (configuredManagedContext != null && configuredManagedContext instanceof HazelcastInstanceAware) {
            ((HazelcastInstanceAware)((Object)configuredManagedContext)).setHazelcastInstance(this);
        }
    }

    public ManagementService getManagementService() {
        return this.managementService;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public <K, V> IMap<K, V> getMap(String name) {
        Preconditions.checkNotNull(name, "Retrieving a map instance with a null name is not allowed!");
        return (IMap)this.getDistributedObject("hz:impl:mapService", name);
    }

    @Override
    public <E> IQueue<E> getQueue(String name) {
        Preconditions.checkNotNull(name, "Retrieving a queue instance with a null name is not allowed!");
        return (IQueue)this.getDistributedObject("hz:impl:queueService", name);
    }

    @Override
    public <E> ITopic<E> getTopic(String name) {
        Preconditions.checkNotNull(name, "Retrieving a topic instance with a null name is not allowed!");
        return (ITopic)this.getDistributedObject("hz:impl:topicService", name);
    }

    @Override
    public <E> ITopic<E> getReliableTopic(String name) {
        Preconditions.checkNotNull(name, "Retrieving a topic instance with a null name is not allowed!");
        return (ITopic)this.getDistributedObject("hz:impl:reliableTopicService", name);
    }

    @Override
    public <E> ISet<E> getSet(String name) {
        Preconditions.checkNotNull(name, "Retrieving a set instance with a null name is not allowed!");
        return (ISet)this.getDistributedObject("hz:impl:setService", name);
    }

    @Override
    public <E> IList<E> getList(String name) {
        Preconditions.checkNotNull(name, "Retrieving a list instance with a null name is not allowed!");
        return (IList)this.getDistributedObject("hz:impl:listService", name);
    }

    @Override
    public <K, V> MultiMap<K, V> getMultiMap(String name) {
        Preconditions.checkNotNull(name, "Retrieving a multi-map instance with a null name is not allowed!");
        return (MultiMap)this.getDistributedObject("hz:impl:multiMapService", name);
    }

    @Override
    public JobTracker getJobTracker(String name) {
        Preconditions.checkNotNull(name, "Retrieving a job tracker instance with a null name is not allowed!");
        return (JobTracker)this.getDistributedObject("hz:impl:mapReduceService", name);
    }

    @Override
    public <E> Ringbuffer<E> getRingbuffer(String name) {
        Preconditions.checkNotNull(name, "Retrieving a ringbuffer instance with a null name is not allowed!");
        return (Ringbuffer)this.getDistributedObject("hz:impl:ringbufferService", name);
    }

    @Override
    public ILock getLock(String key) {
        Preconditions.checkNotNull(key, "Retrieving a lock instance with a null key is not allowed!");
        return (ILock)this.getDistributedObject("hz:impl:lockService", key);
    }

    @Override
    public <T> T executeTransaction(TransactionalTask<T> task) throws TransactionException {
        return this.executeTransaction(TransactionOptions.getDefault(), task);
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task) throws TransactionException {
        TransactionManagerService transactionManagerService = this.node.getNodeEngine().getTransactionManagerService();
        return transactionManagerService.executeTransaction(options, task);
    }

    @Override
    public TransactionContext newTransactionContext() {
        return this.newTransactionContext(TransactionOptions.getDefault());
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
        TransactionManagerService transactionManagerService = this.node.getNodeEngine().getTransactionManagerService();
        return transactionManagerService.newTransactionContext(options);
    }

    @Override
    public IExecutorService getExecutorService(String name) {
        Preconditions.checkNotNull(name, "Retrieving an executor instance with a null name is not allowed!");
        return (IExecutorService)this.getDistributedObject("hz:impl:executorService", name);
    }

    @Override
    public DurableExecutorService getDurableExecutorService(String name) {
        Preconditions.checkNotNull(name, "Retrieving a durable executor instance with a null name is not allowed!");
        return (DurableExecutorService)this.getDistributedObject("hz:impl:durableExecutorService", name);
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
        Preconditions.checkNotNull(name, "Retrieving an ID-generator instance with a null name is not allowed!");
        return (IdGenerator)this.getDistributedObject("hz:impl:idGeneratorService", name);
    }

    @Override
    public FlakeIdGenerator getFlakeIdGenerator(String name) {
        Preconditions.checkNotNull(name, "Retrieving a Flake ID-generator instance with a null name is not allowed!");
        return (FlakeIdGenerator)this.getDistributedObject("hz:impl:flakeIdGeneratorService", name);
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
        Preconditions.checkNotNull(name, "Retrieving an atomic-long instance with a null name is not allowed!");
        return (IAtomicLong)this.getDistributedObject("hz:impl:atomicLongService", name);
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
        Preconditions.checkNotNull(name, "Retrieving an atomic-reference instance with a null name is not allowed!");
        return (IAtomicReference)this.getDistributedObject("hz:impl:atomicReferenceService", name);
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
        Preconditions.checkNotNull(name, "Retrieving a countdown-latch instance with a null name is not allowed!");
        return (ICountDownLatch)this.getDistributedObject("hz:impl:countDownLatchService", name);
    }

    @Override
    public ISemaphore getSemaphore(String name) {
        Preconditions.checkNotNull(name, "Retrieving a semaphore instance with a null name is not allowed!");
        return (ISemaphore)this.getDistributedObject("hz:impl:semaphoreService", name);
    }

    @Override
    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
        Preconditions.checkNotNull(name, "Retrieving a replicated map instance with a null name is not allowed!");
        return (ReplicatedMap)this.getDistributedObject("hz:impl:replicatedMapService", name);
    }

    @Override
    public HazelcastInstanceCacheManager getCacheManager() {
        return this.hazelcastCacheManager;
    }

    @Override
    public Cluster getCluster() {
        return this.node.getClusterService();
    }

    @Override
    public Member getLocalEndpoint() {
        return this.node.getLocalMember();
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects() {
        InternalProxyService proxyService = this.node.getNodeEngine().getProxyService();
        return proxyService.getAllDistributedObjects();
    }

    @Override
    public Config getConfig() {
        return this.node.getConfig();
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
        return this.userContext;
    }

    @Override
    public PartitionService getPartitionService() {
        return this.node.getPartitionService().getPartitionServiceProxy();
    }

    @Override
    public QuorumService getQuorumService() {
        return this.node.getNodeEngine().getQuorumService();
    }

    @Override
    public ClientService getClientService() {
        return new ClientServiceProxy(this.node);
    }

    @Override
    public LoggingService getLoggingService() {
        return this.node.getLoggingService();
    }

    @Override
    public LifecycleServiceImpl getLifecycleService() {
        return this.lifecycleService;
    }

    @Override
    public void shutdown() {
        this.getLifecycleService().shutdown();
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, String name) {
        InternalProxyService proxyService = this.node.getNodeEngine().getProxyService();
        return (T)proxyService.getDistributedObject(serviceName, name);
    }

    @Override
    public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
        InternalProxyService proxyService = this.node.getNodeEngine().getProxyService();
        return proxyService.addProxyListener(distributedObjectListener);
    }

    @Override
    public boolean removeDistributedObjectListener(String registrationId) {
        InternalProxyService proxyService = this.node.getNodeEngine().getProxyService();
        return proxyService.removeProxyListener(registrationId);
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return this.node.getSerializationService();
    }

    public MemoryStats getMemoryStats() {
        return this.node.getNodeExtension().getMemoryStats();
    }

    @Override
    public HazelcastXAResource getXAResource() {
        return (HazelcastXAResource)this.getDistributedObject("hz:impl:xaService", "hz:impl:xaService");
    }

    @Override
    public CardinalityEstimator getCardinalityEstimator(String name) {
        Preconditions.checkNotNull(name, "Retrieving a cardinality estimator instance with a null name is not allowed!");
        return (CardinalityEstimator)this.getDistributedObject("hz:impl:cardinalityEstimatorService", name);
    }

    @Override
    public PNCounter getPNCounter(String name) {
        Preconditions.checkNotNull(name, "Retrieving a PN counter instance with a null name is not allowed!");
        return (PNCounter)this.getDistributedObject("hz:impl:PNCounterService", name);
    }

    @Override
    public IScheduledExecutorService getScheduledExecutorService(String name) {
        Preconditions.checkNotNull(name, "Retrieving a scheduled executor instance with a null name is not allowed!");
        return (IScheduledExecutorService)this.getDistributedObject("hz:impl:scheduledExecutorService", name);
    }

    @Override
    public CPSubsystem getCPSubsystem() {
        if (this.node.getClusterService().getClusterVersion().isLessThan(Versions.V3_12)) {
            throw new UnsupportedOperationException("CP Subsystem is not available before version 3.12!");
        }
        return this.cpSubsystem;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HazelcastInstance)) {
            return false;
        }
        HazelcastInstance that = (HazelcastInstance)o;
        return !(this.name == null ? that.getName() != null : !this.name.equals(that.getName()));
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }

    public String toString() {
        return "HazelcastInstance{name='" + this.name + "', node=" + this.node.getThisAddress() + '}';
    }
}

