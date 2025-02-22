/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.util.Arrays;

public class ISO9796d2Signer
implements SignerWithRecovery {
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private int trailer;
    private int keyBits;
    private byte[] block;
    private byte[] mBuf;
    private int messageLength;
    private boolean fullMessage;
    private byte[] recoveredMessage;
    private byte[] preSig;
    private byte[] preBlock;

    public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest, boolean implicit) {
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

    public ISO9796d2Signer(AsymmetricBlockCipher cipher, Digest digest) {
        this(cipher, digest, false);
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        RSAKeyParameters kParam = (RSAKeyParameters)param;
        this.cipher.init(forSigning, kParam);
        this.keyBits = kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        this.mBuf = this.trailer == 188 ? new byte[this.block.length - this.digest.getDigestSize() - 2] : new byte[this.block.length - this.digest.getDigestSize() - 3];
        this.reset();
    }

    private boolean isSameAs(byte[] a, byte[] b) {
        boolean isOkay = true;
        if (this.messageLength > this.mBuf.length) {
            if (this.mBuf.length > b.length) {
                isOkay = false;
            }
            for (int i = 0; i != this.mBuf.length; ++i) {
                if (a[i] == b[i]) continue;
                isOkay = false;
            }
        } else {
            if (this.messageLength != b.length) {
                isOkay = false;
            }
            for (int i = 0; i != b.length; ++i) {
                if (a[i] == b[i]) continue;
                isOkay = false;
            }
        }
        return isOkay;
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; ++i) {
            block[i] = 0;
        }
    }

    @Override
    public void updateWithRecoveredMessage(byte[] signature) throws InvalidCipherTextException {
        byte[] block = this.cipher.processBlock(signature, 0, signature.length);
        if ((block[0] & 0xC0 ^ 0x40) != 0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        if ((block[block.length - 1] & 0xF ^ 0xC) != 0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        int delta = 0;
        if ((block[block.length - 1] & 0xFF ^ 0xBC) == 0) {
            delta = 1;
        } else {
            int sigTrail = (block[block.length - 2] & 0xFF) << 8 | block[block.length - 1] & 0xFF;
            Integer trailerObj = ISOTrailers.getTrailer(this.digest);
            if (trailerObj != null) {
                int trailer = trailerObj;
                if (sigTrail != trailer && (trailer != 15052 || sigTrail != 16588)) {
                    throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                }
            } else {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            delta = 2;
        }
        int mStart = 0;
        for (mStart = 0; mStart != block.length && (block[mStart] & 0xF ^ 0xA) != 0; ++mStart) {
        }
        int off = block.length - delta - this.digest.getDigestSize();
        if (off - ++mStart <= 0) {
            throw new InvalidCipherTextException("malformed block");
        }
        if ((block[0] & 0x20) == 0) {
            this.fullMessage = true;
            this.recoveredMessage = new byte[off - mStart];
            System.arraycopy(block, mStart, this.recoveredMessage, 0, this.recoveredMessage.length);
        } else {
            this.fullMessage = false;
            this.recoveredMessage = new byte[off - mStart];
            System.arraycopy(block, mStart, this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.preSig = signature;
        this.preBlock = block;
        this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = this.recoveredMessage.length;
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
    }

    @Override
    public void update(byte b) {
        this.digest.update(b);
        if (this.messageLength < this.mBuf.length) {
            this.mBuf[this.messageLength] = b;
        }
        ++this.messageLength;
    }

    @Override
    public void update(byte[] in, int off, int len) {
        while (len > 0 && this.messageLength < this.mBuf.length) {
            this.update(in[off]);
            ++off;
            --len;
        }
        this.digest.update(in, off, len);
        this.messageLength += len;
    }

    @Override
    public void reset() {
        this.digest.reset();
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        if (this.recoveredMessage != null) {
            this.clearBlock(this.recoveredMessage);
        }
        this.recoveredMessage = null;
        this.fullMessage = false;
        if (this.preSig != null) {
            this.preSig = null;
            this.clearBlock(this.preBlock);
            this.preBlock = null;
        }
    }

    @Override
    public byte[] generateSignature() throws CryptoException {
        int digSize = this.digest.getDigestSize();
        int t = 0;
        int delta = 0;
        if (this.trailer == 188) {
            t = 8;
            delta = this.block.length - digSize - 1;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 1] = -68;
        } else {
            t = 16;
            delta = this.block.length - digSize - 2;
            this.digest.doFinal(this.block, delta);
            this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte)this.trailer;
        }
        int header = 0;
        int x = (digSize + this.messageLength) * 8 + t + 4 - this.keyBits;
        if (x > 0) {
            int mR = this.messageLength - (x + 7) / 8;
            header = 96;
            System.arraycopy(this.mBuf, 0, this.block, delta -= mR, mR);
            this.recoveredMessage = new byte[mR];
        } else {
            header = 64;
            System.arraycopy(this.mBuf, 0, this.block, delta -= this.messageLength, this.messageLength);
            this.recoveredMessage = new byte[this.messageLength];
        }
        if (delta - 1 > 0) {
            for (int i = delta - 1; i != 0; --i) {
                this.block[i] = -69;
            }
            int n = delta - 1;
            this.block[n] = (byte)(this.block[n] ^ 1);
            this.block[0] = 11;
            this.block[0] = (byte)(this.block[0] | header);
        } else {
            this.block[0] = 10;
            this.block[0] = (byte)(this.block[0] | header);
        }
        byte[] b = this.cipher.processBlock(this.block, 0, this.block.length);
        this.fullMessage = (header & 0x20) == 0;
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        this.clearBlock(this.block);
        return b;
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        byte[] block = null;
        if (this.preSig == null) {
            try {
                block = this.cipher.processBlock(signature, 0, signature.length);
            }
            catch (Exception e) {
                return false;
            }
        } else {
            if (!Arrays.areEqual(this.preSig, signature)) {
                throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
            }
            block = this.preBlock;
            this.preSig = null;
            this.preBlock = null;
        }
        if ((block[0] & 0xC0 ^ 0x40) != 0) {
            return this.returnFalse(block);
        }
        if ((block[block.length - 1] & 0xF ^ 0xC) != 0) {
            return this.returnFalse(block);
        }
        int delta = 0;
        if ((block[block.length - 1] & 0xFF ^ 0xBC) == 0) {
            delta = 1;
        } else {
            int sigTrail = (block[block.length - 2] & 0xFF) << 8 | block[block.length - 1] & 0xFF;
            Integer trailerObj = ISOTrailers.getTrailer(this.digest);
            if (trailerObj != null) {
                int trailer = trailerObj;
                if (sigTrail != trailer && (trailer != 15052 || sigTrail != 16588)) {
                    throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                }
            } else {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            delta = 2;
        }
        int mStart = 0;
        for (mStart = 0; mStart != block.length && (block[mStart] & 0xF ^ 0xA) != 0; ++mStart) {
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        int off = block.length - delta - hash.length;
        if (off - ++mStart <= 0) {
            return this.returnFalse(block);
        }
        if ((block[0] & 0x20) == 0) {
            this.fullMessage = true;
            if (this.messageLength > off - mStart) {
                return this.returnFalse(block);
            }
            this.digest.reset();
            this.digest.update(block, mStart, off - mStart);
            this.digest.doFinal(hash, 0);
            boolean isOkay = true;
            for (int i = 0; i != hash.length; ++i) {
                int n = off + i;
                block[n] = (byte)(block[n] ^ hash[i]);
                if (block[off + i] == 0) continue;
                isOkay = false;
            }
            if (!isOkay) {
                return this.returnFalse(block);
            }
            this.recoveredMessage = new byte[off - mStart];
            System.arraycopy(block, mStart, this.recoveredMessage, 0, this.recoveredMessage.length);
        } else {
            this.fullMessage = false;
            this.digest.doFinal(hash, 0);
            boolean isOkay = true;
            for (int i = 0; i != hash.length; ++i) {
                int n = off + i;
                block[n] = (byte)(block[n] ^ hash[i]);
                if (block[off + i] == 0) continue;
                isOkay = false;
            }
            if (!isOkay) {
                return this.returnFalse(block);
            }
            this.recoveredMessage = new byte[off - mStart];
            System.arraycopy(block, mStart, this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        if (this.messageLength != 0 && !this.isSameAs(this.mBuf, this.recoveredMessage)) {
            return this.returnFalse(block);
        }
        this.clearBlock(this.mBuf);
        this.clearBlock(block);
        this.messageLength = 0;
        return true;
    }

    private boolean returnFalse(byte[] block) {
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        this.clearBlock(block);
        return false;
    }

    @Override
    public boolean hasFullMessage() {
        return this.fullMessage;
    }

    @Override
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }
}

