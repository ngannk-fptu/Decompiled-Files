/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class ISO9797Alg3Mac
implements Mac {
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private BlockCipherPadding padding;
    private int macSize;
    private KeyParameter lastKey2;
    private KeyParameter lastKey3;

    public ISO9797Alg3Mac(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8, null);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, BlockCipherPadding padding) {
        this(cipher, cipher.getBlockSize() * 8, padding);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, int macSizeInBits) {
        this(cipher, macSizeInBits, null);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, int macSizeInBits, BlockCipherPadding padding) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (!(cipher instanceof DESEngine)) {
            throw new IllegalArgumentException("cipher must be instance of DESEngine");
        }
        this.cipher = CBCBlockCipher.newInstance(cipher);
        this.padding = padding;
        this.macSize = macSizeInBits / 8;
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "ISO9797Alg3";
    }

    @Override
    public void init(CipherParameters params) {
        KeyParameter key1;
        this.reset();
        if (!(params instanceof KeyParameter) && !(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("params must be an instance of KeyParameter or ParametersWithIV");
        }
        KeyParameter kp = params instanceof KeyParameter ? (KeyParameter)params : (KeyParameter)((ParametersWithIV)params).getParameters();
        byte[] keyvalue = kp.getKey();
        if (keyvalue.length == 16) {
            key1 = new KeyParameter(keyvalue, 0, 8);
            this.lastKey2 = new KeyParameter(keyvalue, 8, 8);
            this.lastKey3 = key1;
        } else if (keyvalue.length == 24) {
            key1 = new KeyParameter(keyvalue, 0, 8);
            this.lastKey2 = new KeyParameter(keyvalue, 8, 8);
            this.lastKey3 = new KeyParameter(keyvalue, 16, 8);
        } else {
            throw new IllegalArgumentException("Key must be either 112 or 168 bit long");
        }
        if (params instanceof ParametersWithIV) {
            this.cipher.init(true, new ParametersWithIV(key1, ((ParametersWithIV)params).getIV()));
        } else {
            this.cipher.init(true, key1);
        }
    }

    @Override
    public int getMacSize() {
        return this.macSize;
    }

    @Override
    public void update(byte in) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int resultLen = 0;
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen += this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                resultLen += this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        int blockSize = this.cipher.getBlockSize();
        if (this.padding == null) {
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = 0;
                ++this.bufOff;
            }
        } else {
            if (this.bufOff == blockSize) {
                this.cipher.processBlock(this.buf, 0, this.mac, 0);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        DESEngine deseng = new DESEngine();
        deseng.init(false, this.lastKey2);
        deseng.processBlock(this.mac, 0, this.mac, 0);
        deseng.init(true, this.lastKey3);
        deseng.processBlock(this.mac, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, out, outOff, this.macSize);
        this.reset();
        return this.macSize;
    }

    @Override
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}

