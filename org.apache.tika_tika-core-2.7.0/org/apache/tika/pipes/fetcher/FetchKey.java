/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import java.io.Serializable;
import java.util.Objects;

public class FetchKey
implements Serializable {
    private static final long serialVersionUID = -3861669115439125268L;
    private String fetcherName;
    private String fetchKey;
    private long rangeStart = -1L;
    private long rangeEnd = -1L;

    public FetchKey() {
    }

    public FetchKey(String fetcherName, String fetchKey) {
        this(fetcherName, fetchKey, -1L, -1L);
    }

    public FetchKey(String fetcherName, String fetchKey, long rangeStart, long rangeEnd) {
        this.fetcherName = fetcherName;
        this.fetchKey = fetchKey;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public String getFetcherName() {
        return this.fetcherName;
    }

    public String getFetchKey() {
        return this.fetchKey;
    }

    public boolean hasRange() {
        return this.rangeStart > -1L && this.rangeEnd > -1L;
    }

    public long getRangeStart() {
        return this.rangeStart;
    }

    public long getRangeEnd() {
        return this.rangeEnd;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FetchKey fetchKey1 = (FetchKey)o;
        return this.rangeStart == fetchKey1.rangeStart && this.rangeEnd == fetchKey1.rangeEnd && Objects.equals(this.fetcherName, fetchKey1.fetcherName) && Objects.equals(this.fetchKey, fetchKey1.fetchKey);
    }

    public int hashCode() {
        return Objects.hash(this.fetcherName, this.fetchKey, this.rangeStart, this.rangeEnd);
    }

    public String toString() {
        return "FetchKey{fetcherName='" + this.fetcherName + '\'' + ", fetchKey='" + this.fetchKey + '\'' + ", rangeStart=" + this.rangeStart + ", rangeEnd=" + this.rangeEnd + '}';
    }
}

