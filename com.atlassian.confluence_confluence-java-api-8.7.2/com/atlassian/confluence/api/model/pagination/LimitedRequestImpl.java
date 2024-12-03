/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SkipDiscardLimitedRequest;
import java.util.Objects;

@ExperimentalApi
public class LimitedRequestImpl
implements SkipDiscardLimitedRequest {
    private final int start;
    private final int limit;
    private final int needed;
    private final int maxLimit;
    private final boolean shouldSkipDiscardingThreshold;
    private final Cursor cursor;

    public static LimitedRequest create(PageRequest request, int maxLimit) {
        return new LimitedRequestImpl(request, maxLimit);
    }

    public static LimitedRequest create(int maxLimit) {
        return new LimitedRequestImpl(0, maxLimit, maxLimit);
    }

    public static LimitedRequest create(int start, int limit, int maxLimit) {
        return new LimitedRequestImpl(start, limit, maxLimit);
    }

    public static LimitedRequest create(int start, int limit, int maxLimit, boolean shouldSkipDiscardingThreshold) {
        return new LimitedRequestImpl(start, limit, limit, maxLimit, shouldSkipDiscardingThreshold);
    }

    public static LimitedRequest create(Cursor cursor, int limit, int maxLimit) {
        return new LimitedRequestImpl(0, cursor, limit, limit, maxLimit, false);
    }

    protected LimitedRequestImpl(PageRequest request, int maxLimit) {
        this(request.getStart(), request.getCursor(), request.getLimit(), request.getLimit(), maxLimit, false);
    }

    protected LimitedRequestImpl(int start, int limit, int maxLimit) {
        this(start, Math.min(limit, maxLimit), Math.min(limit, maxLimit), maxLimit, false);
    }

    protected LimitedRequestImpl(int start, int limit, int needed, int maxLimit) {
        this(start, limit, needed, maxLimit, false);
    }

    protected LimitedRequestImpl(int start, int limit, int needed, int maxLimit, boolean shouldSkipDiscardingThreshold) {
        this(start, null, limit, needed, maxLimit, shouldSkipDiscardingThreshold);
    }

    protected LimitedRequestImpl(int start, Cursor cursor, int limit, int needed, int maxLimit, boolean shouldSkipDiscardingThreshold) {
        if (maxLimit < 0) {
            throw new IllegalArgumentException("maxLimit cannot be less than zero");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be less than zero");
        }
        if (start < 0) {
            throw new IllegalArgumentException("start cannot be less than zero");
        }
        if (start > 0 && cursor != null) {
            throw new IllegalArgumentException("start shouldn't be used together with cursor");
        }
        this.start = start;
        this.cursor = cursor;
        this.limit = Math.min(limit, maxLimit);
        this.needed = needed;
        this.maxLimit = maxLimit;
        this.shouldSkipDiscardingThreshold = shouldSkipDiscardingThreshold;
    }

    @Override
    @Deprecated
    public int getNeeded() {
        return this.needed;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public int getMaxLimit() {
        return this.maxLimit;
    }

    public String toString() {
        return "LimitedRequestImpl{start=" + this.start + ", limit=" + this.limit + ", needed=" + this.needed + ", maxLimit=" + this.maxLimit + ", shouldSkipDiscardingThreshold=" + this.shouldSkipDiscardingThreshold + ", cursor=" + this.cursor + '}';
    }

    public int hashCode() {
        return Objects.hash(this.limit, this.maxLimit, this.needed, this.start, this.shouldSkipDiscardingThreshold, this.cursor);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !LimitedRequest.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        if (obj instanceof SkipDiscardLimitedRequest) {
            SkipDiscardLimitedRequest other = (SkipDiscardLimitedRequest)obj;
            return Objects.equals(this.limit, other.getLimit()) && Objects.equals(this.maxLimit, other.getMaxLimit()) && Objects.equals(this.needed, other.getNeeded()) && Objects.equals(this.start, other.getStart()) && Objects.equals(this.shouldSkipDiscardingThreshold, other.shouldSkipDiscardingThreshold()) && Objects.equals(this.cursor, other.getCursor());
        }
        LimitedRequest other = (LimitedRequest)obj;
        return Objects.equals(this.limit, other.getLimit()) && Objects.equals(this.maxLimit, other.getMaxLimit()) && Objects.equals(this.needed, other.getNeeded()) && Objects.equals(this.start, other.getStart()) && Objects.equals(this.cursor, other.getCursor());
    }

    @Override
    public boolean shouldSkipDiscardingThreshold() {
        return this.shouldSkipDiscardingThreshold;
    }

    @Override
    public Cursor getCursor() {
        return this.cursor;
    }
}

