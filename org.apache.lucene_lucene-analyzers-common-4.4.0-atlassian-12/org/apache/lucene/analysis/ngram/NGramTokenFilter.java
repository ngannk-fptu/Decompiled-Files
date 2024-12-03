/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public final class NGramTokenFilter
extends TokenFilter {
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private final int minGram;
    private final int maxGram;
    private char[] curTermBuffer;
    private int curTermLength;
    private int curCodePointCount;
    private int curGramSize;
    private int curPos;
    private int curPosInc;
    private int curPosLen;
    private int tokStart;
    private int tokEnd;
    private boolean hasIllegalOffsets;
    private final Version version;
    private final CharacterUtils charUtils;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt;
    private final PositionLengthAttribute posLenAtt;
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

    public NGramTokenFilter(Version version, TokenStream input, int minGram, int maxGram) {
        super((TokenStream)new LengthFilter(version, input, minGram, Integer.MAX_VALUE));
        this.version = version;
        CharacterUtils characterUtils = this.charUtils = version.onOrAfter(Version.LUCENE_44) ? CharacterUtils.getInstance(version) : CharacterUtils.getJava4Instance();
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        if (version.onOrAfter(Version.LUCENE_44)) {
            this.posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
            this.posLenAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
        } else {
            this.posIncAtt = new PositionIncrementAttribute(){

                public void setPositionIncrement(int positionIncrement) {
                }

                public int getPositionIncrement() {
                    return 0;
                }
            };
            this.posLenAtt = new PositionLengthAttribute(){

                public void setPositionLength(int positionLength) {
                }

                public int getPositionLength() {
                    return 0;
                }
            };
        }
    }

    public NGramTokenFilter(Version version, TokenStream input) {
        this(version, input, 1, 2);
    }

    public final boolean incrementToken() throws IOException {
        while (true) {
            if (this.curTermBuffer == null) {
                if (!this.input.incrementToken()) {
                    return false;
                }
                this.curTermBuffer = (char[])this.termAtt.buffer().clone();
                this.curTermLength = this.termAtt.length();
                this.curCodePointCount = this.charUtils.codePointCount((CharSequence)this.termAtt);
                this.curGramSize = this.minGram;
                this.curPos = 0;
                this.curPosInc = this.posIncAtt.getPositionIncrement();
                this.curPosLen = this.posLenAtt.getPositionLength();
                this.tokStart = this.offsetAtt.startOffset();
                this.tokEnd = this.offsetAtt.endOffset();
                boolean bl = this.hasIllegalOffsets = this.tokStart + this.curTermLength != this.tokEnd;
            }
            if (this.version.onOrAfter(Version.LUCENE_44)) {
                if (this.curGramSize > this.maxGram || this.curPos + this.curGramSize > this.curCodePointCount) {
                    ++this.curPos;
                    this.curGramSize = this.minGram;
                }
                if (this.curPos + this.curGramSize <= this.curCodePointCount) {
                    this.clearAttributes();
                    int start = this.charUtils.offsetByCodePoints(this.curTermBuffer, 0, this.curTermLength, 0, this.curPos);
                    int end = this.charUtils.offsetByCodePoints(this.curTermBuffer, 0, this.curTermLength, start, this.curGramSize);
                    this.termAtt.copyBuffer(this.curTermBuffer, start, end - start);
                    this.posIncAtt.setPositionIncrement(this.curPosInc);
                    this.curPosInc = 0;
                    this.posLenAtt.setPositionLength(this.curPosLen);
                    this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
                    ++this.curGramSize;
                    return true;
                }
            } else {
                while (this.curGramSize <= this.maxGram) {
                    if (this.curPos + this.curGramSize <= this.curTermLength) {
                        this.clearAttributes();
                        this.termAtt.copyBuffer(this.curTermBuffer, this.curPos, this.curGramSize);
                        if (this.hasIllegalOffsets) {
                            this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
                        } else {
                            this.offsetAtt.setOffset(this.tokStart + this.curPos, this.tokStart + this.curPos + this.curGramSize);
                        }
                        ++this.curPos;
                        return true;
                    }
                    ++this.curGramSize;
                    this.curPos = 0;
                }
            }
            this.curTermBuffer = null;
        }
    }

    public void reset() throws IOException {
        super.reset();
        this.curTermBuffer = null;
    }
}

