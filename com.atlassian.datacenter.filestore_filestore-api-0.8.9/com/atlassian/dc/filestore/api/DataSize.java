/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.dc.filestore.api;

import com.atlassian.annotations.ExperimentalApi;
import java.util.Objects;

@ExperimentalApi
public final class DataSize
implements Comparable<DataSize> {
    private final long bytes;

    public static DataSize ofBytes(long bytes) {
        return new DataSize(bytes);
    }

    private DataSize(long bytes) {
        this.bytes = bytes;
    }

    public long getBytes() {
        return this.bytes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DataSize dataSize = (DataSize)o;
        return this.bytes == dataSize.bytes;
    }

    public int hashCode() {
        return Objects.hash(this.bytes);
    }

    @Override
    public int compareTo(DataSize other) {
        return Long.compare(this.bytes, other.bytes);
    }

    public String toString() {
        return String.format("%dB", this.bytes);
    }
}

