/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoderStream
extends FilterOutputStream {
    private byte[] buffer;
    private int bufsize = 0;
    private boolean wrotePrefix = false;
    protected String name;
    protected int mode;

    public UUEncoderStream(OutputStream out) {
        this(out, "encoder.buf", 644);
    }

    public UUEncoderStream(OutputStream out, String name) {
        this(out, name, 644);
    }

    public UUEncoderStream(OutputStream out, String name, int mode) {
        super(out);
        this.name = name;
        this.mode = mode;
        this.buffer = new byte[45];
    }

    public void setNameMode(String name, int mode) {
        this.name = name;
        this.mode = mode;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        this.write(data, 0, data.length);
    }

    @Override
    public void write(int c) throws IOException {
        this.buffer[this.bufsize++] = (byte)c;
        if (this.bufsize == 45) {
            this.writePrefix();
            this.encode();
            this.bufsize = 0;
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.bufsize > 0) {
            this.writePrefix();
            this.encode();
        }
        this.writeSuffix();
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.out.close();
    }

    private void writePrefix() throws IOException {
        if (!this.wrotePrefix) {
            PrintStream ps = new PrintStream(this.out);
            ps.println("begin " + this.mode + " " + this.name);
            ps.flush();
            this.wrotePrefix = true;
        }
    }

    private void writeSuffix() throws IOException {
        PrintStream ps = new PrintStream(this.out);
        ps.println(" \nend");
        ps.flush();
    }

    private void encode() throws IOException {
        int i = 0;
        this.out.write((this.bufsize & 0x3F) + 32);
        while (i < this.bufsize) {
            byte c;
            byte b;
            byte a = this.buffer[i++];
            if (i < this.bufsize) {
                b = this.buffer[i++];
                c = i < this.bufsize ? this.buffer[i++] : (byte)1;
            } else {
                b = 1;
                c = 1;
            }
            int c1 = a >>> 2 & 0x3F;
            int c2 = a << 4 & 0x30 | b >>> 4 & 0xF;
            int c3 = b << 2 & 0x3C | c >>> 6 & 3;
            int c4 = c & 0x3F;
            this.out.write(c1 + 32);
            this.out.write(c2 + 32);
            this.out.write(c3 + 32);
            this.out.write(c4 + 32);
        }
        this.out.write(10);
    }
}

