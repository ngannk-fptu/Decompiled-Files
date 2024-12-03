/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.impl.DefaultNearCache;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationMetaDataFetcher;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.MinimalPartitionService;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.internal.nearcache.impl.invalidation.StaleReadDetectorImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class RepairingTask
implements Runnable {
    public static final HazelcastProperty MAX_TOLERATED_MISS_COUNT = new HazelcastProperty("hazelcast.invalidation.max.tolerated.miss.count", 10);
    public static final HazelcastProperty RECONCILIATION_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.invalidation.reconciliation.interval.seconds", 60, TimeUnit.SECONDS);
    public static final HazelcastProperty MIN_RECONCILIATION_INTERVAL_SECONDS = new HazelcastProperty("hazelcast.invalidation.min.reconciliation.interval.seconds", 30, TimeUnit.SECONDS);
    private static final long RESCHEDULE_FAILED_INITIALIZATION_AFTER_MILLIS = 500L;
    final int maxToleratedMissCount;
    final long reconciliationIntervalNanos;
    private final int partitionCount;
    private final String localUuid;
    private final ILogger logger;
    private final TaskScheduler scheduler;
    private final InvalidationMetaDataFetcher invalidationMetaDataFetcher;
    private final SerializationService serializationService;
    private final MinimalPartitionService partitionService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ConcurrentMap<String, RepairingHandler> handlers = new ConcurrentHashMap<String, RepairingHandler>();
    private final ContextMutexFactory contextMutexFactory = new ContextMutexFactory();
    private volatile long lastAntiEntropyRunNanos;

    public RepairingTask(HazelcastProperties properties, InvalidationMetaDataFetcher invalidationMetaDataFetcher, TaskScheduler scheduler, SerializationService serializationService, MinimalPartitionService partitionService, String localUuid, ILogger logger) {
        this.reconciliationIntervalNanos = TimeUnit.SECONDS.toNanos(this.getReconciliationIntervalSeconds(properties));
        this.maxToleratedMissCount = this.getMaxToleratedMissCount(properties);
        this.invalidationMetaDataFetcher = invalidationMetaDataFetcher;
        this.scheduler = scheduler;
        this.serializationService = serializationService;
        this.partitionService = partitionService;
        this.partitionCount = partitionService.getPartitionCount();
        this.localUuid = localUuid;
        this.logger = logger;
    }

    private int getMaxToleratedMissCount(HazelcastProperties properties) {
        int maxToleratedMissCount = properties.getInteger(MAX_TOLERATED_MISS_COUNT);
        return Preconditions.checkNotNegative(maxToleratedMissCount, String.format("max-tolerated-miss-count cannot be < 0 but found %d", maxToleratedMissCount));
    }

    private int getReconciliationIntervalSeconds(HazelcastProperties properties) {
        int reconciliationIntervalSeconds = properties.getInteger(RECONCILIATION_INTERVAL_SECONDS);
        int minReconciliationIntervalSeconds = properties.getInteger(MIN_RECONCILIATION_INTERVAL_SECONDS);
        if (reconciliationIntervalSeconds < 0 || reconciliationIntervalSeconds > 0 && reconciliationIntervalSeconds < minReconciliationIntervalSeconds) {
            String msg = String.format("Reconciliation interval can be at least %s seconds if it is not zero, but %d was configured. Note: Configuring a value of zero seconds disables the reconciliation task.", MIN_RECONCILIATION_INTERVAL_SECONDS.getDefaultValue(), reconciliationIntervalSeconds);
            throw new IllegalArgumentException(msg);
        }
        return reconciliationIntervalSeconds;
    }

    @Override
    public void run() {
        try {
            this.fixSequenceGaps();
            if (this.isAntiEntropyNeeded()) {
                this.runAntiEntropy();
            }
        }
        finally {
            if (this.running.get()) {
                this.scheduleNextRun();
            }
        }
    }

    private void fixSequenceGaps() {
        for (RepairingHandler handler : this.handlers.values()) {
            if (!this.isAboveMaxToleratedMissCount(handler)) continue;
            this.updateLastKnownStaleSequences(handler);
        }
    }

    private void runAntiEntropy() {
        this.invalidationMetaDataFetcher.fetchMetadata(this.handlers);
        this.lastAntiEntropyRunNanos = System.nanoTime();
    }

    private boolean isAntiEntropyNeeded() {
        if (this.reconciliationIntervalNanos == 0L) {
            return false;
        }
        long sinceLastRunNanos = System.nanoTime() - this.lastAntiEntropyRunNanos;
        return sinceLastRunNanos >= this.reconciliationIntervalNanos;
    }

    private void scheduleNextRun() {
        block2: {
            try {
                this.scheduler.schedule(this, 1L, TimeUnit.SECONDS);
            }
            catch (RejectedExecutionException e) {
                if (!this.logger.isFinestEnabled()) break block2;
                this.logger.finest(e.getMessage());
            }
        }
    }

    public <K, V> RepairingHandler registerAndGetHandler(String dataStructureName, NearCache<K, V> nearCache) {
        RepairingHandler handler = ConcurrencyUtil.getOrPutSynchronized(this.handlers, dataStructureName, this.contextMutexFactory, new HandlerConstructor<K, V>(nearCache));
        if (this.running.compareAndSet(false, true)) {
            this.scheduleNextRun();
            this.lastAntiEntropyRunNanos = System.nanoTime();
        }
        return handler;
    }

    public void deregisterHandler(String dataStructureName) {
        this.handlers.remove(dataStructureName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initRepairingHandler(RepairingHandler handler) {
        this.logger.finest("Initializing repairing handler");
        boolean initialized = false;
        try {
            this.invalidationMetaDataFetcher.init(handler);
            initialized = true;
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
        finally {
            if (!initialized) {
                this.initRepairingHandlerAsync(handler);
            }
        }
    }

    private void initRepairingHandlerAsync(final RepairingHandler handler) {
        this.scheduler.schedule(new Runnable(){
            private final AtomicInteger round = new AtomicInteger();

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                int roundNumber = this.round.incrementAndGet();
                boolean initialized = false;
                try {
                    RepairingTask.this.initRepairingHandler(handler);
                    initialized = true;
                }
                catch (Exception e) {
                    if (RepairingTask.this.logger.isFinestEnabled()) {
                        RepairingTask.this.logger.finest(e);
                    }
                }
                finally {
                    long totalDelaySoFarNanos;
                    if (!initialized && RepairingTask.this.reconciliationIntervalNanos > (totalDelaySoFarNanos = RepairingTask.totalDelaySoFarNanos(roundNumber))) {
                        long delay = (long)roundNumber * 500L;
                        RepairingTask.this.scheduler.schedule(this, delay, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }, 500L, TimeUnit.MILLISECONDS);
    }

    private static long totalDelaySoFarNanos(int roundNumber) {
        long totalDelayMillis = 0L;
        for (int i = 1; i < roundNumber; ++i) {
            totalDelayMillis += (long)roundNumber * 500L;
        }
        return TimeUnit.MILLISECONDS.toNanos(totalDelayMillis);
    }

    private boolean isAboveMaxToleratedMissCount(RepairingHandler handler) {
        long totalMissCount = 0L;
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            MetaDataContainer metaData = handler.getMetaDataContainer(partitionId);
            if ((totalMissCount += metaData.getMissedSequenceCount()) <= (long)this.maxToleratedMissCount) continue;
            if (this.logger.isFinestEnabled()) {
                this.logger.finest(String.format("Above tolerated miss count:[map=%s,missCount=%d,maxToleratedMissCount=%d]", handler.getName(), totalMissCount, this.maxToleratedMissCount));
            }
            return true;
        }
        return false;
    }

    private void updateLastKnownStaleSequences(RepairingHandler handler) {
        for (int partition = 0; partition < this.partitionCount; ++partition) {
            MetaDataContainer metaData = handler.getMetaDataContainer(partition);
            long missCount = metaData.getMissedSequenceCount();
            if (missCount == 0L) continue;
            metaData.addAndGetMissedSequenceCount(-missCount);
            handler.updateLastKnownStaleSequence(metaData, partition);
        }
    }

    public InvalidationMetaDataFetcher getInvalidationMetaDataFetcher() {
        return this.invalidationMetaDataFetcher;
    }

    public ConcurrentMap<String, RepairingHandler> getHandlers() {
        return this.handlers;
    }

    public String toString() {
        return "RepairingTask{}";
    }

    private class HandlerConstructor<K, V>
    implements ConstructorFunction<String, RepairingHandler> {
        private final NearCache<K, V> nearCache;

        HandlerConstructor(NearCache<K, V> nearCache) {
            this.nearCache = nearCache;
        }

        @Override
        public RepairingHandler createNew(String dataStructureName) {
            RepairingHandler handler = new RepairingHandler(RepairingTask.this.logger, RepairingTask.this.localUuid, dataStructureName, this.nearCache, RepairingTask.this.serializationService, RepairingTask.this.partitionService);
            StaleReadDetectorImpl staleReadDetector = new StaleReadDetectorImpl(handler, RepairingTask.this.partitionService);
            this.nearCache.unwrap(DefaultNearCache.class).getNearCacheRecordStore().setStaleReadDetector(staleReadDetector);
            RepairingTask.this.initRepairingHandler(handler);
            return handler;
        }
    }
}

