/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked2
extends BulkOperationPacked {
    public BulkOperationPacked2() {
        super(2);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 62; shift >= 0; shift -= 2) {
                values[valuesOffset++] = (int)(block >>> shift & 3L);
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            byte block = blocks[blocksOffset++];
            values[valuesOffset++] = block >>> 6 & 3;
            values[valuesOffset++] = block >>> 4 & 3;
            values[valuesOffset++] = block >>> 2 & 3;
            values[valuesOffset++] = block & 3;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 62; shift >= 0; shift -= 2) {
                values[valuesOffset++] = block >>> shift & 3L;
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            byte block = blocks[blocksOffset++];
            values[valuesOffset++] = block >>> 6 & 3;
            values[valuesOffset++] = block >>> 4 & 3;
            values[valuesOffset++] = block >>> 2 & 3;
            values[valuesOffset++] = block & 3;
        }
    }
}

