/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.DRBGProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropyUtil;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SP800SecureRandom
extends SecureRandom {
    private final DRBGProvider drbgProvider;
    private final boolean predictionResistant;
    private final SecureRandom randomSource;
    private final EntropySource entropySource;
    private SP80090DRBG drbg;

    SP800SecureRandom(SecureRandom randomSource, EntropySource entropySource, DRBGProvider drbgProvider, boolean predictionResistant) {
        super(null, null);
        this.randomSource = randomSource;
        this.entropySource = entropySource;
        this.drbgProvider = drbgProvider;
        this.predictionResistant = predictionResistant;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSeed(byte[] seed) {
        SP800SecureRandom sP800SecureRandom = this;
        synchronized (sP800SecureRandom) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSeed(long seed) {
        SP800SecureRandom sP800SecureRandom = this;
        synchronized (sP800SecureRandom) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }

    @Override
    public String getAlgorithm() {
        return this.drbgProvider.getAlgorithm();
    }

    @Override
    public String toString() {
        return this.getAlgorithm();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void nextBytes(byte[] bytes) {
        SP800SecureRandom sP800SecureRandom = this;
        synchronized (sP800SecureRandom) {
            if (this.drbg == null) {
                this.drbg = this.drbgProvider.get(this.entropySource);
            }
            if (this.drbg.generate(bytes, null, this.predictionResistant) < 0) {
                this.drbg.reseed(null);
                this.drbg.generate(bytes, null, this.predictionResistant);
            }
        }
    }

    @Override
    public byte[] generateSeed(int numBytes) {
        return EntropyUtil.generateSeed(this.entropySource, numBytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reseed(byte[] additionalInput) {
        SP800SecureRandom sP800SecureRandom = this;
        synchronized (sP800SecureRandom) {
            if (this.drbg == null) {
                this.drbg = this.drbgProvider.get(this.entropySource);
            }
            this.drbg.reseed(additionalInput);
        }
    }
}

