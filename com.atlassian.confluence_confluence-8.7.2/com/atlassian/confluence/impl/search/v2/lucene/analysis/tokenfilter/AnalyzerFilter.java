/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.AttributeSource$State
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.tokenfilter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;

public class AnalyzerFilter
extends TokenFilter {
    private String fieldName;
    private Analyzer analyzer;
    private List<AttributeSource.State> cache = null;
    private Iterator<AttributeSource.State> iterator = null;
    private AttributeSource.State finalState;

    public AnalyzerFilter(TokenStream input, String fieldName, Analyzer analyzer) {
        super(input);
        this.fieldName = fieldName;
        this.analyzer = analyzer;
    }

    private void addTokenStreamAttributes(TokenStream analyzedTokenStream) {
        Iterator attributeClassesIterator = analyzedTokenStream.getAttributeClassesIterator();
        while (attributeClassesIterator.hasNext()) {
            this.addAttribute((Class)attributeClassesIterator.next());
        }
    }

    public final boolean incrementToken() throws IOException {
        if (this.cache == null) {
            this.cache = new LinkedList<AttributeSource.State>();
            this.fillCache();
            this.iterator = this.cache.iterator();
        }
        if (!this.iterator.hasNext()) {
            this.cache = null;
            return false;
        }
        this.restoreState(this.iterator.next());
        return true;
    }

    private void fillCache() {
        try {
            StringBuilder buffer = new StringBuilder();
            CharTermAttribute charTermAttribute = (CharTermAttribute)this.input.addAttribute(CharTermAttribute.class);
            this.input.reset();
            while (this.input.incrementToken()) {
                buffer.append(charTermAttribute.toString()).append(" ");
            }
            this.input.end();
            this.input.reset();
            TokenStream analyzedTokenStream = this.analyzer.tokenStream(this.fieldName, (Reader)new StringReader(buffer.toString()));
            this.addTokenStreamAttributes(analyzedTokenStream);
            analyzedTokenStream.reset();
            while (analyzedTokenStream.incrementToken()) {
                this.cache.add(analyzedTokenStream.captureState());
            }
            analyzedTokenStream.end();
            this.finalState = analyzedTokenStream.captureState();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Error reading tokens for field " + this.fieldName, ioe);
        }
    }

    public void reset() {
        if (this.cache != null) {
            this.iterator = this.cache.iterator();
        }
    }

    public final void end() {
        if (this.finalState != null) {
            this.restoreState(this.finalState);
        }
    }
}

