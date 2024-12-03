/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider
 *  com.atlassian.confluence.plugins.index.api.mapping.MappingDeconflictDarkFeature
 *  com.atlassian.confluence.search.v2.FieldMappings
 *  com.atlassian.confluence.search.v2.FieldMappings$FieldMappingWriter
 *  com.atlassian.confluence.search.v2.Index
 *  javax.annotation.Nullable
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.opensearch.indices.CreateIndexRequest
 *  org.opensearch.client.opensearch.indices.ExistsRequest
 *  org.opensearch.client.opensearch.indices.IndexSettings$Builder
 *  org.opensearch.client.opensearch.indices.IndexSettingsAnalysis
 *  org.opensearch.client.util.ObjectBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.mapping.MappingDeconflictDarkFeature;
import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.OpenSearchAnalyzerMapper;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import com.atlassian.confluence.plugins.opensearch.OpenSearchFieldMappingWriter;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchIndexAccessor;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchManager;
import com.atlassian.confluence.plugins.opensearch.analysis.IndexAnalysisFactory;
import com.atlassian.confluence.plugins.opensearch.johnson.JohnsonUtils;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.Index;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.IndexSettingsAnalysis;
import org.opensearch.client.util.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchSearchIndexAccessorFactory {
    static final Logger LOG = LoggerFactory.getLogger(OpenSearchSearchIndexAccessorFactory.class);
    private final OpenSearchClient client;
    private final OpenSearchAnalyzerMapper analyzerMapper;
    private final MappingDeconflictDarkFeature deconflictDarkFeature;
    private final IndexAnalysisFactory indexAnalysisFactory;
    private final OpenSearchSearchManager searchManager;
    private final DelegatingQueryMapper queryMapperRegistry;
    private final OpenSearchConfig openSearchConfig;

    public OpenSearchSearchIndexAccessorFactory(OpenSearchClient client, OpenSearchAnalyzerMapper analyzerMapper, OpenSearchSearchManager searchManager, DelegatingQueryMapper queryMapper, MappingDeconflictDarkFeature deconflictDarkFeature, OpenSearchConfig openSearchConfig, IndexAnalysisFactory indexAnalysisFactory) {
        this.client = Objects.requireNonNull(client, "client is required");
        this.analyzerMapper = Objects.requireNonNull(analyzerMapper, "analyzerMapper is required");
        this.deconflictDarkFeature = Objects.requireNonNull(deconflictDarkFeature, "deconflictDarkFeature is required");
        this.searchManager = Objects.requireNonNull(searchManager, "searchManager is required");
        this.queryMapperRegistry = Objects.requireNonNull(queryMapper, "queryMapper is required");
        this.openSearchConfig = Objects.requireNonNull(openSearchConfig, "openSearchConfig is required");
        this.indexAnalysisFactory = Objects.requireNonNull(indexAnalysisFactory, "indexAnalysisFactory is required");
    }

    public OpenSearchSearchIndexAccessor createSystemIndexAccessor(Index index) {
        try {
            this.ensureIndexExists(index, this.indexAnalysisFactory::createForSystemIndex);
        }
        catch (IOException exception) {
            JohnsonUtils.raiseStartupErrorIfNotExistFor(exception);
        }
        return this.createAccessor(index);
    }

    public OpenSearchSearchIndexAccessor createCustomIndexAccessor(Index index, @Nullable AnalyzerDescriptorProvider analyzerProvider) throws IOException {
        block2: {
            try {
                this.ensureIndexExists(index, () -> this.indexAnalysisFactory.createForCustomIndex(analyzerProvider));
            }
            catch (IOException exception) {
                if (JohnsonUtils.hasOpenSearchEvent()) break block2;
                throw exception;
            }
        }
        return this.createAccessor(index);
    }

    public void ensureIndexExists(Index index, Supplier<IndexSettingsAnalysis> analysisSupplier) throws IOException {
        String indexName = this.openSearchConfig.getIndexName(index);
        if (!this.client.indices().exists(ExistsRequest.of(i -> i.index(indexName, new String[0]))).value()) {
            LOG.info("Creating OpenSearch index {}", (Object)indexName);
            this.client.indices().create(CreateIndexRequest.of(i -> i.index(indexName).settings(arg_0 -> OpenSearchSearchIndexAccessorFactory.lambda$ensureIndexExists$2((Supplier)analysisSupplier, arg_0))));
        }
    }

    private OpenSearchSearchIndexAccessor createAccessor(Index index) {
        return new OpenSearchSearchIndexAccessor(index, this.client, this.createFieldMappings(index), this.searchManager, this.queryMapperRegistry, this.openSearchConfig);
    }

    private FieldMappings createFieldMappings(Index index) {
        return new FieldMappings((FieldMappings.FieldMappingWriter)new OpenSearchFieldMappingWriter(this.openSearchConfig.getIndexName(index), this.client, this.analyzerMapper), this.deconflictDarkFeature);
    }

    private static /* synthetic */ ObjectBuilder lambda$ensureIndexExists$2(Supplier analysisSupplier, IndexSettings.Builder s) {
        return s.maxResultWindow(Integer.valueOf(Integer.MAX_VALUE)).analysis((IndexSettingsAnalysis)analysisSupplier.get());
    }
}

