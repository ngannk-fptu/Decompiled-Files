/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.util.concurrent.atomic.AtomicInteger
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.util.concurrent.atomic.AtomicInteger;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultsBatchWorkSource<T>
implements BatchableWorkSource<T> {
    private static final Logger log = LoggerFactory.getLogger(SearchResultsBatchWorkSource.class);
    private final SearchManager searchManager;
    private final ImmutableList<SearchResult> searchResults;
    private final int batchSize;
    private final Function<Searchable, T> transformer;
    private AtomicInteger offset = new AtomicInteger();

    public SearchResultsBatchWorkSource(SearchManager searchManager, List<SearchResult> searchResults, int batchSize, Function<Searchable, T> transformer) {
        this.searchManager = searchManager;
        this.searchResults = ImmutableList.copyOf(searchResults);
        this.batchSize = batchSize;
        this.transformer = transformer;
    }

    @Override
    public List<T> getBatch() {
        int oldOffset = 0;
        int newOffset = 0;
        while (!this.offset.compareAndSet(oldOffset = this.offset.get(), newOffset = oldOffset + this.batchSize)) {
        }
        if (oldOffset >= this.searchResults.size()) {
            return Collections.emptyList();
        }
        int endIndex = Math.min(newOffset, this.searchResults.size());
        List<Searchable> searchables = this.searchManager.convertToEntities(new DefaultSearchResults((List<? extends SearchResult>)this.searchResults.subList(oldOffset, endIndex), this.batchSize), SearchManager.EntityVersionPolicy.LATEST_VERSION);
        return new LinkedList(Collections2.filter((Collection)searchables.stream().map(this.transformer).collect(Collectors.toList()), Objects::nonNull));
    }

    @Override
    public boolean hasMoreBatches() {
        return this.offset.get() < this.searchResults.size();
    }

    @Override
    public int numberOfBatches() {
        return this.searchResults.size() / this.batchSize + 1;
    }

    @Override
    public void reset(int total) {
        this.offset.set(0);
    }

    @Override
    public int getTotalSize() {
        return this.searchResults.size();
    }
}

