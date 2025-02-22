/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.util.Arrays;

public class Blake2xsDigest
implements Xof {
    public static final int UNKNOWN_DIGEST_LENGTH = 65535;
    private static final int DIGEST_LENGTH = 32;
    private static final long MAX_NUMBER_BLOCKS = 0x100000000L;
    private int digestLength;
    private Blake2sDigest hash;
    private byte[] h0 = null;
    private byte[] buf = new byte[32];
    private int bufPos = 32;
    private int digestPos = 0;
    private long blockPos = 0L;
    private long nodeOffset;
    private final CryptoServicePurpose purpose;

    public Blake2xsDigest() {
        this(65535, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int digestBytes, CryptoServicePurpose purpose) {
        this(digestBytes, null, null, null, purpose);
    }

    public Blake2xsDigest(int digestBytes) {
        this(digestBytes, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int digestBytes, byte[] key) {
        this(digestBytes, key, null, null, CryptoServicePurpose.ANY);
    }

    public Blake2xsDigest(int digestBytes, byte[] key, byte[] salt, byte[] personalization, CryptoServicePurpose purpose) {
        if (digestBytes < 1 || digestBytes > 65535) {
            throw new IllegalArgumentException("BLAKE2xs digest length must be between 1 and 2^16-1");
        }
        this.digestLength = digestBytes;
        this.nodeOffset = this.computeNodeOffset();
        this.purpose = purpose;
        this.hash = new Blake2sDigest(32, key, salt, personalization, this.nodeOffset, purpose);
    }

    public Blake2xsDigest(Blake2xsDigest digest) {
        this.digestLength = digest.digestLength;
        this.hash = new Blake2sDigest(digest.hash);
        this.h0 = Arrays.clone(digest.h0);
        this.buf = Arrays.clone(digest.buf);
        this.bufPos = digest.bufPos;
        this.digestPos = digest.digestPos;
        this.blockPos = digest.blockPos;
        this.nodeOffset = digest.nodeOffset;
        this.purpose = digest.purpose;
    }

    @Override
    public String getAlgorithmName() {
        return "BLAKE2xs";
    }

    @Override
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override
    public int getByteLength() {
        return this.hash.getByteLength();
    }

    public long getUnknownMaxLength() {
        return 0x2000000000L;
    }

    @Override
    public void update(byte in) {
        this.hash.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.hash.update(in, inOff, len);
    }

    @Override
    public void reset() {
        this.hash.reset();
        this.h0 = null;
        this.bufPos = 32;
        this.digestPos = 0;
        this.blockPos = 0L;
        this.nodeOffset = this.computeNodeOffset();
    }

    @Override
    public int doFinal(byte[] out, int outOffset) {
        return this.doFinal(out, outOffset, this.digestLength);
    }

    @Override
    public int doFinal(byte[] out, int outOff, int outLen) {
        int ret = this.doOutput(out, outOff, outLen);
        this.reset();
        return ret;
    }

    @Override
    public int doOutput(byte[] out, int outOff, int outLen) {
        if (outOff > out.length - outLen) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.h0 == null) {
            this.h0 = new byte[this.hash.getDigestSize()];
            this.hash.doFinal(this.h0, 0);
        }
        if (this.digestLength != 65535) {
            if (this.digestPos + outLen > this.digestLength) {
                throw new IllegalArgumentException("Output length is above the digest length");
            }
        } else if (this.blockPos << 5 >= this.getUnknownMaxLength()) {
            throw new IllegalArgumentException("Maximum length is 2^32 blocks of 32 bytes");
        }
        for (int i = 0; i < outLen; ++i) {
            if (this.bufPos >= 32) {
                Blake2sDigest h = new Blake2sDigest(this.computeStepLength(), 32, this.nodeOffset);
                h.update(this.h0, 0, this.h0.length);
                Arrays.fill(this.buf, (byte)0);
                h.doFinal(this.buf, 0);
                this.bufPos = 0;
                ++this.nodeOffset;
                ++this.blockPos;
            }
            out[outOff + i] = this.buf[this.bufPos];
            ++this.bufPos;
            ++this.digestPos;
        }
        return outLen;
    }

    private int computeStepLength() {
        if (this.digestLength == 65535) {
            return 32;
        }
        return Math.min(32, this.digestLength - this.digestPos);
    }

    private long computeNodeOffset() {
        return (long)this.digestLength * 0x100000000L;
    }
}

