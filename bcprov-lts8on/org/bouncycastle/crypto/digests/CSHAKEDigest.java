/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.util.Arrays;

public class CSHAKEDigest
extends SHAKEDigest {
    private static final byte[] padding = new byte[100];
    private final byte[] diff;

    public CSHAKEDigest(int bitLength, byte[] N, byte[] S) {
        this(bitLength, CryptoServicePurpose.ANY, N, S);
    }

    public CSHAKEDigest(int bitLength, CryptoServicePurpose purpose, byte[] N, byte[] S) {
        super(bitLength, purpose);
        if (!(N != null && N.length != 0 || S != null && S.length != 0)) {
            this.diff = null;
        } else {
            this.diff = Arrays.concatenate(XofUtils.leftEncode(this.rate / 8), this.encodeString(N), this.encodeString(S));
            this.diffPadAndAbsorb();
        }
    }

    CSHAKEDigest(CSHAKEDigest source) {
        super(source);
        this.diff = Arrays.clone(source.diff);
    }

    private void diffPadAndAbsorb() {
        int blockSize = this.rate / 8;
        this.absorb(this.diff, 0, this.diff.length);
        int delta = this.diff.length % blockSize;
        if (delta != 0) {
            int required;
            for (required = blockSize - delta; required > padding.length; required -= padding.length) {
                this.absorb(padding, 0, padding.length);
            }
            this.absorb(padding, 0, required);
        }
    }

    private byte[] encodeString(byte[] str) {
        if (str == null || str.length == 0) {
            return XofUtils.leftEncode(0L);
        }
        return Arrays.concatenate(XofUtils.leftEncode((long)str.length * 8L), str);
    }

    @Override
    public String getAlgorithmName() {
        return "CSHAKE" + this.fixedOutputLength;
    }

    @Override
    public int doOutput(byte[] out, int outOff, int outLen) {
        if (this.diff != null) {
            if (!this.squeezing) {
                this.absorbBits(0, 2);
            }
            this.squeeze(out, outOff, (long)outLen * 8L);
            return outLen;
        }
        return super.doOutput(out, outOff, outLen);
    }

    @Override
    public void reset() {
        super.reset();
        if (this.diff != null) {
            this.diffPadAndAbsorb();
        }
    }
}

