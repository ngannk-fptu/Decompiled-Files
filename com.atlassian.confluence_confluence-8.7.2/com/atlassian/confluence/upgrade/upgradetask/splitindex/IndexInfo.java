/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.upgrade.upgradetask.splitindex;

import org.checkerframework.checker.nullness.qual.NonNull;

public class IndexInfo {
    private final boolean valid;
    private final boolean emptyFolder;
    private final boolean oldVersion;
    private final long documents;
    private final long max;
    private final long deleted;
    private final long content;
    private final long changes;

    private IndexInfo(@NonNull boolean valid, @NonNull boolean emptyFolder, @NonNull boolean oldVersion, @NonNull long documents, @NonNull long max, @NonNull long deleted, @NonNull long content, @NonNull long changes) {
        this.valid = valid;
        this.emptyFolder = emptyFolder;
        this.oldVersion = oldVersion;
        this.documents = documents;
        this.max = max;
        this.deleted = deleted;
        this.content = content;
        this.changes = changes;
    }

    public @NonNull boolean isValid() {
        return this.valid;
    }

    public @NonNull boolean isEmptyFolder() {
        return this.emptyFolder;
    }

    public @NonNull boolean isOldVersion() {
        return this.oldVersion;
    }

    public @NonNull long getDocuments() {
        return this.documents;
    }

    public @NonNull long getMax() {
        return this.max;
    }

    public @NonNull long getDeleted() {
        return this.deleted;
    }

    public @NonNull long getContent() {
        return this.content;
    }

    public @NonNull long getChanges() {
        return this.changes;
    }

    public boolean hasDocuments() {
        return this.documents > 0L;
    }

    public boolean hasContent() {
        return this.content > 0L;
    }

    public boolean hasChanges() {
        return this.changes > 0L;
    }

    public String toString() {
        return "IndexInfo{valid=" + this.valid + ", emptyFolder=" + this.emptyFolder + ", oldVersion=" + this.oldVersion + ", documents=" + this.documents + ", max=" + this.max + ", deleted=" + this.deleted + ", content=" + this.content + ", changes=" + this.changes + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IndexInfo indexInfo = (IndexInfo)o;
        if (this.valid != indexInfo.valid) {
            return false;
        }
        if (this.emptyFolder != indexInfo.emptyFolder) {
            return false;
        }
        if (this.oldVersion != indexInfo.oldVersion) {
            return false;
        }
        if (this.documents != indexInfo.documents) {
            return false;
        }
        if (this.max != indexInfo.max) {
            return false;
        }
        if (this.deleted != indexInfo.deleted) {
            return false;
        }
        if (this.content != indexInfo.content) {
            return false;
        }
        return this.changes == indexInfo.changes;
    }

    public int hashCode() {
        int result = this.valid ? 1 : 0;
        result = 31 * result + (this.emptyFolder ? 1 : 0);
        result = 31 * result + (this.oldVersion ? 1 : 0);
        result = 31 * result + (int)(this.documents ^ this.documents >>> 32);
        result = 31 * result + (int)(this.max ^ this.max >>> 32);
        result = 31 * result + (int)(this.deleted ^ this.deleted >>> 32);
        result = 31 * result + (int)(this.content ^ this.content >>> 32);
        result = 31 * result + (int)(this.changes ^ this.changes >>> 32);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean valid = true;
        private boolean emptyFolder = false;
        private boolean oldVersion = false;
        private long documents = 0L;
        private long max = 0L;
        private long deleted = 0L;
        private long content = 0L;
        private long changes = 0L;

        public Builder withValid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder withEmptyFolder(boolean emptyFolder) {
            this.emptyFolder = emptyFolder;
            return this;
        }

        public Builder withOldVersion(boolean oldVersion) {
            this.oldVersion = oldVersion;
            return this;
        }

        public Builder withDocuments(long documents) {
            this.documents = documents;
            return this;
        }

        public Builder withMax(long max) {
            this.max = max;
            return this;
        }

        public Builder withDeleted(long deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder withContent(long content) {
            this.content = content;
            return this;
        }

        public Builder withChanges(long changes) {
            this.changes = changes;
            return this;
        }

        public IndexInfo build() {
            return new IndexInfo(this.valid, this.emptyFolder, this.oldVersion, this.documents, this.max, this.deleted, this.content, this.changes);
        }
    }
}

