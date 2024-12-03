/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.hunspell;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public final class HunspellStemFilter
extends TokenFilter {
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final HunspellStemmer stemmer;
    private List<HunspellStemmer.Stem> buffer;
    private AttributeSource.State savedState;
    private final boolean dedup;

    public HunspellStemFilter(TokenStream input, HunspellDictionary dictionary) {
        this(input, dictionary, 2);
    }

    public HunspellStemFilter(TokenStream input, HunspellDictionary dictionary, int recursionCap) {
        this(input, dictionary, true, recursionCap);
    }

    public HunspellStemFilter(TokenStream input, HunspellDictionary dictionary, boolean dedup) {
        this(input, dictionary, dedup, 2);
    }

    public HunspellStemFilter(TokenStream input, HunspellDictionary dictionary, boolean dedup, int recursionCap) {
        super(input);
        this.dedup = dedup;
        this.stemmer = new HunspellStemmer(dictionary, recursionCap);
    }

    public boolean incrementToken() throws IOException {
        if (this.buffer != null && !this.buffer.isEmpty()) {
            HunspellStemmer.Stem nextStem = this.buffer.remove(0);
            this.restoreState(this.savedState);
            this.posIncAtt.setPositionIncrement(0);
            this.termAtt.copyBuffer(nextStem.getStem(), 0, nextStem.getStemLength());
            this.termAtt.setLength(nextStem.getStemLength());
            return true;
        }
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.keywordAtt.isKeyword()) {
            return true;
        }
        List<HunspellStemmer.Stem> list = this.buffer = this.dedup ? this.stemmer.uniqueStems(this.termAtt.buffer(), this.termAtt.length()) : this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length());
        if (this.buffer.isEmpty()) {
            return true;
        }
        HunspellStemmer.Stem stem = this.buffer.remove(0);
        this.termAtt.copyBuffer(stem.getStem(), 0, stem.getStemLength());
        this.termAtt.setLength(stem.getStemLength());
        if (!this.buffer.isEmpty()) {
            this.savedState = this.captureState();
        }
        return true;
    }

    public void reset() throws IOException {
        super.reset();
        this.buffer = null;
    }
}

