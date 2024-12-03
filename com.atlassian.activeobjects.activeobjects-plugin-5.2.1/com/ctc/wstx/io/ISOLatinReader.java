/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BaseReader;
import java.io.IOException;
import java.io.InputStream;

public final class ISOLatinReader
extends BaseReader {
    boolean mXml11 = false;
    int mByteCount = 0;

    public ISOLatinReader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
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
        int avail = this.mByteBufferEnd - this.mBytePtr;
        if (avail <= 0) {
            this.mByteCount += this.mByteBufferEnd;
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
        if (this.mXml11) {
            while (i < last) {
                int c;
                if ((c = (int)(this.mByteBuffer[i++] & 0xFF)) >= 127 && c <= 159) {
                    if (c == 133) {
                        c = 10;
                    } else if (c >= 127) {
                        int pos = this.mByteCount + i;
                        this.reportInvalidXml11(c, pos, pos);
                    }
                }
                cbuf[start++] = c;
            }
        } else {
            while (i < last) {
                cbuf[start++] = (char)(this.mByteBuffer[i++] & 0xFF);
            }
        }
        this.mBytePtr = last;
        return len;
    }
}

