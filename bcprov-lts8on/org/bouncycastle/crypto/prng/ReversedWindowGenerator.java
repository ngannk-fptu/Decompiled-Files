/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.prng.RandomGenerator;

public class ReversedWindowGenerator
implements RandomGenerator {
    private final RandomGenerator generator;
    private byte[] window;
    private int windowCount;

    public ReversedWindowGenerator(RandomGenerator generator, int windowSize) {
        if (generator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        if (windowSize < 2) {
            throw new IllegalArgumentException("windowSize must be at least 2");
        }
        this.generator = generator;
        this.window = new byte[windowSize];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSeedMaterial(byte[] seed) {
        ReversedWindowGenerator reversedWindowGenerator = this;
        synchronized (reversedWindowGenerator) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSeedMaterial(long seed) {
        ReversedWindowGenerator reversedWindowGenerator = this;
        synchronized (reversedWindowGenerator) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    @Override
    public void nextBytes(byte[] bytes) {
        this.doNextBytes(bytes, 0, bytes.length);
    }

    @Override
    public void nextBytes(byte[] bytes, int start, int len) {
        this.doNextBytes(bytes, start, len);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doNextBytes(byte[] bytes, int start, int len) {
        ReversedWindowGenerator reversedWindowGenerator = this;
        synchronized (reversedWindowGenerator) {
            int done = 0;
            while (done < len) {
                if (this.windowCount < 1) {
                    this.generator.nextBytes(this.window, 0, this.window.length);
                    this.windowCount = this.window.length;
                }
                bytes[start + done++] = this.window[--this.windowCount];
            }
        }
    }
}

