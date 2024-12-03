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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public final class EdgeNGramTokenFilter
extends TokenFilter {
    public static final Side DEFAULT_SIDE = Side.FRONT;
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;
    private final Version version;
    private final CharacterUtils charUtils;
    private final int minGram;
    private final int maxGram;
    private Side side;
    private char[] curTermBuffer;
    private int curTermLength;
    private int curCodePointCount;
    private int curGramSize;
    private int tokStart;
    private int tokEnd;
    private boolean updateOffsets;
    private int savePosIncr;
    private int savePosLen;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);

    @Deprecated
    public EdgeNGramTokenFilter(Version version, TokenStream input, Side side, int minGram, int maxGram) {
        super(input);
        if (version == null) {
            throw new IllegalArgumentException("version must not be null");
        }
        if (version.onOrAfter(Version.LUCENE_44) && side == Side.BACK) {
            throw new IllegalArgumentException("Side.BACK is not supported anymore as of Lucene 4.4, use ReverseStringFilter up-front and afterward");
        }
        if (side == null) {
            throw new IllegalArgumentException("sideLabel must be either front or back");
        }
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.version = version;
        this.charUtils = version.onOrAfter(Version.LUCENE_44) ? CharacterUtils.getInstance(version) : CharacterUtils.getJava4Instance();
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.side = side;
    }

    @Deprecated
    public EdgeNGramTokenFilter(Version version, TokenStream input, String sideLabel, int minGram, int maxGram) {
        this(version, input, Side.getSide(sideLabel), minGram, maxGram);
    }

    public EdgeNGramTokenFilter(Version version, TokenStream input, int minGram, int maxGram) {
        this(version, input, Side.FRONT, minGram, maxGram);
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
                this.tokStart = this.offsetAtt.startOffset();
                this.tokEnd = this.offsetAtt.endOffset();
                this.updateOffsets = this.version.onOrAfter(Version.LUCENE_44) ? false : this.tokStart + this.curTermLength == this.tokEnd;
                this.savePosIncr += this.posIncrAtt.getPositionIncrement();
                this.savePosLen = this.posLenAtt.getPositionLength();
            }
            if (this.curGramSize <= this.maxGram && this.curGramSize <= this.curCodePointCount) {
                int start = this.side == Side.FRONT ? 0 : this.charUtils.offsetByCodePoints(this.curTermBuffer, 0, this.curTermLength, this.curTermLength, -this.curGramSize);
                int end = this.charUtils.offsetByCodePoints(this.curTermBuffer, 0, this.curTermLength, start, this.curGramSize);
                this.clearAttributes();
                if (this.updateOffsets) {
                    this.offsetAtt.setOffset(this.tokStart + start, this.tokStart + end);
                } else {
                    this.offsetAtt.setOffset(this.tokStart, this.tokEnd);
                }
                if (this.curGramSize == this.minGram) {
                    this.posIncrAtt.setPositionIncrement(this.savePosIncr);
                    this.savePosIncr = 0;
                } else {
                    this.posIncrAtt.setPositionIncrement(0);
                }
                this.posLenAtt.setPositionLength(this.savePosLen);
                this.termAtt.copyBuffer(this.curTermBuffer, start, end - start);
                ++this.curGramSize;
                return true;
            }
            this.curTermBuffer = null;
        }
    }

    public void reset() throws IOException {
        super.reset();
        this.curTermBuffer = null;
        this.savePosIncr = 0;
    }

    public static enum Side {
        FRONT{

            @Override
            public String getLabel() {
                return "front";
            }
        }
        ,
        BACK{

            @Override
            public String getLabel() {
                return "back";
            }
        };


        public abstract String getLabel();

        public static Side getSide(String sideName) {
            if (FRONT.getLabel().equals(sideName)) {
                return FRONT;
            }
            if (BACK.getLabel().equals(sideName)) {
                return BACK;
            }
            return null;
        }
    }
}

