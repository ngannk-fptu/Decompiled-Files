/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked6
extends BulkOperationPacked {
    public BulkOperationPacked6() {
        super(6);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 58);
            values[valuesOffset++] = (int)(block0 >>> 52 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 46 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 40 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 16 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x3FL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x3FL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 2 | block1 >>> 62);
            values[valuesOffset++] = (int)(block1 >>> 56 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 50 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 44 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 32 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 26 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 20 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 14 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0x3FL);
            values[valuesOffset++] = (int)(block1 >>> 2 & 0x3FL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 3L) << 4 | block2 >>> 60);
            values[valuesOffset++] = (int)(block2 >>> 54 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 48 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 42 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 36 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 30 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 18 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x3FL);
            values[valuesOffset++] = (int)(block2 >>> 6 & 0x3FL);
            values[valuesOffset++] = (int)(block2 & 0x3FL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 2;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 3) << 4 | byte1 >>> 4;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0xF) << 2 | byte2 >>> 6;
            values[valuesOffset++] = byte2 & 0x3F;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 58;
            values[valuesOffset++] = block0 >>> 52 & 0x3FL;
            values[valuesOffset++] = block0 >>> 46 & 0x3FL;
            values[valuesOffset++] = block0 >>> 40 & 0x3FL;
            values[valuesOffset++] = block0 >>> 34 & 0x3FL;
            values[valuesOffset++] = block0 >>> 28 & 0x3FL;
            values[valuesOffset++] = block0 >>> 22 & 0x3FL;
            values[valuesOffset++] = block0 >>> 16 & 0x3FL;
            values[valuesOffset++] = block0 >>> 10 & 0x3FL;
            values[valuesOffset++] = block0 >>> 4 & 0x3FL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 2 | block1 >>> 62;
            values[valuesOffset++] = block1 >>> 56 & 0x3FL;
            values[valuesOffset++] = block1 >>> 50 & 0x3FL;
            values[valuesOffset++] = block1 >>> 44 & 0x3FL;
            values[valuesOffset++] = block1 >>> 38 & 0x3FL;
            values[valuesOffset++] = block1 >>> 32 & 0x3FL;
            values[valuesOffset++] = block1 >>> 26 & 0x3FL;
            values[valuesOffset++] = block1 >>> 20 & 0x3FL;
            values[valuesOffset++] = block1 >>> 14 & 0x3FL;
            values[valuesOffset++] = block1 >>> 8 & 0x3FL;
            values[valuesOffset++] = block1 >>> 2 & 0x3FL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 3L) << 4 | block2 >>> 60;
            values[valuesOffset++] = block2 >>> 54 & 0x3FL;
            values[valuesOffset++] = block2 >>> 48 & 0x3FL;
            values[valuesOffset++] = block2 >>> 42 & 0x3FL;
            values[valuesOffset++] = block2 >>> 36 & 0x3FL;
            values[valuesOffset++] = block2 >>> 30 & 0x3FL;
            values[valuesOffset++] = block2 >>> 24 & 0x3FL;
            values[valuesOffset++] = block2 >>> 18 & 0x3FL;
            values[valuesOffset++] = block2 >>> 12 & 0x3FL;
            values[valuesOffset++] = block2 >>> 6 & 0x3FL;
            values[valuesOffset++] = block2 & 0x3FL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 2;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 3L) << 4 | byte1 >>> 4;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0xFL) << 2 | byte2 >>> 6;
            values[valuesOffset++] = byte2 & 0x3FL;
        }
    }
}

