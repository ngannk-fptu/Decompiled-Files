/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$Builder
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractDateRangeFieldHandler;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;

public class CreatedDateFieldHandler
extends AbstractDateRangeFieldHandler {
    public CreatedDateFieldHandler() {
        super("created");
    }

    protected DateRangeQuery.Builder newDateRangeBuilder() {
        return DateRangeQuery.newDateRangeQuery((String)SearchFieldNames.CREATION_DATE);
    }

    protected SearchSort getSearchSort(SearchSort.Order order) {
        return new CreatedSort(order);
    }
}

