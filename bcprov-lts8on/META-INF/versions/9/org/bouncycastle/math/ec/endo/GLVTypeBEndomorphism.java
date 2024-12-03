/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.ScaleXPointMap;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.ec.endo.GLVTypeBParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class GLVTypeBEndomorphism
implements GLVEndomorphism {
    protected final GLVTypeBParameters parameters;
    protected final ECPointMap pointMap;

    public GLVTypeBEndomorphism(ECCurve curve, GLVTypeBParameters parameters) {
        this.parameters = parameters;
        this.pointMap = new ScaleXPointMap(curve.fromBigInteger(parameters.getBeta()));
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

