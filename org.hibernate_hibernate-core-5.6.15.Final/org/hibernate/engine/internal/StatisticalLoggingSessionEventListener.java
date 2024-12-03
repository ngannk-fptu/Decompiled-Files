/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import org.hibernate.BaseSessionEventListener;
import org.jboss.logging.Logger;

public class StatisticalLoggingSessionEventListener
extends BaseSessionEventListener {
    private static final Logger log = Logger.getLogger(StatisticalLoggingSessionEventListener.class);
    private int jdbcConnectionAcquisitionCount;
    private long jdbcConnectionAcquisitionTime;
    private int jdbcConnectionReleaseCount;
    private long jdbcConnectionReleaseTime;
    private int jdbcPrepareStatementCount;
    private long jdbcPrepareStatementTime;
    private int jdbcExecuteStatementCount;
    private long jdbcExecuteStatementTime;
    private int jdbcExecuteBatchCount;
    private long jdbcExecuteBatchTime;
    private int cachePutCount;
    private long cachePutTime;
    private int cacheHitCount;
    private long cacheHitTime;
    private int cacheMissCount;
    private long cacheMissTime;
    private int flushCount;
    private long flushEntityCount;
    private long flushCollectionCount;
    private long flushTime;
    private int partialFlushCount;
    private long partialFlushEntityCount;
    private long partialFlushCollectionCount;
    private long partialFlushTime;
    private long jdbcConnectionAcquisitionStart = -1L;
    private long jdbcConnectionReleaseStart = -1L;
    private long jdbcPrepStart = -1L;
    private long jdbcExecutionStart = -1L;
    private long jdbcBatchExecutionStart = -1L;
    private long cachePutStart = -1L;
    private long cacheGetStart = -1L;
    private long flushStart = -1L;
    private long partialFlushStart = -1L;

    public static boolean isLoggingEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void jdbcConnectionAcquisitionStart() {
        assert (this.jdbcConnectionAcquisitionStart < 0L) : "Nested calls to jdbcConnectionAcquisitionStart";
        this.jdbcConnectionAcquisitionStart = System.nanoTime();
    }

    @Override
    public void jdbcConnectionAcquisitionEnd() {
        assert (this.jdbcConnectionAcquisitionStart > 0L) : "Unexpected call to jdbcConnectionAcquisitionEnd; expecting jdbcConnectionAcquisitionStart";
        ++this.jdbcConnectionAcquisitionCount;
        this.jdbcConnectionAcquisitionTime += System.nanoTime() - this.jdbcConnectionAcquisitionStart;
        this.jdbcConnectionAcquisitionStart = -1L;
    }

    @Override
    public void jdbcConnectionReleaseStart() {
        assert (this.jdbcConnectionReleaseStart < 0L) : "Nested calls to jdbcConnectionReleaseStart";
        this.jdbcConnectionReleaseStart = System.nanoTime();
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
        assert (this.jdbcConnectionReleaseStart > 0L) : "Unexpected call to jdbcConnectionReleaseEnd; expecting jdbcConnectionReleaseStart";
        ++this.jdbcConnectionReleaseCount;
        this.jdbcConnectionReleaseTime += System.nanoTime() - this.jdbcConnectionReleaseStart;
        this.jdbcConnectionReleaseStart = -1L;
    }

    @Override
    public void jdbcPrepareStatementStart() {
        assert (this.jdbcPrepStart < 0L) : "Nested calls to jdbcPrepareStatementStart";
        this.jdbcPrepStart = System.nanoTime();
    }

    @Override
    public void jdbcPrepareStatementEnd() {
        assert (this.jdbcPrepStart > 0L) : "Unexpected call to jdbcPrepareStatementEnd; expecting jdbcPrepareStatementStart";
        ++this.jdbcPrepareStatementCount;
        this.jdbcPrepareStatementTime += System.nanoTime() - this.jdbcPrepStart;
        this.jdbcPrepStart = -1L;
    }

    @Override
    public void jdbcExecuteStatementStart() {
        assert (this.jdbcExecutionStart < 0L) : "Nested calls to jdbcExecuteStatementStart";
        this.jdbcExecutionStart = System.nanoTime();
    }

    @Override
    public void jdbcExecuteStatementEnd() {
        assert (this.jdbcExecutionStart > 0L) : "Unexpected call to jdbcExecuteStatementEnd; expecting jdbcExecuteStatementStart";
        ++this.jdbcExecuteStatementCount;
        this.jdbcExecuteStatementTime += System.nanoTime() - this.jdbcExecutionStart;
        this.jdbcExecutionStart = -1L;
    }

    @Override
    public void jdbcExecuteBatchStart() {
        assert (this.jdbcBatchExecutionStart < 0L) : "Nested calls to jdbcExecuteBatchStart";
        this.jdbcBatchExecutionStart = System.nanoTime();
    }

    @Override
    public void jdbcExecuteBatchEnd() {
        assert (this.jdbcBatchExecutionStart > 0L) : "Unexpected call to jdbcExecuteBatchEnd; expecting jdbcExecuteBatchStart";
        ++this.jdbcExecuteBatchCount;
        this.jdbcExecuteBatchTime += System.nanoTime() - this.jdbcBatchExecutionStart;
        this.jdbcBatchExecutionStart = -1L;
    }

    @Override
    public void cachePutStart() {
        assert (this.cachePutStart < 0L) : "Nested calls to cachePutStart";
        this.cachePutStart = System.nanoTime();
    }

    @Override
    public void cachePutEnd() {
        assert (this.cachePutStart > 0L) : "Unexpected call to cachePutEnd; expecting cachePutStart";
        ++this.cachePutCount;
        this.cachePutTime += System.nanoTime() - this.cachePutStart;
        this.cachePutStart = -1L;
    }

    @Override
    public void cacheGetStart() {
        assert (this.cacheGetStart < 0L) : "Nested calls to cacheGetStart";
        this.cacheGetStart = System.nanoTime();
    }

    @Override
    public void cacheGetEnd(boolean hit) {
        assert (this.cacheGetStart > 0L) : "Unexpected call to cacheGetEnd; expecting cacheGetStart";
        if (hit) {
            ++this.cacheHitCount;
            this.cacheHitTime += System.nanoTime() - this.cacheGetStart;
        } else {
            ++this.cacheMissCount;
            this.cacheMissTime += System.nanoTime() - this.cacheGetStart;
        }
        this.cacheGetStart = -1L;
    }

    @Override
    public void flushStart() {
        assert (this.flushStart < 0L) : "Nested calls to flushStart";
        this.flushStart = System.nanoTime();
    }

    @Override
    public void flushEnd(int numberOfEntities, int numberOfCollections) {
        assert (this.flushStart > 0L) : "Unexpected call to flushEnd; expecting flushStart";
        ++this.flushCount;
        this.flushEntityCount += (long)numberOfEntities;
        this.flushCollectionCount += (long)numberOfCollections;
        this.flushTime += System.nanoTime() - this.flushStart;
        this.flushStart = -1L;
    }

    @Override
    public void partialFlushStart() {
        assert (this.partialFlushStart < 0L) : "Nested calls to partialFlushStart";
        this.partialFlushStart = System.nanoTime();
    }

    @Override
    public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
        assert (this.partialFlushStart > 0L) : "Unexpected call to partialFlushEnd; expecting partialFlushStart";
        ++this.partialFlushCount;
        this.partialFlushEntityCount += (long)numberOfEntities;
        this.partialFlushCollectionCount += (long)numberOfCollections;
        this.partialFlushTime += System.nanoTime() - this.partialFlushStart;
        this.partialFlushStart = -1L;
    }

    @Override
    public void end() {
        log.infof("Session Metrics {\n    %s nanoseconds spent acquiring %s JDBC connections;\n    %s nanoseconds spent releasing %s JDBC connections;\n    %s nanoseconds spent preparing %s JDBC statements;\n    %s nanoseconds spent executing %s JDBC statements;\n    %s nanoseconds spent executing %s JDBC batches;\n    %s nanoseconds spent performing %s L2C puts;\n    %s nanoseconds spent performing %s L2C hits;\n    %s nanoseconds spent performing %s L2C misses;\n    %s nanoseconds spent executing %s flushes (flushing a total of %s entities and %s collections);\n    %s nanoseconds spent executing %s partial-flushes (flushing a total of %s entities and %s collections)\n}", new Object[]{this.jdbcConnectionAcquisitionTime, this.jdbcConnectionAcquisitionCount, this.jdbcConnectionReleaseTime, this.jdbcConnectionReleaseCount, this.jdbcPrepareStatementTime, this.jdbcPrepareStatementCount, this.jdbcExecuteStatementTime, this.jdbcExecuteStatementCount, this.jdbcExecuteBatchTime, this.jdbcExecuteBatchCount, this.cachePutTime, this.cachePutCount, this.cacheHitTime, this.cacheHitCount, this.cacheMissTime, this.cacheMissCount, this.flushTime, this.flushCount, this.flushEntityCount, this.flushCollectionCount, this.partialFlushTime, this.partialFlushCount, this.partialFlushEntityCount, this.partialFlushCollectionCount});
    }
}

