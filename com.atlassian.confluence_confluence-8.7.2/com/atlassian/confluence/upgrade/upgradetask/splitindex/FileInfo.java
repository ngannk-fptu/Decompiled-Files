/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.upgrade.upgradetask.splitindex;

import java.io.File;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FileInfo {
    private final String name;
    private final File file;
    private final long bytes;

    public FileInfo(@NonNull String name, @NonNull File file, long bytes) {
        this.name = Objects.requireNonNull(name);
        this.file = Objects.requireNonNull(file);
        this.bytes = bytes;
    }

    public @NonNull String getName() {
        return this.name;
    }

    public @NonNull File getFile() {
        return this.file;
    }

    public @NonNull long getSize() {
        return this.bytes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileInfo fileInfo = (FileInfo)o;
        return this.bytes == fileInfo.bytes && this.name.equals(fileInfo.name) && this.file.equals(fileInfo.file);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.file, this.bytes);
    }
}

