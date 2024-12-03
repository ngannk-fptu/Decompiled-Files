/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import java.util.Collection;

public interface SearchQueryTokenizer {
    public Collection<String> tokenize(String var1, AnalyzerDescriptorProvider var2, String var3);

    public Collection<String> tokenize(String var1, String var2);
}

