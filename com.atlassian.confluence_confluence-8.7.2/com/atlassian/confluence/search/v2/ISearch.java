/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ISearch {
    public @NonNull SearchQuery getQuery();

    public SearchSort getSort();

    public int getStartOffset();

    public int getLimit();

    default public String getSearchType() {
        return this.getClass().getSimpleName();
    }

    default public Optional<HightlightParams> getHighlight() {
        return Optional.empty();
    }

    default public boolean isExplain() {
        return false;
    }

    default public EnumSet<SearchIndex> getSearchIndexes() {
        return EnumSet.allOf(SearchIndex.class);
    }

    default public List<Index> getIndices() {
        return Collections.emptyList();
    }

    default public List<String> getSearchAfter() {
        return Collections.emptyList();
    }

    default public ISearch withSearchAfter(List<String> searchAfter) {
        return this;
    }

    default public ISearch withIndices(List<Index> indices) {
        return this;
    }

    default public ISearch withQuery(SearchQuery query) {
        throw new UnsupportedOperationException();
    }
}

