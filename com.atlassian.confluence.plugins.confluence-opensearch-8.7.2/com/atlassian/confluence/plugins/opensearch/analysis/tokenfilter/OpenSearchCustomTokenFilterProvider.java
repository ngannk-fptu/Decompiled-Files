/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensearch.client.opensearch._types.analysis.TokenFilter
 */
package com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter;

import org.opensearch.client.opensearch._types.analysis.TokenFilter;

public interface OpenSearchCustomTokenFilterProvider {
    public TokenFilter createTokenFilter();

    public String getName();
}

