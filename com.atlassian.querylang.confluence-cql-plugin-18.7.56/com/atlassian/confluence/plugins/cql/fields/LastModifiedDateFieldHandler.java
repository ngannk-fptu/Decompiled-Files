/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$Builder
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;

public class LastModifiedDateFieldHandler
extends AbstractDateRangeFieldHandler {
    public LastModifiedDateFieldHandler() {
        super("lastmodified");
    }

    protected DateRangeQuery.Builder newDateRangeBuilder() {
        return new DateRangeQuery.Builder().includeFrom(false).includeTo(false).queryType(DateRangeQuery.DateRangeQueryType.MODIFIED);
    }

    protected SearchSort getSearchSort(SearchSort.Order order) {
        return new ModifiedSort(order);
    }
}

