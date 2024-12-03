/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.KeywordTokenizer;
import com.atlassian.lucene36.analysis.ReusableAnalyzerBase;
import java.io.Reader;

public final class KeywordAnalyzer
extends ReusableAnalyzerBase {
    protected ReusableAnalyzerBase.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new ReusableAnalyzerBase.TokenStreamComponents(new KeywordTokenizer(reader));
    }
}

