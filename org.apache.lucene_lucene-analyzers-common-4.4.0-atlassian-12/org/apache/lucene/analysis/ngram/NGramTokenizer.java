/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class NGramTokenizer
extends Tokenizer {
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private CharacterUtils charUtils;
    private CharacterUtils.CharacterBuffer charBuffer;
    private int[] buffer;
    private int bufferStart;
    private int bufferEnd;
    private int offset;
    private int gramSize;
    private int minGram;
    private int maxGram;
    private boolean exhausted;
    private int lastCheckedChar;
    private int lastNonTokenChar;
    private boolean edgesOnly;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

    NGramTokenizer(Version version, Reader input, int minGram, int maxGram, boolean edgesOnly) {
        super(input);
        this.init(version, minGram, maxGram, edgesOnly);
    }

    public NGramTokenizer(Version version, Reader input, int minGram, int maxGram) {
        this(version, input, minGram, maxGram, false);
    }

    NGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, int minGram, int maxGram, boolean edgesOnly) {
        super(factory, input);
        this.init(version, minGram, maxGram, edgesOnly);
    }

    public NGramTokenizer(Version version, AttributeSource.AttributeFactory factory, Reader input, int minGram, int maxGram) {
        this(version, factory, input, minGram, maxGram, false);
    }

    public NGramTokenizer(Version version, Reader input) {
        this(version, input, 1, 2);
    }

    private void init(Version version, int minGram, int maxGram, boolean edgesOnly) {
        if (!version.onOrAfter(Version.LUCENE_44)) {
            throw new IllegalArgumentException("This class only works with Lucene 4.4+. To emulate the old (broken) behavior of NGramTokenizer, use Lucene43NGramTokenizer/Lucene43EdgeNGramTokenizer");
        }
        CharacterUtils characterUtils = this.charUtils = version.onOrAfter(Version.LUCENE_44) ? CharacterUtils.getInstance(version) : CharacterUtils.getJava4Instance();
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.edgesOnly = edgesOnly;
        this.charBuffer = CharacterUtils.newCharacterBuffer(2 * maxGram + 1024);
        this.buffer = new int[this.charBuffer.getBuffer().length];
        this.termAtt.resizeBuffer(2 * maxGram);
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        while (true) {
            boolean isEdgeAndPreviousCharIsTokenChar;
            if (this.bufferStart >= this.bufferEnd - this.maxGram - 1 && !this.exhausted) {
                System.arraycopy(this.buffer, this.bufferStart, this.buffer, 0, this.bufferEnd - this.bufferStart);
                this.bufferEnd -= this.bufferStart;
                this.lastCheckedChar -= this.bufferStart;
                this.lastNonTokenChar -= this.bufferStart;
                this.bufferStart = 0;
                this.exhausted = !this.charUtils.fill(this.charBuffer, this.input, this.buffer.length - this.bufferEnd);
                this.bufferEnd += this.charUtils.toCodePoints(this.charBuffer.getBuffer(), 0, this.charBuffer.getLength(), this.buffer, this.bufferEnd);
            }
            if (this.gramSize > this.maxGram || this.bufferStart + this.gramSize > this.bufferEnd) {
                if (this.bufferStart + 1 + this.minGram > this.bufferEnd) {
                    assert (this.exhausted);
                    return false;
                }
                this.consume();
                this.gramSize = this.minGram;
            }
            this.updateLastNonTokenChar();
            boolean termContainsNonTokenChar = this.lastNonTokenChar >= this.bufferStart && this.lastNonTokenChar < this.bufferStart + this.gramSize;
            boolean bl = isEdgeAndPreviousCharIsTokenChar = this.edgesOnly && this.lastNonTokenChar != this.bufferStart - 1;
            if (!termContainsNonTokenChar && !isEdgeAndPreviousCharIsTokenChar) break;
            this.consume();
            this.gramSize = this.minGram;
        }
        int length = this.charUtils.toChars(this.buffer, this.bufferStart, this.gramSize, this.termAtt.buffer(), 0);
        this.termAtt.setLength(length);
        this.posIncAtt.setPositionIncrement(1);
        this.posLenAtt.setPositionLength(1);
        this.offsetAtt.setOffset(this.correctOffset(this.offset), this.correctOffset(this.offset + length));
        ++this.gramSize;
        return true;
    }

    private void updateLastNonTokenChar() {
        int termEnd = this.bufferStart + this.gramSize - 1;
        if (termEnd > this.lastCheckedChar) {
            for (int i = termEnd; i > this.lastCheckedChar; --i) {
                if (this.isTokenChar(this.buffer[i])) continue;
                this.lastNonTokenChar = i;
                break;
            }
            this.lastCheckedChar = termEnd;
        }
    }

    private void consume() {
        this.offset += Character.charCount(this.buffer[this.bufferStart++]);
    }

    protected boolean isTokenChar(int chr) {
        return true;
    }

    public final void end() {
        assert (this.bufferStart <= this.bufferEnd);
        int endOffset = this.offset;
        for (int i = this.bufferStart; i < this.bufferEnd; ++i) {
            endOffset += Character.charCount(this.buffer[i]);
        }
        endOffset = this.correctOffset(endOffset);
        this.offsetAtt.setOffset(endOffset, endOffset);
    }

    public final void reset() throws IOException {
        super.reset();
        this.bufferStart = this.bufferEnd = this.buffer.length;
        this.lastNonTokenChar = this.lastCheckedChar = this.bufferStart - 1;
        this.offset = 0;
        this.gramSize = this.minGram;
        this.exhausted = false;
        this.charBuffer.reset();
    }
}

