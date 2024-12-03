/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.Arrays;
import org.bouncycastle.crypto.NativeLoader;
import org.bouncycastle.crypto.prng.EntropySource;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class NativeEntropySource
implements EntropySource {
    private final int size;
    private final int effectiveSize;
    private final boolean useSeedSource;

    public NativeEntropySource(int sizeInBits) {
        if (sizeInBits < 1) {
            throw new IllegalStateException("bit size less than 1");
        }
        this.size = (sizeInBits + 7) / 8;
        if (!NativeLoader.hasNativeService("DRBG") || !NativeLoader.hasNativeService("NRBG")) {
            throw new IllegalStateException("no hardware support for random");
        }
        this.useSeedSource = NativeLoader.hasNativeService("NRBG");
        int mod = this.modulus();
        this.effectiveSize = (this.size + mod - 1) / mod * mod;
    }

    @Override
    public native boolean isPredictionResistant();

    public native int modulus();

    @Override
    public byte[] getEntropy() {
        byte[] buf = new byte[this.effectiveSize];
        this.seedBuffer(buf, this.useSeedSource);
        if (this.areAllZeroes(buf, 0, buf.length)) {
            throw new IllegalStateException("entropy source return array of len " + buf.length + " where all elements are 0");
        }
        if (this.size != this.effectiveSize) {
            return Arrays.copyOfRange(buf, 0, this.size);
        }
        return buf;
    }

    native void seedBuffer(byte[] var1, boolean var2);

    @Override
    public int entropySize() {
        return this.size * 8;
    }

    public boolean areAllZeroes(byte[] buf, int off, int len) {
        int bits = 0;
        for (int i = 0; i < len; ++i) {
            bits |= buf[off + i];
        }
        return bits == 0;
    }
}

