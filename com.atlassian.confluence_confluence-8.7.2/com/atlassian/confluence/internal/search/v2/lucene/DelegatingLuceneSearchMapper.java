/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Sort
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneMapperNotFoundException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapperRegistry;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public class DelegatingLuceneSearchMapper
implements LuceneSearchMapper {
    private LuceneSearchMapperRegistry registry;

    public DelegatingLuceneSearchMapper(LuceneSearchMapperRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Query convertToLuceneQuery(SearchQuery searchQuery) {
        LuceneQueryMapper luceneQueryMapper = this.registry.getQueryMapper(searchQuery.getKey());
        if (luceneQueryMapper == null) {
            SearchQuery expanded = SearchExpander.expandAll(searchQuery);
            if (expanded == searchQuery) {
                throw new LuceneMapperNotFoundException(SearchQuery.class, searchQuery.getKey());
            }
            return this.convertToLuceneQuery(expanded);
        }
        return luceneQueryMapper.convertToLuceneQuery(searchQuery);
    }

    public Sort convertToLuceneSort(SearchSort searchSort) {
        LuceneSortMapper luceneSortMapper = this.registry.getSortMapper(searchSort.getKey());
        if (luceneSortMapper == null) {
            SearchSort expanded = SearchExpander.expandAll(searchSort);
            if (expanded == searchSort) {
                throw new LuceneMapperNotFoundException(searchSort.getClass(), searchSort.getKey());
            }
            return this.convertToLuceneSort(expanded);
        }
        return luceneSortMapper.convertToLuceneSort(searchSort);
    }
}

