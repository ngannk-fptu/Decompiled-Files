/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.JaiI18N;
import com.sun.media.jai.codec.SeekableStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileSeekableStream
extends SeekableStream {
    private RandomAccessFile file;
    private long markPos = -1L;
    private static final int PAGE_SHIFT = 9;
    private static final int PAGE_SIZE = 512;
    private static final int PAGE_MASK = 511;
    private static final int NUM_PAGES = 32;
    private static final int READ_CACHE_LIMIT = 512;
    private byte[][] pageBuf = new byte[512][32];
    private int[] currentPage = new int[32];
    private long length = 0L;
    private long pointer = 0L;

    public FileSeekableStream(RandomAccessFile file) throws IOException {
        this.file = file;
        file.seek(0L);
        this.length = file.length();
        for (int i = 0; i < 32; ++i) {
            this.pageBuf[i] = new byte[512];
            this.currentPage[i] = -1;
        }
    }

    public FileSeekableStream(File file) throws IOException {
        this(new RandomAccessFile(file, "r"));
    }

    public FileSeekableStream(String name) throws IOException {
        this(new RandomAccessFile(name, "r"));
    }

    public final boolean canSeekBackwards() {
        return true;
    }

    public final long getFilePointer() throws IOException {
        return this.pointer;
    }

    public final void seek(long pos) throws IOException {
        if (pos < 0L) {
            throw new IOException(JaiI18N.getString("FileSeekableStream0"));
        }
        this.pointer = pos;
    }

    public final int skip(int n) throws IOException {
        this.pointer += (long)n;
        return n;
    }

    private byte[] readPage(long pointer) throws IOException {
        int page = (int)(pointer >> 9);
        for (int i = 0; i < 32; ++i) {
            if (this.currentPage[i] != page) continue;
            return this.pageBuf[i];
        }
        int index = (int)(Math.random() * 32.0);
        this.currentPage[index] = page;
        long pos = (long)page << 9;
        long remaining = this.length - pos;
        int len = 512L < remaining ? 512 : (int)remaining;
        this.file.seek(pos);
        this.file.readFully(this.pageBuf[index], 0, len);
        return this.pageBuf[index];
    }

    public final int read() throws IOException {
        if (this.pointer >= this.length) {
            return -1;
        }
        byte[] buf = this.readPage(this.pointer);
        return buf[(int)(this.pointer++ & 0x1FFL)] & 0xFF;
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if ((len = (int)Math.min((long)len, this.length - this.pointer)) <= 0) {
            return -1;
        }
        if (len > 512) {
            this.file.seek(this.pointer);
            int nbytes = this.file.read(b, off, len);
            this.pointer += (long)nbytes;
            return nbytes;
        }
        byte[] buf = this.readPage(this.pointer);
        int remaining = 512 - (int)(this.pointer & 0x1FFL);
        int newLen = len < remaining ? len : remaining;
        System.arraycopy(buf, (int)(this.pointer & 0x1FFL), b, off, newLen);
        this.pointer += (long)newLen;
        return newLen;
    }

    public final void close() throws IOException {
        this.file.close();
    }

    public final synchronized void mark(int readLimit) {
        this.markPos = this.pointer;
    }

    public final synchronized void reset() throws IOException {
        if (this.markPos != -1L) {
            this.pointer = this.markPos;
        }
    }

    public boolean markSupported() {
        return true;
    }
}

