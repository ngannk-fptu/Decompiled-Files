/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM2KeyExchange {
    private final Digest digest;
    private byte[] userID;
    private ECPrivateKeyParameters staticKey;
    private ECPoint staticPubPoint;
    private ECPoint ephemeralPubPoint;
    private ECDomainParameters ecParams;
    private int w;
    private ECPrivateKeyParameters ephemeralKey;
    private boolean initiator;

    public SM2KeyExchange() {
        this(new SM3Digest());
    }

    public SM2KeyExchange(Digest digest) {
        this.digest = digest;
    }

    public void init(CipherParameters privParam) {
        SM2KeyExchangePrivateParameters baseParam;
        if (privParam instanceof ParametersWithID) {
            baseParam = (SM2KeyExchangePrivateParameters)((ParametersWithID)privParam).getParameters();
            this.userID = ((ParametersWithID)privParam).getID();
        } else {
            baseParam = (SM2KeyExchangePrivateParameters)privParam;
            this.userID = new byte[0];
        }
        this.initiator = baseParam.isInitiator();
        this.staticKey = baseParam.getStaticPrivateKey();
        this.ephemeralKey = baseParam.getEphemeralPrivateKey();
        this.ecParams = this.staticKey.getParameters();
        this.staticPubPoint = baseParam.getStaticPublicPoint();
        this.ephemeralPubPoint = baseParam.getEphemeralPublicPoint();
        this.w = this.ecParams.getCurve().getFieldSize() / 2 - 1;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("SM2KE", this.staticKey));
    }

    public byte[] calculateKey(int kLen, CipherParameters pubParam) {
        byte[] otherUserID;
        SM2KeyExchangePublicParameters otherPub;
        if (pubParam instanceof ParametersWithID) {
            otherPub = (SM2KeyExchangePublicParameters)((ParametersWithID)pubParam).getParameters();
            otherUserID = ((ParametersWithID)pubParam).getID();
        } else {
            otherPub = (SM2KeyExchangePublicParameters)pubParam;
            otherUserID = new byte[]{};
        }
        byte[] za = this.getZ(this.digest, this.userID, this.staticPubPoint);
        byte[] zb = this.getZ(this.digest, otherUserID, otherPub.getStaticPublicKey().getQ());
        ECPoint U = this.calculateU(otherPub);
        byte[] rv = this.initiator ? this.kdf(U, za, zb, kLen) : this.kdf(U, zb, za, kLen);
        return rv;
    }

    public byte[][] calculateKeyWithConfirmation(int kLen, byte[] confirmationTag, CipherParameters pubParam) {
        byte[] otherUserID;
        SM2KeyExchangePublicParameters otherPub;
        if (pubParam instanceof ParametersWithID) {
            otherPub = (SM2KeyExchangePublicParameters)((ParametersWithID)pubParam).getParameters();
            otherUserID = ((ParametersWithID)pubParam).getID();
        } else {
            otherPub = (SM2KeyExchangePublicParameters)pubParam;
            otherUserID = new byte[]{};
        }
        if (this.initiator && confirmationTag == null) {
            throw new IllegalArgumentException("if initiating, confirmationTag must be set");
        }
        byte[] za = this.getZ(this.digest, this.userID, this.staticPubPoint);
        byte[] zb = this.getZ(this.digest, otherUserID, otherPub.getStaticPublicKey().getQ());
        ECPoint U = this.calculateU(otherPub);
        if (this.initiator) {
            byte[] rv = this.kdf(U, za, zb, kLen);
            byte[] inner = this.calculateInnerHash(this.digest, U, za, zb, this.ephemeralPubPoint, otherPub.getEphemeralPublicKey().getQ());
            byte[] s1 = this.S1(this.digest, U, inner);
            if (!Arrays.constantTimeAreEqual(s1, confirmationTag)) {
                throw new IllegalStateException("confirmation tag mismatch");
            }
            return new byte[][]{rv, this.S2(this.digest, U, inner)};
        }
        byte[] rv = this.kdf(U, zb, za, kLen);
        byte[] inner = this.calculateInnerHash(this.digest, U, zb, za, otherPub.getEphemeralPublicKey().getQ(), this.ephemeralPubPoint);
        return new byte[][]{rv, this.S1(this.digest, U, inner), this.S2(this.digest, U, inner)};
    }

    private ECPoint calculateU(SM2KeyExchangePublicParameters otherPub) {
        ECDomainParameters params = this.staticKey.getParameters();
        ECPoint p1 = ECAlgorithms.cleanPoint(params.getCurve(), otherPub.getStaticPublicKey().getQ());
        ECPoint p2 = ECAlgorithms.cleanPoint(params.getCurve(), otherPub.getEphemeralPublicKey().getQ());
        BigInteger x1 = this.reduce(this.ephemeralPubPoint.getAffineXCoord().toBigInteger());
        BigInteger x2 = this.reduce(p2.getAffineXCoord().toBigInteger());
        BigInteger tA = this.staticKey.getD().add(x1.multiply(this.ephemeralKey.getD()));
        BigInteger k1 = this.ecParams.getH().multiply(tA).mod(this.ecParams.getN());
        BigInteger k2 = k1.multiply(x2).mod(this.ecParams.getN());
        return ECAlgorithms.sumOfTwoMultiplies(p1, k1, p2, k2).normalize();
    }

    private byte[] kdf(ECPoint u, byte[] za, byte[] zb, int klen) {
        int digestSize = this.digest.getDigestSize();
        byte[] buf = new byte[Math.max(4, digestSize)];
        byte[] rv = new byte[(klen + 7) / 8];
        int off = 0;
        Memoable memo = null;
        Memoable copy = null;
        if (this.digest instanceof Memoable) {
            this.addFieldElement(this.digest, u.getAffineXCoord());
            this.addFieldElement(this.digest, u.getAffineYCoord());
            this.digest.update(za, 0, za.length);
            this.digest.update(zb, 0, zb.length);
            memo = (Memoable)((Object)this.digest);
            copy = memo.copy();
        }
        int ct = 0;
        while (off < rv.length) {
            if (memo != null) {
                memo.reset(copy);
            } else {
                this.addFieldElement(this.digest, u.getAffineXCoord());
                this.addFieldElement(this.digest, u.getAffineYCoord());
                this.digest.update(za, 0, za.length);
                this.digest.update(zb, 0, zb.length);
            }
            Pack.intToBigEndian(++ct, buf, 0);
            this.digest.update(buf, 0, 4);
            this.digest.doFinal(buf, 0);
            int copyLen = Math.min(digestSize, rv.length - off);
            System.arraycopy(buf, 0, rv, off, copyLen);
            off += copyLen;
        }
        return rv;
    }

    private BigInteger reduce(BigInteger x) {
        return x.and(BigInteger.valueOf(1L).shiftLeft(this.w).subtract(BigInteger.valueOf(1L))).setBit(this.w);
    }

    private byte[] S1(Digest digest, ECPoint u, byte[] inner) {
        digest.update((byte)2);
        this.addFieldElement(digest, u.getAffineYCoord());
        digest.update(inner, 0, inner.length);
        return this.digestDoFinal();
    }

    private byte[] calculateInnerHash(Digest digest, ECPoint u, byte[] za, byte[] zb, ECPoint p1, ECPoint p2) {
        this.addFieldElement(digest, u.getAffineXCoord());
        digest.update(za, 0, za.length);
        digest.update(zb, 0, zb.length);
        this.addFieldElement(digest, p1.getAffineXCoord());
        this.addFieldElement(digest, p1.getAffineYCoord());
        this.addFieldElement(digest, p2.getAffineXCoord());
        this.addFieldElement(digest, p2.getAffineYCoord());
        return this.digestDoFinal();
    }

    private byte[] S2(Digest digest, ECPoint u, byte[] inner) {
        digest.update((byte)3);
        this.addFieldElement(digest, u.getAffineYCoord());
        digest.update(inner, 0, inner.length);
        return this.digestDoFinal();
    }

    private byte[] getZ(Digest digest, byte[] userID, ECPoint pubPoint) {
        this.addUserID(digest, userID);
        this.addFieldElement(digest, this.ecParams.getCurve().getA());
        this.addFieldElement(digest, this.ecParams.getCurve().getB());
        this.addFieldElement(digest, this.ecParams.getG().getAffineXCoord());
        this.addFieldElement(digest, this.ecParams.getG().getAffineYCoord());
        this.addFieldElement(digest, pubPoint.getAffineXCoord());
        this.addFieldElement(digest, pubPoint.getAffineYCoord());
        return this.digestDoFinal();
    }

    private void addUserID(Digest digest, byte[] userID) {
        int len = userID.length * 8;
        digest.update((byte)(len >>> 8));
        digest.update((byte)len);
        digest.update(userID, 0, userID.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = v.getEncoded();
        digest.update(p, 0, p.length);
    }

    private byte[] digestDoFinal() {
        byte[] result = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(result, 0);
        return result;
    }
}

