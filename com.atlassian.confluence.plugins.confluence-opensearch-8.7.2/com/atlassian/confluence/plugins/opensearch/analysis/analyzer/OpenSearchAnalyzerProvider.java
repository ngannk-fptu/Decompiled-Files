/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor
 *  org.opensearch.client.opensearch._types.analysis.Analyzer
 */
package com.atlassian.confluence.plugins.opensearch.analysis.analyzer;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import org.opensearch.client.opensearch._types.analysis.Analyzer;

public interface OpenSearchAnalyzerProvider {
    public Class<? extends MappingAnalyzerDescriptor> getMappingClass();

    public Analyzer getAnalyzer();

    public String getName();

    public boolean isCustom();
}

