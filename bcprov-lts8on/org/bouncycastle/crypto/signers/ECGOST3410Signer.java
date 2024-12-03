/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECGOST3410Signer
implements DSAExt {
    ECKeyParameters key;
    SecureRandom random;

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)param;
                this.random = rParam.getRandom();
                this.key = (ECPrivateKeyParameters)rParam.getParameters();
            } else {
                this.random = CryptoServicesRegistrar.getSecureRandom();
                this.key = (ECPrivateKeyParameters)param;
            }
        } else {
            this.key = (ECPublicKeyParameters)param;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECGOST3410", this.key, forSigning));
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getN();
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger s;
        ECPoint p;
        BigInteger r;
        BigInteger k;
        byte[] mRev = Arrays.reverse(message);
        BigInteger e = new BigInteger(1, mRev);
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger d = ((ECPrivateKeyParameters)this.key).getD();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        while ((k = BigIntegers.createRandomBigInteger(n.bitLength(), this.random)).equals(ECConstants.ZERO) || (r = (p = basePointMultiplier.multiply(ec.getG(), k).normalize()).getAffineXCoord().toBigInteger().mod(n)).equals(ECConstants.ZERO) || (s = k.multiply(e).add(d.multiply(r)).mod(n)).equals(ECConstants.ZERO)) {
        }
        return new BigInteger[]{r, s};
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        ECPoint Q;
        byte[] mRev = Arrays.reverse(message);
        BigInteger e = new BigInteger(1, mRev);
        BigInteger n = this.key.getParameters().getN();
        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0) {
            return false;
        }
        if (s.compareTo(ECConstants.ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger v = BigIntegers.modOddInverseVar(n, e);
        BigInteger z1 = s.multiply(v).mod(n);
        BigInteger z2 = n.subtract(r).multiply(v).mod(n);
        ECPoint G = this.key.getParameters().getG();
        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, z1, Q = ((ECPublicKeyParameters)this.key).getQ(), z2).normalize();
        if (point.isInfinity()) {
            return false;
        }
        BigInteger R = point.getAffineXCoord().toBigInteger().mod(n);
        return R.equals(r);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

