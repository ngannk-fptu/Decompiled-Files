/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.cluster.ClusterEvent
 *  org.terracotta.toolkit.cluster.ClusterInfo
 *  org.terracotta.toolkit.cluster.ClusterListener
 *  org.terracotta.toolkit.cluster.ClusterNode
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.internal.ToolkitInternal
 *  org.terracotta.toolkit.internal.collections.ToolkitListInternal
 *  org.terracotta.toolkit.rejoin.RejoinException
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.async.AsyncCoordinator;
import org.terracotta.modules.ehcache.async.DefaultAsyncConfig;
import org.terracotta.modules.ehcache.async.ItemProcessor;
import org.terracotta.modules.ehcache.async.ItemsFilter;
import org.terracotta.modules.ehcache.async.LockHolder;
import org.terracotta.modules.ehcache.async.PlatformExceptionUtils;
import org.terracotta.modules.ehcache.async.ProcessingBucket;
import org.terracotta.modules.ehcache.async.scatterpolicies.HashCodeScatterPolicy;
import org.terracotta.modules.ehcache.async.scatterpolicies.ItemScatterPolicy;
import org.terracotta.modules.ehcache.async.scatterpolicies.SingleBucketScatterPolicy;
import org.terracotta.toolkit.cluster.ClusterEvent;
import org.terracotta.toolkit.cluster.ClusterInfo;
import org.terracotta.toolkit.cluster.ClusterListener;
import org.terracotta.toolkit.cluster.ClusterNode;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.internal.collections.ToolkitListInternal;
import org.terracotta.toolkit.rejoin.RejoinException;

public class AsyncCoordinatorImpl<E extends Serializable>
implements AsyncCoordinator<E> {
    private static final String DEAD_NODES = "DEAD_NODES";
    private static final Logger LOGGER = LoggerFactory.getLogger((String)AsyncCoordinatorImpl.class.getName());
    private static final String DELIMITER = "|";
    private static final String NODE_ALIVE_TIMEOUT_PROPERTY_NAME = "ehcache.async.node.alive.timeout";
    private static final String ALIVE_LOCK_SUFFIX = "-alive-lock";
    private final ToolkitLock commonAsyncLock;
    private final Lock nodeWriteLock;
    private final Lock nodeReadLock;
    private volatile Status status = Status.UNINITIALIZED;
    private final long aliveTimeoutSec;
    private final List<ProcessingBucket<E>> localBuckets;
    private final List<ProcessingBucket<E>> deadBuckets;
    private final String name;
    private final String cacheName;
    private final ToolkitInstanceFactory toolkitInstanceFactory;
    private final AsyncConfig config;
    private ItemScatterPolicy<? super E> scatterPolicy;
    private ItemsFilter<E> filter;
    private final ClusterInfo cluster;
    private volatile String nodeName;
    private final ToolkitInternal toolkit;
    private ItemProcessor<E> processor;
    private final AsyncClusterListener listener;
    private final Callback asyncFactoryCallback;
    private final BucketManager bucketManager;
    private volatile ClusterNode currentNode;
    private volatile int concurrency = 1;
    private final LockHolder lockHolder;

    public AsyncCoordinatorImpl(String fullAsyncName, String cacheName, AsyncConfig config, ToolkitInstanceFactory toolkitInstanceFactory, Callback asyncFactoryCallback) {
        this.name = fullAsyncName;
        this.cacheName = cacheName;
        this.config = null == config ? DefaultAsyncConfig.getInstance() : config;
        this.toolkitInstanceFactory = toolkitInstanceFactory;
        this.toolkit = (ToolkitInternal)toolkitInstanceFactory.getToolkit();
        this.aliveTimeoutSec = this.toolkit.getProperties().getLong(NODE_ALIVE_TIMEOUT_PROPERTY_NAME, Long.valueOf(5L));
        this.cluster = this.toolkit.getClusterInfo();
        this.listener = new AsyncClusterListener();
        this.currentNode = this.cluster.getCurrentNode();
        this.nodeName = AsyncCoordinatorImpl.getAsyncNodeName(this.name, this.currentNode);
        this.localBuckets = new ArrayList<ProcessingBucket<E>>();
        this.deadBuckets = new ArrayList<ProcessingBucket<E>>();
        this.bucketManager = new BucketManager();
        this.commonAsyncLock = this.toolkit.getLock(this.name);
        ReentrantReadWriteLock nodeLock = new ReentrantReadWriteLock();
        this.nodeWriteLock = nodeLock.writeLock();
        this.nodeReadLock = nodeLock.readLock();
        this.asyncFactoryCallback = asyncFactoryCallback;
        this.lockHolder = new LockHolder();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start(ItemProcessor<E> itemProcessor, int processingConcurrency, ItemScatterPolicy<? super E> policy) {
        this.validateArgs(itemProcessor, processingConcurrency);
        this.nodeWriteLock.lock();
        try {
            if (this.status == Status.STARTED) {
                LOGGER.warn("AsyncCoordinator " + this.name + " already started");
                return;
            }
            if (this.status != Status.UNINITIALIZED) {
                throw new IllegalStateException();
            }
            this.concurrency = processingConcurrency;
            this.scatterPolicy = AsyncCoordinatorImpl.getPolicy(policy, this.concurrency);
            this.processor = itemProcessor;
            this.cluster.addClusterListener((ClusterListener)this.listener);
            this.startBuckets(this.concurrency);
            this.status = Status.STARTED;
        }
        finally {
            this.nodeWriteLock.unlock();
        }
        this.processDeadNodes();
    }

    private void processDeadNodes() {
        this.bucketManager.scanAndAddDeadNodes();
        this.processOneDeadNodeIfNecessary();
    }

    private void validateArgs(ItemProcessor<E> itemProcessor, int processingConcurrency) {
        if (null == itemProcessor) {
            throw new IllegalArgumentException("processor can't be null");
        }
        if (processingConcurrency < 1) {
            throw new IllegalArgumentException("processingConcurrency needs to be at least 1");
        }
    }

    private static <F extends Serializable> ItemScatterPolicy<? super F> getPolicy(ItemScatterPolicy<? super F> policy, int processingConcurrency) {
        if (null == policy) {
            policy = 1 == processingConcurrency ? new SingleBucketScatterPolicy<F>() : new HashCodeScatterPolicy<F>();
        }
        return policy;
    }

    private long startDeadBuckets(Set<String> oldListNames) {
        long totalItems = 0L;
        for (String bucketName : oldListNames) {
            ProcessingBucket<E> bucket = this.createBucket(bucketName, this.config, true);
            this.deadBuckets.add(bucket);
            totalItems += (long)bucket.getWaitCount();
            bucket.start();
        }
        return totalItems;
    }

    private String getAliveLockName(String node) {
        return node + ALIVE_LOCK_SUFFIX;
    }

    private boolean tryLockNodeAlive(String otherNodeName) {
        try {
            return this.toolkit.getLock(this.getAliveLockName(otherNodeName)).tryLock(this.aliveTimeoutSec, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void startBuckets(int processingConcurrency) {
        this.lockHolder.hold(this.toolkit.getLock(this.getAliveLockName(this.nodeName)));
        HashSet<String> nameList = new HashSet<String>();
        for (int i = 0; i < processingConcurrency; ++i) {
            String string = this.nodeName + DELIMITER + i;
            nameList.add(string);
        }
        this.bucketManager.bucketsCreated(nameList);
        for (String string : nameList) {
            ProcessingBucket<E> bucket = this.createBucket(string, this.config, false);
            this.localBuckets.add(bucket);
            bucket.start();
        }
    }

    private ProcessingBucket<E> createBucket(String bucketName, AsyncConfig processingConfig, boolean workingOnDeadBucket) {
        ToolkitListInternal toolkitList = this.toolkitInstanceFactory.getAsyncProcessingBucket(bucketName, this.cacheName);
        if (!workingOnDeadBucket && toolkitList.size() > 0) {
            throw new AssertionError((Object)"List created should not have size greater than 0");
        }
        ProcessingBucket<E> bucket = new ProcessingBucket<E>(bucketName, processingConfig, toolkitList, this.cluster, this.processor, workingOnDeadBucket);
        bucket.setItemsFilter(this.filter);
        if (workingOnDeadBucket) {
            bucket.setCleanupCallback(this.cleanupDeadBucket(this.deadBuckets, bucket));
        }
        return bucket;
    }

    private Callback cleanupDeadBucket(final List<ProcessingBucket<E>> list, final ProcessingBucket<E> bucket) {
        return new Callback(){

            @Override
            public void callback() {
                AsyncCoordinatorImpl.this.nodeWriteLock.lock();
                try {
                    bucket.destroy();
                    list.remove(bucket);
                    AsyncCoordinatorImpl.this.bucketManager.removeBucket(bucket.getBucketName());
                }
                catch (Throwable t) {
                    if (PlatformExceptionUtils.shouldIgnore(t)) {
                        LOGGER.warn("cleanupDeadBucket caught " + t);
                    } else {
                        LOGGER.error("cleanupDeadBucket caught ", t);
                    }
                }
                finally {
                    AsyncCoordinatorImpl.this.nodeWriteLock.unlock();
                }
                AsyncCoordinatorImpl.this.processOneDeadNodeIfNecessary();
            }
        };
    }

    @Override
    public void add(E item) {
        if (null == item) {
            return;
        }
        this.nodeWriteLock.lock();
        try {
            this.status.checkRunning();
            this.addtoBucket(item);
        }
        finally {
            this.nodeWriteLock.unlock();
        }
    }

    private void addtoBucket(E item) {
        int index = this.scatterPolicy.selectBucket(this.localBuckets.size(), item);
        ProcessingBucket<E> bucket = this.localBuckets.get(index);
        bucket.add(item);
    }

    @Override
    public void stop() {
        this.nodeWriteLock.lock();
        try {
            this.status.checkRunning();
            this.status = Status.STOPPED;
            this.stopBuckets(this.localBuckets);
            this.stopBuckets(this.deadBuckets);
            this.cluster.removeClusterListener((ClusterListener)this.listener);
            this.bucketManager.clear();
            this.asyncFactoryCallback.callback();
            this.lockHolder.release(this.toolkit.getLock(this.getAliveLockName(this.nodeName)));
        }
        finally {
            this.nodeWriteLock.unlock();
        }
    }

    private void stopBuckets(List<ProcessingBucket<E>> buckets) {
        for (ProcessingBucket<E> bucket : buckets) {
            bucket.stop();
        }
        buckets.clear();
    }

    private void stopNow() {
        this.debug("stopNow localBuckets " + this.localBuckets.size() + " | deadBuckets " + this.deadBuckets.size());
        this.nodeWriteLock.lock();
        try {
            this.stopBucketsNow(this.localBuckets);
            this.stopBucketsNow(this.deadBuckets);
        }
        finally {
            this.nodeWriteLock.unlock();
        }
    }

    private void nodeRejoined() {
        this.nodeWriteLock.lock();
        try {
            this.currentNode = this.cluster.getCurrentNode();
            this.nodeName = AsyncCoordinatorImpl.getAsyncNodeName(this.name, this.currentNode);
            this.debug("nodeRejoined currentNode " + this.currentNode + " nodeName " + this.nodeName);
            this.localBuckets.clear();
            this.deadBuckets.clear();
            this.lockHolder.reset();
            this.startBuckets(this.concurrency);
        }
        finally {
            this.nodeWriteLock.unlock();
        }
        this.processDeadNodes();
    }

    private void stopBucketsNow(List<ProcessingBucket<E>> buckets) {
        for (ProcessingBucket<E> bucket : buckets) {
            bucket.stopNow();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOperationsFilter(ItemsFilter<E> filter) {
        this.nodeWriteLock.lock();
        try {
            this.filter = filter;
            for (ProcessingBucket<E> bucket : this.localBuckets) {
                bucket.setItemsFilter(filter);
            }
        }
        finally {
            this.nodeWriteLock.unlock();
        }
    }

    private void processOneDeadNodeIfNecessary() {
        this.nodeWriteLock.lock();
        try {
            if (this.status == Status.STARTED && this.deadBuckets.isEmpty()) {
                this.processOneDeadNode();
            } else {
                this.debug("skipped processOneDeadNode status " + this.status + " deadBuckets " + this.deadBuckets.size());
            }
        }
        finally {
            this.nodeWriteLock.unlock();
        }
    }

    private void processOneDeadNode() {
        Set<String> deadNodeBuckets = Collections.EMPTY_SET;
        this.commonAsyncLock.lock();
        try {
            deadNodeBuckets = this.bucketManager.transferBucketsFromDeadNode();
        }
        finally {
            this.commonAsyncLock.unlock();
        }
        if (!deadNodeBuckets.isEmpty()) {
            long totalItems = this.startDeadBuckets(deadNodeBuckets);
            this.debug("processOneDeadNode deadNodeBuckets " + deadNodeBuckets.size() + " totalItems " + totalItems + " at " + this.nodeName);
        }
    }

    private void debug(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(message);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getQueueSize() {
        long size = 0L;
        this.nodeReadLock.lock();
        try {
            this.status.checkRunning();
            for (ProcessingBucket<E> bucket : this.localBuckets) {
                size += (long)bucket.getWaitCount();
            }
            for (ProcessingBucket<E> bucket : this.deadBuckets) {
                size += (long)bucket.getWaitCount();
            }
            long l = size;
            return l;
        }
        finally {
            this.nodeReadLock.unlock();
        }
    }

    @Override
    public void destroy() {
        this.commonAsyncLock.lock();
        try {
            for (String bucketName : this.bucketManager.getAllBuckets()) {
                this.toolkit.getList(bucketName, null).destroy();
            }
            this.bucketManager.destroy();
        }
        finally {
            this.commonAsyncLock.unlock();
        }
    }

    private static String getAsyncNodeName(String name, ClusterNode node) {
        String nodeId = node.getId();
        if (nodeId == null || nodeId.isEmpty()) {
            throw new AssertionError((Object)("nodeId cannot be " + nodeId));
        }
        return name + DELIMITER + node.getId();
    }

    private class BucketManager {
        private final ToolkitMap<String, Set<String>> nodeToBucketNames;

        public BucketManager() {
            this.nodeToBucketNames = AsyncCoordinatorImpl.this.toolkitInstanceFactory.getOrCreateAsyncListNamesMap(AsyncCoordinatorImpl.this.name, AsyncCoordinatorImpl.this.cacheName);
            this.nodeToBucketNames.putIfAbsent((Object)AsyncCoordinatorImpl.DEAD_NODES, new HashSet());
        }

        private void bucketsCreated(Set<String> bucketNames) {
            Set prev = (Set)this.nodeToBucketNames.put((Object)AsyncCoordinatorImpl.this.nodeName, bucketNames);
            if (prev != null) {
                throw new AssertionError((Object)("previous value " + prev + " not null for " + AsyncCoordinatorImpl.this.nodeName));
            }
        }

        private void clear() {
            this.nodeToBucketNames.remove((Object)AsyncCoordinatorImpl.this.nodeName);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void removeBucket(String bucketName) {
            AsyncCoordinatorImpl.this.commonAsyncLock.lock();
            try {
                Set buckets = (Set)this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.this.nodeName);
                if (buckets != null) {
                    boolean removed = buckets.remove(bucketName);
                    this.nodeToBucketNames.put((Object)AsyncCoordinatorImpl.this.nodeName, (Object)buckets);
                    AsyncCoordinatorImpl.this.debug("removeBucket " + bucketName + " " + removed + " remaining deadNodes " + this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.DEAD_NODES));
                }
            }
            finally {
                AsyncCoordinatorImpl.this.commonAsyncLock.unlock();
            }
        }

        private Set<String> transferBucketsFromDeadNode() {
            String deadNode = this.getOneDeadNode();
            while (deadNode != null) {
                Set deadNodeBuckets = (Set)this.nodeToBucketNames.get((Object)deadNode);
                if (deadNodeBuckets != null) {
                    Set newOwner = (Set)this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.this.nodeName);
                    newOwner.addAll(deadNodeBuckets);
                    this.nodeToBucketNames.put((Object)AsyncCoordinatorImpl.this.nodeName, (Object)newOwner);
                    this.nodeToBucketNames.remove((Object)deadNode);
                    AsyncCoordinatorImpl.this.debug("transferBucketsFromDeadNode deadNode " + deadNode + " to node " + AsyncCoordinatorImpl.this.nodeName + " buckets " + newOwner + " remaining deadNodes " + this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.DEAD_NODES));
                    return deadNodeBuckets;
                }
                deadNode = this.getOneDeadNode();
            }
            return Collections.EMPTY_SET;
        }

        private String getOneDeadNode() {
            String deadNode = null;
            Set deadNodes = (Set)this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.DEAD_NODES);
            Iterator itr = deadNodes.iterator();
            if (itr.hasNext()) {
                deadNode = (String)itr.next();
                itr.remove();
                this.nodeToBucketNames.put((Object)AsyncCoordinatorImpl.DEAD_NODES, (Object)deadNodes);
            }
            return deadNode;
        }

        private Set<String> getAllNodes() {
            HashSet<String> nodes = new HashSet<String>(this.nodeToBucketNames.keySet());
            nodes.remove(AsyncCoordinatorImpl.DEAD_NODES);
            return nodes;
        }

        private void addToDeadNodes(Collection<String> nodes) {
            Set allDeadNodes;
            if (!nodes.isEmpty() && (allDeadNodes = (Set)this.nodeToBucketNames.get((Object)AsyncCoordinatorImpl.DEAD_NODES)).addAll(nodes)) {
                this.nodeToBucketNames.put((Object)AsyncCoordinatorImpl.DEAD_NODES, (Object)allDeadNodes);
                AsyncCoordinatorImpl.this.debug(AsyncCoordinatorImpl.this.nodeName + " addToDeadNodes deadNodes " + nodes + " allDeadNodes " + allDeadNodes);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void scanAndAddDeadNodes() {
            AsyncCoordinatorImpl.this.commonAsyncLock.lock();
            try {
                Set<String> nodesFromMap = this.getAllNodes();
                Set<String> clusterNodes = this.getClusterNodes();
                nodesFromMap.removeAll(clusterNodes);
                try {
                    Iterator<String> itr = nodesFromMap.iterator();
                    while (itr.hasNext()) {
                        String deadNode = itr.next();
                        if (AsyncCoordinatorImpl.this.tryLockNodeAlive(deadNode)) continue;
                        itr.remove();
                    }
                    this.addToDeadNodes(nodesFromMap);
                }
                finally {
                    for (String node : nodesFromMap) {
                        String aliveLockName = AsyncCoordinatorImpl.this.getAliveLockName(node);
                        try {
                            AsyncCoordinatorImpl.this.toolkit.getLock(aliveLockName).unlock();
                        }
                        catch (RejoinException e) {
                            LOGGER.debug("Unable to release lock for dead " + node + " [" + aliveLockName + "]", (Throwable)e);
                        }
                        catch (Exception e) {
                            LOGGER.warn("Unable to release lock for dead " + node + " [" + aliveLockName + "]", (Throwable)e);
                        }
                    }
                }
            }
            finally {
                AsyncCoordinatorImpl.this.commonAsyncLock.unlock();
            }
        }

        private Set<String> getClusterNodes() {
            HashSet<String> nodes = new HashSet<String>();
            for (ClusterNode node : AsyncCoordinatorImpl.this.cluster.getNodes()) {
                nodes.add(AsyncCoordinatorImpl.getAsyncNodeName(AsyncCoordinatorImpl.this.name, node));
            }
            return nodes;
        }

        private Set<String> getAllBuckets() {
            HashSet<String> buckets = new HashSet<String>();
            for (String node : this.getAllNodes()) {
                buckets.addAll((Collection)this.nodeToBucketNames.get((Object)node));
            }
            return buckets;
        }

        void destroy() {
            this.nodeToBucketNames.destroy();
        }
    }

    public static interface Callback {
        public void callback();
    }

    private static enum Status {
        UNINITIALIZED,
        STARTED,
        STOPPED{

            @Override
            final void checkRunning() {
                throw new IllegalStateException("AsyncCoordinator is " + this.name().toLowerCase() + "!");
            }
        };


        void checkRunning() {
        }
    }

    private class AsyncClusterListener
    implements ClusterListener {
        private AsyncClusterListener() {
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void onClusterEvent(ClusterEvent event) {
            AsyncCoordinatorImpl.this.debug("onClusterEvent " + event.getType() + " for " + event.getNode().getId() + " received at " + AsyncCoordinatorImpl.this.currentNode.getId());
            switch (event.getType()) {
                case NODE_LEFT: {
                    if (event.getNode().equals(AsyncCoordinatorImpl.this.currentNode)) {
                        AsyncCoordinatorImpl.this.stopNow();
                        return;
                    }
                    String leftNodeKey = AsyncCoordinatorImpl.getAsyncNodeName(AsyncCoordinatorImpl.this.name, event.getNode());
                    AsyncCoordinatorImpl.this.commonAsyncLock.lock();
                    try {
                        AsyncCoordinatorImpl.this.bucketManager.addToDeadNodes(Collections.singleton(leftNodeKey));
                    }
                    finally {
                        AsyncCoordinatorImpl.this.commonAsyncLock.unlock();
                    }
                    AsyncCoordinatorImpl.this.processOneDeadNodeIfNecessary();
                    return;
                }
                case NODE_REJOINED: {
                    AsyncCoordinatorImpl.this.nodeRejoined();
                    return;
                }
            }
        }
    }
}

