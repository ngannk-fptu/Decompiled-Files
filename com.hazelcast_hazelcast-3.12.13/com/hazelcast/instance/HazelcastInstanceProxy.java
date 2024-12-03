/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICacheManager;
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
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.TerminatedLifecycleService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

@PrivateApi
public final class HazelcastInstanceProxy
implements HazelcastInstance,
SerializationServiceSupport {
    protected volatile HazelcastInstanceImpl original;
    private final String name;

    protected HazelcastInstanceProxy(HazelcastInstanceImpl original) {
        this.original = original;
        this.name = original.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public <K, V> IMap<K, V> getMap(String name) {
        return this.getOriginal().getMap(name);
    }

    @Override
    public <E> IQueue<E> getQueue(String name) {
        return this.getOriginal().getQueue(name);
    }

    @Override
    public <E> ITopic<E> getTopic(String name) {
        return this.getOriginal().getTopic(name);
    }

    @Override
    public <E> ITopic<E> getReliableTopic(String name) {
        return this.getOriginal().getReliableTopic(name);
    }

    @Override
    public <E> ISet<E> getSet(String name) {
        return this.getOriginal().getSet(name);
    }

    @Override
    public <E> IList<E> getList(String name) {
        return this.getOriginal().getList(name);
    }

    @Override
    public <K, V> MultiMap<K, V> getMultiMap(String name) {
        return this.getOriginal().getMultiMap(name);
    }

    @Override
    public JobTracker getJobTracker(String name) {
        return this.getOriginal().getJobTracker(name);
    }

    @Override
    public <E> Ringbuffer<E> getRingbuffer(String name) {
        return this.getOriginal().getRingbuffer(name);
    }

    @Override
    public ILock getLock(String key) {
        return this.getOriginal().getLock(key);
    }

    @Override
    public IExecutorService getExecutorService(String name) {
        return this.getOriginal().getExecutorService(name);
    }

    @Override
    public DurableExecutorService getDurableExecutorService(String name) {
        return this.getOriginal().getDurableExecutorService(name);
    }

    @Override
    public <T> T executeTransaction(TransactionalTask<T> task) throws TransactionException {
        return this.getOriginal().executeTransaction(task);
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task) throws TransactionException {
        return this.getOriginal().executeTransaction(options, task);
    }

    @Override
    public TransactionContext newTransactionContext() {
        return this.getOriginal().newTransactionContext();
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
        return this.getOriginal().newTransactionContext(options);
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
        return this.getOriginal().getIdGenerator(name);
    }

    @Override
    public FlakeIdGenerator getFlakeIdGenerator(String name) {
        return this.getOriginal().getFlakeIdGenerator(name);
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
        return this.getOriginal().getAtomicLong(name);
    }

    @Override
    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
        return this.getOriginal().getReplicatedMap(name);
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
        return this.getOriginal().getAtomicReference(name);
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
        return this.getOriginal().getCountDownLatch(name);
    }

    @Override
    public ISemaphore getSemaphore(String name) {
        return this.getOriginal().getSemaphore(name);
    }

    @Override
    public ICacheManager getCacheManager() {
        return this.getOriginal().getCacheManager();
    }

    @Override
    public Cluster getCluster() {
        return this.getOriginal().getCluster();
    }

    @Override
    public Member getLocalEndpoint() {
        return this.getOriginal().getLocalEndpoint();
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects() {
        return this.getOriginal().getDistributedObjects();
    }

    @Override
    public Config getConfig() {
        return this.getOriginal().getConfig();
    }

    @Override
    public PartitionService getPartitionService() {
        return this.getOriginal().getPartitionService();
    }

    @Override
    public QuorumService getQuorumService() {
        return this.getOriginal().getQuorumService();
    }

    @Override
    public ClientService getClientService() {
        return this.getOriginal().getClientService();
    }

    @Override
    public LoggingService getLoggingService() {
        return this.getOriginal().getLoggingService();
    }

    @Override
    public LifecycleService getLifecycleService() {
        HazelcastInstanceImpl hz = this.original;
        return hz != null ? hz.getLifecycleService() : new TerminatedLifecycleService();
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, String name) {
        return this.getOriginal().getDistributedObject(serviceName, name);
    }

    @Override
    public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
        return this.getOriginal().addDistributedObjectListener(distributedObjectListener);
    }

    @Override
    public boolean removeDistributedObjectListener(String registrationId) {
        return this.getOriginal().removeDistributedObjectListener(registrationId);
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
        return this.getOriginal().getUserContext();
    }

    @Override
    public HazelcastXAResource getXAResource() {
        return this.getOriginal().getXAResource();
    }

    @Override
    public CardinalityEstimator getCardinalityEstimator(String name) {
        return this.getOriginal().getCardinalityEstimator(name);
    }

    @Override
    public PNCounter getPNCounter(String name) {
        return this.getOriginal().getPNCounter(name);
    }

    @Override
    public IScheduledExecutorService getScheduledExecutorService(String name) {
        return this.getOriginal().getScheduledExecutorService(name);
    }

    @Override
    public CPSubsystem getCPSubsystem() {
        return this.getOriginal().getCPSubsystem();
    }

    @Override
    public void shutdown() {
        this.getLifecycleService().shutdown();
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return this.getOriginal().getSerializationService();
    }

    public HazelcastInstanceImpl getOriginal() {
        HazelcastInstanceImpl hazelcastInstance = this.original;
        if (hazelcastInstance == null) {
            throw new HazelcastInstanceNotActiveException();
        }
        return hazelcastInstance;
    }

    public String toString() {
        HazelcastInstanceImpl hazelcastInstance = this.original;
        if (hazelcastInstance != null) {
            return hazelcastInstance.toString();
        }
        return "HazelcastInstance {NOT ACTIVE}";
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof HazelcastInstance)) {
            return false;
        }
        HazelcastInstance that = (HazelcastInstance)o;
        return !(this.name == null ? that.getName() != null : !this.name.equals(that.getName()));
    }
}

