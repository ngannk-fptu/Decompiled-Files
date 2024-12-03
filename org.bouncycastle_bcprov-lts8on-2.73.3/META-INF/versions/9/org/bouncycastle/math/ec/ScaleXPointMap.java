/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECPointMap;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ScaleXPointMap
implements ECPointMap {
    protected final ECFieldElement scale;

    public ScaleXPointMap(ECFieldElement scale) {
        this.scale = scale;
    }

    @Override
    public ECPoint map(ECPoint p) {
        return p.scaleX(this.scale);
    }
}

