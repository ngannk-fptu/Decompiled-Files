/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.search.actions;

import java.util.List;

public class PaginationSupport<T> {
    private List<T> items;
    private int startIndex = 0;
    private int pageSize;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_COUNT_ON_EACH_PAGE = 10;
    private int total = -1;

    public PaginationSupport() {
        this(10);
    }

    public PaginationSupport(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size should be greater than zero.");
        }
        this.pageSize = pageSize;
    }

    public PaginationSupport(List<T> items, int pageSize) {
        this(pageSize);
        this.items = items;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
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
        return this.items.subList(this.getStartIndex(), this.getNiceEndIndex());
    }

    public int getTotal() {
        if (this.items != null) {
            return this.items.size();
        }
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }
}

