/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.AttributeSource$State
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.compound;

import java.io.IOException;
import java.util.LinkedList;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public abstract class CompoundWordTokenFilterBase
extends TokenFilter {
    public static final int DEFAULT_MIN_WORD_SIZE = 5;
    public static final int DEFAULT_MIN_SUBWORD_SIZE = 2;
    public static final int DEFAULT_MAX_SUBWORD_SIZE = 15;
    protected final Version matchVersion;
    protected final CharArraySet dictionary;
    protected final LinkedList<CompoundToken> tokens;
    protected final int minWordSize;
    protected final int minSubwordSize;
    protected final int maxSubwordSize;
    protected final boolean onlyLongestMatch;
    protected final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private AttributeSource.State current;

    protected CompoundWordTokenFilterBase(Version matchVersion, TokenStream input, CharArraySet dictionary, boolean onlyLongestMatch) {
        this(matchVersion, input, dictionary, 5, 2, 15, onlyLongestMatch);
    }

    protected CompoundWordTokenFilterBase(Version matchVersion, TokenStream input, CharArraySet dictionary) {
        this(matchVersion, input, dictionary, 5, 2, 15, false);
    }

    protected CompoundWordTokenFilterBase(Version matchVersion, TokenStream input, CharArraySet dictionary, int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch) {
        super(input);
        this.matchVersion = matchVersion;
        this.tokens = new LinkedList();
        if (minWordSize < 0) {
            throw new IllegalArgumentException("minWordSize cannot be negative");
        }
        this.minWordSize = minWordSize;
        if (minSubwordSize < 0) {
            throw new IllegalArgumentException("minSubwordSize cannot be negative");
        }
        this.minSubwordSize = minSubwordSize;
        if (maxSubwordSize < 0) {
            throw new IllegalArgumentException("maxSubwordSize cannot be negative");
        }
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = dictionary;
    }

    public final boolean incrementToken() throws IOException {
        if (!this.tokens.isEmpty()) {
            assert (this.current != null);
            CompoundToken token = this.tokens.removeFirst();
            this.restoreState(this.current);
            this.termAtt.setEmpty().append(token.txt);
            this.offsetAtt.setOffset(token.startOffset, token.endOffset);
            this.posIncAtt.setPositionIncrement(0);
            return true;
        }
        this.current = null;
        if (this.input.incrementToken()) {
            if (this.termAtt.length() >= this.minWordSize) {
                this.decompose();
                if (!this.tokens.isEmpty()) {
                    this.current = this.captureState();
                }
            }
            return true;
        }
        return false;
    }

    protected abstract void decompose();

    public void reset() throws IOException {
        super.reset();
        this.tokens.clear();
        this.current = null;
    }

    protected class CompoundToken {
        public final CharSequence txt;
        public final int startOffset;
        public final int endOffset;

        public CompoundToken(int offset, int length) {
            this.txt = CompoundWordTokenFilterBase.this.termAtt.subSequence(offset, offset + length);
            int startOff = CompoundWordTokenFilterBase.this.offsetAtt.startOffset();
            int endOff = CompoundWordTokenFilterBase.this.offsetAtt.endOffset();
            if (CompoundWordTokenFilterBase.this.matchVersion.onOrAfter(Version.LUCENE_44) || endOff - startOff != CompoundWordTokenFilterBase.this.termAtt.length()) {
                this.startOffset = startOff;
                this.endOffset = endOff;
            } else {
                int newStart;
                this.startOffset = newStart = startOff + offset;
                this.endOffset = newStart + length;
            }
        }
    }
}

