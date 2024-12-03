/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PageRequest {
    private final int start;
    private final int limit;

    private PageRequest(Builder builder) {
        this.start = builder.start;
        this.limit = builder.limit;
    }

    @Nonnull
    public static PageRequest of(int start, int limit) {
        return new Builder(limit).start(start).build();
    }

    @Nonnull
    public static PageRequest ofSize(int limit) {
        return PageRequest.of(0, limit);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PageRequest that = (PageRequest)o;
        return this.limit == that.limit && this.start == that.start;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getStart() {
        return this.start;
    }

    public int hashCode() {
        return Objects.hash(this.start, this.limit);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("start", this.start).add("limit", this.limit).toString();
    }

    public static class Builder {
        private int limit;
        private int start;

        public Builder(@Nonnull PageRequest other) {
            this.limit = Objects.requireNonNull(other, "other").limit;
            this.start = other.start;
        }

        public Builder(int limit) {
            this.limit = limit;
        }

        @Nonnull
        public PageRequest build() {
            return new PageRequest(this);
        }

        @Nonnull
        public Builder limit(int value) {
            if (this.limit < 0) {
                throw new IllegalArgumentException("limit must be at least 0");
            }
            this.limit = value;
            return this;
        }

        @Nonnull
        public Builder start(int value) {
            if (this.start < 0) {
                throw new IllegalArgumentException("start must be at least 0");
            }
            this.start = value;
            return this;
        }
    }
}

