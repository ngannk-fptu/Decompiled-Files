/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.annotations.VisibleForTesting;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PageRequest {
    @VisibleForTesting
    static final int MAX_PAGE_LIMIT = 1000;
    private int limit;
    private int offset;

    public PageRequest() {
    }

    @JsonCreator
    public PageRequest(@JsonProperty(value="offset") int offset, @JsonProperty(value="limit") int limit) {
        if (limit > 1000) {
            throw new IllegalArgumentException("Limit must be less than 1000");
        }
        if (offset < 0 || limit < 0) {
            throw new IllegalArgumentException(String.format("Offset and limit need to be positive but were %d and %d", offset, limit));
        }
        this.offset = offset;
        this.limit = limit;
    }

    @JsonProperty(value="limit")
    public int getLimit() {
        return this.limit;
    }

    @JsonProperty(value="offset")
    public int getOffset() {
        return this.offset;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PageRequest that = (PageRequest)o;
        return this.offset == that.offset && this.limit == that.limit;
    }

    public int hashCode() {
        return Objects.hash(this.offset, this.limit);
    }

    public String toString() {
        return "PageRequest{offset=" + this.offset + ", limit=" + this.limit + '}';
    }
}

