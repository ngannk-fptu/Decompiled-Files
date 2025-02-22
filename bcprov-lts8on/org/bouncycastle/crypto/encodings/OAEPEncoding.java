/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.encodings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class OAEPEncoding
implements AsymmetricBlockCipher {
    private byte[] defHash;
    private Digest mgf1Hash;
    private AsymmetricBlockCipher engine;
    private SecureRandom random;
    private boolean forEncryption;

    public OAEPEncoding(AsymmetricBlockCipher cipher) {
        this(cipher, DigestFactory.createSHA1(), null);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash) {
        this(cipher, hash, null);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash, byte[] encodingParams) {
        this(cipher, hash, hash, encodingParams);
    }

    public OAEPEncoding(AsymmetricBlockCipher cipher, Digest hash, Digest mgf1Hash, byte[] encodingParams) {
        this.engine = cipher;
        this.mgf1Hash = mgf1Hash;
        this.defHash = new byte[hash.getDigestSize()];
        hash.reset();
        if (encodingParams != null) {
            hash.update(encodingParams, 0, encodingParams.length);
        }
        hash.doFinal(this.defHash, 0);
    }

    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.random = rParam.getRandom();
        } else {
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        this.engine.init(forEncryption, param);
        this.forEncryption = forEncryption;
    }

    @Override
    public int getInputBlockSize() {
        int baseBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize - 1 - 2 * this.defHash.length;
        }
        return baseBlockSize;
    }

    @Override
    public int getOutputBlockSize() {
        int baseBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return baseBlockSize;
        }
        return baseBlockSize - 1 - 2 * this.defHash.length;
    }

    @Override
    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(in, inOff, inLen);
        }
        return this.decodeBlock(in, inOff, inLen);
    }

    public byte[] encodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int i;
        if (inLen > this.getInputBlockSize()) {
            throw new DataLengthException("input data too long");
        }
        byte[] block = new byte[this.getInputBlockSize() + 1 + 2 * this.defHash.length];
        System.arraycopy(in, inOff, block, block.length - inLen, inLen);
        block[block.length - inLen - 1] = 1;
        System.arraycopy(this.defHash, 0, block, this.defHash.length, this.defHash.length);
        byte[] seed = new byte[this.defHash.length];
        this.random.nextBytes(seed);
        byte[] mask = this.maskGeneratorFunction1(seed, 0, seed.length, block.length - this.defHash.length);
        for (i = this.defHash.length; i != block.length; ++i) {
            int n = i;
            block[n] = (byte)(block[n] ^ mask[i - this.defHash.length]);
        }
        System.arraycopy(seed, 0, block, 0, this.defHash.length);
        mask = this.maskGeneratorFunction1(block, this.defHash.length, block.length - this.defHash.length, this.defHash.length);
        for (i = 0; i != this.defHash.length; ++i) {
            int n = i;
            block[n] = (byte)(block[n] ^ mask[i]);
        }
        return this.engine.processBlock(block, 0, block.length);
    }

    public byte[] decodeBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        int i;
        byte[] data = this.engine.processBlock(in, inOff, inLen);
        byte[] block = new byte[this.engine.getOutputBlockSize()];
        int wrongMask = block.length - (2 * this.defHash.length + 1) >> 31;
        if (data.length <= block.length) {
            System.arraycopy(data, 0, block, block.length - data.length, data.length);
        } else {
            System.arraycopy(data, 0, block, 0, block.length);
            wrongMask |= 1;
        }
        byte[] mask = this.maskGeneratorFunction1(block, this.defHash.length, block.length - this.defHash.length, this.defHash.length);
        for (i = 0; i != this.defHash.length; ++i) {
            int n = i;
            block[n] = (byte)(block[n] ^ mask[i]);
        }
        mask = this.maskGeneratorFunction1(block, 0, this.defHash.length, block.length - this.defHash.length);
        for (i = this.defHash.length; i != block.length; ++i) {
            int n = i;
            block[n] = (byte)(block[n] ^ mask[i - this.defHash.length]);
        }
        for (i = 0; i != this.defHash.length; ++i) {
            wrongMask |= this.defHash[i] ^ block[this.defHash.length + i];
        }
        int start = -1;
        for (int index = 2 * this.defHash.length; index != block.length; ++index) {
            int octet = block[index] & 0xFF;
            int shouldSetMask = (-octet & start) >> 31;
            start += index & shouldSetMask;
        }
        wrongMask |= start >> 31;
        if ((wrongMask |= block[++start] ^ 1) != 0) {
            Arrays.fill(block, (byte)0);
            throw new InvalidCipherTextException("data wrong");
        }
        byte[] output = new byte[block.length - ++start];
        System.arraycopy(block, start, output, 0, output.length);
        Arrays.fill(block, (byte)0);
        return output;
    }

    private byte[] maskGeneratorFunction1(byte[] Z, int zOff, int zLen, int length) {
        int counter;
        byte[] mask = new byte[length];
        byte[] hashBuf = new byte[this.mgf1Hash.getDigestSize()];
        byte[] C = new byte[4];
        this.mgf1Hash.reset();
        for (counter = 0; counter < length / hashBuf.length; ++counter) {
            Pack.intToBigEndian(counter, C, 0);
            this.mgf1Hash.update(Z, zOff, zLen);
            this.mgf1Hash.update(C, 0, C.length);
            this.mgf1Hash.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * hashBuf.length, hashBuf.length);
        }
        if (counter * hashBuf.length < length) {
            Pack.intToBigEndian(counter, C, 0);
            this.mgf1Hash.update(Z, zOff, zLen);
            this.mgf1Hash.update(C, 0, C.length);
            this.mgf1Hash.doFinal(hashBuf, 0);
            System.arraycopy(hashBuf, 0, mask, counter * hashBuf.length, mask.length - counter * hashBuf.length);
        }
        return mask;
    }
}

