/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.CastingOperationConverter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import net.sf.ehcache.writer.writebehind.WriteBehindQueue;
import net.sf.ehcache.writer.writebehind.operations.DeleteOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperation;
import net.sf.ehcache.writer.writebehind.operations.WriteOperation;

public abstract class AbstractWriteBehindQueue
implements WriteBehind {
    private static final Logger LOGGER = Logger.getLogger(WriteBehindQueue.class.getName());
    private static final int MS_IN_SEC = 1000;
    private final String cacheName;
    private final long minWriteDelayMs;
    private final long maxWriteDelayMs;
    private final int rateLimitPerSecond;
    private final int maxQueueSize;
    private final boolean writeBatching;
    private final int writeBatchSize;
    private final int retryAttempts;
    private final int retryAttemptDelaySeconds;
    private final Thread processingThread;
    private final ReentrantReadWriteLock queueLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock queueReadLock = this.queueLock.readLock();
    private final ReentrantReadWriteLock.WriteLock queueWriteLock = this.queueLock.writeLock();
    private final Condition queueIsFull = this.queueWriteLock.newCondition();
    private final Condition queueIsEmpty = this.queueWriteLock.newCondition();
    private final Condition queueIsStopped = this.queueWriteLock.newCondition();
    private final AtomicLong lastProcessing = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastWorkDone = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean busyProcessing = new AtomicBoolean(false);
    private volatile OperationsFilter filter;
    private CacheWriter cacheWriter;
    private boolean stopping = false;
    private boolean stopped = true;

    public AbstractWriteBehindQueue(CacheConfiguration config) {
        this.cacheName = config.getName();
        CacheWriterConfiguration cacheWriterConfig = config.getCacheWriterConfiguration();
        this.minWriteDelayMs = cacheWriterConfig.getMinWriteDelay() * 1000;
        this.maxWriteDelayMs = cacheWriterConfig.getMaxWriteDelay() * 1000;
        this.rateLimitPerSecond = cacheWriterConfig.getRateLimitPerSecond();
        this.maxQueueSize = cacheWriterConfig.getWriteBehindMaxQueueSize();
        this.writeBatching = cacheWriterConfig.getWriteBatching();
        this.writeBatchSize = cacheWriterConfig.getWriteBatchSize();
        this.retryAttempts = cacheWriterConfig.getRetryAttempts();
        this.retryAttemptDelaySeconds = cacheWriterConfig.getRetryAttemptDelaySeconds();
        this.processingThread = new Thread((Runnable)new ProcessingThread(), this.cacheName + " write-behind");
        this.processingThread.setDaemon(true);
    }

    protected abstract List<SingleOperation> quarantineItems();

    protected abstract void addItem(SingleOperation var1);

    protected abstract void reinsertUnprocessedItems(List<SingleOperation> var1);

    @Override
    public void start(CacheWriter writer) {
        this.queueWriteLock.lock();
        try {
            if (!this.stopped) {
                throw new CacheException("The write-behind queue for cache '" + this.cacheName + "' can't be started more than once");
            }
            if (this.processingThread.isAlive()) {
                throw new CacheException("The thread with name " + this.processingThread.getName() + " already exists and is still running");
            }
            this.stopping = false;
            this.stopped = false;
            this.cacheWriter = writer;
            this.processingThread.start();
        }
        finally {
            this.queueWriteLock.unlock();
        }
    }

    @Override
    public void setOperationsFilter(OperationsFilter filter) {
        this.filter = filter;
    }

    private long getLastProcessing() {
        return this.lastProcessing.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processItems() throws CacheException {
        if (this.busyProcessing.get()) {
            throw new CacheException("The write behind queue for cache '" + this.cacheName + "' is already busy processing.");
        }
        this.busyProcessing.set(true);
        this.lastProcessing.set(System.currentTimeMillis());
        try {
            int workSize;
            List<SingleOperation> quarantined;
            this.queueWriteLock.lock();
            try {
                quarantined = this.getQueueSize() > 0L ? this.quarantineItems() : null;
                workSize = quarantined != null ? quarantined.size() : 0;
            }
            finally {
                this.queueWriteLock.unlock();
            }
            if (0 == workSize) {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(this.getThreadName() + " : processItems() : nothing to process");
                }
                return;
            }
            this.filterQuarantined(quarantined);
            if (this.writeBatching && this.writeBatchSize > 0) {
                if (workSize < this.writeBatchSize && this.maxWriteDelayMs > this.lastProcessing.get() - this.lastWorkDone.get()) {
                    this.waitUntilEnoughWorkItemsAvailable(quarantined, workSize);
                    return;
                }
                if (this.rateLimitPerSecond > 0) {
                    long secondsSinceLastWorkDone = (System.currentTimeMillis() - this.lastWorkDone.get()) / 1000L;
                    long maxBatchSizeSinceLastWorkDone = (long)this.rateLimitPerSecond * secondsSinceLastWorkDone;
                    int batchSize = this.determineBatchSize(quarantined);
                    if ((long)batchSize > maxBatchSizeSinceLastWorkDone) {
                        this.waitUntilEnoughTimeHasPassed(quarantined, batchSize, secondsSinceLastWorkDone);
                        return;
                    }
                }
            }
            try {
                this.lastWorkDone.set(System.currentTimeMillis());
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(this.getThreadName() + " : processItems() : processing started");
                }
                this.processQuarantinedItems(quarantined);
            }
            catch (RuntimeException e) {
                this.reassemble(quarantined);
                throw e;
            }
            catch (Error e) {
                this.reassemble(quarantined);
                throw e;
            }
        }
        finally {
            this.busyProcessing.set(false);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(this.getThreadName() + " : processItems() : processing finished");
            }
        }
    }

    private void waitUntilEnoughWorkItemsAvailable(List<SingleOperation> quarantined, int workSize) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(this.getThreadName() + " : processItems() : only " + workSize + " work items available, waiting for " + this.writeBatchSize + " items to fill up a batch");
        }
        this.reassemble(quarantined);
    }

    private void waitUntilEnoughTimeHasPassed(List<SingleOperation> quarantined, int batchSize, long secondsSinceLastWorkDone) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(this.getThreadName() + " : processItems() : last work was done " + secondsSinceLastWorkDone + " seconds ago, processing " + batchSize + " batch items would exceed the rate limit of " + this.rateLimitPerSecond + ", waiting for a while.");
        }
        this.reassemble(quarantined);
    }

    private int determineBatchSize(List<SingleOperation> quarantined) {
        int batchSize = this.writeBatchSize;
        if (quarantined.size() < batchSize) {
            batchSize = quarantined.size();
        }
        return batchSize;
    }

    private void filterQuarantined(List<SingleOperation> quarantined) {
        OperationsFilter operationsFilter = this.filter;
        if (operationsFilter != null) {
            operationsFilter.filter(quarantined, CastingOperationConverter.getInstance());
        }
    }

    private void processQuarantinedItems(List<SingleOperation> quarantined) {
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config(this.getThreadName() + " : processItems() : processing " + quarantined.size() + " quarantined items");
        }
        if (this.writeBatching && this.writeBatchSize > 0) {
            this.processBatchedOperations(quarantined);
        } else {
            this.processSingleOperation(quarantined);
        }
    }

    private void processBatchedOperations(List<SingleOperation> quarantined) {
        int batchSize = this.determineBatchSize(quarantined);
        List<List<? extends SingleOperation>> batches = this.createMonomorphicBatches(quarantined.subList(0, batchSize));
        block4: for (List<? extends SingleOperation> batch : batches) {
            int executionsLeft = this.retryAttempts + 1;
            while (executionsLeft-- > 0) {
                try {
                    batch.get(0).createBatchOperation(batch).performBatchOperation(this.cacheWriter);
                    continue block4;
                }
                catch (RuntimeException e) {
                    if (executionsLeft <= 0) {
                        for (SingleOperation singleOperation : batch) {
                            singleOperation.throwAway(this.cacheWriter, e);
                        }
                        continue;
                    }
                    LOGGER.warning("Exception while processing write behind queue, retrying in " + this.retryAttemptDelaySeconds + " seconds, " + executionsLeft + " retries left : " + e.getMessage());
                    try {
                        Thread.sleep(this.retryAttemptDelaySeconds * 1000);
                    }
                    catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
            }
        }
        for (int i = 0; i < batchSize; ++i) {
            quarantined.remove(0);
        }
        if (!quarantined.isEmpty()) {
            this.reassemble(quarantined);
        }
    }

    private List<List<? extends SingleOperation>> createMonomorphicBatches(List<SingleOperation> batch) {
        ArrayList<List<? extends SingleOperation>> closedBatches = new ArrayList<List<? extends SingleOperation>>();
        HashSet<Object> deletedKeys = new HashSet<Object>();
        HashSet<Object> writtenKeys = new HashSet<Object>();
        ArrayList<DeleteOperation> deleteBatch = new ArrayList<DeleteOperation>();
        ArrayList<WriteOperation> writeBatch = new ArrayList<WriteOperation>();
        for (SingleOperation item : batch) {
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config(this.getThreadName() + " : processItems() : adding " + item + " to next batch");
            }
            if (item instanceof WriteOperation) {
                if (deletedKeys.contains(item.getKey())) {
                    closedBatches.add(deleteBatch);
                    deleteBatch = new ArrayList();
                    deletedKeys = new HashSet();
                }
                writeBatch.add((WriteOperation)item);
                writtenKeys.add(item.getKey());
                continue;
            }
            if (item instanceof DeleteOperation) {
                if (writtenKeys.contains(item.getKey())) {
                    closedBatches.add(writeBatch);
                    writeBatch = new ArrayList();
                    writtenKeys = new HashSet();
                }
                deleteBatch.add((DeleteOperation)item);
                deletedKeys.add(item.getKey());
                continue;
            }
            throw new AssertionError();
        }
        if (!writeBatch.isEmpty()) {
            closedBatches.add(writeBatch);
        }
        if (!deleteBatch.isEmpty()) {
            closedBatches.add(deleteBatch);
        }
        return closedBatches;
    }

    private void processSingleOperation(List<SingleOperation> quarantined) {
        while (!quarantined.isEmpty()) {
            SingleOperation item = quarantined.get(0);
            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.config(this.getThreadName() + " : processItems() : processing " + item);
            }
            int executionsLeft = this.retryAttempts + 1;
            while (executionsLeft-- > 0) {
                try {
                    item.performSingleOperation(this.cacheWriter);
                    break;
                }
                catch (RuntimeException e) {
                    if (executionsLeft <= 0) {
                        try {
                            item.throwAway(this.cacheWriter, e);
                        }
                        catch (RuntimeException runtimeException) {}
                        continue;
                    }
                    LOGGER.warning("Exception while processing write behind queue, retrying in " + this.retryAttemptDelaySeconds + " seconds, " + executionsLeft + " retries left : " + e.getMessage());
                    try {
                        Thread.sleep(this.retryAttemptDelaySeconds * 1000);
                    }
                    catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
            }
            quarantined.remove(0);
        }
    }

    @Override
    public void write(Element element) {
        this.queueWriteLock.lock();
        try {
            this.waitForQueueSizeToDrop();
            if (this.stopping || this.stopped) {
                throw new CacheException("The element '" + element + "' couldn't be added through the write-behind queue for cache '" + this.cacheName + "' since it's not started.");
            }
            this.addItem(new WriteOperation(element));
            if (this.getQueueSize() + 1L < (long)this.maxQueueSize) {
                this.queueIsFull.signal();
            }
            this.queueIsEmpty.signal();
        }
        finally {
            this.queueWriteLock.unlock();
        }
    }

    private void waitForQueueSizeToDrop() {
        if (this.maxQueueSize > 0) {
            while (this.getQueueSize() >= (long)this.maxQueueSize) {
                try {
                    this.queueIsFull.await();
                }
                catch (InterruptedException e) {
                    this.stop();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void delete(CacheEntry entry) {
        this.queueWriteLock.lock();
        try {
            this.waitForQueueSizeToDrop();
            if (this.stopping || this.stopped) {
                throw new CacheException("The entry for key '" + entry.getKey() + "' couldn't be deleted through the write-behind queue for cache '" + this.cacheName + "' since it's not started.");
            }
            this.addItem(new DeleteOperation(entry));
            if (this.getQueueSize() + 1L < (long)this.maxQueueSize) {
                this.queueIsFull.signal();
            }
            this.queueIsEmpty.signal();
        }
        finally {
            this.queueWriteLock.unlock();
        }
    }

    @Override
    public void stop() throws CacheException {
        this.queueWriteLock.lock();
        try {
            if (this.stopped) {
                return;
            }
            this.stopping = true;
            this.queueIsEmpty.signal();
            while (!this.stopped) {
                this.queueIsStopped.await();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CacheException(e);
        }
        finally {
            this.queueWriteLock.unlock();
        }
    }

    @Override
    public abstract long getQueueSize();

    private boolean isStopped() {
        this.queueReadLock.lock();
        try {
            boolean bl = this.stopped;
            return bl;
        }
        finally {
            this.queueReadLock.unlock();
        }
    }

    private String getThreadName() {
        return this.processingThread.getName();
    }

    private void reassemble(List<SingleOperation> quarantined) {
        this.queueWriteLock.lock();
        try {
            if (null == quarantined) {
                return;
            }
            this.reinsertUnprocessedItems(quarantined);
            this.queueIsEmpty.signal();
        }
        finally {
            this.queueWriteLock.unlock();
        }
    }

    protected Thread getProcessingThread() {
        return this.processingThread;
    }

    private final class ProcessingThread
    implements Runnable {
        private ProcessingThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                while (!AbstractWriteBehindQueue.this.isStopped()) {
                    AbstractWriteBehindQueue.this.processItems();
                    AbstractWriteBehindQueue.this.queueWriteLock.lock();
                    try {
                        AbstractWriteBehindQueue.this.queueIsFull.signal();
                        try {
                            if (AbstractWriteBehindQueue.this.minWriteDelayMs != 0L) {
                                long actualDelay;
                                long delay = AbstractWriteBehindQueue.this.minWriteDelayMs;
                                do {
                                    AbstractWriteBehindQueue.this.queueIsEmpty.await(delay, TimeUnit.MILLISECONDS);
                                } while ((delay = (actualDelay = System.currentTimeMillis() - AbstractWriteBehindQueue.this.getLastProcessing()) < AbstractWriteBehindQueue.this.minWriteDelayMs ? AbstractWriteBehindQueue.this.minWriteDelayMs - actualDelay : 0L) > 0L);
                            } else {
                                while (!AbstractWriteBehindQueue.this.stopping && AbstractWriteBehindQueue.this.getQueueSize() == 0L) {
                                    AbstractWriteBehindQueue.this.queueIsEmpty.await();
                                }
                            }
                        }
                        catch (InterruptedException e) {
                            AbstractWriteBehindQueue.this.stop();
                            Thread.currentThread().interrupt();
                        }
                        if (!AbstractWriteBehindQueue.this.stopping || AbstractWriteBehindQueue.this.getQueueSize() != 0L) continue;
                        this.stopTheQueueThread();
                    }
                    finally {
                        AbstractWriteBehindQueue.this.queueWriteLock.unlock();
                    }
                }
            }
            finally {
                this.stopTheQueueThread();
            }
        }

        private void stopTheQueueThread() {
            AbstractWriteBehindQueue.this.queueWriteLock.lock();
            try {
                AbstractWriteBehindQueue.this.stopped = true;
                AbstractWriteBehindQueue.this.stopping = false;
                AbstractWriteBehindQueue.this.queueIsStopped.signalAll();
            }
            finally {
                AbstractWriteBehindQueue.this.queueWriteLock.unlock();
            }
        }
    }
}

