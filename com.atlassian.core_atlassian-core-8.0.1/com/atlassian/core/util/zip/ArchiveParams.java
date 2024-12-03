/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.core.util.zip;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ArchiveParams {
    private final String archiveFolderName;
    private final boolean includeHiddenFiles;

    private ArchiveParams(Builder builder) {
        this.archiveFolderName = builder.archiveFolderName;
        this.includeHiddenFiles = builder.includeHiddenFiles;
    }

    public String getArchiveFolderName() {
        return this.archiveFolderName;
    }

    public boolean isIncludeHiddenFiles() {
        return this.includeHiddenFiles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String archiveFolderName;
        private boolean includeHiddenFiles = true;

        private Builder() {
        }

        public Builder withArchiveFolderName(String archiveFolderName) {
            this.archiveFolderName = archiveFolderName;
            return this;
        }

        public Builder withIncludeHiddenFiles(boolean includeHiddenFiles) {
            this.includeHiddenFiles = includeHiddenFiles;
            return this;
        }

        public ArchiveParams build() {
            return new ArchiveParams(this);
        }
    }
}

