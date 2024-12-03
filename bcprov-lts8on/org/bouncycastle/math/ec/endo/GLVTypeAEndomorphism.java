/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.ScaleYNegateXPointMap;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.ec.endo.GLVTypeAParameters;

public class GLVTypeAEndomorphism
implements GLVEndomorphism {
    protected final GLVTypeAParameters parameters;
    protected final ECPointMap pointMap;

    public GLVTypeAEndomorphism(ECCurve curve, GLVTypeAParameters parameters) {
        this.parameters = parameters;
        this.pointMap = new ScaleYNegateXPointMap(curve.fromBigInteger(parameters.getI()));
    }

    @Override
    public BigInteger[] decomposeScalar(BigInteger k) {
        return EndoUtil.decomposeScalar(this.parameters.getSplitParams(), k);
    }

    @Override
    public ECPointMap getPointMap() {
        return this.pointMap;
    }

    @Override
    public boolean hasEfficientPointMap() {
        return true;
    }
}

