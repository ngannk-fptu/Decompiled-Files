/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;

public final class ContentIdSort
extends AbstractSort {
    public static final String KEY = "contentIdSort";
    public static final ContentIdSort ASCENDING = new ContentIdSort(SearchSort.Order.ASCENDING);
    public static final ContentIdSort DESCENDING = new ContentIdSort(SearchSort.Order.DESCENDING);

    public ContentIdSort(SearchSort.Order order) {
        super(KEY, order);
    }

    @Override
    public SearchSort expand() {
        return new FieldSort(SearchFieldMappings.CONTENT_ID.getName(), SearchSort.Type.LONG, this.getOrder());
    }
}

