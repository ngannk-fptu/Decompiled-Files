/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.LowercaseFieldSort;

public class FullnameSort
extends AbstractSort {
    public static final String KEY = "fullname";
    public static final FullnameSort DESCENDING = new FullnameSort(SearchSort.Order.DESCENDING);
    public static final FullnameSort ASCENDING;
    public static final FullnameSort DEFAULT;

    public FullnameSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new LowercaseFieldSort("fullNameUntokenized", this.getOrder());
    }

    static {
        DEFAULT = ASCENDING = new FullnameSort(SearchSort.Order.ASCENDING);
    }
}

