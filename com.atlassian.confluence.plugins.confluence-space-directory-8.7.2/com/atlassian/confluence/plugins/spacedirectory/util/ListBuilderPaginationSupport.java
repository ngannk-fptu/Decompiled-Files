/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.PaginationSupport
 *  com.atlassian.confluence.core.ListBuilder
 */
package com.atlassian.confluence.plugins.spacedirectory.util;

import bucket.core.PaginationSupport;
import com.atlassian.confluence.core.ListBuilder;
import java.util.List;

public class ListBuilderPaginationSupport<T>
implements PaginationSupport<T> {
    private int startIndex;
    private int pageSize;
    private ListBuilder<T> items;

    public ListBuilderPaginationSupport(int startIndex, int pageSize, ListBuilder<T> items) {
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.items = items;
    }

    public int getNiceEndIndex() {
        int endIndex = this.getStartIndex() + this.pageSize;
        if (endIndex > this.getTotal()) {
            return this.getTotal();
        }
        return endIndex;
    }

    public int getStartIndex() {
        if (this.startIndex >= this.getTotal() && this.getTotal() > 0) {
            return this.getTotal() - 1;
        }
        if (this.startIndex < 0) {
            return 0;
        }
        return this.startIndex;
    }

    public int getStartIndexValue() {
        return this.startIndex;
    }

    public int getNextStartIndex() {
        int niceEndIndex = this.getNiceEndIndex();
        if (niceEndIndex >= this.getTotal()) {
            return -1;
        }
        return niceEndIndex;
    }

    public int getPreviousStartIndex() {
        int result = this.getStartIndex() - this.pageSize;
        if (this.getStartIndex() == 0) {
            return -1;
        }
        if (result < 0) {
            return 0;
        }
        return result;
    }

    public int[] getNextStartIndexes() {
        int nextStartIndex = this.getNextStartIndex();
        int total1 = this.getTotal();
        if (nextStartIndex == -1) {
            return null;
        }
        int remainingItemsCount = total1 - nextStartIndex;
        int pagesCount = (remainingItemsCount + this.pageSize - 1) / this.pageSize;
        int[] result = new int[pagesCount];
        for (int i = 0; i < pagesCount; ++i) {
            result[i] = nextStartIndex;
            nextStartIndex += this.pageSize;
        }
        return result;
    }

    public int[] getPreviousStartIndexes() {
        int nextStartIndex = this.getStartIndex();
        if (nextStartIndex == 0) {
            return null;
        }
        int pagesCount = (nextStartIndex + this.pageSize - 1) / this.pageSize;
        int[] result = new int[pagesCount];
        for (int i = pagesCount - 1; i > 0; --i) {
            result[i] = nextStartIndex -= this.pageSize;
        }
        return result;
    }

    public int getNiceStartIndex() {
        return this.getStartIndex() + 1;
    }

    public List<T> getPage() {
        if (this.items == null) {
            throw new IllegalStateException("Trying to call getPage() when items has not been initialised in PaginationSupport");
        }
        return this.items.getPage(this.getStartIndex(), this.pageSize);
    }

    public int getTotal() {
        if (this.items != null) {
            return this.items.getAvailableSize();
        }
        return 0;
    }

    public int getPageSize() {
        return this.pageSize;
    }
}

