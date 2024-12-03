/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

public class MathUtils {
    private MathUtils() {
    }

    public static boolean sumOverflows(int a, int b) {
        try {
            Math.addExact(a, b);
            return false;
        }
        catch (ArithmeticException x) {
            return true;
        }
    }

    public static long cappedAdd(long a, long b) {
        try {
            return Math.addExact(a, b);
        }
        catch (ArithmeticException x) {
            return Long.MAX_VALUE;
        }
    }

    public static int cappedAdd(int a, int b, int maxValue) {
        try {
            int sum = Math.addExact(a, b);
            return Math.min(sum, maxValue);
        }
        catch (ArithmeticException x) {
            return maxValue;
        }
    }
}

