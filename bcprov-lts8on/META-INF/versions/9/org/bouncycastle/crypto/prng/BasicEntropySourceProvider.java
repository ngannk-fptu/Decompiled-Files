/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.X931SecureRandom;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BasicEntropySourceProvider
implements EntropySourceProvider {
    private final SecureRandom _sr;
    private final boolean _predictionResistant;

    public BasicEntropySourceProvider(SecureRandom random, boolean isPredictionResistant) {
        this._sr = random;
        this._predictionResistant = isPredictionResistant;
    }

    @Override
    public EntropySource get(final int bitsRequired) {
        return new EntropySource(){

            @Override
            public boolean isPredictionResistant() {
                return BasicEntropySourceProvider.this._predictionResistant;
            }

            @Override
            public byte[] getEntropy() {
                if (BasicEntropySourceProvider.this._sr instanceof SP800SecureRandom || BasicEntropySourceProvider.this._sr instanceof X931SecureRandom) {
                    byte[] rv = new byte[(bitsRequired + 7) / 8];
                    BasicEntropySourceProvider.this._sr.nextBytes(rv);
                    return rv;
                }
                return BasicEntropySourceProvider.this._sr.generateSeed((bitsRequired + 7) / 8);
            }

            @Override
            public int entropySize() {
                return bitsRequired;
            }
        };
    }
}

