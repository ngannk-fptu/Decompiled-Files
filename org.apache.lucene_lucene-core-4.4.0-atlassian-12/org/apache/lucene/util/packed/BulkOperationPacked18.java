/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked18
extends BulkOperationPacked {
    public BulkOperationPacked18() {
        super(18);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 46);
            values[valuesOffset++] = (int)(block0 >>> 28 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block0 >>> 10 & 0x3FFFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0x3FFL) << 8 | block1 >>> 56);
            values[valuesOffset++] = (int)(block1 >>> 38 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block1 >>> 20 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block1 >>> 2 & 0x3FFFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 3L) << 16 | block2 >>> 48);
            values[valuesOffset++] = (int)(block2 >>> 30 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block2 >>> 12 & 0x3FFFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0xFFFL) << 6 | block3 >>> 58);
            values[valuesOffset++] = (int)(block3 >>> 40 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 22 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block3 >>> 4 & 0x3FFFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFL) << 14 | block4 >>> 50);
            values[valuesOffset++] = (int)(block4 >>> 32 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block4 >>> 14 & 0x3FFFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0x3FFFL) << 4 | block5 >>> 60);
            values[valuesOffset++] = (int)(block5 >>> 42 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 24 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block5 >>> 6 & 0x3FFFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FL) << 12 | block6 >>> 52);
            values[valuesOffset++] = (int)(block6 >>> 34 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block6 >>> 16 & 0x3FFFFL);
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block6 & 0xFFFFL) << 2 | block7 >>> 62);
            values[valuesOffset++] = (int)(block7 >>> 44 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 26 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block7 >>> 8 & 0x3FFFFL);
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block7 & 0xFFL) << 10 | block8 >>> 54);
            values[valuesOffset++] = (int)(block8 >>> 36 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block8 >>> 18 & 0x3FFFFL);
            values[valuesOffset++] = (int)(block8 & 0x3FFFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 10 | byte1 << 2 | byte2 >>> 6;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x3F) << 12 | byte3 << 4 | byte4 >>> 4;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0xF) << 14 | byte5 << 6 | byte6 >>> 2;
            int byte7 = blocks[blocksOffset++] & 0xFF;
            int byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 3) << 16 | byte7 << 8 | byte8;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 46;
            values[valuesOffset++] = block0 >>> 28 & 0x3FFFFL;
            values[valuesOffset++] = block0 >>> 10 & 0x3FFFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0x3FFL) << 8 | block1 >>> 56;
            values[valuesOffset++] = block1 >>> 38 & 0x3FFFFL;
            values[valuesOffset++] = block1 >>> 20 & 0x3FFFFL;
            values[valuesOffset++] = block1 >>> 2 & 0x3FFFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 3L) << 16 | block2 >>> 48;
            values[valuesOffset++] = block2 >>> 30 & 0x3FFFFL;
            values[valuesOffset++] = block2 >>> 12 & 0x3FFFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0xFFFL) << 6 | block3 >>> 58;
            values[valuesOffset++] = block3 >>> 40 & 0x3FFFFL;
            values[valuesOffset++] = block3 >>> 22 & 0x3FFFFL;
            values[valuesOffset++] = block3 >>> 4 & 0x3FFFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0xFL) << 14 | block4 >>> 50;
            values[valuesOffset++] = block4 >>> 32 & 0x3FFFFL;
            values[valuesOffset++] = block4 >>> 14 & 0x3FFFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0x3FFFL) << 4 | block5 >>> 60;
            values[valuesOffset++] = block5 >>> 42 & 0x3FFFFL;
            values[valuesOffset++] = block5 >>> 24 & 0x3FFFFL;
            values[valuesOffset++] = block5 >>> 6 & 0x3FFFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FL) << 12 | block6 >>> 52;
            values[valuesOffset++] = block6 >>> 34 & 0x3FFFFL;
            values[valuesOffset++] = block6 >>> 16 & 0x3FFFFL;
            long block7 = blocks[blocksOffset++];
            values[valuesOffset++] = (block6 & 0xFFFFL) << 2 | block7 >>> 62;
            values[valuesOffset++] = block7 >>> 44 & 0x3FFFFL;
            values[valuesOffset++] = block7 >>> 26 & 0x3FFFFL;
            values[valuesOffset++] = block7 >>> 8 & 0x3FFFFL;
            long block8 = blocks[blocksOffset++];
            values[valuesOffset++] = (block7 & 0xFFL) << 10 | block8 >>> 54;
            values[valuesOffset++] = block8 >>> 36 & 0x3FFFFL;
            values[valuesOffset++] = block8 >>> 18 & 0x3FFFFL;
            values[valuesOffset++] = block8 & 0x3FFFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 10 | byte1 << 2 | byte2 >>> 6;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte2 & 0x3FL) << 12 | byte3 << 4 | byte4 >>> 4;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte4 & 0xFL) << 14 | byte5 << 6 | byte6 >>> 2;
            long byte7 = blocks[blocksOffset++] & 0xFF;
            long byte8 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte6 & 3L) << 16 | byte7 << 8 | byte8;
        }
    }
}

