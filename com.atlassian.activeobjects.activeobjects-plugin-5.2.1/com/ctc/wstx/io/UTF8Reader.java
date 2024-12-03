/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BaseReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

public final class UTF8Reader
extends BaseReader {
    boolean mXml11 = false;
    char mSurrogate = '\u0000';
    int mCharCount = 0;
    int mByteCount = 0;

    public UTF8Reader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
    }

    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

    /*
     * Unable to fully structure code
     */
    public int read(char[] cbuf, int start, int len) throws IOException {
        if (start < 0 || start + len > cbuf.length) {
            this.reportBounds(cbuf, start, len);
        }
        if (this.mByteBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return 0;
        }
        len += start;
        outPtr = start;
        if (this.mSurrogate != '\u0000') {
            cbuf[outPtr++] = this.mSurrogate;
            this.mSurrogate = '\u0000';
        } else {
            left = this.mByteBufferEnd - this.mBytePtr;
            if (!(left >= 4 || left >= 1 && this.mByteBuffer[this.mBytePtr] >= 0 || this.loadMore(left))) {
                return -1;
            }
        }
        buf = this.mByteBuffer;
        inPtr = this.mBytePtr;
        inBufLen = this.mByteBufferEnd;
        block0: while (outPtr < len) {
            if ((c = buf[inPtr++]) < 0) ** GOTO lbl41
            if (c == 127 && this.mXml11) {
                bytePos = this.mByteCount + inPtr - 1;
                charPos = this.mCharCount + (outPtr - start);
                this.reportInvalidXml11(c, bytePos, charPos);
            }
            cbuf[outPtr++] = (char)c;
            inMax = inBufLen - inPtr;
            outMax = len - outPtr;
            inEnd = inPtr + (inMax < outMax ? inMax : outMax);
            while (inPtr < inEnd) {
                if ((c = buf[inPtr++] & 255) < 127) {
                    cbuf[outPtr++] = (char)c;
                    continue;
                }
                if (c == 127) {
                    if (this.mXml11) {
                        bytePos = this.mByteCount + inPtr - 1;
                        charPos = this.mCharCount + (outPtr - start);
                        this.reportInvalidXml11(c, bytePos, charPos);
                    }
                    cbuf[outPtr++] = (char)c;
                    if (inPtr < inEnd) continue block0;
                    break block0;
                }
lbl41:
                // 3 sources

                if ((c & 224) == 192) {
                    c &= 31;
                    needed = 1;
                } else if ((c & 240) == 224) {
                    c &= 15;
                    needed = 2;
                } else if ((c & 248) == 240) {
                    c &= 15;
                    needed = 3;
                } else {
                    this.reportInvalidInitial(c & 255, outPtr - start);
                    needed = 1;
                }
                if (inBufLen - inPtr < needed) {
                    --inPtr;
                    break block0;
                }
                if (((d = buf[inPtr++]) & 192) != 128) {
                    this.reportInvalidOther(d & 255, outPtr - start);
                }
                c = c << 6 | d & 63;
                if (needed > 1) {
                    if (((d = buf[inPtr++]) & 192) != 128) {
                        this.reportInvalidOther(d & 255, outPtr - start);
                    }
                    c = c << 6 | d & 63;
                    if (needed > 2) {
                        if (((d = buf[inPtr++]) & 192) != 128) {
                            this.reportInvalidOther(d & 255, outPtr - start);
                        }
                        if ((c = c << 6 | d & 63) > 0x10FFFF) {
                            this.reportInvalid(c, outPtr - start, "(above " + Integer.toHexString(0x10FFFF) + ") ");
                        }
                        cbuf[outPtr++] = (char)(55296 + ((c -= 65536) >> 10));
                        c = 56320 | c & 1023;
                        if (outPtr >= len) {
                            this.mSurrogate = (char)c;
                            break block0;
                        }
                    } else if (c >= 55296) {
                        if (c < 57344) {
                            this.reportInvalid(c, outPtr - start, "(a surrogate character) ");
                        } else if (c >= 65534) {
                            this.reportInvalid(c, outPtr - start, "");
                        }
                    } else if (this.mXml11 && c == 8232) {
                        if (outPtr > start && cbuf[outPtr - 1] == '\r') {
                            cbuf[outPtr - 1] = 10;
                        }
                        c = 10;
                    }
                } else if (this.mXml11 && c <= 159) {
                    if (c == 133) {
                        c = 10;
                    } else if (c >= 127) {
                        bytePos = this.mByteCount + inPtr - 1;
                        charPos = this.mCharCount + (outPtr - start);
                        this.reportInvalidXml11(c, bytePos, charPos);
                    }
                }
                cbuf[outPtr++] = (char)c;
                if (inPtr < inBufLen) continue block0;
                break block0;
            }
            break block0;
        }
        this.mBytePtr = inPtr;
        len = outPtr - start;
        this.mCharCount += len;
        return len;
    }

    private void reportInvalidInitial(int mask, int offset) throws IOException {
        int bytePos = this.mByteCount + this.mBytePtr - 1;
        int charPos = this.mCharCount + offset + 1;
        throw new CharConversionException("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalidOther(int mask, int offset) throws IOException {
        int bytePos = this.mByteCount + this.mBytePtr - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this.mByteCount + gotBytes;
        int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a multi-byte char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = this.mByteCount + this.mBytePtr - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
        int needed;
        byte c;
        this.mByteCount += this.mByteBufferEnd - available;
        if (available > 0) {
            if (this.mBytePtr > 0 && this.canModifyBuffer()) {
                for (int i = 0; i < available; ++i) {
                    this.mByteBuffer[i] = this.mByteBuffer[this.mBytePtr + i];
                }
                this.mBytePtr = 0;
                this.mByteBufferEnd = available;
            }
        } else {
            int count = this.readBytes();
            if (count < 1) {
                if (count < 0) {
                    this.freeBuffers();
                    return false;
                }
                this.reportStrangeStream();
            }
        }
        if ((c = this.mByteBuffer[this.mBytePtr]) >= 0) {
            return true;
        }
        if ((c & 0xE0) == 192) {
            needed = 2;
        } else if ((c & 0xF0) == 224) {
            needed = 3;
        } else if ((c & 0xF8) == 240) {
            needed = 4;
        } else {
            this.reportInvalidInitial(c & 0xFF, 0);
            needed = 1;
        }
        while (this.mBytePtr + needed > this.mByteBufferEnd) {
            int count = this.readBytesAt(this.mByteBufferEnd);
            if (count >= 1) continue;
            if (count < 0) {
                this.freeBuffers();
                this.reportUnexpectedEOF(this.mByteBufferEnd, needed);
            }
            this.reportStrangeStream();
        }
        return true;
    }
}

