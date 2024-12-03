/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.fields.AbstractLongFieldHandler
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.sort.FilesizeSort
 *  com.atlassian.querylang.fields.NumericFieldHandler
 *  com.atlassian.querylang.query.SearchQuery
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.spi.fields.AbstractLongFieldHandler;
import com.atlassian.confluence.plugins.cql.v2search.query.FileSizeQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.FilesizeSort;
import com.atlassian.querylang.fields.NumericFieldHandler;
import com.atlassian.querylang.query.SearchQuery;

public class FileSizeFieldHandler
extends AbstractLongFieldHandler
implements NumericFieldHandler<SearchQuery> {
    private static final String FIELD_NAME = "filesize";

    public FileSizeFieldHandler() {
        super(FIELD_NAME);
    }

    protected Long min() {
        return 0L;
    }

    protected SearchSort getSearchSort(SearchSort.Order order) {
        return new FilesizeSort(order);
    }

    protected com.atlassian.confluence.search.v2.SearchQuery createQuery(Long from, Long to, boolean includeFrom, boolean includeTo) {
        return new FileSizeQuery(from, to, includeFrom, includeTo);
    }
}

