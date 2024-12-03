/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.SeekableInputStream;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;

abstract class AbstractCachedSeekableStream
extends SeekableInputStream {
    protected final InputStream stream;
    protected long streamPosition;
    private StreamCache cache;

    protected AbstractCachedSeekableStream(InputStream inputStream, StreamCache streamCache) {
        Validate.notNull((Object)inputStream, (String)"stream");
        Validate.notNull((Object)streamCache, (String)"cache");
        this.stream = inputStream;
        this.cache = streamCache;
    }

    protected final StreamCache getCache() {
        return this.cache;
    }

    @Override
    public int available() throws IOException {
        long l = this.streamPosition - this.position + (long)this.stream.available();
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
    }

    @Override
    public int read() throws IOException {
        int n;
        this.checkOpen();
        if (this.position == this.streamPosition) {
            n = this.stream.read();
            if (n >= 0) {
                ++this.streamPosition;
                this.cache.write(n);
            }
        } else {
            this.syncPosition();
            n = this.cache.read();
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
            n3 = this.stream.read(byArray, n, n2);
            if (n3 > 0) {
                this.streamPosition += (long)n3;
                this.cache.write(byArray, n, n3);
            }
        } else {
            this.syncPosition();
            n3 = this.cache.read(byArray, n, n2);
        }
        if (n3 > 0) {
            this.position += (long)n3;
        }
        return n3;
    }

    protected final void syncPosition() throws IOException {
        if (this.cache.getPosition() != this.position) {
            this.cache.seek(this.position);
        }
    }

    @Override
    public final boolean isCached() {
        return true;
    }

    @Override
    public abstract boolean isCachedMemory();

    @Override
    public abstract boolean isCachedFile();

    @Override
    protected void seekImpl(long l) throws IOException {
        if (this.streamPosition < l) {
            long l2;
            if (this.cache.getPosition() != this.streamPosition) {
                this.cache.seek(this.streamPosition);
            }
            int n = (l2 = l - this.streamPosition) > 1024L ? 1024 : (int)l2;
            byte[] byArray = new byte[n];
            while (l2 > 0L) {
                int n2 = (long)byArray.length < l2 ? byArray.length : (int)l2;
                int n3 = this.stream.read(byArray, 0, n2);
                if (n3 > 0) {
                    this.cache.write(byArray, 0, n3);
                    this.streamPosition += (long)n3;
                    l2 -= (long)n3;
                    continue;
                }
                if (n3 >= 0) continue;
                break;
            }
        } else {
            this.cache.seek(l);
        }
    }

    @Override
    protected void flushBeforeImpl(long l) {
        this.cache.flush(l);
    }

    @Override
    protected void closeImpl() throws IOException {
        this.cache.close();
        this.cache = null;
        this.stream.close();
    }

    static abstract class StreamCache {
        StreamCache() {
        }

        abstract void write(int var1) throws IOException;

        void write(byte[] byArray, int n, int n2) throws IOException {
            for (int i = 0; i < n2; ++i) {
                this.write(byArray[n + i]);
            }
        }

        abstract int read() throws IOException;

        int read(byte[] byArray, int n, int n2) throws IOException {
            int n3;
            int n4 = 0;
            for (int i = 0; i < n2 && (n3 = this.read()) >= 0; ++i) {
                byArray[n + i] = (byte)n3;
                ++n4;
            }
            return n4;
        }

        abstract void seek(long var1) throws IOException;

        void flush(long l) {
        }

        abstract long getPosition() throws IOException;

        abstract void close() throws IOException;
    }
}

