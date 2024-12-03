/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.core.util.FileSize
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalJobCompletedEvent;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatistic;
import com.atlassian.core.util.FileSize;
import java.util.Objects;

@EventName(value="trash.removal.job")
public class TrashRemovalJobAnalyticsEvent {
    private final String type;
    private final int totalPageGlobal;
    private final int totalPageSpace;
    private final int totalAttachmentGlobal;
    private final int totalAttachmentSpace;
    private final long totalAttachmentSizeGlobal;
    private final long totalAttachmentSizeSpace;

    public TrashRemovalJobAnalyticsEvent(TrashRemovalJobCompletedEvent removalJobCompletedEvent) {
        Objects.requireNonNull(removalJobCompletedEvent);
        this.type = removalJobCompletedEvent.getType().getName();
        TrashRemovalStatistic globalStats = removalJobCompletedEvent.getStatisticHolder().getGlobalStats();
        this.totalPageGlobal = globalStats.getPurgedPageCnt();
        this.totalAttachmentGlobal = globalStats.getPurgedAttachmentCnt();
        this.totalAttachmentSizeGlobal = (long)FileSize.convertBytesToMB((long)globalStats.getPurgedAttachmentTotalSizeInBytes());
        TrashRemovalStatistic exemptionStats = removalJobCompletedEvent.getStatisticHolder().getSpaceStats();
        this.totalPageSpace = exemptionStats.getPurgedPageCnt();
        this.totalAttachmentSpace = exemptionStats.getPurgedAttachmentCnt();
        this.totalAttachmentSizeSpace = (long)FileSize.convertBytesToMB((long)exemptionStats.getPurgedAttachmentTotalSizeInBytes());
    }

    public String getType() {
        return this.type;
    }

    public int getTotalPageGlobal() {
        return this.totalPageGlobal;
    }

    public int getTotalPageSpace() {
        return this.totalPageSpace;
    }

    public int getTotalAttachmentGlobal() {
        return this.totalAttachmentGlobal;
    }

    public int getTotalAttachmentSpace() {
        return this.totalAttachmentSpace;
    }

    public long getTotalAttachmentSizeGlobal() {
        return this.totalAttachmentSizeGlobal;
    }

    public long getTotalAttachmentSizeSpace() {
        return this.totalAttachmentSizeSpace;
    }
}

