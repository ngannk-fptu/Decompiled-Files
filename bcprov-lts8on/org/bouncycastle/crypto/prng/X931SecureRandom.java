/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropyUtil;
import org.bouncycastle.crypto.prng.X931RNG;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X931SecureRandom
extends SecureRandom {
    private final boolean predictionResistant;
    private final SecureRandom randomSource;
    private final X931RNG drbg;

    X931SecureRandom(SecureRandom randomSource, X931RNG drbg, boolean predictionResistant) {
        this.randomSource = randomSource;
        this.drbg = drbg;
        this.predictionResistant = predictionResistant;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSeed(byte[] seed) {
        X931SecureRandom x931SecureRandom = this;
        synchronized (x931SecureRandom) {
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
        X931SecureRandom x931SecureRandom = this;
        synchronized (x931SecureRandom) {
            if (this.randomSource != null) {
                this.randomSource.setSeed(seed);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void nextBytes(byte[] bytes) {
        X931SecureRandom x931SecureRandom = this;
        synchronized (x931SecureRandom) {
            if (this.drbg.generate(bytes, this.predictionResistant) < 0) {
                this.drbg.reseed();
                this.drbg.generate(bytes, this.predictionResistant);
            }
        }
    }

    @Override
    public byte[] generateSeed(int numBytes) {
        return EntropyUtil.generateSeed(this.drbg.getEntropySource(), numBytes);
    }
}

