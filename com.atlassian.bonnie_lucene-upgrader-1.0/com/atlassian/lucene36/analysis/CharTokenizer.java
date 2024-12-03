/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.Tokenizer;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.util.AttributeSource;
import com.atlassian.lucene36.util.CharacterUtils;
import com.atlassian.lucene36.util.Version;
import com.atlassian.lucene36.util.VirtualMethod;
import java.io.IOException;
import java.io.Reader;

public abstract class CharTokenizer
extends Tokenizer {
    private int offset = 0;
    private int bufferIndex = 0;
    private int dataLen = 0;
    private int finalOffset = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 4096;
    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = this.addAttribute(OffsetAttribute.class);
    private final CharacterUtils charUtils;
    private final CharacterUtils.CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(4096);
    @Deprecated
    private final boolean useOldAPI;
    @Deprecated
    private static final VirtualMethod<CharTokenizer> isTokenCharMethod = new VirtualMethod<CharTokenizer>(CharTokenizer.class, "isTokenChar", Character.TYPE);
    @Deprecated
    private static final VirtualMethod<CharTokenizer> normalizeMethod = new VirtualMethod<CharTokenizer>(CharTokenizer.class, "normalize", Character.TYPE);

    public CharTokenizer(Version matchVersion, Reader input) {
        super(input);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
        this.useOldAPI = this.useOldAPI(matchVersion);
    }

    public CharTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(source, input);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
        this.useOldAPI = this.useOldAPI(matchVersion);
    }

    public CharTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(factory, input);
        this.charUtils = CharacterUtils.getInstance(matchVersion);
        this.useOldAPI = this.useOldAPI(matchVersion);
    }

    @Deprecated
    public CharTokenizer(Reader input) {
        this(Version.LUCENE_30, input);
    }

    @Deprecated
    public CharTokenizer(AttributeSource source, Reader input) {
        this(Version.LUCENE_30, source, input);
    }

    @Deprecated
    public CharTokenizer(AttributeSource.AttributeFactory factory, Reader input) {
        this(Version.LUCENE_30, factory, input);
    }

    @Deprecated
    protected boolean isTokenChar(char c) {
        return this.isTokenChar((int)c);
    }

    @Deprecated
    protected char normalize(char c) {
        return (char)this.normalize((int)c);
    }

    protected boolean isTokenChar(int c) {
        throw new UnsupportedOperationException("since LUCENE_31 subclasses of CharTokenizer must implement isTokenChar(int)");
    }

    protected int normalize(int c) {
        return c;
    }

    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (this.useOldAPI) {
            return this.incrementTokenOld();
        }
        int length = 0;
        int start = -1;
        int end = -1;
        char[] buffer = this.termAtt.buffer();
        while (true) {
            if (this.bufferIndex >= this.dataLen) {
                this.offset += this.dataLen;
                if (!this.charUtils.fill(this.ioBuffer, this.input)) {
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
            int c = this.charUtils.codePointAt(this.ioBuffer.getBuffer(), this.bufferIndex);
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

    @Deprecated
    private boolean incrementTokenOld() throws IOException {
        int length = 0;
        int start = -1;
        char[] buffer = this.termAtt.buffer();
        char[] oldIoBuffer = this.ioBuffer.getBuffer();
        while (true) {
            char c;
            if (this.bufferIndex >= this.dataLen) {
                this.offset += this.dataLen;
                this.dataLen = this.input.read(oldIoBuffer);
                if (this.dataLen == -1) {
                    this.dataLen = 0;
                    if (length <= 0) {
                        this.finalOffset = this.correctOffset(this.offset);
                        return false;
                    }
                    break;
                }
                this.bufferIndex = 0;
            }
            if (this.isTokenChar(c = oldIoBuffer[this.bufferIndex++])) {
                if (length == 0) {
                    assert (start == -1);
                    start = this.offset + this.bufferIndex - 1;
                } else if (length == buffer.length) {
                    buffer = this.termAtt.resizeBuffer(1 + length);
                }
                buffer[length++] = this.normalize(c);
                if (length != 255) continue;
                break;
            }
            if (length > 0) break;
        }
        this.termAtt.setLength(length);
        assert (start != -1);
        this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(start + length));
        return true;
    }

    public final void end() {
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }

    public void reset(Reader input) throws IOException {
        super.reset(input);
        this.bufferIndex = 0;
        this.offset = 0;
        this.dataLen = 0;
        this.finalOffset = 0;
        this.ioBuffer.reset();
    }

    @Deprecated
    private boolean useOldAPI(Version matchVersion) {
        Class<?> clazz = this.getClass();
        if (matchVersion.onOrAfter(Version.LUCENE_31) && (isTokenCharMethod.isOverriddenAsOf(clazz) || normalizeMethod.isOverriddenAsOf(clazz))) {
            throw new IllegalArgumentException("For matchVersion >= LUCENE_31, CharTokenizer subclasses must not override isTokenChar(char) or normalize(char).");
        }
        return !matchVersion.onOrAfter(Version.LUCENE_31);
    }
}

