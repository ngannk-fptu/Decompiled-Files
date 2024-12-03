/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apfloat.internal.ConcurrentSoftHashMap;
import org.apfloat.internal.IntModConstants;
import org.apfloat.internal.IntModMath;

public class IntWTables
extends IntModMath {
    private static ConcurrentMap<List<Integer>, int[]> cache = new ConcurrentSoftHashMap<List<Integer>, int[]>();

    private IntWTables() {
    }

    public static int[] getWTable(int modulus, int length) {
        return IntWTables.getWTable(modulus, length, false);
    }

    public static int[] getInverseWTable(int modulus, int length) {
        return IntWTables.getWTable(modulus, length, true);
    }

    private static int[] getWTable(int modulus, int length, boolean isInverse) {
        int w;
        IntModMath instance;
        int[] value;
        List<Integer> key = Arrays.asList(isInverse ? 1 : 0, modulus, length);
        int[] wTable = (int[])cache.get(key);
        if (wTable == null && (value = cache.putIfAbsent(key, wTable = (instance = IntWTables.getInstance(modulus)).createWTable(w = isInverse ? instance.getInverseNthRoot(IntModConstants.PRIMITIVE_ROOT[modulus], length) : instance.getForwardNthRoot(IntModConstants.PRIMITIVE_ROOT[modulus], length), length))) != null) {
            wTable = value;
        }
        return wTable;
    }

    private static IntModMath getInstance(int modulus) {
        IntModMath instance = new IntModMath();
        instance.setModulus(IntModConstants.MODULUS[modulus]);
        return instance;
    }
}

