/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked17
extends BulkOperationPacked {
    public BulkOperationPacked17() {
        super(17);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 47);
            values[valuesOffset++] = (int)(block0 >>> 30 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 13 & 0x1FFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x1FFFL) << 4 | block1 >>> 60);
            values[valuesOffset++] = (int)(block1 >>> 43 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block1 >>> 26 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block1 >>> 9 & 0x1FFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x1FFL) << 8 | block2 >>> 56);
            values[valuesOffset++] = (int)(block2 >>> 39 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 22 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 5 & 0x1FFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x1FL) << 12 | block3 >>> 52);
            values[valuesOffset++] = (int)(block3 >>> 35 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 18 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 1 & 0x1FFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 1L) << 16 | block4 >>> 48);
            values[valuesOffset++] = (int)(block4 >>> 31 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 14 & 0x1FFFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x3FFFL) << 3 | block5 >>> 61);
            values[valuesOffset++] = (int)(block5 >>> 44 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 27 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 10 & 0x1FFFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FFL) << 7 | block6 >>> 57);
            values[valuesOffset++] = (int)(block6 >>> 40 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 23 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 6 & 0x1FFFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x3FL) << 11 | block7 >>> 53);
            values[valuesOffset++] = (int)(block7 >>> 36 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 19 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 2 & 0x1FFFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 3L) << 15 | block8 >>> 49);
            values[valuesOffset++] = (int)(block8 >>> 32 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 15 & 0x1FFFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x7FFFL) << 2 | block9 >>> 62);
            values[valuesOffset++] = (int)(block9 >>> 45 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 28 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 11 & 0x1FFFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x7FFL) << 6 | block10 >>> 58);
            values[valuesOffset++] = (int)(block10 >>> 41 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 24 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 7 & 0x1FFFFL);
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x7FL) << 10 | block11 >>> 54);
            values[valuesOffset++] = (int)(block11 >>> 37 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 20 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 3 & 0x1FFFFL);
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 7L) << 14 | block12 >>> 50);
            values[valuesOffset++] = (int)(block12 >>> 33 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block12 >>> 16 & 0x1FFFFL);
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0xFFFFL) << 1 | block13 >>> 63);
            values[valuesOffset++] = (int)(block13 >>> 46 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 29 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 12 & 0x1FFFFL);
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0xFFFL) << 5 | block14 >>> 59);
            values[valuesOffset++] = (int)(block14 >>> 42 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 25 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 8 & 0x1FFFFL);
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0xFFL) << 9 | block15 >>> 55);
            values[valuesOffset++] = (int)(block15 >>> 38 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 21 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 4 & 0x1FFFFL);
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0xFL) << 13 | block16 >>> 51);
            values[valuesOffset++] = (int)(block16 >>> 34 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 17 & 0x1FFFFL);
            values[valuesOffset++] = (int)(block16 & 0x1FFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 9 | byte1 << 1 | byte2 >>> 7;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x7F) << 10 | byte3 << 2 | byte4 >>> 6;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x3F) << 11 | byte5 << 3 | byte6 >>> 5;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 0x1F) << 12 | byte7 << 4 | byte8 >>> 4;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0xF) << 13 | byte9 << 5 | byte10 >>> 3;
            int byte11 = blocks[blocksOffset++] & 0xFF;
            int byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte10 & 7) << 14 | byte11 << 6 | byte12 >>> 2;
            int byte13 = blocks[blocksOffset++] & 0xFF;
            int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte12 & 3) << 15 | byte13 << 7 | byte14 >>> 1;
            int byte15 = blocks[blocksOffset++] & 0xFF;
            int byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 1) << 16 | byte15 << 8 | byte16;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 47;
            values[valuesOffset++] = block0 >>> 30 & 0x1FFFFL;
            values[valuesOffset++] = block0 >>> 13 & 0x1FFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0x1FFFL) << 4 | block1 >>> 60;
            values[valuesOffset++] = block1 >>> 43 & 0x1FFFFL;
            values[valuesOffset++] = block1 >>> 26 & 0x1FFFFL;
            values[valuesOffset++] = block1 >>> 9 & 0x1FFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x1FFL) << 8 | block2 >>> 56;
            values[valuesOffset++] = block2 >>> 39 & 0x1FFFFL;
            values[valuesOffset++] = block2 >>> 22 & 0x1FFFFL;
            values[valuesOffset++] = block2 >>> 5 & 0x1FFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0x1FL) << 12 | block3 >>> 52;
            values[valuesOffset++] = block3 >>> 35 & 0x1FFFFL;
            values[valuesOffset++] = block3 >>> 18 & 0x1FFFFL;
            values[valuesOffset++] = block3 >>> 1 & 0x1FFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 1L) << 16 | block4 >>> 48;
            values[valuesOffset++] = block4 >>> 31 & 0x1FFFFL;
            values[valuesOffset++] = block4 >>> 14 & 0x1FFFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x3FFFL) << 3 | block5 >>> 61;
            values[valuesOffset++] = block5 >>> 44 & 0x1FFFFL;
            values[valuesOffset++] = block5 >>> 27 & 0x1FFFFL;
            values[valuesOffset++] = block5 >>> 10 & 0x1FFFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FFL) << 7 | block6 >>> 57;
            values[valuesOffset++] = block6 >>> 40 & 0x1FFFFL;
            values[valuesOffset++] = block6 >>> 23 & 0x1FFFFL;
            values[valuesOffset++] = block6 >>> 6 & 0x1FFFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x3FL) << 11 | block7 >>> 53;
            values[valuesOffset++] = block7 >>> 36 & 0x1FFFFL;
            values[valuesOffset++] = block7 >>> 19 & 0x1FFFFL;
            values[valuesOffset++] = block7 >>> 2 & 0x1FFFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 3L) << 15 | block8 >>> 49;
            values[valuesOffset++] = block8 >>> 32 & 0x1FFFFL;
            values[valuesOffset++] = block8 >>> 15 & 0x1FFFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0x7FFFL) << 2 | block9 >>> 62;
            values[valuesOffset++] = block9 >>> 45 & 0x1FFFFL;
            values[valuesOffset++] = block9 >>> 28 & 0x1FFFFL;
            values[valuesOffset++] = block9 >>> 11 & 0x1FFFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 0x7FFL) << 6 | block10 >>> 58;
            values[valuesOffset++] = block10 >>> 41 & 0x1FFFFL;
            values[valuesOffset++] = block10 >>> 24 & 0x1FFFFL;
            values[valuesOffset++] = block10 >>> 7 & 0x1FFFFL;
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (block10 & 0x7FL) << 10 | block11 >>> 54;
            values[valuesOffset++] = block11 >>> 37 & 0x1FFFFL;
            values[valuesOffset++] = block11 >>> 20 & 0x1FFFFL;
            values[valuesOffset++] = block11 >>> 3 & 0x1FFFFL;
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (block11 & 7L) << 14 | block12 >>> 50;
            values[valuesOffset++] = block12 >>> 33 & 0x1FFFFL;
            values[valuesOffset++] = block12 >>> 16 & 0x1FFFFL;
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (block12 & 0xFFFFL) << 1 | block13 >>> 63;
            values[valuesOffset++] = block13 >>> 46 & 0x1FFFFL;
            values[valuesOffset++] = block13 >>> 29 & 0x1FFFFL;
            values[valuesOffset++] = block13 >>> 12 & 0x1FFFFL;
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (block13 & 0xFFFL) << 5 | block14 >>> 59;
            values[valuesOffset++] = block14 >>> 42 & 0x1FFFFL;
            values[valuesOffset++] = block14 >>> 25 & 0x1FFFFL;
            values[valuesOffset++] = block14 >>> 8 & 0x1FFFFL;
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (block14 & 0xFFL) << 9 | block15 >>> 55;
            values[valuesOffset++] = block15 >>> 38 & 0x1FFFFL;
            values[valuesOffset++] = block15 >>> 21 & 0x1FFFFL;
            values[valuesOffset++] = block15 >>> 4 & 0x1FFFFL;
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (block15 & 0xFL) << 13 | block16 >>> 51;
            values[valuesOffset++] = block16 >>> 34 & 0x1FFFFL;
            values[valuesOffset++] = block16 >>> 17 & 0x1FFFFL;
            values[valuesOffset++] = block16 & 0x1FFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 9 | byte1 << 1 | byte2 >>> 7;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x7FL) << 10 | byte3 << 2 | byte4 >>> 6;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0x3FL) << 11 | byte5 << 3 | byte6 >>> 5;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 0x1FL) << 12 | byte7 << 4 | byte8 >>> 4;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0xFL) << 13 | byte9 << 5 | byte10 >>> 3;
            long byte11 = blocks[blocksOffset++] & 0xFF;
            long byte12 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte10 & 7L) << 14 | byte11 << 6 | byte12 >>> 2;
            long byte13 = blocks[blocksOffset++] & 0xFF;
            long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte12 & 3L) << 15 | byte13 << 7 | byte14 >>> 1;
            long byte15 = blocks[blocksOffset++] & 0xFF;
            long byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 1L) << 16 | byte15 << 8 | byte16;
        }
    }
}

