/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import org.bouncycastle.math.ec.ECPointMap;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface ECEndomorphism {
    public ECPointMap getPointMap();

    public boolean hasEfficientPointMap();
}

