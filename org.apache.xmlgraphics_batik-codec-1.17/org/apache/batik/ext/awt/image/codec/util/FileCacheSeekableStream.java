/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;
import org.apache.batik.ext.awt.image.codec.util.SeekableStream;

public final class FileCacheSeekableStream
extends SeekableStream {
    private InputStream stream;
    private File cacheFile;
    private RandomAccessFile cache;
    private int bufLen = 1024;
    private byte[] buf = new byte[this.bufLen];
    private long length = 0L;
    private long pointer = 0L;
    private boolean foundEOF = false;

    public FileCacheSeekableStream(InputStream stream) throws IOException {
        this.stream = stream;
        this.cacheFile = File.createTempFile("jai-FCSS-", ".tmp");
        this.cacheFile.deleteOnExit();
        this.cache = new RandomAccessFile(this.cacheFile, "rw");
    }

    private long readUntil(long pos) throws IOException {
        if (pos < this.length) {
            return pos;
        }
        if (this.foundEOF) {
            return this.length;
        }
        long len = pos - this.length;
        this.cache.seek(this.length);
        while (len > 0L) {
            int nbytes = this.stream.read(this.buf, 0, (int)Math.min(len, (long)this.bufLen));
            if (nbytes == -1) {
                this.foundEOF = true;
                return this.length;
            }
            this.cache.setLength(this.cache.length() + (long)nbytes);
            this.cache.write(this.buf, 0, nbytes);
            len -= (long)nbytes;
            this.length += (long)nbytes;
        }
        return pos;
    }

    @Override
    public boolean canSeekBackwards() {
        return true;
    }

    @Override
    public long getFilePointer() {
        return this.pointer;
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos < 0L) {
            throw new IOException(PropertyUtil.getString("FileCacheSeekableStream0"));
        }
        this.pointer = pos;
    }

    @Override
    public int read() throws IOException {
        long next = this.pointer + 1L;
        long pos = this.readUntil(next);
        if (pos >= next) {
            this.cache.seek(this.pointer++);
            return this.cache.read();
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        long pos = this.readUntil(this.pointer + (long)len);
        if ((len = (int)Math.min((long)len, pos - this.pointer)) > 0) {
            this.cache.seek(this.pointer);
            this.cache.readFully(b, off, len);
            this.pointer += (long)len;
            return len;
        }
        return -1;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.cache.close();
        this.cacheFile.delete();
    }
}

