/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.impl.upgrade.upgradetask;

import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import java.util.Map;
import java.util.stream.Stream;

public class ReindexAllBlogsUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final Map<SearchIndex, IndexTaskQueue<IndexTask>> taskQueueBySearchIndex;

    public ReindexAllBlogsUpgradeTask(IndexTaskFactoryInternal indexTaskFactory, Map<SearchIndex, IndexTaskQueue<IndexTask>> taskQueueBySearchIndex) {
        this.indexTaskFactory = indexTaskFactory;
        this.taskQueueBySearchIndex = taskQueueBySearchIndex;
    }

    public void doUpgrade() throws Exception {
        Stream.of(this.indexTaskFactory.createReindexAllBlogsContentTask(), this.indexTaskFactory.createReindexAllBlogsChangeTask()).forEach(task -> this.taskQueueBySearchIndex.get((Object)task.getSearchIndex()).enqueue((IndexTask)task));
    }

    public String getBuildNumber() {
        return "7110";
    }

    public String getShortDescription() {
        return "Reindex all blogs.";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

