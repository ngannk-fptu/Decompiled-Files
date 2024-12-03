/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apfloat.internal.ConcurrentSoftHashMap;
import org.apfloat.internal.LongModConstants;
import org.apfloat.internal.LongModMath;

public class LongWTables
extends LongModMath {
    private static ConcurrentMap<List<Integer>, long[]> cache = new ConcurrentSoftHashMap<List<Integer>, long[]>();

    private LongWTables() {
    }

    public static long[] getWTable(int modulus, int length) {
        return LongWTables.getWTable(modulus, length, false);
    }

    public static long[] getInverseWTable(int modulus, int length) {
        return LongWTables.getWTable(modulus, length, true);
    }

    private static long[] getWTable(int modulus, int length, boolean isInverse) {
        long w;
        LongModMath instance;
        long[] value;
        List<Integer> key = Arrays.asList(isInverse ? 1 : 0, modulus, length);
        long[] wTable = (long[])cache.get(key);
        if (wTable == null && (value = cache.putIfAbsent(key, wTable = (instance = LongWTables.getInstance(modulus)).createWTable(w = isInverse ? instance.getInverseNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length) : instance.getForwardNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length), length))) != null) {
            wTable = value;
        }
        return wTable;
    }

    private static LongModMath getInstance(int modulus) {
        LongModMath instance = new LongModMath();
        instance.setModulus(LongModConstants.MODULUS[modulus]);
        return instance;
    }
}

