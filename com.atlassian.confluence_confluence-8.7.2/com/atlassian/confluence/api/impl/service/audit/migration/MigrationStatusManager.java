/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.audit.migration;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MigrationStatusManager
implements BiConsumer<Integer, Integer> {
    private static final Logger log = LoggerFactory.getLogger(MigrationStatusManager.class);
    private static final int NUM_RECORDS_TILL_UPDATE = 10000;
    private final Instant startTime = Instant.now();
    private final int numOutstanding;
    private final Consumer<String> statusConsumer;
    private final AtomicInteger numMigrated = new AtomicInteger(0);
    private final AtomicInteger numFailed = new AtomicInteger(0);
    private volatile int nextUpdate = 10000;

    MigrationStatusManager(int numOutstanding, Consumer<String> statusConsumer) {
        this.numOutstanding = numOutstanding;
        this.statusConsumer = Objects.requireNonNull(statusConsumer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void accept(Integer successCount, Integer errorCount) {
        int numProcessed = this.numMigrated.addAndGet(successCount) + this.numFailed.addAndGet(errorCount);
        MigrationStatusManager migrationStatusManager = this;
        synchronized (migrationStatusManager) {
            if (numProcessed >= this.nextUpdate) {
                String update = String.format("Processed %1$d / %2$d audit records. Success: %3$d. Error: %4$d. Percentage: %5$.2f %%", numProcessed, this.numOutstanding, this.numMigrated.get(), this.numFailed.get(), (double)numProcessed * 100.0 / (double)this.numOutstanding);
                this.statusConsumer.accept(update);
                this.nextUpdate += 10000;
            }
        }
    }

    public void waitUntilCompletion(long sleepTimeMs) {
        while (this.numMigrated.get() + this.numFailed.get() < this.numOutstanding) {
            try {
                log.debug("There are still entities waiting to be migrated. Checking again in 3 seconds");
                Thread.sleep(sleepTimeMs);
            }
            catch (InterruptedException e) {
                log.error("Interrupted while waiting for results", (Throwable)e);
                Thread.currentThread().interrupt();
            }
        }
        Duration duration = Duration.between(this.startTime, Instant.now());
        String completeUpdate = String.format("All audit records have been migrated. Total count: %1$d, Success: %2$d. Error: %3$d. Elapsed time: %4$d minutes. Throughput: %5$d records/minute.", this.numOutstanding, this.numMigrated.get(), this.numFailed.get(), duration.toMinutes(), (long)this.numMigrated.get() / Math.max(duration.toMinutes(), 1L));
        this.statusConsumer.accept(completeUpdate);
    }
}

