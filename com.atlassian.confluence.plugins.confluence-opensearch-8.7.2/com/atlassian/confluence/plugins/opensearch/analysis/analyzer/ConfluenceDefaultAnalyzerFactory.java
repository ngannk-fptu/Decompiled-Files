/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 *  org.opensearch.client.opensearch._types.analysis.CustomAnalyzer$Builder
 *  org.opensearch.client.opensearch._types.analysis.TokenFilterDefinition$Kind
 *  org.opensearch.client.opensearch._types.analysis.TokenizerDefinition$Kind
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import org.opensearch.client.opensearch._types.analysis.Analyzer;
import org.opensearch.client.opensearch._types.analysis.CustomAnalyzer;
import org.opensearch.client.opensearch._types.analysis.TokenFilterDefinition;
import org.opensearch.client.opensearch._types.analysis.TokenizerDefinition;

public class ConfluenceDefaultAnalyzerFactory {
    public Analyzer createAnalyzer() {
        return Analyzer.of(a -> a.custom(this::buildCustomAnalyzer));
    }

    public CustomAnalyzer.Builder buildCustomAnalyzer(CustomAnalyzer.Builder builder) {
        return builder.tokenizer(TokenizerDefinition.Kind.UaxUrlEmail.jsonValue()).filter(TokenFilterDefinition.Kind.Lowercase.jsonValue(), new String[]{TokenFilterDefinition.Kind.Stop.jsonValue(), TokenFilterDefinition.Kind.Kstem.jsonValue(), TokenFilterDefinition.Kind.Asciifolding.jsonValue()});
    }
}

