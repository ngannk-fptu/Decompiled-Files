/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 */
package com.atlassian.confluence.pages;

import bucket.core.actions.PaginationSupport;
import java.util.List;

public class ManualTotalPaginationSupport<T>
extends PaginationSupport<T> {
    private int total = -1;

    public ManualTotalPaginationSupport() {
    }

    public ManualTotalPaginationSupport(int pageSize) {
        super(pageSize);
    }

    public ManualTotalPaginationSupport(List<T> items, int startIndex, int total, int pageSize) {
        super(items, pageSize);
        this.total = total;
        this.setStartIndex(startIndex);
    }

    public List<T> getPage() {
        return this.getItems();
    }

    public int getTotal() {
        if (this.getItems() != null && this.total == -1) {
            return this.getItems().size();
        }
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

