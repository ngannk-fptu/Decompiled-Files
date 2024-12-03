/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.SeekableInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileSeekableStream
extends SeekableInputStream {
    final RandomAccessFile mRandomAccess;

    public FileSeekableStream(File file) throws FileNotFoundException {
        this(new RandomAccessFile(file, "r"));
    }

    public FileSeekableStream(RandomAccessFile randomAccessFile) {
        this.mRandomAccess = randomAccessFile;
    }

    @Override
    public boolean isCached() {
        return false;
    }

    @Override
    public boolean isCachedFile() {
        return false;
    }

    @Override
    public boolean isCachedMemory() {
        return false;
    }

    @Override
    public int available() throws IOException {
        long l = this.mRandomAccess.length() - this.position;
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
    }

    @Override
    public void closeImpl() throws IOException {
        this.mRandomAccess.close();
    }

    @Override
    public int read() throws IOException {
        this.checkOpen();
        int n = this.mRandomAccess.read();
        if (n >= 0) {
            ++this.position;
        }
        return n;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        this.checkOpen();
        int n3 = this.mRandomAccess.read(byArray, n, n2);
        if (n3 > 0) {
            this.position += (long)n3;
        }
        return n3;
    }

    @Override
    protected void flushBeforeImpl(long l) {
    }

    @Override
    protected void seekImpl(long l) throws IOException {
        this.mRandomAccess.seek(l);
    }
}

