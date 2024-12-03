/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;

public class IDEAEngine
implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey = null;
    private boolean forEncryption;
    private static final int MASK = 65535;
    private static final int BASE = 65537;

    public IDEAEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128));
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter)params).getKey();
            this.workingKey = this.generateWorkingKey(forEncryption, key);
            this.forEncryption = forEncryption;
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length * 8, params, Utils.getPurpose(forEncryption)));
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "IDEA";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("IDEA engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.ideaFunc(this.workingKey, in, inOff, out, outOff);
        return 8;
    }

    @Override
    public void reset() {
    }

    private int bytesToWord(byte[] in, int inOff) {
        return (in[inOff] << 8 & 0xFF00) + (in[inOff + 1] & 0xFF);
    }

    private void wordToBytes(int word, byte[] out, int outOff) {
        out[outOff] = (byte)(word >>> 8);
        out[outOff + 1] = (byte)word;
    }

    private int mul(int x, int y) {
        int p;
        x = x == 0 ? 65537 - y : (y == 0 ? 65537 - x : y - x + ((y = (p = x * y) & 0xFFFF) < (x = p >>> 16) ? 1 : 0));
        return x & 0xFFFF;
    }

    private void ideaFunc(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int keyOff = 0;
        int x0 = this.bytesToWord(in, inOff);
        int x1 = this.bytesToWord(in, inOff + 2);
        int x2 = this.bytesToWord(in, inOff + 4);
        int x3 = this.bytesToWord(in, inOff + 6);
        for (int round = 0; round < 8; ++round) {
            x0 = this.mul(x0, workingKey[keyOff++]);
            x1 += workingKey[keyOff++];
            x2 += workingKey[keyOff++];
            x3 = this.mul(x3, workingKey[keyOff++]);
            int t0 = x1 &= 0xFFFF;
            int t1 = x2 &= 0xFFFF;
            x2 ^= x0;
            x1 ^= x3;
            x2 = this.mul(x2, workingKey[keyOff++]);
            x1 += x2;
            x1 &= 0xFFFF;
            x1 = this.mul(x1, workingKey[keyOff++]);
            x2 += x1;
            x0 ^= x1;
            x3 ^= (x2 &= 0xFFFF);
            x1 ^= t1;
            x2 ^= t0;
        }
        this.wordToBytes(this.mul(x0, workingKey[keyOff++]), out, outOff);
        this.wordToBytes(x2 + workingKey[keyOff++], out, outOff + 2);
        this.wordToBytes(x1 + workingKey[keyOff++], out, outOff + 4);
        this.wordToBytes(this.mul(x3, workingKey[keyOff]), out, outOff + 6);
    }

    private int[] expandKey(byte[] uKey) {
        int i;
        int[] key = new int[52];
        if (uKey.length < 16) {
            byte[] tmp = new byte[16];
            System.arraycopy(uKey, 0, tmp, tmp.length - uKey.length, uKey.length);
            uKey = tmp;
        }
        for (i = 0; i < 8; ++i) {
            key[i] = this.bytesToWord(uKey, i * 2);
        }
        for (i = 8; i < 52; ++i) {
            key[i] = (i & 7) < 6 ? ((key[i - 7] & 0x7F) << 9 | key[i - 6] >> 7) & 0xFFFF : ((i & 7) == 6 ? ((key[i - 7] & 0x7F) << 9 | key[i - 14] >> 7) & 0xFFFF : ((key[i - 15] & 0x7F) << 9 | key[i - 14] >> 7) & 0xFFFF);
        }
        return key;
    }

    private int mulInv(int x) {
        if (x < 2) {
            return x;
        }
        int t0 = 1;
        int t1 = 65537 / x;
        for (int y = 65537 % x; y != 1; y %= x) {
            int q = x / y;
            t0 = t0 + t1 * q & 0xFFFF;
            if ((x %= y) == 1) {
                return t0;
            }
            q = y / x;
            t1 = t1 + t0 * q & 0xFFFF;
        }
        return 1 - t1 & 0xFFFF;
    }

    int addInv(int x) {
        return 0 - x & 0xFFFF;
    }

    private int[] invertKey(int[] inKey) {
        int p = 52;
        int[] key = new int[52];
        int inOff = 0;
        int t1 = this.mulInv(inKey[inOff++]);
        int t2 = this.addInv(inKey[inOff++]);
        int t3 = this.addInv(inKey[inOff++]);
        int t4 = this.mulInv(inKey[inOff++]);
        key[--p] = t4;
        key[--p] = t3;
        key[--p] = t2;
        key[--p] = t1;
        for (int round = 1; round < 8; ++round) {
            t1 = inKey[inOff++];
            t2 = inKey[inOff++];
            key[--p] = t2;
            key[--p] = t1;
            t1 = this.mulInv(inKey[inOff++]);
            t2 = this.addInv(inKey[inOff++]);
            t3 = this.addInv(inKey[inOff++]);
            t4 = this.mulInv(inKey[inOff++]);
            key[--p] = t4;
            key[--p] = t2;
            key[--p] = t3;
            key[--p] = t1;
        }
        t1 = inKey[inOff++];
        t2 = inKey[inOff++];
        key[--p] = t2;
        key[--p] = t1;
        t1 = this.mulInv(inKey[inOff++]);
        t2 = this.addInv(inKey[inOff++]);
        t3 = this.addInv(inKey[inOff++]);
        t4 = this.mulInv(inKey[inOff]);
        key[--p] = t4;
        key[--p] = t3;
        key[--p] = t2;
        key[--p] = t1;
        return key;
    }

    private int[] generateWorkingKey(boolean forEncryption, byte[] userKey) {
        if (forEncryption) {
            return this.expandKey(userKey);
        }
        return this.invertKey(this.expandKey(userKey));
    }
}

