/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.impl.retention.rules.ContentType;
import java.util.Objects;

public class DeletedHistoricalVersion {
    private final ContentType contentType;
    private final boolean failed;
    private final long attachmentSize;

    private DeletedHistoricalVersion(Builder builder) {
        this.attachmentSize = builder.attachmentSize;
        this.contentType = builder.contentType;
        this.failed = builder.failed;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public long getAttachmentSize() {
        return this.attachmentSize;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DeletedHistoricalVersion)) {
            return false;
        }
        DeletedHistoricalVersion versionsRemoval = (DeletedHistoricalVersion)obj;
        return Objects.equals((Object)versionsRemoval.contentType, (Object)this.contentType) && Objects.equals(versionsRemoval.failed, this.failed) && Objects.equals(versionsRemoval.attachmentSize, this.attachmentSize);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.contentType, this.failed, this.attachmentSize});
    }

    public static class Builder {
        private ContentType contentType;
        private boolean failed;
        private long attachmentSize;

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder attachmentSize(long attachmentSize) {
            this.attachmentSize = attachmentSize;
            return this;
        }

        public Builder isFailed() {
            this.failed = true;
            return this;
        }

        public DeletedHistoricalVersion build() {
            return new DeletedHistoricalVersion(this);
        }
    }
}

