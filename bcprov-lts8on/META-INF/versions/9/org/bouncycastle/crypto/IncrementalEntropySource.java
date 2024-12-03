/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.prng.EntropySource;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
interface IncrementalEntropySource
extends EntropySource {
    public byte[] getEntropy(long var1) throws InterruptedException;
}

