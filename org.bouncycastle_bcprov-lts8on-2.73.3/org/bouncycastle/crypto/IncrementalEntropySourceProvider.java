/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.security.SecureRandom;
import org.bouncycastle.crypto.IncrementalEntropySource;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class IncrementalEntropySourceProvider
implements EntropySourceProvider {
    private final SecureRandom random;
    private final boolean predictionResistant;

    public IncrementalEntropySourceProvider(SecureRandom random, boolean isPredictionResistant) {
        this.random = random;
        this.predictionResistant = isPredictionResistant;
    }

    @Override
    public EntropySource get(final int bitsRequired) {
        return new IncrementalEntropySource(){
            final int numBytes;
            {
                this.numBytes = (bitsRequired + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return IncrementalEntropySourceProvider.this.predictionResistant;
            }

            @Override
            public byte[] getEntropy() {
                try {
                    return this.getEntropy(0L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override
            public byte[] getEntropy(long pause) throws InterruptedException {
                byte[] rn;
                byte[] seed = new byte[this.numBytes];
                for (int i = 0; i < this.numBytes / 8; ++i) {
                    IncrementalEntropySourceProvider.sleep(pause);
                    rn = IncrementalEntropySourceProvider.this.random.generateSeed(8);
                    System.arraycopy(rn, 0, seed, i * 8, rn.length);
                }
                int extra = this.numBytes - this.numBytes / 8 * 8;
                if (extra != 0) {
                    IncrementalEntropySourceProvider.sleep(pause);
                    rn = IncrementalEntropySourceProvider.this.random.generateSeed(extra);
                    System.arraycopy(rn, 0, seed, seed.length - rn.length, rn.length);
                }
                return seed;
            }

            @Override
            public int entropySize() {
                return bitsRequired;
            }
        };
    }

    private static void sleep(long ms) throws InterruptedException {
        if (ms != 0L) {
            Thread.sleep(ms);
        }
    }
}

