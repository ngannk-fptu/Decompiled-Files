/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked8
extends BulkOperationPacked {
    public BulkOperationPacked8() {
        super(8);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 56; shift >= 0; shift -= 8) {
                values[valuesOffset++] = (int)(block >>> shift & 0xFFL);
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = blocks[blocksOffset++] & 0xFF;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            for (int shift = 56; shift >= 0; shift -= 8) {
                values[valuesOffset++] = block >>> shift & 0xFFL;
            }
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int j = 0; j < iterations; ++j) {
            values[valuesOffset++] = blocks[blocksOffset++] & 0xFF;
        }
    }
}

