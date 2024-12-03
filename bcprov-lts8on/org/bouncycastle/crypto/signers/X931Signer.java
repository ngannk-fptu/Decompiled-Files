/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class X931Signer
implements Signer {
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private RSAKeyParameters kParam;
    private int trailer;
    private int keyBits;
    private byte[] block;

    public X931Signer(AsymmetricBlockCipher cipher, Digest digest, boolean implicit) {
        this.cipher = cipher;
        this.digest = digest;
        if (implicit) {
            this.trailer = 188;
        } else {
            Integer trailerObj = ISOTrailers.getTrailer(digest);
            if (trailerObj != null) {
                this.trailer = trailerObj;
            } else {
                throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
            }
        }
    }

    public X931Signer(AsymmetricBlockCipher cipher, Digest digest) {
        this(cipher, digest, false);
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        this.kParam = (RSAKeyParameters)param;
        this.cipher.init(forSigning, this.kParam);
        this.keyBits = this.kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        this.reset();
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; ++i) {
            block[i] = 0;
        }
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
    public void reset() {
        this.digest.reset();
    }

    @Override
    public byte[] generateSignature() throws CryptoException {
        this.createSignatureBlock(this.trailer);
        BigInteger t = new BigInteger(1, this.cipher.processBlock(this.block, 0, this.block.length));
        this.clearBlock(this.block);
        t = t.min(this.kParam.getModulus().subtract(t));
        int size = BigIntegers.getUnsignedByteLength(this.kParam.getModulus());
        return BigIntegers.asUnsignedByteArray(size, t);
    }

    private void createSignatureBlock(int trailer) {
        int delta;
        int digSize = this.digest.getDigestSize();
        if (trailer == 188) {
            delta = this.block.length - digSize - 1;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 1] = -68;
        } else {
            delta = this.block.length - digSize - 2;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 2] = (byte)(trailer >>> 8);
            this.block[this.block.length - 1] = (byte)trailer;
        }
        this.block[0] = 107;
        for (int i = delta - 2; i != 0; --i) {
            this.block[i] = -69;
        }
        this.block[delta - 1] = -70;
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        BigInteger f;
        try {
            this.block = this.cipher.processBlock(signature, 0, signature.length);
        }
        catch (Exception e) {
            return false;
        }
        BigInteger t = new BigInteger(1, this.block);
        if ((t.intValue() & 0xF) == 12) {
            f = t;
        } else {
            t = this.kParam.getModulus().subtract(t);
            if ((t.intValue() & 0xF) == 12) {
                f = t;
            } else {
                return false;
            }
        }
        this.createSignatureBlock(this.trailer);
        byte[] fBlock = BigIntegers.asUnsignedByteArray(this.block.length, f);
        boolean rv = Arrays.constantTimeAreEqual(this.block, fBlock);
        if (this.trailer == 15052 && !rv) {
            this.block[this.block.length - 2] = 64;
            rv = Arrays.constantTimeAreEqual(this.block, fBlock);
        }
        this.clearBlock(this.block);
        this.clearBlock(fBlock);
        return rv;
    }
}

