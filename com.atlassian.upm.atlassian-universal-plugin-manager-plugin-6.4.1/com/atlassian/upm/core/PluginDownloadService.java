/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.upm.core;

import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.upm.api.util.Option;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PluginDownloadService {
    public static final ProgressTracker NULL_TRACKER = new ProgressTracker(){

        @Override
        public void notify(Progress p) {
        }

        @Override
        public void redirectedTo(URI newUri) {
        }
    };

    public DownloadResult downloadPlugin(URI var1, Option<String> var2, ProgressTracker var3) throws ResponseException, FileNotFoundException;

    public DownloadResult downloadPlugin(URI var1, Option<String> var2, String[] var3, ProgressTracker var4) throws ResponseException, FileNotFoundException;

    public static final class Progress {
        private final long amountDownloaded;
        private final Long totalSize;
        private final Option<String> source;

        public Progress(long amountDownloaded, Long totalSize, Option<String> source) {
            this.amountDownloaded = amountDownloaded;
            this.totalSize = totalSize;
            this.source = source;
        }

        public long getAmountDownloaded() {
            return this.amountDownloaded;
        }

        public Long getTotalSize() {
            return this.totalSize;
        }

        public Option<String> getSource() {
            return this.source;
        }

        public String toString() {
            if (this.totalSize != null) {
                return this.amountDownloaded + "/" + this.totalSize + "(" + Math.round((double)this.amountDownloaded / (double)this.totalSize.longValue() * 10.0) * 10L + "%)";
            }
            return Long.toString(this.amountDownloaded);
        }
    }

    public static final class DownloadResult {
        private static final List<String> CONTENT_TYPES_TO_IGNORE = Collections.unmodifiableList(Arrays.asList("application/octet-stream", "application/x-upload-data"));
        private final File file;
        private final String name;
        private final Option<String> contentType;

        public DownloadResult(File file, String name, Option<String> contentType) {
            this.file = file;
            this.name = name;
            this.contentType = contentType;
        }

        public File getFile() {
            return this.file;
        }

        public String getName() {
            return this.name;
        }

        public Option<String> getContentType() {
            for (String value : this.contentType) {
                String ct = value.split(";")[0];
                if (CONTENT_TYPES_TO_IGNORE.contains(ct)) continue;
                return Option.some(value);
            }
            return Option.none(String.class);
        }
    }

    public static interface ProgressTracker {
        public void notify(Progress var1);

        public void redirectedTo(URI var1);
    }
}

