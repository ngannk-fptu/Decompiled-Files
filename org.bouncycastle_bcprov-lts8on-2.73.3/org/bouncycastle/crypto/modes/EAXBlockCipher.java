/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.EAXModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class EAXBlockCipher
implements EAXModeCipher {
    private static final byte nTAG = 0;
    private static final byte hTAG = 1;
    private static final byte cTAG = 2;
    private CTRModeCipher cipher;
    private boolean forEncryption;
    private int blockSize;
    private Mac mac;
    private byte[] nonceMac;
    private byte[] associatedTextMac;
    private byte[] macBlock;
    private int macSize;
    private byte[] bufBlock;
    private int bufOff;
    private boolean cipherInitialized;
    private byte[] initialAssociatedText;

    public EAXBlockCipher(BlockCipher cipher) {
        this.blockSize = cipher.getBlockSize();
        this.mac = new CMac(cipher);
        this.macBlock = new byte[this.blockSize];
        this.associatedTextMac = new byte[this.mac.getMacSize()];
        this.nonceMac = new byte[this.mac.getMacSize()];
        this.cipher = SICBlockCipher.newInstance(cipher);
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher.getUnderlyingCipher();
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters keyParam;
        byte[] nonce;
        CipherParameters param;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            param = (AEADParameters)params;
            nonce = ((AEADParameters)param).getNonce();
            this.initialAssociatedText = ((AEADParameters)param).getAssociatedText();
            this.macSize = ((AEADParameters)param).getMacSize() / 8;
            keyParam = ((AEADParameters)param).getKey();
        } else if (params instanceof ParametersWithIV) {
            param = (ParametersWithIV)params;
            nonce = ((ParametersWithIV)param).getIV();
            this.initialAssociatedText = null;
            this.macSize = this.mac.getMacSize() / 2;
            keyParam = ((ParametersWithIV)param).getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to EAX");
        }
        this.bufBlock = new byte[forEncryption ? this.blockSize : this.blockSize + this.macSize];
        byte[] tag = new byte[this.blockSize];
        this.mac.init(keyParam);
        tag[this.blockSize - 1] = 0;
        this.mac.update(tag, 0, this.blockSize);
        this.mac.update(nonce, 0, nonce.length);
        this.mac.doFinal(this.nonceMac, 0);
        this.cipher.init(true, new ParametersWithIV(keyParam, this.nonceMac));
        this.reset();
    }

    private void initCipher() {
        if (this.cipherInitialized) {
            return;
        }
        this.cipherInitialized = true;
        this.mac.doFinal(this.associatedTextMac, 0);
        byte[] tag = new byte[this.blockSize];
        tag[this.blockSize - 1] = 2;
        this.mac.update(tag, 0, this.blockSize);
    }

    private void calculateMac() {
        byte[] outC = new byte[this.blockSize];
        this.mac.doFinal(outC, 0);
        for (int i = 0; i < this.macBlock.length; ++i) {
            this.macBlock[i] = (byte)(this.nonceMac[i] ^ this.associatedTextMac[i] ^ outC[i]);
        }
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    private void reset(boolean clearMac) {
        this.cipher.reset();
        this.mac.reset();
        this.bufOff = 0;
        Arrays.fill(this.bufBlock, (byte)0);
        if (clearMac) {
            Arrays.fill(this.macBlock, (byte)0);
        }
        byte[] tag = new byte[this.blockSize];
        tag[this.blockSize - 1] = 1;
        this.mac.update(tag, 0, this.blockSize);
        this.cipherInitialized = false;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override
    public void processAADByte(byte in) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(in, inOff, len);
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        this.initCipher();
        return this.process(in, out, outOff);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        this.initCipher();
        if (in.length < inOff + len) {
            throw new DataLengthException("Input buffer too short");
        }
        int resultLen = 0;
        for (int i = 0; i != len; ++i) {
            resultLen += this.process(in[inOff + i], out, outOff + resultLen);
        }
        return resultLen;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        this.initCipher();
        int extra = this.bufOff;
        byte[] tmp = new byte[this.bufBlock.length];
        this.bufOff = 0;
        if (this.forEncryption) {
            if (out.length < outOff + extra + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            System.arraycopy(tmp, 0, out, outOff, extra);
            this.mac.update(tmp, 0, extra);
            this.calculateMac();
            System.arraycopy(this.macBlock, 0, out, outOff + extra, this.macSize);
            this.reset(false);
            return extra + this.macSize;
        }
        if (extra < this.macSize) {
            throw new InvalidCipherTextException("data too short");
        }
        if (out.length < outOff + extra - this.macSize) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (extra > this.macSize) {
            this.mac.update(this.bufBlock, 0, extra - this.macSize);
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            System.arraycopy(tmp, 0, out, outOff, extra - this.macSize);
        }
        this.calculateMac();
        if (!this.verifyMac(this.bufBlock, extra - this.macSize)) {
            throw new InvalidCipherTextException("mac check in EAX failed");
        }
        this.reset(false);
        return extra - this.macSize;
    }

    @Override
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, this.macSize);
        return mac;
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
        return totalData - totalData % this.blockSize;
    }

    @Override
    public int getOutputSize(int len) {
        int totalData = len + this.bufOff;
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        return totalData < this.macSize ? 0 : totalData - this.macSize;
    }

    private int process(byte b, byte[] out, int outOff) {
        this.bufBlock[this.bufOff++] = b;
        if (this.bufOff == this.bufBlock.length) {
            int size;
            if (out.length < outOff + this.blockSize) {
                throw new OutputLengthException("Output buffer is too short");
            }
            if (this.forEncryption) {
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
                this.mac.update(out, outOff, this.blockSize);
            } else {
                this.mac.update(this.bufBlock, 0, this.blockSize);
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
            }
            this.bufOff = 0;
            if (!this.forEncryption) {
                System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.macSize);
                this.bufOff = this.macSize;
            }
            return size;
        }
        return 0;
    }

    private boolean verifyMac(byte[] mac, int off) {
        int nonEqual = 0;
        for (int i = 0; i < this.macSize; ++i) {
            nonEqual |= this.macBlock[i] ^ mac[off + i];
        }
        return nonEqual == 0;
    }
}

