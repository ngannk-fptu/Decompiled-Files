/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.rules;

import java.util.Objects;

public class DeletedHistoricalVersionSummary {
    private final long pageVersionsRemoved;
    private final long attachmentVersionsRemoved;
    private final long attachmentSizeRemoved;
    private final long lastIdProcessed;

    public DeletedHistoricalVersionSummary(Builder builder) {
        this.pageVersionsRemoved = builder.pageVersionsRemoved;
        this.attachmentVersionsRemoved = builder.attachmentVersionsRemoved;
        this.attachmentSizeRemoved = builder.attachmentSizeRemoved;
        this.lastIdProcessed = builder.lastIdProcessed;
    }

    public long getPageVersionsRemoved() {
        return this.pageVersionsRemoved;
    }

    public long getAttachmentVersionsRemoved() {
        return this.attachmentVersionsRemoved;
    }

    public long getAttachmentSizeRemoved() {
        return this.attachmentSizeRemoved;
    }

    public long getLastIdProcessed() {
        return this.lastIdProcessed;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DeletedHistoricalVersionSummary)) {
            return false;
        }
        DeletedHistoricalVersionSummary versionsRemoval = (DeletedHistoricalVersionSummary)obj;
        return Objects.equals(versionsRemoval.pageVersionsRemoved, this.pageVersionsRemoved) && Objects.equals(versionsRemoval.attachmentVersionsRemoved, this.attachmentVersionsRemoved) && Objects.equals(versionsRemoval.attachmentSizeRemoved, this.attachmentSizeRemoved) && Objects.equals(versionsRemoval.lastIdProcessed, this.lastIdProcessed);
    }

    public int hashCode() {
        return Objects.hash(this.pageVersionsRemoved, this.attachmentVersionsRemoved, this.attachmentSizeRemoved, this.lastIdProcessed);
    }

    public static class Builder {
        private long pageVersionsRemoved;
        private long attachmentVersionsRemoved;
        private long attachmentSizeRemoved;
        private long lastIdProcessed;

        public Builder pageVersionsRemoved(long globalPageVersionsRemoved) {
            this.pageVersionsRemoved = globalPageVersionsRemoved;
            return this;
        }

        public Builder attachmentVersionsRemoved(long globalAttachmentVersionsRemoved) {
            this.attachmentVersionsRemoved = globalAttachmentVersionsRemoved;
            return this;
        }

        public Builder attachmentSizeRemoved(long globalAttachmentSizeRemoved) {
            this.attachmentSizeRemoved = globalAttachmentSizeRemoved;
            return this;
        }

        public Builder lastIdProcessed(long lastIdProcessed) {
            this.lastIdProcessed = lastIdProcessed;
            return this;
        }

        public DeletedHistoricalVersionSummary build() {
            return new DeletedHistoricalVersionSummary(this);
        }
    }
}

