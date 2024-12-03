/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchSort;

public abstract class AbstractSort
implements SearchSort {
    private final String key;
    private final SearchSort.Order order;

    protected AbstractSort(String key, SearchSort.Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order is a required constructor parameter.");
        }
        this.key = key;
        this.order = order;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public SearchSort.Order getOrder() {
        return this.order;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractSort that = (AbstractSort)o;
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.order != null ? !this.order.equals((Object)that.order) : that.order != null);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.order != null ? this.order.hashCode() : 0);
        return result;
    }
}

