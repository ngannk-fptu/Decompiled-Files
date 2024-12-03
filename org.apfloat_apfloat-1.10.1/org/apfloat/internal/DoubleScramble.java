/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

public class DoubleScramble {
    private DoubleScramble() {
    }

    public static void scramble(double[] data, int offset, int[] permutationTable) {
        for (int k = 0; k < permutationTable.length; k += 2) {
            int i = offset + permutationTable[k];
            int j = offset + permutationTable[k + 1];
            double tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }
}

