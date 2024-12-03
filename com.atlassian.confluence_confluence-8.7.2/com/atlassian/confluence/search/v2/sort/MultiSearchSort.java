/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchSort;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.List;

@SearchPrimitive
public class MultiSearchSort
implements SearchSort {
    @VisibleForTesting
    public static final String KEY = "multiSearchSort";
    private final List<SearchSort> searchSorts;

    public MultiSearchSort(Iterable<SearchSort> searchSorts) {
        this.searchSorts = ImmutableList.copyOf(searchSorts);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchSort.Order getOrder() {
        return SearchSort.Order.ASCENDING;
    }

    public List<SearchSort> getSearchSorts() {
        return this.searchSorts;
    }
}

