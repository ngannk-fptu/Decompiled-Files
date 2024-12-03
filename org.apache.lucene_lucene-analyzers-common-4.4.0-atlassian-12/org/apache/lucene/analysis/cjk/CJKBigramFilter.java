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
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.AttributeSource$State
 */
package org.apache.lucene.analysis.cjk;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;

public final class CJKBigramFilter
extends TokenFilter {
    public static final int HAN = 1;
    public static final int HIRAGANA = 2;
    public static final int KATAKANA = 4;
    public static final int HANGUL = 8;
    public static final String DOUBLE_TYPE = "<DOUBLE>";
    public static final String SINGLE_TYPE = "<SINGLE>";
    private static final String HAN_TYPE = StandardTokenizer.TOKEN_TYPES[10];
    private static final String HIRAGANA_TYPE = StandardTokenizer.TOKEN_TYPES[11];
    private static final String KATAKANA_TYPE = StandardTokenizer.TOKEN_TYPES[12];
    private static final String HANGUL_TYPE = StandardTokenizer.TOKEN_TYPES[13];
    private static final Object NO = new Object();
    private final Object doHan;
    private final Object doHiragana;
    private final Object doKatakana;
    private final Object doHangul;
    private final boolean outputUnigrams;
    private boolean ngramState;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLengthAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    int[] buffer = new int[8];
    int[] startOffset = new int[8];
    int[] endOffset = new int[8];
    int bufferLen;
    int index;
    int lastEndOffset;
    private boolean exhausted;
    private AttributeSource.State loneState;

    public CJKBigramFilter(TokenStream in) {
        this(in, 15);
    }

    public CJKBigramFilter(TokenStream in, int flags) {
        this(in, flags, false);
    }

    public CJKBigramFilter(TokenStream in, int flags, boolean outputUnigrams) {
        super(in);
        this.doHan = (flags & 1) == 0 ? NO : HAN_TYPE;
        this.doHiragana = (flags & 2) == 0 ? NO : HIRAGANA_TYPE;
        this.doKatakana = (flags & 4) == 0 ? NO : KATAKANA_TYPE;
        this.doHangul = (flags & 8) == 0 ? NO : HANGUL_TYPE;
        this.outputUnigrams = outputUnigrams;
    }

    public boolean incrementToken() throws IOException {
        block10: {
            while (true) {
                if (this.hasBufferedBigram()) {
                    if (this.outputUnigrams) {
                        if (this.ngramState) {
                            this.flushBigram();
                        } else {
                            this.flushUnigram();
                            --this.index;
                        }
                        this.ngramState = !this.ngramState;
                    } else {
                        this.flushBigram();
                    }
                    return true;
                }
                if (!this.doNext()) break block10;
                String type = this.typeAtt.type();
                if (type != this.doHan && type != this.doHiragana && type != this.doKatakana && type != this.doHangul) break;
                if (this.offsetAtt.startOffset() != this.lastEndOffset) {
                    if (this.hasBufferedUnigram()) {
                        this.loneState = this.captureState();
                        this.flushUnigram();
                        return true;
                    }
                    this.index = 0;
                    this.bufferLen = 0;
                }
                this.refill();
            }
            if (this.hasBufferedUnigram()) {
                this.loneState = this.captureState();
                this.flushUnigram();
                return true;
            }
            return true;
        }
        if (this.hasBufferedUnigram()) {
            this.flushUnigram();
            return true;
        }
        return false;
    }

    private boolean doNext() throws IOException {
        if (this.loneState != null) {
            this.restoreState(this.loneState);
            this.loneState = null;
            return true;
        }
        if (this.exhausted) {
            return false;
        }
        if (this.input.incrementToken()) {
            return true;
        }
        this.exhausted = true;
        return false;
    }

    private void refill() {
        if (this.bufferLen > 64) {
            int last = this.bufferLen - 1;
            this.buffer[0] = this.buffer[last];
            this.startOffset[0] = this.startOffset[last];
            this.endOffset[0] = this.endOffset[last];
            this.bufferLen = 1;
            this.index -= last;
        }
        char[] termBuffer = this.termAtt.buffer();
        int len = this.termAtt.length();
        int start = this.offsetAtt.startOffset();
        int end = this.offsetAtt.endOffset();
        int newSize = this.bufferLen + len;
        this.buffer = ArrayUtil.grow((int[])this.buffer, (int)newSize);
        this.startOffset = ArrayUtil.grow((int[])this.startOffset, (int)newSize);
        this.endOffset = ArrayUtil.grow((int[])this.endOffset, (int)newSize);
        this.lastEndOffset = end;
        if (end - start != len) {
            int cp = 0;
            for (int i = 0; i < len; i += Character.charCount(cp)) {
                cp = this.buffer[this.bufferLen] = Character.codePointAt(termBuffer, i, len);
                this.startOffset[this.bufferLen] = start;
                this.endOffset[this.bufferLen] = end;
                ++this.bufferLen;
            }
        } else {
            int cp = 0;
            int cpLen = 0;
            for (int i = 0; i < len; i += cpLen) {
                cp = this.buffer[this.bufferLen] = Character.codePointAt(termBuffer, i, len);
                cpLen = Character.charCount(cp);
                this.startOffset[this.bufferLen] = start;
                start = this.endOffset[this.bufferLen] = start + cpLen;
                ++this.bufferLen;
            }
        }
    }

    private void flushBigram() {
        this.clearAttributes();
        char[] termBuffer = this.termAtt.resizeBuffer(4);
        int len1 = Character.toChars(this.buffer[this.index], termBuffer, 0);
        int len2 = len1 + Character.toChars(this.buffer[this.index + 1], termBuffer, len1);
        this.termAtt.setLength(len2);
        this.offsetAtt.setOffset(this.startOffset[this.index], this.endOffset[this.index + 1]);
        this.typeAtt.setType(DOUBLE_TYPE);
        if (this.outputUnigrams) {
            this.posIncAtt.setPositionIncrement(0);
            this.posLengthAtt.setPositionLength(2);
        }
        ++this.index;
    }

    private void flushUnigram() {
        this.clearAttributes();
        char[] termBuffer = this.termAtt.resizeBuffer(2);
        int len = Character.toChars(this.buffer[this.index], termBuffer, 0);
        this.termAtt.setLength(len);
        this.offsetAtt.setOffset(this.startOffset[this.index], this.endOffset[this.index]);
        this.typeAtt.setType(SINGLE_TYPE);
        ++this.index;
    }

    private boolean hasBufferedBigram() {
        return this.bufferLen - this.index > 1;
    }

    private boolean hasBufferedUnigram() {
        if (this.outputUnigrams) {
            return this.bufferLen - this.index == 1;
        }
        return this.bufferLen == 1 && this.index == 0;
    }

    public void reset() throws IOException {
        super.reset();
        this.bufferLen = 0;
        this.index = 0;
        this.lastEndOffset = 0;
        this.loneState = null;
        this.exhausted = false;
        this.ngramState = false;
    }
}

