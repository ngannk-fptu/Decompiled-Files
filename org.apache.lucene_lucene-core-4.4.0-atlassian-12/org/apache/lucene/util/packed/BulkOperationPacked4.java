/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked4
extends BulkOperationPacked {
    public BulkOperationPacked4() {
        super(4);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 60; shift >= 0; shift -= 4) {
                values[valuesOffset++] = (int)(block >>> shift & 0xFL);
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            byte block = blocks[blocksOffset++];
            values[valuesOffset++] = block >>> 4 & 0xF;
            values[valuesOffset++] = block & 0xF;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 60; shift >= 0; shift -= 4) {
                values[valuesOffset++] = block >>> shift & 0xFL;
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            byte block = blocks[blocksOffset++];
            values[valuesOffset++] = block >>> 4 & 0xF;
            values[valuesOffset++] = block & 0xF;
        }
    }
}

