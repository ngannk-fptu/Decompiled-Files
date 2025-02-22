/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA384Digest
extends LongDigest {
    private static final int DIGEST_LENGTH = 48;

    public SHA384Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA384Digest(CryptoServicePurpose purpose) {
        super(purpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SHA384Digest(SHA384Digest t) {
        super(t);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public SHA384Digest(byte[] encodedState) {
        super(CryptoServicePurpose.values()[encodedState[encodedState.length - 1]]);
        this.restoreState(encodedState);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-384";
    }

    @Override
    public int getDigestSize() {
        return 48;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.finish();
        Pack.longToBigEndian(this.H1, out, outOff);
        Pack.longToBigEndian(this.H2, out, outOff + 8);
        Pack.longToBigEndian(this.H3, out, outOff + 16);
        Pack.longToBigEndian(this.H4, out, outOff + 24);
        Pack.longToBigEndian(this.H5, out, outOff + 32);
        Pack.longToBigEndian(this.H6, out, outOff + 40);
        this.reset();
        return 48;
    }

    @Override
    public void reset() {
        super.reset();
        this.H1 = -3766243637369397544L;
        this.H2 = 7105036623409894663L;
        this.H3 = -7973340178411365097L;
        this.H4 = 1526699215303891257L;
        this.H5 = 7436329637833083697L;
        this.H6 = -8163818279084223215L;
        this.H7 = -2662702644619276377L;
        this.H8 = 5167115440072839076L;
    }

    @Override
    public Memoable copy() {
        return new SHA384Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA384Digest d = (SHA384Digest)other;
        super.copyIn(d);
    }

    @Override
    public byte[] getEncodedState() {
        byte[] encoded = new byte[this.getEncodedStateSize() + 1];
        super.populateState(encoded);
        encoded[encoded.length - 1] = (byte)this.purpose.ordinal();
        return encoded;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }
}

