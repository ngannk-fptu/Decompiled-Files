/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHKeyParameters;
import org.bouncycastle.crypto.params.DHParameters;

public class DHPrivateKeyParameters
extends DHKeyParameters {
    private BigInteger x;

    public DHPrivateKeyParameters(BigInteger x, DHParameters params) {
        super(true, params);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }

    @Override
    public int hashCode() {
        return this.x.hashCode() ^ super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DHPrivateKeyParameters)) {
            return false;
        }
        DHPrivateKeyParameters other = (DHPrivateKeyParameters)obj;
        return other.getX().equals(this.x) && super.equals(obj);
    }
}

