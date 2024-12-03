/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked7
extends BulkOperationPacked {
    public BulkOperationPacked7() {
        super(7);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 57);
            values[valuesOffset++] = (int)(block0 >>> 50 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 43 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 36 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 29 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 15 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 8 & 0x7FL);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x7FL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 1L) << 6 | block1 >>> 58);
            values[valuesOffset++] = (int)(block1 >>> 51 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 44 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 37 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 30 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 23 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 16 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 9 & 0x7FL);
            values[valuesOffset++] = (int)(block1 >>> 2 & 0x7FL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 3L) << 5 | block2 >>> 59);
            values[valuesOffset++] = (int)(block2 >>> 52 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 45 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 31 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 17 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 10 & 0x7FL);
            values[valuesOffset++] = (int)(block2 >>> 3 & 0x7FL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 7L) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 53 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 46 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 39 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 32 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 25 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 18 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 11 & 0x7FL);
            values[valuesOffset++] = (int)(block3 >>> 4 & 0x7FL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFL) << 3 | block4 >>> 61);
            values[valuesOffset++] = (int)(block4 >>> 54 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 47 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 33 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 26 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 19 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 12 & 0x7FL);
            values[valuesOffset++] = (int)(block4 >>> 5 & 0x7FL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FL) << 2 | block5 >>> 62);
            values[valuesOffset++] = (int)(block5 >>> 55 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 48 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 41 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 34 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 27 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 13 & 0x7FL);
            values[valuesOffset++] = (int)(block5 >>> 6 & 0x7FL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FL) << 1 | block6 >>> 63);
            values[valuesOffset++] = (int)(block6 >>> 56 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 49 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 42 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 35 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 28 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 21 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 14 & 0x7FL);
            values[valuesOffset++] = (int)(block6 >>> 7 & 0x7FL);
            values[valuesOffset++] = (int)(block6 & 0x7FL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 1;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 1) << 6 | byte1 >>> 2;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 3) << 5 | byte2 >>> 3;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 7) << 4 | byte3 >>> 4;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0xF) << 3 | byte4 >>> 5;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x1F) << 2 | byte5 >>> 6;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0x3F) << 1 | byte6 >>> 7;
            values[valuesOffset++] = byte6 & 0x7F;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 57;
            values[valuesOffset++] = block0 >>> 50 & 0x7FL;
            values[valuesOffset++] = block0 >>> 43 & 0x7FL;
            values[valuesOffset++] = block0 >>> 36 & 0x7FL;
            values[valuesOffset++] = block0 >>> 29 & 0x7FL;
            values[valuesOffset++] = block0 >>> 22 & 0x7FL;
            values[valuesOffset++] = block0 >>> 15 & 0x7FL;
            values[valuesOffset++] = block0 >>> 8 & 0x7FL;
            values[valuesOffset++] = block0 >>> 1 & 0x7FL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 1L) << 6 | block1 >>> 58;
            values[valuesOffset++] = block1 >>> 51 & 0x7FL;
            values[valuesOffset++] = block1 >>> 44 & 0x7FL;
            values[valuesOffset++] = block1 >>> 37 & 0x7FL;
            values[valuesOffset++] = block1 >>> 30 & 0x7FL;
            values[valuesOffset++] = block1 >>> 23 & 0x7FL;
            values[valuesOffset++] = block1 >>> 16 & 0x7FL;
            values[valuesOffset++] = block1 >>> 9 & 0x7FL;
            values[valuesOffset++] = block1 >>> 2 & 0x7FL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 3L) << 5 | block2 >>> 59;
            values[valuesOffset++] = block2 >>> 52 & 0x7FL;
            values[valuesOffset++] = block2 >>> 45 & 0x7FL;
            values[valuesOffset++] = block2 >>> 38 & 0x7FL;
            values[valuesOffset++] = block2 >>> 31 & 0x7FL;
            values[valuesOffset++] = block2 >>> 24 & 0x7FL;
            values[valuesOffset++] = block2 >>> 17 & 0x7FL;
            values[valuesOffset++] = block2 >>> 10 & 0x7FL;
            values[valuesOffset++] = block2 >>> 3 & 0x7FL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 7L) << 4 | block3 >>> 60;
            values[valuesOffset++] = block3 >>> 53 & 0x7FL;
            values[valuesOffset++] = block3 >>> 46 & 0x7FL;
            values[valuesOffset++] = block3 >>> 39 & 0x7FL;
            values[valuesOffset++] = block3 >>> 32 & 0x7FL;
            values[valuesOffset++] = block3 >>> 25 & 0x7FL;
            values[valuesOffset++] = block3 >>> 18 & 0x7FL;
            values[valuesOffset++] = block3 >>> 11 & 0x7FL;
            values[valuesOffset++] = block3 >>> 4 & 0x7FL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0xFL) << 3 | block4 >>> 61;
            values[valuesOffset++] = block4 >>> 54 & 0x7FL;
            values[valuesOffset++] = block4 >>> 47 & 0x7FL;
            values[valuesOffset++] = block4 >>> 40 & 0x7FL;
            values[valuesOffset++] = block4 >>> 33 & 0x7FL;
            values[valuesOffset++] = block4 >>> 26 & 0x7FL;
            values[valuesOffset++] = block4 >>> 19 & 0x7FL;
            values[valuesOffset++] = block4 >>> 12 & 0x7FL;
            values[valuesOffset++] = block4 >>> 5 & 0x7FL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x1FL) << 2 | block5 >>> 62;
            values[valuesOffset++] = block5 >>> 55 & 0x7FL;
            values[valuesOffset++] = block5 >>> 48 & 0x7FL;
            values[valuesOffset++] = block5 >>> 41 & 0x7FL;
            values[valuesOffset++] = block5 >>> 34 & 0x7FL;
            values[valuesOffset++] = block5 >>> 27 & 0x7FL;
            values[valuesOffset++] = block5 >>> 20 & 0x7FL;
            values[valuesOffset++] = block5 >>> 13 & 0x7FL;
            values[valuesOffset++] = block5 >>> 6 & 0x7FL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FL) << 1 | block6 >>> 63;
            values[valuesOffset++] = block6 >>> 56 & 0x7FL;
            values[valuesOffset++] = block6 >>> 49 & 0x7FL;
            values[valuesOffset++] = block6 >>> 42 & 0x7FL;
            values[valuesOffset++] = block6 >>> 35 & 0x7FL;
            values[valuesOffset++] = block6 >>> 28 & 0x7FL;
            values[valuesOffset++] = block6 >>> 21 & 0x7FL;
            values[valuesOffset++] = block6 >>> 14 & 0x7FL;
            values[valuesOffset++] = block6 >>> 7 & 0x7FL;
            values[valuesOffset++] = block6 & 0x7FL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 >>> 1;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte0 & 1L) << 6 | byte1 >>> 2;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 3L) << 5 | byte2 >>> 3;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 7L) << 4 | byte3 >>> 4;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0xFL) << 3 | byte4 >>> 5;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x1FL) << 2 | byte5 >>> 6;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0x3FL) << 1 | byte6 >>> 7;
            values[valuesOffset++] = byte6 & 0x7FL;
        }
    }
}

