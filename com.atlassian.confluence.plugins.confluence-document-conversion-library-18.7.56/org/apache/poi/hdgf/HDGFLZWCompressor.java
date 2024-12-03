/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class HDGFLZWCompressor {
    private final byte[] dict = new byte[4096];
    private final byte[] buffer = new byte[16];
    private int bufferLen;
    private final byte[] rawCode = new byte[18];
    private int rawCodeLen;
    private int posInp;
    private int posOut;
    private int nextMask;
    private int maskBitsSet;
    private final OutputStream res;

    public HDGFLZWCompressor(OutputStream res) {
        this.res = res;
    }

    private int findRawCodeInBuffer() {
        for (int i = this.rawCodeLen + 1; i < 4096; ++i) {
            int pos = this.posInp - i & 0xFFF;
            boolean matches = true;
            for (int j = 0; j < this.rawCodeLen; ++j) {
                if (this.dict[pos + j & 0xFFF] == this.rawCode[j]) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            return pos;
        }
        return -1;
    }

    private void outputCompressed() throws IOException {
        if (this.rawCodeLen < 3) {
            int rcl = this.rawCodeLen;
            for (int i = 0; i < rcl; ++i) {
                this.outputUncompressed(this.rawCode[i]);
            }
            return;
        }
        int codesAt = this.findRawCodeInBuffer();
        codesAt = codesAt - 18 & 0xFFF;
        ++this.maskBitsSet;
        int bp1 = codesAt & 0xFF;
        int bp2 = this.rawCodeLen - 3 + (codesAt - bp1 >>> 4);
        this.buffer[this.bufferLen++] = (byte)bp1;
        this.buffer[this.bufferLen++] = (byte)bp2;
        assert (this.maskBitsSet <= 8);
        if (this.maskBitsSet == 8) {
            this.output8Codes();
        }
        this.rawCodeLen = 0;
    }

    private void outputUncompressed(byte b) throws IOException {
        this.nextMask += 1 << this.maskBitsSet;
        ++this.maskBitsSet;
        this.buffer[this.bufferLen++] = b;
        if (this.maskBitsSet == 8) {
            this.output8Codes();
        }
        this.rawCodeLen = 0;
    }

    private void output8Codes() throws IOException {
        this.res.write(this.nextMask);
        this.res.write(this.buffer, 0, this.bufferLen);
        this.posOut += 1 + this.bufferLen;
        this.nextMask = 0;
        this.maskBitsSet = 0;
        this.bufferLen = 0;
    }

    public void compress(InputStream src) throws IOException {
        int dataI = -1;
        while (true) {
            if (dataI > -1) {
                this.dict[this.posInp++ & 0xFFF] = (byte)dataI;
            }
            if ((dataI = src.read()) == -1) {
                if (this.rawCodeLen <= 0) break;
                this.outputCompressed();
                if (this.maskBitsSet <= 0) break;
                this.output8Codes();
                break;
            }
            byte dataB = (byte)dataI;
            this.rawCode[this.rawCodeLen++] = dataB;
            int rawAt = this.findRawCodeInBuffer();
            if (rawAt > -1) {
                if (this.rawCodeLen != 18) continue;
                this.outputCompressed();
                continue;
            }
            --this.rawCodeLen;
            if (this.rawCodeLen > 0) {
                this.outputCompressed();
                this.rawCode[0] = dataB;
                this.rawCodeLen = 1;
                if (this.findRawCodeInBuffer() > -1) continue;
                this.outputUncompressed(dataB);
                continue;
            }
            this.outputUncompressed(dataB);
        }
    }
}

