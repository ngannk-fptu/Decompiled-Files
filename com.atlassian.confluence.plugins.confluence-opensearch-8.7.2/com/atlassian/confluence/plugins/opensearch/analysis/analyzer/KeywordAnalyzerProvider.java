/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.KeywordAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch._types.analysis.Analyzer$Kind
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.KeywordAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import org.opensearch.client.opensearch._types.analysis.Analyzer;

public class KeywordAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return KeywordAnalyzerDescriptor.class;
    }

    @Override
    public Analyzer getAnalyzer() {
        return Analyzer.of(a -> a.keyword(k -> k));
    }

    @Override
    public String getName() {
        return Analyzer.Kind.Keyword.jsonValue();
    }

    @Override
    public boolean isCustom() {
        return false;
    }
}

