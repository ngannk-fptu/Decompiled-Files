/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 */
package org.apache.lucene.analysis.cn;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.ChineseFilter;
import org.apache.lucene.analysis.cn.ChineseTokenizer;

@Deprecated
public final class ChineseAnalyzer
extends Analyzer {
    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        ChineseTokenizer source = new ChineseTokenizer(reader);
        return new Analyzer.TokenStreamComponents((Tokenizer)source, (TokenStream)new ChineseFilter((TokenStream)source));
    }
}

