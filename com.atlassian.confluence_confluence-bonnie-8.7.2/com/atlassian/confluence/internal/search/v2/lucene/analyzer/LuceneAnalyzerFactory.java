/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 */
package com.atlassian.confluence.internal.search.v2.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;

public interface LuceneAnalyzerFactory {
    public Analyzer createIndexingAnalyzer();

    public Analyzer createAnalyzer();
}

