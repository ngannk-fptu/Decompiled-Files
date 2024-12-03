/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.AbstractCachedSeekableStream;
import com.twelvemonkeys.lang.Validate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public final class FileCacheSeekableStream
extends AbstractCachedSeekableStream {
    private byte[] buffer = new byte[1024];

    public FileCacheSeekableStream(InputStream inputStream) throws IOException {
        this(inputStream, "iocache", null);
    }

    public FileCacheSeekableStream(InputStream inputStream, String string) throws IOException {
        this(inputStream, string, null);
    }

    public FileCacheSeekableStream(InputStream inputStream, String string, File file) throws IOException {
        this((InputStream)Validate.notNull((Object)inputStream, (String)"stream"), FileCacheSeekableStream.createTempFile(string, file));
    }

    static File createTempFile(String string, File file) throws IOException {
        Validate.notNull((Object)string, (String)"tempBaseName");
        File file2 = File.createTempFile(string, null, file);
        file2.deleteOnExit();
        return file2;
    }

    FileCacheSeekableStream(InputStream inputStream, File file) throws FileNotFoundException {
        super(inputStream, new FileCache(file));
    }

    @Override
    public final boolean isCachedMemory() {
        return false;
    }

    @Override
    public final boolean isCachedFile() {
        return true;
    }

    @Override
    protected void closeImpl() throws IOException {
        super.closeImpl();
        this.buffer = null;
    }

    @Override
    public int read() throws IOException {
        int n;
        this.checkOpen();
        if (this.position == this.streamPosition) {
            n = this.readAhead(this.buffer, 0, this.buffer.length);
            if (n >= 0) {
                n = this.buffer[0] & 0xFF;
            }
        } else {
            this.syncPosition();
            n = this.getCache().read();
        }
        if (n != -1) {
            ++this.position;
        }
        return n;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        this.checkOpen();
        if (this.position == this.streamPosition) {
            n3 = this.readAhead(byArray, n, n2);
        } else {
            this.syncPosition();
            n3 = this.getCache().read(byArray, n, (int)Math.min((long)n2, this.streamPosition - this.position));
        }
        if (n3 > 0) {
            this.position += (long)n3;
        }
        return n3;
    }

    private int readAhead(byte[] byArray, int n, int n2) throws IOException {
        int n3 = this.stream.read(byArray, n, n2);
        if (n3 > 0) {
            this.streamPosition += (long)n3;
            this.getCache().write(byArray, n, n3);
        }
        return n3;
    }

    static final class FileCache
    extends AbstractCachedSeekableStream.StreamCache {
        private RandomAccessFile cacheFile;

        public FileCache(File file) throws FileNotFoundException {
            Validate.notNull((Object)file, (String)"file");
            this.cacheFile = new RandomAccessFile(file, "rw");
        }

        @Override
        public void write(int n) throws IOException {
            this.cacheFile.write(n);
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this.cacheFile.write(byArray, n, n2);
        }

        @Override
        public int read() throws IOException {
            return this.cacheFile.read();
        }

        @Override
        public int read(byte[] byArray, int n, int n2) throws IOException {
            return this.cacheFile.read(byArray, n, n2);
        }

        @Override
        public void seek(long l) throws IOException {
            this.cacheFile.seek(l);
        }

        @Override
        public long getPosition() throws IOException {
            return this.cacheFile.getFilePointer();
        }

        @Override
        void close() throws IOException {
            this.cacheFile.close();
        }
    }
}

