/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class Grain128AEADEngine
implements AEADCipher {
    private static final int STATE_SIZE = 4;
    private byte[] workingKey;
    private byte[] workingIV;
    private int[] lfsr;
    private int[] nfsr;
    private int[] authAcc;
    private int[] authSr;
    private boolean initialised = false;
    private boolean aadFinished = false;
    private ErasableOutputStream aadData = new ErasableOutputStream();
    private byte[] mac;

    @Override
    public String getAlgorithmName() {
        return "Grain-128AEAD";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Grain-128AEAD init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV)params;
        byte[] iv = ivParams.getIV();
        if (iv == null || iv.length != 12) {
            throw new IllegalArgumentException("Grain-128AEAD requires exactly 12 bytes of IV");
        }
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Grain-128AEAD init parameters must include a key");
        }
        KeyParameter key = (KeyParameter)ivParams.getParameters();
        byte[] keyBytes = key.getKey();
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("Grain-128AEAD key must be 128 bits long");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, params, Utils.getPurpose(forEncryption)));
        this.workingIV = new byte[16];
        this.workingKey = new byte[16];
        this.lfsr = new int[4];
        this.nfsr = new int[4];
        this.authAcc = new int[2];
        this.authSr = new int[2];
        System.arraycopy(iv, 0, this.workingIV, 0, iv.length);
        System.arraycopy(keyBytes, 0, this.workingKey, 0, keyBytes.length);
        this.reset();
    }

    private void initGrain() {
        int output;
        int remainder;
        int quotient;
        for (int i = 0; i < 320; ++i) {
            int output2 = this.getOutput();
            this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ output2) & 1);
            this.lfsr = this.shift(this.lfsr, (this.getOutputLFSR() ^ output2) & 1);
        }
        for (quotient = 0; quotient < 8; ++quotient) {
            for (remainder = 0; remainder < 8; ++remainder) {
                output = this.getOutput();
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ output ^ this.workingKey[quotient] >> remainder) & 1);
                this.lfsr = this.shift(this.lfsr, (this.getOutputLFSR() ^ output ^ this.workingKey[quotient + 8] >> remainder) & 1);
            }
        }
        for (quotient = 0; quotient < 2; ++quotient) {
            for (remainder = 0; remainder < 32; ++remainder) {
                output = this.getOutput();
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
                int n = quotient;
                this.authAcc[n] = this.authAcc[n] | output << remainder;
            }
        }
        for (quotient = 0; quotient < 2; ++quotient) {
            for (remainder = 0; remainder < 32; ++remainder) {
                output = this.getOutput();
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
                int n = quotient;
                this.authSr[n] = this.authSr[n] | output << remainder;
            }
        }
        this.initialised = true;
    }

    private int getOutputNFSR() {
        int b0 = this.nfsr[0];
        int b3 = this.nfsr[0] >>> 3;
        int b11 = this.nfsr[0] >>> 11;
        int b13 = this.nfsr[0] >>> 13;
        int b17 = this.nfsr[0] >>> 17;
        int b18 = this.nfsr[0] >>> 18;
        int b22 = this.nfsr[0] >>> 22;
        int b24 = this.nfsr[0] >>> 24;
        int b25 = this.nfsr[0] >>> 25;
        int b26 = this.nfsr[0] >>> 26;
        int b27 = this.nfsr[0] >>> 27;
        int b40 = this.nfsr[1] >>> 8;
        int b48 = this.nfsr[1] >>> 16;
        int b56 = this.nfsr[1] >>> 24;
        int b59 = this.nfsr[1] >>> 27;
        int b61 = this.nfsr[1] >>> 29;
        int b65 = this.nfsr[2] >>> 1;
        int b67 = this.nfsr[2] >>> 3;
        int b68 = this.nfsr[2] >>> 4;
        int b70 = this.nfsr[2] >>> 6;
        int b78 = this.nfsr[2] >>> 14;
        int b82 = this.nfsr[2] >>> 18;
        int b84 = this.nfsr[2] >>> 20;
        int b88 = this.nfsr[2] >>> 24;
        int b91 = this.nfsr[2] >>> 27;
        int b92 = this.nfsr[2] >>> 28;
        int b93 = this.nfsr[2] >>> 29;
        int b95 = this.nfsr[2] >>> 31;
        int b96 = this.nfsr[3];
        return (b0 ^ b26 ^ b56 ^ b91 ^ b96 ^ b3 & b67 ^ b11 & b13 ^ b17 & b18 ^ b27 & b59 ^ b40 & b48 ^ b61 & b65 ^ b68 & b84 ^ b22 & b24 & b25 ^ b70 & b78 & b82 ^ b88 & b92 & b93 & b95) & 1;
    }

    private int getOutputLFSR() {
        int s0 = this.lfsr[0];
        int s7 = this.lfsr[0] >>> 7;
        int s38 = this.lfsr[1] >>> 6;
        int s70 = this.lfsr[2] >>> 6;
        int s81 = this.lfsr[2] >>> 17;
        int s96 = this.lfsr[3];
        return (s0 ^ s7 ^ s38 ^ s70 ^ s81 ^ s96) & 1;
    }

    private int getOutput() {
        int b2 = this.nfsr[0] >>> 2;
        int b12 = this.nfsr[0] >>> 12;
        int b15 = this.nfsr[0] >>> 15;
        int b36 = this.nfsr[1] >>> 4;
        int b45 = this.nfsr[1] >>> 13;
        int b64 = this.nfsr[2];
        int b73 = this.nfsr[2] >>> 9;
        int b89 = this.nfsr[2] >>> 25;
        int b95 = this.nfsr[2] >>> 31;
        int s8 = this.lfsr[0] >>> 8;
        int s13 = this.lfsr[0] >>> 13;
        int s20 = this.lfsr[0] >>> 20;
        int s42 = this.lfsr[1] >>> 10;
        int s60 = this.lfsr[1] >>> 28;
        int s79 = this.lfsr[2] >>> 15;
        int s93 = this.lfsr[2] >>> 29;
        int s94 = this.lfsr[2] >>> 30;
        return (b12 & s8 ^ s13 & s20 ^ b95 & s42 ^ s60 & s79 ^ b12 & b95 & s94 ^ s93 ^ b2 ^ b15 ^ b36 ^ b45 ^ b64 ^ b73 ^ b89) & 1;
    }

    private int[] shift(int[] array, int val) {
        array[0] = array[0] >>> 1 | array[1] << 31;
        array[1] = array[1] >>> 1 | array[2] << 31;
        array[2] = array[2] >>> 1 | array[3] << 31;
        array[3] = array[3] >>> 1 | val << 31;
        return array;
    }

    private void setKey(byte[] keyBytes, byte[] ivBytes) {
        ivBytes[12] = -1;
        ivBytes[13] = -1;
        ivBytes[14] = -1;
        ivBytes[15] = 127;
        this.workingKey = keyBytes;
        this.workingIV = ivBytes;
        Pack.littleEndianToInt(this.workingKey, 0, this.nfsr);
        Pack.littleEndianToInt(this.workingIV, 0, this.lfsr);
    }

    @Override
    public int processBytes(byte[] input, int inOff, int len, byte[] output, int outOff) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (!this.aadFinished) {
            this.doProcessAADBytes(this.aadData.getBuf(), 0, this.aadData.size());
            this.aadFinished = true;
        }
        if (inOff + len > input.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > output.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.getKeyStream(input, inOff, len, output, outOff);
        return len;
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    private void reset(boolean clearMac) {
        if (clearMac) {
            this.mac = null;
        }
        this.aadData.reset();
        this.aadFinished = false;
        this.setKey(this.workingKey, this.workingIV);
        this.initGrain();
    }

    private byte[] getKeyStream(byte[] input, int inOff, int len, byte[] ciphertext, int outOff) {
        for (int i = 0; i < len; ++i) {
            byte cc = 0;
            byte input_i = input[inOff + i];
            for (int j = 0; j < 8; ++j) {
                int output = this.getOutput();
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
                int input_i_j = input_i >> j & 1;
                cc = (byte)(cc | (input_i_j ^ output) << j);
                int mask = -input_i_j;
                this.authAcc[0] = this.authAcc[0] ^ this.authSr[0] & mask;
                this.authAcc[1] = this.authAcc[1] ^ this.authSr[1] & mask;
                this.authShift(this.getOutput());
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
            }
            ciphertext[outOff + i] = cc;
        }
        return ciphertext;
    }

    @Override
    public void processAADByte(byte in) {
        if (this.aadFinished) {
            throw new IllegalStateException("associated data must be added before plaintext/ciphertext");
        }
        this.aadData.write(in);
    }

    @Override
    public void processAADBytes(byte[] input, int inOff, int len) {
        if (this.aadFinished) {
            throw new IllegalStateException("associated data must be added before plaintext/ciphertext");
        }
        this.aadData.write(input, inOff, len);
    }

    private void doProcessAADBytes(byte[] input, int inOff, int len) {
        int i;
        int aderlen;
        byte[] ader;
        if (len < 128) {
            ader = new byte[1 + len];
            ader[0] = (byte)len;
            aderlen = 0;
        } else {
            aderlen = Grain128AEADEngine.len_length(len);
            ader = new byte[1 + aderlen + len];
            ader[0] = (byte)(0x80 | aderlen);
            int tmp = len;
            for (int i2 = 0; i2 < aderlen; ++i2) {
                ader[1 + i2] = (byte)tmp;
                tmp >>>= 8;
            }
        }
        for (i = 0; i < len; ++i) {
            ader[1 + aderlen + i] = input[inOff + i];
        }
        for (i = 0; i < ader.length; ++i) {
            byte ader_i = ader[i];
            for (int j = 0; j < 8; ++j) {
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
                int ader_i_j = ader_i >> j & 1;
                int mask = -ader_i_j;
                this.authAcc[0] = this.authAcc[0] ^ this.authSr[0] & mask;
                this.authAcc[1] = this.authAcc[1] ^ this.authSr[1] & mask;
                this.authShift(this.getOutput());
                this.nfsr = this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() & 1);
            }
        }
    }

    private void accumulate() {
        this.authAcc[0] = this.authAcc[0] ^ this.authSr[0];
        this.authAcc[1] = this.authAcc[1] ^ this.authSr[1];
    }

    private void authShift(int val) {
        this.authSr[0] = this.authSr[0] >>> 1 | this.authSr[1] << 31;
        this.authSr[1] = this.authSr[1] >>> 1 | val << 31;
    }

    @Override
    public int processByte(byte input, byte[] output, int outOff) throws DataLengthException {
        return this.processBytes(new byte[]{input}, 0, 1, output, outOff);
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        if (!this.aadFinished) {
            this.doProcessAADBytes(this.aadData.getBuf(), 0, this.aadData.size());
            this.aadFinished = true;
        }
        this.accumulate();
        this.mac = Pack.intToLittleEndian(this.authAcc);
        System.arraycopy(this.mac, 0, out, outOff, this.mac.length);
        this.reset(false);
        return this.mac.length;
    }

    @Override
    public byte[] getMac() {
        return this.mac;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return len;
    }

    @Override
    public int getOutputSize(int len) {
        return len + 8;
    }

    private static int len_length(int v) {
        if ((v & 0xFF) == v) {
            return 1;
        }
        if ((v & 0xFFFF) == v) {
            return 2;
        }
        if ((v & 0xFFFFFF) == v) {
            return 3;
        }
        return 4;
    }

    private static final class ErasableOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }
    }
}

