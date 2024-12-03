/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

final class ASCII85OutputStream
extends FilterOutputStream {
    private int lineBreak = 72;
    private int count = 0;
    private byte[] indata = new byte[4];
    private byte[] outdata = new byte[5];
    private int maxline = 72;
    private boolean flushed = true;
    private char terminator = (char)126;
    private static final char OFFSET = '!';
    private static final char NEWLINE = '\n';
    private static final char Z = 'z';

    ASCII85OutputStream(OutputStream out) {
        super(out);
    }

    public void setTerminator(char term) {
        if (term < 'v' || term > '~' || term == 'z') {
            throw new IllegalArgumentException("Terminator must be 118-126 excluding z");
        }
        this.terminator = term;
    }

    public char getTerminator() {
        return this.terminator;
    }

    public void setLineLength(int l) {
        if (this.lineBreak > l) {
            this.lineBreak = l;
        }
        this.maxline = l;
    }

    public int getLineLength() {
        return this.maxline;
    }

    private void transformASCII85() {
        long word = (long)((this.indata[0] << 8 | this.indata[1] & 0xFF) << 16 | (this.indata[2] & 0xFF) << 8 | this.indata[3] & 0xFF) & 0xFFFFFFFFL;
        if (word == 0L) {
            this.outdata[0] = 122;
            this.outdata[1] = 0;
            return;
        }
        long x = word / 52200625L;
        this.outdata[0] = (byte)(x + 33L);
        word -= x * 85L * 85L * 85L * 85L;
        x = word / 614125L;
        this.outdata[1] = (byte)(x + 33L);
        word -= x * 85L * 85L * 85L;
        x = word / 7225L;
        this.outdata[2] = (byte)(x + 33L);
        word -= x * 85L * 85L;
        x = word / 85L;
        this.outdata[3] = (byte)(x + 33L);
        this.outdata[4] = (byte)(word % 85L + 33L);
    }

    @Override
    public void write(int b) throws IOException {
        this.flushed = false;
        this.indata[this.count++] = (byte)b;
        if (this.count < 4) {
            return;
        }
        this.transformASCII85();
        for (int i = 0; i < 5 && this.outdata[i] != 0; ++i) {
            this.out.write(this.outdata[i]);
            if (--this.lineBreak != 0) continue;
            this.out.write(10);
            this.lineBreak = this.maxline;
        }
        this.count = 0;
    }

    @Override
    public void flush() throws IOException {
        if (this.flushed) {
            return;
        }
        if (this.count > 0) {
            int i;
            for (i = this.count; i < 4; ++i) {
                this.indata[i] = 0;
            }
            this.transformASCII85();
            if (this.outdata[0] == 122) {
                for (i = 0; i < 5; ++i) {
                    this.outdata[i] = 33;
                }
            }
            for (i = 0; i < this.count + 1; ++i) {
                this.out.write(this.outdata[i]);
                if (--this.lineBreak != 0) continue;
                this.out.write(10);
                this.lineBreak = this.maxline;
            }
        }
        if (--this.lineBreak == 0) {
            this.out.write(10);
        }
        this.out.write(this.terminator);
        this.out.write(62);
        this.out.write(10);
        this.count = 0;
        this.lineBreak = this.maxline;
        this.flushed = true;
        super.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            this.flush();
            super.close();
        }
        finally {
            this.outdata = null;
            this.indata = null;
        }
    }
}

