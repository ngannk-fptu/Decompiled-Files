/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensearch.client.opensearch._types.analysis.TokenFilter
 */
package com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter;

import com.atlassian.confluence.plugins.opensearch.analysis.tokenfilter.OpenSearchCustomTokenFilterProvider;
import org.opensearch.client.opensearch._types.analysis.TokenFilter;

public class FilenameFilterProvider
implements OpenSearchCustomTokenFilterProvider {
    public static final String NAME = "filename";
    private static final String CAPTURE_PATTERN = "([^\\.\\s]+)";
    private final TokenFilter tokenFilter = TokenFilter.of(f -> f.definition(d -> d.patternCapture(p -> p.preserveOriginal(true).patterns(CAPTURE_PATTERN, new String[0]))));

    @Override
    public TokenFilter createTokenFilter() {
        return this.tokenFilter;
    }

    @Override
    public String getName() {
        return NAME;
    }
}

