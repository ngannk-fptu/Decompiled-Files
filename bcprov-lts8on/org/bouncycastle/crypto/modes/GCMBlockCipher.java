/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.NativeBlockCipherProvider;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.modes.gcm.BasicGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMBlockCipher
implements GCMModeCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher cipher;
    private GCMMultiplier multiplier;
    private GCMExponentiator exp;
    private boolean forEncryption;
    private boolean initialised;
    private int macSize;
    private byte[] lastKey;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private byte[] H;
    private byte[] J0;
    private byte[] bufBlock;
    private byte[] macBlock;
    private byte[] S;
    private byte[] S_at;
    private byte[] S_atPre;
    private byte[] counter;
    private int blocksRemaining;
    private int bufOff;
    private long totalLength;
    private byte[] atBlock;
    private int atBlockPos;
    private long atLength;
    private long atLengthPre;

    public static GCMModeCipher newInstance(BlockCipher cipher) {
        if (cipher instanceof NativeBlockCipherProvider) {
            NativeBlockCipherProvider engine = (NativeBlockCipherProvider)((Object)cipher);
            return engine.createGCM();
        }
        return new GCMBlockCipher(cipher);
    }

    public static GCMModeCipher newInstance(BlockCipher cipher, GCMMultiplier m) {
        return new GCMBlockCipher(cipher, m);
    }

    public GCMBlockCipher(BlockCipher c) {
        this(c, null);
    }

    public GCMBlockCipher(BlockCipher c, GCMMultiplier m) {
        if (c.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        if (m == null) {
            m = new Tables4kGCMMultiplier();
        }
        this.cipher = c;
        this.multiplier = m;
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        KeyParameter keyParam;
        CipherParameters param;
        this.forEncryption = forEncryption;
        this.macBlock = null;
        this.initialised = true;
        byte[] newNonce = null;
        if (params instanceof AEADParameters) {
            param = (AEADParameters)params;
            newNonce = ((AEADParameters)param).getNonce();
            this.initialAssociatedText = ((AEADParameters)param).getAssociatedText();
            int macSizeBits = ((AEADParameters)param).getMacSize();
            if (macSizeBits < 32 || macSizeBits > 128 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits / 8;
            keyParam = ((AEADParameters)param).getKey();
        } else if (params instanceof ParametersWithIV) {
            param = (ParametersWithIV)params;
            newNonce = ((ParametersWithIV)param).getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            keyParam = (KeyParameter)((ParametersWithIV)param).getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
        }
        int bufLength = forEncryption ? 16 : 16 + this.macSize;
        this.bufBlock = new byte[bufLength];
        if (newNonce == null || newNonce.length < 12) {
            throw new IllegalArgumentException("IV must be at least 12 byte");
        }
        if (forEncryption && this.nonce != null && Arrays.areEqual(this.nonce, newNonce)) {
            if (keyParam == null) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
            if (this.lastKey != null && Arrays.areEqual(this.lastKey, keyParam.getKey())) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
        }
        this.nonce = newNonce;
        if (keyParam != null) {
            this.lastKey = keyParam.getKey();
        }
        if (keyParam != null) {
            this.cipher.init(true, keyParam);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
        } else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
        }
        this.J0 = new byte[16];
        if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        } else {
            this.gHASH(this.J0, this.nonce, this.nonce.length);
            byte[] X = new byte[16];
            Pack.longToBigEndian((long)this.nonce.length * 8L, X, 8);
            this.gHASHBlock(this.J0, X);
        }
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }

    @Override
    public int getOutputSize(int len) {
        int totalData = len + this.bufOff;
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        return totalData < this.macSize ? 0 : totalData - this.macSize;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int totalData = len + this.bufOff;
        if (!this.forEncryption) {
            if (totalData < this.macSize) {
                return 0;
            }
            totalData -= this.macSize;
        }
        return totalData - totalData % 16;
    }

    @Override
    public void processAADByte(byte in) {
        this.checkStatus();
        this.atBlock[this.atBlockPos] = in;
        if (++this.atBlockPos == 16) {
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16L;
        }
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.checkStatus();
        if (this.atBlockPos > 0) {
            int available = 16 - this.atBlockPos;
            if (len < available) {
                System.arraycopy(in, inOff, this.atBlock, this.atBlockPos, len);
                this.atBlockPos += len;
                return;
            }
            System.arraycopy(in, inOff, this.atBlock, this.atBlockPos, available);
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atLength += 16L;
            inOff += available;
            len -= available;
        }
        int inLimit = inOff + len - 16;
        while (inOff <= inLimit) {
            this.gHASHBlock(this.S_at, in, inOff);
            this.atLength += 16L;
            inOff += 16;
        }
        this.atBlockPos = 16 + inLimit - inOff;
        System.arraycopy(in, inOff, this.atBlock, 0, this.atBlockPos);
    }

    private void initCipher() {
        if (this.atLength > 0L) {
            System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
            this.atLengthPre = this.atLength;
        }
        if (this.atBlockPos > 0) {
            this.gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
            this.atLengthPre += (long)this.atBlockPos;
        }
        if (this.atLengthPre > 0L) {
            System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
        }
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        this.checkStatus();
        this.bufBlock[this.bufOff] = in;
        if (++this.bufOff == this.bufBlock.length) {
            if (this.forEncryption) {
                this.encryptBlock(this.bufBlock, 0, out, outOff);
                this.bufOff = 0;
            } else {
                this.decryptBlock(this.bufBlock, 0, out, outOff);
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
                this.bufOff = this.macSize;
            }
            return 16;
        }
        return 0;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        this.checkStatus();
        if (in.length - inOff < len) {
            throw new DataLengthException("Input buffer too short");
        }
        int resultLen = 0;
        if (this.forEncryption) {
            if (this.bufOff > 0) {
                int available = 16 - this.bufOff;
                if (len < available) {
                    System.arraycopy(in, inOff, this.bufBlock, this.bufOff, len);
                    this.bufOff += len;
                    return 0;
                }
                System.arraycopy(in, inOff, this.bufBlock, this.bufOff, available);
                this.encryptBlock(this.bufBlock, 0, out, outOff);
                inOff += available;
                len -= available;
                resultLen = 16;
            }
            int inLimit = inOff + len - 16;
            while (inOff <= inLimit) {
                this.encryptBlock(in, inOff, out, outOff + resultLen);
                inOff += 16;
                resultLen += 16;
            }
            this.bufOff = 16 + inLimit - inOff;
            System.arraycopy(in, inOff, this.bufBlock, 0, this.bufOff);
        } else {
            int available = this.bufBlock.length - this.bufOff;
            if (len < available) {
                System.arraycopy(in, inOff, this.bufBlock, this.bufOff, len);
                this.bufOff += len;
                return 0;
            }
            if (this.bufOff >= 16) {
                this.decryptBlock(this.bufBlock, 0, out, outOff);
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.bufOff -= 16);
                resultLen = 16;
                if (len < (available += 16)) {
                    System.arraycopy(in, inOff, this.bufBlock, this.bufOff, len);
                    this.bufOff += len;
                    return resultLen;
                }
            }
            int inLimit = inOff + len - this.bufBlock.length;
            available = 16 - this.bufOff;
            System.arraycopy(in, inOff, this.bufBlock, this.bufOff, available);
            this.decryptBlock(this.bufBlock, 0, out, outOff + resultLen);
            inOff += available;
            resultLen += 16;
            while (inOff <= inLimit) {
                this.decryptBlock(in, inOff, out, outOff + resultLen);
                inOff += 16;
                resultLen += 16;
            }
            this.bufOff = this.bufBlock.length + inLimit - inOff;
            System.arraycopy(in, inOff, this.bufBlock, 0, this.bufOff);
        }
        return resultLen;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        this.checkStatus();
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        int extra = this.bufOff;
        if (this.forEncryption) {
            if (out.length - outOff < extra + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
        } else {
            if (extra < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            if (out.length - outOff < (extra -= this.macSize)) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        if (extra > 0) {
            this.processPartial(this.bufBlock, 0, extra, out, outOff);
        }
        this.atLength += (long)this.atBlockPos;
        if (this.atLength > this.atLengthPre) {
            if (this.atBlockPos > 0) {
                this.gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
            }
            if (this.atLengthPre > 0L) {
                GCMUtil.xor(this.S_at, this.S_atPre);
            }
            long c = this.totalLength * 8L + 127L >>> 7;
            byte[] H_c = new byte[16];
            if (this.exp == null) {
                this.exp = new BasicGCMExponentiator();
                this.exp.init(this.H);
            }
            this.exp.exponentiateX(c, H_c);
            GCMUtil.multiply(this.S_at, H_c);
            GCMUtil.xor(this.S, this.S_at);
        }
        byte[] X = new byte[16];
        Pack.longToBigEndian(this.atLength * 8L, X, 0);
        Pack.longToBigEndian(this.totalLength * 8L, X, 8);
        this.gHASHBlock(this.S, X);
        byte[] tag = new byte[16];
        this.cipher.processBlock(this.J0, 0, tag, 0);
        GCMUtil.xor(tag, this.S);
        int resultLen = extra;
        this.macBlock = new byte[this.macSize];
        System.arraycopy(tag, 0, this.macBlock, 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, out, outOff + this.bufOff, this.macSize);
            resultLen += this.macSize;
        } else {
            byte[] msgMac = new byte[this.macSize];
            System.arraycopy(this.bufBlock, extra, msgMac, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, msgMac)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        this.reset(false);
        return resultLen;
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    private void reset(boolean clearMac) {
        this.cipher.reset();
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte)0);
        }
        if (clearMac) {
            this.macBlock = null;
        }
        if (this.forEncryption) {
            this.initialised = false;
        } else if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void decryptBlock(byte[] buf, int bufOff, byte[] out, int outOff) {
        if (out.length - outOff < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        byte[] ctrBlock = new byte[16];
        this.getNextCTRBlock(ctrBlock);
        this.gHASHBlock(this.S, buf, bufOff);
        GCMUtil.xor(ctrBlock, 0, buf, bufOff, out, outOff);
        this.totalLength += 16L;
    }

    private void encryptBlock(byte[] buf, int bufOff, byte[] out, int outOff) {
        if (out.length - outOff < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        byte[] ctrBlock = new byte[16];
        this.getNextCTRBlock(ctrBlock);
        GCMUtil.xor(ctrBlock, buf, bufOff);
        this.gHASHBlock(this.S, ctrBlock);
        System.arraycopy(ctrBlock, 0, out, outOff, 16);
        this.totalLength += 16L;
    }

    private void processPartial(byte[] buf, int off, int len, byte[] out, int outOff) {
        byte[] ctrBlock = new byte[16];
        this.getNextCTRBlock(ctrBlock);
        if (this.forEncryption) {
            GCMUtil.xor(buf, off, ctrBlock, 0, len);
            this.gHASHPartial(this.S, buf, off, len);
        } else {
            this.gHASHPartial(this.S, buf, off, len);
            GCMUtil.xor(buf, off, ctrBlock, 0, len);
        }
        System.arraycopy(buf, off, out, outOff, len);
        this.totalLength += (long)len;
    }

    private void gHASH(byte[] Y, byte[] b, int len) {
        for (int pos = 0; pos < len; pos += 16) {
            int num = Math.min(len - pos, 16);
            this.gHASHPartial(Y, b, pos, num);
        }
    }

    private void gHASHBlock(byte[] Y, byte[] b) {
        GCMUtil.xor(Y, b);
        this.multiplier.multiplyH(Y);
    }

    private void gHASHBlock(byte[] Y, byte[] b, int off) {
        GCMUtil.xor(Y, b, off);
        this.multiplier.multiplyH(Y);
    }

    private void gHASHPartial(byte[] Y, byte[] b, int off, int len) {
        GCMUtil.xor(Y, b, off, len);
        this.multiplier.multiplyH(Y);
    }

    private void getNextCTRBlock(byte[] block) {
        if (this.blocksRemaining == 0) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        --this.blocksRemaining;
        int c = 1;
        this.counter[15] = (byte)(c += this.counter[15] & 0xFF);
        c >>>= 8;
        this.counter[14] = (byte)(c += this.counter[14] & 0xFF);
        c >>>= 8;
        this.counter[13] = (byte)(c += this.counter[13] & 0xFF);
        c >>>= 8;
        this.counter[12] = (byte)(c += this.counter[12] & 0xFF);
        this.cipher.processBlock(this.counter, 0, block, 0);
    }

    private void checkStatus() {
        if (!this.initialised) {
            if (this.forEncryption) {
                throw new IllegalStateException("GCM cipher cannot be reused for encryption");
            }
            throw new IllegalStateException("GCM cipher needs to be initialised");
        }
    }

    public String toString() {
        return "GCM[Java](" + this.cipher.toString() + ")";
    }
}

