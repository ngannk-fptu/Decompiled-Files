/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apfloat.internal.ConcurrentSoftHashMap;
import org.apfloat.internal.DoubleModConstants;
import org.apfloat.internal.DoubleModMath;

public class DoubleWTables
extends DoubleModMath {
    private static ConcurrentMap<List<Integer>, double[]> cache = new ConcurrentSoftHashMap<List<Integer>, double[]>();

    private DoubleWTables() {
    }

    public static double[] getWTable(int modulus, int length) {
        return DoubleWTables.getWTable(modulus, length, false);
    }

    public static double[] getInverseWTable(int modulus, int length) {
        return DoubleWTables.getWTable(modulus, length, true);
    }

    private static double[] getWTable(int modulus, int length, boolean isInverse) {
        double w;
        DoubleModMath instance;
        double[] value;
        List<Integer> key = Arrays.asList(isInverse ? 1 : 0, modulus, length);
        double[] wTable = (double[])cache.get(key);
        if (wTable == null && (value = cache.putIfAbsent(key, wTable = (instance = DoubleWTables.getInstance(modulus)).createWTable(w = isInverse ? instance.getInverseNthRoot(DoubleModConstants.PRIMITIVE_ROOT[modulus], length) : instance.getForwardNthRoot(DoubleModConstants.PRIMITIVE_ROOT[modulus], length), length))) != null) {
            wTable = value;
        }
        return wTable;
    }

    private static DoubleModMath getInstance(int modulus) {
        DoubleModMath instance = new DoubleModMath();
        instance.setModulus(DoubleModConstants.MODULUS[modulus]);
        return instance;
    }
}

