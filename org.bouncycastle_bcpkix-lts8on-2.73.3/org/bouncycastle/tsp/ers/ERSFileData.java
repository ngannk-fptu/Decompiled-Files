/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSFileData
extends ERSCachingData {
    private final File content;

    public ERSFileData(File content) throws FileNotFoundException {
        if (content.isDirectory()) {
            throw new IllegalArgumentException("directory not allowed as ERSFileData");
        }
        if (!content.exists()) {
            throw new FileNotFoundException(content.getAbsolutePath() + " does not exist");
        }
        if (!content.canRead()) {
            throw new FileNotFoundException(content.getAbsolutePath() + " is not readable");
        }
        this.content = content;
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] previousChainHash) {
        try {
            FileInputStream contentStream = new FileInputStream(this.content);
            byte[] hash = ERSUtil.calculateDigest(digestCalculator, contentStream);
            ((InputStream)contentStream).close();
            if (previousChainHash != null) {
                return ERSUtil.concatPreviousHashes(digestCalculator, previousChainHash, hash);
            }
            return hash;
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to process " + this.content.getAbsolutePath());
        }
    }
}

