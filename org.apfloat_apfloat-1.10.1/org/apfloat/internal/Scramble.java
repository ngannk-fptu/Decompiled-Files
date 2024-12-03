/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.spi.Util;

public class Scramble {
    private Scramble() {
    }

    public static int permute(int n, int length) {
        assert (length == (length & -length));
        int p = 1;
        while (p < length) {
            p += p + (n & 1);
            n >>= 1;
        }
        return p - length;
    }

    public static int[] createScrambleTable(int length) {
        assert (length == (length & -length));
        int[] scrambleTable = new int[length - Util.sqrt4up(length)];
        int k = 0;
        for (int i = 0; i < length; ++i) {
            int j = Scramble.permute(i, length);
            if (j >= i) continue;
            scrambleTable[k] = i;
            scrambleTable[k + 1] = j;
            k += 2;
        }
        return scrambleTable;
    }
}

