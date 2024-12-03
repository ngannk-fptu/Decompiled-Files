/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober.statemachine;

public class PkgInt {
    public static final int INDEX_SHIFT_4BITS = 3;
    public static final int INDEX_SHIFT_8BITS = 2;
    public static final int INDEX_SHIFT_16BITS = 1;
    public static final int SHIFT_MASK_4BITS = 7;
    public static final int SHIFT_MASK_8BITS = 3;
    public static final int SHIFT_MASK_16BITS = 1;
    public static final int BIT_SHIFT_4BITS = 2;
    public static final int BIT_SHIFT_8BITS = 3;
    public static final int BIT_SHIFT_16BITS = 4;
    public static final int UNIT_MASK_4BITS = 15;
    public static final int UNIT_MASK_8BITS = 255;
    public static final int UNIT_MASK_16BITS = 65535;
    private int indexShift;
    private int shiftMask;
    private int bitShift;
    private int unitMask;
    private int[] data;

    public PkgInt(int indexShift, int shiftMask, int bitShift, int unitMask, int[] data) {
        this.indexShift = indexShift;
        this.shiftMask = shiftMask;
        this.bitShift = bitShift;
        this.unitMask = unitMask;
        this.data = (int[])data.clone();
    }

    public static int pack16bits(int a, int b) {
        return b << 16 | a;
    }

    public static int pack8bits(int a, int b, int c, int d) {
        return PkgInt.pack16bits(b << 8 | a, d << 8 | c);
    }

    public static int pack4bits(int a, int b, int c, int d, int e, int f, int g, int h) {
        return PkgInt.pack8bits(b << 4 | a, d << 4 | c, f << 4 | e, h << 4 | g);
    }

    public int unpack(int i) {
        return this.data[i >> this.indexShift] >> ((i & this.shiftMask) << this.bitShift) & this.unitMask;
    }
}

