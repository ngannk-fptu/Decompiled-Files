/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.crypto.prng.drbg.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class CTRSP800DRBG
implements SP80090DRBG {
    private static final long TDEA_RESEED_MAX = 0x80000000L;
    private static final long AES_RESEED_MAX = 0x800000000000L;
    private static final int TDEA_MAX_BITS_REQUEST = 4096;
    private static final int AES_MAX_BITS_REQUEST = 262144;
    private EntropySource _entropySource;
    private BlockCipher _engine;
    private int _keySizeInBits;
    private int _seedLength;
    private int _securityStrength;
    private byte[] _Key;
    private byte[] _V;
    private long _reseedCounter = 0L;
    private boolean _isTDEA = false;
    private static final byte[] K_BITS = Hex.decodeStrict("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");

    public CTRSP800DRBG(BlockCipher engine, int keySizeInBits, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        this._entropySource = entropySource;
        this._engine = engine;
        this._keySizeInBits = keySizeInBits;
        this._securityStrength = securityStrength;
        this._seedLength = keySizeInBits + engine.getBlockSize() * 8;
        this._isTDEA = this.isTDEA(engine);
        if (securityStrength > 256) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (this.getMaxSecurityStrength(engine, keySizeInBits) < securityStrength) {
            throw new IllegalArgumentException("Requested security strength is not supported by block cipher and key size");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        byte[] entropy = this.getEntropy();
        this.CTR_DRBG_Instantiate_algorithm(entropy, nonce, personalizationString);
    }

    private void CTR_DRBG_Instantiate_algorithm(byte[] entropy, byte[] nonce, byte[] personalisationString) {
        byte[] seedMaterial = Arrays.concatenate(entropy, nonce, personalisationString);
        byte[] seed = this.Block_Cipher_df(seedMaterial, this._seedLength);
        int outlen = this._engine.getBlockSize();
        this._Key = new byte[(this._keySizeInBits + 7) / 8];
        this._V = new byte[outlen];
        this.CTR_DRBG_Update(seed, this._Key, this._V);
        this._reseedCounter = 1L;
    }

    private void CTR_DRBG_Update(byte[] seed, byte[] key, byte[] v) {
        byte[] temp = new byte[seed.length];
        byte[] outputBlock = new byte[this._engine.getBlockSize()];
        int i = 0;
        int outLen = this._engine.getBlockSize();
        this._engine.init(true, new KeyParameter(this.expandKey(key)));
        while (i * outLen < seed.length) {
            this.addOneTo(v);
            this._engine.processBlock(v, 0, outputBlock, 0);
            int bytesToCopy = temp.length - i * outLen > outLen ? outLen : temp.length - i * outLen;
            System.arraycopy(outputBlock, 0, temp, i * outLen, bytesToCopy);
            ++i;
        }
        this.XOR(temp, seed, temp, 0);
        System.arraycopy(temp, 0, key, 0, key.length);
        System.arraycopy(temp, key.length, v, 0, v.length);
    }

    private void CTR_DRBG_Reseed_algorithm(byte[] additionalInput) {
        byte[] seedMaterial = Arrays.concatenate(this.getEntropy(), additionalInput);
        seedMaterial = this.Block_Cipher_df(seedMaterial, this._seedLength);
        this.CTR_DRBG_Update(seedMaterial, this._Key, this._V);
        this._reseedCounter = 1L;
    }

    private void XOR(byte[] out, byte[] a, byte[] b, int bOff) {
        for (int i = 0; i < out.length; ++i) {
            out[i] = (byte)(a[i] ^ b[i + bOff]);
        }
    }

    private void addOneTo(byte[] longer) {
        int carry = 1;
        for (int i = 1; i <= longer.length; ++i) {
            int res = (longer[longer.length - i] & 0xFF) + carry;
            carry = res > 255 ? 1 : 0;
            longer[longer.length - i] = (byte)res;
        }
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }

    private byte[] Block_Cipher_df(byte[] inputString, int bitLength) {
        int outLen = this._engine.getBlockSize();
        int L = inputString.length;
        int N = bitLength / 8;
        int sLen = 8 + L + 1;
        int blockLen = (sLen + outLen - 1) / outLen * outLen;
        byte[] S = new byte[blockLen];
        this.copyIntToByteArray(S, L, 0);
        this.copyIntToByteArray(S, N, 4);
        System.arraycopy(inputString, 0, S, 8, L);
        S[8 + L] = -128;
        byte[] temp = new byte[this._keySizeInBits / 8 + outLen];
        byte[] bccOut = new byte[outLen];
        byte[] IV = new byte[outLen];
        int i = 0;
        byte[] K = new byte[this._keySizeInBits / 8];
        System.arraycopy(K_BITS, 0, K, 0, K.length);
        while (i * outLen * 8 < this._keySizeInBits + outLen * 8) {
            this.copyIntToByteArray(IV, i, 0);
            this.BCC(bccOut, K, IV, S);
            int bytesToCopy = temp.length - i * outLen > outLen ? outLen : temp.length - i * outLen;
            System.arraycopy(bccOut, 0, temp, i * outLen, bytesToCopy);
            ++i;
        }
        byte[] X = new byte[outLen];
        System.arraycopy(temp, 0, K, 0, K.length);
        System.arraycopy(temp, K.length, X, 0, X.length);
        temp = new byte[bitLength / 8];
        i = 0;
        this._engine.init(true, new KeyParameter(this.expandKey(K)));
        while (i * outLen < temp.length) {
            this._engine.processBlock(X, 0, X, 0);
            int bytesToCopy = temp.length - i * outLen > outLen ? outLen : temp.length - i * outLen;
            System.arraycopy(X, 0, temp, i * outLen, bytesToCopy);
            ++i;
        }
        return temp;
    }

    private void BCC(byte[] bccOut, byte[] k, byte[] iV, byte[] data) {
        int outlen = this._engine.getBlockSize();
        byte[] chainingValue = new byte[outlen];
        int n = data.length / outlen;
        byte[] inputBlock = new byte[outlen];
        this._engine.init(true, new KeyParameter(this.expandKey(k)));
        this._engine.processBlock(iV, 0, chainingValue, 0);
        for (int i = 0; i < n; ++i) {
            this.XOR(inputBlock, chainingValue, data, i * outlen);
            this._engine.processBlock(inputBlock, 0, chainingValue, 0);
        }
        System.arraycopy(chainingValue, 0, bccOut, 0, bccOut.length);
    }

    private void copyIntToByteArray(byte[] buf, int value, int offSet) {
        buf[offSet + 0] = (byte)(value >> 24);
        buf[offSet + 1] = (byte)(value >> 16);
        buf[offSet + 2] = (byte)(value >> 8);
        buf[offSet + 3] = (byte)value;
    }

    @Override
    public int getBlockSize() {
        return this._V.length * 8;
    }

    @Override
    public int generate(byte[] output, byte[] additionalInput, boolean predictionResistant) {
        if (this._isTDEA) {
            if (this._reseedCounter > 0x80000000L) {
                return -1;
            }
            if (Utils.isTooLarge(output, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        } else {
            if (this._reseedCounter > 0x800000000000L) {
                return -1;
            }
            if (Utils.isTooLarge(output, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (predictionResistant) {
            this.CTR_DRBG_Reseed_algorithm(additionalInput);
            additionalInput = null;
        }
        if (additionalInput != null) {
            additionalInput = this.Block_Cipher_df(additionalInput, this._seedLength);
            this.CTR_DRBG_Update(additionalInput, this._Key, this._V);
        } else {
            additionalInput = new byte[this._seedLength / 8];
        }
        byte[] out = new byte[this._V.length];
        this._engine.init(true, new KeyParameter(this.expandKey(this._Key)));
        for (int i = 0; i <= output.length / out.length; ++i) {
            int bytesToCopy;
            int n = bytesToCopy = output.length - i * out.length > out.length ? out.length : output.length - i * this._V.length;
            if (bytesToCopy == 0) continue;
            this.addOneTo(this._V);
            this._engine.processBlock(this._V, 0, out, 0);
            System.arraycopy(out, 0, output, i * out.length, bytesToCopy);
        }
        this.CTR_DRBG_Update(additionalInput, this._Key, this._V);
        ++this._reseedCounter;
        return output.length * 8;
    }

    @Override
    public void reseed(byte[] additionalInput) {
        this.CTR_DRBG_Reseed_algorithm(additionalInput);
    }

    private boolean isTDEA(BlockCipher cipher) {
        return cipher.getAlgorithmName().equals("DESede") || cipher.getAlgorithmName().equals("TDEA");
    }

    private int getMaxSecurityStrength(BlockCipher cipher, int keySizeInBits) {
        if (this.isTDEA(cipher) && keySizeInBits == 168) {
            return 112;
        }
        if (cipher.getAlgorithmName().equals("AES")) {
            return keySizeInBits;
        }
        return -1;
    }

    byte[] expandKey(byte[] key) {
        if (this._isTDEA) {
            byte[] tmp = new byte[24];
            this.padKey(key, 0, tmp, 0);
            this.padKey(key, 7, tmp, 8);
            this.padKey(key, 14, tmp, 16);
            return tmp;
        }
        return key;
    }

    private void padKey(byte[] keyMaster, int keyOff, byte[] tmp, int tmpOff) {
        tmp[tmpOff + 0] = (byte)(keyMaster[keyOff + 0] & 0xFE);
        tmp[tmpOff + 1] = (byte)(keyMaster[keyOff + 0] << 7 | (keyMaster[keyOff + 1] & 0xFC) >>> 1);
        tmp[tmpOff + 2] = (byte)(keyMaster[keyOff + 1] << 6 | (keyMaster[keyOff + 2] & 0xF8) >>> 2);
        tmp[tmpOff + 3] = (byte)(keyMaster[keyOff + 2] << 5 | (keyMaster[keyOff + 3] & 0xF0) >>> 3);
        tmp[tmpOff + 4] = (byte)(keyMaster[keyOff + 3] << 4 | (keyMaster[keyOff + 4] & 0xE0) >>> 4);
        tmp[tmpOff + 5] = (byte)(keyMaster[keyOff + 4] << 3 | (keyMaster[keyOff + 5] & 0xC0) >>> 5);
        tmp[tmpOff + 6] = (byte)(keyMaster[keyOff + 5] << 2 | (keyMaster[keyOff + 6] & 0x80) >>> 6);
        tmp[tmpOff + 7] = (byte)(keyMaster[keyOff + 6] << 1);
        for (int i = tmpOff; i <= tmpOff + 7; ++i) {
            byte b = tmp[i];
            tmp[i] = (byte)(b & 0xFE | (b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 1) & 1);
        }
    }
}

