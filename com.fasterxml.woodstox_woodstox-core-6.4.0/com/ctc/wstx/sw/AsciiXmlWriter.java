/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.sw.EncodingXmlWriter;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;

public final class AsciiXmlWriter
extends EncodingXmlWriter {
    public AsciiXmlWriter(OutputStream out, WriterConfig cfg, boolean autoclose) throws IOException {
        super(out, cfg, "US-ASCII", autoclose);
    }

    @Override
    public void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int inEnd;
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                inEnd = offset + max;
                while (offset < inEnd) {
                    char c = cbuf[offset];
                    if (c < ' ') {
                        if (c != '\n' && c != '\r' && c != '\t') {
                            this.mOutputPtr = ptr;
                            c = this.handleInvalidChar(c);
                        }
                    } else if (c > '~') {
                        this.mOutputPtr = ptr;
                        if (c > '\u007f') {
                            this.handleInvalidAsciiChar(c);
                        } else if (this.mXml11) {
                            c = this.handleInvalidChar(c);
                        }
                    }
                    this.mOutputBuffer[ptr++] = (byte)c;
                    ++offset;
                }
            } else {
                inEnd = offset + max;
                while (offset < inEnd) {
                    this.mOutputBuffer[ptr++] = (byte)cbuf[offset];
                    ++offset;
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override
    public void writeRaw(String str, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int inEnd;
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                inEnd = offset + max;
                while (offset < inEnd) {
                    char c = str.charAt(offset);
                    if (c < ' ') {
                        if (c != '\n' && c != '\r' && c != '\t') {
                            this.mOutputPtr = ptr;
                            c = this.handleInvalidChar(c);
                        }
                    } else if (c > '~') {
                        this.mOutputPtr = ptr;
                        if (c > '\u007f') {
                            this.handleInvalidAsciiChar(c);
                        } else if (this.mXml11) {
                            c = this.handleInvalidChar(c);
                        }
                    }
                    this.mOutputBuffer[ptr++] = (byte)c;
                    ++offset;
                }
            } else {
                inEnd = offset + max;
                while (offset < inEnd) {
                    this.mOutputBuffer[ptr++] = (byte)str.charAt(offset);
                    ++offset;
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override
    protected void writeAttrValue(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        int ptr = this.mOutputPtr;
        block0: while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data.charAt(offset++);
                sec = this.calcSurrogate(sec);
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(sec);
                --len;
                continue;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int c;
                if ((c = data.charAt(offset++)) < 32) {
                    if (c == 13) {
                        if (!this.mEscapeCR) {
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    } else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                        c = this.handleInvalidChar(c);
                        this.mOutputBuffer[ptr++] = (byte)c;
                        continue;
                    }
                } else if (c < 127) {
                    if (c != 60 && c != 38 && c != 34) {
                        this.mOutputBuffer[ptr++] = (byte)c;
                        continue;
                    }
                } else if (c >= 55296 && c <= 57343) {
                    this.mSurrogate = c;
                    if (offset == inEnd) break;
                    c = this.calcSurrogate(data.charAt(offset++));
                }
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(c);
                len = data.length() - offset;
                continue block0;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override
    protected void writeAttrValue(char[] data, int offset, int len) throws IOException {
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data[offset++];
                sec = this.calcSurrogate(sec);
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(sec);
                --len;
                continue;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int c;
                if ((c = data[offset++]) < 32) {
                    if (c == 13) {
                        if (!this.mEscapeCR) {
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    } else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                        c = this.handleInvalidChar(c);
                        this.mOutputBuffer[ptr++] = (byte)c;
                        continue;
                    }
                } else if (c < 127) {
                    if (c != 60 && c != 38 && c != 34) {
                        this.mOutputBuffer[ptr++] = (byte)c;
                        continue;
                    }
                } else if (c >= 55296 && c <= 57343) {
                    this.mSurrogate = c;
                    if (offset == inEnd) break;
                    c = this.calcSurrogate(data[offset++]);
                }
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(c);
                max -= inEnd - offset;
                break;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override
    protected int writeCDataContent(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        block0: while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                char c;
                block9: {
                    block10: {
                        block11: {
                            block8: {
                                if ((c = data.charAt(offset++)) >= ' ') break block8;
                                if (c != '\n' && c != '\r' && c != '\t') {
                                    this.mOutputPtr = ptr;
                                    c = this.handleInvalidChar(c);
                                }
                                break block9;
                            }
                            if (c <= '~') break block10;
                            this.mOutputPtr = ptr;
                            if (c <= '\u007f') break block11;
                            this.handleInvalidAsciiChar(c);
                            break block9;
                        }
                        if (!this.mXml11) break block9;
                        c = this.handleInvalidChar(c);
                        break block9;
                    }
                    if (c == '>' && offset > 2 && data.charAt(offset - 2) == ']' && data.charAt(offset - 3) == ']') {
                        if (!this.mFixContent) {
                            return offset - 3;
                        }
                        this.mOutputPtr = ptr;
                        this.writeCDataEnd();
                        this.writeCDataStart();
                        this.writeAscii((byte)62);
                        ptr = this.mOutputPtr;
                        len = data.length() - offset;
                        continue block0;
                    }
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override
    protected int writeCDataContent(char[] cbuf, int start, int len) throws IOException {
        if (!this.mCheckContent) {
            this.writeRaw(cbuf, start, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        int offset = start;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                char c;
                block9: {
                    block10: {
                        block11: {
                            block8: {
                                if ((c = cbuf[offset++]) >= ' ') break block8;
                                if (c != '\n' && c != '\r' && c != '\t') {
                                    this.mOutputPtr = ptr;
                                    c = this.handleInvalidChar(c);
                                }
                                break block9;
                            }
                            if (c <= '~') break block10;
                            this.mOutputPtr = ptr;
                            if (c <= '\u007f') break block11;
                            this.handleInvalidAsciiChar(c);
                            break block9;
                        }
                        if (!this.mXml11) break block9;
                        c = this.handleInvalidChar(c);
                        break block9;
                    }
                    if (c == '>' && offset >= start + 3 && cbuf[offset - 2] == ']' && cbuf[offset - 3] == ']') {
                        if (!this.mFixContent) {
                            return offset - 3;
                        }
                        this.mOutputPtr = ptr;
                        this.writeCDataEnd();
                        this.writeCDataStart();
                        this.writeAscii((byte)62);
                        ptr = this.mOutputPtr;
                        max -= inEnd - offset;
                        break;
                    }
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override
    protected int writeCommentContent(String data) throws IOException {
        int max;
        int len;
        int offset = 0;
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        block0: for (len = data.length(); len > 0; len -= max) {
            max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                char c;
                block10: {
                    block11: {
                        block12: {
                            block9: {
                                if ((c = data.charAt(offset++)) >= ' ') break block9;
                                if (c != '\n' && c != '\r' && c != '\t') {
                                    this.mOutputPtr = ptr;
                                    c = this.handleInvalidChar(c);
                                }
                                break block10;
                            }
                            if (c <= '~') break block11;
                            this.mOutputPtr = ptr;
                            if (c <= '\u007f') break block12;
                            this.handleInvalidAsciiChar(c);
                            break block10;
                        }
                        if (!this.mXml11) break block10;
                        c = this.handleInvalidChar(c);
                        break block10;
                    }
                    if (c == '-' && offset > 1 && data.charAt(offset - 2) == '-') {
                        if (!this.mFixContent) {
                            return offset - 2;
                        }
                        this.mOutputBuffer[ptr++] = 32;
                        if (ptr >= this.mOutputBuffer.length) {
                            this.mOutputPtr = ptr;
                            this.flushBuffer();
                            ptr = 0;
                        }
                        this.mOutputBuffer[ptr++] = 45;
                        max -= inEnd - offset;
                        continue block0;
                    }
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override
    protected int writePIData(String data) throws IOException, XMLStreamException {
        int max;
        int len;
        int offset = 0;
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        for (len = data.length(); len > 0; len -= max) {
            max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                char c = data.charAt(offset);
                if (c < ' ') {
                    if (c != '\n' && c != '\r' && c != '\t') {
                        this.mOutputPtr = ptr;
                        c = this.handleInvalidChar(c);
                    }
                } else if (c > '~') {
                    this.mOutputPtr = ptr;
                    if (c > '\u007f') {
                        this.handleInvalidAsciiChar(c);
                    } else if (this.mXml11) {
                        c = this.handleInvalidChar(c);
                    }
                } else if (c == '>' && offset > 0 && data.charAt(offset - 1) == '?') {
                    return offset - 2;
                }
                this.mOutputBuffer[ptr++] = (byte)c;
                ++offset;
            }
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override
    protected void writeTextContent(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        block0: while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                this.flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data.charAt(offset++);
                sec = this.calcSurrogate(sec);
                this.writeAsEntity(sec);
                --len;
                continue;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int c;
                if ((c = data.charAt(offset++)) < 32) {
                    if (c == 10 || c == 9) {
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                    if (c == 13) {
                        if (!this.mEscapeCR) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    } else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                        c = this.handleInvalidChar(c);
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                } else if (c < 127) {
                    if (c != 60 && c != 38 && (c != 62 || offset > 1 && data.charAt(offset - 2) != ']')) {
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                } else if (c >= 55296 && c <= 57343) {
                    this.mSurrogate = c;
                    if (offset == inEnd) break;
                    c = this.calcSurrogate(data.charAt(offset++));
                }
                this.writeAsEntity(c);
                len = data.length() - offset;
                continue block0;
            }
            len -= max;
        }
    }

    @Override
    protected void writeTextContent(char[] cbuf, int offset, int len) throws IOException {
        while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                this.flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = cbuf[offset++];
                sec = this.calcSurrogate(sec);
                this.writeAsEntity(sec);
                --len;
                continue;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int c;
                if ((c = cbuf[offset++]) < 32) {
                    if (c == 10 || c == 9) {
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                    if (c == 13) {
                        if (!this.mEscapeCR) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    } else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                        c = this.handleInvalidChar(c);
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                } else if (c < 127) {
                    if (c != 60 && c != 38 && (c != 62 || offset > 1 && cbuf[offset - 2] != ']')) {
                        this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                        continue;
                    }
                } else if (c >= 55296 && c <= 57343) {
                    this.mSurrogate = c;
                    if (offset == inEnd) break;
                    c = this.calcSurrogate(cbuf[offset++]);
                }
                this.writeAsEntity(c);
                max -= inEnd - offset;
                break;
            }
            len -= max;
        }
    }

    protected void handleInvalidAsciiChar(int c) throws IOException {
        this.flush();
        throw new IOException("Invalid XML character (0x" + Integer.toHexString(c) + "); can only be output using character entity when using US-ASCII encoding");
    }
}

