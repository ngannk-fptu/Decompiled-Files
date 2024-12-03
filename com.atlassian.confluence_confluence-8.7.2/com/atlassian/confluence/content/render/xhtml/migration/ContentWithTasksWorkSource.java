/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.PageManager;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ContentWithTasksWorkSource
implements BatchableWorkSource<ContentEntityObject> {
    private final PageManager pageManager;
    private final int batchSize;
    private int numberOfBatches = -1;
    private long startContentId = -1L;
    private boolean hasMoreBatches = true;
    private long highestCeoId = -1L;

    public ContentWithTasksWorkSource(PageManager pageManager, int batchSize) {
        this.pageManager = pageManager;
        this.batchSize = batchSize;
    }

    @Override
    public synchronized List<ContentEntityObject> getBatch() {
        List<ContentEntityObject> result;
        if (!this.hasMoreBatches()) {
            return Collections.emptyList();
        }
        long maxId = this.getHighestContentId();
        if (this.startContentId == -1L) {
            result = this.getUnfilteredBatch(0L, maxId, this.batchSize + 1);
            this.updateStartContentIdForNextBatch(result);
        } else {
            result = this.getUnfilteredBatch(this.startContentId, maxId, this.batchSize + 1);
            this.updateStartContentIdForNextBatch(result);
        }
        result = result.subList(0, Math.min(this.batchSize, result.size()));
        ImmutableList.Builder filteredResult = ImmutableList.builder();
        for (ContentEntityObject ceo : result) {
            if (!StringUtils.contains((CharSequence)ceo.getBodyAsString(), (CharSequence)"<ac:task")) continue;
            filteredResult.add((Object)ceo);
        }
        return filteredResult.build();
    }

    private List<ContentEntityObject> getUnfilteredBatch(long minId, long maxId, int batchSize) {
        return this.pageManager.getOrderedXhtmlContentFromContentId(minId, maxId, batchSize);
    }

    private void updateStartContentIdForNextBatch(List<ContentEntityObject> contentIds) {
        boolean bl = this.hasMoreBatches = contentIds.size() > this.batchSize;
        if (this.hasMoreBatches) {
            this.startContentId = contentIds.get(contentIds.size() - 1).getId();
        }
    }

    @Override
    public synchronized boolean hasMoreBatches() {
        return this.hasMoreBatches;
    }

    @Override
    public synchronized int numberOfBatches() {
        if (this.numberOfBatches == -1) {
            int count = this.getTotalSize();
            this.numberOfBatches = ContentWithTasksWorkSource.calculateNumberOfBatches(count, this.batchSize);
        }
        return this.numberOfBatches;
    }

    private static int calculateNumberOfBatches(int total, int batchSize) {
        return total / batchSize + (total % batchSize > 0 ? 1 : 0);
    }

    @Override
    public synchronized void reset(int total) {
        this.numberOfBatches = -1;
        this.startContentId = -1L;
        this.hasMoreBatches = true;
    }

    @Override
    public synchronized int getTotalSize() {
        return this.pageManager.getCountOfLatestXhtmlContent(this.getHighestContentId());
    }

    private long getHighestContentId() {
        if (this.highestCeoId == -1L) {
            this.highestCeoId = this.pageManager.getHighestCeoId();
        }
        return this.highestCeoId;
    }
}

