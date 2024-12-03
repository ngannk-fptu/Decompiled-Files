/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked12
extends BulkOperationPacked {
    public BulkOperationPacked12() {
        super(12);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 52);
            values[valuesOffset++] = (int)(block0 >>> 40 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0xFFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0xFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 8 | block1 >>> 56);
            values[valuesOffset++] = (int)(block1 >>> 44 & 0xFFFL);
            values[valuesOffset++] = (int)(block1 >>> 32 & 0xFFFL);
            values[valuesOffset++] = (int)(block1 >>> 20 & 0xFFFL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0xFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0xFFL) << 4 | block2 >>> 60);
            values[valuesOffset++] = (int)(block2 >>> 48 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 36 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0xFFFL);
            values[valuesOffset++] = (int)(block2 & 0xFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 4 | byte1 >>> 4;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0xF) << 8 | byte2;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 52;
            values[valuesOffset++] = block0 >>> 40 & 0xFFFL;
            values[valuesOffset++] = block0 >>> 28 & 0xFFFL;
            values[valuesOffset++] = block0 >>> 16 & 0xFFFL;
            values[valuesOffset++] = block0 >>> 4 & 0xFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 8 | block1 >>> 56;
            values[valuesOffset++] = block1 >>> 44 & 0xFFFL;
            values[valuesOffset++] = block1 >>> 32 & 0xFFFL;
            values[valuesOffset++] = block1 >>> 20 & 0xFFFL;
            values[valuesOffset++] = block1 >>> 8 & 0xFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0xFFL) << 4 | block2 >>> 60;
            values[valuesOffset++] = block2 >>> 48 & 0xFFFL;
            values[valuesOffset++] = block2 >>> 36 & 0xFFFL;
            values[valuesOffset++] = block2 >>> 24 & 0xFFFL;
            values[valuesOffset++] = block2 >>> 12 & 0xFFFL;
            values[valuesOffset++] = block2 & 0xFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 4 | byte1 >>> 4;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0xFL) << 8 | byte2;
        }
    }
}

