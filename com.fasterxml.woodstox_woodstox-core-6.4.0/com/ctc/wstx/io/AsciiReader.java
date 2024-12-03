/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BaseReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

public final class AsciiReader
extends BaseReader {
    boolean mXml11 = false;
    int mCharCount = 0;

    public AsciiReader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
    }

    @Override
    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

    @Override
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
        int avail = this.mByteBufferEnd - this.mBytePtr;
        if (avail <= 0) {
            this.mCharCount += this.mByteBufferEnd;
            int count = this.readBytes();
            if (count <= 0) {
                if (count == 0) {
                    this.reportStrangeStream();
                }
                this.freeBuffers();
                return -1;
            }
            avail = count;
        }
        if (len > avail) {
            len = avail;
        }
        int i = this.mBytePtr;
        int last = i + len;
        while (i < last) {
            char c;
            if ((c = (char)this.mByteBuffer[i++]) >= '\u007f') {
                if (c > '\u007f') {
                    this.reportInvalidAscii(c);
                } else if (this.mXml11) {
                    int pos = this.mCharCount + this.mBytePtr;
                    this.reportInvalidXml11(c, pos, pos);
                }
            }
            cbuf[start++] = c;
        }
        this.mBytePtr = last;
        return len;
    }

    private void reportInvalidAscii(char c) throws IOException {
        throw new CharConversionException("Invalid ascii byte; value above 7-bit ascii range (" + c + "; at pos #" + (this.mCharCount + this.mBytePtr) + ")");
    }
}

