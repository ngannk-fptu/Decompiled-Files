/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

abstract class AbstractPagedMutable<T extends AbstractPagedMutable<T>> {
    static final int MIN_BLOCK_SIZE = 64;
    static final int MAX_BLOCK_SIZE = 0x40000000;
    final long size;
    final int pageShift;
    final int pageMask;
    final PackedInts.Mutable[] subMutables;
    final int bitsPerValue;

    AbstractPagedMutable(int bitsPerValue, long size, int pageSize) {
        this.bitsPerValue = bitsPerValue;
        this.size = size;
        this.pageShift = PackedInts.checkBlockSize(pageSize, 64, 0x40000000);
        this.pageMask = pageSize - 1;
        int numPages = PackedInts.numBlocks(size, pageSize);
        this.subMutables = new PackedInts.Mutable[numPages];
    }

    protected final void fillPages() {
        int numPages = PackedInts.numBlocks(this.size, this.pageSize());
        for (int i = 0; i < numPages; ++i) {
            int valueCount = i == numPages - 1 ? this.lastPageSize(this.size) : this.pageSize();
            this.subMutables[i] = this.newMutable(valueCount, this.bitsPerValue);
        }
    }

    protected abstract PackedInts.Mutable newMutable(int var1, int var2);

    final int lastPageSize(long size) {
        int sz = this.indexInPage(size);
        return sz == 0 ? this.pageSize() : sz;
    }

    final int pageSize() {
        return this.pageMask + 1;
    }

    public final long size() {
        return this.size;
    }

    final int pageIndex(long index) {
        return (int)(index >>> this.pageShift);
    }

    final int indexInPage(long index) {
        return (int)index & this.pageMask;
    }

    public final long get(long index) {
        assert (index >= 0L && index < this.size);
        int pageIndex = this.pageIndex(index);
        int indexInPage = this.indexInPage(index);
        return this.subMutables[pageIndex].get(indexInPage);
    }

    public final void set(long index, long value) {
        assert (index >= 0L && index < this.size);
        int pageIndex = this.pageIndex(index);
        int indexInPage = this.indexInPage(index);
        this.subMutables[pageIndex].set(indexInPage, value);
    }

    protected long baseRamBytesUsed() {
        return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8 + 12;
    }

    public long ramBytesUsed() {
        long bytesUsed = RamUsageEstimator.alignObjectSize(this.baseRamBytesUsed());
        bytesUsed += RamUsageEstimator.alignObjectSize((long)RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + (long)RamUsageEstimator.NUM_BYTES_OBJECT_REF * (long)this.subMutables.length);
        for (PackedInts.Mutable gw : this.subMutables) {
            bytesUsed += gw.ramBytesUsed();
        }
        return bytesUsed;
    }

    protected abstract T newUnfilledCopy(long var1);

    public final T resize(long newSize) {
        T copy = this.newUnfilledCopy(newSize);
        int numCommonPages = Math.min(((AbstractPagedMutable)copy).subMutables.length, this.subMutables.length);
        long[] copyBuffer = new long[1024];
        for (int i = 0; i < ((AbstractPagedMutable)copy).subMutables.length; ++i) {
            int valueCount = i == ((AbstractPagedMutable)copy).subMutables.length - 1 ? this.lastPageSize(newSize) : this.pageSize();
            int bpv = i < numCommonPages ? this.subMutables[i].getBitsPerValue() : this.bitsPerValue;
            ((AbstractPagedMutable)copy).subMutables[i] = this.newMutable(valueCount, bpv);
            if (i >= numCommonPages) continue;
            int copyLength = Math.min(valueCount, this.subMutables[i].size());
            PackedInts.copy((PackedInts.Reader)this.subMutables[i], 0, ((AbstractPagedMutable)copy).subMutables[i], 0, copyLength, copyBuffer);
        }
        return copy;
    }

    public final T grow(long minSize) {
        assert (minSize >= 0L);
        if (minSize <= this.size()) {
            AbstractPagedMutable result = this;
            return (T)result;
        }
        long extra = minSize >>> 3;
        if (extra < 3L) {
            extra = 3L;
        }
        long newSize = minSize + extra;
        return this.resize(newSize);
    }

    public final T grow() {
        return this.grow(this.size() + 1L);
    }

    public final String toString() {
        return this.getClass().getSimpleName() + "(size=" + this.size() + ",pageSize=" + this.pageSize() + ")";
    }
}

