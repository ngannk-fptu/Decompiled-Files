/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked24
extends BulkOperationPacked {
    public BulkOperationPacked24() {
        super(24);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 40);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0xFFFFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFFL) << 8 | block1 >>> 56);
            values[valuesOffset++] = (int)(block1 >>> 32 & 0xFFFFFFL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0xFFFFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0xFFL) << 16 | block2 >>> 48);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0xFFFFFFL);
            values[valuesOffset++] = (int)(block2 & 0xFFFFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 16 | byte1 << 8 | byte2;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 40;
            values[valuesOffset++] = block0 >>> 16 & 0xFFFFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFFFFL) << 8 | block1 >>> 56;
            values[valuesOffset++] = block1 >>> 32 & 0xFFFFFFL;
            values[valuesOffset++] = block1 >>> 8 & 0xFFFFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0xFFL) << 16 | block2 >>> 48;
            values[valuesOffset++] = block2 >>> 24 & 0xFFFFFFL;
            values[valuesOffset++] = block2 & 0xFFFFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 16 | byte1 << 8 | byte2;
        }
    }
}

