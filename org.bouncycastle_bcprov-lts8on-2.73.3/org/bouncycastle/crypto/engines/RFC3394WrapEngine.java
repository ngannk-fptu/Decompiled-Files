/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RFC3394WrapEngine
implements Wrapper {
    private static final byte[] DEFAULT_IV = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};
    private final BlockCipher engine;
    private final boolean wrapCipherMode;
    private final byte[] iv = new byte[8];
    private KeyParameter param = null;
    private boolean forWrapping = true;

    public RFC3394WrapEngine(BlockCipher engine) {
        this(engine, false);
    }

    public RFC3394WrapEngine(BlockCipher engine, boolean useReverseDirection) {
        this.engine = engine;
        this.wrapCipherMode = !useReverseDirection;
    }

    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            param = ((ParametersWithRandom)param).getParameters();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter)param;
            System.arraycopy(DEFAULT_IV, 0, this.iv, 0, 8);
        } else if (param instanceof ParametersWithIV) {
            ParametersWithIV withIV = (ParametersWithIV)param;
            byte[] iv = withIV.getIV();
            if (iv.length != 8) {
                throw new IllegalArgumentException("IV not equal to 8");
            }
            this.param = (KeyParameter)withIV.getParameters();
            System.arraycopy(iv, 0, this.iv, 0, 8);
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (inLen < 8) {
            throw new DataLengthException("wrap data must be at least 8 bytes");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
        }
        this.engine.init(this.wrapCipherMode, this.param);
        byte[] block = new byte[inLen + this.iv.length];
        System.arraycopy(this.iv, 0, block, 0, this.iv.length);
        System.arraycopy(in, inOff, block, this.iv.length, inLen);
        if (n == 1) {
            this.engine.processBlock(block, 0, block, 0);
        } else {
            byte[] buf = new byte[8 + this.iv.length];
            for (int j = 0; j != 6; ++j) {
                for (int i = 1; i <= n; ++i) {
                    System.arraycopy(block, 0, buf, 0, this.iv.length);
                    System.arraycopy(block, 8 * i, buf, this.iv.length, 8);
                    this.engine.processBlock(buf, 0, buf, 0);
                    int t = n * j + i;
                    int k = 1;
                    while (t != 0) {
                        byte v = (byte)t;
                        int n2 = this.iv.length - k;
                        buf[n2] = (byte)(buf[n2] ^ v);
                        t >>>= 8;
                        ++k;
                    }
                    System.arraycopy(buf, 0, block, 0, 8);
                    System.arraycopy(buf, 8, block, 8 * i, 8);
                }
            }
        }
        return block;
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int j;
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        if (inLen < 16) {
            throw new InvalidCipherTextException("unwrap data too short");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        this.engine.init(!this.wrapCipherMode, this.param);
        byte[] block = new byte[inLen - this.iv.length];
        byte[] a = new byte[this.iv.length];
        byte[] buf = new byte[8 + this.iv.length];
        if (--n == 1) {
            this.engine.processBlock(in, inOff, buf, 0);
            System.arraycopy(buf, 0, a, 0, this.iv.length);
            System.arraycopy(buf, this.iv.length, block, 0, 8);
        } else {
            System.arraycopy(in, inOff, a, 0, this.iv.length);
            System.arraycopy(in, inOff + this.iv.length, block, 0, inLen - this.iv.length);
            for (j = 5; j >= 0; --j) {
                for (int i = n; i >= 1; --i) {
                    System.arraycopy(a, 0, buf, 0, this.iv.length);
                    System.arraycopy(block, 8 * (i - 1), buf, this.iv.length, 8);
                    int t = n * j + i;
                    int k = 1;
                    while (t != 0) {
                        byte v = (byte)t;
                        int n2 = this.iv.length - k;
                        buf[n2] = (byte)(buf[n2] ^ v);
                        t >>>= 8;
                        ++k;
                    }
                    this.engine.processBlock(buf, 0, buf, 0);
                    System.arraycopy(buf, 0, a, 0, 8);
                    System.arraycopy(buf, 8, block, 8 * (i - 1), 8);
                }
            }
        }
        if (n != 1) {
            if (!Arrays.constantTimeAreEqual(a, this.iv)) {
                throw new InvalidCipherTextException("checksum failed");
            }
        } else if (!Arrays.constantTimeAreEqual(a, this.iv)) {
            System.arraycopy(in, inOff, a, 0, this.iv.length);
            System.arraycopy(in, inOff + this.iv.length, block, 0, inLen - this.iv.length);
            for (j = 5; j >= 0; --j) {
                System.arraycopy(a, 0, buf, 0, this.iv.length);
                System.arraycopy(block, 0, buf, this.iv.length, 8);
                int t = n * j + 1;
                int k = 1;
                while (t != 0) {
                    byte v = (byte)t;
                    int n3 = this.iv.length - k;
                    buf[n3] = (byte)(buf[n3] ^ v);
                    t >>>= 8;
                    ++k;
                }
                this.engine.processBlock(buf, 0, buf, 0);
                System.arraycopy(buf, 0, a, 0, 8);
                System.arraycopy(buf, 8, block, 0, 8);
            }
            if (!Arrays.constantTimeAreEqual(a, this.iv)) {
                throw new InvalidCipherTextException("checksum failed");
            }
        }
        return block;
    }
}

