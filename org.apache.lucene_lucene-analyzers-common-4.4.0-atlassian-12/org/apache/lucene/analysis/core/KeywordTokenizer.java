/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.core;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

public final class KeywordTokenizer
extends Tokenizer {
    public static final int DEFAULT_BUFFER_SIZE = 256;
    private boolean done = false;
    private int finalOffset;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

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

    public void reset() throws IOException {
        this.done = false;
    }
}

