/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.core.bean.EntityObject;
import java.util.Collections;
import java.util.List;

public class OrderedEntityObjectBatchableWorkSource<T extends EntityObject>
implements BatchableWorkSource<T> {
    private int numberOfBatches = -1;
    private int batchesRetrieved = 0;
    private long nextStartContentId = -1L;
    private final int batchSize;
    private final EntitySource<T> entitySource;

    public OrderedEntityObjectBatchableWorkSource(int batchSize, EntitySource<T> entitySource) {
        this.batchSize = batchSize;
        this.entitySource = entitySource;
    }

    @Override
    public synchronized List<T> getBatch() {
        long endContentId;
        if (!this.hasMoreBatches()) {
            return Collections.emptyList();
        }
        long startContentId = Math.max(0L, this.nextStartContentId);
        List<Long> contentIds = this.entitySource.getLatestEntityIds(startContentId, this.batchSize + 1);
        if (contentIds.size() > this.batchSize) {
            endContentId = contentIds.get(this.batchSize - 1);
            this.nextStartContentId = contentIds.get(contentIds.size() - 1);
        } else {
            endContentId = contentIds.get(contentIds.size() - 1);
            this.nextStartContentId = Long.MAX_VALUE;
        }
        try {
            List<T> list = this.entitySource.getEntityObjects(startContentId, endContentId);
            return list;
        }
        catch (RuntimeException ex) {
            throw new EntityRetrievalException(this.batchesRetrieved + 1, startContentId, endContentId, ex);
        }
        finally {
            ++this.batchesRetrieved;
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
            this.numberOfBatches = OrderedEntityObjectBatchableWorkSource.calculateNumberOfBatches(count, this.batchSize);
        }
        return this.numberOfBatches;
    }

    @Override
    public int getTotalSize() {
        return this.entitySource.getTotalSize();
    }

    @Override
    public synchronized void reset(int total) {
        this.numberOfBatches = OrderedEntityObjectBatchableWorkSource.calculateNumberOfBatches(total, this.batchSize);
        this.batchesRetrieved = 0;
        this.nextStartContentId = -1L;
    }

    private static int calculateNumberOfBatches(int total, int batchSize) {
        return total / batchSize + (total % batchSize > 0 ? 1 : 0);
    }

    public static class EntityRetrievalException
    extends RuntimeException {
        EntityRetrievalException(int batchNumber, long startContentId, long endContentId, Exception cause) {
            super(String.format("Failed to retrieve batch %s, covering entity IDs %s to %s inclusive", batchNumber, startContentId, endContentId), cause);
        }
    }

    protected static interface EntitySource<T extends EntityObject> {
        public List<Long> getLatestEntityIds(long var1, int var3);

        public List<T> getEntityObjects(long var1, long var3);

        public int getTotalSize();
    }
}

