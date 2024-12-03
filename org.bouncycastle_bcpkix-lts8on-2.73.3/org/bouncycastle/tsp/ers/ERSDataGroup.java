/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSDataGroup
extends ERSCachingData {
    protected List<ERSData> dataObjects;

    public ERSDataGroup(ERSData ... dataObjects) {
        this.dataObjects = new ArrayList<ERSData>(dataObjects.length);
        this.dataObjects.addAll(Arrays.asList(dataObjects));
    }

    public ERSDataGroup(List<ERSData> dataObjects) {
        this.dataObjects = new ArrayList<ERSData>(dataObjects.size());
        this.dataObjects.addAll(dataObjects);
    }

    public ERSDataGroup(ERSData dataObject) {
        this.dataObjects = Collections.singletonList(dataObject);
    }

    public List<byte[]> getHashes(DigestCalculator digestCalculator, byte[] previousChainHash) {
        return ERSUtil.buildHashList(digestCalculator, this.dataObjects, previousChainHash);
    }

    @Override
    public byte[] getHash(DigestCalculator digestCalculator, byte[] previousChainHash) {
        List<byte[]> hashes = this.getHashes(digestCalculator, previousChainHash);
        if (hashes.size() > 1) {
            return ERSUtil.calculateDigest(digestCalculator, hashes.iterator());
        }
        return hashes.get(0);
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] previousChainHash) {
        List<byte[]> hashes = this.getHashes(digestCalculator, previousChainHash);
        if (hashes.size() > 1) {
            ArrayList<byte[]> dHashes = new ArrayList<byte[]>(hashes.size());
            for (int i = 0; i != dHashes.size(); ++i) {
                dHashes.add(hashes.get(i));
            }
            return ERSUtil.calculateDigest(digestCalculator, dHashes.iterator());
        }
        return hashes.get(0);
    }

    public int size() {
        return this.dataObjects.size();
    }
}

