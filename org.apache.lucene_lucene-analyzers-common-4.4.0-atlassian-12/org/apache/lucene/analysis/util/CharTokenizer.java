/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public abstract class CharTokenizer
extends Tokenizer {
    private int offset = 0;
    private int bufferIndex = -1;
    private int dataLen = 0;
    private int finalOffset = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 4096;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final CharacterUtils charUtils;
    private final CharacterUtils.CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(4096);

    public CharTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
    }

    public CharTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
    }

    protected abstract boolean isTokenChar(int var1);

    protected int normalize(int c) {
        return c;
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        int length = 0;
        int start = -1;
        int end = -1;
        char[] buffer = this.termAtt.buffer();
        while (true) {
            if (this.bufferIndex >= this.dataLen) {
                this.offset += this.dataLen;
                this.charUtils.fill(this.ioBuffer, this.input);
                if (this.ioBuffer.getLength() == 0) {
                    this.dataLen = 0;
                    if (length <= 0) {
                        this.finalOffset = this.correctOffset(this.offset);
                        return false;
                    }
                    break;
                }
                this.dataLen = this.ioBuffer.getLength();
                this.bufferIndex = 0;
            }
            int c = this.charUtils.codePointAt(this.ioBuffer.getBuffer(), this.bufferIndex, this.ioBuffer.getLength());
            int charCount = Character.charCount(c);
            this.bufferIndex += charCount;
            if (this.isTokenChar(c)) {
                if (length == 0) {
                    assert (start == -1);
                    end = start = this.offset + this.bufferIndex - charCount;
                } else if (length >= buffer.length - 1) {
                    buffer = this.termAtt.resizeBuffer(2 + length);
                }
                end += charCount;
                if ((length += Character.toChars(this.normalize(c), buffer, length)) < 255) continue;
                break;
            }
            if (length > 0) break;
        }
        this.termAtt.setLength(length);
        assert (start != -1);
        this.finalOffset = this.correctOffset(end);
        this.offsetAtt.setOffset(this.correctOffset(start), this.finalOffset);
        return true;
    }

    public final void end() {
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }

    public void reset() throws IOException {
        this.bufferIndex = 0;
        this.offset = 0;
        this.dataLen = 0;
        this.finalOffset = 0;
        this.ioBuffer.reset();
    }
}

