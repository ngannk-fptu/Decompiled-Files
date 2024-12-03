/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.WhitespaceAnalyzerDescriptor
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch._types.analysis.Analyzer$Kind
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.WhitespaceAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import org.opensearch.client.opensearch._types.analysis.Analyzer;

public class WhitespaceAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return WhitespaceAnalyzerDescriptor.class;
    }

    @Override
    public Analyzer getAnalyzer() {
        return Analyzer.of(a -> a.whitespace(w -> w));
    }

    @Override
    public String getName() {
        return Analyzer.Kind.Whitespace.jsonValue();
    }

    @Override
    public boolean isCustom() {
        return false;
    }
}

