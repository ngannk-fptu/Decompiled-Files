/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.AbstractCachedSeekableStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class MemoryCacheSeekableStream
extends AbstractCachedSeekableStream {
    public MemoryCacheSeekableStream(InputStream inputStream) {
        super(inputStream, new MemoryCache());
    }

    @Override
    public final boolean isCachedMemory() {
        return true;
    }

    @Override
    public final boolean isCachedFile() {
        return false;
    }

    static final class MemoryCache
    extends AbstractCachedSeekableStream.StreamCache {
        static final int BLOCK_SIZE = 8192;
        private final List<byte[]> cache = new ArrayList<byte[]>();
        private long length;
        private long position;
        private long start;

        MemoryCache() {
        }

        private byte[] getBlock() throws IOException {
            long l = this.position - this.start;
            if (l < 0L) {
                throw new IOException("StreamCache flushed before read position");
            }
            long l2 = l / 8192L;
            if (l2 >= Integer.MAX_VALUE) {
                throw new IOException("Memory cache max size exceeded");
            }
            if (l2 >= (long)this.cache.size()) {
                try {
                    this.cache.add(new byte[8192]);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    throw new IOException("No more memory for cache: " + this.cache.size() * 8192);
                }
            }
            return this.cache.get((int)l2);
        }

        @Override
        public void write(int n) throws IOException {
            byte[] byArray = this.getBlock();
            int n2 = (int)(this.position % 8192L);
            byArray[n2] = (byte)n;
            ++this.position;
            if (this.position > this.length) {
                this.length = this.position;
            }
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            byte[] byArray2 = this.getBlock();
            for (int i = 0; i < n2; ++i) {
                int n3 = (int)this.position % 8192;
                if (n3 == 0) {
                    byArray2 = this.getBlock();
                }
                byArray2[n3] = byArray[n + i];
                ++this.position;
            }
            if (this.position > this.length) {
                this.length = this.position;
            }
        }

        @Override
        public int read() throws IOException {
            if (this.position >= this.length) {
                return -1;
            }
            byte[] byArray = this.getBlock();
            int n = (int)(this.position % 8192L);
            ++this.position;
            return byArray[n] & 0xFF;
        }

        @Override
        public int read(byte[] byArray, int n, int n2) throws IOException {
            int n3;
            if (this.position >= this.length) {
                return -1;
            }
            byte[] byArray2 = this.getBlock();
            int n4 = (int)(this.position % 8192L);
            int n5 = (int)Math.min((long)Math.min(n2, byArray2.length - n4), this.length - this.position);
            for (n3 = 0; n3 < n5; ++n3) {
                byArray[n + n3] = byArray2[n4 + n3];
            }
            this.position += (long)n3;
            return n3;
        }

        @Override
        public void seek(long l) throws IOException {
            if (l < this.start) {
                throw new IOException("Seek before flush position");
            }
            this.position = l;
        }

        @Override
        public void flush(long l) {
            int n = (int)(l / 8192L) - 1;
            for (int i = 0; i < n; ++i) {
                this.cache.remove(0);
            }
            this.start = l;
        }

        @Override
        void close() throws IOException {
            this.cache.clear();
        }

        @Override
        public long getPosition() {
            return this.position;
        }
    }
}

