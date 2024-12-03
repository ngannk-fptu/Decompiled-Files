/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultMultiBlockCipher;
import org.bouncycastle.crypto.NativeBlockCipherProvider;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class CBCBlockCipher
extends DefaultMultiBlockCipher
implements CBCModeCipher {
    private byte[] IV;
    private byte[] cbcV;
    private byte[] cbcNextV;
    private int blockSize;
    private BlockCipher cipher = null;
    private boolean encrypting;

    public static CBCModeCipher newInstance(BlockCipher cipher) {
        if (cipher instanceof NativeBlockCipherProvider) {
            return ((NativeBlockCipherProvider)((Object)cipher)).createCBC();
        }
        return new CBCBlockCipher(cipher);
    }

    public CBCBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.cbcV = new byte[this.blockSize];
        this.cbcNextV = new byte[this.blockSize];
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        boolean oldEncrypting = this.encrypting;
        this.encrypting = encrypting;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            byte[] iv = ivParam.getIV();
            if (iv.length != this.blockSize) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(iv, 0, this.IV, 0, iv.length);
            this.reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(encrypting, ivParam.getParameters());
            } else if (oldEncrypting != encrypting) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        } else {
            this.reset();
            if (params != null) {
                this.cipher.init(encrypting, params);
            } else if (oldEncrypting != encrypting) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }

    @Override
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        return this.encrypting ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
        System.arraycopy(this.IV, 0, this.cbcV, 0, this.IV.length);
        Arrays.fill(this.cbcNextV, (byte)0);
        this.cipher.reset();
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < this.blockSize; ++i) {
            int n = i;
            this.cbcV[n] = (byte)(this.cbcV[n] ^ in[inOff + i]);
        }
        int length = this.cipher.processBlock(this.cbcV, 0, out, outOff);
        System.arraycopy(out, outOff, this.cbcV, 0, this.cbcV.length);
        return length;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        System.arraycopy(in, inOff, this.cbcNextV, 0, this.blockSize);
        int length = this.cipher.processBlock(in, inOff, out, outOff);
        for (int i = 0; i < this.blockSize; ++i) {
            int n = outOff + i;
            out[n] = (byte)(out[n] ^ this.cbcV[i]);
        }
        byte[] tmp = this.cbcV;
        this.cbcV = this.cbcNextV;
        this.cbcNextV = tmp;
        return length;
    }

    public String toString() {
        return "CBC[Java](" + this.cipher.toString() + ")";
    }
}

