/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.EndoPreCompInfo;
import org.bouncycastle.math.ec.endo.ScalarSplitParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class EndoUtil {
    public static final String PRECOMP_NAME = "bc_endo";

    public static BigInteger[] decomposeScalar(ScalarSplitParameters p, BigInteger k) {
        int bits = p.getBits();
        BigInteger b1 = EndoUtil.calculateB(k, p.getG1(), bits);
        BigInteger b2 = EndoUtil.calculateB(k, p.getG2(), bits);
        BigInteger a = k.subtract(b1.multiply(p.getV1A()).add(b2.multiply(p.getV2A())));
        BigInteger b = b1.multiply(p.getV1B()).add(b2.multiply(p.getV2B())).negate();
        return new BigInteger[]{a, b};
    }

    public static ECPoint mapPoint(final ECEndomorphism endomorphism, final ECPoint p) {
        ECCurve c = p.getCurve();
        EndoPreCompInfo precomp = (EndoPreCompInfo)c.precompute(p, PRECOMP_NAME, new PreCompCallback(){

            @Override
            public PreCompInfo precompute(PreCompInfo existing) {
                EndoPreCompInfo existingEndo;
                EndoPreCompInfo endoPreCompInfo = existingEndo = existing instanceof EndoPreCompInfo ? (EndoPreCompInfo)existing : null;
                if (this.checkExisting(existingEndo, endomorphism)) {
                    return existingEndo;
                }
                ECPoint mappedPoint = endomorphism.getPointMap().map(p);
                EndoPreCompInfo result = new EndoPreCompInfo();
                result.setEndomorphism(endomorphism);
                result.setMappedPoint(mappedPoint);
                return result;
            }

            private boolean checkExisting(EndoPreCompInfo existingEndo, ECEndomorphism endomorphism2) {
                return null != existingEndo && existingEndo.getEndomorphism() == endomorphism2 && existingEndo.getMappedPoint() != null;
            }
        });
        return precomp.getMappedPoint();
    }

    private static BigInteger calculateB(BigInteger k, BigInteger g, int t) {
        boolean negative = g.signum() < 0;
        BigInteger b = k.multiply(g.abs());
        boolean extra = b.testBit(t - 1);
        b = b.shiftRight(t);
        if (extra) {
            b = b.add(ECConstants.ONE);
        }
        return negative ? b.negate() : b;
    }
}

