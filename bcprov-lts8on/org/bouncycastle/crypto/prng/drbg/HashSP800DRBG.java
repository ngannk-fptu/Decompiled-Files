/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.crypto.prng.drbg.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class HashSP800DRBG
implements SP80090DRBG {
    private static final byte[] ONE = new byte[]{1};
    private static final long RESEED_MAX = 0x800000000000L;
    private static final int MAX_BITS_REQUEST = 262144;
    private static final Hashtable seedlens = new Hashtable();
    private Digest _digest;
    private byte[] _V;
    private byte[] _C;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private int _securityStrength;
    private int _seedLength;

    public HashSP800DRBG(Digest digest, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        if (securityStrength > Utils.getMaxSecurityStrength(digest)) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this._digest = digest;
        this._entropySource = entropySource;
        this._securityStrength = securityStrength;
        this._seedLength = (Integer)seedlens.get(digest.getAlgorithmName());
        byte[] entropy = this.getEntropy();
        byte[] seedMaterial = Arrays.concatenate(entropy, nonce, personalizationString);
        byte[] seed = Utils.hash_df(this._digest, seedMaterial, this._seedLength);
        this._V = seed;
        byte[] subV = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, subV, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, subV, this._seedLength);
        this._reseedCounter = 1L;
    }

    @Override
    public int getBlockSize() {
        return this._digest.getDigestSize() * 8;
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
            byte[] newInput = new byte[1 + this._V.length + additionalInput.length];
            newInput[0] = 2;
            System.arraycopy(this._V, 0, newInput, 1, this._V.length);
            System.arraycopy(additionalInput, 0, newInput, 1 + this._V.length, additionalInput.length);
            byte[] w = this.hash(newInput);
            this.addTo(this._V, w);
        }
        byte[] rv = this.hashgen(this._V, numberOfBits);
        byte[] subH = new byte[this._V.length + 1];
        System.arraycopy(this._V, 0, subH, 1, this._V.length);
        subH[0] = 3;
        byte[] H = this.hash(subH);
        this.addTo(this._V, H);
        this.addTo(this._V, this._C);
        byte[] c = new byte[]{(byte)(this._reseedCounter >> 24), (byte)(this._reseedCounter >> 16), (byte)(this._reseedCounter >> 8), (byte)this._reseedCounter};
        this.addTo(this._V, c);
        ++this._reseedCounter;
        System.arraycopy(rv, 0, output, 0, output.length);
        return numberOfBits;
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }

    private void addTo(byte[] longer, byte[] shorter) {
        int res;
        int i;
        int carry = 0;
        for (i = 1; i <= shorter.length; ++i) {
            res = (longer[longer.length - i] & 0xFF) + (shorter[shorter.length - i] & 0xFF) + carry;
            carry = res > 255 ? 1 : 0;
            longer[longer.length - i] = (byte)res;
        }
        for (i = shorter.length + 1; i <= longer.length; ++i) {
            res = (longer[longer.length - i] & 0xFF) + carry;
            carry = res > 255 ? 1 : 0;
            longer[longer.length - i] = (byte)res;
        }
    }

    @Override
    public void reseed(byte[] additionalInput) {
        byte[] entropy = this.getEntropy();
        byte[] seedMaterial = Arrays.concatenate(ONE, this._V, entropy, additionalInput);
        byte[] seed = Utils.hash_df(this._digest, seedMaterial, this._seedLength);
        this._V = seed;
        byte[] subV = new byte[this._V.length + 1];
        subV[0] = 0;
        System.arraycopy(this._V, 0, subV, 1, this._V.length);
        this._C = Utils.hash_df(this._digest, subV, this._seedLength);
        this._reseedCounter = 1L;
    }

    private byte[] hash(byte[] input) {
        byte[] hash = new byte[this._digest.getDigestSize()];
        this.doHash(input, hash);
        return hash;
    }

    private void doHash(byte[] input, byte[] output) {
        this._digest.update(input, 0, input.length);
        this._digest.doFinal(output, 0);
    }

    private byte[] hashgen(byte[] input, int lengthInBits) {
        int digestSize = this._digest.getDigestSize();
        int m = lengthInBits / 8 / digestSize;
        byte[] data = new byte[input.length];
        System.arraycopy(input, 0, data, 0, input.length);
        byte[] W = new byte[lengthInBits / 8];
        byte[] dig = new byte[this._digest.getDigestSize()];
        for (int i = 0; i <= m; ++i) {
            this.doHash(data, dig);
            int bytesToCopy = W.length - i * dig.length > dig.length ? dig.length : W.length - i * dig.length;
            System.arraycopy(dig, 0, W, i * dig.length, bytesToCopy);
            this.addTo(data, ONE);
        }
        return W;
    }

    static {
        seedlens.put("SHA-1", Integers.valueOf(440));
        seedlens.put("SHA-224", Integers.valueOf(440));
        seedlens.put("SHA-256", Integers.valueOf(440));
        seedlens.put("SHA-512/256", Integers.valueOf(440));
        seedlens.put("SHA-512/224", Integers.valueOf(440));
        seedlens.put("SHA-384", Integers.valueOf(888));
        seedlens.put("SHA-512", Integers.valueOf(888));
    }
}

