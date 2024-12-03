/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.Tokenizer
 */
package org.apache.lucene.analysis.core;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;

public final class KeywordAnalyzer
extends Analyzer {
    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new Analyzer.TokenStreamComponents((Tokenizer)new KeywordTokenizer(reader));
    }
}

