/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class IntScramble {
    private IntScramble() {
    }

    public static void scramble(int[] data, int offset, int[] permutationTable) {
        for (int k = 0; k < permutationTable.length; k += 2) {
            int i = offset + permutationTable[k];
            int j = offset + permutationTable[k + 1];
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }
}

