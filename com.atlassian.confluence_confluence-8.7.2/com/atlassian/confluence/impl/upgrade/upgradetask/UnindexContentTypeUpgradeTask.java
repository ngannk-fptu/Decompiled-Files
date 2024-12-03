/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 */
package com.atlassian.confluence.impl.upgrade.upgradetask;

import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import java.util.Map;
import java.util.stream.Stream;

public abstract class UnindexContentTypeUpgradeTask
extends AbstractUpgradeTask {
    private final Map<SearchIndex, IndexTaskQueue<IndexTask>> taskQueueBySearchIndex;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final String contentTypeRepresentation;

    public UnindexContentTypeUpgradeTask(Map<SearchIndex, IndexTaskQueue<IndexTask>> taskQueueBySearchIndex, IndexTaskFactoryInternal indexTaskFactory, ContentTypeEnum contentTypeEnum) {
        this(taskQueueBySearchIndex, indexTaskFactory, contentTypeEnum.getRepresentation());
    }

    public UnindexContentTypeUpgradeTask(Map<SearchIndex, IndexTaskQueue<IndexTask>> taskQueueBySearchIndex, IndexTaskFactoryInternal indexTaskFactory, String contentTypeRepresentation) {
        this.taskQueueBySearchIndex = taskQueueBySearchIndex;
        this.indexTaskFactory = indexTaskFactory;
        this.contentTypeRepresentation = contentTypeRepresentation;
    }

    public final void doUpgrade() throws Exception {
        log.info(String.format("UnindexContentTypeUpgradeTask for %s", this.contentTypeRepresentation));
        Stream.of(this.indexTaskFactory.createUnindexContentTypeContentTask(this.contentTypeRepresentation), this.indexTaskFactory.createUnindexContentTypeChangeTask(this.contentTypeRepresentation)).forEach(task -> this.taskQueueBySearchIndex.get((Object)task.getSearchIndex()).enqueue((IndexTask)task));
        log.info("UnindexContentTypeContentTask and UnindexContentTypeChangeTask enqueued");
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

