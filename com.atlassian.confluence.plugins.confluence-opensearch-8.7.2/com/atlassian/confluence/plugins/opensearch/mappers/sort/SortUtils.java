/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  org.opensearch.client.opensearch._types.SortOrder
 */
package com.atlassian.confluence.plugins.opensearch.mappers.sort;

import com.atlassian.confluence.search.v2.SearchSort;
import org.opensearch.client.opensearch._types.SortOrder;

public class SortUtils {
    public static SortOrder toOpenSearchSortOrder(SearchSort.Order sortOrder) {
        if (sortOrder == SearchSort.Order.DESCENDING) {
            return SortOrder.Desc;
        }
        return SortOrder.Asc;
    }
}

