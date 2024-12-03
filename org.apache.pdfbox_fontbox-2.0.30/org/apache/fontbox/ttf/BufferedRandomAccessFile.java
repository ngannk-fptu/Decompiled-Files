/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BufferedRandomAccessFile
extends RandomAccessFile {
    private final byte[] buffer;
    private int bufend = 0;
    private int bufpos = 0;
    private long realpos = 0L;

    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws FileNotFoundException {
        super(filename, mode);
        this.buffer = new byte[bufsize];
    }

    public BufferedRandomAccessFile(File file, String mode, int bufsize) throws FileNotFoundException {
        super(file, mode);
        this.buffer = new byte[bufsize];
    }

    @Override
    public final int read() throws IOException {
        if (this.bufpos >= this.bufend && this.fillBuffer() < 0) {
            return -1;
        }
        if (this.bufend == 0) {
            return -1;
        }
        return this.buffer[this.bufpos++] + 256 & 0xFF;
    }

    private int fillBuffer() throws IOException {
        int n = super.read(this.buffer);
        if (n >= 0) {
            this.realpos += (long)n;
            this.bufend = n;
            this.bufpos = 0;
        }
        return n;
    }

    private void invalidate() throws IOException {
        this.bufend = 0;
        this.bufpos = 0;
        this.realpos = super.getFilePointer();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int curLen = len;
        int curOff = off;
        int totalRead = 0;
        while (true) {
            int leftover;
            if (curLen <= (leftover = this.bufend - this.bufpos)) {
                System.arraycopy(this.buffer, this.bufpos, b, curOff, curLen);
                this.bufpos += curLen;
                return totalRead + curLen;
            }
            System.arraycopy(this.buffer, this.bufpos, b, curOff, leftover);
            totalRead += leftover;
            this.bufpos += leftover;
            if (this.fillBuffer() <= 0) break;
            curOff += leftover;
            curLen -= leftover;
        }
        if (totalRead == 0) {
            return -1;
        }
        return totalRead;
    }

    @Override
    public long getFilePointer() throws IOException {
        return this.realpos - (long)this.bufend + (long)this.bufpos;
    }

    @Override
    public void seek(long pos) throws IOException {
        int n = (int)(this.realpos - pos);
        if (n >= 0 && n <= this.bufend) {
            this.bufpos = this.bufend - n;
        } else {
            super.seek(pos);
            this.invalidate();
        }
    }
}

