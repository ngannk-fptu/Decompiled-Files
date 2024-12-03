/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class LongScramble {
    private LongScramble() {
    }

    public static void scramble(long[] data, int offset, int[] permutationTable) {
        for (int k = 0; k < permutationTable.length; k += 2) {
            int i = offset + permutationTable[k];
            int j = offset + permutationTable[k + 1];
            long tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }
}

