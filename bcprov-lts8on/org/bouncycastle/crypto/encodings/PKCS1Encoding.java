/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class PKCS1Encoding
implements AsymmetricBlockCipher {
    private static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.not_strict";
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength;
    private int pLen = -1;
    private byte[] fallback = null;
    private byte[] blockBuffer;

    public PKCS1Encoding(AsymmetricBlockCipher cipher) {
        this.engine = cipher;
        this.useStrictLength = this.useStrict();
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, int pLen) {
        this.engine = cipher;
        this.useStrictLength = this.useStrict();
        this.pLen = pLen;
    }

    public PKCS1Encoding(AsymmetricBlockCipher cipher, byte[] fallback) {
        this.engine = cipher;
        this.useStrictLength = this.useStrict();
        this.fallback = fallback;
        this.pLen = fallback.length;
    }

    private boolean useStrict() {
        if (Properties.isOverrideSetTo(NOT_STRICT_LENGTH_ENABLED_PROPERTY, true)) {
            return false;
        }
        return !Properties.isOverrideSetTo(STRICT_LENGTH_ENABLED_PROPERTY, false);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        AsymmetricKeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.random = rParam.getRandom();
            kParam = (AsymmetricKeyParameter)rParam.getParameters();
        } else {
            kParam = (AsymmetricKeyParameter)param;
            if (!kParam.isPrivate() && forEncryption) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
        }
        this.engine.init(forEncryption, param);
        this.forPrivateKey = kParam.isPrivate();
        this.forEncryption = forEncryption;
        this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
        if (this.pLen > 0 && this.fallback == null && this.random == null) {
            throw new IllegalArgumentException("encoder requires random");
        }
    }

    @Override
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize - 10;
        }
        return baseBlockSize;
    }

    @Override
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return baseBlockSize - 10;
    }

    @Override
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(in, inOff, inLen);
        }
        return this.decodeBlock(in, inOff, inLen);
    }

    private byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (inLen > this.getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        byte[] block = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            block[0] = 1;
            for (int i = 1; i != block.length - inLen - 1; ++i) {
                block[i] = -1;
            }
        } else {
            this.random.nextBytes(block);
            block[0] = 2;
            for (int i = 1; i != block.length - inLen - 1; ++i) {
                while (block[i] == 0) {
                    block[i] = (byte)this.random.nextInt();
                }
            }
        }
        block[block.length - inLen - 1] = 0;
        System.arraycopy(in, inOff, block, block.length - inLen, inLen);
        return this.engine.processBlock(block, 0, block.length);
    }

    private static int checkPkcs1Encoding(byte[] encoded, int pLen) {
        int correct = 0;
        correct |= encoded[0] ^ 2;
        int plen = encoded.length - (pLen + 1);
        for (int i = 1; i < plen; ++i) {
            int tmp = encoded[i];
            tmp |= tmp >> 1;
            tmp |= tmp >> 2;
            tmp |= tmp >> 4;
            correct |= (tmp & 1) - 1;
        }
        correct |= encoded[encoded.length - (pLen + 1)];
        correct |= correct >> 1;
        correct |= correct >> 2;
        correct |= correct >> 4;
        return ~((correct & 1) - 1);
    }

    private byte[] decodeBlockOrRandom(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] random;
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        if (this.fallback == null) {
            random = new byte[this.pLen];
            this.random.nextBytes(random);
        } else {
            random = this.fallback;
        }
        byte[] data = this.useStrictLength & block.length != this.engine.getOutputBlockSize() ? this.blockBuffer : block;
        int correct = PKCS1Encoding.checkPkcs1Encoding(data, this.pLen);
        byte[] result = new byte[this.pLen];
        for (int i = 0; i < this.pLen; ++i) {
            result[i] = (byte)(data[i + (data.length - this.pLen)] & ~correct | random[i] & correct);
        }
        Arrays.fill(data, (byte)0);
        return result;
    }

    private byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.pLen != -1) {
            return this.decodeBlockOrRandom(in, inOff, inLen);
        }
        byte[] block = this.engine.processBlock(in, inOff, inLen);
        boolean incorrectLength = this.useStrictLength & block.length != this.engine.getOutputBlockSize();
        byte[] data = block.length < this.getOutputBlockSize() ? this.blockBuffer : block;
        byte type = data[0];
        boolean badType = this.forPrivateKey ? type != 2 : type != 1;
        int start = this.findStart(type, data);
        if (badType | ++start < 10) {
            Arrays.fill(data, (byte)0);
            throw new InvalidCipherTextException("block incorrect");
        }
        if (incorrectLength) {
            Arrays.fill(data, (byte)0);
            throw new InvalidCipherTextException("block incorrect size");
        }
        byte[] result = new byte[data.length - start];
        System.arraycopy(data, start, result, 0, result.length);
        return result;
    }

    private int findStart(byte type, byte[] block) throws InvalidCipherTextException {
        int start = -1;
        boolean padErr = false;
        for (int i = 1; i != block.length; ++i) {
            byte pad = block[i];
            if (pad == 0 & start < 0) {
                start = i;
            }
            padErr |= type == 1 & start < 0 & pad != -1;
        }
        if (padErr) {
            return -1;
        }
        return start;
    }
}

