/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.AbstractPagedMutable;
import org.apache.lucene.util.packed.GrowableWriter;
import org.apache.lucene.util.packed.PackedInts;

public final class PagedGrowableWriter
extends AbstractPagedMutable<PagedGrowableWriter> {
    final float acceptableOverheadRatio;

    public PagedGrowableWriter(long size, int pageSize, int startBitsPerValue, float acceptableOverheadRatio) {
        this(size, pageSize, startBitsPerValue, acceptableOverheadRatio, true);
    }

    PagedGrowableWriter(long size, int pageSize, int startBitsPerValue, float acceptableOverheadRatio, boolean fillPages) {
        super(startBitsPerValue, size, pageSize);
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        if (fillPages) {
            this.fillPages();
        }
    }

    @Override
    protected PackedInts.Mutable newMutable(int valueCount, int bitsPerValue) {
        return new GrowableWriter(bitsPerValue, valueCount, this.acceptableOverheadRatio);
    }

    @Override
    protected PagedGrowableWriter newUnfilledCopy(long newSize) {
        return new PagedGrowableWriter(newSize, this.pageSize(), this.bitsPerValue, this.acceptableOverheadRatio, false);
    }

    @Override
    protected long baseRamBytesUsed() {
        return super.baseRamBytesUsed() + 4L;
    }
}

