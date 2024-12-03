/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;

public abstract class AbstractECMultiplier
implements ECMultiplier {
    @Override
    public ECPoint multiply(ECPoint eCPoint, BigInteger bigInteger) {
        int n = bigInteger.signum();
        if (n == 0 || eCPoint.isInfinity()) {
            return eCPoint.getCurve().getInfinity();
        }
        ECPoint eCPoint2 = this.multiplyPositive(eCPoint, bigInteger.abs());
        ECPoint eCPoint3 = n > 0 ? eCPoint2 : eCPoint2.negate();
        return this.checkResult(eCPoint3);
    }

    protected abstract ECPoint multiplyPositive(ECPoint var1, BigInteger var2);

    protected ECPoint checkResult(ECPoint eCPoint) {
        return ECAlgorithms.implCheckResult(eCPoint);
    }
}

