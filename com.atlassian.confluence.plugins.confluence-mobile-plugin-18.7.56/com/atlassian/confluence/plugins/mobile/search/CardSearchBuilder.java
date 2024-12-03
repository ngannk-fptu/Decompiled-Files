/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.collections4.CollectionUtils
 */
package com.atlassian.confluence.plugins.mobile.search;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public final class CardSearchBuilder {
    private int hours;
    private BooleanQuery.Builder queryBuilder = BooleanQuery.builder();

    public ISearch buildSearch(PageRequest pageRequest) {
        return new ContentSearch(this.buildSearchQuery(), (SearchSort)ModifiedSort.DESCENDING, pageRequest.getStart(), pageRequest.getLimit());
    }

    public CardSearchBuilder withSpaceKeys(Set<String> spaceKeys) {
        if (CollectionUtils.isNotEmpty(spaceKeys)) {
            this.queryBuilder.addFilter((SearchQuery)new InSpaceQuery(spaceKeys));
        }
        return this;
    }

    public CardSearchBuilder withFilterQuery(SearchQuery filterQuery) {
        this.queryBuilder.addFilter(filterQuery);
        return this;
    }

    public CardSearchBuilder since(int hours) {
        this.hours = hours;
        return this;
    }

    private SearchQuery buildSearchQuery() {
        HashSet searchTerms = Sets.newHashSet();
        searchTerms.add(new ContentTypeQuery((Collection)Lists.newArrayList((Object[])new ContentTypeEnum[]{ContentTypeEnum.PAGE, ContentTypeEnum.BLOG, ContentTypeEnum.COMMENT})));
        if (this.hours > 0) {
            searchTerms.add(this.buildDateRangeQuery(this.hours));
        }
        this.queryBuilder.addMust((Collection)searchTerms);
        return this.queryBuilder.build();
    }

    private DateRangeQuery buildDateRangeQuery(int hours) {
        Date endDate = Calendar.getInstance().getTime();
        Calendar startDate = Calendar.getInstance();
        startDate.add(10, -hours);
        return new DateRangeQuery(startDate.getTime(), endDate, true, true, DateRangeQuery.DateRangeQueryType.MODIFIED);
    }
}

