/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.google.common.base.Preconditions;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.7.0", forRemoval=true)
public class DefaultSearchWithToken
implements SearchWithToken {
    private final ISearch delegate;
    private final long searchToken;

    public DefaultSearchWithToken(ISearch delegate, long searchToken) {
        Preconditions.checkNotNull((Object)delegate);
        Preconditions.checkArgument((searchToken > 0L ? 1 : 0) != 0, (Object)"searchToken must be greater than 0.");
        this.searchToken = searchToken;
        this.delegate = delegate;
    }

    @Override
    public long getSearchToken() {
        return this.searchToken;
    }

    @Override
    public @NonNull SearchQuery getQuery() {
        return this.delegate.getQuery();
    }

    @Override
    public SearchSort getSort() {
        return this.delegate.getSort();
    }

    @Override
    public int getStartOffset() {
        return this.delegate.getStartOffset();
    }

    @Override
    public int getLimit() {
        return this.delegate.getLimit();
    }

    @Override
    public String getSearchType() {
        return this.delegate.getSearchType();
    }

    @Override
    public Optional<HightlightParams> getHighlight() {
        return this.delegate.getHighlight();
    }

    @Override
    public boolean isExplain() {
        return this.delegate.isExplain();
    }

    @Override
    public EnumSet<SearchIndex> getSearchIndexes() {
        return this.delegate.getSearchIndexes();
    }

    @Override
    public List<Index> getIndices() {
        return this.delegate.getIndices();
    }

    @Override
    public List<String> getSearchAfter() {
        return this.delegate.getSearchAfter();
    }

    @Override
    public ISearch withSearchAfter(List<String> searchAfter) {
        return this.delegate.withSearchAfter(searchAfter);
    }

    @Override
    public ISearch withIndices(List<Index> indices) {
        return this.delegate.withIndices(indices);
    }

    @Override
    public ISearch withQuery(SearchQuery query) {
        return this.delegate.withQuery(query);
    }
}

