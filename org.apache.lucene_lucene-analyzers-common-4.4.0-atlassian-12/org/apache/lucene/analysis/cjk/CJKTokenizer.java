/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.cjk;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

@Deprecated
public final class CJKTokenizer
extends Tokenizer {
    static final int WORD_TYPE = 0;
    static final int SINGLE_TOKEN_TYPE = 1;
    static final int DOUBLE_TOKEN_TYPE = 2;
    static final String[] TOKEN_TYPE_NAMES = new String[]{"word", "single", "double"};
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 256;
    private int offset = 0;
    private int bufferIndex = 0;
    private int dataLen = 0;
    private final char[] buffer = new char[255];
    private final char[] ioBuffer = new char[256];
    private int tokenType = 0;
    private boolean preIsTokened = false;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);

    public CJKTokenizer(Reader in) {
        super(in);
    }

    public CJKTokenizer(AttributeSource.AttributeFactory factory, Reader in) {
        super(factory, in);
    }

    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        do {
            int length = 0;
            int start = this.offset;
            while (true) {
                char c;
                Character.UnicodeBlock ub;
                ++this.offset;
                if (this.bufferIndex >= this.dataLen) {
                    this.dataLen = this.input.read(this.ioBuffer);
                    this.bufferIndex = 0;
                }
                if (this.dataLen == -1) {
                    if (length > 0) {
                        if (this.preIsTokened) {
                            length = 0;
                            this.preIsTokened = false;
                            break;
                        }
                        --this.offset;
                        break;
                    }
                    --this.offset;
                    return false;
                }
                if ((ub = Character.UnicodeBlock.of(c = this.ioBuffer[this.bufferIndex++])) == Character.UnicodeBlock.BASIC_LATIN || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                    char i;
                    if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS && (i = c) >= '\uff01' && i <= '\uff5e') {
                        c = i -= 65248;
                    }
                    if (Character.isLetterOrDigit(c) || c == '_' || c == '+' || c == '#') {
                        if (length == 0) {
                            start = this.offset - 1;
                        } else if (this.tokenType == 2) {
                            --this.offset;
                            --this.bufferIndex;
                            if (!this.preIsTokened) break;
                            length = 0;
                            this.preIsTokened = false;
                            break;
                        }
                        this.buffer[length++] = Character.toLowerCase(c);
                        this.tokenType = 1;
                        if (length != 255) continue;
                        break;
                    }
                    if (length <= 0) continue;
                    if (!this.preIsTokened) break;
                    length = 0;
                    this.preIsTokened = false;
                    continue;
                }
                if (Character.isLetter(c)) {
                    if (length == 0) {
                        start = this.offset - 1;
                        this.buffer[length++] = c;
                        this.tokenType = 2;
                        continue;
                    }
                    if (this.tokenType == 1) {
                        --this.offset;
                        --this.bufferIndex;
                        break;
                    }
                    this.buffer[length++] = c;
                    this.tokenType = 2;
                    if (length != 2) continue;
                    --this.offset;
                    --this.bufferIndex;
                    this.preIsTokened = true;
                    break;
                }
                if (length <= 0) continue;
                if (!this.preIsTokened) break;
                length = 0;
                this.preIsTokened = false;
            }
            if (length <= 0) continue;
            this.termAtt.copyBuffer(this.buffer, 0, length);
            this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + length));
            this.typeAtt.setType(TOKEN_TYPE_NAMES[this.tokenType]);
            return true;
        } while (this.dataLen != -1);
        --this.offset;
        return false;
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
        this.preIsTokened = false;
        this.tokenType = 0;
    }
}

