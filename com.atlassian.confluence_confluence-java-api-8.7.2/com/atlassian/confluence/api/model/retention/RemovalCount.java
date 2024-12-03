/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.retention;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class RemovalCount {
    @JsonProperty
    private long pageVersionsRemoved = 0L;
    @JsonProperty
    private long attachmentVersionsRemoved = 0L;
    @JsonProperty
    private long attachmentFileSize = 0L;

    public long getPageVersionsRemoved() {
        return this.pageVersionsRemoved;
    }

    public long getAttachmentVersionsRemoved() {
        return this.attachmentVersionsRemoved;
    }

    public long getAttachmentFileSize() {
        return this.attachmentFileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RemovalCount that = (RemovalCount)o;
        return this.pageVersionsRemoved == that.pageVersionsRemoved && this.attachmentVersionsRemoved == that.attachmentVersionsRemoved && this.attachmentFileSize == that.attachmentFileSize;
    }

    public int hashCode() {
        return Objects.hash(this.pageVersionsRemoved, this.attachmentVersionsRemoved, this.attachmentFileSize);
    }

    public String toString() {
        return "RemovalCount{pageVersionsRemoved=" + this.pageVersionsRemoved + ", attachmentVersionsRemoved=" + this.attachmentVersionsRemoved + ", attachmentFileSize=" + this.attachmentFileSize + '}';
    }

    public static class Builder {
        private long pageVersionsRemoved = 0L;
        private long attachmentVersionsRemoved = 0L;
        private long attachmentFileSize = 0L;

        public Builder pageVersionsRemoved(long pageVersionsRemoved) {
            this.pageVersionsRemoved = pageVersionsRemoved;
            return this;
        }

        public Builder attachmentVersionsRemoved(long attachmentVersionsRemoved) {
            this.attachmentVersionsRemoved = attachmentVersionsRemoved;
            return this;
        }

        public Builder attachmentFileSize(long attachmentFileSize) {
            this.attachmentFileSize = attachmentFileSize;
            return this;
        }

        public RemovalCount build() {
            RemovalCount removalCount = new RemovalCount();
            removalCount.pageVersionsRemoved = this.pageVersionsRemoved;
            removalCount.attachmentVersionsRemoved = this.attachmentVersionsRemoved;
            removalCount.attachmentFileSize = this.attachmentFileSize;
            return removalCount;
        }
    }
}

