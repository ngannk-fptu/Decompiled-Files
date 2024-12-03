/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

abstract class Wnaf {
    Wnaf() {
    }

    static void getSignedVar(int[] n, int width, byte[] ws) {
        int[] t = new int[n.length * 2];
        int c = n[n.length - 1] >> 31;
        int i = n.length;
        int tPos = t.length;
        while (--i >= 0) {
            int next = n[i];
            t[--tPos] = next >>> 16 | c << 16;
            t[--tPos] = c = next;
        }
        int lead = 32 - width;
        int j = 0;
        int carry = 0;
        int i2 = 0;
        while (i2 < t.length) {
            int word = t[i2];
            while (j < 16) {
                int word16 = word >>> j;
                int bit = word16 & 1;
                if (bit == carry) {
                    ++j;
                    continue;
                }
                int digit = (word16 | 1) << lead;
                carry = digit >>> 31;
                ws[(i2 << 4) + j] = (byte)(digit >> lead);
                j += width;
            }
            ++i2;
            j -= 16;
        }
    }
}

