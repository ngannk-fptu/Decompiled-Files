/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.cluster.ClusterInfo
 *  org.terracotta.toolkit.internal.collections.ToolkitListInternal
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.async.AsyncCoordinatorImpl;
import org.terracotta.modules.ehcache.async.ItemProcessor;
import org.terracotta.modules.ehcache.async.ItemsFilter;
import org.terracotta.modules.ehcache.async.exceptions.ProcessingException;
import org.terracotta.toolkit.cluster.ClusterInfo;
import org.terracotta.toolkit.internal.collections.ToolkitListInternal;

public class ProcessingBucket<E extends Serializable> {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)ProcessingBucket.class.getName());
    private static final int UNLIMITED_QUEUE_SIZE = 0;
    private static final String threadNamePrefix = "ProcessingWorker|";
    private final String bucketName;
    private final AsyncConfig config;
    private final ClusterInfo cluster;
    private final ItemProcessor<E> processor;
    private volatile ItemsFilter<E> filter;
    private final long baselineTimestampMillis;
    private final Lock bucketWriteLock;
    private final Lock bucketReadLock;
    private final Condition bucketNotEmpty;
    private final Condition bucketNotFull;
    private final Condition stoppedButBucketNotEmpty;
    private final ToolkitListInternal<E> toolkitList;
    private long lastProcessingTimeMillis = -1L;
    private long lastWorkDoneMillis = -1L;
    private volatile STOP_STATE stopState = STOP_STATE.NORMAL;
    private final AtomicLong workDelay;
    private final ProcessingWorker processingWorkerRunnable;
    private volatile Thread processingWorkerThread;
    private AsyncCoordinatorImpl.Callback cleanupCallback;
    private final boolean workingOnDeadBucket;
    private volatile boolean destroyAfterStop;

    public ProcessingBucket(String bucketName, AsyncConfig config, ToolkitListInternal<E> toolkitList, ClusterInfo cluster, ItemProcessor<E> processor, boolean workingOnDeadBucket) {
        this.bucketName = bucketName;
        this.config = config;
        this.cluster = cluster;
        this.processor = processor;
        this.toolkitList = toolkitList;
        this.baselineTimestampMillis = System.currentTimeMillis();
        ReentrantReadWriteLock bucketLock = new ReentrantReadWriteLock();
        this.bucketReadLock = bucketLock.readLock();
        this.bucketWriteLock = bucketLock.writeLock();
        this.bucketNotEmpty = this.bucketWriteLock.newCondition();
        this.bucketNotFull = this.bucketWriteLock.newCondition();
        this.stoppedButBucketNotEmpty = this.bucketWriteLock.newCondition();
        this.workDelay = new AtomicLong(config.getWorkDelay());
        this.workingOnDeadBucket = workingOnDeadBucket;
        this.processingWorkerRunnable = new ProcessingWorker(threadNamePrefix + bucketName);
        this.destroyAfterStop = true;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public long getLastProcessing() {
        this.bucketReadLock.lock();
        try {
            long l = this.lastProcessingTimeMillis;
            return l;
        }
        finally {
            this.bucketReadLock.unlock();
        }
    }

    public void setItemsFilter(ItemsFilter<E> filter) {
        this.filter = filter;
    }

    private long baselinedCurrentTimeMillis() {
        return System.currentTimeMillis() - this.baselineTimestampMillis;
    }

    void start() {
        this.bucketWriteLock.lock();
        try {
            this.ensureNonExistingThread();
            this.processingWorkerThread = new Thread(this.processingWorkerRunnable);
            this.processingWorkerThread.setName(this.processingWorkerRunnable.getThreadName());
            this.processingWorkerThread.setDaemon(true);
            this.processingWorkerThread.start();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    private void ensureNonExistingThread() {
        if (this.processingWorkerThread != null) {
            throw new AssertionError((Object)this.processingWorkerRunnable.getThreadName());
        }
    }

    private boolean isCancelled() {
        this.bucketReadLock.lock();
        try {
            boolean bl = this.stopState == STOP_STATE.STOPPED || this.workingOnDeadBucket && this.toolkitList.isEmpty();
            this.bucketReadLock.unlock();
            return bl;
        }
        catch (Throwable throwable) {
            try {
                this.bucketReadLock.unlock();
                throw throwable;
            }
            catch (RuntimeException e) {
                if (this.isTCNRE(e)) {
                    return true;
                }
                throw e;
            }
        }
    }

    private boolean isTCNRE(Throwable th) {
        return th.getClass().getName().equals("com.tc.exception.TCNotRunningException");
    }

    public int getWaitCount() {
        this.bucketReadLock.lock();
        try {
            int n = this.toolkitList.size();
            return n;
        }
        finally {
            this.bucketReadLock.unlock();
        }
    }

    public void stopNow() {
        this.bucketWriteLock.lock();
        try {
            this.destroyAfterStop = false;
            this.stopState = STOP_STATE.STOPPED;
            this.bucketNotEmpty.signalAll();
            this.bucketNotFull.signalAll();
            this.processingWorkerThread.interrupt();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    public void stop() {
        this.bucketWriteLock.lock();
        try {
            this.workDelay.set(0L);
            this.stopState = STOP_STATE.STOP_REQUESTED;
            while (!this.toolkitList.isEmpty()) {
                this.stoppedButBucketNotEmpty.await();
            }
            this.stopState = STOP_STATE.STOPPED;
            this.bucketNotEmpty.signalAll();
            this.bucketNotFull.signalAll();
            this.processingWorkerThread.interrupt();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    public void destroy() {
        block2: {
            try {
                this.debug("destroying bucket " + this.bucketName + " " + this.toolkitList.size());
                this.toolkitList.destroy();
            }
            catch (Throwable t) {
                if (!this.isTCNRE(t) || this.cluster.areOperationsEnabled()) break block2;
                LOGGER.warn("destroyToolkitList caught TCNotRunningException on processing thread, but looks like we were shut down. This can safely be ignored!", t);
            }
        }
    }

    private String getThreadName() {
        return this.processingWorkerRunnable.getThreadName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(E item) {
        if (null == item) {
            return;
        }
        int maxQueueSize = this.config.getMaxQueueSize();
        this.bucketWriteLock.lock();
        boolean interrupted = false;
        try {
            if (maxQueueSize != 0) {
                while (!this.isCancelled() && this.toolkitList.size() >= maxQueueSize) {
                    try {
                        this.bucketNotFull.await();
                    }
                    catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            }
            boolean signalNotEmpty = this.toolkitList.isEmpty();
            this.toolkitList.unlockedAdd(item);
            if (signalNotEmpty) {
                this.bucketNotEmpty.signalAll();
            }
        }
        finally {
            this.bucketWriteLock.unlock();
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int determineBatchSize() {
        int batchSize = this.config.getBatchSize();
        int listSize = this.toolkitList.size();
        if (listSize < batchSize) {
            batchSize = listSize;
        }
        return batchSize;
    }

    private void debug(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(message);
        }
    }

    private void filterQuarantined() {
        if (null == this.filter) {
            return;
        }
        this.bucketWriteLock.lock();
        try {
            ItemsFilter<E> itemsFilter = this.filter;
            if (itemsFilter != null) {
                this.debug(this.getThreadName() + " : filterQuarantined: filtering " + this.toolkitList.size() + " quarantined items");
                itemsFilter.filter((List<E>)this.toolkitList);
                this.debug(this.getThreadName() + " : filterQuarantined: retained " + this.toolkitList.size() + " quarantined items");
            }
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processItems() throws ProcessingException {
        int workSize;
        this.bucketWriteLock.lock();
        try {
            this.lastProcessingTimeMillis = this.baselinedCurrentTimeMillis();
            workSize = this.toolkitList.size();
            if (0 == workSize) {
                this.debug(this.getThreadName() + " : processItems : nothing to process");
                return;
            }
            this.filterQuarantined();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
        int batchSize = this.config.getBatchSize();
        if (this.config.isBatchingEnabled() && batchSize > 0) {
            int rateLimit;
            if (workSize < batchSize && this.config.getMaxAllowedFallBehind() > this.lastProcessingTimeMillis - this.lastWorkDoneMillis) {
                this.bucketReadLock.lock();
                try {
                    if (this.stopState == STOP_STATE.NORMAL) {
                        this.debug(this.getThreadName() + " : processItems : only " + workSize + " work items available, waiting for " + batchSize + " items to fill up a batch");
                        return;
                    }
                }
                finally {
                    this.bucketReadLock.unlock();
                }
            }
            if ((rateLimit = this.config.getRateLimit()) > 0) {
                this.bucketReadLock.lock();
                try {
                    if (this.stopState == STOP_STATE.NORMAL) {
                        long maxBatchSizeSinceLastWorkDone;
                        long secondsSinceLastWorkDone = (this.baselinedCurrentTimeMillis() - this.lastWorkDoneMillis) / 1000L;
                        int effectiveBatchSize = this.determineBatchSize();
                        if ((long)effectiveBatchSize > (maxBatchSizeSinceLastWorkDone = (long)rateLimit * secondsSinceLastWorkDone)) {
                            this.debug(this.getThreadName() + " : processItems() : last work was done " + secondsSinceLastWorkDone + " seconds ago, processing " + effectiveBatchSize + " batch items would exceed the rate limit of " + rateLimit + ", waiting for a while.");
                            return;
                        }
                    }
                }
                finally {
                    this.bucketReadLock.unlock();
                }
            }
        }
        this.bucketWriteLock.lock();
        try {
            this.lastWorkDoneMillis = this.baselinedCurrentTimeMillis();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
        this.doProcessItems();
    }

    private void doProcessItems() throws ProcessingException {
        if (!this.cluster.areOperationsEnabled()) {
            return;
        }
        if (this.config.isBatchingEnabled() && this.config.getBatchSize() > 0) {
            this.processBatchedItems();
        } else {
            this.processListSnapshot();
        }
        if (this.toolkitList.isEmpty() && this.stopState == STOP_STATE.STOP_REQUESTED) {
            this.signalStop();
        }
    }

    private void signalStop() {
        this.bucketWriteLock.lock();
        try {
            this.stoppedButBucketNotEmpty.signalAll();
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    private void processListSnapshot() throws ProcessingException {
        int size = this.toolkitList.size();
        this.debug(this.getThreadName() + " : processListSnapshot size " + size + " quarantined items");
        while (size-- > 0) {
            this.processSingleItem();
        }
    }

    private void processSingleItem() throws ProcessingException {
        Serializable item = (Serializable)this.getItemsFromQueue(1).get(0);
        int retryAttempts = this.config.getRetryAttempts();
        int executionsLeft = retryAttempts + 1;
        while (executionsLeft-- > 0) {
            try {
                this.processor.process(item);
                break;
            }
            catch (RuntimeException e) {
                if (executionsLeft <= 0) {
                    try {
                        this.processor.throwAway(item, e);
                    }
                    catch (Throwable th) {
                        LOGGER.warn("processSingleItem caught error while throwing away an item: " + item, th);
                    }
                    continue;
                }
                LOGGER.warn(this.getThreadName() + " : processSingleItem() : exception during processing, retrying in " + retryAttempts + " milliseconds, " + executionsLeft + " retries left", (Throwable)e);
                try {
                    Thread.sleep(this.config.getRetryAttemptDelay());
                }
                catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        this.removeFromQueue(1);
    }

    private void processBatchedItems() throws ProcessingException {
        int effectiveBatchSize = this.determineBatchSize();
        List<E> batch = this.getItemsFromQueue(effectiveBatchSize);
        int retryAttempts = this.config.getRetryAttempts();
        int executionsLeft = retryAttempts + 1;
        while (executionsLeft-- > 0) {
            try {
                this.processor.process(batch);
                break;
            }
            catch (RuntimeException e) {
                LOGGER.warn("processBatchedItems caught error while processing batch of " + batch.size(), (Throwable)e);
                if (executionsLeft <= 0) {
                    for (Serializable item : batch) {
                        try {
                            this.processor.throwAway(item, e);
                        }
                        catch (Throwable th) {
                            LOGGER.warn("processBatchedItems caught error while throwing away an item: " + item, th);
                        }
                    }
                    continue;
                }
                LOGGER.warn(this.getThreadName() + " : processBatchedItems() : exception during processing, retrying in " + retryAttempts + " milliseconds, " + executionsLeft + " retries left", (Throwable)e);
                try {
                    Thread.sleep(this.config.getRetryAttemptDelay());
                }
                catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        this.removeFromQueue(effectiveBatchSize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<E> getItemsFromQueue(int effectiveBatchSize) {
        this.bucketReadLock.lock();
        try {
            ArrayList<Serializable> batch = new ArrayList<Serializable>(effectiveBatchSize);
            for (int i = 0; i < effectiveBatchSize; ++i) {
                Serializable item = (Serializable)this.toolkitList.get(i);
                batch.add(item);
            }
            ArrayList<Serializable> arrayList = batch;
            return arrayList;
        }
        finally {
            this.bucketReadLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeFromQueue(int effectiveBatchSize) {
        this.bucketWriteLock.lock();
        try {
            boolean signalNotFull = this.toolkitList.size() >= this.config.getMaxQueueSize();
            for (int i = 0; i < effectiveBatchSize; ++i) {
                this.toolkitList.remove(0);
            }
            if (signalNotFull) {
                this.bucketNotFull.signalAll();
            }
        }
        finally {
            this.bucketWriteLock.unlock();
        }
    }

    void setCleanupCallback(AsyncCoordinatorImpl.Callback cleanupDeadBucket) {
        this.cleanupCallback = cleanupDeadBucket;
    }

    private final class ProcessingWorker
    implements Runnable {
        private final String threadName;

        public ProcessingWorker(String threadName) {
            this.threadName = threadName;
        }

        public String getThreadName() {
            return this.threadName;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            block19: {
                try {
                    while (!ProcessingBucket.this.isCancelled()) {
                        if (ProcessingBucket.this.cluster.areOperationsEnabled()) {
                            try {
                                ProcessingBucket.this.processItems();
                            }
                            catch (Throwable e) {
                                if (ProcessingBucket.this.cluster.areOperationsEnabled()) {
                                    if (ProcessingBucket.this.isTCNRE(e)) continue;
                                    LOGGER.error("Caught error on processing bucket " + ProcessingBucket.this.bucketName, e);
                                    continue;
                                }
                                LOGGER.warn("Caught error on processing items, but looks like we were shut down. This can probably be safely ignored", e);
                                continue;
                            }
                        }
                        long currentLastProcessing = ProcessingBucket.this.getLastProcessing();
                        ProcessingBucket.this.bucketWriteLock.lock();
                        try {
                            try {
                                long tmpWorkDelay = ProcessingBucket.this.workDelay.get();
                                if (tmpWorkDelay != 0L) {
                                    do {
                                        ProcessingBucket.this.bucketNotEmpty.await(tmpWorkDelay, TimeUnit.MILLISECONDS);
                                        long actualWorkDelay = ProcessingBucket.this.baselinedCurrentTimeMillis() - currentLastProcessing;
                                        if (actualWorkDelay < tmpWorkDelay) {
                                            tmpWorkDelay -= actualWorkDelay;
                                            continue;
                                        }
                                        tmpWorkDelay = 0L;
                                    } while (tmpWorkDelay > 0L && ProcessingBucket.this.stopState == STOP_STATE.NORMAL);
                                    continue;
                                }
                                while (!ProcessingBucket.this.workingOnDeadBucket && ProcessingBucket.this.stopState == STOP_STATE.NORMAL && ProcessingBucket.this.toolkitList.isEmpty()) {
                                    ProcessingBucket.this.bucketNotEmpty.await();
                                }
                            }
                            catch (InterruptedException e) {
                                ProcessingBucket.this.stop();
                                Thread.currentThread().interrupt();
                            }
                        }
                        finally {
                            ProcessingBucket.this.bucketWriteLock.unlock();
                        }
                    }
                }
                catch (Throwable t) {
                    if (!ProcessingBucket.this.isTCNRE(t) || ProcessingBucket.this.cluster.areOperationsEnabled()) break block19;
                    LOGGER.warn("Caught TCNotRunningException on processing thread, but looks like we were shut down. This can safely be ignored!", t);
                }
            }
            if (ProcessingBucket.this.destroyAfterStop) {
                if (ProcessingBucket.this.workingOnDeadBucket) {
                    ProcessingBucket.this.cleanupCallback.callback();
                } else {
                    ProcessingBucket.this.destroy();
                }
            }
        }
    }

    private static enum STOP_STATE {
        NORMAL,
        STOP_REQUESTED,
        STOPPED;

    }
}

