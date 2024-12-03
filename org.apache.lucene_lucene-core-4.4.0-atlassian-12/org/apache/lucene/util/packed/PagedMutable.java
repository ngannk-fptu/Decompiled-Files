/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.AbstractPagedMutable;
import org.apache.lucene.util.packed.PackedInts;

public final class PagedMutable
extends AbstractPagedMutable<PagedMutable> {
    final PackedInts.Format format;

    public PagedMutable(long size, int pageSize, int bitsPerValue, float acceptableOverheadRatio) {
        this(size, pageSize, PackedInts.fastestFormatAndBits(pageSize, bitsPerValue, acceptableOverheadRatio));
        this.fillPages();
    }

    PagedMutable(long size, int pageSize, PackedInts.FormatAndBits formatAndBits) {
        this(size, pageSize, formatAndBits.bitsPerValue, formatAndBits.format);
    }

    PagedMutable(long size, int pageSize, int bitsPerValue, PackedInts.Format format) {
        super(bitsPerValue, size, pageSize);
        this.format = format;
    }

    @Override
    protected PackedInts.Mutable newMutable(int valueCount, int bitsPerValue) {
        assert (this.bitsPerValue >= bitsPerValue);
        return PackedInts.getMutable(valueCount, this.bitsPerValue, this.format);
    }

    @Override
    protected PagedMutable newUnfilledCopy(long newSize) {
        return new PagedMutable(newSize, this.pageSize(), this.bitsPerValue, this.format);
    }

    @Override
    protected long baseRamBytesUsed() {
        return super.baseRamBytesUsed() + (long)RamUsageEstimator.NUM_BYTES_OBJECT_REF;
    }
}

