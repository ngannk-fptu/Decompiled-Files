/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class Blake3Mac
implements Mac {
    private final Blake3Digest theDigest;

    public Blake3Mac(Blake3Digest pDigest) {
        this.theDigest = pDigest;
    }

    @Override
    public String getAlgorithmName() {
        return this.theDigest.getAlgorithmName() + "Mac";
    }

    @Override
    public void init(CipherParameters pParams) {
        CipherParameters myParams = pParams;
        if (myParams instanceof KeyParameter) {
            myParams = Blake3Parameters.key(((KeyParameter)myParams).getKey());
        }
        if (!(myParams instanceof Blake3Parameters)) {
            throw new IllegalArgumentException("Invalid parameter passed to Blake3Mac init - " + pParams.getClass().getName());
        }
        Blake3Parameters myBlakeParams = (Blake3Parameters)myParams;
        if (myBlakeParams.getKey() == null) {
            throw new IllegalArgumentException("Blake3Mac requires a key parameter.");
        }
        this.theDigest.init(myBlakeParams);
    }

    @Override
    public int getMacSize() {
        return this.theDigest.getDigestSize();
    }

    @Override
    public void update(byte in) {
        this.theDigest.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.theDigest.update(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        return this.theDigest.doFinal(out, outOff);
    }

    @Override
    public void reset() {
        this.theDigest.reset();
    }
}

