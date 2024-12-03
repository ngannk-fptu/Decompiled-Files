/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.prng.EntropySource;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class EntropyUtil {
    public static byte[] generateSeed(EntropySource entropySource, int numBytes) {
        byte[] bytes = new byte[numBytes];
        if (numBytes * 8 <= entropySource.entropySize()) {
            byte[] ent = entropySource.getEntropy();
            System.arraycopy(ent, 0, bytes, 0, bytes.length);
        } else {
            int entSize = entropySource.entropySize() / 8;
            for (int i = 0; i < bytes.length; i += entSize) {
                byte[] ent = entropySource.getEntropy();
                if (ent.length <= bytes.length - i) {
                    System.arraycopy(ent, 0, bytes, i, ent.length);
                    continue;
                }
                System.arraycopy(ent, 0, bytes, i, bytes.length - i);
            }
        }
        return bytes;
    }
}

