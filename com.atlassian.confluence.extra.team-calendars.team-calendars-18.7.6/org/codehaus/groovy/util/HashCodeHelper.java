/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.Arrays;

public class HashCodeHelper {
    private static final int SEED = 127;
    private static final int MULT = 31;

    public static int initHash() {
        return 127;
    }

    public static int updateHash(int current, boolean var) {
        return HashCodeHelper.shift(current) + (var ? 1 : 0);
    }

    public static int updateHash(int current, char var) {
        return HashCodeHelper.shift(current) + var;
    }

    public static int updateHash(int current, Character var) {
        return HashCodeHelper.updateHash(current, var == null ? (char)'\u0000' : var.charValue());
    }

    public static int updateHash(int current, int var) {
        return HashCodeHelper.shift(current) + var;
    }

    public static int updateHash(int current, Integer var) {
        return HashCodeHelper.updateHash(current, var == null ? 0 : var);
    }

    public static int updateHash(int current, long var) {
        return HashCodeHelper.shift(current) + (int)(var ^ var >>> 32);
    }

    public static int updateHash(int current, Long var) {
        return HashCodeHelper.updateHash(current, var == null ? 0L : var);
    }

    public static int updateHash(int current, float var) {
        return HashCodeHelper.updateHash(current, Float.floatToIntBits(var));
    }

    public static int updateHash(int current, Float var) {
        return HashCodeHelper.updateHash(current, var == null ? 0.0f : var.floatValue());
    }

    public static int updateHash(int current, double var) {
        return HashCodeHelper.updateHash(current, Double.doubleToLongBits(var));
    }

    public static int updateHash(int current, Double var) {
        return HashCodeHelper.updateHash(current, var == null ? 0.0 : var);
    }

    public static int updateHash(int current, Object var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        if (var.getClass().isArray()) {
            return HashCodeHelper.shift(current) + Arrays.hashCode((Object[])var);
        }
        return HashCodeHelper.updateHash(current, var.hashCode());
    }

    public static int updateHash(int current, boolean[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, char[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, byte[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, short[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, int[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, long[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, float[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    public static int updateHash(int current, double[] var) {
        if (var == null) {
            return HashCodeHelper.updateHash(current, 0);
        }
        return HashCodeHelper.shift(current) + Arrays.hashCode(var);
    }

    private static int shift(int current) {
        return 31 * current;
    }
}

