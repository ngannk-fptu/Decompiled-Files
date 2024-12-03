/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface EntropySource {
    public boolean isPredictionResistant();

    public byte[] getEntropy();

    public int entropySize();
}

