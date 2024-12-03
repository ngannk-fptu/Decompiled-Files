/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.LowercaseFieldSort;

public class TitleSort
extends AbstractSort {
    public static final String KEY = "title";
    public static final TitleSort DESCENDING = new TitleSort(SearchSort.Order.DESCENDING);
    public static final TitleSort ASCENDING;
    public static final TitleSort DEFAULT;

    public TitleSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new LowercaseFieldSort(SearchFieldNames.CONTENT_NAME_UNTOKENIZED, this.getOrder());
    }

    static {
        DEFAULT = ASCENDING = new TitleSort(SearchSort.Order.ASCENDING);
    }
}

