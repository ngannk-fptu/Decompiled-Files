/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

public class F2mSqrtOptimizer {
    public static void main(String[] args) {
        TreeSet names = new TreeSet(F2mSqrtOptimizer.enumToList(ECNamedCurveTable.getNames()));
        names.addAll(F2mSqrtOptimizer.enumToList(CustomNamedCurves.getNames()));
        for (String name : names) {
            ECCurve curve;
            X9ECParametersHolder x9 = CustomNamedCurves.getByNameLazy(name);
            if (x9 == null) {
                x9 = ECNamedCurveTable.getByNameLazy(name);
            }
            if (x9 == null || !ECAlgorithms.isF2mCurve(curve = x9.getCurve())) continue;
            System.out.print(name + ":");
            F2mSqrtOptimizer.implPrintRootZ(curve);
        }
    }

    public static void printRootZ(ECCurve curve) {
        if (!ECAlgorithms.isF2mCurve(curve)) {
            throw new IllegalArgumentException("Sqrt optimization only defined over characteristic-2 fields");
        }
        F2mSqrtOptimizer.implPrintRootZ(curve);
    }

    private static void implPrintRootZ(ECCurve curve) {
        ECFieldElement z = curve.fromBigInteger(BigInteger.valueOf(2L));
        ECFieldElement rootZ = z.sqrt();
        System.out.println(rootZ.toBigInteger().toString(16).toUpperCase());
        if (!rootZ.square().equals(z)) {
            throw new IllegalStateException("Optimized-sqrt sanity check failed");
        }
    }

    private static ArrayList enumToList(Enumeration en) {
        ArrayList rv = new ArrayList();
        while (en.hasMoreElements()) {
            rv.add(en.nextElement());
        }
        return rv;
    }
}

