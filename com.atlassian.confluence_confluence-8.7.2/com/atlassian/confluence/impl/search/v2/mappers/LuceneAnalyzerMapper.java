/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import org.apache.lucene.analysis.Analyzer;

public interface LuceneAnalyzerMapper {
    public Analyzer map(MappingAnalyzerDescriptor var1);
}

