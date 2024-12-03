/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.GOST3411_2012Digest;
import org.bouncycastle.util.Memoable;

public final class GOST3411_2012_256Digest
extends GOST3411_2012Digest {
    private static final byte[] IV = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    public GOST3411_2012_256Digest(CryptoServicePurpose purpose) {
        super(IV, purpose);
    }

    public GOST3411_2012_256Digest() {
        super(IV, CryptoServicePurpose.ANY);
    }

    public GOST3411_2012_256Digest(GOST3411_2012_256Digest other) {
        super(IV, other.purpose);
        this.reset(other);
    }

    @Override
    public String getAlgorithmName() {
        return "GOST3411-2012-256";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        byte[] result = new byte[64];
        super.doFinal(result, 0);
        System.arraycopy(result, 32, out, outOff, 32);
        return 32;
    }

    @Override
    public Memoable copy() {
        return new GOST3411_2012_256Digest(this);
    }
}

