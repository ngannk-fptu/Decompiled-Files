/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 */
package com.atlassian.confluence.contributors.search.extractors;

import com.atlassian.confluence.plugins.index.api.Extractor2;

public abstract class AbstractContributionExtractor
implements Extractor2 {
    public static final String CONTRIBUTION_TOKEN_SEPARATOR = "<>";

    public StringBuilder extractText(Object o) {
        return null;
    }
}

