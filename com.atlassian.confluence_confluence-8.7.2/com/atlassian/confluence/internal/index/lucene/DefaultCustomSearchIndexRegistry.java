/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection
 *  javax.inject.Provider
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.index.lucene.CustomLuceneConnectionFactory;
import com.atlassian.confluence.internal.index.lucene.LuceneFieldVisitor;
import com.atlassian.confluence.internal.index.lucene.LuceneSearchIndexAccessor;
import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;

public class DefaultCustomSearchIndexRegistry
implements CustomSearchIndexRegistry {
    private final LuceneFieldVisitor luceneFieldVisitor;
    private final LuceneSearchMapper searchMapper;
    private final CustomLuceneConnectionFactory customLuceneConnectionFactory;
    private final Provider<FieldMappings> fieldMappingsProvider;
    private final Map<String, LuceneSearchIndexAccessor> searchIndexAccessorMap;

    public DefaultCustomSearchIndexRegistry(LuceneFieldVisitor luceneFieldVisitor, LuceneSearchMapper luceneSearchMapper, CustomLuceneConnectionFactory customLuceneConnectionFactory, Provider<FieldMappings> fieldMappingsProvider) {
        this.luceneFieldVisitor = luceneFieldVisitor;
        this.searchMapper = luceneSearchMapper;
        this.customLuceneConnectionFactory = customLuceneConnectionFactory;
        this.fieldMappingsProvider = fieldMappingsProvider;
        this.searchIndexAccessorMap = new HashMap<String, LuceneSearchIndexAccessor>();
    }

    @Override
    public SearchIndexAccessor add(String name, String relativeIndexPath, ScoringStrategy scoringStrategy, AnalyzerDescriptorProvider analyzerDescriptorProvider) throws SearchIndexAccessException {
        return this.searchIndexAccessorMap.computeIfAbsent(name, searchIndexName -> {
            final LuceneConnection luceneConnection = this.customLuceneConnectionFactory.create(relativeIndexPath, scoringStrategy, analyzerDescriptorProvider);
            return new LuceneSearchIndexAccessor(luceneConnection, this.luceneFieldVisitor, this.searchMapper, (FieldMappings)this.fieldMappingsProvider.get()){

                @Override
                public void snapshot(File destinationDirectory) throws SearchIndexAccessException {
                    try {
                        luceneConnection.snapshot(DirectoryUtil.getDirectory((File)destinationDirectory));
                    }
                    catch (IOException e) {
                        throw new SearchIndexAccessException("Unexpected IOException while making a snapshot of the index directory", e);
                    }
                }
            };
        });
    }

    @Override
    public SearchIndexAccessor get(String name) throws SearchIndexAccessException {
        if (!this.searchIndexAccessorMap.containsKey(name)) {
            throw new SearchIndexAccessException("The custom index with name " + name + " does not exist");
        }
        return this.searchIndexAccessorMap.get(name);
    }

    @Override
    public void remove(String name) throws SearchIndexAccessException {
        if (!this.searchIndexAccessorMap.containsKey(name)) {
            throw new SearchIndexAccessException("The custom index with name " + name + " does not exist");
        }
        LuceneSearchIndexAccessor searchIndexAccessor = this.searchIndexAccessorMap.get(name);
        searchIndexAccessor.close();
        this.searchIndexAccessorMap.remove(name);
    }

    public void destroy() {
        this.searchIndexAccessorMap.values().forEach(LuceneSearchIndexAccessor::close);
        this.searchIndexAccessorMap.clear();
    }
}

