/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPointMap;

public class ScaleYNegateXPointMap
implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleYNegateXPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override
    public ECPoint map(ECPoint p) {
        return p.scaleYNegateX(this.scale);
    }
}

