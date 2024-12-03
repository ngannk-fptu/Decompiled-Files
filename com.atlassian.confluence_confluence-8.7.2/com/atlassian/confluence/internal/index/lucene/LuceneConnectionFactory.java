/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$Configuration
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection
 *  com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import java.nio.file.Path;
import java.util.Objects;

@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class LuceneConnectionFactory {
    public static final String CHANGE_INDEX_DIRECTORY_NAME = "change";
    public static final String EDGE_INDEX_DIRECTORY_NAME = "edge";
    private final LuceneAnalyzerFactory analyzerFactory;
    private final ILuceneConnection.Configuration configuration;
    private final SearcherInitialisation searcherInitialisation;
    private final ConfluenceDirectories confluenceDirectories;

    public LuceneConnectionFactory(LuceneAnalyzerFactory analyzerFactory, ILuceneConnection.Configuration configuration, SearcherInitialisation searcherInitialisation, ConfluenceDirectories confluenceDirectories) {
        this.analyzerFactory = Objects.requireNonNull(analyzerFactory);
        this.configuration = Objects.requireNonNull(configuration);
        this.searcherInitialisation = Objects.requireNonNull(searcherInitialisation);
        this.confluenceDirectories = Objects.requireNonNull(confluenceDirectories);
    }

    public LuceneConnection createContentIndexConnection() {
        return this.createIndexConnection(this.getIndexRootDirectory());
    }

    public LuceneConnection createChangeIndexConnection() {
        return this.createIndexConnection(this.getIndexRootDirectory().resolve(CHANGE_INDEX_DIRECTORY_NAME));
    }

    private Path getIndexRootDirectory() {
        return this.confluenceDirectories.getLuceneIndexDirectory();
    }

    private LuceneConnection createIndexConnection(Path indexDir) {
        return new LuceneConnection(indexDir.toFile(), this.analyzerFactory.createIndexingAnalyzer(), this.configuration, this.searcherInitialisation);
    }
}

