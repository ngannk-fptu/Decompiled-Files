/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BASE64EncoderStream
extends FilterOutputStream {
    private byte[] buffer = new byte[3];
    private int bufsize = 0;
    private int count = 0;
    private int bytesPerLine;
    private static final char[] pem_array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public BASE64EncoderStream(OutputStream out, int bytesPerLine) {
        super(out);
        this.bytesPerLine = bytesPerLine;
    }

    public BASE64EncoderStream(OutputStream out) {
        this(out, 76);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(int c) throws IOException {
        this.buffer[this.bufsize++] = (byte)c;
        if (this.bufsize == 3) {
            this.encode();
            this.bufsize = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.bufsize > 0) {
            this.encode();
            this.bufsize = 0;
        }
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.out.close();
    }

    private void encode() throws IOException {
        if (this.count + 4 > this.bytesPerLine) {
            this.out.write(13);
            this.out.write(10);
            this.count = 0;
        }
        if (this.bufsize == 1) {
            byte a = this.buffer[0];
            int b = 0;
            boolean c = false;
            this.out.write(pem_array[a >>> 2 & 0x3F]);
            this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(61);
            this.out.write(61);
        } else if (this.bufsize == 2) {
            byte a = this.buffer[0];
            byte b = this.buffer[1];
            int c = 0;
            this.out.write(pem_array[a >>> 2 & 0x3F]);
            this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)]);
            this.out.write(61);
        } else {
            byte a = this.buffer[0];
            byte b = this.buffer[1];
            byte c = this.buffer[2];
            this.out.write(pem_array[a >>> 2 & 0x3F]);
            this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)]);
            this.out.write(pem_array[c & 0x3F]);
        }
        this.count += 4;
    }

    public static byte[] encode(byte[] inbuf) {
        if (inbuf.length == 0) {
            return inbuf;
        }
        byte[] outbuf = new byte[(inbuf.length + 2) / 3 * 4];
        int inpos = 0;
        int outpos = 0;
        for (int size = inbuf.length; size > 0; size -= 3) {
            byte c;
            byte b;
            byte a;
            if (size == 1) {
                a = inbuf[inpos++];
                b = 0;
                c = 0;
                outbuf[outpos++] = (byte)pem_array[a >>> 2 & 0x3F];
                outbuf[outpos++] = (byte)pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
                outbuf[outpos++] = 61;
                outbuf[outpos++] = 61;
                continue;
            }
            if (size == 2) {
                a = inbuf[inpos++];
                b = inbuf[inpos++];
                c = 0;
                outbuf[outpos++] = (byte)pem_array[a >>> 2 & 0x3F];
                outbuf[outpos++] = (byte)pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
                outbuf[outpos++] = (byte)pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)];
                outbuf[outpos++] = 61;
                continue;
            }
            a = inbuf[inpos++];
            b = inbuf[inpos++];
            c = inbuf[inpos++];
            outbuf[outpos++] = (byte)pem_array[a >>> 2 & 0x3F];
            outbuf[outpos++] = (byte)pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            outbuf[outpos++] = (byte)pem_array[(b << 2 & 0x3C) + (c >>> 6 & 3)];
            outbuf[outpos++] = (byte)pem_array[c & 0x3F];
        }
        return outbuf;
    }
}

