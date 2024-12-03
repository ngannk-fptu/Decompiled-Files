/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DoubleModMath;
import org.apfloat.internal.DoubleScramble;
import org.apfloat.spi.ArrayAccess;

public class DoubleTableFNT
extends DoubleModMath {
    public void tableFNT(ArrayAccess arrayAccess, double[] wTable, int[] permutationTable) throws ApfloatRuntimeException {
        double[] data = arrayAccess.getDoubleData();
        int offset = arrayAccess.getOffset();
        int nn = arrayAccess.getLength();
        assert (nn == (nn & -nn));
        if (nn < 2) {
            return;
        }
        int r = 1;
        for (int mmax = nn >> 1; mmax > 0; mmax >>= 1) {
            int istep = mmax << 1;
            for (int i = offset; i < offset + nn; i += istep) {
                int j = i + mmax;
                double a = data[i];
                double b = data[j];
                data[i] = this.modAdd(a, b);
                data[j] = this.modSubtract(a, b);
            }
            int t = r;
            for (int m = 1; m < mmax; ++m) {
                for (int i = offset + m; i < offset + nn; i += istep) {
                    int j = i + mmax;
                    double a = data[i];
                    double b = data[j];
                    data[i] = this.modAdd(a, b);
                    data[j] = this.modMultiply(wTable[t], this.modSubtract(a, b));
                }
                t += r;
            }
            r <<= 1;
        }
        if (permutationTable != null) {
            DoubleScramble.scramble(data, offset, permutationTable);
        }
    }

    public void inverseTableFNT(ArrayAccess arrayAccess, double[] wTable, int[] permutationTable) throws ApfloatRuntimeException {
        double[] data = arrayAccess.getDoubleData();
        int offset = arrayAccess.getOffset();
        int nn = arrayAccess.getLength();
        assert (nn == (nn & -nn));
        if (nn < 2) {
            return;
        }
        if (permutationTable != null) {
            DoubleScramble.scramble(data, offset, permutationTable);
        }
        int r = nn;
        int mmax = 1;
        while (nn > mmax) {
            int istep = mmax << 1;
            r >>= 1;
            for (int i = offset; i < offset + nn; i += istep) {
                int j = i + mmax;
                double wTemp = data[j];
                data[j] = this.modSubtract(data[i], wTemp);
                data[i] = this.modAdd(data[i], wTemp);
            }
            int t = r;
            for (int m = 1; m < mmax; ++m) {
                for (int i = offset + m; i < offset + nn; i += istep) {
                    int j = i + mmax;
                    double wTemp = this.modMultiply(wTable[t], data[j]);
                    data[j] = this.modSubtract(data[i], wTemp);
                    data[i] = this.modAdd(data[i], wTemp);
                }
                t += r;
            }
            mmax = istep;
        }
    }
}

