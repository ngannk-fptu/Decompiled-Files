/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import java.security.DigestException;
import java.security.MessageDigest;
import org.bouncycastle.crypto.Digest;

public class BCMessageDigest
extends MessageDigest {
    protected Digest digest;
    protected int digestSize;

    protected BCMessageDigest(Digest digest) {
        super(digest.getAlgorithmName());
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
    }

    @Override
    public void engineReset() {
        this.digest.reset();
    }

    @Override
    public void engineUpdate(byte input) {
        this.digest.update(input);
    }

    @Override
    public void engineUpdate(byte[] input, int offset, int len) {
        this.digest.update(input, offset, len);
    }

    @Override
    public int engineGetDigestLength() {
        return this.digestSize;
    }

    @Override
    public byte[] engineDigest() {
        byte[] digestBytes = new byte[this.digestSize];
        this.digest.doFinal(digestBytes, 0);
        return digestBytes;
    }

    @Override
    public int engineDigest(byte[] buf, int off, int len) throws DigestException {
        if (len < this.digestSize) {
            throw new DigestException("partial digests not returned");
        }
        if (buf.length - off < this.digestSize) {
            throw new DigestException("insufficient space in the output buffer to store the digest");
        }
        this.digest.doFinal(buf, off);
        return this.digestSize;
    }
}

