/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECPrivateKeyParameters
extends ECKeyParameters {
    private final BigInteger d;

    public ECPrivateKeyParameters(BigInteger d, ECDomainParameters parameters) {
        super(true, parameters);
        this.d = parameters.validatePrivateScalar(d);
    }

    public BigInteger getD() {
        return this.d;
    }
}

