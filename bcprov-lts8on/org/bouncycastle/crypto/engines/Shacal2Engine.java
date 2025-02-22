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

public class Shacal2Engine
implements BlockCipher {
    private static final int[] K = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
    private static final int BLOCK_SIZE = 32;
    private boolean forEncryption = false;
    private static final int ROUNDS = 64;
    private int[] workingKey = null;

    @Override
    public void reset() {
    }

    @Override
    public String getAlgorithmName() {
        return "Shacal2";
    }

    @Override
    public int getBlockSize() {
        return 32;
    }

    @Override
    public void init(boolean _forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("only simple KeyParameter expected.");
        }
        this.forEncryption = _forEncryption;
        this.workingKey = new int[64];
        byte[] key = ((KeyParameter)params).getKey();
        this.setKey(key);
        int keyBits = key.length * 8;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), keyBits < 256 ? keyBits : 256, params, Utils.getPurpose(this.forEncryption)));
    }

    public void setKey(byte[] kb) {
        if (kb.length == 0 || kb.length > 64 || kb.length < 16 || kb.length % 8 != 0) {
            throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8");
        }
        this.bytes2ints(kb, this.workingKey, 0, 0);
        for (int i = 16; i < 64; ++i) {
            this.workingKey[i] = ((this.workingKey[i - 2] >>> 17 | this.workingKey[i - 2] << -17) ^ (this.workingKey[i - 2] >>> 19 | this.workingKey[i - 2] << -19) ^ this.workingKey[i - 2] >>> 10) + this.workingKey[i - 7] + ((this.workingKey[i - 15] >>> 7 | this.workingKey[i - 15] << -7) ^ (this.workingKey[i - 15] >>> 18 | this.workingKey[i - 15] << -18) ^ this.workingKey[i - 15] >>> 3) + this.workingKey[i - 16];
        }
    }

    private void encryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[8];
        this.byteBlockToInts(in, block, inOffset, 0);
        for (int i = 0; i < 64; ++i) {
            int tmp = ((block[4] >>> 6 | block[4] << -6) ^ (block[4] >>> 11 | block[4] << -11) ^ (block[4] >>> 25 | block[4] << -25)) + (block[4] & block[5] ^ ~block[4] & block[6]) + block[7] + K[i] + this.workingKey[i];
            block[7] = block[6];
            block[6] = block[5];
            block[5] = block[4];
            block[4] = block[3] + tmp;
            block[3] = block[2];
            block[2] = block[1];
            block[1] = block[0];
            block[0] = tmp + ((block[0] >>> 2 | block[0] << -2) ^ (block[0] >>> 13 | block[0] << -13) ^ (block[0] >>> 22 | block[0] << -22)) + (block[0] & block[2] ^ block[0] & block[3] ^ block[2] & block[3]);
        }
        this.ints2bytes(block, out, outOffset);
    }

    private void decryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[8];
        this.byteBlockToInts(in, block, inOffset, 0);
        for (int i = 63; i > -1; --i) {
            int tmp = block[0] - ((block[1] >>> 2 | block[1] << -2) ^ (block[1] >>> 13 | block[1] << -13) ^ (block[1] >>> 22 | block[1] << -22)) - (block[1] & block[2] ^ block[1] & block[3] ^ block[2] & block[3]);
            block[0] = block[1];
            block[1] = block[2];
            block[2] = block[3];
            block[3] = block[4] - tmp;
            block[4] = block[5];
            block[5] = block[6];
            block[6] = block[7];
            block[7] = tmp - K[i] - this.workingKey[i] - ((block[4] >>> 6 | block[4] << -6) ^ (block[4] >>> 11 | block[4] << -11) ^ (block[4] >>> 25 | block[4] << -25)) - (block[4] & block[5] ^ ~block[4] & block[6]);
        }
        this.ints2bytes(block, out, outOffset);
    }

    @Override
    public int processBlock(byte[] in, int inOffset, byte[] out, int outOffset) throws DataLengthException, IllegalStateException {
        if (this.workingKey == null) {
            throw new IllegalStateException("Shacal2 not initialised");
        }
        if (inOffset + 32 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOffset + 32 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            this.encryptBlock(in, inOffset, out, outOffset);
        } else {
            this.decryptBlock(in, inOffset, out, outOffset);
        }
        return 32;
    }

    private void byteBlockToInts(byte[] bytes, int[] block, int bytesPos, int blockPos) {
        for (int i = blockPos; i < 8; ++i) {
            block[i] = (bytes[bytesPos++] & 0xFF) << 24 | (bytes[bytesPos++] & 0xFF) << 16 | (bytes[bytesPos++] & 0xFF) << 8 | bytes[bytesPos++] & 0xFF;
        }
    }

    private void bytes2ints(byte[] bytes, int[] block, int bytesPos, int blockPos) {
        for (int i = blockPos; i < bytes.length / 4; ++i) {
            block[i] = (bytes[bytesPos++] & 0xFF) << 24 | (bytes[bytesPos++] & 0xFF) << 16 | (bytes[bytesPos++] & 0xFF) << 8 | bytes[bytesPos++] & 0xFF;
        }
    }

    private void ints2bytes(int[] block, byte[] out, int pos) {
        for (int i = 0; i < block.length; ++i) {
            out[pos++] = (byte)(block[i] >>> 24);
            out[pos++] = (byte)(block[i] >>> 16);
            out[pos++] = (byte)(block[i] >>> 8);
            out[pos++] = (byte)block[i];
        }
    }
}

