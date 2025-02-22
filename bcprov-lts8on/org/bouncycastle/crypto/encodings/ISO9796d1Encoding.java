/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.encodings;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class ISO9796d1Encoding
implements AsymmetricBlockCipher {
    private static final BigInteger SIXTEEN = BigInteger.valueOf(16L);
    private static final BigInteger SIX = BigInteger.valueOf(6L);
    private static byte[] shadows = new byte[]{14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 11, 6, 7, 10, 12, 1};
    private static byte[] inverse = new byte[]{8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 13, 10, 14, 9, 0, 7};
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private int bitSize;
    private int padBits = 0;
    private BigInteger modulus;

    public ISO9796d1Encoding(AsymmetricBlockCipher cipher) {
        this.engine = cipher;
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        RSAKeyParameters kParam = null;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            kParam = (RSAKeyParameters)rParam.getParameters();
        } else {
            kParam = (RSAKeyParameters)param;
        }
        this.engine.init(forEncryption, param);
        this.modulus = kParam.getModulus();
        this.bitSize = this.modulus.bitLength();
        this.forEncryption = forEncryption;
    }

    @Override
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return (baseBlockSize + 1) / 2;
        }
        return baseBlockSize;
    }

    @Override
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return (baseBlockSize + 1) / 2;
    }

    public void setPadBits(int padBits) {
        if (padBits > 7) {
            throw new IllegalArgumentException("padBits > 7");
        }
        this.padBits = padBits;
    }

    public int getPadBits() {
        return this.padBits;
    }

    @Override
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(in, inOff, inLen);
        }
        return this.decodeBlock(in, inOff, inLen);
    }

    private byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int i;
        byte[] block = new byte[(this.bitSize + 7) / 8];
        int r = this.padBits + 1;
        int z = inLen;
        int t = (this.bitSize + 13) / 16;
        for (i = 0; i < t; i += z) {
            if (i > t - z) {
                System.arraycopy(in, inOff + inLen - (t - i), block, block.length - t, t - i);
                continue;
            }
            System.arraycopy(in, inOff, block, block.length - (i + z), z);
        }
        for (i = block.length - 2 * t; i != block.length; i += 2) {
            byte val = block[block.length - t + i / 2];
            block[i] = (byte)(shadows[(val & 0xFF) >>> 4] << 4 | shadows[val & 0xF]);
            block[i + 1] = val;
        }
        int n = block.length - 2 * z;
        block[n] = (byte)(block[n] ^ r);
        block[block.length - 1] = (byte)(block[block.length - 1] << 4 | 6);
        int maxBit = 8 - (this.bitSize - 1) % 8;
        int offSet = 0;
        if (maxBit != 8) {
            block[0] = (byte)(block[0] & 255 >>> maxBit);
            block[0] = (byte)(block[0] | 128 >>> maxBit);
        } else {
            block[0] = 0;
            block[1] = (byte)(block[1] | 0x80);
            offSet = 1;
        }
        return this.engine.processBlock(block, offSet, block.length - offSet);
    }

    private byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        BigInteger iR;
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        int r = 1;
        int t = (this.bitSize + 13) / 16;
        BigInteger iS = new BigInteger(1, block);
        if (iS.mod(SIXTEEN).equals(SIX)) {
            iR = iS;
        } else if (this.modulus.subtract(iS).mod(SIXTEEN).equals(SIX)) {
            iR = this.modulus.subtract(iS);
        } else {
            throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
        }
        block = ISO9796d1Encoding.convertOutputDecryptOnly(iR);
        if ((block[block.length - 1] & 0xF) != 6) {
            throw new InvalidCipherTextException("invalid forcing byte in block");
        }
        block[block.length - 1] = (byte)((block[block.length - 1] & 0xFF) >>> 4 | inverse[(block[block.length - 2] & 0xFF) >> 4] << 4);
        block[0] = (byte)(shadows[(block[1] & 0xFF) >>> 4] << 4 | shadows[block[1] & 0xF]);
        boolean boundaryFound = false;
        int boundary = 0;
        for (int i = block.length - 1; i >= block.length - 2 * t; i -= 2) {
            int val = shadows[(block[i] & 0xFF) >>> 4] << 4 | shadows[block[i] & 0xF];
            if (((block[i - 1] ^ val) & 0xFF) == 0) continue;
            if (!boundaryFound) {
                boundaryFound = true;
                r = (block[i - 1] ^ val) & 0xFF;
                boundary = i - 1;
                continue;
            }
            throw new InvalidCipherTextException("invalid tsums in block");
        }
        block[boundary] = 0;
        byte[] nblock = new byte[(block.length - boundary) / 2];
        for (int i = 0; i < nblock.length; ++i) {
            nblock[i] = block[2 * i + boundary + 1];
        }
        this.padBits = r - 1;
        return nblock;
    }

    private static byte[] convertOutputDecryptOnly(BigInteger result) {
        byte[] output = result.toByteArray();
        if (output[0] == 0) {
            byte[] tmp = new byte[output.length - 1];
            System.arraycopy(output, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return output;
    }
}

