/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.QueryModifiers;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

public final class QueryResults<T>
implements Serializable {
    private static final long serialVersionUID = -4591506147471300909L;
    private static final QueryResults<Object> EMPTY = new QueryResults(ImmutableList.of(), Long.MAX_VALUE, 0L, 0L);
    private final long limit;
    private final long offset;
    private final long total;
    private final List<T> results;

    public static <T> QueryResults<T> emptyResults() {
        return EMPTY;
    }

    public QueryResults(List<T> results, @Nullable Long limit, @Nullable Long offset, long total) {
        this.limit = limit != null ? limit : Long.MAX_VALUE;
        this.offset = offset != null ? offset : 0L;
        this.total = total;
        this.results = results;
    }

    public QueryResults(List<T> results, QueryModifiers mod, long total) {
        this(results, mod.getLimit(), mod.getOffset(), total);
    }

    public List<T> getResults() {
        return this.results;
    }

    public long getTotal() {
        return this.total;
    }

    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    public long getLimit() {
        return this.limit;
    }

    public long getOffset() {
        return this.offset;
    }
}

