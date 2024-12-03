/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.NativeBlockCipherProvider;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class SICBlockCipher
extends StreamBlockCipher
implements CTRModeCipher {
    private final BlockCipher cipher;
    private final int blockSize;
    private byte[] IV;
    private byte[] counter;
    private byte[] counterOut;
    private int byteCount;

    public static CTRModeCipher newInstance(BlockCipher cipher) {
        if (cipher instanceof NativeBlockCipherProvider) {
            return ((NativeBlockCipherProvider)((Object)cipher)).createCTR();
        }
        return new SICBlockCipher(cipher);
    }

    public SICBlockCipher(BlockCipher c) {
        super(c);
        this.cipher = c;
        this.blockSize = this.cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.counter = new byte[this.blockSize];
        this.counterOut = new byte[this.blockSize];
        this.byteCount = 0;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            int maxCounterSize;
            ParametersWithIV ivParam = (ParametersWithIV)params;
            this.IV = Arrays.clone(ivParam.getIV());
            if (this.blockSize < this.IV.length) {
                throw new IllegalArgumentException("CTR/SIC mode requires IV no greater than: " + this.blockSize + " bytes.");
            }
            int n = maxCounterSize = 8 > this.blockSize / 2 ? this.blockSize / 2 : 8;
            if (this.blockSize - this.IV.length > maxCounterSize) {
                throw new IllegalArgumentException("CTR/SIC mode requires IV of at least: " + (this.blockSize - maxCounterSize) + " bytes.");
            }
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
            }
        } else {
            throw new IllegalArgumentException("CTR/SIC mode requires ParametersWithIV");
        }
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/SIC";
    }

    @Override
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.byteCount != 0) {
            this.processBytes(in, inOff, this.blockSize, out, outOff);
            return this.blockSize;
        }
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
        for (int i = 0; i < this.blockSize; ++i) {
            out[outOff + i] = (byte)(in[inOff + i] ^ this.counterOut[i]);
        }
        this.incrementCounter();
        return this.blockSize;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; ++i) {
            byte next;
            if (this.byteCount == 0) {
                this.checkLastIncrement();
                this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
                next = (byte)(in[inOff + i] ^ this.counterOut[this.byteCount++]);
            } else {
                next = (byte)(in[inOff + i] ^ this.counterOut[this.byteCount++]);
                if (this.byteCount == this.counter.length) {
                    this.byteCount = 0;
                    this.incrementCounter();
                }
            }
            out[outOff + i] = next;
        }
        return len;
    }

    @Override
    protected byte calculateByte(byte in) throws DataLengthException, IllegalStateException {
        if (this.byteCount == 0) {
            this.checkLastIncrement();
            this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
            return (byte)(this.counterOut[this.byteCount++] ^ in);
        }
        byte rv = (byte)(this.counterOut[this.byteCount++] ^ in);
        if (this.byteCount == this.counter.length) {
            this.byteCount = 0;
            this.incrementCounter();
        }
        return rv;
    }

    private void checkCounter() {
        if (this.IV.length < this.blockSize) {
            for (int i = this.IV.length - 1; i >= 0; --i) {
                if (this.counter[i] == this.IV[i]) continue;
                throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
            }
        }
    }

    private void checkLastIncrement() {
        if (this.IV.length < this.blockSize && this.counter[this.IV.length - 1] != this.IV[this.IV.length - 1]) {
            throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
        }
    }

    private void incrementCounter() {
        int i = this.counter.length;
        while (--i >= 0) {
            int n = i;
            this.counter[n] = (byte)(this.counter[n] + 1);
            if (this.counter[n] == 0) continue;
            break;
        }
    }

    private void incrementCounterAt(int pos) {
        int i = this.counter.length - pos;
        while (--i >= 0) {
            int n = i;
            this.counter[n] = (byte)(this.counter[n] + 1);
            if (this.counter[n] == 0) continue;
            break;
        }
    }

    private void incrementCounter(int offSet) {
        byte old = this.counter[this.counter.length - 1];
        int n = this.counter.length - 1;
        this.counter[n] = (byte)(this.counter[n] + offSet);
        if (old != 0 && this.counter[this.counter.length - 1] < old) {
            this.incrementCounterAt(1);
        }
    }

    private void decrementCounterAt(int pos) {
        int i = this.counter.length - pos;
        while (--i >= 0) {
            int n = i;
            this.counter[n] = (byte)(this.counter[n] - 1);
            if (this.counter[n] == -1) continue;
            return;
        }
    }

    private void adjustCounter(long n) {
        if (n >= 0L) {
            long numBlocks = (n + (long)this.byteCount) / (long)this.blockSize;
            long rem = numBlocks;
            if (rem > 255L) {
                for (int i = 5; i >= 1; --i) {
                    long diff = 1L << 8 * i;
                    while (rem >= diff) {
                        this.incrementCounterAt(i);
                        rem -= diff;
                    }
                }
            }
            this.incrementCounter((int)rem);
            this.byteCount = (int)(n + (long)this.byteCount - (long)this.blockSize * numBlocks);
        } else {
            long numBlocks = (-n - (long)this.byteCount) / (long)this.blockSize;
            long rem = numBlocks;
            if (rem > 255L) {
                for (int i = 5; i >= 1; --i) {
                    long diff = 1L << 8 * i;
                    while (rem > diff) {
                        this.decrementCounterAt(i);
                        rem -= diff;
                    }
                }
            }
            for (long i = 0L; i != rem; ++i) {
                this.decrementCounterAt(0);
            }
            int gap = (int)((long)this.byteCount + n + (long)this.blockSize * numBlocks);
            if (gap >= 0) {
                this.byteCount = 0;
            } else {
                this.decrementCounterAt(0);
                this.byteCount = this.blockSize + gap;
            }
        }
    }

    @Override
    public void reset() {
        Arrays.fill(this.counter, (byte)0);
        System.arraycopy(this.IV, 0, this.counter, 0, this.IV.length);
        this.cipher.reset();
        this.byteCount = 0;
    }

    @Override
    public long skip(long numberOfBytes) {
        this.adjustCounter(numberOfBytes);
        this.checkCounter();
        this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
        return numberOfBytes;
    }

    @Override
    public long seekTo(long position) {
        this.reset();
        return this.skip(position);
    }

    @Override
    public long getPosition() {
        byte[] res = new byte[this.counter.length];
        System.arraycopy(this.counter, 0, res, 0, res.length);
        for (int i = res.length - 1; i >= 1; --i) {
            int v = i < this.IV.length ? (res[i] & 0xFF) - (this.IV[i] & 0xFF) : res[i] & 0xFF;
            if (v < 0) {
                int n = i - 1;
                res[n] = (byte)(res[n] - 1);
                v += 256;
            }
            res[i] = (byte)v;
        }
        return Pack.bigEndianToLong(res, res.length - 8) * (long)this.blockSize + (long)this.byteCount;
    }
}

