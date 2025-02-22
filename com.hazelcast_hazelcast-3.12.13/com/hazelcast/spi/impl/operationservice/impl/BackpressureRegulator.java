/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.sequence.CallIdFactory;
import com.hazelcast.spi.impl.sequence.CallIdSequence;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class BackpressureRegulator {
    static final float RANGE = 0.25f;
    private final AtomicInteger syncCountdown = new AtomicInteger();
    private final boolean enabled;
    private final boolean disabled;
    private final int syncWindow;
    private final int partitionCount;
    private final int maxConcurrentInvocations;
    private final int backoffTimeoutMs;

    BackpressureRegulator(HazelcastProperties properties, ILogger logger) {
        this.enabled = properties.getBoolean(GroupProperty.BACKPRESSURE_ENABLED);
        this.disabled = !this.enabled;
        this.partitionCount = properties.getInteger(GroupProperty.PARTITION_COUNT);
        this.syncWindow = this.getSyncWindow(properties);
        this.syncCountdown.set(this.syncWindow);
        this.maxConcurrentInvocations = this.getMaxConcurrentInvocations(properties);
        this.backoffTimeoutMs = this.getBackoffTimeoutMs(properties);
        if (this.enabled) {
            logger.info("Backpressure is enabled, maxConcurrentInvocations:" + this.maxConcurrentInvocations + ", syncWindow: " + this.syncWindow);
            int backupTimeoutMillis = properties.getInteger(GroupProperty.OPERATION_BACKUP_TIMEOUT_MILLIS);
            if ((long)backupTimeoutMillis < TimeUnit.MINUTES.toMillis(1L)) {
                logger.warning(String.format("Back pressure is enabled, but '%s' is too small. ", GroupProperty.OPERATION_BACKUP_TIMEOUT_MILLIS.getName()));
            }
        } else {
            logger.info("Backpressure is disabled");
        }
    }

    int syncCountDown() {
        return this.syncCountdown.get();
    }

    private int getSyncWindow(HazelcastProperties props) {
        int syncWindow = props.getInteger(GroupProperty.BACKPRESSURE_SYNCWINDOW);
        if (this.enabled && syncWindow <= 0) {
            throw new IllegalArgumentException("Can't have '" + GroupProperty.BACKPRESSURE_SYNCWINDOW + "' with a value smaller than 1");
        }
        return syncWindow;
    }

    private int getBackoffTimeoutMs(HazelcastProperties props) {
        int backoffTimeoutMs = (int)props.getMillis(GroupProperty.BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS);
        if (this.enabled && backoffTimeoutMs < 0) {
            throw new IllegalArgumentException("Can't have '" + GroupProperty.BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS + "' with a value smaller than 0");
        }
        return backoffTimeoutMs;
    }

    private int getMaxConcurrentInvocations(HazelcastProperties props) {
        int invocationsPerPartition = props.getInteger(GroupProperty.BACKPRESSURE_MAX_CONCURRENT_INVOCATIONS_PER_PARTITION);
        if (invocationsPerPartition < 1) {
            throw new IllegalArgumentException("Can't have '" + GroupProperty.BACKPRESSURE_MAX_CONCURRENT_INVOCATIONS_PER_PARTITION + "' with a value smaller than 1");
        }
        return (this.partitionCount + 1) * invocationsPerPartition;
    }

    boolean isEnabled() {
        return this.enabled;
    }

    int getMaxConcurrentInvocations() {
        if (this.enabled) {
            return this.maxConcurrentInvocations;
        }
        return Integer.MAX_VALUE;
    }

    CallIdSequence newCallIdSequence() {
        return CallIdFactory.newCallIdSequence(this.enabled, this.maxConcurrentInvocations, this.backoffTimeoutMs);
    }

    boolean isSyncForced(BackupAwareOperation backupAwareOp) {
        int current;
        if (this.disabled) {
            return false;
        }
        if (backupAwareOp.getAsyncBackupCount() == 0) {
            return false;
        }
        if (backupAwareOp instanceof UrgentSystemOperation) {
            return false;
        }
        do {
            if ((current = this.syncCountdown.decrementAndGet()) <= 0) continue;
            return false;
        } while (!this.syncCountdown.compareAndSet(current, this.randomSyncDelay()));
        return true;
    }

    private int randomSyncDelay() {
        if (this.syncWindow == 1) {
            return 1;
        }
        Random random = ThreadLocalRandomProvider.get();
        int randomSyncWindow = Math.round(0.75f * (float)this.syncWindow + (float)random.nextInt(Math.round(0.5f * (float)this.syncWindow)));
        return Math.max(1, randomSyncWindow);
    }
}

