/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked23
extends BulkOperationPacked {
    public BulkOperationPacked23() {
        super(23);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 41);
            values[valuesOffset++] = (int)(block0 >>> 18 & 0x7FFFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x3FFFFL) << 5 | block1 >>> 59);
            values[valuesOffset++] = (int)(block1 >>> 36 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block1 >>> 13 & 0x7FFFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x1FFFL) << 10 | block2 >>> 54);
            values[valuesOffset++] = (int)(block2 >>> 31 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 8 & 0x7FFFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFL) << 15 | block3 >>> 49);
            values[valuesOffset++] = (int)(block3 >>> 26 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 3 & 0x7FFFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 7L) << 20 | block4 >>> 44);
            values[valuesOffset++] = (int)(block4 >>> 21 & 0x7FFFFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x1FFFFFL) << 2 | block5 >>> 62);
            values[valuesOffset++] = (int)(block5 >>> 39 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block5 >>> 16 & 0x7FFFFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0xFFFFL) << 7 | block6 >>> 57);
            values[valuesOffset++] = (int)(block6 >>> 34 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block6 >>> 11 & 0x7FFFFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0x7FFL) << 12 | block7 >>> 52);
            values[valuesOffset++] = (int)(block7 >>> 29 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block7 >>> 6 & 0x7FFFFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FL) << 17 | block8 >>> 47);
            values[valuesOffset++] = (int)(block8 >>> 24 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block8 >>> 1 & 0x7FFFFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 1L) << 22 | block9 >>> 42);
            values[valuesOffset++] = (int)(block9 >>> 19 & 0x7FFFFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 0x7FFFFL) << 4 | block10 >>> 60);
            values[valuesOffset++] = (int)(block10 >>> 37 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block10 >>> 14 & 0x7FFFFFL);
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block10 & 0x3FFFL) << 9 | block11 >>> 55);
            values[valuesOffset++] = (int)(block11 >>> 32 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block11 >>> 9 & 0x7FFFFFL);
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block11 & 0x1FFL) << 14 | block12 >>> 50);
            values[valuesOffset++] = (int)(block12 >>> 27 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block12 >>> 4 & 0x7FFFFFL);
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block12 & 0xFL) << 19 | block13 >>> 45);
            values[valuesOffset++] = (int)(block13 >>> 22 & 0x7FFFFFL);
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block13 & 0x3FFFFFL) << 1 | block14 >>> 63);
            values[valuesOffset++] = (int)(block14 >>> 40 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block14 >>> 17 & 0x7FFFFFL);
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block14 & 0x1FFFFL) << 6 | block15 >>> 58);
            values[valuesOffset++] = (int)(block15 >>> 35 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block15 >>> 12 & 0x7FFFFFL);
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block15 & 0xFFFL) << 11 | block16 >>> 53);
            values[valuesOffset++] = (int)(block16 >>> 30 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block16 >>> 7 & 0x7FFFFFL);
            long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block16 & 0x7FL) << 16 | block17 >>> 48);
            values[valuesOffset++] = (int)(block17 >>> 25 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block17 >>> 2 & 0x7FFFFFL);
            long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block17 & 3L) << 21 | block18 >>> 43);
            values[valuesOffset++] = (int)(block18 >>> 20 & 0x7FFFFFL);
            long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block18 & 0xFFFFFL) << 3 | block19 >>> 61);
            values[valuesOffset++] = (int)(block19 >>> 38 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block19 >>> 15 & 0x7FFFFFL);
            long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block19 & 0x7FFFL) << 8 | block20 >>> 56);
            values[valuesOffset++] = (int)(block20 >>> 33 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block20 >>> 10 & 0x7FFFFFL);
            long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block20 & 0x3FFL) << 13 | block21 >>> 51);
            values[valuesOffset++] = (int)(block21 >>> 28 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block21 >>> 5 & 0x7FFFFFL);
            long block22 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block21 & 0x1FL) << 18 | block22 >>> 46);
            values[valuesOffset++] = (int)(block22 >>> 23 & 0x7FFFFFL);
            values[valuesOffset++] = (int)(block22 & 0x7FFFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 15 | byte1 << 7 | byte2 >>> 1;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 1) << 22 | byte3 << 14 | byte4 << 6 | byte5 >>> 2;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 3) << 21 | byte6 << 13 | byte7 << 5 | byte8 >>> 3;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            int byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 7) << 20 | byte9 << 12 | byte10 << 4 | byte11 >>> 4;
            int byte12 = blocks[blocksOffset++] & 0xFF;
            int byte13 = blocks[blocksOffset++] & 0xFF;
            int byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0xF) << 19 | byte12 << 11 | byte13 << 3 | byte14 >>> 5;
            int byte15 = blocks[blocksOffset++] & 0xFF;
            int byte16 = blocks[blocksOffset++] & 0xFF;
            int byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 0x1F) << 18 | byte15 << 10 | byte16 << 2 | byte17 >>> 6;
            int byte18 = blocks[blocksOffset++] & 0xFF;
            int byte19 = blocks[blocksOffset++] & 0xFF;
            int byte20 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte17 & 0x3F) << 17 | byte18 << 9 | byte19 << 1 | byte20 >>> 7;
            int byte21 = blocks[blocksOffset++] & 0xFF;
            int byte22 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte20 & 0x7F) << 16 | byte21 << 8 | byte22;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 41;
            values[valuesOffset++] = block0 >>> 18 & 0x7FFFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0x3FFFFL) << 5 | block1 >>> 59;
            values[valuesOffset++] = block1 >>> 36 & 0x7FFFFFL;
            values[valuesOffset++] = block1 >>> 13 & 0x7FFFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x1FFFL) << 10 | block2 >>> 54;
            values[valuesOffset++] = block2 >>> 31 & 0x7FFFFFL;
            values[valuesOffset++] = block2 >>> 8 & 0x7FFFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0xFFL) << 15 | block3 >>> 49;
            values[valuesOffset++] = block3 >>> 26 & 0x7FFFFFL;
            values[valuesOffset++] = block3 >>> 3 & 0x7FFFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 7L) << 20 | block4 >>> 44;
            values[valuesOffset++] = block4 >>> 21 & 0x7FFFFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x1FFFFFL) << 2 | block5 >>> 62;
            values[valuesOffset++] = block5 >>> 39 & 0x7FFFFFL;
            values[valuesOffset++] = block5 >>> 16 & 0x7FFFFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0xFFFFL) << 7 | block6 >>> 57;
            values[valuesOffset++] = block6 >>> 34 & 0x7FFFFFL;
            values[valuesOffset++] = block6 >>> 11 & 0x7FFFFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0x7FFL) << 12 | block7 >>> 52;
            values[valuesOffset++] = block7 >>> 29 & 0x7FFFFFL;
            values[valuesOffset++] = block7 >>> 6 & 0x7FFFFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0x3FL) << 17 | block8 >>> 47;
            values[valuesOffset++] = block8 >>> 24 & 0x7FFFFFL;
            values[valuesOffset++] = block8 >>> 1 & 0x7FFFFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 1L) << 22 | block9 >>> 42;
            values[valuesOffset++] = block9 >>> 19 & 0x7FFFFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 0x7FFFFL) << 4 | block10 >>> 60;
            values[valuesOffset++] = block10 >>> 37 & 0x7FFFFFL;
            values[valuesOffset++] = block10 >>> 14 & 0x7FFFFFL;
            long block11 = blocks[blocksOffset++];
            values[valuesOffset++] = (block10 & 0x3FFFL) << 9 | block11 >>> 55;
            values[valuesOffset++] = block11 >>> 32 & 0x7FFFFFL;
            values[valuesOffset++] = block11 >>> 9 & 0x7FFFFFL;
            long block12 = blocks[blocksOffset++];
            values[valuesOffset++] = (block11 & 0x1FFL) << 14 | block12 >>> 50;
            values[valuesOffset++] = block12 >>> 27 & 0x7FFFFFL;
            values[valuesOffset++] = block12 >>> 4 & 0x7FFFFFL;
            long block13 = blocks[blocksOffset++];
            values[valuesOffset++] = (block12 & 0xFL) << 19 | block13 >>> 45;
            values[valuesOffset++] = block13 >>> 22 & 0x7FFFFFL;
            long block14 = blocks[blocksOffset++];
            values[valuesOffset++] = (block13 & 0x3FFFFFL) << 1 | block14 >>> 63;
            values[valuesOffset++] = block14 >>> 40 & 0x7FFFFFL;
            values[valuesOffset++] = block14 >>> 17 & 0x7FFFFFL;
            long block15 = blocks[blocksOffset++];
            values[valuesOffset++] = (block14 & 0x1FFFFL) << 6 | block15 >>> 58;
            values[valuesOffset++] = block15 >>> 35 & 0x7FFFFFL;
            values[valuesOffset++] = block15 >>> 12 & 0x7FFFFFL;
            long block16 = blocks[blocksOffset++];
            values[valuesOffset++] = (block15 & 0xFFFL) << 11 | block16 >>> 53;
            values[valuesOffset++] = block16 >>> 30 & 0x7FFFFFL;
            values[valuesOffset++] = block16 >>> 7 & 0x7FFFFFL;
            long block17 = blocks[blocksOffset++];
            values[valuesOffset++] = (block16 & 0x7FL) << 16 | block17 >>> 48;
            values[valuesOffset++] = block17 >>> 25 & 0x7FFFFFL;
            values[valuesOffset++] = block17 >>> 2 & 0x7FFFFFL;
            long block18 = blocks[blocksOffset++];
            values[valuesOffset++] = (block17 & 3L) << 21 | block18 >>> 43;
            values[valuesOffset++] = block18 >>> 20 & 0x7FFFFFL;
            long block19 = blocks[blocksOffset++];
            values[valuesOffset++] = (block18 & 0xFFFFFL) << 3 | block19 >>> 61;
            values[valuesOffset++] = block19 >>> 38 & 0x7FFFFFL;
            values[valuesOffset++] = block19 >>> 15 & 0x7FFFFFL;
            long block20 = blocks[blocksOffset++];
            values[valuesOffset++] = (block19 & 0x7FFFL) << 8 | block20 >>> 56;
            values[valuesOffset++] = block20 >>> 33 & 0x7FFFFFL;
            values[valuesOffset++] = block20 >>> 10 & 0x7FFFFFL;
            long block21 = blocks[blocksOffset++];
            values[valuesOffset++] = (block20 & 0x3FFL) << 13 | block21 >>> 51;
            values[valuesOffset++] = block21 >>> 28 & 0x7FFFFFL;
            values[valuesOffset++] = block21 >>> 5 & 0x7FFFFFL;
            long block22 = blocks[blocksOffset++];
            values[valuesOffset++] = (block21 & 0x1FL) << 18 | block22 >>> 46;
            values[valuesOffset++] = block22 >>> 23 & 0x7FFFFFL;
            values[valuesOffset++] = block22 & 0x7FFFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 15 | byte1 << 7 | byte2 >>> 1;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 1L) << 22 | byte3 << 14 | byte4 << 6 | byte5 >>> 2;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 3L) << 21 | byte6 << 13 | byte7 << 5 | byte8 >>> 3;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            long byte11 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 7L) << 20 | byte9 << 12 | byte10 << 4 | byte11 >>> 4;
            long byte12 = blocks[blocksOffset++] & 0xFF;
            long byte13 = blocks[blocksOffset++] & 0xFF;
            long byte14 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte11 & 0xFL) << 19 | byte12 << 11 | byte13 << 3 | byte14 >>> 5;
            long byte15 = blocks[blocksOffset++] & 0xFF;
            long byte16 = blocks[blocksOffset++] & 0xFF;
            long byte17 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte14 & 0x1FL) << 18 | byte15 << 10 | byte16 << 2 | byte17 >>> 6;
            long byte18 = blocks[blocksOffset++] & 0xFF;
            long byte19 = blocks[blocksOffset++] & 0xFF;
            long byte20 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte17 & 0x3FL) << 17 | byte18 << 9 | byte19 << 1 | byte20 >>> 7;
            long byte21 = blocks[blocksOffset++] & 0xFF;
            long byte22 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte20 & 0x7FL) << 16 | byte21 << 8 | byte22;
        }
    }
}

