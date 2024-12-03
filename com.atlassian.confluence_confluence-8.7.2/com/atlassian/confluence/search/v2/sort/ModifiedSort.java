/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;

public class ModifiedSort
extends AbstractSort {
    public static final String KEY = "modified";
    public static final ModifiedSort DESCENDING = new ModifiedSort(SearchSort.Order.DESCENDING);
    public static final ModifiedSort ASCENDING = new ModifiedSort(SearchSort.Order.ASCENDING);
    public static final ModifiedSort DEFAULT = DESCENDING;

    public ModifiedSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new FieldSort(SearchFieldNames.LAST_MODIFICATION_DATE, SearchSort.Type.LONG, this.getOrder());
    }
}

