/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import org.bouncycastle.util.encoders.Translator;

public class BufferedDecoder {
    protected byte[] buf;
    protected int bufOff;
    protected Translator translator;

    public BufferedDecoder(Translator translator, int bufSize) {
        this.translator = translator;
        if (bufSize % translator.getEncodedBlockSize() != 0) {
            throw new IllegalArgumentException("buffer size not multiple of input block size");
        }
        this.buf = new byte[bufSize];
        this.bufOff = 0;
    }

    public int processByte(byte in, byte[] out, int outOff) {
        int resultLen = 0;
        this.buf[this.bufOff++] = in;
        if (this.bufOff == this.buf.length) {
            resultLen = this.translator.decode(this.buf, 0, this.buf.length, out, outOff);
            this.bufOff = 0;
        }
        return resultLen;
    }

    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen += this.translator.decode(this.buf, 0, this.buf.length, out, outOff);
            this.bufOff = 0;
            outOff += resultLen;
            int chunkSize = (len -= gapLen) - len % this.buf.length;
            resultLen += this.translator.decode(in, inOff += gapLen, chunkSize, out, outOff);
            len -= chunkSize;
            inOff += chunkSize;
        }
        if (len != 0) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, len);
            this.bufOff += len;
        }
        return resultLen;
    }
}

