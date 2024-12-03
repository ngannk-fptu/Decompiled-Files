/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.JaiI18N;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TempFileCleanupThread;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;

public final class FileCacheSeekableStream
extends SeekableStream {
    private static TempFileCleanupThread cleanupThread = null;
    private InputStream stream;
    private File cacheFile;
    private RandomAccessFile cache;
    private int bufLen = 1024;
    private byte[] buf = new byte[this.bufLen];
    private long length = 0L;
    private long pointer = 0L;
    private boolean foundEOF = false;
    static /* synthetic */ Class class$java$lang$Thread;
    static /* synthetic */ Class class$java$lang$Runtime;

    public FileCacheSeekableStream(InputStream stream) throws IOException {
        this.stream = stream;
        this.cacheFile = File.createTempFile("jai-FCSS-", ".tmp");
        this.cacheFile.deleteOnExit();
        this.cache = new RandomAccessFile(this.cacheFile, "rw");
        if (cleanupThread != null) {
            cleanupThread.addFile(this.cacheFile);
        }
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

    public boolean canSeekBackwards() {
        return true;
    }

    public long getFilePointer() {
        return this.pointer;
    }

    public void seek(long pos) throws IOException {
        if (pos < 0L) {
            throw new IOException(JaiI18N.getString("FileCacheSeekableStream0"));
        }
        this.pointer = pos;
    }

    public int read() throws IOException {
        long next = this.pointer + 1L;
        long pos = this.readUntil(next);
        if (pos >= next) {
            this.cache.seek(this.pointer++);
            return this.cache.read();
        }
        return -1;
    }

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

    public void close() throws IOException {
        super.close();
        this.cache.close();
        this.cacheFile.delete();
        if (cleanupThread != null) {
            cleanupThread.removeFile(this.cacheFile);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        try {
            Method shutdownMethod = (class$java$lang$Runtime == null ? (class$java$lang$Runtime = FileCacheSeekableStream.class$("java.lang.Runtime")) : class$java$lang$Runtime).getDeclaredMethod("addShutdownHook", class$java$lang$Thread == null ? (class$java$lang$Thread = FileCacheSeekableStream.class$("java.lang.Thread")) : class$java$lang$Thread);
            cleanupThread = new TempFileCleanupThread();
            shutdownMethod.invoke((Object)Runtime.getRuntime(), cleanupThread);
        }
        catch (Exception e) {
            cleanupThread = null;
        }
    }
}

