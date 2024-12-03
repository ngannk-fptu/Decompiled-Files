/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

public class TestRandomEntropySourceProvider
implements EntropySourceProvider {
    private final SecureRandom _sr = new SecureRandom();
    private final boolean _predictionResistant;

    public TestRandomEntropySourceProvider(boolean isPredictionResistant) {
        this._predictionResistant = isPredictionResistant;
    }

    @Override
    public EntropySource get(final int bitsRequired) {
        return new EntropySource(){

            @Override
            public boolean isPredictionResistant() {
                return TestRandomEntropySourceProvider.this._predictionResistant;
            }

            @Override
            public byte[] getEntropy() {
                byte[] rv = new byte[(bitsRequired + 7) / 8];
                TestRandomEntropySourceProvider.this._sr.nextBytes(rv);
                return rv;
            }

            @Override
            public int entropySize() {
                return bitsRequired;
            }
        };
    }
}

