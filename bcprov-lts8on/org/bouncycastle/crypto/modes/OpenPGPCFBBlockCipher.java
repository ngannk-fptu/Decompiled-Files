/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

public class OpenPGPCFBBlockCipher
implements BlockCipher {
    private byte[] IV;
    private byte[] FR;
    private byte[] FRE;
    private BlockCipher cipher;
    private int count;
    private int blockSize;
    private boolean forEncryption;

    public OpenPGPCFBBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
    }

    @Override
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        return this.forEncryption ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
        this.count = 0;
        System.arraycopy(this.IV, 0, this.FR, 0, this.FR.length);
        this.cipher.reset();
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        this.cipher.init(true, params);
    }

    private byte encryptByte(byte data, int blockOff) {
        return (byte)(this.FRE[blockOff] ^ data);
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            this.FR[this.blockSize - 2] = out[outOff] = this.encryptByte(in[inOff], this.blockSize - 2);
            byte by = this.encryptByte(in[inOff + 1], this.blockSize - 1);
            out[outOff + 1] = by;
            this.FR[this.blockSize - 1] = by;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; ++n) {
                byte by2 = this.encryptByte(in[inOff + n], n - 2);
                out[outOff + n] = by2;
                this.FR[n - 2] = by2;
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 0; n < this.blockSize; ++n) {
                byte by = this.encryptByte(in[inOff + n], n);
                out[outOff + n] = by;
                this.FR[n] = by;
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            out[outOff] = this.encryptByte(in[inOff], 0);
            out[outOff + 1] = this.encryptByte(in[inOff + 1], 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            System.arraycopy(out, outOff, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; ++n) {
                byte by = this.encryptByte(in[inOff + n], n - 2);
                out[outOff + n] = by;
                this.FR[n - 2] = by;
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            byte inVal;
            this.FR[this.blockSize - 2] = inVal = in[inOff];
            out[outOff] = this.encryptByte(inVal, this.blockSize - 2);
            this.FR[this.blockSize - 1] = inVal = in[inOff + 1];
            out[outOff + 1] = this.encryptByte(inVal, this.blockSize - 1);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; ++n) {
                this.FR[n - 2] = inVal = in[inOff + n];
                out[outOff + n] = this.encryptByte(inVal, n - 2);
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 0; n < this.blockSize; ++n) {
                this.FR[n] = in[inOff + n];
                out[outOff + n] = this.encryptByte(in[inOff + n], n);
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            byte inVal1 = in[inOff];
            byte inVal2 = in[inOff + 1];
            out[outOff] = this.encryptByte(inVal1, 0);
            out[outOff + 1] = this.encryptByte(inVal2, 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = inVal1;
            this.FR[this.blockSize - 1] = inVal2;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; ++n) {
                byte inVal;
                this.FR[n - 2] = inVal = in[inOff + n];
                out[outOff + n] = this.encryptByte(inVal, n - 2);
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }
}

