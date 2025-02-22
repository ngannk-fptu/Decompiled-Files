/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.SimpleBigDecimal;
import org.bouncycastle.math.ec.ZTauElement;
import org.bouncycastle.util.BigIntegers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class Tnaf {
    private static final BigInteger MINUS_ONE = ECConstants.ONE.negate();
    private static final BigInteger MINUS_TWO = ECConstants.TWO.negate();
    private static final BigInteger MINUS_THREE = ECConstants.THREE.negate();
    public static final byte WIDTH = 4;
    public static final ZTauElement[] alpha0 = new ZTauElement[]{null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, MINUS_ONE), null, new ZTauElement(ECConstants.ONE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.THREE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ZERO)};
    public static final byte[][] alpha0Tnaf = new byte[][]{null, {1}, null, {-1, 0, 1}, null, {1, 0, 1}, null, {-1, 0, 0, 1}};
    public static final ZTauElement[] alpha1 = new ZTauElement[]{null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(MINUS_THREE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null, new ZTauElement(MINUS_ONE, MINUS_ONE), null, new ZTauElement(ECConstants.ONE, MINUS_ONE), null, new ZTauElement(ECConstants.THREE, MINUS_ONE), null, new ZTauElement(MINUS_ONE, ECConstants.ZERO)};
    public static final byte[][] alpha1Tnaf = new byte[][]{null, {1}, null, {-1, 0, 1}, null, {1, 0, 1}, null, {-1, 0, 0, -1}};

    Tnaf() {
    }

    public static BigInteger norm(byte mu, ZTauElement lambda) {
        BigInteger s1 = lambda.u.multiply(lambda.u);
        if (mu == 1) {
            return lambda.v.shiftLeft(1).add(lambda.u).multiply(lambda.v).add(s1);
        }
        if (mu == -1) {
            return lambda.v.shiftLeft(1).subtract(lambda.u).multiply(lambda.v).add(s1);
        }
        throw new IllegalArgumentException("mu must be 1 or -1");
    }

    public static SimpleBigDecimal norm(byte mu, SimpleBigDecimal u, SimpleBigDecimal v) {
        SimpleBigDecimal norm;
        SimpleBigDecimal s1 = u.multiply(u);
        SimpleBigDecimal s2 = u.multiply(v);
        SimpleBigDecimal s3 = v.multiply(v).shiftLeft(1);
        if (mu == 1) {
            norm = s1.add(s2).add(s3);
        } else if (mu == -1) {
            norm = s1.subtract(s2).add(s3);
        } else {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        return norm;
    }

    public static ZTauElement round(SimpleBigDecimal lambda0, SimpleBigDecimal lambda1, byte mu) {
        SimpleBigDecimal check2;
        SimpleBigDecimal check1;
        int scale = lambda0.getScale();
        if (lambda1.getScale() != scale) {
            throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
        }
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger f0 = lambda0.round();
        BigInteger f1 = lambda1.round();
        SimpleBigDecimal eta0 = lambda0.subtract(f0);
        SimpleBigDecimal eta1 = lambda1.subtract(f1);
        SimpleBigDecimal eta = eta0.add(eta0);
        eta = mu == 1 ? eta.add(eta1) : eta.subtract(eta1);
        SimpleBigDecimal threeEta1 = eta1.add(eta1).add(eta1);
        SimpleBigDecimal fourEta1 = threeEta1.add(eta1);
        if (mu == 1) {
            check1 = eta0.subtract(threeEta1);
            check2 = eta0.add(fourEta1);
        } else {
            check1 = eta0.add(threeEta1);
            check2 = eta0.subtract(fourEta1);
        }
        int h0 = 0;
        byte h1 = 0;
        if (eta.compareTo(ECConstants.ONE) >= 0) {
            if (check1.compareTo(MINUS_ONE) < 0) {
                h1 = mu;
            } else {
                h0 = 1;
            }
        } else if (check2.compareTo(ECConstants.TWO) >= 0) {
            h1 = mu;
        }
        if (eta.compareTo(MINUS_ONE) < 0) {
            if (check1.compareTo(ECConstants.ONE) >= 0) {
                h1 = -mu;
            } else {
                h0 = -1;
            }
        } else if (check2.compareTo(MINUS_TWO) < 0) {
            h1 = -mu;
        }
        BigInteger q0 = f0.add(BigInteger.valueOf(h0));
        BigInteger q1 = f1.add(BigInteger.valueOf(h1));
        return new ZTauElement(q0, q1);
    }

    public static SimpleBigDecimal approximateDivisionByN(BigInteger k, BigInteger s, BigInteger vm, byte a, int m, int c) {
        int _k = (m + 5) / 2 + c;
        BigInteger ns = k.shiftRight(m - _k - 2 + a);
        BigInteger gs = s.multiply(ns);
        BigInteger hs = gs.shiftRight(m);
        BigInteger js = vm.multiply(hs);
        BigInteger gsPlusJs = gs.add(js);
        BigInteger ls = gsPlusJs.shiftRight(_k - c);
        if (gsPlusJs.testBit(_k - c - 1)) {
            ls = ls.add(ECConstants.ONE);
        }
        return new SimpleBigDecimal(ls, c);
    }

    public static byte[] tauAdicNaf(byte mu, ZTauElement lambda) {
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger norm = Tnaf.norm(mu, lambda);
        int log2Norm = norm.bitLength();
        int maxLength = log2Norm > 30 ? log2Norm + 4 : 34;
        byte[] u = new byte[maxLength];
        int i = 0;
        int length = 0;
        BigInteger r0 = lambda.u;
        BigInteger r1 = lambda.v;
        while (!r0.equals(ECConstants.ZERO) || !r1.equals(ECConstants.ZERO)) {
            if (r0.testBit(0)) {
                u[i] = (byte)ECConstants.TWO.subtract(r0.subtract(r1.shiftLeft(1)).mod(ECConstants.FOUR)).intValue();
                r0 = u[i] == 1 ? r0.clearBit(0) : r0.add(ECConstants.ONE);
                length = i;
            } else {
                u[i] = 0;
            }
            BigInteger t = r0;
            BigInteger s = r0.shiftRight(1);
            r0 = mu == 1 ? r1.add(s) : r1.subtract(s);
            r1 = t.shiftRight(1).negate();
            ++i;
        }
        byte[] tnaf = new byte[++length];
        System.arraycopy(u, 0, tnaf, 0, length);
        return tnaf;
    }

    public static ECPoint.AbstractF2m tau(ECPoint.AbstractF2m p) {
        return p.tau();
    }

    public static byte getMu(ECCurve.AbstractF2m curve) {
        if (!curve.isKoblitz()) {
            throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible");
        }
        if (curve.getA().isZero()) {
            return -1;
        }
        return 1;
    }

    public static byte getMu(ECFieldElement curveA) {
        return (byte)(curveA.isZero() ? -1 : 1);
    }

    public static byte getMu(int curveA) {
        return (byte)(curveA == 0 ? -1 : 1);
    }

    public static BigInteger[] getLucas(byte mu, int k, boolean doV) {
        BigInteger u1;
        BigInteger u0;
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        if (doV) {
            u0 = ECConstants.TWO;
            u1 = BigInteger.valueOf(mu);
        } else {
            u0 = ECConstants.ZERO;
            u1 = ECConstants.ONE;
        }
        for (int i = 1; i < k; ++i) {
            BigInteger s = u1;
            if (mu < 0) {
                s = s.negate();
            }
            BigInteger u2 = s.subtract(u0.shiftLeft(1));
            u0 = u1;
            u1 = u2;
        }
        return new BigInteger[]{u0, u1};
    }

    public static BigInteger getTw(byte mu, int w) {
        if (w == 4) {
            if (mu == 1) {
                return BigInteger.valueOf(6L);
            }
            return BigInteger.valueOf(10L);
        }
        BigInteger[] us = Tnaf.getLucas(mu, w, false);
        BigInteger twoToW = ECConstants.ZERO.setBit(w);
        BigInteger u1invert = us[1].modInverse(twoToW);
        return us[0].shiftLeft(1).multiply(u1invert).mod(twoToW);
    }

    public static BigInteger[] getSi(ECCurve.AbstractF2m curve) {
        if (!curve.isKoblitz()) {
            throw new IllegalArgumentException("si is defined for Koblitz curves only");
        }
        return Tnaf.getSi(curve.getFieldSize(), curve.getA().toBigInteger().intValue(), curve.getCofactor());
    }

    public static BigInteger[] getSi(int fieldSize, int curveA, BigInteger cofactor) {
        byte mu = Tnaf.getMu(curveA);
        int shifts = Tnaf.getShiftsForCofactor(cofactor);
        int index = fieldSize + 3 - curveA;
        BigInteger[] ui = Tnaf.getLucas(mu, index, false);
        if (mu == 1) {
            ui[0] = ui[0].negate();
            ui[1] = ui[1].negate();
        }
        BigInteger dividend0 = ECConstants.ONE.add(ui[1]).shiftRight(shifts);
        BigInteger dividend1 = ECConstants.ONE.add(ui[0]).shiftRight(shifts).negate();
        return new BigInteger[]{dividend0, dividend1};
    }

    protected static int getShiftsForCofactor(BigInteger h) {
        if (h != null) {
            if (h.equals(ECConstants.TWO)) {
                return 1;
            }
            if (h.equals(ECConstants.FOUR)) {
                return 2;
            }
        }
        throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
    }

    public static ZTauElement partModReduction(ECCurve.AbstractF2m curve, BigInteger k, byte a, byte mu, byte c) {
        BigInteger vm;
        int m = curve.getFieldSize();
        BigInteger[] s = curve.getSi();
        BigInteger d0 = mu == 1 ? s[0].add(s[1]) : s[0].subtract(s[1]);
        if (curve.isKoblitz()) {
            vm = ECConstants.ONE.shiftLeft(m).add(ECConstants.ONE).subtract(curve.getOrder().multiply(curve.getCofactor()));
        } else {
            BigInteger[] v = Tnaf.getLucas(mu, m, true);
            vm = v[1];
        }
        SimpleBigDecimal lambda0 = Tnaf.approximateDivisionByN(k, s[0], vm, a, m, c);
        SimpleBigDecimal lambda1 = Tnaf.approximateDivisionByN(k, s[1], vm, a, m, c);
        ZTauElement q = Tnaf.round(lambda0, lambda1, mu);
        BigInteger r0 = k.subtract(d0.multiply(q.u)).subtract(s[1].multiply(q.v).shiftLeft(1));
        BigInteger r1 = s[1].multiply(q.u).subtract(s[0].multiply(q.v));
        return new ZTauElement(r0, r1);
    }

    public static ECPoint.AbstractF2m multiplyRTnaf(ECPoint.AbstractF2m p, BigInteger k) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m)p.getCurve();
        int a = curve.getA().toBigInteger().intValue();
        byte mu = Tnaf.getMu(a);
        ZTauElement rho = Tnaf.partModReduction(curve, k, (byte)a, mu, (byte)10);
        return Tnaf.multiplyTnaf(p, rho);
    }

    public static ECPoint.AbstractF2m multiplyTnaf(ECPoint.AbstractF2m p, ZTauElement lambda) {
        ECCurve.AbstractF2m curve = (ECCurve.AbstractF2m)p.getCurve();
        ECPoint.AbstractF2m pNeg = (ECPoint.AbstractF2m)p.negate();
        byte mu = Tnaf.getMu(curve.getA());
        byte[] u = Tnaf.tauAdicNaf(mu, lambda);
        return Tnaf.multiplyFromTnaf(p, pNeg, u);
    }

    public static ECPoint.AbstractF2m multiplyFromTnaf(ECPoint.AbstractF2m p, ECPoint.AbstractF2m pNeg, byte[] u) {
        ECCurve curve = p.getCurve();
        ECPoint.AbstractF2m q = (ECPoint.AbstractF2m)curve.getInfinity();
        int tauCount = 0;
        for (int i = u.length - 1; i >= 0; --i) {
            ++tauCount;
            byte ui = u[i];
            if (ui == 0) continue;
            q = q.tauPow(tauCount);
            tauCount = 0;
            ECPoint.AbstractF2m x = ui > 0 ? p : pNeg;
            q = (ECPoint.AbstractF2m)q.add(x);
        }
        if (tauCount > 0) {
            q = q.tauPow(tauCount);
        }
        return q;
    }

    public static byte[] tauAdicWNaf(byte mu, ZTauElement lambda, int width, int tw, ZTauElement[] alpha) {
        if (mu != 1 && mu != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger norm = Tnaf.norm(mu, lambda);
        int log2Norm = norm.bitLength();
        int maxLength = log2Norm > 30 ? log2Norm + 4 + width : 34 + width;
        byte[] u = new byte[maxLength];
        int pow2Width = 1 << width;
        int pow2Mask = pow2Width - 1;
        int s = 32 - width;
        BigInteger R0 = lambda.u;
        BigInteger R1 = lambda.v;
        int uPos = 0;
        while (R0.bitLength() > 62 || R1.bitLength() > 62) {
            if (R0.testBit(0)) {
                int uVal = R0.intValue() + R1.intValue() * tw;
                int alphaPos = uVal & pow2Mask;
                u[uPos] = (byte)(uVal << s >> s);
                R0 = R0.subtract(alpha[alphaPos].u);
                R1 = R1.subtract(alpha[alphaPos].v);
            }
            ++uPos;
            BigInteger t = R0.shiftRight(1);
            R0 = mu == 1 ? R1.add(t) : R1.subtract(t);
            R1 = t.negate();
        }
        long r0_64 = BigIntegers.longValueExact(R0);
        long r1_64 = BigIntegers.longValueExact(R1);
        while ((r0_64 | r1_64) != 0L) {
            if ((r0_64 & 1L) != 0L) {
                int uVal = (int)r0_64 + (int)r1_64 * tw;
                int alphaPos = uVal & pow2Mask;
                u[uPos] = (byte)(uVal << s >> s);
                r0_64 -= (long)alpha[alphaPos].u.intValue();
                r1_64 -= (long)alpha[alphaPos].v.intValue();
            }
            ++uPos;
            long t_64 = r0_64 >> 1;
            r0_64 = mu == 1 ? r1_64 + t_64 : r1_64 - t_64;
            r1_64 = -t_64;
        }
        return u;
    }

    public static ECPoint.AbstractF2m[] getPreComp(ECPoint.AbstractF2m p, byte a) {
        ECPoint.AbstractF2m pNeg = (ECPoint.AbstractF2m)p.negate();
        byte[][] alphaTnaf = a == 0 ? alpha0Tnaf : alpha1Tnaf;
        ECPoint[] pu = new ECPoint.AbstractF2m[alphaTnaf.length + 1 >>> 1];
        pu[0] = p;
        int precompLen = alphaTnaf.length;
        for (int i = 3; i < precompLen; i += 2) {
            pu[i >>> 1] = Tnaf.multiplyFromTnaf(p, pNeg, alphaTnaf[i]);
        }
        p.getCurve().normalizeAll(pu);
        return pu;
    }
}

