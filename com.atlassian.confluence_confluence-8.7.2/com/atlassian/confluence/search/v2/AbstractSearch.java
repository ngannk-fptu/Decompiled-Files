/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractSearch
implements ISearch {
    protected final List<Index> indices;
    protected final SearchQuery query;
    protected final SearchSort sort;
    protected final int startOffset;
    protected final int limit;
    protected final List<String> searchAfter;

    protected AbstractSearch(EnumSet<SearchIndex> indexes, @NonNull SearchQuery query, SearchSort sort) {
        this(indexes, query, sort, 0, 10);
    }

    @Deprecated(since="8.7.0", forRemoval=true)
    protected AbstractSearch(EnumSet<SearchIndex> indexes, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit) {
        this(Index.from(indexes), query, sort, startOffset, limit);
    }

    protected AbstractSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit) {
        this(indices, query, sort, startOffset, limit, Collections.emptyList());
    }

    protected AbstractSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit, List<String> searchAfter) {
        Preconditions.checkArgument((query != null ? 1 : 0) != 0, (Object)"query should be non-null");
        Preconditions.checkArgument((startOffset >= 0 ? 1 : 0) != 0, (Object)"startOffset should be a non-negative number");
        Preconditions.checkArgument((limit > 0 ? 1 : 0) != 0, (Object)"limit should be a positive number");
        Preconditions.checkNotNull(indices, (Object)"indices should be non-null");
        this.indices = indices;
        this.query = query;
        this.sort = sort;
        this.startOffset = startOffset;
        this.limit = limit;
        this.searchAfter = searchAfter;
    }

    @Override
    public @NonNull SearchQuery getQuery() {
        return this.query;
    }

    @Override
    public SearchSort getSort() {
        return this.sort;
    }

    @Override
    public int getStartOffset() {
        return this.startOffset;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public EnumSet<SearchIndex> getSearchIndexes() {
        Set searchIndexes = this.indices.stream().map(SearchIndex::fromIndex).filter(Objects::nonNull).collect(Collectors.toSet());
        return searchIndexes.isEmpty() ? EnumSet.noneOf(SearchIndex.class) : EnumSet.copyOf(searchIndexes);
    }

    @Override
    public List<Index> getIndices() {
        return this.indices;
    }

    @Override
    public List<String> getSearchAfter() {
        return this.searchAfter;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractSearch that = (AbstractSearch)o;
        return this.startOffset == that.startOffset && this.limit == that.limit && Objects.equals(this.indices, that.indices) && Objects.equals(this.query, that.query) && Objects.equals(this.sort, that.sort) && Objects.equals(this.searchAfter, that.searchAfter);
    }

    public int hashCode() {
        return Objects.hash(this.indices, this.query, this.sort, this.startOffset, this.limit, this.searchAfter);
    }
}

