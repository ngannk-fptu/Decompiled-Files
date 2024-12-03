/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.ScoringStrategy;

public interface CustomLuceneConnectionFactory {
    public LuceneConnection create(String var1, ScoringStrategy var2, AnalyzerDescriptorProvider var3);
}

