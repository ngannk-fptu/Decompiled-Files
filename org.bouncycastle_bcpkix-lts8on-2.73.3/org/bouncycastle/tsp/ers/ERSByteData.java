/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSByteData
extends ERSCachingData {
    private final byte[] content;

    public ERSByteData(byte[] content) {
        this.content = content;
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] previousChainHash) {
        byte[] hash = ERSUtil.calculateDigest(digestCalculator, this.content);
        if (previousChainHash != null) {
            return ERSUtil.concatPreviousHashes(digestCalculator, previousChainHash, hash);
        }
        return hash;
    }
}

