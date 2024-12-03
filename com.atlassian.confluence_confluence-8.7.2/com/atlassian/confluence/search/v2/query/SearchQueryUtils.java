/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SearchQueryUtils {
    public static SearchQuery appendIfQueryNotPresent(@Nullable SearchQuery searchQuery, SearchQuery searchQueryToCheck, SearchQuery searchQueryToAppend) {
        if (searchQuery == null) {
            return searchQueryToAppend;
        }
        if (!SearchQueryUtils.containsQuery(searchQuery, searchQueryToCheck.getClass())) {
            return (SearchQuery)BooleanQuery.builder().addMust((U[])new SearchQuery[]{searchQuery, searchQueryToAppend}).build();
        }
        return searchQuery;
    }

    public static <T extends SearchQuery> boolean containsQuery(SearchQuery searchQuery, Class<T> searchQueryClass) {
        if (searchQuery == null) {
            return false;
        }
        if (searchQueryClass.isInstance(searchQuery)) {
            return true;
        }
        return searchQuery.getSubClauses().anyMatch(x -> SearchQueryUtils.containsQuery((SearchQuery)x.getClause(), searchQueryClass));
    }
}

