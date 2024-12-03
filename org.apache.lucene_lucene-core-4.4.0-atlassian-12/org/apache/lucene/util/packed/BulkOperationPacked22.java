/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked22
extends BulkOperationPacked {
    public BulkOperationPacked22() {
        super(22);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 42);
            values[valuesOffset++] = (int)(block0 >>> 20 & 0x3FFFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFFFFL) << 2 | block1 >>> 62);
            values[valuesOffset++] = (int)(block1 >>> 40 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block1 >>> 18 & 0x3FFFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 0x3FFFFL) << 4 | block2 >>> 60);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block2 >>> 16 & 0x3FFFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFFFL) << 6 | block3 >>> 58);
            values[valuesOffset++] = (int)(block3 >>> 36 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block3 >>> 14 & 0x3FFFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0x3FFFL) << 8 | block4 >>> 56);
            values[valuesOffset++] = (int)(block4 >>> 34 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block4 >>> 12 & 0x3FFFFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFFFL) << 10 | block5 >>> 54);
            values[valuesOffset++] = (int)(block5 >>> 32 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block5 >>> 10 & 0x3FFFFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FFL) << 12 | block6 >>> 52);
            values[valuesOffset++] = (int)(block6 >>> 30 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block6 >>> 8 & 0x3FFFFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0xFFL) << 14 | block7 >>> 50);
            values[valuesOffset++] = (int)(block7 >>> 28 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block7 >>> 6 & 0x3FFFFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0x3FL) << 16 | block8 >>> 48);
            values[valuesOffset++] = (int)(block8 >>> 26 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block8 >>> 4 & 0x3FFFFFL);
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block8 & 0xFL) << 18 | block9 >>> 46);
            values[valuesOffset++] = (int)(block9 >>> 24 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block9 >>> 2 & 0x3FFFFFL);
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block9 & 3L) << 20 | block10 >>> 44);
            values[valuesOffset++] = (int)(block10 >>> 22 & 0x3FFFFFL);
            values[valuesOffset++] = (int)(block10 & 0x3FFFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 14 | byte1 << 6 | byte2 >>> 2;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 3) << 20 | byte3 << 12 | byte4 << 4 | byte5 >>> 4;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0xF) << 18 | byte6 << 10 | byte7 << 2 | byte8 >>> 6;
            int byte9 = blocks[blocksOffset++] & 0xFF;
            int byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x3F) << 16 | byte9 << 8 | byte10;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 42;
            values[valuesOffset++] = block0 >>> 20 & 0x3FFFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFFFFFL) << 2 | block1 >>> 62;
            values[valuesOffset++] = block1 >>> 40 & 0x3FFFFFL;
            values[valuesOffset++] = block1 >>> 18 & 0x3FFFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 0x3FFFFL) << 4 | block2 >>> 60;
            values[valuesOffset++] = block2 >>> 38 & 0x3FFFFFL;
            values[valuesOffset++] = block2 >>> 16 & 0x3FFFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0xFFFFL) << 6 | block3 >>> 58;
            values[valuesOffset++] = block3 >>> 36 & 0x3FFFFFL;
            values[valuesOffset++] = block3 >>> 14 & 0x3FFFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0x3FFFL) << 8 | block4 >>> 56;
            values[valuesOffset++] = block4 >>> 34 & 0x3FFFFFL;
            values[valuesOffset++] = block4 >>> 12 & 0x3FFFFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0xFFFL) << 10 | block5 >>> 54;
            values[valuesOffset++] = block5 >>> 32 & 0x3FFFFFL;
            values[valuesOffset++] = block5 >>> 10 & 0x3FFFFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FFL) << 12 | block6 >>> 52;
            values[valuesOffset++] = block6 >>> 30 & 0x3FFFFFL;
            values[valuesOffset++] = block6 >>> 8 & 0x3FFFFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0xFFL) << 14 | block7 >>> 50;
            values[valuesOffset++] = block7 >>> 28 & 0x3FFFFFL;
            values[valuesOffset++] = block7 >>> 6 & 0x3FFFFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0x3FL) << 16 | block8 >>> 48;
            values[valuesOffset++] = block8 >>> 26 & 0x3FFFFFL;
            values[valuesOffset++] = block8 >>> 4 & 0x3FFFFFL;
            long block9 = blocks[blocksOffset++];
            values[valuesOffset++] = (block8 & 0xFL) << 18 | block9 >>> 46;
            values[valuesOffset++] = block9 >>> 24 & 0x3FFFFFL;
            values[valuesOffset++] = block9 >>> 2 & 0x3FFFFFL;
            long block10 = blocks[blocksOffset++];
            values[valuesOffset++] = (block9 & 3L) << 20 | block10 >>> 44;
            values[valuesOffset++] = block10 >>> 22 & 0x3FFFFFL;
            values[valuesOffset++] = block10 & 0x3FFFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 14 | byte1 << 6 | byte2 >>> 2;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 3L) << 20 | byte3 << 12 | byte4 << 4 | byte5 >>> 4;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0xFL) << 18 | byte6 << 10 | byte7 << 2 | byte8 >>> 6;
            long byte9 = blocks[blocksOffset++] & 0xFF;
            long byte10 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte8 & 0x3FL) << 16 | byte9 << 8 | byte10;
        }
    }
}

