/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BaseReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

public final class UTF32Reader
extends BaseReader {
    final boolean mBigEndian;
    boolean mXml11;
    char mSurrogate = '\u0000';
    int mCharCount = 0;
    int mByteCount = 0;

    public UTF32Reader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer, boolean isBigEndian) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mBigEndian = isBigEndian;
    }

    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

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
        int outPtr = start;
        if (this.mSurrogate != '\u0000') {
            cbuf[outPtr++] = this.mSurrogate;
            this.mSurrogate = '\u0000';
        } else {
            int left = this.mByteBufferEnd - this.mBytePtr;
            if (left < 4 && !this.loadMore(left)) {
                return -1;
            }
        }
        byte[] buf = this.mByteBuffer;
        while (outPtr < len) {
            int ptr = this.mBytePtr;
            int ch = this.mBigEndian ? buf[ptr] << 24 | (buf[ptr + 1] & 0xFF) << 16 | (buf[ptr + 2] & 0xFF) << 8 | buf[ptr + 3] & 0xFF : buf[ptr] & 0xFF | (buf[ptr + 1] & 0xFF) << 8 | (buf[ptr + 2] & 0xFF) << 16 | buf[ptr + 3] << 24;
            this.mBytePtr += 4;
            if (ch >= 127) {
                if (ch <= 159) {
                    if (this.mXml11) {
                        if (ch != 133) {
                            this.reportInvalid(ch, outPtr - start, "(can only be included via entity in xml 1.1)");
                        }
                        ch = 10;
                    }
                } else if (ch >= 55296) {
                    if (ch > 0x10FFFF) {
                        this.reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(0x10FFFF) + ") ");
                    }
                    if (ch > 65535) {
                        cbuf[outPtr++] = (char)(55296 + ((ch -= 65536) >> 10));
                        ch = 0xDC00 | ch & 0x3FF;
                        if (outPtr >= len) {
                            this.mSurrogate = (char)ch;
                            break;
                        }
                    } else if (ch < 57344) {
                        this.reportInvalid(ch, outPtr - start, "(a surrogate char) ");
                    } else if (ch >= 65534) {
                        this.reportInvalid(ch, outPtr - start, "");
                    }
                } else if (ch == 8232 && this.mXml11) {
                    ch = 10;
                }
            }
            cbuf[outPtr++] = (char)ch;
            if (this.mBytePtr < this.mByteBufferEnd) continue;
            break;
        }
        len = outPtr - start;
        this.mCharCount += len;
        return len;
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this.mByteCount + gotBytes;
        int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = this.mByteCount + this.mBytePtr - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
        int count;
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
            count = this.readBytes();
            if (count < 1) {
                if (count < 0) {
                    this.freeBuffers();
                    return false;
                }
                this.reportStrangeStream();
            }
        }
        while (this.mByteBufferEnd < 4) {
            count = this.readBytesAt(this.mByteBufferEnd);
            if (count >= 1) continue;
            if (count < 0) {
                this.freeBuffers();
                this.reportUnexpectedEOF(this.mByteBufferEnd, 4);
            }
            this.reportStrangeStream();
        }
        return true;
    }
}

