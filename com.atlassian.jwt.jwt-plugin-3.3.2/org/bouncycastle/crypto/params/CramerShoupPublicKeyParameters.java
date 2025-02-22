/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.CramerShoupKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;

public class CramerShoupPublicKeyParameters
extends CramerShoupKeyParameters {
    private BigInteger c;
    private BigInteger d;
    private BigInteger h;

    public CramerShoupPublicKeyParameters(CramerShoupParameters cramerShoupParameters, BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        super(false, cramerShoupParameters);
        this.c = bigInteger;
        this.d = bigInteger2;
        this.h = bigInteger3;
    }

    public BigInteger getC() {
        return this.c;
    }

    public BigInteger getD() {
        return this.d;
    }

    public BigInteger getH() {
        return this.h;
    }

    public int hashCode() {
        return this.c.hashCode() ^ this.d.hashCode() ^ this.h.hashCode() ^ super.hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof CramerShoupPublicKeyParameters)) {
            return false;
        }
        CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = (CramerShoupPublicKeyParameters)object;
        return cramerShoupPublicKeyParameters.getC().equals(this.c) && cramerShoupPublicKeyParameters.getD().equals(this.d) && cramerShoupPublicKeyParameters.getH().equals(this.h) && super.equals(object);
    }
}

