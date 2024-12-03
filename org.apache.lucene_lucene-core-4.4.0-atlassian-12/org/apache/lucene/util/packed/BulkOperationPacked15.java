/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked15
extends BulkOperationPacked {
    public BulkOperationPacked15() {
        super(15);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 49);
            values[valuesOffset++] = (int)(block0 >>> 34 & 0x7FFFL);
            values[valuesOffset++] = (int)(block0 >>> 19 & 0x7FFFL);
            values[valuesOffset++] = (int)(block0 >>> 4 & 0x7FFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFL) << 11 | block1 >>> 53);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x7FFFL);
            values[valuesOffset++] = (int)(block1 >>> 23 & 0x7FFFL);
            values[valuesOffset++] = (int)(block1 >>> 8 & 0x7FFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0xFFL) << 7 | block2 >>> 57);
            values[valuesOffset++] = (int)(block2 >>> 42 & 0x7FFFL);
            values[valuesOffset++] = (int)(block2 >>> 27 & 0x7FFFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x7FFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFFL) << 3 | block3 >>> 61);
            values[valuesOffset++] = (int)(block3 >>> 46 & 0x7FFFL);
            values[valuesOffset++] = (int)(block3 >>> 31 & 0x7FFFL);
            values[valuesOffset++] = (int)(block3 >>> 16 & 0x7FFFL);
            values[valuesOffset++] = (int)(block3 >>> 1 & 0x7FFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 1L) << 14 | block4 >>> 50);
            values[valuesOffset++] = (int)(block4 >>> 35 & 0x7FFFL);
            values[valuesOffset++] = (int)(block4 >>> 20 & 0x7FFFL);
            values[valuesOffset++] = (int)(block4 >>> 5 & 0x7FFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (int)(block5 >>> 39 & 0x7FFFL);
            values[valuesOffset++] = (int)(block5 >>> 24 & 0x7FFFL);
            values[valuesOffset++] = (int)(block5 >>> 9 & 0x7FFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x1FFL) << 6 | block6 >>> 58);
            values[valuesOffset++] = (int)(block6 >>> 43 & 0x7FFFL);
            values[valuesOffset++] = (int)(block6 >>> 28 & 0x7FFFL);
            values[valuesOffset++] = (int)(block6 >>> 13 & 0x7FFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x1FFFL) << 2 | block7 >>> 62);
            values[valuesOffset++] = (int)(block7 >>> 47 & 0x7FFFL);
            values[valuesOffset++] = (int)(block7 >>> 32 & 0x7FFFL);
            values[valuesOffset++] = (int)(block7 >>> 17 & 0x7FFFL);
            values[valuesOffset++] = (int)(block7 >>> 2 & 0x7FFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 3L) << 13 | block8 >>> 51);
            values[valuesOffset++] = (int)(block8 >>> 36 & 0x7FFFL);
            values[valuesOffset++] = (int)(block8 >>> 21 & 0x7FFFL);
            values[valuesOffset++] = (int)(block8 >>> 6 & 0x7FFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FL) << 9 | block9 >>> 55);
            values[valuesOffset++] = (int)(block9 >>> 40 & 0x7FFFL);
            values[valuesOffset++] = (int)(block9 >>> 25 & 0x7FFFL);
            values[valuesOffset++] = (int)(block9 >>> 10 & 0x7FFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x3FFL) << 5 | block10 >>> 59);
            values[valuesOffset++] = (int)(block10 >>> 44 & 0x7FFFL);
            values[valuesOffset++] = (int)(block10 >>> 29 & 0x7FFFL);
            values[valuesOffset++] = (int)(block10 >>> 14 & 0x7FFFL);
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3FFFL) << 1 | block11 >>> 63);
            values[valuesOffset++] = (int)(block11 >>> 48 & 0x7FFFL);
            values[valuesOffset++] = (int)(block11 >>> 33 & 0x7FFFL);
            values[valuesOffset++] = (int)(block11 >>> 18 & 0x7FFFL);
            values[valuesOffset++] = (int)(block11 >>> 3 & 0x7FFFL);
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 7L) << 12 | block12 >>> 52);
            values[valuesOffset++] = (int)(block12 >>> 37 & 0x7FFFL);
            values[valuesOffset++] = (int)(block12 >>> 22 & 0x7FFFL);
            values[valuesOffset++] = (int)(block12 >>> 7 & 0x7FFFL);
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x7FL) << 8 | block13 >>> 56);
            values[valuesOffset++] = (int)(block13 >>> 41 & 0x7FFFL);
            values[valuesOffset++] = (int)(block13 >>> 26 & 0x7FFFL);
            values[valuesOffset++] = (int)(block13 >>> 11 & 0x7FFFL);
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0x7FFL) << 4 | block14 >>> 60);
            values[valuesOffset++] = (int)(block14 >>> 45 & 0x7FFFL);
            values[valuesOffset++] = (int)(block14 >>> 30 & 0x7FFFL);
            values[valuesOffset++] = (int)(block14 >>> 15 & 0x7FFFL);
            values[valuesOffset++] = (int)(block14 & 0x7FFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 7 | byte1 >>> 1;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 1) << 14 | byte2 << 6 | byte3 >>> 2;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3) << 13 | byte4 << 5 | byte5 >>> 3;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 7) << 12 | byte6 << 4 | byte7 >>> 4;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 0xF) << 11 | byte8 << 3 | byte9 >>> 5;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 0x1F) << 10 | byte10 << 2 | byte11 >>> 6;
            int byte12 = blocks[blocksOffset++] & 0xFF;
            int byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0x3F) << 9 | byte12 << 1 | byte13 >>> 7;
            int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte13 & 0x7F) << 8 | byte14;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 49;
            values[valuesOffset++] = block0 >>> 34 & 0x7FFFL;
            values[valuesOffset++] = block0 >>> 19 & 0x7FFFL;
            values[valuesOffset++] = block0 >>> 4 & 0x7FFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFL) << 11 | block1 >>> 53;
            values[valuesOffset++] = block1 >>> 38 & 0x7FFFL;
            values[valuesOffset++] = block1 >>> 23 & 0x7FFFL;
            values[valuesOffset++] = block1 >>> 8 & 0x7FFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0xFFL) << 7 | block2 >>> 57;
            values[valuesOffset++] = block2 >>> 42 & 0x7FFFL;
            values[valuesOffset++] = block2 >>> 27 & 0x7FFFL;
            values[valuesOffset++] = block2 >>> 12 & 0x7FFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0xFFFL) << 3 | block3 >>> 61;
            values[valuesOffset++] = block3 >>> 46 & 0x7FFFL;
            values[valuesOffset++] = block3 >>> 31 & 0x7FFFL;
            values[valuesOffset++] = block3 >>> 16 & 0x7FFFL;
            values[valuesOffset++] = block3 >>> 1 & 0x7FFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 1L) << 14 | block4 >>> 50;
            values[valuesOffset++] = block4 >>> 35 & 0x7FFFL;
            values[valuesOffset++] = block4 >>> 20 & 0x7FFFL;
            values[valuesOffset++] = block4 >>> 5 & 0x7FFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x1FL) << 10 | block5 >>> 54;
            values[valuesOffset++] = block5 >>> 39 & 0x7FFFL;
            values[valuesOffset++] = block5 >>> 24 & 0x7FFFL;
            values[valuesOffset++] = block5 >>> 9 & 0x7FFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x1FFL) << 6 | block6 >>> 58;
            values[valuesOffset++] = block6 >>> 43 & 0x7FFFL;
            values[valuesOffset++] = block6 >>> 28 & 0x7FFFL;
            values[valuesOffset++] = block6 >>> 13 & 0x7FFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x1FFFL) << 2 | block7 >>> 62;
            values[valuesOffset++] = block7 >>> 47 & 0x7FFFL;
            values[valuesOffset++] = block7 >>> 32 & 0x7FFFL;
            values[valuesOffset++] = block7 >>> 17 & 0x7FFFL;
            values[valuesOffset++] = block7 >>> 2 & 0x7FFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 3L) << 13 | block8 >>> 51;
            values[valuesOffset++] = block8 >>> 36 & 0x7FFFL;
            values[valuesOffset++] = block8 >>> 21 & 0x7FFFL;
            values[valuesOffset++] = block8 >>> 6 & 0x7FFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0x3FL) << 9 | block9 >>> 55;
            values[valuesOffset++] = block9 >>> 40 & 0x7FFFL;
            values[valuesOffset++] = block9 >>> 25 & 0x7FFFL;
            values[valuesOffset++] = block9 >>> 10 & 0x7FFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 0x3FFL) << 5 | block10 >>> 59;
            values[valuesOffset++] = block10 >>> 44 & 0x7FFFL;
            values[valuesOffset++] = block10 >>> 29 & 0x7FFFL;
            values[valuesOffset++] = block10 >>> 14 & 0x7FFFL;
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (block10 & 0x3FFFL) << 1 | block11 >>> 63;
            values[valuesOffset++] = block11 >>> 48 & 0x7FFFL;
            values[valuesOffset++] = block11 >>> 33 & 0x7FFFL;
            values[valuesOffset++] = block11 >>> 18 & 0x7FFFL;
            values[valuesOffset++] = block11 >>> 3 & 0x7FFFL;
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (block11 & 7L) << 12 | block12 >>> 52;
            values[valuesOffset++] = block12 >>> 37 & 0x7FFFL;
            values[valuesOffset++] = block12 >>> 22 & 0x7FFFL;
            values[valuesOffset++] = block12 >>> 7 & 0x7FFFL;
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (block12 & 0x7FL) << 8 | block13 >>> 56;
            values[valuesOffset++] = block13 >>> 41 & 0x7FFFL;
            values[valuesOffset++] = block13 >>> 26 & 0x7FFFL;
            values[valuesOffset++] = block13 >>> 11 & 0x7FFFL;
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (block13 & 0x7FFL) << 4 | block14 >>> 60;
            values[valuesOffset++] = block14 >>> 45 & 0x7FFFL;
            values[valuesOffset++] = block14 >>> 30 & 0x7FFFL;
            values[valuesOffset++] = block14 >>> 15 & 0x7FFFL;
            values[valuesOffset++] = block14 & 0x7FFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 7 | byte1 >>> 1;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 1L) << 14 | byte2 << 6 | byte3 >>> 2;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 3L) << 13 | byte4 << 5 | byte5 >>> 3;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 7L) << 12 | byte6 << 4 | byte7 >>> 4;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 0xFL) << 11 | byte8 << 3 | byte9 >>> 5;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 0x1FL) << 10 | byte10 << 2 | byte11 >>> 6;
            long byte12 = blocks[blocksOffset++] & 0xFF;
            long byte13 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0x3FL) << 9 | byte12 << 1 | byte13 >>> 7;
            long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte13 & 0x7FL) << 8 | byte14;
        }
    }
}

