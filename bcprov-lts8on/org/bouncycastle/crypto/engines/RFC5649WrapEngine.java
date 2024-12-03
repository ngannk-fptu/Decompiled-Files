/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class RFC5649WrapEngine
implements Wrapper {
    private static final byte[] DEFAULT_IV = new byte[]{-90, 89, 89, -90};
    private final BlockCipher engine;
    private final byte[] preIV = new byte[4];
    private KeyParameter param = null;
    private boolean forWrapping = true;

    public RFC5649WrapEngine(BlockCipher engine) {
        this.engine = engine;
    }

    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            param = ((ParametersWithRandom)param).getParameters();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter)param;
            System.arraycopy(DEFAULT_IV, 0, this.preIV, 0, 4);
        } else if (param instanceof ParametersWithIV) {
            ParametersWithIV withIV = (ParametersWithIV)param;
            byte[] iv = withIV.getIV();
            if (iv.length != 4) {
                throw new IllegalArgumentException("IV length not equal to 4");
            }
            this.param = (KeyParameter)withIV.getParameters();
            System.arraycopy(iv, 0, this.preIV, 0, 4);
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    private byte[] padPlaintext(byte[] plaintext) {
        int plaintextLength = plaintext.length;
        int numOfZerosToAppend = (8 - plaintextLength % 8) % 8;
        byte[] paddedPlaintext = new byte[plaintextLength + numOfZerosToAppend];
        System.arraycopy(plaintext, 0, paddedPlaintext, 0, plaintextLength);
        if (numOfZerosToAppend != 0) {
            byte[] zeros = new byte[numOfZerosToAppend];
            System.arraycopy(zeros, 0, paddedPlaintext, plaintextLength, numOfZerosToAppend);
        }
        return paddedPlaintext;
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        byte[] iv = new byte[8];
        System.arraycopy(this.preIV, 0, iv, 0, 4);
        Pack.intToBigEndian(inLen, iv, 4);
        byte[] relevantPlaintext = new byte[inLen];
        System.arraycopy(in, inOff, relevantPlaintext, 0, inLen);
        byte[] paddedPlaintext = this.padPlaintext(relevantPlaintext);
        if (paddedPlaintext.length == 8) {
            byte[] paddedPlainTextWithIV = new byte[paddedPlaintext.length + iv.length];
            System.arraycopy(iv, 0, paddedPlainTextWithIV, 0, iv.length);
            System.arraycopy(paddedPlaintext, 0, paddedPlainTextWithIV, iv.length, paddedPlaintext.length);
            this.engine.init(true, this.param);
            int blockSize = this.engine.getBlockSize();
            for (int i = 0; i < paddedPlainTextWithIV.length; i += blockSize) {
                this.engine.processBlock(paddedPlainTextWithIV, i, paddedPlainTextWithIV, i);
            }
            return paddedPlainTextWithIV;
        }
        RFC3394WrapEngine wrapper = new RFC3394WrapEngine(this.engine);
        ParametersWithIV paramsWithIV = new ParametersWithIV(this.param, iv);
        wrapper.init(true, paramsWithIV);
        return wrapper.wrap(paddedPlaintext, 0, paddedPlaintext.length);
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int expectedZeros;
        byte[] paddedPlaintext;
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        if (n <= 1) {
            throw new InvalidCipherTextException("unwrap data must be at least 16 bytes");
        }
        byte[] relevantCiphertext = new byte[inLen];
        System.arraycopy(in, inOff, relevantCiphertext, 0, inLen);
        byte[] decrypted = new byte[inLen];
        byte[] extractedAIV = new byte[8];
        if (n == 2) {
            this.engine.init(false, this.param);
            int blockSize = this.engine.getBlockSize();
            for (int i = 0; i < relevantCiphertext.length; i += blockSize) {
                this.engine.processBlock(relevantCiphertext, i, decrypted, i);
            }
            System.arraycopy(decrypted, 0, extractedAIV, 0, extractedAIV.length);
            paddedPlaintext = new byte[decrypted.length - extractedAIV.length];
            System.arraycopy(decrypted, extractedAIV.length, paddedPlaintext, 0, paddedPlaintext.length);
        } else {
            paddedPlaintext = decrypted = this.rfc3394UnwrapNoIvCheck(in, inOff, inLen, extractedAIV);
        }
        byte[] extractedHighOrderAIV = new byte[4];
        System.arraycopy(extractedAIV, 0, extractedHighOrderAIV, 0, 4);
        int mli = Pack.bigEndianToInt(extractedAIV, 4);
        boolean isValid = Arrays.constantTimeAreEqual(extractedHighOrderAIV, this.preIV);
        int upperBound = paddedPlaintext.length;
        int lowerBound = upperBound - 8;
        if (mli <= lowerBound) {
            isValid = false;
        }
        if (mli > upperBound) {
            isValid = false;
        }
        if ((expectedZeros = upperBound - mli) >= 8 || expectedZeros < 0) {
            isValid = false;
            expectedZeros = 4;
        }
        byte[] zeros = new byte[expectedZeros];
        byte[] pad = new byte[expectedZeros];
        System.arraycopy(paddedPlaintext, paddedPlaintext.length - expectedZeros, pad, 0, expectedZeros);
        if (!Arrays.constantTimeAreEqual(pad, zeros)) {
            isValid = false;
        }
        if (!isValid) {
            throw new InvalidCipherTextException("checksum failed");
        }
        byte[] plaintext = new byte[mli];
        System.arraycopy(paddedPlaintext, 0, plaintext, 0, plaintext.length);
        return plaintext;
    }

    private byte[] rfc3394UnwrapNoIvCheck(byte[] in, int inOff, int inLen, byte[] extractedAIV) {
        byte[] block = new byte[inLen - 8];
        byte[] buf = new byte[16];
        System.arraycopy(in, inOff, buf, 0, 8);
        System.arraycopy(in, inOff + 8, block, 0, inLen - 8);
        this.engine.init(false, this.param);
        int n = inLen / 8;
        --n;
        for (int j = 5; j >= 0; --j) {
            for (int i = n; i >= 1; --i) {
                System.arraycopy(block, 8 * (i - 1), buf, 8, 8);
                int t = n * j + i;
                int k = 1;
                while (t != 0) {
                    int n2 = 8 - k;
                    buf[n2] = (byte)(buf[n2] ^ (byte)t);
                    t >>>= 8;
                    ++k;
                }
                this.engine.processBlock(buf, 0, buf, 0);
                System.arraycopy(buf, 8, block, 8 * (i - 1), 8);
            }
        }
        System.arraycopy(buf, 0, extractedAIV, 0, 8);
        return block;
    }
}

