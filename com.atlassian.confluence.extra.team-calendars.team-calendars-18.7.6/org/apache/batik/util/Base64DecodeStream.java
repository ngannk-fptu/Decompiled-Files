/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;

public class Base64DecodeStream
extends InputStream {
    InputStream src;
    private static final byte[] pem_array;
    byte[] decode_buffer = new byte[4];
    byte[] out_buffer = new byte[3];
    int out_offset = 3;
    boolean EOF = false;

    public Base64DecodeStream(InputStream src) {
        this.src = src;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void close() throws IOException {
        this.EOF = true;
    }

    @Override
    public int available() throws IOException {
        return 3 - this.out_offset;
    }

    @Override
    public int read() throws IOException {
        if (this.out_offset == 3 && (this.EOF || this.getNextAtom())) {
            this.EOF = true;
            return -1;
        }
        return this.out_buffer[this.out_offset++] & 0xFF;
    }

    @Override
    public int read(byte[] out, int offset, int len) throws IOException {
        int idx;
        for (idx = 0; idx < len; ++idx) {
            if (this.out_offset == 3 && (this.EOF || this.getNextAtom())) {
                this.EOF = true;
                if (idx == 0) {
                    return -1;
                }
                return idx;
            }
            out[offset + idx] = this.out_buffer[this.out_offset++];
        }
        return idx;
    }

    final boolean getNextAtom() throws IOException {
        int off = 0;
        while (off != 4) {
            int count = this.src.read(this.decode_buffer, off, 4 - off);
            if (count == -1) {
                return true;
            }
            int out = off;
            for (int in = off; in < off + count; ++in) {
                if (this.decode_buffer[in] == 10 || this.decode_buffer[in] == 13 || this.decode_buffer[in] == 32) continue;
                this.decode_buffer[out++] = this.decode_buffer[in];
            }
            off = out;
        }
        byte a = pem_array[this.decode_buffer[0] & 0xFF];
        byte b = pem_array[this.decode_buffer[1] & 0xFF];
        byte c = pem_array[this.decode_buffer[2] & 0xFF];
        byte d = pem_array[this.decode_buffer[3] & 0xFF];
        this.out_buffer[0] = (byte)(a << 2 | b >>> 4);
        this.out_buffer[1] = (byte)(b << 4 | c >>> 2);
        this.out_buffer[2] = (byte)(c << 6 | d);
        if (this.decode_buffer[3] != 61) {
            this.out_offset = 0;
        } else if (this.decode_buffer[2] == 61) {
            this.out_buffer[2] = this.out_buffer[0];
            this.out_offset = 2;
            this.EOF = true;
        } else {
            this.out_buffer[2] = this.out_buffer[1];
            this.out_buffer[1] = this.out_buffer[0];
            this.out_offset = 1;
            this.EOF = true;
        }
        return false;
    }

    static {
        int c;
        pem_array = new byte[256];
        for (int i = 0; i < pem_array.length; ++i) {
            Base64DecodeStream.pem_array[i] = -1;
        }
        int idx = 0;
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            Base64DecodeStream.pem_array[c] = (byte)idx++;
        }
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            Base64DecodeStream.pem_array[c] = (byte)idx++;
        }
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            Base64DecodeStream.pem_array[c] = (byte)idx++;
        }
        Base64DecodeStream.pem_array[43] = (byte)idx++;
        Base64DecodeStream.pem_array[47] = (byte)idx++;
    }
}

