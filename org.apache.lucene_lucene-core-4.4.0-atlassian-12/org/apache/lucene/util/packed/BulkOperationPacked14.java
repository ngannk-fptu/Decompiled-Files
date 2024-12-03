/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;

final class BulkOperationPacked14
extends BulkOperationPacked {
    public BulkOperationPacked14() {
        super(14);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)(block0 >>> 50);
            values[valuesOffset++] = (int)(block0 >>> 36 & 0x3FFFL);
            values[valuesOffset++] = (int)(block0 >>> 22 & 0x3FFFL);
            values[valuesOffset++] = (int)(block0 >>> 8 & 0x3FFFL);
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block0 & 0xFFL) << 6 | block1 >>> 58);
            values[valuesOffset++] = (int)(block1 >>> 44 & 0x3FFFL);
            values[valuesOffset++] = (int)(block1 >>> 30 & 0x3FFFL);
            values[valuesOffset++] = (int)(block1 >>> 16 & 0x3FFFL);
            values[valuesOffset++] = (int)(block1 >>> 2 & 0x3FFFL);
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block1 & 3L) << 12 | block2 >>> 52);
            values[valuesOffset++] = (int)(block2 >>> 38 & 0x3FFFL);
            values[valuesOffset++] = (int)(block2 >>> 24 & 0x3FFFL);
            values[valuesOffset++] = (int)(block2 >>> 10 & 0x3FFFL);
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block2 & 0x3FFL) << 4 | block3 >>> 60);
            values[valuesOffset++] = (int)(block3 >>> 46 & 0x3FFFL);
            values[valuesOffset++] = (int)(block3 >>> 32 & 0x3FFFL);
            values[valuesOffset++] = (int)(block3 >>> 18 & 0x3FFFL);
            values[valuesOffset++] = (int)(block3 >>> 4 & 0x3FFFL);
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block3 & 0xFL) << 10 | block4 >>> 54);
            values[valuesOffset++] = (int)(block4 >>> 40 & 0x3FFFL);
            values[valuesOffset++] = (int)(block4 >>> 26 & 0x3FFFL);
            values[valuesOffset++] = (int)(block4 >>> 12 & 0x3FFFL);
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block4 & 0xFFFL) << 2 | block5 >>> 62);
            values[valuesOffset++] = (int)(block5 >>> 48 & 0x3FFFL);
            values[valuesOffset++] = (int)(block5 >>> 34 & 0x3FFFL);
            values[valuesOffset++] = (int)(block5 >>> 20 & 0x3FFFL);
            values[valuesOffset++] = (int)(block5 >>> 6 & 0x3FFFL);
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (int)((block5 & 0x3FL) << 8 | block6 >>> 56);
            values[valuesOffset++] = (int)(block6 >>> 42 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 >>> 28 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 >>> 14 & 0x3FFFL);
            values[valuesOffset++] = (int)(block6 & 0x3FFFL);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            int byte0 = blocks[blocksOffset++] & 0xFF;
            int byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 6 | byte1 >>> 2;
            int byte2 = blocks[blocksOffset++] & 0xFF;
            int byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 3) << 12 | byte2 << 4 | byte3 >>> 4;
            int byte4 = blocks[blocksOffset++] & 0xFF;
            int byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0xF) << 10 | byte4 << 2 | byte5 >>> 6;
            int byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0x3F) << 8 | byte6;
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block0 = blocks[blocksOffset++];
            values[valuesOffset++] = block0 >>> 50;
            values[valuesOffset++] = block0 >>> 36 & 0x3FFFL;
            values[valuesOffset++] = block0 >>> 22 & 0x3FFFL;
            values[valuesOffset++] = block0 >>> 8 & 0x3FFFL;
            long block1 = blocks[blocksOffset++];
            values[valuesOffset++] = (block0 & 0xFFL) << 6 | block1 >>> 58;
            values[valuesOffset++] = block1 >>> 44 & 0x3FFFL;
            values[valuesOffset++] = block1 >>> 30 & 0x3FFFL;
            values[valuesOffset++] = block1 >>> 16 & 0x3FFFL;
            values[valuesOffset++] = block1 >>> 2 & 0x3FFFL;
            long block2 = blocks[blocksOffset++];
            values[valuesOffset++] = (block1 & 3L) << 12 | block2 >>> 52;
            values[valuesOffset++] = block2 >>> 38 & 0x3FFFL;
            values[valuesOffset++] = block2 >>> 24 & 0x3FFFL;
            values[valuesOffset++] = block2 >>> 10 & 0x3FFFL;
            long block3 = blocks[blocksOffset++];
            values[valuesOffset++] = (block2 & 0x3FFL) << 4 | block3 >>> 60;
            values[valuesOffset++] = block3 >>> 46 & 0x3FFFL;
            values[valuesOffset++] = block3 >>> 32 & 0x3FFFL;
            values[valuesOffset++] = block3 >>> 18 & 0x3FFFL;
            values[valuesOffset++] = block3 >>> 4 & 0x3FFFL;
            long block4 = blocks[blocksOffset++];
            values[valuesOffset++] = (block3 & 0xFL) << 10 | block4 >>> 54;
            values[valuesOffset++] = block4 >>> 40 & 0x3FFFL;
            values[valuesOffset++] = block4 >>> 26 & 0x3FFFL;
            values[valuesOffset++] = block4 >>> 12 & 0x3FFFL;
            long block5 = blocks[blocksOffset++];
            values[valuesOffset++] = (block4 & 0xFFFL) << 2 | block5 >>> 62;
            values[valuesOffset++] = block5 >>> 48 & 0x3FFFL;
            values[valuesOffset++] = block5 >>> 34 & 0x3FFFL;
            values[valuesOffset++] = block5 >>> 20 & 0x3FFFL;
            values[valuesOffset++] = block5 >>> 6 & 0x3FFFL;
            long block6 = blocks[blocksOffset++];
            values[valuesOffset++] = (block5 & 0x3FL) << 8 | block6 >>> 56;
            values[valuesOffset++] = block6 >>> 42 & 0x3FFFL;
            values[valuesOffset++] = block6 >>> 28 & 0x3FFFL;
            values[valuesOffset++] = block6 >>> 14 & 0x3FFFL;
            values[valuesOffset++] = block6 & 0x3FFFL;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long byte0 = blocks[blocksOffset++] & 0xFF;
            long byte1 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = byte0 << 6 | byte1 >>> 2;
            long byte2 = blocks[blocksOffset++] & 0xFF;
            long byte3 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte1 & 3L) << 12 | byte2 << 4 | byte3 >>> 4;
            long byte4 = blocks[blocksOffset++] & 0xFF;
            long byte5 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte3 & 0xFL) << 10 | byte4 << 2 | byte5 >>> 6;
            long byte6 = blocks[blocksOffset++] & 0xFF;
            values[valuesOffset++] = (byte5 & 0x3FL) << 8 | byte6;
        }
    }
}

