/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.google.common.base.Strings
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class LuceneTextFieldTokenizer {
    private static final Logger log = LoggerFactory.getLogger(LuceneTextFieldTokenizer.class);
    private final LuceneAnalyzerFactory luceneAnalyzerFactory;

    public LuceneTextFieldTokenizer(LuceneAnalyzerFactory luceneAnalyzerFactory) {
        this.luceneAnalyzerFactory = luceneAnalyzerFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> tokenize(String fieldName, String text) {
        if (Strings.isNullOrEmpty((String)text)) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<String>();
        Analyzer analyzer = this.luceneAnalyzerFactory.createAnalyzer();
        try (TokenStream tokens = analyzer.tokenStream(fieldName, (Reader)new StringReader(text));){
            try {
                CharTermAttribute charTermAtt = (CharTermAttribute)tokens.addAttribute(CharTermAttribute.class);
                tokens.reset();
                while (tokens.incrementToken()) {
                    result.add(charTermAtt.toString());
                }
            }
            finally {
                tokens.end();
            }
        }
        catch (IOException e) {
            log.error("Error iterating through token stream.", (Throwable)e);
        }
        return result;
    }
}

