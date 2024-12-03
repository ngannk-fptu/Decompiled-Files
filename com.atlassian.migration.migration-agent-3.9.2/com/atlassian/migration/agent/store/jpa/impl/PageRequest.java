/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.Pageable;

public class PageRequest
implements Pageable {
    private final int pageNumber;
    private final int pageSize;

    public PageRequest(int pageSize) {
        this(0, pageSize);
    }

    public PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public Pageable next() {
        return new PageRequest(this.pageNumber + 1, this.pageSize);
    }
}

