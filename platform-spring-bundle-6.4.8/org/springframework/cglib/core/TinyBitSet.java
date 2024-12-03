/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.core;

@Deprecated
public class TinyBitSet {
    private static int[] T = new int[256];
    private int value = 0;

    private static int gcount(int x) {
        int c = 0;
        while (x != 0) {
            ++c;
            x &= x - 1;
        }
        return c;
    }

    private static int topbit(int i2) {
        int j = 0;
        while (i2 != 0) {
            j = i2 & -i2;
            i2 ^= j;
        }
        return j;
    }

    private static int log2(int i2) {
        int j = 0;
        j = 0;
        while (i2 != 0) {
            ++j;
            i2 >>= 1;
        }
        return j;
    }

    public int length() {
        return TinyBitSet.log2(TinyBitSet.topbit(this.value));
    }

    public int cardinality() {
        int c = 0;
        for (int w = this.value; w != 0; w >>= 8) {
            c += T[w & 0xFF];
        }
        return c;
    }

    public boolean get(int index) {
        return (this.value & 1 << index) != 0;
    }

    public void set(int index) {
        this.value |= 1 << index;
    }

    public void clear(int index) {
        this.value &= ~(1 << index);
    }

    static {
        for (int j = 0; j < 256; ++j) {
            TinyBitSet.T[j] = TinyBitSet.gcount(j);
        }
    }
}

