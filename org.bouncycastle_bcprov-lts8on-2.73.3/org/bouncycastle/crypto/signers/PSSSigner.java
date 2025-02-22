/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class PSSSigner
implements Signer {
    public static final byte TRAILER_IMPLICIT = -68;
    private Digest contentDigest1;
    private Digest contentDigest2;
    private Digest mgfDigest;
    private AsymmetricBlockCipher cipher;
    private SecureRandom random;
    private int hLen;
    private int mgfhLen;
    private boolean sSet;
    private int sLen;
    private int emBits;
    private byte[] salt;
    private byte[] mDash;
    private byte[] block;
    private byte trailer;

    public static PSSSigner createRawSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, int sLen, byte trailer) {
        return new PSSSigner(cipher, (Digest)new NullDigest(), contentDigest, mgfDigest, sLen, trailer);
    }

    public static PSSSigner createRawSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, byte[] salt, byte trailer) {
        return new PSSSigner(cipher, (Digest)new NullDigest(), contentDigest, mgfDigest, salt, trailer);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int sLen) {
        this(cipher, digest, sLen, -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, int sLen) {
        this(cipher, contentDigest, mgfDigest, sLen, -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, int sLen, byte trailer) {
        this(cipher, digest, digest, sLen, trailer);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, int sLen, byte trailer) {
        this(cipher, contentDigest, contentDigest, mgfDigest, sLen, trailer);
    }

    private PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest1, Digest contentDigest2, Digest mgfDigest, int sLen, byte trailer) {
        this.cipher = cipher;
        this.contentDigest1 = contentDigest1;
        this.contentDigest2 = contentDigest2;
        this.mgfDigest = mgfDigest;
        this.hLen = contentDigest2.getDigestSize();
        this.mgfhLen = mgfDigest.getDigestSize();
        this.sSet = false;
        this.sLen = sLen;
        this.salt = new byte[sLen];
        this.mDash = new byte[8 + sLen + this.hLen];
        this.trailer = trailer;
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest digest, byte[] salt) {
        this(cipher, digest, digest, salt, -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, byte[] salt) {
        this(cipher, contentDigest, mgfDigest, salt, -68);
    }

    public PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest, Digest mgfDigest, byte[] salt, byte trailer) {
        this(cipher, contentDigest, contentDigest, mgfDigest, salt, trailer);
    }

    private PSSSigner(AsymmetricBlockCipher cipher, Digest contentDigest1, Digest contentDigest2, Digest mgfDigest, byte[] salt, byte trailer) {
        this.cipher = cipher;
        this.contentDigest1 = contentDigest1;
        this.contentDigest2 = contentDigest2;
        this.mgfDigest = mgfDigest;
        this.hLen = contentDigest2.getDigestSize();
        this.mgfhLen = mgfDigest.getDigestSize();
        this.sSet = true;
        this.sLen = salt.length;
        this.salt = salt;
        this.mDash = new byte[8 + this.sLen + this.hLen];
        this.trailer = trailer;
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        RSAKeyParameters kParam;
        CipherParameters params;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom)param;
            params = p.getParameters();
            this.random = p.getRandom();
        } else {
            params = param;
            if (forSigning) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
        }
        if (params instanceof RSABlindingParameters) {
            kParam = ((RSABlindingParameters)params).getPublicKey();
            this.cipher.init(forSigning, param);
        } else {
            kParam = (RSAKeyParameters)params;
            this.cipher.init(forSigning, params);
        }
        this.emBits = kParam.getModulus().bitLength() - 1;
        if (this.emBits < 8 * this.hLen + 8 * this.sLen + 9) {
            throw new IllegalArgumentException("key too small for specified hash and salt lengths");
        }
        this.block = new byte[(this.emBits + 7) / 8];
        this.reset();
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; ++i) {
            block[i] = 0;
        }
    }

    @Override
    public void update(byte b) {
        this.contentDigest1.update(b);
    }

    @Override
    public void update(byte[] in, int off, int len) {
        this.contentDigest1.update(in, off, len);
    }

    @Override
    public void reset() {
        this.contentDigest1.reset();
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (this.contentDigest1.getDigestSize() != this.hLen) {
            throw new IllegalStateException();
        }
        this.contentDigest1.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);
        if (this.sLen != 0) {
            if (!this.sSet) {
                this.random.nextBytes(this.salt);
            }
            System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
        }
        byte[] h = new byte[this.hLen];
        this.contentDigest2.update(this.mDash, 0, this.mDash.length);
        this.contentDigest2.doFinal(h, 0);
        this.block[this.block.length - this.sLen - 1 - this.hLen - 1] = 1;
        System.arraycopy(this.salt, 0, this.block, this.block.length - this.sLen - this.hLen - 1, this.sLen);
        byte[] dbMask = this.maskGenerator(h, 0, h.length, this.block.length - this.hLen - 1);
        for (int i = 0; i != dbMask.length; ++i) {
            int n = i;
            this.block[n] = (byte)(this.block[n] ^ dbMask[i]);
        }
        System.arraycopy(h, 0, this.block, this.block.length - this.hLen - 1, this.hLen);
        int firstByteMask = 255 >>> this.block.length * 8 - this.emBits;
        this.block[0] = (byte)(this.block[0] & firstByteMask);
        this.block[this.block.length - 1] = this.trailer;
        byte[] b = this.cipher.processBlock(this.block, 0, this.block.length);
        this.clearBlock(this.block);
        return b;
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        int i;
        if (this.contentDigest1.getDigestSize() != this.hLen) {
            throw new IllegalStateException();
        }
        this.contentDigest1.doFinal(this.mDash, this.mDash.length - this.hLen - this.sLen);
        try {
            byte[] b = this.cipher.processBlock(signature, 0, signature.length);
            Arrays.fill(this.block, 0, this.block.length - b.length, (byte)0);
            System.arraycopy(b, 0, this.block, this.block.length - b.length, b.length);
        }
        catch (Exception e) {
            return false;
        }
        int firstByteMask = 255 >>> this.block.length * 8 - this.emBits;
        if ((this.block[0] & 0xFF) != (this.block[0] & firstByteMask) || this.block[this.block.length - 1] != this.trailer) {
            this.clearBlock(this.block);
            return false;
        }
        byte[] dbMask = this.maskGenerator(this.block, this.block.length - this.hLen - 1, this.hLen, this.block.length - this.hLen - 1);
        for (i = 0; i != dbMask.length; ++i) {
            int n = i;
            this.block[n] = (byte)(this.block[n] ^ dbMask[i]);
        }
        this.block[0] = (byte)(this.block[0] & firstByteMask);
        for (i = 0; i != this.block.length - this.hLen - this.sLen - 2; ++i) {
            if (this.block[i] == 0) continue;
            this.clearBlock(this.block);
            return false;
        }
        if (this.block[this.block.length - this.hLen - this.sLen - 2] != 1) {
            this.clearBlock(this.block);
            return false;
        }
        if (this.sSet) {
            System.arraycopy(this.salt, 0, this.mDash, this.mDash.length - this.sLen, this.sLen);
        } else {
            System.arraycopy(this.block, this.block.length - this.sLen - this.hLen - 1, this.mDash, this.mDash.length - this.sLen, this.sLen);
        }
        this.contentDigest2.update(this.mDash, 0, this.mDash.length);
        this.contentDigest2.doFinal(this.mDash, this.mDash.length - this.hLen);
        i = this.block.length - this.hLen - 1;
        for (int j = this.mDash.length - this.hLen; j != this.mDash.length; ++j) {
            if ((this.block[i] ^ this.mDash[j]) != 0) {
                this.clearBlock(this.mDash);
                this.clearBlock(this.block);
                return false;
            }
            ++i;
        }
        this.clearBlock(this.mDash);
        this.clearBlock(this.block);
        return true;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte)(i >>> 24);
        sp[1] = (byte)(i >>> 16);
        sp[2] = (byte)(i >>> 8);
        sp[3] = (byte)(i >>> 0);
    }

    private byte[] maskGenerator(byte[] Z, int zOff, int zLen, int length) {
        if (this.mgfDigest instanceof Xof) {
            byte[] mask = new byte[length];
            this.mgfDigest.update(Z, zOff, zLen);
            ((Xof)this.mgfDigest).doFinal(mask, 0, mask.length);
            return mask;
        }
        return this.maskGeneratorFunction1(Z, zOff, zLen, length);
    }

    private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length) {
        int counter;
        byte[] mask = new byte[length];
        byte[] hashBuf = new byte[this.mgfhLen];
        byte[] C = new byte[4];
        this.mgfDigest.reset();
        for (counter = 0; counter < length / this.mgfhLen; ++counter) {
            this.ItoOSP(counter, C);
            this.mgfDigest.update(Z, zOff, zLen);
            this.mgfDigest.update(C, 0, C.length);
            this.mgfDigest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.mgfhLen, this.mgfhLen);
        }
        if (counter * this.mgfhLen < length) {
            this.ItoOSP(counter, C);
            this.mgfDigest.update(Z, zOff, zLen);
            this.mgfDigest.update(C, 0, C.length);
            this.mgfDigest.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * this.mgfhLen, mask.length - counter * this.mgfhLen);
        }
        return mask;
    }
}

