/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.util.Arrays;
import java.util.function.BiConsumer;

public enum HwmfBinaryRasterOp {
    R2_BLACK(1, HwmfBinaryRasterOp::R2_BLACK),
    R2_NOTMERGEPEN(2, HwmfBinaryRasterOp::R2_NOTMERGEPEN),
    R2_MASKNOTPEN(3, HwmfBinaryRasterOp::R2_MASKNOTPEN),
    R2_NOTCOPYPEN(4, HwmfBinaryRasterOp::R2_NOTCOPYPEN),
    R2_MASKPENNOT(5, HwmfBinaryRasterOp::R2_MASKPENNOT),
    R2_NOT(6, HwmfBinaryRasterOp::R2_NOT),
    R2_XORPEN(7, HwmfBinaryRasterOp::R2_XORPEN),
    R2_NOTMASKPEN(8, HwmfBinaryRasterOp::R2_NOTMASKPEN),
    R2_MASKPEN(9, HwmfBinaryRasterOp::R2_MASKPEN),
    R2_NOTXORPEN(10, HwmfBinaryRasterOp::R2_NOTXORPEN),
    R2_NOP(11, HwmfBinaryRasterOp::R2_NOP),
    R2_MERGENOTPEN(12, HwmfBinaryRasterOp::R2_MERGENOTPEN),
    R2_COPYPEN(13, HwmfBinaryRasterOp::R2_COPYPEN),
    R2_MERGEPENNOT(14, HwmfBinaryRasterOp::R2_MERGEPENNOT),
    R2_MERGEPEN(15, HwmfBinaryRasterOp::R2_MERGEPEN),
    R2_WHITE(16, HwmfBinaryRasterOp::R2_WHITE);

    private final int opIndex;
    private final BiConsumer<int[], int[]> op;

    private HwmfBinaryRasterOp(int opIndex, BiConsumer<int[], int[]> op) {
        this.opIndex = opIndex;
        this.op = op;
    }

    public int getOpIndex() {
        return this.opIndex;
    }

    public static HwmfBinaryRasterOp valueOf(int opIndex) {
        for (HwmfBinaryRasterOp bb : HwmfBinaryRasterOp.values()) {
            if (bb.opIndex != opIndex) continue;
            return bb;
        }
        return null;
    }

    public void process(int[] srcPixels, int[] dstPixels) {
        this.op.accept(srcPixels, dstPixels);
    }

    private static void R2_BLACK(int[] srcPixels, int[] dstPixels) {
        Arrays.fill(dstPixels, -16777216);
    }

    private static void R2_NOTMERGEPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | ~(dstPixels[x] | srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_MASKNOTPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | dstPixels[x] & ~srcPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_NOTCOPYPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | ~srcPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_MASKPENNOT(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | srcPixels[x] & ~dstPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_NOT(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | ~dstPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_XORPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | (dstPixels[x] ^ srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_NOTMASKPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | ~(dstPixels[x] & srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_MASKPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | dstPixels[x] & srcPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_NOTXORPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | ~(dstPixels[x] ^ srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_NOP(int[] srcPixels, int[] dstPixels) {
    }

    private static void R2_MERGENOTPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | (dstPixels[x] | ~srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_COPYPEN(int[] srcPixels, int[] dstPixels) {
        System.arraycopy(srcPixels, 0, dstPixels, 0, srcPixels.length);
    }

    private static void R2_MERGEPENNOT(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | srcPixels[x] & ~dstPixels[x] & 0xFFFFFF;
        }
    }

    private static void R2_MERGEPEN(int[] srcPixels, int[] dstPixels) {
        for (int x = 0; x < srcPixels.length; ++x) {
            dstPixels[x] = dstPixels[x] & 0xFF000000 | (dstPixels[x] | srcPixels[x]) & 0xFFFFFF;
        }
    }

    private static void R2_WHITE(int[] srcPixels, int[] dstPixels) {
        Arrays.fill(dstPixels, -1);
    }
}

