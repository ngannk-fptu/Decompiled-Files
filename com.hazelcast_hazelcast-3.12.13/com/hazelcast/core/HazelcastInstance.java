/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
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
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public interface HazelcastInstance {
    public String getName();

    public <E> IQueue<E> getQueue(String var1);

    public <E> ITopic<E> getTopic(String var1);

    public <E> ISet<E> getSet(String var1);

    public <E> IList<E> getList(String var1);

    public <K, V> IMap<K, V> getMap(String var1);

    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String var1);

    public JobTracker getJobTracker(String var1);

    public <K, V> MultiMap<K, V> getMultiMap(String var1);

    @Deprecated
    public ILock getLock(String var1);

    public <E> Ringbuffer<E> getRingbuffer(String var1);

    public <E> ITopic<E> getReliableTopic(String var1);

    public Cluster getCluster();

    public Endpoint getLocalEndpoint();

    public IExecutorService getExecutorService(String var1);

    public DurableExecutorService getDurableExecutorService(String var1);

    public <T> T executeTransaction(TransactionalTask<T> var1) throws TransactionException;

    public <T> T executeTransaction(TransactionOptions var1, TransactionalTask<T> var2) throws TransactionException;

    public TransactionContext newTransactionContext();

    public TransactionContext newTransactionContext(TransactionOptions var1);

    @Deprecated
    public IdGenerator getIdGenerator(String var1);

    public FlakeIdGenerator getFlakeIdGenerator(String var1);

    @Deprecated
    public IAtomicLong getAtomicLong(String var1);

    @Deprecated
    public <E> IAtomicReference<E> getAtomicReference(String var1);

    @Deprecated
    public ICountDownLatch getCountDownLatch(String var1);

    @Deprecated
    public ISemaphore getSemaphore(String var1);

    public Collection<DistributedObject> getDistributedObjects();

    public String addDistributedObjectListener(DistributedObjectListener var1);

    public boolean removeDistributedObjectListener(String var1);

    public Config getConfig();

    public PartitionService getPartitionService();

    public QuorumService getQuorumService();

    public ClientService getClientService();

    public LoggingService getLoggingService();

    public LifecycleService getLifecycleService();

    public <T extends DistributedObject> T getDistributedObject(String var1, String var2);

    public ConcurrentMap<String, Object> getUserContext();

    public HazelcastXAResource getXAResource();

    public ICacheManager getCacheManager();

    public CardinalityEstimator getCardinalityEstimator(String var1);

    public PNCounter getPNCounter(String var1);

    public IScheduledExecutorService getScheduledExecutorService(String var1);

    public CPSubsystem getCPSubsystem();

    public void shutdown();
}

