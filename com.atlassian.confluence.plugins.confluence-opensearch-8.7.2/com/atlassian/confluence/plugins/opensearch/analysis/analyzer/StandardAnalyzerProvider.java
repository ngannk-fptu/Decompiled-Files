/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.StandardAnalyzerDescriptor
 *  com.google.common.annotations.VisibleForTesting
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.StandardAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.google.common.annotations.VisibleForTesting;
import org.opensearch.client.opensearch._types.analysis.Analyzer;

public class StandardAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    @VisibleForTesting
    public static final String NAME = "standard_analyzer";
    private static final String DEFAULT_STOP_WORDS = "_english_";

    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return StandardAnalyzerDescriptor.class;
    }

    @Override
    public Analyzer getAnalyzer() {
        return Analyzer.of(a -> a.standard(s -> s.stopwords(DEFAULT_STOP_WORDS, new String[0])));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isCustom() {
        return true;
    }
}

