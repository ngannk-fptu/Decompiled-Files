/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class AbstractECMultiplier
implements ECMultiplier {
    @Override
    public ECPoint multiply(ECPoint p, BigInteger k) {
        int sign = k.signum();
        if (sign == 0 || p.isInfinity()) {
            return p.getCurve().getInfinity();
        }
        ECPoint positive = this.multiplyPositive(p, k.abs());
        ECPoint result = sign > 0 ? positive : positive.negate();
        return this.checkResult(result);
    }

    protected abstract ECPoint multiplyPositive(ECPoint var1, BigInteger var2);

    protected ECPoint checkResult(ECPoint p) {
        return ECAlgorithms.implCheckResult(p);
    }
}

