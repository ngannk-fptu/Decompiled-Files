/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider
 *  com.atlassian.confluence.search.v2.CustomSearchIndexRegistry
 *  com.atlassian.confluence.search.v2.Index
 *  com.atlassian.confluence.search.v2.ScoringStrategy
 *  com.atlassian.confluence.search.v2.SearchIndexAccessException
 *  com.atlassian.confluence.search.v2.SearchIndexAccessor
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchIndexAccessor;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchIndexAccessorFactory;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class OpenSearchCustomSearchIndexRegistry
implements CustomSearchIndexRegistry {
    private final OpenSearchSearchIndexAccessorFactory openSearchSearchIndexAccessorFactory;
    private final OpenSearchConfig openSearchConfig;
    private final Map<String, OpenSearchSearchIndexAccessor> searchIndexAccessorMap = new ConcurrentHashMap<String, OpenSearchSearchIndexAccessor>();

    public OpenSearchCustomSearchIndexRegistry(OpenSearchSearchIndexAccessorFactory openSearchSearchIndexAccessorFactory, OpenSearchConfig openSearchConfig) {
        this.openSearchSearchIndexAccessorFactory = Objects.requireNonNull(openSearchSearchIndexAccessorFactory);
        this.openSearchConfig = Objects.requireNonNull(openSearchConfig);
    }

    public SearchIndexAccessor add(String name, String relativeIndexPath, ScoringStrategy scoringStrategy, @Nullable AnalyzerDescriptorProvider analyzerDescriptorProvider) throws SearchIndexAccessException {
        return this.searchIndexAccessorMap.computeIfAbsent(name, n -> {
            Index index = Index.custom((String)name);
            try {
                return this.openSearchSearchIndexAccessorFactory.createCustomIndexAccessor(index, analyzerDescriptorProvider);
            }
            catch (IOException e) {
                throw new SearchIndexAccessException("Failed to initialise index " + index, (Throwable)e);
            }
        });
    }

    public SearchIndexAccessor get(String name) throws SearchIndexAccessException {
        OpenSearchSearchIndexAccessor found = this.searchIndexAccessorMap.get(name);
        if (found == null) {
            throw new SearchIndexAccessException("The custom index with name " + name + " does not exist");
        }
        return found;
    }

    public void remove(String name) throws SearchIndexAccessException {
        OpenSearchSearchIndexAccessor found = this.searchIndexAccessorMap.remove(name);
        if (found == null) {
            throw new SearchIndexAccessException("The custom index with name " + name + " does not exist");
        }
    }
}

