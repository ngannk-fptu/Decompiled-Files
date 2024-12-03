/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.page;

import com.atlassian.crowd.model.page.Page;

public class PageImpl<T>
implements Page<T> {
    private final Iterable<T> results;
    private final int size;
    private final int start;
    private final int limit;
    private final boolean isLastPage;

    public PageImpl(Iterable<T> results, int size, int start, int limit, boolean isLastPage) {
        this.results = results;
        this.start = start;
        this.size = size;
        this.limit = limit;
        this.isLastPage = isLastPage;
    }

    @Override
    public Iterable<T> getResults() {
        return this.results;
    }

    @Override
    public int getSize() {
        return this.size;
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
    public boolean isLastPage() {
        return this.isLastPage;
    }
}

