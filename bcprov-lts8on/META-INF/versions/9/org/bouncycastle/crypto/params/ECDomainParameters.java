/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECDomainParameters
implements ECConstants {
    private final ECCurve curve;
    private final byte[] seed;
    private final ECPoint G;
    private final BigInteger n;
    private final BigInteger h;
    private BigInteger hInv = null;

    public ECDomainParameters(X9ECParameters x9) {
        this(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
    }

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n) {
        this(curve, G, n, ONE, null);
    }

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        this(curve, G, n, h, null);
    }

    public ECDomainParameters(ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        if (curve == null) {
            throw new NullPointerException("curve");
        }
        if (n == null) {
            throw new NullPointerException("n");
        }
        this.curve = curve;
        this.G = ECDomainParameters.validatePublicPoint(curve, G);
        this.n = n;
        this.h = h;
        this.seed = Arrays.clone(seed);
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public ECPoint getG() {
        return this.G;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }

    public synchronized BigInteger getHInv() {
        if (this.hInv == null) {
            this.hInv = BigIntegers.modOddInverseVar(this.n, this.h);
        }
        return this.hInv;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ECDomainParameters)) {
            return false;
        }
        ECDomainParameters other = (ECDomainParameters)obj;
        return this.curve.equals(other.curve) && this.G.equals(other.G) && this.n.equals(other.n);
    }

    public int hashCode() {
        int hc = 4;
        hc *= 257;
        hc ^= this.curve.hashCode();
        hc *= 257;
        hc ^= this.G.hashCode();
        hc *= 257;
        return hc ^= this.n.hashCode();
    }

    public BigInteger validatePrivateScalar(BigInteger d) {
        if (null == d) {
            throw new NullPointerException("Scalar cannot be null");
        }
        if (d.compareTo(ECConstants.ONE) < 0 || d.compareTo(this.getN()) >= 0) {
            throw new IllegalArgumentException("Scalar is not in the interval [1, n - 1]");
        }
        return d;
    }

    public ECPoint validatePublicPoint(ECPoint q) {
        return ECDomainParameters.validatePublicPoint(this.getCurve(), q);
    }

    static ECPoint validatePublicPoint(ECCurve c, ECPoint q) {
        if (null == q) {
            throw new NullPointerException("Point cannot be null");
        }
        if ((q = ECAlgorithms.importPoint(c, q).normalize()).isInfinity()) {
            throw new IllegalArgumentException("Point at infinity");
        }
        if (!q.isValid()) {
            throw new IllegalArgumentException("Point not on curve");
        }
        return q;
    }
}

