/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked10
extends BulkOperationPacked {
    public BulkOperationPacked10() {
        super(10);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 54);
            values[valuesOffset++] = (int)(block0 >>> 44 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 14 & 0x3FFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x3FFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 6 | block1 >>> 58);
            values[valuesOffset++] = (int)(block1 >>> 48 & 0x3FFL);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x3FFL);
            values[valuesOffset++] = (int)(block1 >>> 28 & 0x3FFL);
            values[valuesOffset++] = (int)(block1 >>> 18 & 0x3FFL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0x3FFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0xFFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 52 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 42 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 22 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x3FFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x3FFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 3L) << 8 | block3 >>> 56);
            values[valuesOffset++] = (int)(block3 >>> 46 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 26 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0x3FFL);
            values[valuesOffset++] = (int)(block3 >>> 6 & 0x3FFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3FL) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 50 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 30 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 20 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 >>> 10 & 0x3FFL);
            values[valuesOffset++] = (int)(block4 & 0x3FFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 2 | byte1 >>> 6;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x3F) << 4 | byte2 >>> 4;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xF) << 6 | byte3 >>> 2;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3) << 8 | byte4;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 54;
            values[valuesOffset++] = block0 >>> 44 & 0x3FFL;
            values[valuesOffset++] = block0 >>> 34 & 0x3FFL;
            values[valuesOffset++] = block0 >>> 24 & 0x3FFL;
            values[valuesOffset++] = block0 >>> 14 & 0x3FFL;
            values[valuesOffset++] = block0 >>> 4 & 0x3FFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 6 | block1 >>> 58;
            values[valuesOffset++] = block1 >>> 48 & 0x3FFL;
            values[valuesOffset++] = block1 >>> 38 & 0x3FFL;
            values[valuesOffset++] = block1 >>> 28 & 0x3FFL;
            values[valuesOffset++] = block1 >>> 18 & 0x3FFL;
            values[valuesOffset++] = block1 >>> 8 & 0x3FFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0xFFL) << 2 | block2 >>> 62;
            values[valuesOffset++] = block2 >>> 52 & 0x3FFL;
            values[valuesOffset++] = block2 >>> 42 & 0x3FFL;
            values[valuesOffset++] = block2 >>> 32 & 0x3FFL;
            values[valuesOffset++] = block2 >>> 22 & 0x3FFL;
            values[valuesOffset++] = block2 >>> 12 & 0x3FFL;
            values[valuesOffset++] = block2 >>> 2 & 0x3FFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 3L) << 8 | block3 >>> 56;
            values[valuesOffset++] = block3 >>> 46 & 0x3FFL;
            values[valuesOffset++] = block3 >>> 36 & 0x3FFL;
            values[valuesOffset++] = block3 >>> 26 & 0x3FFL;
            values[valuesOffset++] = block3 >>> 16 & 0x3FFL;
            values[valuesOffset++] = block3 >>> 6 & 0x3FFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0x3FL) << 4 | block4 >>> 60;
            values[valuesOffset++] = block4 >>> 50 & 0x3FFL;
            values[valuesOffset++] = block4 >>> 40 & 0x3FFL;
            values[valuesOffset++] = block4 >>> 30 & 0x3FFL;
            values[valuesOffset++] = block4 >>> 20 & 0x3FFL;
            values[valuesOffset++] = block4 >>> 10 & 0x3FFL;
            values[valuesOffset++] = block4 & 0x3FFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 2 | byte1 >>> 6;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x3FL) << 4 | byte2 >>> 4;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xFL) << 6 | byte3 >>> 2;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3L) << 8 | byte4;
        }
    }
}

