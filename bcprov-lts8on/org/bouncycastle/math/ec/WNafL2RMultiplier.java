/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WNafPreCompInfo;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class WNafL2RMultiplier
extends AbstractECMultiplier {
    @Override
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        ECPoint[] table;
        int n;
        int zeroes;
        int digit;
        int wi;
        int minWidth = WNafUtil.getWindowSize(k.bitLength());
        WNafPreCompInfo info = WNafUtil.precompute(p, minWidth, true);
        ECPoint[] preComp = info.getPreComp();
        ECPoint[] preCompNeg = info.getPreCompNeg();
        int width = info.getWidth();
        int[] wnaf = WNafUtil.generateCompactWindowNaf(width, k);
        ECPoint R = p.getCurve().getInfinity();
        int i = wnaf.length;
        if (i > 1) {
            wi = wnaf[--i];
            digit = wi >> 16;
            zeroes = wi & 0xFFFF;
            n = Math.abs(digit);
            ECPoint[] eCPointArray = table = digit < 0 ? preCompNeg : preComp;
            if (n << 2 < 1 << width) {
                int highest = 32 - Integers.numberOfLeadingZeros(n);
                int scale = width - highest;
                int lowBits = n ^ 1 << highest - 1;
                int i1 = (1 << width - 1) - 1;
                int i2 = (lowBits << scale) + 1;
                R = table[i1 >>> 1].add(table[i2 >>> 1]);
                zeroes -= scale;
            } else {
                R = table[n >>> 1];
            }
            R = R.timesPow2(zeroes);
        }
        while (i > 0) {
            wi = wnaf[--i];
            digit = wi >> 16;
            zeroes = wi & 0xFFFF;
            n = Math.abs(digit);
            table = digit < 0 ? preCompNeg : preComp;
            ECPoint r = table[n >>> 1];
            R = R.twicePlus(r);
            R = R.timesPow2(zeroes);
        }
        return R;
    }
}

