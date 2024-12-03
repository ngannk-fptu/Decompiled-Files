/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastInstance;
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
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import com.hazelcast.util.StringUtil;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

class HazelcastOSGiInstanceImpl
implements HazelcastOSGiInstance {
    private final HazelcastInstance delegatedInstance;
    private final HazelcastOSGiService ownerService;

    public HazelcastOSGiInstanceImpl(HazelcastInstance delegatedInstance, HazelcastOSGiService ownerService) {
        this.delegatedInstance = delegatedInstance;
        this.ownerService = ownerService;
    }

    @Override
    public String getName() {
        return this.delegatedInstance.getName();
    }

    @Override
    public <E> IQueue<E> getQueue(String name) {
        return this.delegatedInstance.getQueue(name);
    }

    @Override
    public <E> ITopic<E> getTopic(String name) {
        return this.delegatedInstance.getTopic(name);
    }

    @Override
    public <E> ISet<E> getSet(String name) {
        return this.delegatedInstance.getSet(name);
    }

    @Override
    public <E> IList<E> getList(String name) {
        return this.delegatedInstance.getList(name);
    }

    @Override
    public <K, V> IMap<K, V> getMap(String name) {
        return this.delegatedInstance.getMap(name);
    }

    @Override
    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
        return this.delegatedInstance.getReplicatedMap(name);
    }

    @Override
    public JobTracker getJobTracker(String name) {
        return this.delegatedInstance.getJobTracker(name);
    }

    @Override
    public <K, V> MultiMap<K, V> getMultiMap(String name) {
        return this.delegatedInstance.getMultiMap(name);
    }

    @Override
    public ILock getLock(String key) {
        return this.delegatedInstance.getLock(key);
    }

    @Override
    public <E> Ringbuffer<E> getRingbuffer(String name) {
        return this.delegatedInstance.getRingbuffer(name);
    }

    @Override
    public <E> ITopic<E> getReliableTopic(String name) {
        return this.delegatedInstance.getReliableTopic(name);
    }

    @Override
    public ICacheManager getCacheManager() {
        return this.delegatedInstance.getCacheManager();
    }

    @Override
    public Cluster getCluster() {
        return this.delegatedInstance.getCluster();
    }

    @Override
    public Endpoint getLocalEndpoint() {
        return this.delegatedInstance.getLocalEndpoint();
    }

    @Override
    public IExecutorService getExecutorService(String name) {
        return this.delegatedInstance.getExecutorService(name);
    }

    @Override
    public DurableExecutorService getDurableExecutorService(String name) {
        return this.delegatedInstance.getDurableExecutorService(name);
    }

    @Override
    public <T> T executeTransaction(TransactionalTask<T> task) throws TransactionException {
        return this.delegatedInstance.executeTransaction(task);
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task) throws TransactionException {
        return this.delegatedInstance.executeTransaction(options, task);
    }

    @Override
    public TransactionContext newTransactionContext() {
        return this.delegatedInstance.newTransactionContext();
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
        return this.delegatedInstance.newTransactionContext(options);
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
        return this.delegatedInstance.getIdGenerator(name);
    }

    @Override
    public FlakeIdGenerator getFlakeIdGenerator(String name) {
        return this.delegatedInstance.getFlakeIdGenerator(name);
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
        return this.delegatedInstance.getAtomicLong(name);
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
        return this.delegatedInstance.getAtomicReference(name);
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
        return this.delegatedInstance.getCountDownLatch(name);
    }

    @Override
    public ISemaphore getSemaphore(String name) {
        return this.delegatedInstance.getSemaphore(name);
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects() {
        return this.delegatedInstance.getDistributedObjects();
    }

    @Override
    public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
        return this.delegatedInstance.addDistributedObjectListener(distributedObjectListener);
    }

    @Override
    public boolean removeDistributedObjectListener(String registrationId) {
        return this.delegatedInstance.removeDistributedObjectListener(registrationId);
    }

    @Override
    public Config getConfig() {
        return this.delegatedInstance.getConfig();
    }

    @Override
    public PartitionService getPartitionService() {
        return this.delegatedInstance.getPartitionService();
    }

    @Override
    public QuorumService getQuorumService() {
        return this.delegatedInstance.getQuorumService();
    }

    @Override
    public ClientService getClientService() {
        return this.delegatedInstance.getClientService();
    }

    @Override
    public LoggingService getLoggingService() {
        return this.delegatedInstance.getLoggingService();
    }

    @Override
    public LifecycleService getLifecycleService() {
        return this.delegatedInstance.getLifecycleService();
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, String name) {
        return this.delegatedInstance.getDistributedObject(serviceName, name);
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
        return this.delegatedInstance.getUserContext();
    }

    @Override
    public HazelcastXAResource getXAResource() {
        return this.delegatedInstance.getXAResource();
    }

    @Override
    public CardinalityEstimator getCardinalityEstimator(String name) {
        return this.delegatedInstance.getCardinalityEstimator(name);
    }

    @Override
    public PNCounter getPNCounter(String name) {
        return this.delegatedInstance.getPNCounter(name);
    }

    @Override
    public IScheduledExecutorService getScheduledExecutorService(String name) {
        return this.delegatedInstance.getScheduledExecutorService(name);
    }

    @Override
    public CPSubsystem getCPSubsystem() {
        return this.delegatedInstance.getCPSubsystem();
    }

    @Override
    public void shutdown() {
        this.delegatedInstance.shutdown();
    }

    @Override
    public HazelcastInstance getDelegatedInstance() {
        return this.delegatedInstance;
    }

    @Override
    public HazelcastOSGiService getOwnerService() {
        return this.ownerService;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HazelcastOSGiInstanceImpl that = (HazelcastOSGiInstanceImpl)o;
        if (!this.delegatedInstance.equals(that.delegatedInstance)) {
            return false;
        }
        return this.ownerService.equals(that.ownerService);
    }

    public int hashCode() {
        int result = this.ownerService.hashCode();
        result = 31 * result + this.delegatedInstance.hashCode();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HazelcastOSGiInstanceImpl");
        sb.append("{delegatedInstance='").append(this.delegatedInstance).append('\'');
        Config config = this.getConfig();
        GroupConfig groupConfig = config.getGroupConfig();
        if (groupConfig != null && !StringUtil.isNullOrEmpty(groupConfig.getName())) {
            sb.append(", groupName=").append(groupConfig.getName());
        }
        sb.append(", ownerServiceId=").append(this.ownerService.getId());
        sb.append('}');
        return sb.toString();
    }
}

