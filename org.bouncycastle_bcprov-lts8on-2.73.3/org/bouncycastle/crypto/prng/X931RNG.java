/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.prng.EntropySource;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class X931RNG {
    private static final long BLOCK64_RESEED_MAX = 32768L;
    private static final long BLOCK128_RESEED_MAX = 0x800000L;
    private static final int BLOCK64_MAX_BITS_REQUEST = 4096;
    private static final int BLOCK128_MAX_BITS_REQUEST = 262144;
    private final BlockCipher engine;
    private final EntropySource entropySource;
    private final byte[] DT;
    private final byte[] I;
    private final byte[] R;
    private byte[] V;
    private long reseedCounter = 1L;

    public X931RNG(BlockCipher engine, byte[] dateTimeVector, EntropySource entropySource) {
        this.engine = engine;
        this.entropySource = entropySource;
        this.DT = new byte[engine.getBlockSize()];
        System.arraycopy(dateTimeVector, 0, this.DT, 0, this.DT.length);
        this.I = new byte[engine.getBlockSize()];
        this.R = new byte[engine.getBlockSize()];
    }

    int generate(byte[] output, boolean predictionResistant) {
        if (this.R.length == 8) {
            if (this.reseedCounter > 32768L) {
                return -1;
            }
            if (X931RNG.isTooLarge(output, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        } else {
            if (this.reseedCounter > 0x800000L) {
                return -1;
            }
            if (X931RNG.isTooLarge(output, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (predictionResistant || this.V == null) {
            this.V = this.entropySource.getEntropy();
            if (this.V.length != this.engine.getBlockSize()) {
                throw new IllegalStateException("Insufficient entropy returned");
            }
        }
        int m = output.length / this.R.length;
        for (int i = 0; i < m; ++i) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            this.process(this.R, this.I, this.V);
            this.process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, output, i * this.R.length, this.R.length);
            this.increment(this.DT);
        }
        int bytesToCopy = output.length - m * this.R.length;
        if (bytesToCopy > 0) {
            this.engine.processBlock(this.DT, 0, this.I, 0);
            this.process(this.R, this.I, this.V);
            this.process(this.V, this.R, this.I);
            System.arraycopy(this.R, 0, output, m * this.R.length, bytesToCopy);
            this.increment(this.DT);
        }
        ++this.reseedCounter;
        return output.length * 8;
    }

    void reseed() {
        this.V = this.entropySource.getEntropy();
        if (this.V.length != this.engine.getBlockSize()) {
            throw new IllegalStateException("Insufficient entropy returned");
        }
        this.reseedCounter = 1L;
    }

    EntropySource getEntropySource() {
        return this.entropySource;
    }

    private void process(byte[] res, byte[] a, byte[] b) {
        for (int i = 0; i != res.length; ++i) {
            res[i] = (byte)(a[i] ^ b[i]);
        }
        this.engine.processBlock(res, 0, res, 0);
    }

    private void increment(byte[] val) {
        int i = val.length - 1;
        while (i >= 0) {
            int n = i--;
            val[n] = (byte)(val[n] + 1);
            if (val[n] != 0) break;
        }
    }

    private static boolean isTooLarge(byte[] bytes, int maxBytes) {
        return bytes != null && bytes.length > maxBytes;
    }
}

