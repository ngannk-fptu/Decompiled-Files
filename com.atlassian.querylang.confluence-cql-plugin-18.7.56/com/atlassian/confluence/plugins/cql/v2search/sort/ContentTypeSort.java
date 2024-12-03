/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.sort.AbstractSort
 *  com.atlassian.confluence.search.v2.sort.FieldSort
 *  com.atlassian.querylang.query.OrderDirection
 */
package com.atlassian.confluence.plugins.cql.v2search.sort;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.AbstractSort;
import com.atlassian.confluence.search.v2.sort.FieldSort;
import com.atlassian.querylang.query.OrderDirection;

public class ContentTypeSort
extends AbstractSort {
    private static final String KEY = SearchFieldNames.TYPE;
    public static final ContentTypeSort ASCENDING = new ContentTypeSort(SearchSort.Order.ASCENDING);
    public static final ContentTypeSort DESCENDING = new ContentTypeSort(SearchSort.Order.DESCENDING);

    public static ContentTypeSort forDirection(OrderDirection direction) {
        if (direction.equals((Object)OrderDirection.ASC)) {
            return ASCENDING;
        }
        return DESCENDING;
    }

    private ContentTypeSort(SearchSort.Order order) {
        super(KEY, order);
    }

    public SearchSort expand() {
        return new FieldSort(KEY, SearchSort.Type.STRING, this.getOrder());
    }
}

