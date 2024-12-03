/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensearch.client.opensearch._types.analysis.StopTokenFilter
 *  org.opensearch.client.opensearch._types.analysis.StopTokenFilter$Builder
 *  org.opensearch.client.opensearch._types.analysis.TokenFilter
 */
package com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter;

import com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter.OpenSearchCustomTokenFilterProvider;
import org.opensearch.client.opensearch._types.analysis.StopTokenFilter;
import org.opensearch.client.opensearch._types.analysis.TokenFilter;

public class LanguageStopTokenFilterProvider
implements OpenSearchCustomTokenFilterProvider {
    public static final String NAME = "language_stop";

    @Override
    public TokenFilter createTokenFilter() {
        return TokenFilter.of(f -> f.definition(d -> d.stop(this.buildStopTokenFilter())));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private StopTokenFilter buildStopTokenFilter() {
        return new StopTokenFilter.Builder().stopwords("_english_", new String[0]).build();
    }
}

