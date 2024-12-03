/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

public final class XORShiftRandomUtils {
    public static long nextLong(long l) {
        l ^= l << 21;
        l ^= l >>> 35;
        l ^= l << 4;
        return l;
    }

    public static void main(String[] stringArray) {
        int n;
        long l = System.currentTimeMillis();
        int n2 = 100;
        int[] nArray = new int[n2];
        for (n = 0; n < 1000000; ++n) {
            l = XORShiftRandomUtils.nextLong(l);
            int n3 = (int)(Math.abs(l) % (long)n2);
            nArray[n3] = nArray[n3] + 1;
            if (n % 10000 != 0) continue;
            System.out.println(l);
        }
        for (n = 0; n < n2; ++n) {
            if (n != 0) {
                System.out.print(", ");
            }
            System.out.print(n + " -> " + nArray[n]);
        }
        System.out.println();
    }
}

