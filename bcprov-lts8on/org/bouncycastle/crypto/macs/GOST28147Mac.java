/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Pack;

public class GOST28147Mac
implements Mac {
    private final CryptoServicePurpose purpose;
    private static final int BLOCK_SIZE = 8;
    private static final int MAC_SIZE = 4;
    private int bufOff;
    private byte[] buf;
    private byte[] mac;
    private boolean firstStep = true;
    private int[] workingKey = null;
    private byte[] macIV = null;
    private byte[] S = new byte[]{9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4};

    public GOST28147Mac() {
        this(CryptoServicePurpose.AUTHENTICATION);
    }

    public GOST28147Mac(CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.mac = new byte[8];
        this.buf = new byte[8];
        this.bufOff = 0;
    }

    private int[] generateWorkingKey(byte[] userKey) {
        if (userKey.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        int[] key = new int[8];
        for (int i = 0; i != 8; ++i) {
            key[i] = Pack.littleEndianToInt(userKey, i * 4);
        }
        return key;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        this.reset();
        this.buf = new byte[8];
        this.macIV = null;
        this.recursiveInit(params);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 178, params, this.purpose));
    }

    private void recursiveInit(CipherParameters params) throws IllegalArgumentException {
        if (params == null) {
            return;
        }
        CipherParameters child = null;
        if (params instanceof ParametersWithSBox) {
            ParametersWithSBox param = (ParametersWithSBox)params;
            System.arraycopy(param.getSBox(), 0, this.S, 0, param.getSBox().length);
            child = param.getParameters();
        } else if (params instanceof KeyParameter) {
            this.workingKey = this.generateWorkingKey(((KeyParameter)params).getKey());
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV p = (ParametersWithIV)params;
            System.arraycopy(p.getIV(), 0, this.mac, 0, this.mac.length);
            this.macIV = p.getIV();
            child = p.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + params.getClass().getName());
        }
        this.recursiveInit(child);
    }

    @Override
    public String getAlgorithmName() {
        return "GOST28147Mac";
    }

    @Override
    public int getMacSize() {
        return 4;
    }

    private int gost28147_mainStep(int n1, int key) {
        int cm = key + n1;
        int om = this.S[0 + (cm >> 0 & 0xF)] << 0;
        om += this.S[16 + (cm >> 4 & 0xF)] << 4;
        om += this.S[32 + (cm >> 8 & 0xF)] << 8;
        om += this.S[48 + (cm >> 12 & 0xF)] << 12;
        om += this.S[64 + (cm >> 16 & 0xF)] << 16;
        om += this.S[80 + (cm >> 20 & 0xF)] << 20;
        om += this.S[96 + (cm >> 24 & 0xF)] << 24;
        return (om += this.S[112 + (cm >> 28 & 0xF)] << 28) << 11 | om >>> 21;
    }

    private void gost28147MacFunc(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int N1 = Pack.littleEndianToInt(in, inOff);
        int N2 = Pack.littleEndianToInt(in, inOff + 4);
        for (int k = 0; k < 2; ++k) {
            for (int j = 0; j < 8; ++j) {
                int tmp = N1;
                N1 = N2 ^ this.gost28147_mainStep(N1, workingKey[j]);
                N2 = tmp;
            }
        }
        Pack.intToLittleEndian(N1, out, outOff);
        Pack.intToLittleEndian(N2, out, outOff + 4);
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        if (this.bufOff == this.buf.length) {
            byte[] sum = new byte[this.buf.length];
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    GOST28147Mac.CM5func(this.buf, 0, this.macIV, sum);
                } else {
                    System.arraycopy(this.buf, 0, sum, 0, this.mac.length);
                }
            } else {
                GOST28147Mac.CM5func(this.buf, 0, this.mac, sum);
            }
            this.gost28147MacFunc(this.workingKey, sum, 0, this.mac, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int gapLen = 8 - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            byte[] sum = new byte[this.buf.length];
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    GOST28147Mac.CM5func(this.buf, 0, this.macIV, sum);
                } else {
                    System.arraycopy(this.buf, 0, sum, 0, this.mac.length);
                }
            } else {
                GOST28147Mac.CM5func(this.buf, 0, this.mac, sum);
            }
            this.gost28147MacFunc(this.workingKey, sum, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > 8) {
                GOST28147Mac.CM5func(in, inOff, this.mac, sum);
                this.gost28147MacFunc(this.workingKey, sum, 0, this.mac, 0);
                len -= 8;
                inOff += 8;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        while (this.bufOff < 8) {
            this.buf[this.bufOff] = 0;
            ++this.bufOff;
        }
        byte[] sum = new byte[this.buf.length];
        if (this.firstStep) {
            this.firstStep = false;
            System.arraycopy(this.buf, 0, sum, 0, this.mac.length);
        } else {
            GOST28147Mac.CM5func(this.buf, 0, this.mac, sum);
        }
        this.gost28147MacFunc(this.workingKey, sum, 0, this.mac, 0);
        System.arraycopy(this.mac, this.mac.length / 2 - 4, out, outOff, 4);
        this.reset();
        return 4;
    }

    @Override
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.firstStep = true;
    }

    private static void CM5func(byte[] buf, int bufOff, byte[] mac, byte[] sum) {
        for (int i = 0; i < 8; ++i) {
            sum[i] = (byte)(buf[bufOff + i] ^ mac[i]);
        }
    }
}

