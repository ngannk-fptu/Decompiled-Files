/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.cn;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public final class ChineseTokenizer
extends Tokenizer {
    private int offset = 0;
    private int bufferIndex = 0;
    private int dataLen = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 1024;
    private final char[] buffer = new char[255];
    private final char[] ioBuffer = new char[1024];
    private int length;
    private int start;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

    public ChineseTokenizer(Reader in) {
        super(in);
    }

    public ChineseTokenizer(AttributeSource.AttributeFactory factory, Reader in) {
        super(factory, in);
    }

    private final void push(char c) {
        if (this.length == 0) {
            this.start = this.offset - 1;
        }
        this.buffer[this.length++] = Character.toLowerCase(c);
    }

    private final boolean flush() {
        if (this.length > 0) {
            this.termAtt.copyBuffer(this.buffer, 0, this.length);
            this.offsetAtt.setOffset(this.correctOffset(this.start), this.correctOffset(this.start + this.length));
            return true;
        }
        return false;
    }

    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        this.length = 0;
        this.start = this.offset;
        block4: while (true) {
            ++this.offset;
            if (this.bufferIndex >= this.dataLen) {
                this.dataLen = this.input.read(this.ioBuffer);
                this.bufferIndex = 0;
            }
            if (this.dataLen == -1) {
                --this.offset;
                return this.flush();
            }
            char c = this.ioBuffer[this.bufferIndex++];
            switch (Character.getType(c)) {
                case 1: 
                case 2: 
                case 9: {
                    this.push(c);
                    if (this.length != 255) continue block4;
                    return this.flush();
                }
                case 5: {
                    if (this.length > 0) {
                        --this.bufferIndex;
                        --this.offset;
                        return this.flush();
                    }
                    this.push(c);
                    return this.flush();
                }
            }
            if (this.length > 0) break;
        }
        return this.flush();
    }

    public final void end() {
        int finalOffset = this.correctOffset(this.offset);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.dataLen = 0;
        this.bufferIndex = 0;
        this.offset = 0;
    }
}

