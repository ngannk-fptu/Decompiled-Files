/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.X931RNG;
import org.bouncycastle.crypto.prng.X931SecureRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class X931SecureRandomBuilder {
    private SecureRandom random;
    private EntropySourceProvider entropySourceProvider;
    private byte[] dateTimeVector;

    public X931SecureRandomBuilder() {
        this(CryptoServicesRegistrar.getSecureRandom(), false);
    }

    public X931SecureRandomBuilder(SecureRandom entropySource, boolean predictionResistant) {
        this.random = entropySource;
        this.entropySourceProvider = new BasicEntropySourceProvider(this.random, predictionResistant);
    }

    public X931SecureRandomBuilder(EntropySourceProvider entropySourceProvider) {
        this.random = null;
        this.entropySourceProvider = entropySourceProvider;
    }

    public X931SecureRandomBuilder setDateTimeVector(byte[] dateTimeVector) {
        this.dateTimeVector = Arrays.clone(dateTimeVector);
        return this;
    }

    public X931SecureRandom build(BlockCipher engine, KeyParameter key, boolean predictionResistant) {
        if (this.dateTimeVector == null) {
            this.dateTimeVector = new byte[engine.getBlockSize()];
            Pack.longToBigEndian(System.currentTimeMillis(), this.dateTimeVector, 0);
        }
        engine.init(true, key);
        return new X931SecureRandom(this.random, new X931RNG(engine, this.dateTimeVector, this.entropySourceProvider.get(engine.getBlockSize() * 8)), predictionResistant);
    }
}

