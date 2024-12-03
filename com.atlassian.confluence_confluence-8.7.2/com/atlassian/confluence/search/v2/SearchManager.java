/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface SearchManager {
    public SearchResults search(ISearch var1) throws InvalidSearchException;

    @Deprecated
    public SearchResults search(SearchWithToken var1) throws SearchTokenExpiredException, InvalidSearchException;

    public SearchResults search(ISearch var1, Set<String> var2) throws InvalidSearchException;

    public List<Searchable> searchEntities(ISearch var1, EntityVersionPolicy var2) throws InvalidSearchException;

    public List<Searchable> convertToEntities(SearchResults var1, EntityVersionPolicy var2);

    default public String explain(ISearch search, long contentId) {
        return "";
    }

    default public <T> Map<T, List<Map<String, String>>> searchCategorised(ISearch search, Categorizer<T> categorizer) throws InvalidSearchException {
        throw new UnsupportedOperationException();
    }

    public long scan(EnumSet<SearchIndex> var1, SearchQuery var2, Set<String> var3, Consumer<Map<String, String[]>> var4) throws InvalidSearchException;

    public long scan(List<Index> var1, SearchQuery var2, Set<String> var3, Consumer<Map<String, String[]>> var4) throws InvalidSearchException;

    public static interface Categorizer<T> {
        public Set<T> getCategories();

        public int getLimit(T var1);

        public Set<String> getFields(T var1);
    }

    public static enum EntityVersionPolicy {
        INDEXED_VERSION,
        LATEST_VERSION;

    }
}

