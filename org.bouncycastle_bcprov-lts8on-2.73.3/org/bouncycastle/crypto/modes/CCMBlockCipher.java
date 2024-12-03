/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.modes.CCMModeCipher;
import org.bouncycastle.crypto.modes.NativeCCMProvider;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CCMBlockCipher
implements CCMModeCipher {
    private BlockCipher cipher;
    private int blockSize;
    private boolean forEncryption;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private int macSize;
    private CipherParameters keyParam;
    private byte[] macBlock;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();

    public static CCMModeCipher newInstance(BlockCipher cipher) {
        if (cipher instanceof NativeCCMProvider) {
            NativeCCMProvider engine = (NativeCCMProvider)((Object)cipher);
            return engine.createCCM();
        }
        return new CCMBlockCipher(cipher);
    }

    public CCMBlockCipher(BlockCipher c) {
        this.cipher = c;
        this.blockSize = c.getBlockSize();
        this.macBlock = new byte[this.blockSize];
        if (this.blockSize != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters cipherParameters;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters)params;
            this.nonce = param.getNonce();
            this.initialAssociatedText = param.getAssociatedText();
            this.macSize = this.getMacSize(forEncryption, param.getMacSize());
            cipherParameters = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param = (ParametersWithIV)params;
            this.nonce = param.getIV();
            this.initialAssociatedText = null;
            this.macSize = this.getMacSize(forEncryption, 64);
            cipherParameters = param.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to CCM: " + params.getClass().getName());
        }
        if (cipherParameters != null) {
            this.keyParam = cipherParameters;
        }
        if (this.nonce == null || this.nonce.length < 7 || this.nonce.length > 13) {
            throw new IllegalArgumentException("nonce must have length from 7 to 13 octets");
        }
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CCM";
    }

    @Override
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.associatedText.write(in, inOff, len);
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in);
        return 0;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length < inOff + inLen) {
            throw new DataLengthException("Input buffer too short");
        }
        this.data.write(in, inOff, inLen);
        return 0;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int len = this.processPacket(this.data.getBuffer(), 0, this.data.size(), out, outOff);
        this.reset();
        return len;
    }

    @Override
    public void reset() {
        this.cipher.reset();
        this.associatedText.reset();
        this.data.reset();
    }

    @Override
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, mac.length);
        return mac;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return 0;
    }

    @Override
    public int getOutputSize(int len) {
        int totalData = len + this.data.size();
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        return totalData < this.macSize ? 0 : totalData - this.macSize;
    }

    @Override
    public byte[] processPacket(byte[] in, int inOff, int inLen) throws IllegalStateException, InvalidCipherTextException {
        byte[] output;
        if (this.forEncryption) {
            output = new byte[inLen + this.macSize];
        } else {
            if (inLen < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            output = new byte[inLen - this.macSize];
        }
        this.processPacket(in, inOff, inLen, output, 0);
        return output;
    }

    @Override
    public int processPacket(byte[] in, int inOff, int inLen, byte[] output, int outOff) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
        int inIndex;
        int outputLen;
        int limitLen;
        if (this.keyParam == null) {
            throw new IllegalStateException("CCM cipher unitialized.");
        }
        int n = this.nonce.length;
        int q = 15 - n;
        if (q < 4 && inLen >= (limitLen = 1 << 8 * q)) {
            throw new IllegalStateException("CCM packet too large for choice of q.");
        }
        byte[] iv = new byte[this.blockSize];
        iv[0] = (byte)(q - 1 & 7);
        System.arraycopy(this.nonce, 0, iv, 1, this.nonce.length);
        SICBlockCipher ctrCipher = new SICBlockCipher(this.cipher);
        ctrCipher.init(this.forEncryption, new ParametersWithIV(this.keyParam, iv));
        int outIndex = outOff;
        if (this.forEncryption) {
            outputLen = inLen + this.macSize;
            if (output.length < outputLen + outOff) {
                throw new OutputLengthException("Output buffer too short.");
            }
            this.calculateMac(in, inOff, inLen, this.macBlock);
            byte[] encMac = new byte[this.blockSize];
            ctrCipher.processBlock(this.macBlock, 0, encMac, 0);
            for (inIndex = inOff; inIndex < inOff + inLen - this.blockSize; inIndex += this.blockSize) {
                ctrCipher.processBlock(in, inIndex, output, outIndex);
                outIndex += this.blockSize;
            }
            byte[] block = new byte[this.blockSize];
            System.arraycopy(in, inIndex, block, 0, inLen + inOff - inIndex);
            ctrCipher.processBlock(block, 0, block, 0);
            System.arraycopy(block, 0, output, outIndex, inLen + inOff - inIndex);
            System.arraycopy(encMac, 0, output, outOff + inLen, this.macSize);
        } else {
            if (inLen < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            outputLen = inLen - this.macSize;
            if (output.length < outputLen + outOff) {
                throw new OutputLengthException("Output buffer too short.");
            }
            System.arraycopy(in, inOff + outputLen, this.macBlock, 0, this.macSize);
            ctrCipher.processBlock(this.macBlock, 0, this.macBlock, 0);
            for (int i = this.macSize; i != this.macBlock.length; ++i) {
                this.macBlock[i] = 0;
            }
            while (inIndex < inOff + outputLen - this.blockSize) {
                ctrCipher.processBlock(in, inIndex, output, outIndex);
                outIndex += this.blockSize;
                inIndex += this.blockSize;
            }
            byte[] block = new byte[this.blockSize];
            System.arraycopy(in, inIndex, block, 0, outputLen - (inIndex - inOff));
            ctrCipher.processBlock(block, 0, block, 0);
            System.arraycopy(block, 0, output, outIndex, outputLen - (inIndex - inOff));
            byte[] calculatedMacBlock = new byte[this.blockSize];
            this.calculateMac(output, outOff, outputLen, calculatedMacBlock);
            if (!Arrays.constantTimeAreEqual(this.macBlock, calculatedMacBlock)) {
                throw new InvalidCipherTextException("mac check in CCM failed");
            }
        }
        return outputLen;
    }

    private int calculateMac(byte[] data, int dataOff, int dataLen, byte[] macBlock) {
        CBCBlockCipherMac cMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
        cMac.init(this.keyParam);
        byte[] b0 = new byte[16];
        if (this.hasAssociatedText()) {
            b0[0] = (byte)(b0[0] | 0x40);
        }
        b0[0] = (byte)(b0[0] | ((cMac.getMacSize() - 2) / 2 & 7) << 3);
        b0[0] = (byte)(b0[0] | 15 - this.nonce.length - 1 & 7);
        System.arraycopy(this.nonce, 0, b0, 1, this.nonce.length);
        int q = dataLen;
        int count = 1;
        while (q > 0) {
            b0[b0.length - count] = (byte)(q & 0xFF);
            q >>>= 8;
            ++count;
        }
        cMac.update(b0, 0, b0.length);
        if (this.hasAssociatedText()) {
            int extra;
            int textLength = this.getAssociatedTextLength();
            if (textLength < 65280) {
                cMac.update((byte)(textLength >> 8));
                cMac.update((byte)textLength);
                extra = 2;
            } else {
                cMac.update((byte)-1);
                cMac.update((byte)-2);
                cMac.update((byte)(textLength >> 24));
                cMac.update((byte)(textLength >> 16));
                cMac.update((byte)(textLength >> 8));
                cMac.update((byte)textLength);
                extra = 6;
            }
            if (this.initialAssociatedText != null) {
                cMac.update(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
            if (this.associatedText.size() > 0) {
                cMac.update(this.associatedText.getBuffer(), 0, this.associatedText.size());
            }
            if ((extra = (extra + textLength) % 16) != 0) {
                for (int i = extra; i != 16; ++i) {
                    cMac.update((byte)0);
                }
            }
        }
        cMac.update(data, dataOff, dataLen);
        return cMac.doFinal(macBlock, 0);
    }

    private int getMacSize(boolean forEncryption, int requestedMacBits) {
        if (forEncryption && (requestedMacBits < 32 || requestedMacBits > 128 || 0 != (requestedMacBits & 0xF))) {
            throw new IllegalArgumentException("tag length in octets must be one of {4,6,8,10,12,14,16}");
        }
        return requestedMacBits >>> 3;
    }

    private int getAssociatedTextLength() {
        return this.associatedText.size() + (this.initialAssociatedText == null ? 0 : this.initialAssociatedText.length);
    }

    private boolean hasAssociatedText() {
        return this.getAssociatedTextLength() > 0;
    }

    public String toString() {
        return "CCM[Java](" + this.cipher.toString() + ")";
    }

    private static class ExposedByteArrayOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}

