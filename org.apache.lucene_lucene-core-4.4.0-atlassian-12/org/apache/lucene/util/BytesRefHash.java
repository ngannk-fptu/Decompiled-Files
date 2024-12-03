/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.IntroSorter;

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
    private int[] ids;
    private final BytesStartArray bytesStartArray;
    private Counter bytesUsed;

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
        this.ids = new int[this.hashSize];
        Arrays.fill(this.ids, -1);
        this.bytesStartArray = bytesStartArray;
        this.bytesStart = bytesStartArray.init();
        this.bytesUsed = bytesStartArray.bytesUsed() == null ? Counter.newCounter() : bytesStartArray.bytesUsed();
        this.bytesUsed.addAndGet(this.hashSize * 4);
    }

    public int size() {
        return this.count;
    }

    public BytesRef get(int bytesID, BytesRef ref) {
        assert (this.bytesStart != null) : "bytesStart is null - not initialized";
        assert (bytesID < this.bytesStart.length) : "bytesID exceeds byteStart len: " + this.bytesStart.length;
        this.pool.setBytesRef(ref, this.bytesStart[bytesID]);
        return ref;
    }

    int[] compact() {
        assert (this.bytesStart != null) : "bytesStart is null - not initialized";
        int upto = 0;
        for (int i = 0; i < this.hashSize; ++i) {
            if (this.ids[i] == -1) continue;
            if (upto < i) {
                this.ids[upto] = this.ids[i];
                this.ids[i] = -1;
            }
            ++upto;
        }
        assert (upto == this.count);
        this.lastCount = this.count;
        return this.ids;
    }

    public int[] sort(final Comparator<BytesRef> comp) {
        final int[] compact = this.compact();
        new IntroSorter(){
            private final BytesRef pivot = new BytesRef();
            private final BytesRef scratch1 = new BytesRef();
            private final BytesRef scratch2 = new BytesRef();

            @Override
            protected void swap(int i, int j) {
                int o = compact[i];
                compact[i] = compact[j];
                compact[j] = o;
            }

            @Override
            protected int compare(int i, int j) {
                int id1 = compact[i];
                int id2 = compact[j];
                assert (BytesRefHash.this.bytesStart.length > id1 && BytesRefHash.this.bytesStart.length > id2);
                BytesRefHash.this.pool.setBytesRef(this.scratch1, BytesRefHash.this.bytesStart[id1]);
                BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[id2]);
                return comp.compare(this.scratch1, this.scratch2);
            }

            @Override
            protected void setPivot(int i) {
                int id = compact[i];
                assert (BytesRefHash.this.bytesStart.length > id);
                BytesRefHash.this.pool.setBytesRef(this.pivot, BytesRefHash.this.bytesStart[id]);
            }

            @Override
            protected int comparePivot(int j) {
                int id = compact[j];
                assert (BytesRefHash.this.bytesStart.length > id);
                BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[id]);
                return comp.compare(this.pivot, this.scratch2);
            }
        }.sort(0, this.count);
        return compact;
    }

    private boolean equals(int id, BytesRef b) {
        this.pool.setBytesRef(this.scratch1, this.bytesStart[id]);
        return this.scratch1.bytesEquals(b);
    }

    private boolean shrink(int targetSize) {
        int newSize;
        for (newSize = this.hashSize; newSize >= 8 && newSize / 4 > targetSize; newSize /= 2) {
        }
        if (newSize != this.hashSize) {
            this.bytesUsed.addAndGet(4 * -(this.hashSize - newSize));
            this.hashSize = newSize;
            this.ids = new int[this.hashSize];
            Arrays.fill(this.ids, -1);
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
            this.pool.reset(false, false);
        }
        this.bytesStart = this.bytesStartArray.clear();
        if (this.lastCount != -1 && this.shrink(this.lastCount)) {
            return;
        }
        Arrays.fill(this.ids, -1);
    }

    public void clear() {
        this.clear(true);
    }

    public void close() {
        this.clear(true);
        this.ids = null;
        this.bytesUsed.addAndGet(4 * -this.hashSize);
    }

    public int add(BytesRef bytes) {
        return this.add(bytes, bytes.hashCode());
    }

    public int add(BytesRef bytes, int code) {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        int length = bytes.length;
        int hashPos = this.findHash(bytes, code);
        int e = this.ids[hashPos];
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
            assert (this.ids[hashPos] == -1);
            this.ids[hashPos] = e;
            if (this.count == this.hashHalfSize) {
                this.rehash(2 * this.hashSize, true);
            }
            return e;
        }
        return -(e + 1);
    }

    public int find(BytesRef bytes) {
        return this.find(bytes, bytes.hashCode());
    }

    public int find(BytesRef bytes, int code) {
        return this.ids[this.findHash(bytes, code)];
    }

    private final int findHash(BytesRef bytes, int code) {
        assert (this.bytesStart != null) : "bytesStart is null - not initialized";
        int hashPos = code & this.hashMask;
        int e = this.ids[hashPos];
        if (e != -1 && !this.equals(e, bytes)) {
            int inc = (code >> 8) + code | 1;
            while ((e = this.ids[hashPos = (code += inc) & this.hashMask]) != -1 && !this.equals(e, bytes)) {
            }
        }
        return hashPos;
    }

    public int addByPoolOffset(int offset) {
        assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
        int code = offset;
        int hashPos = offset & this.hashMask;
        int e = this.ids[hashPos];
        if (e != -1 && this.bytesStart[e] != offset) {
            int inc = (code >> 8) + code | 1;
            while ((e = this.ids[hashPos = (code += inc) & this.hashMask]) != -1 && this.bytesStart[e] != offset) {
            }
        }
        if (e == -1) {
            if (this.count >= this.bytesStart.length) {
                this.bytesStart = this.bytesStartArray.grow();
                assert (this.count < this.bytesStart.length + 1) : "count: " + this.count + " len: " + this.bytesStart.length;
            }
            e = this.count++;
            this.bytesStart[e] = offset;
            assert (this.ids[hashPos] == -1);
            this.ids[hashPos] = e;
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
            int e0 = this.ids[i];
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
        this.bytesUsed.addAndGet(4 * -this.ids.length);
        this.ids = newHash;
        this.hashSize = newSize;
        this.hashHalfSize = newSize / 2;
    }

    public void reinit() {
        if (this.bytesStart == null) {
            this.bytesStart = this.bytesStartArray.init();
        }
        if (this.ids == null) {
            this.ids = new int[this.hashSize];
            this.bytesUsed.addAndGet(4 * this.hashSize);
        }
    }

    public int byteStart(int bytesID) {
        assert (this.bytesStart != null) : "bytesStart is null - not initialized";
        assert (bytesID >= 0 && bytesID < this.count) : bytesID;
        return this.bytesStart[bytesID];
    }

    public static class DirectBytesStartArray
    extends BytesStartArray {
        protected final int initSize;
        private int[] bytesStart;
        private final Counter bytesUsed;

        public DirectBytesStartArray(int initSize, Counter counter) {
            this.bytesUsed = counter;
            this.initSize = initSize;
        }

        public DirectBytesStartArray(int initSize) {
            this(initSize, Counter.newCounter());
        }

        @Override
        public int[] clear() {
            this.bytesStart = null;
            return null;
        }

        @Override
        public int[] grow() {
            assert (this.bytesStart != null);
            this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
            return this.bytesStart;
        }

        @Override
        public int[] init() {
            this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
            return this.bytesStart;
        }

        @Override
        public Counter bytesUsed() {
            return this.bytesUsed;
        }
    }

    public static abstract class BytesStartArray {
        public abstract int[] init();

        public abstract int[] grow();

        public abstract int[] clear();

        public abstract Counter bytesUsed();
    }

    public static class MaxBytesLengthExceededException
    extends RuntimeException {
        MaxBytesLengthExceededException(String message) {
            super(message);
        }
    }
}

