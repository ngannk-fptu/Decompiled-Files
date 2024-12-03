/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.io.CompletelyCloseable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public final class UTF8Writer
extends Writer
implements CompletelyCloseable {
    private static final int DEFAULT_BUF_LEN = 4000;
    static final int SURR1_FIRST = 55296;
    static final int SURR1_LAST = 56319;
    static final int SURR2_FIRST = 56320;
    static final int SURR2_LAST = 57343;
    final WriterConfig mConfig;
    final boolean mAutoCloseOutput;
    final OutputStream mOut;
    byte[] mOutBuffer;
    final int mOutBufferLast;
    int mOutPtr;
    int mSurrogate = 0;

    public UTF8Writer(WriterConfig cfg, OutputStream out, boolean autoclose) {
        this.mConfig = cfg;
        this.mAutoCloseOutput = autoclose;
        this.mOut = out;
        this.mOutBuffer = this.mConfig == null ? new byte[4000] : cfg.allocFullBBuffer(4000);
        this.mOutBufferLast = this.mOutBuffer.length - 4;
        this.mOutPtr = 0;
    }

    @Override
    public void closeCompletely() throws IOException {
        this._close(true);
    }

    @Override
    public void close() throws IOException {
        this._close(this.mAutoCloseOutput);
    }

    @Override
    public void flush() throws IOException {
        if (this.mOutPtr > 0 && this.mOutBuffer != null) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        this.mOut.flush();
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                this.write(cbuf[off]);
            }
            return;
        }
        if (this.mSurrogate > 0) {
            second = cbuf[off++];
            --len;
            this.write(this._convertSurrogate(second));
        }
        outPtr = this.mOutPtr;
        outBuf = this.mOutBuffer;
        outBufLast = this.mOutBufferLast;
        len += off;
        block0: while (off < len) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            if ((c = cbuf[off++]) >= 128) ** GOTO lbl28
            outBuf[outPtr++] = (byte)c;
            maxInCount = len - off;
            maxOutCount = outBufLast - outPtr;
            if (maxInCount > maxOutCount) {
                maxInCount = maxOutCount;
            }
            maxInCount += off;
            while (off < maxInCount) {
                if ((c = cbuf[off++]) < 128) {
                    outBuf[outPtr++] = (byte)c;
                    continue;
                }
lbl28:
                // 3 sources

                if (c < 2048) {
                    outBuf[outPtr++] = (byte)(192 | c >> 6);
                    outBuf[outPtr++] = (byte)(128 | c & 63);
                    continue block0;
                }
                if (c < 55296 || c > 57343) {
                    outBuf[outPtr++] = (byte)(224 | c >> 12);
                    outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
                    outBuf[outPtr++] = (byte)(128 | c & 63);
                    continue block0;
                }
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len) break block0;
                if ((c = this._convertSurrogate(cbuf[off++])) > 0x10FFFF) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                outBuf[outPtr++] = (byte)(240 | c >> 18);
                outBuf[outPtr++] = (byte)(128 | c >> 12 & 63);
                outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
                outBuf[outPtr++] = (byte)(128 | c & 63);
                continue block0;
            }
        }
        this.mOutPtr = outPtr;
    }

    @Override
    public void write(int c) throws IOException {
        if (this.mSurrogate > 0) {
            c = this._convertSurrogate(c);
        } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
                this.throwIllegal(c);
            }
            this.mSurrogate = c;
            return;
        }
        if (this.mOutPtr >= this.mOutBufferLast) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        if (c < 128) {
            this.mOutBuffer[this.mOutPtr++] = (byte)c;
        } else {
            int ptr = this.mOutPtr;
            if (c < 2048) {
                this.mOutBuffer[ptr++] = (byte)(0xC0 | c >> 6);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
            } else if (c <= 65535) {
                this.mOutBuffer[ptr++] = (byte)(0xE0 | c >> 12);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c >> 6 & 0x3F);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
            } else {
                if (c > 0x10FFFF) {
                    this.throwIllegal(c);
                }
                this.mOutBuffer[ptr++] = (byte)(0xF0 | c >> 18);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c >> 12 & 0x3F);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c >> 6 & 0x3F);
                this.mOutBuffer[ptr++] = (byte)(0x80 | c & 0x3F);
            }
            this.mOutPtr = ptr;
        }
    }

    @Override
    public void write(String str) throws IOException {
        this.write(str, 0, str.length());
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                this.write(str.charAt(off));
            }
            return;
        }
        if (this.mSurrogate > 0) {
            second = str.charAt(off++);
            --len;
            this.write(this._convertSurrogate(second));
        }
        outPtr = this.mOutPtr;
        outBuf = this.mOutBuffer;
        outBufLast = this.mOutBufferLast;
        len += off;
        block0: while (off < len) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            if ((c = str.charAt(off++)) >= 128) ** GOTO lbl28
            outBuf[outPtr++] = (byte)c;
            maxInCount = len - off;
            maxOutCount = outBufLast - outPtr;
            if (maxInCount > maxOutCount) {
                maxInCount = maxOutCount;
            }
            maxInCount += off;
            while (off < maxInCount) {
                if ((c = (int)str.charAt(off++)) < 128) {
                    outBuf[outPtr++] = (byte)c;
                    continue;
                }
lbl28:
                // 3 sources

                if (c < 2048) {
                    outBuf[outPtr++] = (byte)(192 | c >> 6);
                    outBuf[outPtr++] = (byte)(128 | c & 63);
                    continue block0;
                }
                if (c < 55296 || c > 57343) {
                    outBuf[outPtr++] = (byte)(224 | c >> 12);
                    outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
                    outBuf[outPtr++] = (byte)(128 | c & 63);
                    continue block0;
                }
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len) break block0;
                if ((c = this._convertSurrogate(str.charAt(off++))) > 0x10FFFF) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                outBuf[outPtr++] = (byte)(240 | c >> 18);
                outBuf[outPtr++] = (byte)(128 | c >> 12 & 63);
                outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
                outBuf[outPtr++] = (byte)(128 | c & 63);
                continue block0;
            }
        }
        this.mOutPtr = outPtr;
    }

    private final void _close(boolean forceClosing) throws IOException {
        int code;
        byte[] buf = this.mOutBuffer;
        if (buf != null) {
            this.mOutBuffer = null;
            if (this.mOutPtr > 0) {
                this.mOut.write(buf, 0, this.mOutPtr);
                this.mOutPtr = 0;
            }
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(buf);
            }
        }
        if (forceClosing) {
            this.mOut.close();
        }
        if ((code = this.mSurrogate) > 0) {
            this.mSurrogate = 0;
            this.throwIllegal(code);
        }
    }

    private final int _convertSurrogate(int secondPart) throws IOException {
        int firstPart = this.mSurrogate;
        this.mSurrogate = 0;
        if (secondPart < 56320 || secondPart > 57343) {
            throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + (firstPart - 55296 << 10) + (secondPart - 56320);
    }

    private void throwIllegal(int code) throws IOException {
        if (code > 0x10FFFF) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 3629");
        }
        if (code >= 55296) {
            if (code <= 56319) {
                throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}

