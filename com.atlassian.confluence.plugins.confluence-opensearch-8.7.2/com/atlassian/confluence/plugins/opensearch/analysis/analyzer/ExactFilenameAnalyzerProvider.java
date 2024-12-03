/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.ExactFilenameAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.google.common.annotations.VisibleForTesting
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch._types.analysis.AnalyzerVariant
 *  org.opensearch.client.opensearch._types.analysis.CustomAnalyzer
 *  org.opensearch.client.opensearch._types.analysis.CustomAnalyzer$Builder
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.ExactFilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import org.opensearch.client.opensearch._types.analysis.Analyzer;
import org.opensearch.client.opensearch._types.analysis.AnalyzerVariant;
import org.opensearch.client.opensearch._types.analysis.CustomAnalyzer;

public class ExactFilenameAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    private final CustomAnalyzer customAnalyzer = new CustomAnalyzer.Builder().tokenizer("keyword").filter(Arrays.asList("lowercase", "filename")).build();
    @VisibleForTesting
    public static final String NAME = "exact_filename_analyzer";

    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return ExactFilenameAnalyzerDescriptor.class;
    }

    @Override
    public Analyzer getAnalyzer() {
        return new Analyzer((AnalyzerVariant)this.customAnalyzer);
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

