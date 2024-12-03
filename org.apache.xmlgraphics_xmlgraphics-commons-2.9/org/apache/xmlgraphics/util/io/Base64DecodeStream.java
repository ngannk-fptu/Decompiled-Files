/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.io;

import java.io.IOException;
import java.io.InputStream;

public class Base64DecodeStream
extends InputStream {
    InputStream src;
    private static final byte[] PEM_ARRAY;
    byte[] decodeBuffer = new byte[4];
    byte[] outBuffer = new byte[3];
    int outOffset = 3;
    boolean eof;

    public Base64DecodeStream(InputStream src) {
        this.src = src;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void close() throws IOException {
        this.eof = true;
    }

    @Override
    public int available() throws IOException {
        return 3 - this.outOffset;
    }

    @Override
    public int read() throws IOException {
        if (this.outOffset == 3 && (this.eof || this.getNextAtom())) {
            this.eof = true;
            return -1;
        }
        return this.outBuffer[this.outOffset++] & 0xFF;
    }

    @Override
    public int read(byte[] out, int offset, int len) throws IOException {
        int idx;
        for (idx = 0; idx < len; ++idx) {
            if (this.outOffset == 3 && (this.eof || this.getNextAtom())) {
                this.eof = true;
                if (idx == 0) {
                    return -1;
                }
                return idx;
            }
            out[offset + idx] = this.outBuffer[this.outOffset++];
        }
        return idx;
    }

    final boolean getNextAtom() throws IOException {
        int off = 0;
        while (off != 4) {
            int count = this.src.read(this.decodeBuffer, off, 4 - off);
            if (count == -1) {
                return true;
            }
            int out = off;
            for (int in = off; in < off + count; ++in) {
                if (this.decodeBuffer[in] == 10 || this.decodeBuffer[in] == 13 || this.decodeBuffer[in] == 32) continue;
                this.decodeBuffer[out++] = this.decodeBuffer[in];
            }
            off = out;
        }
        byte a = PEM_ARRAY[this.decodeBuffer[0] & 0xFF];
        byte b = PEM_ARRAY[this.decodeBuffer[1] & 0xFF];
        byte c = PEM_ARRAY[this.decodeBuffer[2] & 0xFF];
        byte d = PEM_ARRAY[this.decodeBuffer[3] & 0xFF];
        this.outBuffer[0] = (byte)(a << 2 | b >>> 4);
        this.outBuffer[1] = (byte)(b << 4 | c >>> 2);
        this.outBuffer[2] = (byte)(c << 6 | d);
        if (this.decodeBuffer[3] != 61) {
            this.outOffset = 0;
        } else if (this.decodeBuffer[2] == 61) {
            this.outBuffer[2] = this.outBuffer[0];
            this.outOffset = 2;
            this.eof = true;
        } else {
            this.outBuffer[2] = this.outBuffer[1];
            this.outBuffer[1] = this.outBuffer[0];
            this.outOffset = 1;
            this.eof = true;
        }
        return false;
    }

    static {
        int c;
        PEM_ARRAY = new byte[256];
        for (int i = 0; i < PEM_ARRAY.length; ++i) {
            Base64DecodeStream.PEM_ARRAY[i] = -1;
        }
        int idx = 0;
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            Base64DecodeStream.PEM_ARRAY[c] = (byte)idx++;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            Base64DecodeStream.PEM_ARRAY[c] = (byte)idx++;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            Base64DecodeStream.PEM_ARRAY[c] = (byte)idx++;
        }
        Base64DecodeStream.PEM_ARRAY[43] = (byte)idx++;
        Base64DecodeStream.PEM_ARRAY[47] = (byte)idx++;
    }
}

