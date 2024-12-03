/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.DefaultConfiguration
 *  com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$Configuration
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.core.KeywordAnalyzer
 *  org.apache.lucene.search.similarities.Similarity
 *  org.apache.lucene.store.Directory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.index.lucene.CustomLuceneConnectionFactory;
import com.atlassian.confluence.internal.index.lucene.EdgeIndexSimilarity;
import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.internal.search.v2.lucene.DefaultConfiguration;
import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.Optional;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

public class CustomLuceneConnectionFactoryImpl
implements CustomLuceneConnectionFactory {
    private final ConfluenceDirectories confluenceDirectories;
    private final LuceneAnalyzerMapper luceneAnalyzerMapper;
    private final SearchLanguageProvider searchLanguageProvider;

    public CustomLuceneConnectionFactoryImpl(ConfluenceDirectories confluenceDirectories, LuceneAnalyzerMapper luceneAnalyzerMapper, SearchLanguageProvider searchLanguageProvider) {
        this.confluenceDirectories = Objects.requireNonNull(confluenceDirectories);
        this.luceneAnalyzerMapper = luceneAnalyzerMapper;
        this.searchLanguageProvider = searchLanguageProvider;
    }

    @Override
    public LuceneConnection create(String relativePath, ScoringStrategy scoringStrategy, AnalyzerDescriptorProvider analyzerDescriptorProvider) {
        SearchLanguage language;
        Optional<MappingAnalyzerDescriptor> mappingAnalyzerDescriptor;
        Path path;
        try {
            path = this.confluenceDirectories.getLuceneIndexDirectory().resolve(relativePath);
        }
        catch (InvalidPathException e) {
            throw new LuceneException(String.format("Tried to create LuceneConnection with invalid relative path to index: %s", relativePath), (Throwable)e);
        }
        if (Files.notExists(path, new LinkOption[0])) {
            try {
                Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        Object analyzer = analyzerDescriptorProvider != null ? ((mappingAnalyzerDescriptor = analyzerDescriptorProvider.getAnalyzer(language = this.searchLanguageProvider.get())).isPresent() ? this.luceneAnalyzerMapper.map(mappingAnalyzerDescriptor.get()) : new KeywordAnalyzer()) : new KeywordAnalyzer();
        Directory directory = DirectoryUtil.getDirectory((File)path.toFile());
        SearcherInitialisation searcherInitialisation = searcher -> {
            switch (scoringStrategy) {
                case EDGE: {
                    searcher.setSimilarity((Similarity)new EdgeIndexSimilarity());
                    break;
                }
            }
        };
        return new LuceneConnection(directory, (Analyzer)analyzer, (ILuceneConnection.Configuration)new DefaultConfiguration(), searcherInitialisation);
    }
}

