/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.RandomDSAKCalculator;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.BigIntegers;

public class DSASigner
implements DSAExt {
    private final DSAKCalculator kCalculator;
    private DSAKeyParameters key;
    private SecureRandom random;

    public DSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }

    public DSASigner(DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        SecureRandom providedRandom = null;
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.key = (DSAPrivateKeyParameters)rParam.getParameters();
                providedRandom = rParam.getRandom();
            } else {
                this.key = (DSAPrivateKeyParameters)param;
            }
        } else {
            this.key = (DSAPublicKeyParameters)param;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DSA", this.key, forSigning));
        this.random = this.initSecureRandom(forSigning && !this.kCalculator.isDeterministic(), providedRandom);
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getQ();
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        DSAParameters params = this.key.getParameters();
        BigInteger q = params.getQ();
        BigInteger m = this.calculateE(q, message);
        BigInteger x = ((DSAPrivateKeyParameters)this.key).getX();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(q, x, message);
        } else {
            this.kCalculator.init(q, this.random);
        }
        BigInteger k = this.kCalculator.nextK();
        BigInteger r = params.getG().modPow(k.add(this.getRandomizer(q, this.random)), params.getP()).mod(q);
        k = BigIntegers.modOddInverse(q, k).multiply(m.add(x.multiply(r)));
        BigInteger s = k.mod(q);
        return new BigInteger[]{r, s};
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        DSAParameters params = this.key.getParameters();
        BigInteger q = params.getQ();
        BigInteger m = this.calculateE(q, message);
        BigInteger zero = BigInteger.valueOf(0L);
        if (zero.compareTo(r) >= 0 || q.compareTo(r) <= 0) {
            return false;
        }
        if (zero.compareTo(s) >= 0 || q.compareTo(s) <= 0) {
            return false;
        }
        BigInteger w = BigIntegers.modOddInverseVar(q, s);
        BigInteger u1 = m.multiply(w).mod(q);
        BigInteger u2 = r.multiply(w).mod(q);
        BigInteger p = params.getP();
        u1 = params.getG().modPow(u1, p);
        u2 = ((DSAPublicKeyParameters)this.key).getY().modPow(u2, p);
        BigInteger v = u1.multiply(u2).mod(p).mod(q);
        return v.equals(r);
    }

    private BigInteger calculateE(BigInteger n, byte[] message) {
        if (n.bitLength() >= message.length * 8) {
            return new BigInteger(1, message);
        }
        byte[] trunc = new byte[n.bitLength() / 8];
        System.arraycopy(message, 0, trunc, 0, trunc.length);
        return new BigInteger(1, trunc);
    }

    protected SecureRandom initSecureRandom(boolean needed, SecureRandom provided) {
        return needed ? CryptoServicesRegistrar.getSecureRandom(provided) : null;
    }

    private BigInteger getRandomizer(BigInteger q, SecureRandom provided) {
        int randomBits = 7;
        return BigIntegers.createRandomBigInteger(randomBits, CryptoServicesRegistrar.getSecureRandom(provided)).add(BigInteger.valueOf(128L)).multiply(q);
    }
}

