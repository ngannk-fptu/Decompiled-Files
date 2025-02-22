/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointPreCompInfo;
import org.bouncycastle.math.ec.FixedPointUtil;
import org.bouncycastle.math.raw.Nat;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class FixedPointCombMultiplier
extends AbstractECMultiplier {
    @Override
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECCurve c = p.getCurve();
        int size = FixedPointUtil.getCombSize(c);
        if (k.bitLength() > size) {
            throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
        }
        FixedPointPreCompInfo info = FixedPointUtil.precompute(p);
        ECLookupTable lookupTable = info.getLookupTable();
        int width = info.getWidth();
        int d = (size + width - 1) / width;
        ECPoint R = c.getInfinity();
        int fullComb = d * width;
        int[] K = Nat.fromBigInteger(fullComb, k);
        int top = fullComb - 1;
        for (int i = 0; i < d; ++i) {
            int secretIndex = 0;
            for (int j = top - i; j >= 0; j -= d) {
                int secretBit = K[j >>> 5] >>> (j & 0x1F);
                secretIndex ^= secretBit >>> 1;
                secretIndex <<= 1;
                secretIndex ^= secretBit;
            }
            ECPoint add = lookupTable.lookup(secretIndex);
            R = R.twicePlus(add);
        }
        return R.add(info.getOffset());
    }
}

