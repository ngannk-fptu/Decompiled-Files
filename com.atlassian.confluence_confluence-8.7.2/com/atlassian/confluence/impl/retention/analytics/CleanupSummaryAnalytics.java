/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.core.util.FileSize;
import com.atlassian.event.api.AsynchronousPreferred;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CleanupSummaryAnalytics {

    @AsynchronousPreferred
    @EventName(value="attachment.removal.job")
    public static class AttachmentRemovalJobEvent {
        private final String type;
        private final long totalRemoved;
        private final BigDecimal totalSizeRemovedGlobal;
        private final BigDecimal totalSizeRemovedSpace;

        public AttachmentRemovalJobEvent(String type, long totalRemoved, long totalSizeRemovedGlobal, long totalSizeRemovedSpace) {
            this.type = type;
            this.totalRemoved = totalRemoved;
            this.totalSizeRemovedGlobal = this.getFileSizeInMb(totalSizeRemovedGlobal);
            this.totalSizeRemovedSpace = this.getFileSizeInMb(totalSizeRemovedSpace);
        }

        public String getType() {
            return this.type;
        }

        public long getTotalRemoved() {
            return this.totalRemoved;
        }

        public BigDecimal getTotalSizeRemovedGlobal() {
            return this.totalSizeRemovedGlobal;
        }

        public BigDecimal getTotalSizeRemovedSpace() {
            return this.totalSizeRemovedSpace;
        }

        public int hashCode() {
            return Objects.hash(this.type, this.totalRemoved, this.totalSizeRemovedGlobal, this.totalSizeRemovedSpace);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AttachmentRemovalJobEvent)) {
                return false;
            }
            AttachmentRemovalJobEvent attachmentRemovalJobEvent = (AttachmentRemovalJobEvent)obj;
            return Objects.equals(attachmentRemovalJobEvent.type, this.type) && Objects.equals(attachmentRemovalJobEvent.totalRemoved, this.totalRemoved) && Objects.equals(attachmentRemovalJobEvent.totalSizeRemovedGlobal, this.totalSizeRemovedGlobal) && Objects.equals(attachmentRemovalJobEvent.totalSizeRemovedSpace, this.totalSizeRemovedSpace);
        }

        private BigDecimal getFileSizeInMb(long fileSize) {
            if (fileSize > 0L) {
                return BigDecimal.valueOf(FileSize.convertBytesToMB((long)fileSize)).setScale(2, RoundingMode.HALF_UP);
            }
            return new BigDecimal(0);
        }
    }

    @AsynchronousPreferred
    @EventName(value="page.removal.job")
    public static class PageRemovalJobEvent {
        private final String type;
        private final long totalRemovedByGlobalPolicy;
        private final long totalRemovedBySpacePolicy;

        public PageRemovalJobEvent(String type, long totalRemovedByGlobalPolicy, long totalRemovedBySpacePolicy) {
            this.type = type;
            this.totalRemovedByGlobalPolicy = totalRemovedByGlobalPolicy;
            this.totalRemovedBySpacePolicy = totalRemovedBySpacePolicy;
        }

        public String getType() {
            return this.type;
        }

        public long getTotalRemovedByGlobalPolicy() {
            return this.totalRemovedByGlobalPolicy;
        }

        public long getTotalRemovedBySpacePolicy() {
            return this.totalRemovedBySpacePolicy;
        }

        public int hashCode() {
            return Objects.hash(this.type, this.totalRemovedByGlobalPolicy, this.totalRemovedBySpacePolicy);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PageRemovalJobEvent)) {
                return false;
            }
            PageRemovalJobEvent pageRemovalJobEvent = (PageRemovalJobEvent)obj;
            return Objects.equals(pageRemovalJobEvent.type, this.type) && Objects.equals(pageRemovalJobEvent.totalRemovedByGlobalPolicy, this.totalRemovedByGlobalPolicy) && Objects.equals(pageRemovalJobEvent.totalRemovedBySpacePolicy, this.totalRemovedBySpacePolicy);
        }
    }
}

