/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index;

import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;
import java.util.EnumMap;

public class IndexQueueSizeMetric {
    private final EnumMap<IndexQueueType, Long> queueSizes = new EnumMap(IndexQueueType.class);

    public IndexQueueSizeMetric(long mainIndexSize, long changeIndexSize, long edgeIndexQueueSize) {
        this.queueSizes.put(IndexQueueType.MAIN, mainIndexSize);
        this.queueSizes.put(IndexQueueType.CHANGE, changeIndexSize);
        this.queueSizes.put(IndexQueueType.EDGE, edgeIndexQueueSize);
    }

    public long getQueueSize(IndexQueueType indexQueueType) {
        return this.queueSizes.getOrDefault((Object)indexQueueType, 0L);
    }
}

