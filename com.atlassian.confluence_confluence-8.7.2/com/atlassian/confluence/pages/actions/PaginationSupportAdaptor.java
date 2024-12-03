/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 */
package com.atlassian.confluence.pages.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import java.util.List;
import java.util.Objects;

public class PaginationSupportAdaptor<T>
extends PaginationSupport<T> {
    private long totalItems;
    private PageResponse pageResponse;

    public PaginationSupportAdaptor(long totalItems, int pageSize, PageResponse<T> pageResponse) {
        super(pageSize);
        Objects.requireNonNull(pageResponse);
        if (totalItems < 0L) {
            throw new IllegalArgumentException("totalItems could not less than zero");
        }
        this.totalItems = totalItems;
        this.pageResponse = pageResponse;
        this.setStartIndex(pageResponse.getPageRequest().getStart());
        this.setTotal(this.getTotal());
    }

    public List getItems() {
        return this.pageResponse.getResults();
    }

    public List<T> getPage() {
        return this.pageResponse.getResults();
    }

    public int getTotal() {
        return Long.valueOf(this.totalItems).intValue();
    }
}

