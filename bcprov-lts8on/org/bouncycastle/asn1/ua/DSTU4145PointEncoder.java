/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public abstract class DSTU4145PointEncoder {
    private static ECFieldElement trace(ECFieldElement fe) {
        ECFieldElement t = fe;
        for (int i = 1; i < fe.getFieldSize(); ++i) {
            t = t.square().add(fe);
        }
        return t;
    }

    private static ECFieldElement solveQuadraticEquation(ECCurve curve, ECFieldElement beta) {
        if (beta.isZero()) {
            return beta;
        }
        ECFieldElement zeroElement = curve.fromBigInteger(ECConstants.ZERO);
        ECFieldElement z = null;
        ECFieldElement gamma = null;
        Random rand = new Random();
        int m = beta.getFieldSize();
        do {
            ECFieldElement t = curve.fromBigInteger(new BigInteger(m, rand));
            z = zeroElement;
            ECFieldElement w = beta;
            for (int i = 1; i <= m - 1; ++i) {
                ECFieldElement w2 = w.square();
                z = z.square().add(w2.multiply(t));
                w = w2.add(beta);
            }
            if (w.isZero()) continue;
            return null;
        } while ((gamma = z.square().add(z)).isZero());
        return z;
    }

    public static byte[] encodePoint(ECPoint Q) {
        Q = Q.normalize();
        ECFieldElement x = Q.getAffineXCoord();
        byte[] bytes = x.getEncoded();
        if (!x.isZero()) {
            ECFieldElement z = Q.getAffineYCoord().divide(x);
            if (DSTU4145PointEncoder.trace(z).isOne()) {
                int n = bytes.length - 1;
                bytes[n] = (byte)(bytes[n] | 1);
            } else {
                int n = bytes.length - 1;
                bytes[n] = (byte)(bytes[n] & 0xFE);
            }
        }
        return bytes;
    }

    public static ECPoint decodePoint(ECCurve curve, byte[] bytes) {
        ECFieldElement k = curve.fromBigInteger(BigInteger.valueOf(bytes[bytes.length - 1] & 1));
        ECFieldElement xp = curve.fromBigInteger(new BigInteger(1, bytes));
        if (!DSTU4145PointEncoder.trace(xp).equals(curve.getA())) {
            xp = xp.addOne();
        }
        ECFieldElement yp = null;
        if (xp.isZero()) {
            yp = curve.getB().sqrt();
        } else {
            ECFieldElement beta = xp.square().invert().multiply(curve.getB()).add(curve.getA()).add(xp);
            ECFieldElement z = DSTU4145PointEncoder.solveQuadraticEquation(curve, beta);
            if (z != null) {
                if (!DSTU4145PointEncoder.trace(z).equals(k)) {
                    z = z.addOne();
                }
                yp = xp.multiply(z);
            }
        }
        if (yp == null) {
            throw new IllegalArgumentException("Invalid point compression");
        }
        return curve.validatePoint(xp.toBigInteger(), yp.toBigInteger());
    }
}

