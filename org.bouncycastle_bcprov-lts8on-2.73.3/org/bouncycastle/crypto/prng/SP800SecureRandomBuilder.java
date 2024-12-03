/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.DRBGProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.drbg.CTRSP800DRBG;
import org.bouncycastle.crypto.prng.drbg.HMacSP800DRBG;
import org.bouncycastle.crypto.prng.drbg.HashSP800DRBG;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SP800SecureRandomBuilder {
    private final SecureRandom random;
    private final EntropySourceProvider entropySourceProvider;
    private byte[] personalizationString;
    private int securityStrength = 256;
    private int entropyBitsRequired = 256;

    public SP800SecureRandomBuilder() {
        this(CryptoServicesRegistrar.getSecureRandom(), false);
    }

    public SP800SecureRandomBuilder(SecureRandom entropySource, boolean predictionResistant) {
        this.random = entropySource;
        this.entropySourceProvider = new BasicEntropySourceProvider(this.random, predictionResistant);
    }

    public SP800SecureRandomBuilder(EntropySourceProvider entropySourceProvider) {
        this.random = null;
        this.entropySourceProvider = entropySourceProvider;
    }

    public SP800SecureRandomBuilder setPersonalizationString(byte[] personalizationString) {
        this.personalizationString = Arrays.clone(personalizationString);
        return this;
    }

    public SP800SecureRandomBuilder setSecurityStrength(int securityStrength) {
        this.securityStrength = securityStrength;
        return this;
    }

    public SP800SecureRandomBuilder setEntropyBitsRequired(int entropyBitsRequired) {
        this.entropyBitsRequired = entropyBitsRequired;
        return this;
    }

    public SP800SecureRandom buildHash(Digest digest, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new HashDRBGProvider(digest, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    public SP800SecureRandom buildCTR(BlockCipher cipher, int keySizeInBits, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new CTRDRBGProvider(cipher, keySizeInBits, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    public SP800SecureRandom buildHMAC(Mac hMac, byte[] nonce, boolean predictionResistant) {
        return new SP800SecureRandom(this.random, this.entropySourceProvider.get(this.entropyBitsRequired), new HMacDRBGProvider(hMac, nonce, this.personalizationString, this.securityStrength), predictionResistant);
    }

    private static String getSimplifiedName(Digest digest) {
        String name = digest.getAlgorithmName();
        int dIndex = name.indexOf(45);
        if (dIndex > 0 && !name.startsWith("SHA3")) {
            return name.substring(0, dIndex) + name.substring(dIndex + 1);
        }
        return name;
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class CTRDRBGProvider
    implements DRBGProvider {
        private final BlockCipher blockCipher;
        private final int keySizeInBits;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public CTRDRBGProvider(BlockCipher blockCipher, int keySizeInBits, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.blockCipher = blockCipher;
            this.keySizeInBits = keySizeInBits;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override
        public String getAlgorithm() {
            if (this.blockCipher instanceof DESedeEngine) {
                return "CTR-DRBG-3KEY-TDES";
            }
            return "CTR-DRBG-" + this.blockCipher.getAlgorithmName() + this.keySizeInBits;
        }

        @Override
        public SP80090DRBG get(EntropySource entropySource) {
            return new CTRSP800DRBG(this.blockCipher, this.keySizeInBits, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class HMacDRBGProvider
    implements DRBGProvider {
        private final Mac hMac;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public HMacDRBGProvider(Mac hMac, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.hMac = hMac;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override
        public String getAlgorithm() {
            if (this.hMac instanceof HMac) {
                return "HMAC-DRBG-" + SP800SecureRandomBuilder.getSimplifiedName(((HMac)this.hMac).getUnderlyingDigest());
            }
            return "HMAC-DRBG-" + this.hMac.getAlgorithmName();
        }

        @Override
        public SP80090DRBG get(EntropySource entropySource) {
            return new HMacSP800DRBG(this.hMac, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class HashDRBGProvider
    implements DRBGProvider {
        private final Digest digest;
        private final byte[] nonce;
        private final byte[] personalizationString;
        private final int securityStrength;

        public HashDRBGProvider(Digest digest, byte[] nonce, byte[] personalizationString, int securityStrength) {
            this.digest = digest;
            this.nonce = nonce;
            this.personalizationString = personalizationString;
            this.securityStrength = securityStrength;
        }

        @Override
        public String getAlgorithm() {
            return "HASH-DRBG-" + SP800SecureRandomBuilder.getSimplifiedName(this.digest);
        }

        @Override
        public SP80090DRBG get(EntropySource entropySource) {
            return new HashSP800DRBG(this.digest, this.securityStrength, entropySource, this.personalizationString, this.nonce);
        }
    }
}

