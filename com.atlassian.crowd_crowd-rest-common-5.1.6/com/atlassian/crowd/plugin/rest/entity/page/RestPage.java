/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.page.Page
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.page;

import com.atlassian.crowd.model.page.Page;
import com.atlassian.crowd.plugin.rest.entity.page.RestPageRequest;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class RestPage<T>
implements Page<T> {
    public static final String LAST_PAGE_PROPERTY = "isLastPage";
    public static final String LIMIT_PROPERTY = "limit";
    public static final String START_PROPERTY = "start";
    public static final String SIZE_PROPERTY = "size";
    public static final String VALUES_PROPERTY = "values";
    @JsonProperty(value="values")
    private List<T> results;
    @JsonProperty(value="size")
    private int size;
    @JsonProperty(value="start")
    private int start;
    @JsonProperty(value="limit")
    private int limit;
    @JsonProperty(value="isLastPage")
    private boolean isLastPage;

    protected RestPage() {
    }

    public <E> RestPage(Page<? extends E> page, Function<E, ? extends T> restTransform) {
        this((List<T>)ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)page.getResults(), restTransform)), page.getSize(), page.getStart(), page.getLimit(), page.isLastPage());
    }

    public RestPage(List<T> results, int size, int start, int limit, boolean isLastPage) {
        this.results = results;
        this.size = size;
        this.start = start;
        this.limit = limit;
        this.isLastPage = isLastPage;
    }

    public List<T> getResults() {
        return this.results;
    }

    public int getSize() {
        return this.size;
    }

    public int getStart() {
        return this.start;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean isLastPage() {
        return this.isLastPage;
    }

    public static <T> RestPage<T> fromListPlusOne(List<T> results, RestPageRequest pageRequest) {
        int limit = pageRequest.getLimit();
        int start = pageRequest.getStart();
        if (pageRequest.isAllResultsQuery()) {
            return new RestPage<T>(results, results.size(), start, limit, true);
        }
        List<T> trimmedResults = results.size() > limit ? results.subList(0, limit) : results;
        return new RestPage<T>(trimmedResults, trimmedResults.size(), start, limit, results.size() <= limit);
    }

    public static <T, A> RestPage<T> fromListPlusOne(List<A> results, Function<A, ? extends T> transformer, RestPageRequest pageRequest) {
        return new RestPage<T>(RestPage.fromListPlusOne(results, pageRequest), transformer);
    }

    public static int limitPlusOne(int limit) {
        return limit == -1 || limit == Integer.MAX_VALUE ? limit : limit + 1;
    }

    public static long allResultsToLongMax(int maxResults) {
        return maxResults == -1 ? Long.MAX_VALUE : (long)maxResults;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestPage restPage = (RestPage)o;
        return this.size == restPage.size && this.start == restPage.start && this.limit == restPage.limit && this.isLastPage == restPage.isLastPage && Objects.equals(this.results, restPage.results);
    }

    public int hashCode() {
        return Objects.hash(this.results, this.size, this.start, this.limit, this.isLastPage);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("results", this.results).add(SIZE_PROPERTY, this.size).add(START_PROPERTY, this.start).add(LIMIT_PROPERTY, this.limit).add(LAST_PAGE_PROPERTY, this.isLastPage).toString();
    }
}

