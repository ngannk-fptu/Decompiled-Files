/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;

public class FavouriteSort
extends AbstractSort {
    public static final String KEY = "label";
    public static final FavouriteSort DESCENDING = new FavouriteSort(SearchSort.Order.DESCENDING);
    public static final FavouriteSort ASCENDING = new FavouriteSort(SearchSort.Order.ASCENDING);
    public static final FavouriteSort DEFAULT = DESCENDING;

    public FavouriteSort(SearchSort.Order order) {
        super(KEY, order);
    }
}

