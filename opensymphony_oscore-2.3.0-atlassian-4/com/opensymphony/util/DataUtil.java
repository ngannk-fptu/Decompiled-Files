/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

public class DataUtil {
    public static boolean getBoolean(Boolean b) {
        if (b == null) {
            return false;
        }
        return b;
    }

    public static byte getByte(Byte b) {
        if (b == null) {
            return 0;
        }
        return b;
    }

    public static double getDouble(Double d) {
        if (d == null) {
            return 0.0;
        }
        return d;
    }

    public static float getFloat(Float f) {
        if (f == null) {
            return 0.0f;
        }
        return f.floatValue();
    }

    public static int getInt(Integer i) {
        if (i == null) {
            return 0;
        }
        return i;
    }

    public static long getLong(Long l) {
        if (l == null) {
            return 0L;
        }
        return l;
    }
}

