/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECPublicKeyParameters
extends ECKeyParameters {
    private final ECPoint q;

    public ECPublicKeyParameters(ECPoint q, ECDomainParameters parameters) {
        super(false, parameters);
        this.q = parameters.validatePublicPoint(q);
    }

    public ECPoint getQ() {
        return this.q;
    }
}

