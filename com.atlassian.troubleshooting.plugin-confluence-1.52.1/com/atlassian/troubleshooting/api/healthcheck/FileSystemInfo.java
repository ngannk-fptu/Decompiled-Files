/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileStore;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface FileSystemInfo {
    public FileStore getFileStore(String var1) throws IOException;

    public Optional<ThreadLimit> getThreadLimit();

    public boolean isExecutable(@Nonnull String var1);

    public List<File> listFiles(@Nonnull File var1, @Nonnull FilenameFilter var2);

    public static class ThreadLimit {
        private final int value;

        private ThreadLimit(int value) {
            this.value = value;
        }

        public static ThreadLimit threadLimit(int value) {
            return new ThreadLimit(value);
        }

        public boolean greaterThanOrEqualTo(int value) {
            return this.value == 0 || this.value >= value;
        }

        public int value() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ThreadLimit that = (ThreadLimit)o;
            return this.value == that.value;
        }

        public int hashCode() {
            return this.value;
        }

        public String toString() {
            return this.value != 0 ? String.valueOf(this.value) : "unlimited";
        }
    }
}

