/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECNRSigner
implements DSAExt {
    private boolean forSigning;
    private ECKeyParameters key;
    private SecureRandom random;

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        this.forSigning = forSigning;
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
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECNR", this.key, forSigning));
    }

    @Override
    public BigInteger getOrder() {
        return this.key.getParameters().getN();
    }

    @Override
    public BigInteger[] generateSignature(byte[] digest) {
        ECKeyPairGenerator keyGen;
        AsymmetricCipherKeyPair tempPair;
        ECPublicKeyParameters V;
        BigInteger Vx;
        if (!this.forSigning) {
            throw new IllegalStateException("not initialised for signing");
        }
        BigInteger n = this.getOrder();
        BigInteger e = new BigInteger(1, digest);
        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters)this.key;
        if (e.compareTo(n) >= 0) {
            throw new DataLengthException("input too large for ECNR key");
        }
        BigInteger r = null;
        BigInteger s = null;
        do {
            keyGen = new ECKeyPairGenerator();
            keyGen.init(new ECKeyGenerationParameters(privKey.getParameters(), this.random));
        } while ((r = (Vx = (V = (ECPublicKeyParameters)(tempPair = keyGen.generateKeyPair()).getPublic()).getQ().getAffineXCoord().toBigInteger()).add(e).mod(n)).equals(ECConstants.ZERO));
        BigInteger x = privKey.getD();
        BigInteger u = ((ECPrivateKeyParameters)tempPair.getPrivate()).getD();
        s = u.subtract(r.multiply(x)).mod(n);
        BigInteger[] res = new BigInteger[]{r, s};
        return res;
    }

    @Override
    public boolean verifySignature(byte[] digest, BigInteger r, BigInteger s) {
        if (this.forSigning) {
            throw new IllegalStateException("not initialised for verifying");
        }
        ECPublicKeyParameters pubKey = (ECPublicKeyParameters)this.key;
        BigInteger n = pubKey.getParameters().getN();
        int nBitLength = n.bitLength();
        BigInteger e = new BigInteger(1, digest);
        int eBitLength = e.bitLength();
        if (eBitLength > nBitLength) {
            throw new DataLengthException("input too large for ECNR key.");
        }
        BigInteger t = this.extractT(pubKey, r, s);
        return t != null && t.equals(e.mod(n));
    }

    public byte[] getRecoveredMessage(BigInteger r, BigInteger s) {
        if (this.forSigning) {
            throw new IllegalStateException("not initialised for verifying/recovery");
        }
        BigInteger t = this.extractT((ECPublicKeyParameters)this.key, r, s);
        if (t != null) {
            return BigIntegers.asUnsignedByteArray(t);
        }
        return null;
    }

    private BigInteger extractT(ECPublicKeyParameters pubKey, BigInteger r, BigInteger s) {
        ECPoint W;
        BigInteger n = pubKey.getParameters().getN();
        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0) {
            return null;
        }
        if (s.compareTo(ECConstants.ZERO) < 0 || s.compareTo(n) >= 0) {
            return null;
        }
        ECPoint G = pubKey.getParameters().getG();
        ECPoint P = ECAlgorithms.sumOfTwoMultiplies(G, s, W = pubKey.getQ(), r).normalize();
        if (P.isInfinity()) {
            return null;
        }
        BigInteger x = P.getAffineXCoord().toBigInteger();
        return r.subtract(x).mod(n);
    }
}

