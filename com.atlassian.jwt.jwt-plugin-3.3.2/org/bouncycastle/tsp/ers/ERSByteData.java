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

    public ERSByteData(byte[] byArray) {
        this.content = byArray;
    }

    protected byte[] calculateHash(DigestCalculator digestCalculator) {
        return ERSUtil.calculateDigest(digestCalculator, this.content);
    }
}

