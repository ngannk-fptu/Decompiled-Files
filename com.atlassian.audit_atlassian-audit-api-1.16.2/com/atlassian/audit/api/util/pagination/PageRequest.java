/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.api.util.pagination;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PageRequest<C> {
    private final int offset;
    private final int limit;
    private final C cursor;

    private PageRequest(Builder<C> builder) {
        this.offset = ((Builder)builder).offset;
        this.limit = ((Builder)builder).limit;
        this.cursor = ((Builder)builder).cursor;
    }

    public int getOffset() {
        return this.offset;
    }

    @Nonnull
    public Optional<C> getCursor() {
        return Optional.ofNullable(this.cursor);
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PageRequest that = (PageRequest)o;
        return this.offset == that.offset && this.limit == that.limit && Objects.equals(this.cursor, that.cursor);
    }

    public int hashCode() {
        return Objects.hash(this.offset, this.cursor, this.limit);
    }

    public String toString() {
        return "PageRequest{offset=" + this.offset + ", limit=" + this.limit + ", cursor=" + this.cursor + '}';
    }

    public static class Builder<C> {
        int MAX_PAGE_LIMIT = 100000;
        private int offset;
        private int limit;
        private C cursor;

        @Nonnull
        public Builder<C> offset(int offset) {
            this.offset = offset;
            return this;
        }

        @Nonnull
        public Builder<C> limit(int limit) {
            this.limit = limit;
            return this;
        }

        @Nonnull
        public Builder<C> cursor(@Nullable C cursor) {
            this.cursor = cursor;
            return this;
        }

        @Nonnull
        public PageRequest<C> build() {
            if (this.limit > this.MAX_PAGE_LIMIT) {
                throw new IllegalArgumentException("Limit must be less than " + this.MAX_PAGE_LIMIT);
            }
            return new PageRequest(this);
        }
    }
}

