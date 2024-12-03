/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider
 *  com.atlassian.confluence.plugins.index.api.LanguageDescriptor
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.atlassian.confluence.search.SearchLanguage
 *  javax.inject.Provider
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.atlassian.confluence.search.SearchLanguage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Provider;

public class OpenSearchAnalyzerMapper {
    private final Provider<SearchLanguage> searchLanguageProvider;
    private final Map<Class<? extends MappingAnalyzerDescriptor>, OpenSearchAnalyzerProvider> analyzerProviderMap;

    public OpenSearchAnalyzerMapper(Provider<SearchLanguage> searchLanguageProvider, List<OpenSearchAnalyzerProvider> providers) {
        this.searchLanguageProvider = searchLanguageProvider;
        this.analyzerProviderMap = providers.stream().collect(Collectors.toMap(OpenSearchAnalyzerProvider::getMappingClass, Function.identity()));
    }

    public String getAnalyzerName(AnalyzerDescriptorProvider provider) {
        return this.getAnalyzerProvider(provider).map(OpenSearchAnalyzerProvider::getName).orElse(null);
    }

    public Optional<OpenSearchAnalyzerProvider> getAnalyzerProvider(AnalyzerDescriptorProvider provider) {
        return provider.getAnalyzer((LanguageDescriptor)this.searchLanguageProvider.get()).map(this::getAnalyzerProvider);
    }

    private OpenSearchAnalyzerProvider getAnalyzerProvider(MappingAnalyzerDescriptor analyzer) {
        Class<?> clazz = analyzer.getClass();
        OpenSearchAnalyzerProvider mapping = this.analyzerProviderMap.getOrDefault(clazz, null);
        if (mapping == null) {
            throw new IllegalArgumentException("There is no OpenSearch analyzer mapped for the descriptor: " + analyzer.getClass());
        }
        return mapping;
    }
}

