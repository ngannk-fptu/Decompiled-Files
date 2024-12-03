/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.Writer;
import org.apache.axiom.util.base64.Base64Constants;

public abstract class AbstractBase64DecodingWriter
extends Writer {
    private final char[] in = new char[4];
    private final byte[] out = new byte[3];
    private int rest;

    private static boolean isWhitespace(int c) {
        return c <= 32 && (c == 32 || c == 10 || c == 13 || c == 9);
    }

    public final void write(char[] cbuf, int off, int len) throws IOException {
        while (len > 0) {
            this.write(cbuf[off]);
            ++off;
            --len;
        }
    }

    public final void write(String str, int off, int len) throws IOException {
        while (len > 0) {
            this.write(str.charAt(off));
            ++off;
            --len;
        }
    }

    public final void write(int c) throws IOException {
        if (!AbstractBase64DecodingWriter.isWhitespace(c)) {
            this.in[this.rest++] = (char)c;
            if (this.rest == 4) {
                this.decode(this.in, 0);
                this.rest = 0;
            }
        }
    }

    private int decode(char c) throws IOException {
        byte result;
        if (c == '=') {
            return -1;
        }
        if (c < Base64Constants.S_DECODETABLE.length && (result = Base64Constants.S_DECODETABLE[c]) != 127) {
            return result;
        }
        throw new IOException("Invalid base64 char '" + c + "'");
    }

    private void decode(char[] data, int off) throws IOException {
        int outlen = 3;
        if (data[off + 3] == '=') {
            outlen = 2;
        }
        if (data[off + 2] == '=') {
            outlen = 1;
        }
        int b0 = this.decode(data[off]);
        int b1 = this.decode(data[off + 1]);
        int b2 = this.decode(data[off + 2]);
        int b3 = this.decode(data[off + 3]);
        switch (outlen) {
            case 1: {
                this.out[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                break;
            }
            case 2: {
                this.out[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                this.out[1] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                break;
            }
            case 3: {
                this.out[0] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                this.out[1] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                this.out[2] = (byte)(b2 << 6 & 0xC0 | b3 & 0x3F);
            }
        }
        this.doWrite(this.out, outlen);
    }

    protected abstract void doWrite(byte[] var1, int var2) throws IOException;
}

