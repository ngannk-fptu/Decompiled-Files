/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index.items;

import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;

public interface IndexQueueItemsService {
    public long getQueueItemsAdded(IndexQueueType var1);

    public long getQueueItemsProcessed(IndexQueueType var1);
}

