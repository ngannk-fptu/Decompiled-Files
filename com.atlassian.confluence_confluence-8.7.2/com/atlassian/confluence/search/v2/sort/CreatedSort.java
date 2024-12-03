/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;

public class CreatedSort
extends AbstractSort {
    public static final String KEY = "created";
    public static final CreatedSort DESCENDING = new CreatedSort(SearchSort.Order.DESCENDING);
    public static final CreatedSort ASCENDING = new CreatedSort(SearchSort.Order.ASCENDING);
    public static final CreatedSort DEFAULT = DESCENDING;

    public CreatedSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new FieldSort(SearchFieldNames.CREATION_DATE, SearchSort.Type.LONG, this.getOrder());
    }
}

