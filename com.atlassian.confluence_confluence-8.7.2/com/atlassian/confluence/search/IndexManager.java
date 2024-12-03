/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface IndexManager {
    public static final String REINDEX_SPACES_DARK_FEATURE = "confluence.reindex.spaces";

    public boolean isFlushing();

    default public boolean isReIndexing() {
        ReIndexTask reindexingTask = this.getLastReindexingTask();
        return reindexingTask != null && !reindexingTask.isFinishedReindexing();
    }

    public boolean flushQueue(IndexQueueFlushMode var1);

    default public boolean flushQueue() {
        return this.flushQueue(IndexQueueFlushMode.ENTIRE_QUEUE);
    }

    default public ReIndexTask reIndex() {
        return this.reIndex(ReIndexOption.fullReindex());
    }

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1);

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1, SearchQuery var2);

    public ReIndexTask reIndex(EnumSet<ReIndexOption> var1, @NonNull List<String> var2);

    public void unIndexAll();

    public ReIndexTask getLastReindexingTask();

    public void resetIndexQueue();

    @Deprecated
    public IndexTaskQueue getTaskQueue();

    public FlushStatistics getLastNonEmptyFlushStats();

    public int getQueueSize();

    public void addTask(ConfluenceIndexTask var1);

    public static enum IndexQueueFlushMode {
        ENTIRE_QUEUE,
        ONLY_FIRST_BATCH;

    }
}

