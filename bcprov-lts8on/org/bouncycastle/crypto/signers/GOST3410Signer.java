/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class GOST3410Signer
implements DSAExt {
    GOST3410KeyParameters key;
    SecureRandom random;

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.random = rParam.getRandom();
                this.key = (GOST3410PrivateKeyParameters)rParam.getParameters();
            } else {
                this.random = CryptoServicesRegistrar.getSecureRandom();
                this.key = (GOST3410PrivateKeyParameters)param;
            }
        } else {
            this.key = (GOST3410PublicKeyParameters)param;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("GOST3410", this.key, forSigning));
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getQ();
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger k;
        byte[] mRev = Arrays.reverse(message);
        BigInteger m = new BigInteger(1, mRev);
        GOST3410Parameters params = this.key.getParameters();
        while ((k = BigIntegers.createRandomBigInteger(params.getQ().bitLength(), this.random)).compareTo(params.getQ()) >= 0) {
        }
        BigInteger r = params.getA().modPow(k, params.getP()).mod(params.getQ());
        BigInteger s = k.multiply(m).add(((GOST3410PrivateKeyParameters)this.key).getX().multiply(r)).mod(params.getQ());
        BigInteger[] res = new BigInteger[]{r, s};
        return res;
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        byte[] mRev = Arrays.reverse(message);
        BigInteger m = new BigInteger(1, mRev);
        GOST3410Parameters params = this.key.getParameters();
        BigInteger zero = BigInteger.valueOf(0L);
        if (zero.compareTo(r) >= 0 || params.getQ().compareTo(r) <= 0) {
            return false;
        }
        if (zero.compareTo(s) >= 0 || params.getQ().compareTo(s) <= 0) {
            return false;
        }
        BigInteger v = m.modPow(params.getQ().subtract(new BigInteger("2")), params.getQ());
        BigInteger z1 = s.multiply(v).mod(params.getQ());
        BigInteger z2 = params.getQ().subtract(r).multiply(v).mod(params.getQ());
        z1 = params.getA().modPow(z1, params.getP());
        z2 = ((GOST3410PublicKeyParameters)this.key).getY().modPow(z2, params.getP());
        BigInteger u = z1.multiply(z2).mod(params.getP()).mod(params.getQ());
        return u.equals(r);
    }
}

