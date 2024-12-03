/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperationPacked;
import org.apache.lucene.util.packed.BulkOperationPacked1;
import org.apache.lucene.util.packed.BulkOperationPacked10;
import org.apache.lucene.util.packed.BulkOperationPacked11;
import org.apache.lucene.util.packed.BulkOperationPacked12;
import org.apache.lucene.util.packed.BulkOperationPacked13;
import org.apache.lucene.util.packed.BulkOperationPacked14;
import org.apache.lucene.util.packed.BulkOperationPacked15;
import org.apache.lucene.util.packed.BulkOperationPacked16;
import org.apache.lucene.util.packed.BulkOperationPacked17;
import org.apache.lucene.util.packed.BulkOperationPacked18;
import org.apache.lucene.util.packed.BulkOperationPacked19;
import org.apache.lucene.util.packed.BulkOperationPacked2;
import org.apache.lucene.util.packed.BulkOperationPacked20;
import org.apache.lucene.util.packed.BulkOperationPacked21;
import org.apache.lucene.util.packed.BulkOperationPacked22;
import org.apache.lucene.util.packed.BulkOperationPacked23;
import org.apache.lucene.util.packed.BulkOperationPacked24;
import org.apache.lucene.util.packed.BulkOperationPacked3;
import org.apache.lucene.util.packed.BulkOperationPacked4;
import org.apache.lucene.util.packed.BulkOperationPacked5;
import org.apache.lucene.util.packed.BulkOperationPacked6;
import org.apache.lucene.util.packed.BulkOperationPacked7;
import org.apache.lucene.util.packed.BulkOperationPacked8;
import org.apache.lucene.util.packed.BulkOperationPacked9;
import org.apache.lucene.util.packed.BulkOperationPackedSingleBlock;
import org.apache.lucene.util.packed.PackedInts;

abstract class BulkOperation
implements PackedInts.Decoder,
PackedInts.Encoder {
    private static final BulkOperation[] packedBulkOps = new BulkOperation[]{new BulkOperationPacked1(), new BulkOperationPacked2(), new BulkOperationPacked3(), new BulkOperationPacked4(), new BulkOperationPacked5(), new BulkOperationPacked6(), new BulkOperationPacked7(), new BulkOperationPacked8(), new BulkOperationPacked9(), new BulkOperationPacked10(), new BulkOperationPacked11(), new BulkOperationPacked12(), new BulkOperationPacked13(), new BulkOperationPacked14(), new BulkOperationPacked15(), new BulkOperationPacked16(), new BulkOperationPacked17(), new BulkOperationPacked18(), new BulkOperationPacked19(), new BulkOperationPacked20(), new BulkOperationPacked21(), new BulkOperationPacked22(), new BulkOperationPacked23(), new BulkOperationPacked24(), new BulkOperationPacked(25), new BulkOperationPacked(26), new BulkOperationPacked(27), new BulkOperationPacked(28), new BulkOperationPacked(29), new BulkOperationPacked(30), new BulkOperationPacked(31), new BulkOperationPacked(32), new BulkOperationPacked(33), new BulkOperationPacked(34), new BulkOperationPacked(35), new BulkOperationPacked(36), new BulkOperationPacked(37), new BulkOperationPacked(38), new BulkOperationPacked(39), new BulkOperationPacked(40), new BulkOperationPacked(41), new BulkOperationPacked(42), new BulkOperationPacked(43), new BulkOperationPacked(44), new BulkOperationPacked(45), new BulkOperationPacked(46), new BulkOperationPacked(47), new BulkOperationPacked(48), new BulkOperationPacked(49), new BulkOperationPacked(50), new BulkOperationPacked(51), new BulkOperationPacked(52), new BulkOperationPacked(53), new BulkOperationPacked(54), new BulkOperationPacked(55), new BulkOperationPacked(56), new BulkOperationPacked(57), new BulkOperationPacked(58), new BulkOperationPacked(59), new BulkOperationPacked(60), new BulkOperationPacked(61), new BulkOperationPacked(62), new BulkOperationPacked(63), new BulkOperationPacked(64)};
    private static final BulkOperation[] packedSingleBlockBulkOps = new BulkOperation[]{new BulkOperationPackedSingleBlock(1), new BulkOperationPackedSingleBlock(2), new BulkOperationPackedSingleBlock(3), new BulkOperationPackedSingleBlock(4), new BulkOperationPackedSingleBlock(5), new BulkOperationPackedSingleBlock(6), new BulkOperationPackedSingleBlock(7), new BulkOperationPackedSingleBlock(8), new BulkOperationPackedSingleBlock(9), new BulkOperationPackedSingleBlock(10), null, new BulkOperationPackedSingleBlock(12), null, null, null, new BulkOperationPackedSingleBlock(16), null, null, null, null, new BulkOperationPackedSingleBlock(21), null, null, null, null, null, null, null, null, null, null, new BulkOperationPackedSingleBlock(32)};

    BulkOperation() {
    }

    public static BulkOperation of(PackedInts.Format format, int bitsPerValue) {
        switch (format) {
            case PACKED: {
                assert (packedBulkOps[bitsPerValue - 1] != null);
                return packedBulkOps[bitsPerValue - 1];
            }
            case PACKED_SINGLE_BLOCK: {
                assert (packedSingleBlockBulkOps[bitsPerValue - 1] != null);
                return packedSingleBlockBulkOps[bitsPerValue - 1];
            }
        }
        throw new AssertionError();
    }

    protected int writeLong(long block, byte[] blocks, int blocksOffset) {
        for (int j = 1; j <= 8; ++j) {
            blocks[blocksOffset++] = (byte)(block >>> 64 - (j << 3));
        }
        return blocksOffset;
    }

    public final int computeIterations(int valueCount, int ramBudget) {
        int iterations = ramBudget / (this.byteBlockCount() + 8 * this.byteValueCount());
        if (iterations == 0) {
            return 1;
        }
        if ((iterations - 1) * this.byteValueCount() >= valueCount) {
            return (int)Math.ceil((double)valueCount / (double)this.byteValueCount());
        }
        return iterations;
    }
}

