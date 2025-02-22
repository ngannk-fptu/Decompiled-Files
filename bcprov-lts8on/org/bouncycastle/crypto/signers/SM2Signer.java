/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.RandomDSAKCalculator;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

public class SM2Signer
implements Signer,
ECConstants {
    private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
    private final Digest digest;
    private final DSAEncoding encoding;
    private ECDomainParameters ecParams;
    private ECPoint pubPoint;
    private ECKeyParameters ecKey;
    private byte[] z;

    public SM2Signer() {
        this(StandardDSAEncoding.INSTANCE, new SM3Digest());
    }

    public SM2Signer(Digest digest) {
        this(StandardDSAEncoding.INSTANCE, digest);
    }

    public SM2Signer(DSAEncoding encoding) {
        this.encoding = encoding;
        this.digest = new SM3Digest();
    }

    public SM2Signer(DSAEncoding encoding, Digest digest) {
        this.encoding = encoding;
        this.digest = digest;
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        byte[] userID;
        CipherParameters baseParam;
        if (param instanceof ParametersWithID) {
            baseParam = ((ParametersWithID)param).getParameters();
            userID = ((ParametersWithID)param).getID();
            if (userID.length >= 8192) {
                throw new IllegalArgumentException("SM2 user ID must be less than 2^13 bits long");
            }
        } else {
            baseParam = param;
            userID = Hex.decodeStrict("31323334353637383132333435363738");
        }
        if (forSigning) {
            if (baseParam instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom)baseParam;
                this.ecKey = (ECKeyParameters)rParam.getParameters();
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), rParam.getRandom());
            } else {
                this.ecKey = (ECKeyParameters)baseParam;
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), CryptoServicesRegistrar.getSecureRandom());
            }
            BigInteger d = ((ECPrivateKeyParameters)this.ecKey).getD();
            BigInteger nSub1 = this.ecParams.getN().subtract(BigIntegers.ONE);
            if (d.compareTo(ONE) < 0 || d.compareTo(nSub1) >= 0) {
                throw new IllegalArgumentException("SM2 private key out of range");
            }
            this.pubPoint = this.createBasePointMultiplier().multiply(this.ecParams.getG(), d).normalize();
        } else {
            this.ecKey = (ECKeyParameters)baseParam;
            this.ecParams = this.ecKey.getParameters();
            this.pubPoint = ((ECPublicKeyParameters)this.ecKey).getQ();
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECNR", this.ecKey, forSigning));
        this.z = this.getZ(userID);
        this.digest.update(this.z, 0, this.z.length);
    }

    @Override
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override
    public void update(byte[] in, int off, int len) {
        this.digest.update(in, off, len);
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        try {
            BigInteger[] rs = this.encoding.decode(this.ecParams.getN(), signature);
            return this.verifySignature(rs[0], rs[1]);
        }
        catch (Exception exception) {
            return false;
        }
    }

    @Override
    public void reset() {
        this.digest.reset();
        if (this.z != null) {
            this.digest.update(this.z, 0, this.z.length);
        }
    }

    @Override
    public byte[] generateSignature() throws CryptoException {
        BigInteger s;
        BigInteger r;
        byte[] eHash = this.digestDoFinal();
        BigInteger n = this.ecParams.getN();
        BigInteger e = this.calculateE(n, eHash);
        BigInteger d = ((ECPrivateKeyParameters)this.ecKey).getD();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        while (true) {
            BigInteger k = this.kCalculator.nextK();
            ECPoint p = basePointMultiplier.multiply(this.ecParams.getG(), k).normalize();
            r = e.add(p.getAffineXCoord().toBigInteger()).mod(n);
            if (r.equals(ZERO) || r.add(k).equals(n)) continue;
            BigInteger dPlus1ModN = BigIntegers.modOddInverse(n, d.add(ONE));
            s = k.subtract(r.multiply(d)).mod(n);
            if (!(s = dPlus1ModN.multiply(s).mod(n)).equals(ZERO)) break;
        }
        try {
            return this.encoding.encode(this.ecParams.getN(), r, s);
        }
        catch (Exception ex) {
            throw new CryptoException("unable to encode signature: " + ex.getMessage(), ex);
        }
    }

    private boolean verifySignature(BigInteger r, BigInteger s) {
        BigInteger n = this.ecParams.getN();
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0) {
            return false;
        }
        if (s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        byte[] eHash = this.digestDoFinal();
        BigInteger e = this.calculateE(n, eHash);
        BigInteger t = r.add(s).mod(n);
        if (t.equals(ZERO)) {
            return false;
        }
        ECPoint q = ((ECPublicKeyParameters)this.ecKey).getQ();
        ECPoint x1y1 = ECAlgorithms.sumOfTwoMultiplies(this.ecParams.getG(), s, q, t).normalize();
        if (x1y1.isInfinity()) {
            return false;
        }
        BigInteger expectedR = e.add(x1y1.getAffineXCoord().toBigInteger()).mod(n);
        return expectedR.equals(r);
    }

    private byte[] digestDoFinal() {
        byte[] result = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(result, 0);
        this.reset();
        return result;
    }

    private byte[] getZ(byte[] userID) {
        this.digest.reset();
        this.addUserID(this.digest, userID);
        this.addFieldElement(this.digest, this.ecParams.getCurve().getA());
        this.addFieldElement(this.digest, this.ecParams.getCurve().getB());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineXCoord());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineYCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineXCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineYCoord());
        byte[] result = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(result, 0);
        return result;
    }

    private void addUserID(Digest digest, byte[] userID) {
        int len = userID.length * 8;
        digest.update((byte)(len >> 8 & 0xFF));
        digest.update((byte)(len & 0xFF));
        digest.update(userID, 0, userID.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = v.getEncoded();
        digest.update(p, 0, p.length);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected BigInteger calculateE(BigInteger n, byte[] message) {
        return new BigInteger(1, message);
    }
}

