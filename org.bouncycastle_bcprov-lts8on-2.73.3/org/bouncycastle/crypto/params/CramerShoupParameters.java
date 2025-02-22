/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Memoable;

public class CramerShoupParameters
implements CipherParameters {
    private BigInteger p;
    private BigInteger g1;
    private BigInteger g2;
    private Digest H;

    public CramerShoupParameters(BigInteger p, BigInteger g1, BigInteger g2, Digest H) {
        this.p = p;
        this.g1 = g1;
        this.g2 = g2;
        this.H = (Digest)((Object)((Memoable)((Object)H)).copy());
        this.H.reset();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupParameters)) {
            return false;
        }
        CramerShoupParameters pm = (CramerShoupParameters)obj;
        return pm.getP().equals(this.p) && pm.getG1().equals(this.g1) && pm.getG2().equals(this.g2);
    }

    public int hashCode() {
        return this.getP().hashCode() ^ this.getG1().hashCode() ^ this.getG2().hashCode();
    }

    public BigInteger getG1() {
        return this.g1;
    }

    public BigInteger getG2() {
        return this.g2;
    }

    public BigInteger getP() {
        return this.p;
    }

    public Digest getH() {
        return (Digest)((Object)((Memoable)((Object)this.H)).copy());
    }
}

