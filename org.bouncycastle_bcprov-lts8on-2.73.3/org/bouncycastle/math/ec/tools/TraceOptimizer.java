/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Integers;

public class TraceOptimizer {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final SecureRandom R = new SecureRandom();

    public static void main(String[] args) {
        TreeSet names = new TreeSet(TraceOptimizer.enumToList(ECNamedCurveTable.getNames()));
        names.addAll(TraceOptimizer.enumToList(CustomNamedCurves.getNames()));
        for (String name : names) {
            ECCurve curve;
            X9ECParametersHolder x9 = CustomNamedCurves.getByNameLazy(name);
            if (x9 == null) {
                x9 = ECNamedCurveTable.getByNameLazy(name);
            }
            if (x9 == null || !ECAlgorithms.isF2mCurve(curve = x9.getCurve())) continue;
            System.out.print(name + ":");
            TraceOptimizer.implPrintNonZeroTraceBits(curve);
        }
    }

    public static void printNonZeroTraceBits(ECCurve curve) {
        if (!ECAlgorithms.isF2mCurve(curve)) {
            throw new IllegalArgumentException("Trace only defined over characteristic-2 fields");
        }
        TraceOptimizer.implPrintNonZeroTraceBits(curve);
    }

    public static void implPrintNonZeroTraceBits(ECCurve curve) {
        ECFieldElement fe;
        int i;
        int m = curve.getFieldSize();
        ArrayList<Integer> nonZeroTraceBits = new ArrayList<Integer>();
        for (i = 0; i < m; ++i) {
            if (0 == (i & 1) && 0 != i) {
                if (!nonZeroTraceBits.contains(Integers.valueOf(i >>> 1))) continue;
                nonZeroTraceBits.add(Integers.valueOf(i));
                System.out.print(" " + i);
                continue;
            }
            BigInteger zi = ONE.shiftLeft(i);
            fe = curve.fromBigInteger(zi);
            int tr = TraceOptimizer.calculateTrace(fe);
            if (tr == 0) continue;
            nonZeroTraceBits.add(Integers.valueOf(i));
            System.out.print(" " + i);
        }
        System.out.println();
        for (i = 0; i < 1000; ++i) {
            BigInteger x = new BigInteger(m, R);
            fe = curve.fromBigInteger(x);
            int check = TraceOptimizer.calculateTrace(fe);
            int tr = 0;
            for (int j = 0; j < nonZeroTraceBits.size(); ++j) {
                int bit = (Integer)nonZeroTraceBits.get(j);
                if (!x.testBit(bit)) continue;
                tr ^= 1;
            }
            if (check == tr) continue;
            throw new IllegalStateException("Optimized-trace sanity check failed");
        }
    }

    private static int calculateTrace(ECFieldElement fe) {
        int m = fe.getFieldSize();
        int k = 31 - Integers.numberOfLeadingZeros(m);
        int mk = 1;
        ECFieldElement tr = fe;
        while (k > 0) {
            tr = tr.squarePow(mk).add(tr);
            if (0 == ((mk = m >>> --k) & 1)) continue;
            tr = tr.square().add(fe);
        }
        if (tr.isZero()) {
            return 0;
        }
        if (tr.isOne()) {
            return 1;
        }
        throw new IllegalStateException("Internal error in trace calculation");
    }

    private static ArrayList enumToList(Enumeration en) {
        ArrayList rv = new ArrayList();
        while (en.hasMoreElements()) {
            rv.add(en.nextElement());
        }
        return rv;
    }
}

