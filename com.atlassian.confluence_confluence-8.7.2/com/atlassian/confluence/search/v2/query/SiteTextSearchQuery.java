/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SiteTextSearchQuery
implements SearchQuery {
    private static final Pattern QUERY_STRING_SYNTAX_REGEX = Pattern.compile("(?<!\\\\)[\\*\"\\(\\):~^+-]|(\\s|^)AND\\s|(\\s|^)OR\\s|(\\s|^)NOT\\s");
    public static final String KEY = "siteTextSearch";
    private final String textQuery;
    private final Boost shouldBoost;

    @Deprecated
    public SiteTextSearchQuery(String text, Boost shouldBoost) {
        this.textQuery = text;
        this.shouldBoost = shouldBoost;
    }

    public SiteTextSearchQuery(String text) {
        this.textQuery = text;
        this.shouldBoost = Boost.NO_BOOST;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return ImmutableList.of((Object)this.textQuery);
    }

    public String getTextQuery() {
        return this.textQuery;
    }

    public Boost shouldBoost() {
        return this.shouldBoost;
    }

    public static boolean isQueryStringSyntax(String queryString) {
        return QUERY_STRING_SYNTAX_REGEX.matcher(queryString).find();
    }

    public static boolean isExactSearchSyntax(String queryString) {
        if (queryString.length() < 3) {
            return false;
        }
        return queryString.startsWith("\"") && queryString.endsWith("\"");
    }

    public String toString() {
        return MoreObjects.toStringHelper(SiteTextSearchQuery.class).add("textQuery", (Object)this.textQuery).add("shouldBoost", (Object)this.shouldBoost).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteTextSearchQuery)) {
            return false;
        }
        SiteTextSearchQuery that = (SiteTextSearchQuery)o;
        return Objects.equals(this.textQuery, that.textQuery) && this.shouldBoost == that.shouldBoost;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.textQuery, this.shouldBoost});
    }

    public static enum Boost {
        BOOST,
        NO_BOOST;

    }
}

