/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.crypto.prng.drbg.Utils;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class HMacSP800DRBG
implements SP80090DRBG {
    private static final long RESEED_MAX = 0x800000000000L;
    private static final int MAX_BITS_REQUEST = 262144;
    private byte[] _K;
    private byte[] _V;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private Mac _hMac;
    private int _securityStrength;

    public HMacSP800DRBG(Mac hMac, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        if (securityStrength > Utils.getMaxSecurityStrength(hMac)) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this._securityStrength = securityStrength;
        this._entropySource = entropySource;
        this._hMac = hMac;
        byte[] entropy = this.getEntropy();
        byte[] seedMaterial = Arrays.concatenate(entropy, nonce, personalizationString);
        this._K = new byte[hMac.getMacSize()];
        this._V = new byte[this._K.length];
        Arrays.fill(this._V, (byte)1);
        this.hmac_DRBG_Update(seedMaterial);
        this._reseedCounter = 1L;
    }

    private void hmac_DRBG_Update(byte[] seedMaterial) {
        this.hmac_DRBG_Update_Func(seedMaterial, (byte)0);
        if (seedMaterial != null) {
            this.hmac_DRBG_Update_Func(seedMaterial, (byte)1);
        }
    }

    private void hmac_DRBG_Update_Func(byte[] seedMaterial, byte vValue) {
        this._hMac.init(new KeyParameter(this._K));
        this._hMac.update(this._V, 0, this._V.length);
        this._hMac.update(vValue);
        if (seedMaterial != null) {
            this._hMac.update(seedMaterial, 0, seedMaterial.length);
        }
        this._hMac.doFinal(this._K, 0);
        this._hMac.init(new KeyParameter(this._K));
        this._hMac.update(this._V, 0, this._V.length);
        this._hMac.doFinal(this._V, 0);
    }

    @Override
    public int getBlockSize() {
        return this._V.length * 8;
    }

    @Override
    public int generate(byte[] output, byte[] additionalInput, boolean predictionResistant) {
        int numberOfBits = output.length * 8;
        if (numberOfBits > 262144) {
            throw new IllegalArgumentException("Number of bits per request limited to 262144");
        }
        if (this._reseedCounter > 0x800000000000L) {
            return -1;
        }
        if (predictionResistant) {
            this.reseed(additionalInput);
            additionalInput = null;
        }
        if (additionalInput != null) {
            this.hmac_DRBG_Update(additionalInput);
        }
        byte[] rv = new byte[output.length];
        int m = output.length / this._V.length;
        this._hMac.init(new KeyParameter(this._K));
        for (int i = 0; i < m; ++i) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, rv, i * this._V.length, this._V.length);
        }
        if (m * this._V.length < rv.length) {
            this._hMac.update(this._V, 0, this._V.length);
            this._hMac.doFinal(this._V, 0);
            System.arraycopy(this._V, 0, rv, m * this._V.length, rv.length - m * this._V.length);
        }
        this.hmac_DRBG_Update(additionalInput);
        ++this._reseedCounter;
        System.arraycopy(rv, 0, output, 0, output.length);
        return numberOfBits;
    }

    @Override
    public void reseed(byte[] additionalInput) {
        byte[] entropy = this.getEntropy();
        byte[] seedMaterial = Arrays.concatenate(entropy, additionalInput);
        this.hmac_DRBG_Update(seedMaterial);
        this._reseedCounter = 1L;
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }
}

