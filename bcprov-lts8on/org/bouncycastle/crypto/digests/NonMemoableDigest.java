/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class NonMemoableDigest
implements ExtendedDigest {
    private ExtendedDigest baseDigest;

    public NonMemoableDigest(ExtendedDigest baseDigest) {
        if (baseDigest == null) {
            throw new IllegalArgumentException("baseDigest must not be null");
        }
        this.baseDigest = baseDigest;
    }

    @Override
    public String getAlgorithmName() {
        return this.baseDigest.getAlgorithmName();
    }

    @Override
    public int getDigestSize() {
        return this.baseDigest.getDigestSize();
    }

    @Override
    public void update(byte in) {
        this.baseDigest.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.baseDigest.update(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        return this.baseDigest.doFinal(out, outOff);
    }

    @Override
    public void reset() {
        this.baseDigest.reset();
    }

    @Override
    public int getByteLength() {
        return this.baseDigest.getByteLength();
    }
}

