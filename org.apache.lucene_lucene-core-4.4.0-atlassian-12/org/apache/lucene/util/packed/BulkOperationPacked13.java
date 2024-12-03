/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked13
extends BulkOperationPacked {
    public BulkOperationPacked13() {
        super(13);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 51);
            values[valuesOffset++] = (int)(block0 >>> 38 & 0x1FFFL);
            values[valuesOffset++] = (int)(block0 >>> 25 & 0x1FFFL);
            values[valuesOffset++] = (int)(block0 >>> 12 & 0x1FFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFL) << 1 | block1 >>> 63);
            values[valuesOffset++] = (int)(block1 >>> 50 & 0x1FFFL);
            values[valuesOffset++] = (int)(block1 >>> 37 & 0x1FFFL);
            values[valuesOffset++] = (int)(block1 >>> 24 & 0x1FFFL);
            values[valuesOffset++] = (int)(block1 >>> 11 & 0x1FFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x7FFL) << 2 | block2 >>> 62);
            values[valuesOffset++] = (int)(block2 >>> 49 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 36 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 23 & 0x1FFFL);
            values[valuesOffset++] = (int)(block2 >>> 10 & 0x1FFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3FFL) << 3 | block3 >>> 61);
            values[valuesOffset++] = (int)(block3 >>> 48 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 35 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x1FFFL);
            values[valuesOffset++] = (int)(block3 >>> 9 & 0x1FFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x1FFL) << 4 | block4 >>> 60);
            values[valuesOffset++] = (int)(block4 >>> 47 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 34 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 21 & 0x1FFFL);
            values[valuesOffset++] = (int)(block4 >>> 8 & 0x1FFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFFL) << 5 | block5 >>> 59);
            values[valuesOffset++] = (int)(block5 >>> 46 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 33 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x1FFFL);
            values[valuesOffset++] = (int)(block5 >>> 7 & 0x1FFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x7FL) << 6 | block6 >>> 58);
            values[valuesOffset++] = (int)(block6 >>> 45 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 32 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 19 & 0x1FFFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x1FFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 7 | block7 >>> 57);
            values[valuesOffset++] = (int)(block7 >>> 44 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 31 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 18 & 0x1FFFL);
            values[valuesOffset++] = (int)(block7 >>> 5 & 0x1FFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x1FL) << 8 | block8 >>> 56);
            values[valuesOffset++] = (int)(block8 >>> 43 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 30 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 17 & 0x1FFFL);
            values[valuesOffset++] = (int)(block8 >>> 4 & 0x1FFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFL) << 9 | block9 >>> 55);
            values[valuesOffset++] = (int)(block9 >>> 42 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 29 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 16 & 0x1FFFL);
            values[valuesOffset++] = (int)(block9 >>> 3 & 0x1FFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 7L) << 10 | block10 >>> 54);
            values[valuesOffset++] = (int)(block10 >>> 41 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 28 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 15 & 0x1FFFL);
            values[valuesOffset++] = (int)(block10 >>> 2 & 0x1FFFL);
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 3L) << 11 | block11 >>> 53);
            values[valuesOffset++] = (int)(block11 >>> 40 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 27 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 14 & 0x1FFFL);
            values[valuesOffset++] = (int)(block11 >>> 1 & 0x1FFFL);
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 1L) << 12 | block12 >>> 52);
            values[valuesOffset++] = (int)(block12 >>> 39 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 >>> 26 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 >>> 13 & 0x1FFFL);
            values[valuesOffset++] = (int)(block12 & 0x1FFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 5 | byte1 >>> 3;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 7) << 10 | byte2 << 2 | byte3 >>> 6;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0x3F) << 7 | byte4 >>> 1;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 1) << 12 | byte5 << 4 | byte6 >>> 4;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 0xF) << 9 | byte7 << 1 | byte8 >>> 7;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x7F) << 6 | byte9 >>> 2;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 3) << 11 | byte10 << 3 | byte11 >>> 5;
            int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0x1F) << 8 | byte12;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 51;
            values[valuesOffset++] = block0 >>> 38 & 0x1FFFL;
            values[valuesOffset++] = block0 >>> 25 & 0x1FFFL;
            values[valuesOffset++] = block0 >>> 12 & 0x1FFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFFFL) << 1 | block1 >>> 63;
            values[valuesOffset++] = block1 >>> 50 & 0x1FFFL;
            values[valuesOffset++] = block1 >>> 37 & 0x1FFFL;
            values[valuesOffset++] = block1 >>> 24 & 0x1FFFL;
            values[valuesOffset++] = block1 >>> 11 & 0x1FFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x7FFL) << 2 | block2 >>> 62;
            values[valuesOffset++] = block2 >>> 49 & 0x1FFFL;
            values[valuesOffset++] = block2 >>> 36 & 0x1FFFL;
            values[valuesOffset++] = block2 >>> 23 & 0x1FFFL;
            values[valuesOffset++] = block2 >>> 10 & 0x1FFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0x3FFL) << 3 | block3 >>> 61;
            values[valuesOffset++] = block3 >>> 48 & 0x1FFFL;
            values[valuesOffset++] = block3 >>> 35 & 0x1FFFL;
            values[valuesOffset++] = block3 >>> 22 & 0x1FFFL;
            values[valuesOffset++] = block3 >>> 9 & 0x1FFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0x1FFL) << 4 | block4 >>> 60;
            values[valuesOffset++] = block4 >>> 47 & 0x1FFFL;
            values[valuesOffset++] = block4 >>> 34 & 0x1FFFL;
            values[valuesOffset++] = block4 >>> 21 & 0x1FFFL;
            values[valuesOffset++] = block4 >>> 8 & 0x1FFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0xFFL) << 5 | block5 >>> 59;
            values[valuesOffset++] = block5 >>> 46 & 0x1FFFL;
            values[valuesOffset++] = block5 >>> 33 & 0x1FFFL;
            values[valuesOffset++] = block5 >>> 20 & 0x1FFFL;
            values[valuesOffset++] = block5 >>> 7 & 0x1FFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x7FL) << 6 | block6 >>> 58;
            values[valuesOffset++] = block6 >>> 45 & 0x1FFFL;
            values[valuesOffset++] = block6 >>> 32 & 0x1FFFL;
            values[valuesOffset++] = block6 >>> 19 & 0x1FFFL;
            values[valuesOffset++] = block6 >>> 6 & 0x1FFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x3FL) << 7 | block7 >>> 57;
            values[valuesOffset++] = block7 >>> 44 & 0x1FFFL;
            values[valuesOffset++] = block7 >>> 31 & 0x1FFFL;
            values[valuesOffset++] = block7 >>> 18 & 0x1FFFL;
            values[valuesOffset++] = block7 >>> 5 & 0x1FFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0x1FL) << 8 | block8 >>> 56;
            values[valuesOffset++] = block8 >>> 43 & 0x1FFFL;
            values[valuesOffset++] = block8 >>> 30 & 0x1FFFL;
            values[valuesOffset++] = block8 >>> 17 & 0x1FFFL;
            values[valuesOffset++] = block8 >>> 4 & 0x1FFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0xFL) << 9 | block9 >>> 55;
            values[valuesOffset++] = block9 >>> 42 & 0x1FFFL;
            values[valuesOffset++] = block9 >>> 29 & 0x1FFFL;
            values[valuesOffset++] = block9 >>> 16 & 0x1FFFL;
            values[valuesOffset++] = block9 >>> 3 & 0x1FFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 7L) << 10 | block10 >>> 54;
            values[valuesOffset++] = block10 >>> 41 & 0x1FFFL;
            values[valuesOffset++] = block10 >>> 28 & 0x1FFFL;
            values[valuesOffset++] = block10 >>> 15 & 0x1FFFL;
            values[valuesOffset++] = block10 >>> 2 & 0x1FFFL;
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (block10 & 3L) << 11 | block11 >>> 53;
            values[valuesOffset++] = block11 >>> 40 & 0x1FFFL;
            values[valuesOffset++] = block11 >>> 27 & 0x1FFFL;
            values[valuesOffset++] = block11 >>> 14 & 0x1FFFL;
            values[valuesOffset++] = block11 >>> 1 & 0x1FFFL;
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (block11 & 1L) << 12 | block12 >>> 52;
            values[valuesOffset++] = block12 >>> 39 & 0x1FFFL;
            values[valuesOffset++] = block12 >>> 26 & 0x1FFFL;
            values[valuesOffset++] = block12 >>> 13 & 0x1FFFL;
            values[valuesOffset++] = block12 & 0x1FFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 5 | byte1 >>> 3;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 7L) << 10 | byte2 << 2 | byte3 >>> 6;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0x3FL) << 7 | byte4 >>> 1;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 1L) << 12 | byte5 << 4 | byte6 >>> 4;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 0xFL) << 9 | byte7 << 1 | byte8 >>> 7;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x7FL) << 6 | byte9 >>> 2;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 3L) << 11 | byte10 << 3 | byte11 >>> 5;
            long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0x1FL) << 8 | byte12;
        }
    }
}

