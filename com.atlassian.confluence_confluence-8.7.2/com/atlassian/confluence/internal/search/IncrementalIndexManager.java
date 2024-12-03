/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexManager;
import org.checkerframework.checker.nullness.qual.Nullable;

@LuceneIndependent
@Internal
public interface IncrementalIndexManager {
    public boolean isFlushing();

    public boolean flushQueue(IndexManager.IndexQueueFlushMode var1);

    default public boolean flushQueue() {
        return this.flushQueue(IndexManager.IndexQueueFlushMode.ENTIRE_QUEUE);
    }

    public void resetIndexQueue();

    public @Nullable FlushStatistics getLastNonEmptyFlushStats();

    public int getQueueSize();

    public void addTask(ConfluenceIndexTask var1);
}

