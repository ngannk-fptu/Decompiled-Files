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

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SHA512Digest
extends LongDigest {
    private static final int DIGEST_LENGTH = 64;

    public SHA512Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA512Digest(CryptoServicePurpose purpose) {
        super(purpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SHA512Digest(SHA512Digest t) {
        super(t);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public SHA512Digest(byte[] encodedState) {
        super(CryptoServicePurpose.values()[encodedState[encodedState.length - 1]]);
        this.restoreState(encodedState);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512";
    }

    @Override
    public int getDigestSize() {
        return 64;
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
        Pack.longToBigEndian(this.H7, out, outOff + 48);
        Pack.longToBigEndian(this.H8, out, outOff + 56);
        this.reset();
        return 64;
    }

    @Override
    public void reset() {
        super.reset();
        this.H1 = 7640891576956012808L;
        this.H2 = -4942790177534073029L;
        this.H3 = 4354685564936845355L;
        this.H4 = -6534734903238641935L;
        this.H5 = 5840696475078001361L;
        this.H6 = -7276294671716946913L;
        this.H7 = 2270897969802886507L;
        this.H8 = 6620516959819538809L;
    }

    @Override
    public Memoable copy() {
        return new SHA512Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA512Digest d = (SHA512Digest)other;
        this.copyIn(d);
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

