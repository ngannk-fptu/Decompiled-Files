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
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCCMBlockCipher
implements AEADBlockCipher {
    private static final int BYTES_IN_INT = 4;
    private static final int BITS_IN_BYTE = 8;
    private static final int MAX_MAC_BIT_LENGTH = 512;
    private static final int MIN_MAC_BIT_LENGTH = 64;
    private BlockCipher engine;
    private int macSize;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] mac;
    private byte[] macBlock;
    private byte[] nonce;
    private byte[] G1;
    private byte[] buffer;
    private byte[] s;
    private byte[] counter;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();
    private int Nb_ = 4;

    private void setNb(int Nb) {
        if (Nb != 4 && Nb != 6 && Nb != 8) {
            throw new IllegalArgumentException("Nb = 4 is recommended by DSTU7624 but can be changed to only 6 or 8 in this implementation");
        }
        this.Nb_ = Nb;
    }

    public KCCMBlockCipher(BlockCipher engine) {
        this(engine, 4);
    }

    public KCCMBlockCipher(BlockCipher engine, int nB) {
        this.engine = engine;
        this.macSize = engine.getBlockSize();
        this.nonce = new byte[engine.getBlockSize()];
        this.initialAssociatedText = new byte[engine.getBlockSize()];
        this.mac = new byte[engine.getBlockSize()];
        this.macBlock = new byte[engine.getBlockSize()];
        this.G1 = new byte[engine.getBlockSize()];
        this.buffer = new byte[engine.getBlockSize()];
        this.s = new byte[engine.getBlockSize()];
        this.counter = new byte[engine.getBlockSize()];
        this.setNb(nB);
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters cipherParameters;
        if (params instanceof AEADParameters) {
            AEADParameters parameters = (AEADParameters)params;
            if (parameters.getMacSize() > 512 || parameters.getMacSize() < 64 || parameters.getMacSize() % 8 != 0) {
                throw new IllegalArgumentException("Invalid mac size specified");
            }
            this.nonce = parameters.getNonce();
            this.macSize = parameters.getMacSize() / 8;
            this.initialAssociatedText = parameters.getAssociatedText();
            cipherParameters = parameters.getKey();
        } else if (params instanceof ParametersWithIV) {
            this.nonce = ((ParametersWithIV)params).getIV();
            this.macSize = this.engine.getBlockSize();
            this.initialAssociatedText = null;
            cipherParameters = ((ParametersWithIV)params).getParameters();
        } else {
            throw new IllegalArgumentException("Invalid parameters specified");
        }
        this.mac = new byte[this.macSize];
        this.forEncryption = forEncryption;
        this.engine.init(true, cipherParameters);
        this.counter[0] = 1;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCCM";
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.associatedText.write(in, inOff, len);
    }

    private void processAAD(byte[] assocText, int assocOff, int assocLen, int dataLen) {
        if (assocLen - assocOff < this.engine.getBlockSize()) {
            throw new IllegalArgumentException("authText buffer too short");
        }
        if (assocLen % this.engine.getBlockSize() != 0) {
            throw new IllegalArgumentException("padding not supported");
        }
        System.arraycopy(this.nonce, 0, this.G1, 0, this.nonce.length - this.Nb_ - 1);
        this.intToBytes(dataLen, this.buffer, 0);
        System.arraycopy(this.buffer, 0, this.G1, this.nonce.length - this.Nb_ - 1, 4);
        this.G1[this.G1.length - 1] = this.getFlag(true, this.macSize);
        this.engine.processBlock(this.G1, 0, this.macBlock, 0);
        this.intToBytes(assocLen, this.buffer, 0);
        if (assocLen <= this.engine.getBlockSize() - this.Nb_) {
            int byteIndex;
            for (byteIndex = 0; byteIndex < assocLen; ++byteIndex) {
                int n = byteIndex + this.Nb_;
                this.buffer[n] = (byte)(this.buffer[n] ^ assocText[assocOff + byteIndex]);
            }
            for (byteIndex = 0; byteIndex < this.engine.getBlockSize(); ++byteIndex) {
                int n = byteIndex;
                this.macBlock[n] = (byte)(this.macBlock[n] ^ this.buffer[byteIndex]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            return;
        }
        for (int byteIndex = 0; byteIndex < this.engine.getBlockSize(); ++byteIndex) {
            int n = byteIndex;
            this.macBlock[n] = (byte)(this.macBlock[n] ^ this.buffer[byteIndex]);
        }
        this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
        for (int authLen = assocLen; authLen != 0; authLen -= this.engine.getBlockSize()) {
            for (int byteIndex = 0; byteIndex < this.engine.getBlockSize(); ++byteIndex) {
                int n = byteIndex;
                this.macBlock[n] = (byte)(this.macBlock[n] ^ assocText[byteIndex + assocOff]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            assocOff += this.engine.getBlockSize();
        }
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in);
        return 0;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length < inOff + inLen) {
            throw new DataLengthException("input buffer too short");
        }
        this.data.write(in, inOff, inLen);
        return 0;
    }

    public int processPacket(byte[] in, int inOff, int len, byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int byteIndex;
        if (in.length - inOff < len) {
            throw new DataLengthException("input buffer too short");
        }
        if (out.length - outOff < len) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.associatedText.size() > 0) {
            if (this.forEncryption) {
                this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size());
            } else {
                this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size() - this.macSize);
            }
        }
        if (this.forEncryption) {
            int byteIndex2;
            if (len % this.engine.getBlockSize() != 0) {
                throw new DataLengthException("partial blocks not supported");
            }
            this.CalculateMac(in, inOff, len);
            this.engine.processBlock(this.nonce, 0, this.s, 0);
            int totalLength = len;
            while (totalLength > 0) {
                this.ProcessBlock(in, inOff, len, out, outOff);
                totalLength -= this.engine.getBlockSize();
                inOff += this.engine.getBlockSize();
                outOff += this.engine.getBlockSize();
            }
            for (byteIndex2 = 0; byteIndex2 < this.counter.length; ++byteIndex2) {
                int n = byteIndex2;
                this.s[n] = (byte)(this.s[n] + this.counter[byteIndex2]);
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            for (byteIndex2 = 0; byteIndex2 < this.macSize; ++byteIndex2) {
                out[outOff + byteIndex2] = (byte)(this.buffer[byteIndex2] ^ this.macBlock[byteIndex2]);
            }
            System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
            this.reset();
            return len + this.macSize;
        }
        if ((len - this.macSize) % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("partial blocks not supported");
        }
        this.engine.processBlock(this.nonce, 0, this.s, 0);
        int blocks = len / this.engine.getBlockSize();
        for (int blockNum = 0; blockNum < blocks; ++blockNum) {
            this.ProcessBlock(in, inOff, len, out, outOff);
            inOff += this.engine.getBlockSize();
            outOff += this.engine.getBlockSize();
        }
        if (len > inOff) {
            for (byteIndex = 0; byteIndex < this.counter.length; ++byteIndex) {
                int n = byteIndex;
                this.s[n] = (byte)(this.s[n] + this.counter[byteIndex]);
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            for (byteIndex = 0; byteIndex < this.macSize; ++byteIndex) {
                out[outOff + byteIndex] = (byte)(this.buffer[byteIndex] ^ in[inOff + byteIndex]);
            }
            outOff += this.macSize;
        }
        for (byteIndex = 0; byteIndex < this.counter.length; ++byteIndex) {
            int n = byteIndex;
            this.s[n] = (byte)(this.s[n] + this.counter[byteIndex]);
        }
        this.engine.processBlock(this.s, 0, this.buffer, 0);
        System.arraycopy(out, outOff - this.macSize, this.buffer, 0, this.macSize);
        this.CalculateMac(out, 0, outOff - this.macSize);
        System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
        byte[] calculatedMac = new byte[this.macSize];
        System.arraycopy(this.buffer, 0, calculatedMac, 0, this.macSize);
        if (!Arrays.constantTimeAreEqual(this.mac, calculatedMac)) {
            throw new InvalidCipherTextException("mac check failed");
        }
        this.reset();
        return len - this.macSize;
    }

    private void ProcessBlock(byte[] input, int inOff, int len, byte[] output, int outOff) {
        int byteIndex;
        for (byteIndex = 0; byteIndex < this.counter.length; ++byteIndex) {
            int n = byteIndex;
            this.s[n] = (byte)(this.s[n] + this.counter[byteIndex]);
        }
        this.engine.processBlock(this.s, 0, this.buffer, 0);
        for (byteIndex = 0; byteIndex < this.engine.getBlockSize(); ++byteIndex) {
            output[outOff + byteIndex] = (byte)(this.buffer[byteIndex] ^ input[inOff + byteIndex]);
        }
    }

    private void CalculateMac(byte[] authText, int authOff, int len) {
        int totalLen = len;
        while (totalLen > 0) {
            for (int byteIndex = 0; byteIndex < this.engine.getBlockSize(); ++byteIndex) {
                int n = byteIndex;
                this.macBlock[n] = (byte)(this.macBlock[n] ^ authText[authOff + byteIndex]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            totalLen -= this.engine.getBlockSize();
            authOff += this.engine.getBlockSize();
        }
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int len = this.processPacket(this.data.getBuffer(), 0, this.data.size(), out, outOff);
        this.reset();
        return len;
    }

    @Override
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return len;
    }

    @Override
    public int getOutputSize(int len) {
        return len + this.macSize;
    }

    @Override
    public void reset() {
        Arrays.fill(this.G1, (byte)0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.counter, (byte)0);
        Arrays.fill(this.macBlock, (byte)0);
        this.counter[0] = 1;
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void intToBytes(int num, byte[] outBytes, int outOff) {
        outBytes[outOff + 3] = (byte)(num >> 24);
        outBytes[outOff + 2] = (byte)(num >> 16);
        outBytes[outOff + 1] = (byte)(num >> 8);
        outBytes[outOff] = (byte)num;
    }

    private byte getFlag(boolean authTextPresents, int macSize) {
        StringBuffer flagByte = new StringBuffer();
        if (authTextPresents) {
            flagByte.append("1");
        } else {
            flagByte.append("0");
        }
        switch (macSize) {
            case 8: {
                flagByte.append("010");
                break;
            }
            case 16: {
                flagByte.append("011");
                break;
            }
            case 32: {
                flagByte.append("100");
                break;
            }
            case 48: {
                flagByte.append("101");
                break;
            }
            case 64: {
                flagByte.append("110");
            }
        }
        String binaryNb = Integer.toBinaryString(this.Nb_ - 1);
        while (binaryNb.length() < 4) {
            binaryNb = new StringBuffer(binaryNb).insert(0, "0").toString();
        }
        flagByte.append(binaryNb);
        return (byte)Integer.parseInt(flagByte.toString(), 2);
    }

    private static class ExposedByteArrayOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}

