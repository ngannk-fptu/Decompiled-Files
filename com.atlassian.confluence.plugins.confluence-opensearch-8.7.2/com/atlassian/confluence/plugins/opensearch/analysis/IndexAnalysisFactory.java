/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch.indices.IndexSettingsAnalysis
 */
package com.atlassian.confluence.plugins.opensearch.analysis;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.opensearch.OpenSearchAnalyzerMapper;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.ConfluenceDefaultAnalyzerFactory;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter.OpenSearchCustomTokenFilterProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.opensearch.client.opensearch._types.analysis.Analyzer;
import org.opensearch.client.opensearch.indices.IndexSettingsAnalysis;

public class IndexAnalysisFactory {
    @VisibleForTesting
    public static final String DEFAULT_ANALYZER = "default";
    @VisibleForTesting
    public static final String DEFAULT_SEARCH_ANALYZER = "default_search";
    private final List<OpenSearchAnalyzerProvider> analyzerProviders;
    private final List<OpenSearchCustomTokenFilterProvider> tokenFilterProviders;
    private final ConfluenceDefaultAnalyzerFactory defaultAnalyzerFactory;
    private final OpenSearchAnalyzerMapper analyzerMapper;

    public IndexAnalysisFactory(List<OpenSearchAnalyzerProvider> analyzerProviders, List<OpenSearchCustomTokenFilterProvider> tokenFilterProviders, ConfluenceDefaultAnalyzerFactory defaultAnalyzerFactory, OpenSearchAnalyzerMapper analyzerMapper) {
        this.analyzerProviders = Objects.requireNonNull(analyzerProviders, "analyzerProviders is required");
        this.tokenFilterProviders = Objects.requireNonNull(tokenFilterProviders, "tokenFilterProviders is required");
        this.defaultAnalyzerFactory = Objects.requireNonNull(defaultAnalyzerFactory, "defaultAnalyzerFactory is required");
        this.analyzerMapper = Objects.requireNonNull(analyzerMapper, "analyzerMapper is required");
    }

    public IndexSettingsAnalysis createForSystemIndex() {
        return this.create(this.defaultAnalyzerFactory.createAnalyzer());
    }

    public IndexSettingsAnalysis createForCustomIndex(@Nullable AnalyzerDescriptorProvider defaultAnalyzerProvider) {
        Analyzer defaultAnalyzer = Optional.ofNullable(defaultAnalyzerProvider).flatMap(this.analyzerMapper::getAnalyzerProvider).map(OpenSearchAnalyzerProvider::getAnalyzer).orElse(Analyzer.of(a -> a.keyword(k -> k)));
        return this.create(defaultAnalyzer);
    }

    private IndexSettingsAnalysis create(Analyzer defaultAnalyzer) {
        Objects.requireNonNull(defaultAnalyzer);
        return IndexSettingsAnalysis.of(b -> b.analyzer(DEFAULT_ANALYZER, defaultAnalyzer).analyzer(DEFAULT_SEARCH_ANALYZER, defaultAnalyzer).analyzer(this.createCustomAnalyzers()).filter(this.tokenFilterProviders.stream().collect(Collectors.toMap(OpenSearchCustomTokenFilterProvider::getName, OpenSearchCustomTokenFilterProvider::createTokenFilter))));
    }

    private Map<String, Analyzer> createCustomAnalyzers() {
        return this.analyzerProviders.stream().filter(OpenSearchAnalyzerProvider::isCustom).collect(Collectors.toMap(OpenSearchAnalyzerProvider::getName, OpenSearchAnalyzerProvider::getAnalyzer));
    }
}

