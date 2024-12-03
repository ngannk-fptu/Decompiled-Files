/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked19
extends BulkOperationPacked {
    public BulkOperationPacked19() {
        super(19);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 45);
            values[valuesOffset++] = (int)(block0 >>> 26 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 7 & 0x7FFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x7FL) << 12 | block1 >>> 52);
            values[valuesOffset++] = (int)(block1 >>> 33 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block1 >>> 14 & 0x7FFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x3FFFL) << 5 | block2 >>> 59);
            values[valuesOffset++] = (int)(block2 >>> 40 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 21 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 2 & 0x7FFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 3L) << 17 | block3 >>> 47);
            values[valuesOffset++] = (int)(block3 >>> 28 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 9 & 0x7FFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x1FFL) << 10 | block4 >>> 54);
            values[valuesOffset++] = (int)(block4 >>> 35 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 16 & 0x7FFFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFFFFL) << 3 | block5 >>> 61);
            values[valuesOffset++] = (int)(block5 >>> 42 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 23 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 4 & 0x7FFFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFL) << 15 | block6 >>> 49);
            values[valuesOffset++] = (int)(block6 >>> 30 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 11 & 0x7FFFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x7FFL) << 8 | block7 >>> 56);
            values[valuesOffset++] = (int)(block7 >>> 37 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 18 & 0x7FFFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FFFFL) << 1 | block8 >>> 63);
            values[valuesOffset++] = (int)(block8 >>> 44 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 25 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 6 & 0x7FFFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0x3FL) << 13 | block9 >>> 51);
            values[valuesOffset++] = (int)(block9 >>> 32 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block9 >>> 13 & 0x7FFFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x1FFFL) << 6 | block10 >>> 58);
            values[valuesOffset++] = (int)(block10 >>> 39 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 20 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block10 >>> 1 & 0x7FFFFL);
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 1L) << 18 | block11 >>> 46);
            values[valuesOffset++] = (int)(block11 >>> 27 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block11 >>> 8 & 0x7FFFFL);
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0xFFL) << 11 | block12 >>> 53);
            values[valuesOffset++] = (int)(block12 >>> 34 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block12 >>> 15 & 0x7FFFFL);
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0x7FFFL) << 4 | block13 >>> 60);
            values[valuesOffset++] = (int)(block13 >>> 41 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 22 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block13 >>> 3 & 0x7FFFFL);
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 7L) << 16 | block14 >>> 48);
            values[valuesOffset++] = (int)(block14 >>> 29 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block14 >>> 10 & 0x7FFFFL);
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x3FFL) << 9 | block15 >>> 55);
            values[valuesOffset++] = (int)(block15 >>> 36 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block15 >>> 17 & 0x7FFFFL);
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0x1FFFFL) << 2 | block16 >>> 62);
            values[valuesOffset++] = (int)(block16 >>> 43 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 24 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block16 >>> 5 & 0x7FFFFL);
            long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0x1FL) << 14 | block17 >>> 50);
            values[valuesOffset++] = (int)(block17 >>> 31 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block17 >>> 12 & 0x7FFFFL);
            long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block17 & 0xFFFL) << 7 | block18 >>> 57);
            values[valuesOffset++] = (int)(block18 >>> 38 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block18 >>> 19 & 0x7FFFFL);
            values[valuesOffset++] = (int)(block18 & 0x7FFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 11 | byte1 << 3 | byte2 >>> 5;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x1F) << 14 | byte3 << 6 | byte4 >>> 2;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 3) << 17 | byte5 << 9 | byte6 << 1 | byte7 >>> 7;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 0x7F) << 12 | byte8 << 4 | byte9 >>> 4;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 0xF) << 15 | byte10 << 7 | byte11 >>> 1;
            int byte12 = blocks[blocksOffset++] & 0xFF;
            int byte13 = blocks[blocksOffset++] & 0xFF;
            int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 1) << 18 | byte12 << 10 | byte13 << 2 | byte14 >>> 6;
            int byte15 = blocks[blocksOffset++] & 0xFF;
            int byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 0x3F) << 13 | byte15 << 5 | byte16 >>> 3;
            int byte17 = blocks[blocksOffset++] & 0xFF;
            int byte18 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte16 & 7) << 16 | byte17 << 8 | byte18;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 45;
            values[valuesOffset++] = block0 >>> 26 & 0x7FFFFL;
            values[valuesOffset++] = block0 >>> 7 & 0x7FFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0x7FL) << 12 | block1 >>> 52;
            values[valuesOffset++] = block1 >>> 33 & 0x7FFFFL;
            values[valuesOffset++] = block1 >>> 14 & 0x7FFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x3FFFL) << 5 | block2 >>> 59;
            values[valuesOffset++] = block2 >>> 40 & 0x7FFFFL;
            values[valuesOffset++] = block2 >>> 21 & 0x7FFFFL;
            values[valuesOffset++] = block2 >>> 2 & 0x7FFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 3L) << 17 | block3 >>> 47;
            values[valuesOffset++] = block3 >>> 28 & 0x7FFFFL;
            values[valuesOffset++] = block3 >>> 9 & 0x7FFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0x1FFL) << 10 | block4 >>> 54;
            values[valuesOffset++] = block4 >>> 35 & 0x7FFFFL;
            values[valuesOffset++] = block4 >>> 16 & 0x7FFFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0xFFFFL) << 3 | block5 >>> 61;
            values[valuesOffset++] = block5 >>> 42 & 0x7FFFFL;
            values[valuesOffset++] = block5 >>> 23 & 0x7FFFFL;
            values[valuesOffset++] = block5 >>> 4 & 0x7FFFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0xFL) << 15 | block6 >>> 49;
            values[valuesOffset++] = block6 >>> 30 & 0x7FFFFL;
            values[valuesOffset++] = block6 >>> 11 & 0x7FFFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x7FFL) << 8 | block7 >>> 56;
            values[valuesOffset++] = block7 >>> 37 & 0x7FFFFL;
            values[valuesOffset++] = block7 >>> 18 & 0x7FFFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0x3FFFFL) << 1 | block8 >>> 63;
            values[valuesOffset++] = block8 >>> 44 & 0x7FFFFL;
            values[valuesOffset++] = block8 >>> 25 & 0x7FFFFL;
            values[valuesOffset++] = block8 >>> 6 & 0x7FFFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0x3FL) << 13 | block9 >>> 51;
            values[valuesOffset++] = block9 >>> 32 & 0x7FFFFL;
            values[valuesOffset++] = block9 >>> 13 & 0x7FFFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 0x1FFFL) << 6 | block10 >>> 58;
            values[valuesOffset++] = block10 >>> 39 & 0x7FFFFL;
            values[valuesOffset++] = block10 >>> 20 & 0x7FFFFL;
            values[valuesOffset++] = block10 >>> 1 & 0x7FFFFL;
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (block10 & 1L) << 18 | block11 >>> 46;
            values[valuesOffset++] = block11 >>> 27 & 0x7FFFFL;
            values[valuesOffset++] = block11 >>> 8 & 0x7FFFFL;
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (block11 & 0xFFL) << 11 | block12 >>> 53;
            values[valuesOffset++] = block12 >>> 34 & 0x7FFFFL;
            values[valuesOffset++] = block12 >>> 15 & 0x7FFFFL;
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (block12 & 0x7FFFL) << 4 | block13 >>> 60;
            values[valuesOffset++] = block13 >>> 41 & 0x7FFFFL;
            values[valuesOffset++] = block13 >>> 22 & 0x7FFFFL;
            values[valuesOffset++] = block13 >>> 3 & 0x7FFFFL;
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (block13 & 7L) << 16 | block14 >>> 48;
            values[valuesOffset++] = block14 >>> 29 & 0x7FFFFL;
            values[valuesOffset++] = block14 >>> 10 & 0x7FFFFL;
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (block14 & 0x3FFL) << 9 | block15 >>> 55;
            values[valuesOffset++] = block15 >>> 36 & 0x7FFFFL;
            values[valuesOffset++] = block15 >>> 17 & 0x7FFFFL;
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (block15 & 0x1FFFFL) << 2 | block16 >>> 62;
            values[valuesOffset++] = block16 >>> 43 & 0x7FFFFL;
            values[valuesOffset++] = block16 >>> 24 & 0x7FFFFL;
            values[valuesOffset++] = block16 >>> 5 & 0x7FFFFL;
            long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (block16 & 0x1FL) << 14 | block17 >>> 50;
            values[valuesOffset++] = block17 >>> 31 & 0x7FFFFL;
            values[valuesOffset++] = block17 >>> 12 & 0x7FFFFL;
            long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (block17 & 0xFFFL) << 7 | block18 >>> 57;
            values[valuesOffset++] = block18 >>> 38 & 0x7FFFFL;
            values[valuesOffset++] = block18 >>> 19 & 0x7FFFFL;
            values[valuesOffset++] = block18 & 0x7FFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 11 | byte1 << 3 | byte2 >>> 5;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x1FL) << 14 | byte3 << 6 | byte4 >>> 2;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 3L) << 17 | byte5 << 9 | byte6 << 1 | byte7 >>> 7;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte7 & 0x7FL) << 12 | byte8 << 4 | byte9 >>> 4;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte9 & 0xFL) << 15 | byte10 << 7 | byte11 >>> 1;
            long byte12 = blocks[blocksOffset++] & 0xFF;
            long byte13 = blocks[blocksOffset++] & 0xFF;
            long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 1L) << 18 | byte12 << 10 | byte13 << 2 | byte14 >>> 6;
            long byte15 = blocks[blocksOffset++] & 0xFF;
            long byte16 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 0x3FL) << 13 | byte15 << 5 | byte16 >>> 3;
            long byte17 = blocks[blocksOffset++] & 0xFF;
            long byte18 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte16 & 7L) << 16 | byte17 << 8 | byte18;
        }
    }
}

