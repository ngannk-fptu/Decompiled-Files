/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.ByteBlockPool;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.SorterTemplate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BytesRefHash {
    public static final int DEFAULT_CAPACITY = 16;
    final ByteBlockPool pool;
    int[] bytesStart;
    private final BytesRef scratch1 = new BytesRef();
    private int hashSize;
    private int hashHalfSize;
    private int hashMask;
    private int count;
    private int lastCount = -1;
    private int[] ords;
    private final BytesStartArray bytesStartArray;
    private AtomicLong bytesUsed;

    public BytesRefHash() {
        this(new ByteBlockPool(new ByteBlockPool.DirectAllocator()));
    }

    public BytesRefHash(ByteBlockPool pool) {
        this(pool, 16, new DirectBytesStartArray(16));
    }

    public BytesRefHash(ByteBlockPool pool, int capacity, BytesStartArray bytesStartArray) {
        this.hashSize = capacity;
        this.hashHalfSize = this.hashSize >> 1;
        this.hashMask = this.hashSize - 1;
        this.pool = pool;
        this.ords = new int[this.hashSize];
        Arrays.fill(this.ords, -1);
        this.bytesStartArray = bytesStartArray;
        this.bytesStart = bytesStartArray.init();
        this.bytesUsed = bytesStartArray.bytesUsed() == null ? new AtomicLong(0L) : bytesStartArray.bytesUsed();
        this.bytesUsed.addAndGet(this.hashSize * 4);
    }

    public int size() {
        return this.count;
    }

    public BytesRef get(int ord, BytesRef ref) {
        assert (this.bytesStart != null) : "bytesStart is null - not initialized";
        assert (ord < this.bytesStart.length) : "ord exceeds byteStart len: " + this.bytesStart.length;
        return this.pool.setBytesRef(ref, this.bytesStart[ord]);
    }

    public int[] compact() {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        int upto = 0;
        for (int i = 0; i < this.hashSize; ++i) {
            if (this.ords[i] == -1) continue;
            if (upto < i) {
                this.ords[upto] = this.ords[i];
                this.ords[i] = -1;
            }
            ++upto;
        }
        assert (upto == this.count);
        this.lastCount = this.count;
        return this.ords;
    }

    public int[] sort(final Comparator<BytesRef> comp) {
        final int[] compact = this.compact();
        new SorterTemplate(){
            private final BytesRef pivot = new BytesRef();
            private final BytesRef scratch1 = new BytesRef();
            private final BytesRef scratch2 = new BytesRef();

            protected void swap(int i, int j) {
                int o = compact[i];
                compact[i] = compact[j];
                compact[j] = o;
            }

            protected int compare(int i, int j) {
                int ord1 = compact[i];
                int ord2 = compact[j];
                assert (BytesRefHash.this.bytesStart.length > ord1 && BytesRefHash.this.bytesStart.length > ord2);
                return comp.compare(BytesRefHash.this.pool.setBytesRef(this.scratch1, BytesRefHash.this.bytesStart[ord1]), BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[ord2]));
            }

            protected void setPivot(int i) {
                int ord = compact[i];
                assert (BytesRefHash.this.bytesStart.length > ord);
                BytesRefHash.this.pool.setBytesRef(this.pivot, BytesRefHash.this.bytesStart[ord]);
            }

            protected int comparePivot(int j) {
                int ord = compact[j];
                assert (BytesRefHash.this.bytesStart.length > ord);
                return comp.compare(this.pivot, BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[ord]));
            }
        }.quickSort(0, this.count - 1);
        return compact;
    }

    private boolean equals(int ord, BytesRef b) {
        return this.pool.setBytesRef(this.scratch1, this.bytesStart[ord]).bytesEquals(b);
    }

    private boolean shrink(int targetSize) {
        int newSize;
        for (newSize = this.hashSize; newSize >= 8 && newSize / 4 > targetSize; newSize /= 2) {
        }
        if (newSize != this.hashSize) {
            this.bytesUsed.addAndGet(4 * -(this.hashSize - newSize));
            this.hashSize = newSize;
            this.ords = new int[this.hashSize];
            Arrays.fill(this.ords, -1);
            this.hashHalfSize = newSize / 2;
            this.hashMask = newSize - 1;
            return true;
        }
        return false;
    }

    public void clear(boolean resetPool) {
        this.lastCount = this.count;
        this.count = 0;
        if (resetPool) {
            this.pool.dropBuffersAndReset();
        }
        this.bytesStart = this.bytesStartArray.clear();
        if (this.lastCount != -1 && this.shrink(this.lastCount)) {
            return;
        }
        Arrays.fill(this.ords, -1);
    }

    public void clear() {
        this.clear(true);
    }

    public void close() {
        this.clear(true);
        this.ords = null;
        this.bytesUsed.addAndGet(4 * -this.hashSize);
    }

    public int add(BytesRef bytes) {
        return this.add(bytes, bytes.hashCode());
    }

    public int add(BytesRef bytes, int code) {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        int length = bytes.length;
        int hashPos = code & this.hashMask;
        int e = this.ords[hashPos];
        if (e != -1 && !this.equals(e, bytes)) {
            int inc = (code >> 8) + code | 1;
            while ((e = this.ords[hashPos = (code += inc) & this.hashMask]) != -1 && !this.equals(e, bytes)) {
            }
        }
        if (e == -1) {
            int len2 = 2 + bytes.length;
            if (len2 + this.pool.byteUpto > 32768) {
                if (len2 > 32768) {
                    throw new MaxBytesLengthExceededException("bytes can be at most 32766 in length; got " + bytes.length);
                }
                this.pool.nextBuffer();
            }
            byte[] buffer = this.pool.buffer;
            int bufferUpto = this.pool.byteUpto;
            if (this.count >= this.bytesStart.length) {
                this.bytesStart = this.bytesStartArray.grow();
                assert (this.count < this.bytesStart.length + 1) : "count: " + this.count + " len: " + this.bytesStart.length;
            }
            e = this.count++;
            this.bytesStart[e] = bufferUpto + this.pool.byteOffset;
            if (length < 128) {
                buffer[bufferUpto] = (byte)length;
                this.pool.byteUpto += length + 1;
                assert (length >= 0) : "Length must be positive: " + length;
                System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 1, length);
            } else {
                buffer[bufferUpto] = (byte)(0x80 | length & 0x7F);
                buffer[bufferUpto + 1] = (byte)(length >> 7 & 0xFF);
                this.pool.byteUpto += length + 2;
                System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 2, length);
            }
            assert (this.ords[hashPos] == -1);
            this.ords[hashPos] = e;
            if (this.count == this.hashHalfSize) {
                this.rehash(2 * this.hashSize, true);
            }
            return e;
        }
        return -(e + 1);
    }

    public int addByPoolOffset(int offset) {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        int code = offset;
        int hashPos = offset & this.hashMask;
        int e = this.ords[hashPos];
        if (e != -1 && this.bytesStart[e] != offset) {
            int inc = (code >> 8) + code | 1;
            while ((e = this.ords[hashPos = (code += inc) & this.hashMask]) != -1 && this.bytesStart[e] != offset) {
            }
        }
        if (e == -1) {
            if (this.count >= this.bytesStart.length) {
                this.bytesStart = this.bytesStartArray.grow();
                assert (this.count < this.bytesStart.length + 1) : "count: " + this.count + " len: " + this.bytesStart.length;
            }
            e = this.count++;
            this.bytesStart[e] = offset;
            assert (this.ords[hashPos] == -1);
            this.ords[hashPos] = e;
            if (this.count == this.hashHalfSize) {
                this.rehash(2 * this.hashSize, false);
            }
            return e;
        }
        return -(e + 1);
    }

    private void rehash(int newSize, boolean hashOnData) {
        int newMask = newSize - 1;
        this.bytesUsed.addAndGet(4 * newSize);
        int[] newHash = new int[newSize];
        Arrays.fill(newHash, -1);
        for (int i = 0; i < this.hashSize; ++i) {
            int code;
            int e0 = this.ords[i];
            if (e0 == -1) continue;
            if (hashOnData) {
                int pos;
                int len;
                int off = this.bytesStart[e0];
                int start = off & Short.MAX_VALUE;
                byte[] bytes = this.pool.buffers[off >> 15];
                code = 0;
                if ((bytes[start] & 0x80) == 0) {
                    len = bytes[start];
                    pos = start + 1;
                } else {
                    len = (bytes[start] & 0x7F) + ((bytes[start + 1] & 0xFF) << 7);
                    pos = start + 2;
                }
                int endPos = pos + len;
                while (pos < endPos) {
                    code = 31 * code + bytes[pos++];
                }
            } else {
                code = this.bytesStart[e0];
            }
            int hashPos = code & newMask;
            assert (hashPos >= 0);
            if (newHash[hashPos] != -1) {
                int inc = (code >> 8) + code | 1;
                while (newHash[hashPos = (code += inc) & newMask] != -1) {
                }
            }
            newHash[hashPos] = e0;
        }
        this.hashMask = newMask;
        this.bytesUsed.addAndGet(4 * -this.ords.length);
        this.ords = newHash;
        this.hashSize = newSize;
        this.hashHalfSize = newSize / 2;
    }

    public void reinit() {
        if (this.bytesStart == null) {
            this.bytesStart = this.bytesStartArray.init();
        }
        if (this.ords == null) {
            this.ords = new int[this.hashSize];
            this.bytesUsed.addAndGet(4 * this.hashSize);
        }
    }

    public int byteStart(int ord) {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        assert (ord >= 0 && ord < this.count) : ord;
        return this.bytesStart[ord];
    }

    public static class DirectBytesStartArray
    extends BytesStartArray {
        protected final int initSize;
        private int[] bytesStart;
        private final AtomicLong bytesUsed = new AtomicLong(0L);

        public DirectBytesStartArray(int initSize) {
            this.initSize = initSize;
        }

        public int[] clear() {
            this.bytesStart = null;
            return null;
        }

        public int[] grow() {
            assert (this.bytesStart != null);
            this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
            return this.bytesStart;
        }

        public int[] init() {
            this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
            return this.bytesStart;
        }

        public AtomicLong bytesUsed() {
            return this.bytesUsed;
        }
    }

    public static class TrackingDirectBytesStartArray
    extends BytesStartArray {
        protected final int initSize;
        private int[] bytesStart;
        protected final AtomicLong bytesUsed;

        public TrackingDirectBytesStartArray(int initSize, AtomicLong bytesUsed) {
            this.initSize = initSize;
            this.bytesUsed = bytesUsed;
        }

        public int[] clear() {
            if (this.bytesStart != null) {
                this.bytesUsed.addAndGet(-this.bytesStart.length * 4);
            }
            this.bytesStart = null;
            return null;
        }

        public int[] grow() {
            assert (this.bytesStart != null);
            int oldSize = this.bytesStart.length;
            this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
            this.bytesUsed.addAndGet((this.bytesStart.length - oldSize) * 4);
            return this.bytesStart;
        }

        public int[] init() {
            this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
            this.bytesUsed.addAndGet(this.bytesStart.length * 4);
            return this.bytesStart;
        }

        public AtomicLong bytesUsed() {
            return this.bytesUsed;
        }
    }

    public static abstract class BytesStartArray {
        public abstract int[] init();

        public abstract int[] grow();

        public abstract int[] clear();

        public abstract AtomicLong bytesUsed();
    }

    public static class MaxBytesLengthExceededException
    extends RuntimeException {
        MaxBytesLengthExceededException(String message) {
            super(message);
        }
    }
}

