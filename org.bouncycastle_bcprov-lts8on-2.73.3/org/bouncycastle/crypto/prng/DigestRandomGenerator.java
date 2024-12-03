/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.util.Arrays;

public class DigestRandomGenerator
implements RandomGenerator {
    private static long CYCLE_COUNT = 10L;
    private long stateCounter;
    private long seedCounter;
    private Digest digest;
    private byte[] state;
    private byte[] seed;

    public DigestRandomGenerator(Digest digest) {
        this.digest = digest;
        this.seed = new byte[digest.getDigestSize()];
        this.seedCounter = 1L;
        this.state = new byte[digest.getDigestSize()];
        this.stateCounter = 1L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSeedMaterial(byte[] inSeed) {
        DigestRandomGenerator digestRandomGenerator = this;
        synchronized (digestRandomGenerator) {
            if (!Arrays.isNullOrEmpty(inSeed)) {
                this.digestUpdate(inSeed);
            }
            this.digestUpdate(this.seed);
            this.digestDoFinal(this.seed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSeedMaterial(long rSeed) {
        DigestRandomGenerator digestRandomGenerator = this;
        synchronized (digestRandomGenerator) {
            this.digestAddCounter(rSeed);
            this.digestUpdate(this.seed);
            this.digestDoFinal(this.seed);
        }
    }

    @Override
    public void nextBytes(byte[] bytes) {
        this.nextBytes(bytes, 0, bytes.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void nextBytes(byte[] bytes, int start, int len) {
        DigestRandomGenerator digestRandomGenerator = this;
        synchronized (digestRandomGenerator) {
            int stateOff = 0;
            this.generateState();
            int end = start + len;
            for (int i = start; i != end; ++i) {
                if (stateOff == this.state.length) {
                    this.generateState();
                    stateOff = 0;
                }
                bytes[i] = this.state[stateOff++];
            }
        }
    }

    private void cycleSeed() {
        this.digestUpdate(this.seed);
        this.digestAddCounter(this.seedCounter++);
        this.digestDoFinal(this.seed);
    }

    private void generateState() {
        this.digestAddCounter(this.stateCounter++);
        this.digestUpdate(this.state);
        this.digestUpdate(this.seed);
        this.digestDoFinal(this.state);
        if (this.stateCounter % CYCLE_COUNT == 0L) {
            this.cycleSeed();
        }
    }

    private void digestAddCounter(long seed) {
        for (int i = 0; i != 8; ++i) {
            this.digest.update((byte)seed);
            seed >>>= 8;
        }
    }

    private void digestUpdate(byte[] inSeed) {
        this.digest.update(inSeed, 0, inSeed.length);
    }

    private void digestDoFinal(byte[] result) {
        this.digest.doFinal(result, 0);
    }
}

