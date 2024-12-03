/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Collections;
import java.util.List;

public class XhtmlContentWorkSource
implements BatchableWorkSource<ContentEntityObject> {
    private final ContentDao contentDao;
    private int numberOfBatches = -1;
    private int batchesRetrieved = 0;
    private long startContentId = -1L;
    private final int batchSize;

    public XhtmlContentWorkSource(ContentDao contentDao, int batchSize) {
        this.contentDao = contentDao;
        this.batchSize = batchSize;
    }

    @Override
    public synchronized List<ContentEntityObject> getBatch() {
        List<ContentEntityObject> result;
        if (!this.hasMoreBatches()) {
            return Collections.emptyList();
        }
        if (this.startContentId == -1L) {
            result = this.contentDao.getOrderedXhtmlContentFromContentId(0L, this.batchSize + 1);
            this.updateStartContentIdForNextBatch(result);
        } else {
            result = this.contentDao.getOrderedXhtmlContentFromContentId(this.startContentId, this.batchSize + 1);
            this.updateStartContentIdForNextBatch(result);
        }
        ++this.batchesRetrieved;
        return result.subList(0, Math.min(this.batchSize, result.size()));
    }

    private void updateStartContentIdForNextBatch(List<ContentEntityObject> contentIds) {
        if (contentIds.size() > this.batchSize) {
            this.startContentId = contentIds.get(contentIds.size() - 1).getId();
        }
    }

    @Override
    public synchronized boolean hasMoreBatches() {
        return this.batchesRetrieved < this.numberOfBatches();
    }

    @Override
    public synchronized int numberOfBatches() {
        if (this.numberOfBatches == -1) {
            int count = this.getTotalSize();
            this.numberOfBatches = XhtmlContentWorkSource.calculateNumberOfBatches(count, this.batchSize);
        }
        return this.numberOfBatches;
    }

    private static int calculateNumberOfBatches(int total, int batchSize) {
        return total / batchSize + (total % batchSize > 0 ? 1 : 0);
    }

    @Override
    public synchronized void reset(int total) {
        this.numberOfBatches = -1;
        this.batchesRetrieved = 0;
        this.startContentId = -1L;
    }

    @Override
    public synchronized int getTotalSize() {
        return this.contentDao.getCountOfXhtmlContent();
    }
}

