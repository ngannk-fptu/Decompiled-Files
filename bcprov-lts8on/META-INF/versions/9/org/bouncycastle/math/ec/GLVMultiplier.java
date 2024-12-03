/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class GLVMultiplier
extends AbstractECMultiplier {
    protected final ECCurve curve;
    protected final GLVEndomorphism glvEndomorphism;

    public GLVMultiplier(ECCurve curve, GLVEndomorphism glvEndomorphism) {
        if (curve == null || curve.getOrder() == null) {
            throw new IllegalArgumentException("Need curve with known group order");
        }
        this.curve = curve;
        this.glvEndomorphism = glvEndomorphism;
    }

    @Override
    protected ECPoint multiplyPositive(ECPoint p, BigInteger k) {
        if (!this.curve.equals(p.getCurve())) {
            throw new IllegalStateException();
        }
        BigInteger n = p.getCurve().getOrder();
        BigInteger[] ab = this.glvEndomorphism.decomposeScalar(k.mod(n));
        BigInteger a = ab[0];
        BigInteger b = ab[1];
        if (this.glvEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implShamirsTrickWNaf(this.glvEndomorphism, p, a, b);
        }
        ECPoint q = EndoUtil.mapPoint(this.glvEndomorphism, p);
        return ECAlgorithms.implShamirsTrickWNaf(p, a, q, b);
    }
}

