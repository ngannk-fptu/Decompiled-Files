/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.ngram;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public final class Lucene43NGramTokenizer
extends Tokenizer {
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;
    private int minGram;
    private int maxGram;
    private int gramSize;
    private int pos;
    private int inLen;
    private int charsRead;
    private String inStr;
    private boolean started;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

    public Lucene43NGramTokenizer(Reader input, int minGram, int maxGram) {
        super(input);
        this.init(minGram, maxGram);
    }

    public Lucene43NGramTokenizer(AttributeSource.AttributeFactory factory, Reader input, int minGram, int maxGram) {
        super(factory, input);
        this.init(minGram, maxGram);
    }

    public Lucene43NGramTokenizer(Reader input) {
        this(input, 1, 2);
    }

    private void init(int minGram, int maxGram) {
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (!this.started) {
            int inc;
            this.started = true;
            this.gramSize = this.minGram;
            char[] chars = new char[1024];
            this.charsRead = 0;
            while (this.charsRead < chars.length && (inc = this.input.read(chars, this.charsRead, chars.length - this.charsRead)) != -1) {
                this.charsRead += inc;
            }
            this.inStr = new String(chars, 0, this.charsRead).trim();
            if (this.charsRead == chars.length) {
                int inc2;
                char[] throwaway = new char[1024];
                while ((inc2 = this.input.read(throwaway, 0, throwaway.length)) != -1) {
                    this.charsRead += inc2;
                }
            }
            this.inLen = this.inStr.length();
            if (this.inLen == 0) {
                return false;
            }
        }
        if (this.pos + this.gramSize > this.inLen) {
            this.pos = 0;
            ++this.gramSize;
            if (this.gramSize > this.maxGram) {
                return false;
            }
            if (this.pos + this.gramSize > this.inLen) {
                return false;
            }
        }
        int oldPos = this.pos++;
        this.termAtt.setEmpty().append((CharSequence)this.inStr, oldPos, oldPos + this.gramSize);
        this.offsetAtt.setOffset(this.correctOffset(oldPos), this.correctOffset(oldPos + this.gramSize));
        return true;
    }

    public void end() {
        int finalOffset = this.correctOffset(this.charsRead);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.started = false;
        this.pos = 0;
    }
}

