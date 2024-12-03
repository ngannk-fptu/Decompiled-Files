/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.LongUtils;

public final class DoubleUtils {
    public static byte[] byteArrayFromDouble(double d) {
        long l = Double.doubleToLongBits(d);
        return LongUtils.byteArrayFromLong(l);
    }

    public static double doubleFromByteArray(byte[] byArray, int n) {
        long l = LongUtils.longFromByteArray(byArray, n);
        return Double.longBitsToDouble(l);
    }
}

