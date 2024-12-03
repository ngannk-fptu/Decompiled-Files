/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  com.atlassian.confluence.plugins.index.api.UnstemmedAnalyzerDescriptor
 *  com.google.common.annotations.VisibleForTesting
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch._types.analysis.AnalyzerVariant
 *  org.opensearch.client.opensearch._types.analysis.CustomAnalyzer
 *  org.opensearch.client.opensearch._types.analysis.TokenFilterDefinition$Kind
 *  org.opensearch.client.opensearch._types.analysis.TokenizerDefinition$Kind
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.UnstemmedAnalyzerDescriptor;
import com.atlassian.confluence.plugins.opensearch.analysis.analyzer.OpenSearchAnalyzerProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import org.opensearch.client.opensearch._types.analysis.Analyzer;
import org.opensearch.client.opensearch._types.analysis.AnalyzerVariant;
import org.opensearch.client.opensearch._types.analysis.CustomAnalyzer;
import org.opensearch.client.opensearch._types.analysis.TokenFilterDefinition;
import org.opensearch.client.opensearch._types.analysis.TokenizerDefinition;

public class UnstemmedAnalyzerProvider
implements OpenSearchAnalyzerProvider {
    @VisibleForTesting
    public static final String NAME = "unstemmed_analyzer";

    @Override
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass() {
        return UnstemmedAnalyzerDescriptor.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isCustom() {
        return true;
    }

    @Override
    public Analyzer getAnalyzer() {
        return new Analyzer((AnalyzerVariant)this.buildEuropeanAnalyzer());
    }

    private CustomAnalyzer buildEuropeanAnalyzer() {
        return CustomAnalyzer.of(b -> b.tokenizer(TokenizerDefinition.Kind.UaxUrlEmail.jsonValue()).filter(Arrays.asList("underscore_delimited", "language_stop", TokenFilterDefinition.Kind.Lowercase.jsonValue())));
    }
}

