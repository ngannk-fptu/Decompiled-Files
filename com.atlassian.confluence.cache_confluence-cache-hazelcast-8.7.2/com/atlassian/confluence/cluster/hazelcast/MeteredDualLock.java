/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.impl.metrics.CoreMetrics
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.util.profiling.LongRunningMetricTimer
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Timer
 *  io.micrometer.core.instrument.Timer$Sample
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.DualLock;
import com.atlassian.confluence.impl.metrics.CoreMetrics;
import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.LongRunningMetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

@Internal
final class MeteredDualLock
implements DualLock {
    private final DualLock delegate;
    private final MeterRegistry meterRegistry;
    private final String lockName;
    private final String pluginKey;
    private final LongRunningMetricTimer clusterLockHeldMetricTimer;
    private final AtomicReference<Ticker> clusterLockHeldTicker = new AtomicReference<Ticker>(Ticker.NO_OP);

    MeteredDualLock(DualLock delegate, MeterRegistry meterRegistry, String lockName) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
        this.lockName = lockName;
        this.pluginKey = PluginKeyStack.getFirstPluginKey();
        this.clusterLockHeldMetricTimer = this.newClusterLockHeldMetricTimer();
    }

    public Serializable getValue() {
        LockOperationTimer timer = new LockOperationTimer("getValue");
        try {
            Serializable value = this.delegate.getValue();
            timer.stopAndRecord(true);
            return value;
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            throw ex;
        }
    }

    public void setValue(Serializable value) {
        LockOperationTimer timer = new LockOperationTimer("setValue");
        try {
            this.delegate.setValue(value);
            timer.stopAndRecord(true);
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            throw ex;
        }
    }

    public boolean tryLock() {
        LockOperationTimer timer = new LockOperationTimer("tryLock");
        try {
            boolean success = this.delegate.tryLock();
            timer.stopAndRecord(success);
            if (success) {
                this.onLocked();
            }
            return success;
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            throw ex;
        }
    }

    public void unlock() {
        LockOperationTimer timer = new LockOperationTimer("unlock");
        try {
            this.delegate.unlock();
            timer.stopAndRecord(true);
            this.onUnlocked();
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            throw ex;
        }
    }

    public void lock() {
        LockOperationTimer timer = new LockOperationTimer("lock");
        Ticker clusterLockWaitedTicker = this.newClusterLockWaitedMetricTimer().start();
        try {
            this.delegate.lock();
            timer.stopAndRecord(true);
            clusterLockWaitedTicker.close();
            this.onLocked();
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            clusterLockWaitedTicker.close();
            throw ex;
        }
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        LockOperationTimer timer = new LockOperationTimer("tryLock");
        Ticker clusterLockWaitedTicker = this.newClusterLockWaitedMetricTimer().start();
        try {
            boolean success = this.delegate.tryLock(time, unit);
            timer.stopAndRecord(success);
            clusterLockWaitedTicker.close();
            if (success) {
                this.onLocked();
            }
            return success;
        }
        catch (InterruptedException ex) {
            timer.stopAndRecord(false);
            clusterLockWaitedTicker.close();
            Thread.currentThread().interrupt();
            throw ex;
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            clusterLockWaitedTicker.close();
            throw ex;
        }
    }

    public void lockInterruptibly() throws InterruptedException {
        LockOperationTimer timer = new LockOperationTimer("lockInterruptibly");
        Ticker clusterLockWaitedTicker = this.newClusterLockWaitedMetricTimer().start();
        try {
            this.delegate.lockInterruptibly();
            timer.stopAndRecord(true);
            clusterLockWaitedTicker.close();
            this.onLocked();
        }
        catch (InterruptedException ex) {
            timer.stopAndRecord(false);
            clusterLockWaitedTicker.close();
            Thread.currentThread().interrupt();
            throw ex;
        }
        catch (RuntimeException ex) {
            timer.stopAndRecord(false);
            clusterLockWaitedTicker.close();
            throw ex;
        }
    }

    public boolean isHeldByCurrentThread() {
        return this.delegate.isHeldByCurrentThread();
    }

    public Condition newCondition() {
        return this.delegate.newCondition();
    }

    private void onLocked() {
        this.clusterLockHeldTicker.set(this.clusterLockHeldMetricTimer.start());
    }

    private void onUnlocked() {
        this.clusterLockHeldTicker.getAndSet(Ticker.NO_OP).close();
    }

    private LongRunningMetricTimer newClusterLockWaitedMetricTimer() {
        return this.constructMetricTimer("cluster.lock.waited.duration", this.pluginKey, this.lockName, this.delegate.getClass().getCanonicalName());
    }

    private LongRunningMetricTimer newClusterLockHeldMetricTimer() {
        return this.constructMetricTimer("cluster.lock.held.duration", this.pluginKey, this.lockName, this.delegate.getClass().getCanonicalName());
    }

    private LongRunningMetricTimer constructMetricTimer(String metricKey, String pluginKey, String lockName, String implementationName) {
        return Metrics.metric((String)metricKey).withAnalytics().tag("lockName", lockName).tag("pluginKeyAtCreation", pluginKey).tag("implementation", implementationName).longRunningTimer();
    }

    class LockOperationTimer {
        private final Timer.Sample sample;
        private final String operationName;

        public LockOperationTimer(String operationName) {
            this.sample = Timer.start((MeterRegistry)MeteredDualLock.this.meterRegistry);
            this.operationName = operationName;
        }

        public void stopAndRecord(boolean success) {
            this.sample.stop(CoreMetrics.LOCK_OPERATION_TIMER.timer(MeteredDualLock.this.meterRegistry, new String[]{"operation", this.operationName, "lockName", MeteredDualLock.this.lockName, "success", String.valueOf(success)}));
        }
    }
}

