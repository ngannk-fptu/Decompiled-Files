/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked9
extends BulkOperationPacked {
    public BulkOperationPacked9() {
        super(9);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 55);
            values[valuesOffset++] = (int)(block0 >>> 46 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 37 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x1FFL);
            values[valuesOffset++] = (int)(block0 >>> 1 & 0x1FFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 1L) << 8 | block1 >>> 56);
            values[valuesOffset++] = (int)(block1 >>> 47 & 0x1FFL);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x1FFL);
            values[valuesOffset++] = (int)(block1 >>> 29 & 0x1FFL);
            values[valuesOffset++] = (int)(block1 >>> 20 & 0x1FFL);
            values[valuesOffset++] = (int)(block1 >>> 11 & 0x1FFL);
            values[valuesOffset++] = (int)(block1 >>> 2 & 0x1FFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 3L) << 7 | block2 >>> 57);
            values[valuesOffset++] = (int)(block2 >>> 48 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 39 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 30 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 21 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x1FFL);
            values[valuesOffset++] = (int)(block2 >>> 3 & 0x1FFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 7L) << 6 | block3 >>> 58);
            values[valuesOffset++] = (int)(block3 >>> 49 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 40 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 31 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 13 & 0x1FFL);
            values[valuesOffset++] = (int)(block3 >>> 4 & 0x1FFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFL) << 5 | block4 >>> 59);
            values[valuesOffset++] = (int)(block4 >>> 50 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 41 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 32 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 23 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 14 & 0x1FFL);
            values[valuesOffset++] = (int)(block4 >>> 5 & 0x1FFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 51 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 42 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 33 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 24 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 15 & 0x1FFL);
            values[valuesOffset++] = (int)(block5 >>> 6 & 0x1FFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FL) << 3 | block6 >>> 61);
            values[valuesOffset++] = (int)(block6 >>> 52 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 43 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 34 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 25 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 16 & 0x1FFL);
            values[valuesOffset++] = (int)(block6 >>> 7 & 0x1FFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x7FL) << 2 | block7 >>> 62);
            values[valuesOffset++] = (int)(block7 >>> 53 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 44 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 35 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 26 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 17 & 0x1FFL);
            values[valuesOffset++] = (int)(block7 >>> 8 & 0x1FFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0xFFL) << 1 | block8 >>> 63);
            values[valuesOffset++] = (int)(block8 >>> 54 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 45 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 36 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 27 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 18 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 >>> 9 & 0x1FFL);
            values[valuesOffset++] = (int)(block8 & 0x1FFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 1 | byte1 >>> 7;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x7F) << 2 | byte2 >>> 6;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x3F) << 3 | byte3 >>> 5;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0x1F) << 4 | byte4 >>> 4;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0xF) << 5 | byte5 >>> 3;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 7) << 6 | byte6 >>> 2;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 3) << 7 | byte7 >>> 1;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 1) << 8 | byte8;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 55;
            values[valuesOffset++] = block0 >>> 46 & 0x1FFL;
            values[valuesOffset++] = block0 >>> 37 & 0x1FFL;
            values[valuesOffset++] = block0 >>> 28 & 0x1FFL;
            values[valuesOffset++] = block0 >>> 19 & 0x1FFL;
            values[valuesOffset++] = block0 >>> 10 & 0x1FFL;
            values[valuesOffset++] = block0 >>> 1 & 0x1FFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 1L) << 8 | block1 >>> 56;
            values[valuesOffset++] = block1 >>> 47 & 0x1FFL;
            values[valuesOffset++] = block1 >>> 38 & 0x1FFL;
            values[valuesOffset++] = block1 >>> 29 & 0x1FFL;
            values[valuesOffset++] = block1 >>> 20 & 0x1FFL;
            values[valuesOffset++] = block1 >>> 11 & 0x1FFL;
            values[valuesOffset++] = block1 >>> 2 & 0x1FFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 3L) << 7 | block2 >>> 57;
            values[valuesOffset++] = block2 >>> 48 & 0x1FFL;
            values[valuesOffset++] = block2 >>> 39 & 0x1FFL;
            values[valuesOffset++] = block2 >>> 30 & 0x1FFL;
            values[valuesOffset++] = block2 >>> 21 & 0x1FFL;
            values[valuesOffset++] = block2 >>> 12 & 0x1FFL;
            values[valuesOffset++] = block2 >>> 3 & 0x1FFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 7L) << 6 | block3 >>> 58;
            values[valuesOffset++] = block3 >>> 49 & 0x1FFL;
            values[valuesOffset++] = block3 >>> 40 & 0x1FFL;
            values[valuesOffset++] = block3 >>> 31 & 0x1FFL;
            values[valuesOffset++] = block3 >>> 22 & 0x1FFL;
            values[valuesOffset++] = block3 >>> 13 & 0x1FFL;
            values[valuesOffset++] = block3 >>> 4 & 0x1FFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0xFL) << 5 | block4 >>> 59;
            values[valuesOffset++] = block4 >>> 50 & 0x1FFL;
            values[valuesOffset++] = block4 >>> 41 & 0x1FFL;
            values[valuesOffset++] = block4 >>> 32 & 0x1FFL;
            values[valuesOffset++] = block4 >>> 23 & 0x1FFL;
            values[valuesOffset++] = block4 >>> 14 & 0x1FFL;
            values[valuesOffset++] = block4 >>> 5 & 0x1FFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x1FL) << 4 | block5 >>> 60;
            values[valuesOffset++] = block5 >>> 51 & 0x1FFL;
            values[valuesOffset++] = block5 >>> 42 & 0x1FFL;
            values[valuesOffset++] = block5 >>> 33 & 0x1FFL;
            values[valuesOffset++] = block5 >>> 24 & 0x1FFL;
            values[valuesOffset++] = block5 >>> 15 & 0x1FFL;
            values[valuesOffset++] = block5 >>> 6 & 0x1FFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FL) << 3 | block6 >>> 61;
            values[valuesOffset++] = block6 >>> 52 & 0x1FFL;
            values[valuesOffset++] = block6 >>> 43 & 0x1FFL;
            values[valuesOffset++] = block6 >>> 34 & 0x1FFL;
            values[valuesOffset++] = block6 >>> 25 & 0x1FFL;
            values[valuesOffset++] = block6 >>> 16 & 0x1FFL;
            values[valuesOffset++] = block6 >>> 7 & 0x1FFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x7FL) << 2 | block7 >>> 62;
            values[valuesOffset++] = block7 >>> 53 & 0x1FFL;
            values[valuesOffset++] = block7 >>> 44 & 0x1FFL;
            values[valuesOffset++] = block7 >>> 35 & 0x1FFL;
            values[valuesOffset++] = block7 >>> 26 & 0x1FFL;
            values[valuesOffset++] = block7 >>> 17 & 0x1FFL;
            values[valuesOffset++] = block7 >>> 8 & 0x1FFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0xFFL) << 1 | block8 >>> 63;
            values[valuesOffset++] = block8 >>> 54 & 0x1FFL;
            values[valuesOffset++] = block8 >>> 45 & 0x1FFL;
            values[valuesOffset++] = block8 >>> 36 & 0x1FFL;
            values[valuesOffset++] = block8 >>> 27 & 0x1FFL;
            values[valuesOffset++] = block8 >>> 18 & 0x1FFL;
            values[valuesOffset++] = block8 >>> 9 & 0x1FFL;
            values[valuesOffset++] = block8 & 0x1FFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 1 | byte1 >>> 7;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x7FL) << 2 | byte2 >>> 6;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x3FL) << 3 | byte3 >>> 5;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0x1FL) << 4 | byte4 >>> 4;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0xFL) << 5 | byte5 >>> 3;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 7L) << 6 | byte6 >>> 2;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 3L) << 7 | byte7 >>> 1;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 1L) << 8 | byte8;
        }
    }
}

