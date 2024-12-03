/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSInputStreamData
extends ERSCachingData {
    private final InputStream content;

    public ERSInputStreamData(File content) throws FileNotFoundException {
        if (content.isDirectory()) {
            throw new IllegalArgumentException("directory not allowed");
        }
        this.content = new FileInputStream(content);
    }

    public ERSInputStreamData(InputStream content) {
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

