/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Grain128Engine
implements StreamCipher {
    private static final int STATE_SIZE = 4;
    private byte[] workingKey;
    private byte[] workingIV;
    private byte[] out;
    private int[] lfsr;
    private int[] nfsr;
    private int output;
    private int index = 4;
    private boolean initialised = false;

    @Override
    public String getAlgorithmName() {
        return "Grain-128";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Grain-128 Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV)params;
        byte[] iv = ivParams.getIV();
        if (iv == null || iv.length != 12) {
            throw new IllegalArgumentException("Grain-128 requires exactly 12 bytes of IV");
        }
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Grain-128 init parameters must include a key");
        }
        KeyParameter key = (KeyParameter)ivParams.getParameters();
        byte[] keyBytes = key.getKey();
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("Grain-128 key must be 128 bits long");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, params, Utils.getPurpose(forEncryption)));
        this.workingIV = new byte[keyBytes.length];
        this.workingKey = new byte[keyBytes.length];
        this.lfsr = new int[4];
        this.nfsr = new int[4];
        this.out = new byte[4];
        System.arraycopy(iv, 0, this.workingIV, 0, iv.length);
        System.arraycopy(keyBytes, 0, this.workingKey, 0, keyBytes.length);
        this.reset();
    }

    private void initGrain() {
        for (int i = 0; i < 8; ++i) {
            this.output = this.getOutput();
            this.nfsr = this.shift(this.nfsr, this.getOutputNFSR() ^ this.lfsr[0] ^ this.output);
            this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() ^ this.output);
        }
        this.initialised = true;
    }

    private int getOutputNFSR() {
        int b0 = this.nfsr[0];
        int b3 = this.nfsr[0] >>> 3 | this.nfsr[1] << 29;
        int b11 = this.nfsr[0] >>> 11 | this.nfsr[1] << 21;
        int b13 = this.nfsr[0] >>> 13 | this.nfsr[1] << 19;
        int b17 = this.nfsr[0] >>> 17 | this.nfsr[1] << 15;
        int b18 = this.nfsr[0] >>> 18 | this.nfsr[1] << 14;
        int b26 = this.nfsr[0] >>> 26 | this.nfsr[1] << 6;
        int b27 = this.nfsr[0] >>> 27 | this.nfsr[1] << 5;
        int b40 = this.nfsr[1] >>> 8 | this.nfsr[2] << 24;
        int b48 = this.nfsr[1] >>> 16 | this.nfsr[2] << 16;
        int b56 = this.nfsr[1] >>> 24 | this.nfsr[2] << 8;
        int b59 = this.nfsr[1] >>> 27 | this.nfsr[2] << 5;
        int b61 = this.nfsr[1] >>> 29 | this.nfsr[2] << 3;
        int b65 = this.nfsr[2] >>> 1 | this.nfsr[3] << 31;
        int b67 = this.nfsr[2] >>> 3 | this.nfsr[3] << 29;
        int b68 = this.nfsr[2] >>> 4 | this.nfsr[3] << 28;
        int b84 = this.nfsr[2] >>> 20 | this.nfsr[3] << 12;
        int b91 = this.nfsr[2] >>> 27 | this.nfsr[3] << 5;
        int b96 = this.nfsr[3];
        return b0 ^ b26 ^ b56 ^ b91 ^ b96 ^ b3 & b67 ^ b11 & b13 ^ b17 & b18 ^ b27 & b59 ^ b40 & b48 ^ b61 & b65 ^ b68 & b84;
    }

    private int getOutputLFSR() {
        int s0 = this.lfsr[0];
        int s7 = this.lfsr[0] >>> 7 | this.lfsr[1] << 25;
        int s38 = this.lfsr[1] >>> 6 | this.lfsr[2] << 26;
        int s70 = this.lfsr[2] >>> 6 | this.lfsr[3] << 26;
        int s81 = this.lfsr[2] >>> 17 | this.lfsr[3] << 15;
        int s96 = this.lfsr[3];
        return s0 ^ s7 ^ s38 ^ s70 ^ s81 ^ s96;
    }

    private int getOutput() {
        int b2 = this.nfsr[0] >>> 2 | this.nfsr[1] << 30;
        int b12 = this.nfsr[0] >>> 12 | this.nfsr[1] << 20;
        int b15 = this.nfsr[0] >>> 15 | this.nfsr[1] << 17;
        int b36 = this.nfsr[1] >>> 4 | this.nfsr[2] << 28;
        int b45 = this.nfsr[1] >>> 13 | this.nfsr[2] << 19;
        int b64 = this.nfsr[2];
        int b73 = this.nfsr[2] >>> 9 | this.nfsr[3] << 23;
        int b89 = this.nfsr[2] >>> 25 | this.nfsr[3] << 7;
        int b95 = this.nfsr[2] >>> 31 | this.nfsr[3] << 1;
        int s8 = this.lfsr[0] >>> 8 | this.lfsr[1] << 24;
        int s13 = this.lfsr[0] >>> 13 | this.lfsr[1] << 19;
        int s20 = this.lfsr[0] >>> 20 | this.lfsr[1] << 12;
        int s42 = this.lfsr[1] >>> 10 | this.lfsr[2] << 22;
        int s60 = this.lfsr[1] >>> 28 | this.lfsr[2] << 4;
        int s79 = this.lfsr[2] >>> 15 | this.lfsr[3] << 17;
        int s93 = this.lfsr[2] >>> 29 | this.lfsr[3] << 3;
        int s94 = this.lfsr[2] >>> 31 | this.lfsr[3] << 1;
        return b12 & s8 ^ s13 & s20 ^ b95 & s42 ^ s60 & s79 ^ b12 & b95 & s94 ^ s93 ^ b2 ^ b15 ^ b36 ^ b45 ^ b64 ^ b73 ^ b89;
    }

    private int[] shift(int[] array, int val) {
        array[0] = array[1];
        array[1] = array[2];
        array[2] = array[3];
        array[3] = val;
        return array;
    }

    private void setKey(byte[] keyBytes, byte[] ivBytes) {
        ivBytes[12] = -1;
        ivBytes[13] = -1;
        ivBytes[14] = -1;
        ivBytes[15] = -1;
        this.workingKey = keyBytes;
        this.workingIV = ivBytes;
        int j = 0;
        for (int i = 0; i < this.nfsr.length; ++i) {
            this.nfsr[i] = this.workingKey[j + 3] << 24 | this.workingKey[j + 2] << 16 & 0xFF0000 | this.workingKey[j + 1] << 8 & 0xFF00 | this.workingKey[j] & 0xFF;
            this.lfsr[i] = this.workingIV[j + 3] << 24 | this.workingIV[j + 2] << 16 & 0xFF0000 | this.workingIV[j + 1] << 8 & 0xFF00 | this.workingIV[j] & 0xFF;
            j += 4;
        }
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; ++i) {
            out[outOff + i] = (byte)(in[inOff + i] ^ this.getKeyStream());
        }
        return len;
    }

    @Override
    public void reset() {
        this.index = 4;
        this.setKey(this.workingKey, this.workingIV);
        this.initGrain();
    }

    private void oneRound() {
        this.output = this.getOutput();
        this.out[0] = (byte)this.output;
        this.out[1] = (byte)(this.output >> 8);
        this.out[2] = (byte)(this.output >> 16);
        this.out[3] = (byte)(this.output >> 24);
        this.nfsr = this.shift(this.nfsr, this.getOutputNFSR() ^ this.lfsr[0]);
        this.lfsr = this.shift(this.lfsr, this.getOutputLFSR());
    }

    @Override
    public byte returnByte(byte in) {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        return (byte)(in ^ this.getKeyStream());
    }

    private byte getKeyStream() {
        if (this.index > 3) {
            this.oneRound();
            this.index = 0;
        }
        return this.out[this.index++];
    }
}

