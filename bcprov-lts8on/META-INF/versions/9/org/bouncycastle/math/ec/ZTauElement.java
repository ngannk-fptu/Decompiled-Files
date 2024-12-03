/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class ZTauElement {
    public final BigInteger u;
    public final BigInteger v;

    public ZTauElement(BigInteger u, BigInteger v) {
        this.u = u;
        this.v = v;
    }
}

