/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apfloat.internal.ConcurrentSoftHashMap;
import org.apfloat.internal.FloatModConstants;
import org.apfloat.internal.FloatModMath;

public class FloatWTables
extends FloatModMath {
    private static ConcurrentMap<List<Integer>, float[]> cache = new ConcurrentSoftHashMap<List<Integer>, float[]>();

    private FloatWTables() {
    }

    public static float[] getWTable(int modulus, int length) {
        return FloatWTables.getWTable(modulus, length, false);
    }

    public static float[] getInverseWTable(int modulus, int length) {
        return FloatWTables.getWTable(modulus, length, true);
    }

    private static float[] getWTable(int modulus, int length, boolean isInverse) {
        float w;
        FloatModMath instance;
        float[] value;
        List<Integer> key = Arrays.asList(isInverse ? 1 : 0, modulus, length);
        float[] wTable = (float[])cache.get(key);
        if (wTable == null && (value = cache.putIfAbsent(key, wTable = (instance = FloatWTables.getInstance(modulus)).createWTable(w = isInverse ? instance.getInverseNthRoot(FloatModConstants.PRIMITIVE_ROOT[modulus], length) : instance.getForwardNthRoot(FloatModConstants.PRIMITIVE_ROOT[modulus], length), length))) != null) {
            wTable = value;
        }
        return wTable;
    }

    private static FloatModMath getInstance(int modulus) {
        FloatModMath instance = new FloatModMath();
        instance.setModulus(FloatModConstants.MODULUS[modulus]);
        return instance;
    }
}

