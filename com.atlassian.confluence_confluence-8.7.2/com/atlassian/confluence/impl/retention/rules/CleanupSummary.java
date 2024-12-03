/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.rules;

import java.io.Serializable;
import java.util.Objects;

public class CleanupSummary
implements Serializable {
    private final long pageVersionsRemovedByGlobalRules;
    private final long pageVersionsRemovedBySpaceRules;
    private final long attachmentVersionsRemovedByGlobalRules;
    private final long attachmentVersionsRemovedBySpaceRules;
    private final long attachmentSizeRemovedByGlobalRules;
    private final long attachmentSizeRemovedBySpaceRules;
    private final long lastIdProcessed;

    public static CleanupSummary createDefault() {
        return new Builder().pageVersionsRemovedByGlobalRules(0L).pageVersionsRemovedBySpaceRules(0L).attachmentVersionsRemovedByGlobalRules(0L).attachmentVersionsRemovedBySpaceRules(0L).attachmentSizeRemovedByGlobalRules(0L).attachmentSizeRemovedBySpaceRules(0L).build();
    }

    public CleanupSummary(Builder builder) {
        this.pageVersionsRemovedByGlobalRules = builder.pageVersionsRemovedByGlobalRules;
        this.pageVersionsRemovedBySpaceRules = builder.pageVersionsRemovedBySpaceRules;
        this.attachmentVersionsRemovedByGlobalRules = builder.attachmentVersionsRemovedByGlobalRules;
        this.attachmentVersionsRemovedBySpaceRules = builder.attachmentVersionsRemovedBySpaceRules;
        this.attachmentSizeRemovedByGlobalRules = builder.attachmentSizeRemovedByGlobalRules;
        this.attachmentSizeRemovedBySpaceRules = builder.attachmentSizeRemovedBySpaceRules;
        this.lastIdProcessed = builder.lastIdProcessed;
    }

    public long getPageVersionsRemovedByGlobalRules() {
        return this.pageVersionsRemovedByGlobalRules;
    }

    public long getPageVersionsRemovedBySpaceRules() {
        return this.pageVersionsRemovedBySpaceRules;
    }

    public long getAttachmentVersionsRemovedByGlobalRules() {
        return this.attachmentVersionsRemovedByGlobalRules;
    }

    public long getAttachmentVersionsRemovedBySpaceRules() {
        return this.attachmentVersionsRemovedBySpaceRules;
    }

    public long getAttachmentSizeRemovedByGlobalRules() {
        return this.attachmentSizeRemovedByGlobalRules;
    }

    public long getAttachmentSizeRemovedBySpaceRules() {
        return this.attachmentSizeRemovedBySpaceRules;
    }

    public long getLastIdProcessed() {
        return this.lastIdProcessed;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CleanupSummary)) {
            return false;
        }
        CleanupSummary versionsRemoval = (CleanupSummary)obj;
        return Objects.equals(versionsRemoval.pageVersionsRemovedByGlobalRules, this.pageVersionsRemovedByGlobalRules) && Objects.equals(versionsRemoval.pageVersionsRemovedBySpaceRules, this.pageVersionsRemovedBySpaceRules) && Objects.equals(versionsRemoval.attachmentVersionsRemovedByGlobalRules, this.attachmentVersionsRemovedByGlobalRules) && Objects.equals(versionsRemoval.attachmentVersionsRemovedBySpaceRules, this.attachmentVersionsRemovedBySpaceRules) && Objects.equals(versionsRemoval.attachmentSizeRemovedByGlobalRules, this.attachmentSizeRemovedByGlobalRules) && Objects.equals(versionsRemoval.attachmentSizeRemovedBySpaceRules, this.attachmentSizeRemovedBySpaceRules) && Objects.equals(versionsRemoval.lastIdProcessed, this.lastIdProcessed);
    }

    public int hashCode() {
        return Objects.hash(this.pageVersionsRemovedByGlobalRules, this.pageVersionsRemovedBySpaceRules, this.attachmentVersionsRemovedByGlobalRules, this.attachmentVersionsRemovedBySpaceRules, this.attachmentSizeRemovedByGlobalRules, this.attachmentSizeRemovedBySpaceRules, this.lastIdProcessed);
    }

    public String toString() {
        return "CleanupSummary{pageVersionsRemovedByGlobalRules=" + this.pageVersionsRemovedByGlobalRules + ", pageVersionsRemovedBySpaceRules=" + this.pageVersionsRemovedBySpaceRules + ", attachmentVersionsRemovedByGlobalRules=" + this.attachmentVersionsRemovedByGlobalRules + ", attachmentVersionsRemovedBySpaceRules=" + this.attachmentVersionsRemovedBySpaceRules + ", attachmentSizeRemovedByGlobalRules=" + this.attachmentSizeRemovedByGlobalRules + ", attachmentSizeRemovedBySpaceRules=" + this.attachmentSizeRemovedBySpaceRules + ", lastIdProcessed=" + this.lastIdProcessed + "}";
    }

    public static class Builder {
        private long pageVersionsRemovedByGlobalRules;
        private long pageVersionsRemovedBySpaceRules;
        private long attachmentVersionsRemovedByGlobalRules;
        private long attachmentVersionsRemovedBySpaceRules;
        private long attachmentSizeRemovedByGlobalRules;
        private long attachmentSizeRemovedBySpaceRules;
        private long lastIdProcessed;

        public Builder pageVersionsRemovedByGlobalRules(long pageVersionsRemovedByGlobalRules) {
            this.pageVersionsRemovedByGlobalRules = pageVersionsRemovedByGlobalRules;
            return this;
        }

        public Builder pageVersionsRemovedBySpaceRules(long pageVersionsRemovedBySpaceRules) {
            this.pageVersionsRemovedBySpaceRules = pageVersionsRemovedBySpaceRules;
            return this;
        }

        public Builder attachmentVersionsRemovedByGlobalRules(long attachmentVersionsRemovedByGlobalRules) {
            this.attachmentVersionsRemovedByGlobalRules = attachmentVersionsRemovedByGlobalRules;
            return this;
        }

        public Builder attachmentVersionsRemovedBySpaceRules(long attachmentVersionsRemovedBySpaceRules) {
            this.attachmentVersionsRemovedBySpaceRules = attachmentVersionsRemovedBySpaceRules;
            return this;
        }

        public Builder attachmentSizeRemovedByGlobalRules(long attachmentSizeRemovedByGlobalRules) {
            this.attachmentSizeRemovedByGlobalRules = attachmentSizeRemovedByGlobalRules;
            return this;
        }

        public Builder attachmentSizeRemovedBySpaceRules(long attachmentSizeRemovedBySpaceRules) {
            this.attachmentSizeRemovedBySpaceRules = attachmentSizeRemovedBySpaceRules;
            return this;
        }

        public Builder lastIdProcessed(long lastIdProcessed) {
            this.lastIdProcessed = lastIdProcessed;
            return this;
        }

        public CleanupSummary build() {
            return new CleanupSummary(this);
        }
    }
}

