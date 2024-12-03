/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider
 *  com.atlassian.confluence.plugins.index.api.WhitespaceAnalyzerDescriptor
 *  com.atlassian.confluence.search.v2.CustomSearchIndexRegistry
 *  com.atlassian.confluence.search.v2.DelegatingSearchIndexAccessor
 *  com.atlassian.confluence.search.v2.ScoringStrategy
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.WhitespaceAnalyzerDescriptor;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.DelegatingSearchIndexAccessor;
import com.atlassian.confluence.search.v2.ScoringStrategy;

public class EdgeSearchIndexAccessor
extends DelegatingSearchIndexAccessor {
    public static final String EDGE = "edge";

    public EdgeSearchIndexAccessor(CustomSearchIndexRegistry customSearchIndexRegistry) {
        super(customSearchIndexRegistry, EDGE, ScoringStrategy.EDGE, (AnalyzerDescriptorProvider)new WhitespaceAnalyzerDescriptor());
    }
}

