/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class GCRead {
    private final long collectionCount;
    private final Duration collectionTime;
    private final Instant timestamp;

    private GCRead(long collectionCount, Duration collectionTime, Instant timestamp) {
        this.collectionCount = collectionCount;
        this.collectionTime = collectionTime;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getCollectionCount() {
        return this.collectionCount;
    }

    public Duration getCollectionTime() {
        return this.collectionTime;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GCRead gcRead = (GCRead)o;
        return this.collectionCount == gcRead.collectionCount && this.collectionTime.equals(gcRead.collectionTime) && this.timestamp.equals(gcRead.timestamp);
    }

    public int hashCode() {
        return Objects.hash(this.collectionCount, this.collectionTime, this.timestamp);
    }

    public String toString() {
        return "GCRead{collectionCount=" + this.collectionCount + ", collectionTime=" + this.collectionTime + ", timestamp=" + this.timestamp + '}';
    }

    public static final class Builder {
        private Long collectionCount;
        private Duration collectionTime;
        private Instant timestamp;

        private Builder() {
        }

        public Builder collectionCount(long collectionCount) {
            this.collectionCount = collectionCount;
            return this;
        }

        public Builder collectionTime(Duration collectionTime) {
            this.collectionTime = collectionTime;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public GCRead build() {
            return new GCRead(Objects.requireNonNull(this.collectionCount), Objects.requireNonNull(this.collectionTime), Objects.requireNonNull(this.timestamp));
        }
    }
}

