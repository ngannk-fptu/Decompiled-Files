/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import java.util.Objects;

public class SimplePageRequest
implements PageRequest {
    public static final PageRequest ONE = new SimplePageRequest(0, 1);
    private final int start;
    private final int limit;
    private final Cursor cursor;

    public SimplePageRequest(LimitedRequest request) {
        this(request.getStart(), request.getCursor(), request.getLimit());
    }

    public SimplePageRequest(int start, int limit) {
        this(start, null, limit);
    }

    public SimplePageRequest(Cursor cursor, int limit) {
        this(0, cursor, limit);
    }

    protected SimplePageRequest(int start, Cursor cursor, int limit) {
        if (start > 0 && cursor != null) {
            throw new IllegalArgumentException("start shouldn't be used together with cursor");
        }
        this.start = start;
        this.cursor = cursor;
        this.limit = limit;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public Cursor getCursor() {
        return this.cursor;
    }

    public String toString() {
        return "SimplePageRequest{start=" + this.start + ", limit=" + this.limit + ", cursor=" + this.cursor + '}';
    }

    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            SimplePageRequest spr = (SimplePageRequest)obj;
            return spr.start == this.start && spr.limit == this.limit && Objects.equals(spr.cursor, this.cursor);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.start, this.limit, this.cursor);
    }
}

