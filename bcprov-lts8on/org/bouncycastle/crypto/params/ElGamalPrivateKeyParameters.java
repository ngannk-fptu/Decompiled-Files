/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.ElGamalKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;

public class ElGamalPrivateKeyParameters
extends ElGamalKeyParameters {
    private BigInteger x;

    public ElGamalPrivateKeyParameters(BigInteger x, ElGamalParameters params) {
        super(true, params);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ElGamalPrivateKeyParameters)) {
            return false;
        }
        ElGamalPrivateKeyParameters pKey = (ElGamalPrivateKeyParameters)obj;
        if (!pKey.getX().equals(this.x)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getX().hashCode();
    }
}

