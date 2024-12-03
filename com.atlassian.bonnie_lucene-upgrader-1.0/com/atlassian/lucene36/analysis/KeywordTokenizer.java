/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.Tokenizer;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.util.AttributeSource;
import java.io.IOException;
import java.io.Reader;

public final class KeywordTokenizer
extends Tokenizer {
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private boolean done = false;
    private int finalOffset;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private OffsetAttribute offsetAtt = this.addAttribute(OffsetAttribute.class);

    public KeywordTokenizer(Reader input) {
        this(input, 256);
    }

    public KeywordTokenizer(Reader input, int bufferSize) {
        super(input);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.termAtt.resizeBuffer(bufferSize);
    }

    public KeywordTokenizer(AttributeSource source, Reader input, int bufferSize) {
        super(source, input);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.termAtt.resizeBuffer(bufferSize);
    }

    public KeywordTokenizer(AttributeSource.AttributeFactory factory, Reader input, int bufferSize) {
        super(factory, input);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.termAtt.resizeBuffer(bufferSize);
    }

    public final boolean incrementToken() throws IOException {
        if (!this.done) {
            int length;
            this.clearAttributes();
            this.done = true;
            int upto = 0;
            char[] buffer = this.termAtt.buffer();
            while ((length = this.input.read(buffer, upto, buffer.length - upto)) != -1) {
                if ((upto += length) != buffer.length) continue;
                buffer = this.termAtt.resizeBuffer(1 + buffer.length);
            }
            this.termAtt.setLength(upto);
            this.finalOffset = this.correctOffset(upto);
            this.offsetAtt.setOffset(this.correctOffset(0), this.finalOffset);
            return true;
        }
        return false;
    }

    public final void end() {
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }

    public void reset(Reader input) throws IOException {
        super.reset(input);
        this.done = false;
    }
}

