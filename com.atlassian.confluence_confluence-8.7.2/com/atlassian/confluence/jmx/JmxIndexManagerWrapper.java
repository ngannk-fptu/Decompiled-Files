/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.ReIndexTask;
import java.util.Date;

public class JmxIndexManagerWrapper {
    private IndexManager indexManager;

    public JmxIndexManagerWrapper(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public boolean isFlushing() {
        return this.indexManager.isFlushing();
    }

    public boolean isReIndexing() {
        return this.indexManager.isReIndexing();
    }

    public String getLastReindexingTaskName() {
        ReIndexTask reindexingTask = this.indexManager.getLastReindexingTask();
        if (reindexingTask == null) {
            return null;
        }
        return reindexingTask.getName();
    }

    public int getTaskQueueLength() {
        return this.indexManager.getQueueSize();
    }

    public Date getLastStarted() {
        FlushStatistics flushStatistics = this.indexManager.getLastNonEmptyFlushStats();
        if (flushStatistics == null) {
            return null;
        }
        return flushStatistics.getStarted();
    }

    public long getLastElapsedMilliseconds() {
        FlushStatistics flushStatistics = this.indexManager.getLastNonEmptyFlushStats();
        if (flushStatistics == null) {
            return -1L;
        }
        return flushStatistics.getElapsedMilliseconds();
    }

    public boolean getLastWasRecreated() {
        FlushStatistics flushStatistics = this.indexManager.getLastNonEmptyFlushStats();
        return flushStatistics != null && flushStatistics.wasRecreated();
    }
}

