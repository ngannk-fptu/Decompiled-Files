/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 */
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import java.io.IOException;
import org.apache.lucene.index.AtomicReader;

public interface LuceneScoreFunctionFactory {
    public LuceneScoreFunction create(AtomicReader var1) throws IOException;
}

