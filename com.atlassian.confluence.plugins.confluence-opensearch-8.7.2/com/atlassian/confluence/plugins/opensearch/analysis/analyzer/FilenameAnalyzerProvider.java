/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.FilenameAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.google.common.annotations.VisibleForTesting
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.FilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.ConfluenceDefaultAnalyzerFactory;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.google.common.annotations.VisibleForTesting;
import org.opensearch.client.opensearch._types.analysis.Analyzer;

public class FilenameAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    @VisibleForTesting
    public static final String NAME = "filename_analyzer";
    private final ConfluenceDefaultAnalyzerFactory confluenceDefaultAnalyzerFactory;

    public FilenameAnalyzerProvider(ConfluenceDefaultAnalyzerFactory confluenceDefaultAnalyzerFactory) {
        this.confluenceDefaultAnalyzerFactory = confluenceDefaultAnalyzerFactory;
    }

    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return FilenameAnalyzerDescriptor.class;
    }

    @Override
    public Analyzer getAnalyzer() {
        return Analyzer.of(a -> a.custom(c -> this.confluenceDefaultAnalyzerFactory.buildCustomAnalyzer(c.filter("filename", new String[0]))));
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

