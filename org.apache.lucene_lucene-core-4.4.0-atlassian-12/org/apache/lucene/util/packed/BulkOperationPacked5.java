/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked5
extends BulkOperationPacked {
    public BulkOperationPacked5() {
        super(5);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 59);
            values[valuesOffset++] = (int)(block0 >>> 54 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 49 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 44 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 39 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 29 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 24 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 14 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 9 & 0x1FL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x1FL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 1 | block1 >>> 63);
            values[valuesOffset++] = (int)(block1 >>> 58 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 53 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 48 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 43 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 33 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 28 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 23 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 18 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 13 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0x1FL);
            values[valuesOffset++] = (int)(block1 >>> 3 & 0x1FL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 7L) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 57 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 52 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 47 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 42 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 37 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 32 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 27 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 22 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 17 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 7 & 0x1FL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x1FL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 3L) << 3 | block3 >>> 61);
            values[valuesOffset++] = (int)(block3 >>> 56 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 51 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 46 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 41 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 31 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 26 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 21 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 11 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 6 & 0x1FL);
            values[valuesOffset++] = (int)(block3 >>> 1 & 0x1FL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 1L) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 55 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 50 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 45 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 35 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 30 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 25 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 20 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 15 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 10 & 0x1FL);
            values[valuesOffset++] = (int)(block4 >>> 5 & 0x1FL);
            values[valuesOffset++] = (int)(block4 & 0x1FL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 3;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 7) << 2 | byte1 >>> 6;
            values[valuesOffset++] = byte1 >>> 1 & 0x1F;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 1) << 4 | byte2 >>> 4;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xF) << 1 | byte3 >>> 7;
            values[valuesOffset++] = byte3 >>> 2 & 0x1F;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3) << 3 | byte4 >>> 5;
            values[valuesOffset++] = byte4 & 0x1F;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 59;
            values[valuesOffset++] = block0 >>> 54 & 0x1FL;
            values[valuesOffset++] = block0 >>> 49 & 0x1FL;
            values[valuesOffset++] = block0 >>> 44 & 0x1FL;
            values[valuesOffset++] = block0 >>> 39 & 0x1FL;
            values[valuesOffset++] = block0 >>> 34 & 0x1FL;
            values[valuesOffset++] = block0 >>> 29 & 0x1FL;
            values[valuesOffset++] = block0 >>> 24 & 0x1FL;
            values[valuesOffset++] = block0 >>> 19 & 0x1FL;
            values[valuesOffset++] = block0 >>> 14 & 0x1FL;
            values[valuesOffset++] = block0 >>> 9 & 0x1FL;
            values[valuesOffset++] = block0 >>> 4 & 0x1FL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 1 | block1 >>> 63;
            values[valuesOffset++] = block1 >>> 58 & 0x1FL;
            values[valuesOffset++] = block1 >>> 53 & 0x1FL;
            values[valuesOffset++] = block1 >>> 48 & 0x1FL;
            values[valuesOffset++] = block1 >>> 43 & 0x1FL;
            values[valuesOffset++] = block1 >>> 38 & 0x1FL;
            values[valuesOffset++] = block1 >>> 33 & 0x1FL;
            values[valuesOffset++] = block1 >>> 28 & 0x1FL;
            values[valuesOffset++] = block1 >>> 23 & 0x1FL;
            values[valuesOffset++] = block1 >>> 18 & 0x1FL;
            values[valuesOffset++] = block1 >>> 13 & 0x1FL;
            values[valuesOffset++] = block1 >>> 8 & 0x1FL;
            values[valuesOffset++] = block1 >>> 3 & 0x1FL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 7L) << 2 | block2 >>> 62;
            values[valuesOffset++] = block2 >>> 57 & 0x1FL;
            values[valuesOffset++] = block2 >>> 52 & 0x1FL;
            values[valuesOffset++] = block2 >>> 47 & 0x1FL;
            values[valuesOffset++] = block2 >>> 42 & 0x1FL;
            values[valuesOffset++] = block2 >>> 37 & 0x1FL;
            values[valuesOffset++] = block2 >>> 32 & 0x1FL;
            values[valuesOffset++] = block2 >>> 27 & 0x1FL;
            values[valuesOffset++] = block2 >>> 22 & 0x1FL;
            values[valuesOffset++] = block2 >>> 17 & 0x1FL;
            values[valuesOffset++] = block2 >>> 12 & 0x1FL;
            values[valuesOffset++] = block2 >>> 7 & 0x1FL;
            values[valuesOffset++] = block2 >>> 2 & 0x1FL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 3L) << 3 | block3 >>> 61;
            values[valuesOffset++] = block3 >>> 56 & 0x1FL;
            values[valuesOffset++] = block3 >>> 51 & 0x1FL;
            values[valuesOffset++] = block3 >>> 46 & 0x1FL;
            values[valuesOffset++] = block3 >>> 41 & 0x1FL;
            values[valuesOffset++] = block3 >>> 36 & 0x1FL;
            values[valuesOffset++] = block3 >>> 31 & 0x1FL;
            values[valuesOffset++] = block3 >>> 26 & 0x1FL;
            values[valuesOffset++] = block3 >>> 21 & 0x1FL;
            values[valuesOffset++] = block3 >>> 16 & 0x1FL;
            values[valuesOffset++] = block3 >>> 11 & 0x1FL;
            values[valuesOffset++] = block3 >>> 6 & 0x1FL;
            values[valuesOffset++] = block3 >>> 1 & 0x1FL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 1L) << 4 | block4 >>> 60;
            values[valuesOffset++] = block4 >>> 55 & 0x1FL;
            values[valuesOffset++] = block4 >>> 50 & 0x1FL;
            values[valuesOffset++] = block4 >>> 45 & 0x1FL;
            values[valuesOffset++] = block4 >>> 40 & 0x1FL;
            values[valuesOffset++] = block4 >>> 35 & 0x1FL;
            values[valuesOffset++] = block4 >>> 30 & 0x1FL;
            values[valuesOffset++] = block4 >>> 25 & 0x1FL;
            values[valuesOffset++] = block4 >>> 20 & 0x1FL;
            values[valuesOffset++] = block4 >>> 15 & 0x1FL;
            values[valuesOffset++] = block4 >>> 10 & 0x1FL;
            values[valuesOffset++] = block4 >>> 5 & 0x1FL;
            values[valuesOffset++] = block4 & 0x1FL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 3;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 7L) << 2 | byte1 >>> 6;
            values[valuesOffset++] = byte1 >>> 1 & 0x1FL;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 1L) << 4 | byte2 >>> 4;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0xFL) << 1 | byte3 >>> 7;
            values[valuesOffset++] = byte3 >>> 2 & 0x1FL;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3L) << 3 | byte4 >>> 5;
            values[valuesOffset++] = byte4 & 0x1FL;
        }
    }
}

