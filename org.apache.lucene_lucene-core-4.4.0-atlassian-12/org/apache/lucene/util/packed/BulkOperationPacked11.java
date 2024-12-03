/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked11
extends BulkOperationPacked {
    public BulkOperationPacked11() {
        super(11);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 53);
            values[valuesOffset++] = (int)(block0 >>> 42 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 31 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 20 & 0x7FFL);
            values[valuesOffset++] = (int)(block0 >>> 9 & 0x7FFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1FFL) << 2 | block1 >>> 62);
            values[valuesOffset++] = (int)(block1 >>> 51 & 0x7FFL);
            values[valuesOffset++] = (int)(block1 >>> 40 & 0x7FFL);
            values[valuesOffset++] = (int)(block1 >>> 29 & 0x7FFL);
            values[valuesOffset++] = (int)(block1 >>> 18 & 0x7FFL);
            values[valuesOffset++] = (int)(block1 >>> 7 & 0x7FFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x7FL) << 4 | block2 >>> 60);
            values[valuesOffset++] = (int)(block2 >>> 49 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 27 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 16 & 0x7FFL);
            values[valuesOffset++] = (int)(block2 >>> 5 & 0x7FFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x1FL) << 6 | block3 >>> 58);
            values[valuesOffset++] = (int)(block3 >>> 47 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 25 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 14 & 0x7FFL);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x7FFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 7L) << 8 | block4 >>> 56);
            values[valuesOffset++] = (int)(block4 >>> 45 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 34 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 23 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 12 & 0x7FFL);
            values[valuesOffset++] = (int)(block4 >>> 1 & 0x7FFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 1L) << 10 | block5 >>> 54);
            values[valuesOffset++] = (int)(block5 >>> 43 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 32 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 21 & 0x7FFL);
            values[valuesOffset++] = (int)(block5 >>> 10 & 0x7FFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FFL) << 1 | block6 >>> 63);
            values[valuesOffset++] = (int)(block6 >>> 52 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 41 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 30 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 19 & 0x7FFL);
            values[valuesOffset++] = (int)(block6 >>> 8 & 0x7FFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0xFFL) << 3 | block7 >>> 61);
            values[valuesOffset++] = (int)(block7 >>> 50 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 39 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 17 & 0x7FFL);
            values[valuesOffset++] = (int)(block7 >>> 6 & 0x7FFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FL) << 5 | block8 >>> 59);
            values[valuesOffset++] = (int)(block8 >>> 48 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 37 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 26 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 15 & 0x7FFL);
            values[valuesOffset++] = (int)(block8 >>> 4 & 0x7FFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFL) << 7 | block9 >>> 57);
            values[valuesOffset++] = (int)(block9 >>> 46 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 35 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 24 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 13 & 0x7FFL);
            values[valuesOffset++] = (int)(block9 >>> 2 & 0x7FFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 3L) << 9 | block10 >>> 55);
            values[valuesOffset++] = (int)(block10 >>> 44 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 33 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 22 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 >>> 11 & 0x7FFL);
            values[valuesOffset++] = (int)(block10 & 0x7FFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 3 | byte1 >>> 5;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x1F) << 6 | byte2 >>> 2;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 3) << 9 | byte3 << 1 | byte4 >>> 7;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x7F) << 4 | byte5 >>> 4;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0xF) << 7 | byte6 >>> 1;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 1) << 10 | byte7 << 2 | byte8 >>> 6;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x3F) << 5 | byte9 >>> 3;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 7) << 8 | byte10;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 53;
            values[valuesOffset++] = block0 >>> 42 & 0x7FFL;
            values[valuesOffset++] = block0 >>> 31 & 0x7FFL;
            values[valuesOffset++] = block0 >>> 20 & 0x7FFL;
            values[valuesOffset++] = block0 >>> 9 & 0x7FFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0x1FFL) << 2 | block1 >>> 62;
            values[valuesOffset++] = block1 >>> 51 & 0x7FFL;
            values[valuesOffset++] = block1 >>> 40 & 0x7FFL;
            values[valuesOffset++] = block1 >>> 29 & 0x7FFL;
            values[valuesOffset++] = block1 >>> 18 & 0x7FFL;
            values[valuesOffset++] = block1 >>> 7 & 0x7FFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x7FL) << 4 | block2 >>> 60;
            values[valuesOffset++] = block2 >>> 49 & 0x7FFL;
            values[valuesOffset++] = block2 >>> 38 & 0x7FFL;
            values[valuesOffset++] = block2 >>> 27 & 0x7FFL;
            values[valuesOffset++] = block2 >>> 16 & 0x7FFL;
            values[valuesOffset++] = block2 >>> 5 & 0x7FFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0x1FL) << 6 | block3 >>> 58;
            values[valuesOffset++] = block3 >>> 47 & 0x7FFL;
            values[valuesOffset++] = block3 >>> 36 & 0x7FFL;
            values[valuesOffset++] = block3 >>> 25 & 0x7FFL;
            values[valuesOffset++] = block3 >>> 14 & 0x7FFL;
            values[valuesOffset++] = block3 >>> 3 & 0x7FFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 7L) << 8 | block4 >>> 56;
            values[valuesOffset++] = block4 >>> 45 & 0x7FFL;
            values[valuesOffset++] = block4 >>> 34 & 0x7FFL;
            values[valuesOffset++] = block4 >>> 23 & 0x7FFL;
            values[valuesOffset++] = block4 >>> 12 & 0x7FFL;
            values[valuesOffset++] = block4 >>> 1 & 0x7FFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 1L) << 10 | block5 >>> 54;
            values[valuesOffset++] = block5 >>> 43 & 0x7FFL;
            values[valuesOffset++] = block5 >>> 32 & 0x7FFL;
            values[valuesOffset++] = block5 >>> 21 & 0x7FFL;
            values[valuesOffset++] = block5 >>> 10 & 0x7FFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FFL) << 1 | block6 >>> 63;
            values[valuesOffset++] = block6 >>> 52 & 0x7FFL;
            values[valuesOffset++] = block6 >>> 41 & 0x7FFL;
            values[valuesOffset++] = block6 >>> 30 & 0x7FFL;
            values[valuesOffset++] = block6 >>> 19 & 0x7FFL;
            values[valuesOffset++] = block6 >>> 8 & 0x7FFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0xFFL) << 3 | block7 >>> 61;
            values[valuesOffset++] = block7 >>> 50 & 0x7FFL;
            values[valuesOffset++] = block7 >>> 39 & 0x7FFL;
            values[valuesOffset++] = block7 >>> 28 & 0x7FFL;
            values[valuesOffset++] = block7 >>> 17 & 0x7FFL;
            values[valuesOffset++] = block7 >>> 6 & 0x7FFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0x3FL) << 5 | block8 >>> 59;
            values[valuesOffset++] = block8 >>> 48 & 0x7FFL;
            values[valuesOffset++] = block8 >>> 37 & 0x7FFL;
            values[valuesOffset++] = block8 >>> 26 & 0x7FFL;
            values[valuesOffset++] = block8 >>> 15 & 0x7FFL;
            values[valuesOffset++] = block8 >>> 4 & 0x7FFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0xFL) << 7 | block9 >>> 57;
            values[valuesOffset++] = block9 >>> 46 & 0x7FFL;
            values[valuesOffset++] = block9 >>> 35 & 0x7FFL;
            values[valuesOffset++] = block9 >>> 24 & 0x7FFL;
            values[valuesOffset++] = block9 >>> 13 & 0x7FFL;
            values[valuesOffset++] = block9 >>> 2 & 0x7FFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 3L) << 9 | block10 >>> 55;
            values[valuesOffset++] = block10 >>> 44 & 0x7FFL;
            values[valuesOffset++] = block10 >>> 33 & 0x7FFL;
            values[valuesOffset++] = block10 >>> 22 & 0x7FFL;
            values[valuesOffset++] = block10 >>> 11 & 0x7FFL;
            values[valuesOffset++] = block10 & 0x7FFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 3 | byte1 >>> 5;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 0x1FL) << 6 | byte2 >>> 2;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 3L) << 9 | byte3 << 1 | byte4 >>> 7;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x7FL) << 4 | byte5 >>> 4;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0xFL) << 7 | byte6 >>> 1;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 1L) << 10 | byte7 << 2 | byte8 >>> 6;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x3FL) << 5 | byte9 >>> 3;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 7L) << 8 | byte10;
        }
    }
}

