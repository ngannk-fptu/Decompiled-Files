/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

public class MathUtil {
    public static long factorial(int n) {
        long multi = 1L;
        for (int i = 1; i <= n; ++i) {
            multi *= (long)i;
        }
        return multi;
    }

    public static int log2(int n) {
        int log = 0;
        if ((n & 0xFFFF0000) != 0) {
            n >>>= 16;
            log = 16;
        }
        if (n >= 256) {
            n >>>= 8;
            log += 8;
        }
        if (n >= 16) {
            n >>>= 4;
            log += 4;
        }
        if (n >= 4) {
            n >>>= 2;
            log += 2;
        }
        return log + (n >>> 1);
    }
}

