/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked20
extends BulkOperationPacked {
    public BulkOperationPacked20() {
        super(20);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 44);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0xFFFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 16 | block1 >>> 48);
            values[valuesOffset++] = (int)(block1 >>> 28 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0xFFFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0xFFL) << 12 | block2 >>> 52);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0xFFFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFFL) << 8 | block3 >>> 56);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0xFFFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFFFFL) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 20 & 0xFFFFFL);
            values[valuesOffset++] = (int)(block4 & 0xFFFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 12 | byte1 << 4 | byte2 >>> 4;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xF) << 16 | byte3 << 8 | byte4;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 44;
            values[valuesOffset++] = block0 >>> 24 & 0xFFFFFL;
            values[valuesOffset++] = block0 >>> 4 & 0xFFFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 16 | block1 >>> 48;
            values[valuesOffset++] = block1 >>> 28 & 0xFFFFFL;
            values[valuesOffset++] = block1 >>> 8 & 0xFFFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0xFFL) << 12 | block2 >>> 52;
            values[valuesOffset++] = block2 >>> 32 & 0xFFFFFL;
            values[valuesOffset++] = block2 >>> 12 & 0xFFFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0xFFFL) << 8 | block3 >>> 56;
            values[valuesOffset++] = block3 >>> 36 & 0xFFFFFL;
            values[valuesOffset++] = block3 >>> 16 & 0xFFFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0xFFFFL) << 4 | block4 >>> 60;
            values[valuesOffset++] = block4 >>> 40 & 0xFFFFFL;
            values[valuesOffset++] = block4 >>> 20 & 0xFFFFFL;
            values[valuesOffset++] = block4 & 0xFFFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 12 | byte1 << 4 | byte2 >>> 4;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xFL) << 16 | byte3 << 8 | byte4;
        }
    }
}

