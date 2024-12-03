/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ByteArrayComparator;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ExpUtil;
import org.bouncycastle.tsp.ers.IndexedHash;
import org.bouncycastle.tsp.ers.SortedHashList;
import org.bouncycastle.tsp.ers.SortedIndexedHashList;
import org.bouncycastle.util.io.Streams;

class ERSUtil {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();

    private ERSUtil() {
    }

    static byte[] calculateDigest(DigestCalculator digCalc, byte[] data) {
        try {
            OutputStream mdOut = digCalc.getOutputStream();
            mdOut.write(data);
            mdOut.close();
            return digCalc.getDigest();
        }
        catch (IOException e) {
            throw ExpUtil.createIllegalState("unable to calculate hash: " + e.getMessage(), e);
        }
    }

    static byte[] calculateBranchHash(DigestCalculator digCalc, byte[] a, byte[] b) {
        if (hashComp.compare(a, b) <= 0) {
            return ERSUtil.calculateDigest(digCalc, a, b);
        }
        return ERSUtil.calculateDigest(digCalc, b, a);
    }

    static byte[] calculateBranchHash(DigestCalculator digCalc, byte[][] values) {
        if (values.length == 2) {
            return ERSUtil.calculateBranchHash(digCalc, values[0], values[1]);
        }
        return ERSUtil.calculateDigest(digCalc, ERSUtil.buildIndexedHashList(values).iterator());
    }

    static byte[] calculateDigest(DigestCalculator digCalc, byte[] a, byte[] b) {
        try {
            OutputStream mdOut = digCalc.getOutputStream();
            mdOut.write(a);
            mdOut.write(b);
            mdOut.close();
            return digCalc.getDigest();
        }
        catch (IOException e) {
            throw ExpUtil.createIllegalState("unable to calculate hash: " + e.getMessage(), e);
        }
    }

    static byte[] calculateDigest(DigestCalculator digCalc, Iterator<byte[]> dataGroup) {
        try {
            OutputStream mdOut = digCalc.getOutputStream();
            while (dataGroup.hasNext()) {
                mdOut.write(dataGroup.next());
            }
            mdOut.close();
            return digCalc.getDigest();
        }
        catch (IOException e) {
            throw ExpUtil.createIllegalState("unable to calculate hash: " + e.getMessage(), e);
        }
    }

    static byte[] calculateDigest(DigestCalculator digCalc, InputStream inStream) {
        try {
            OutputStream mdOut = digCalc.getOutputStream();
            Streams.pipeAll((InputStream)inStream, (OutputStream)mdOut);
            mdOut.close();
            return digCalc.getDigest();
        }
        catch (IOException e) {
            throw ExpUtil.createIllegalState("unable to calculate hash: " + e.getMessage(), e);
        }
    }

    static byte[] computeNodeHash(DigestCalculator digCalc, PartialHashtree node) {
        byte[][] values = node.getValues();
        if (values.length > 1) {
            return ERSUtil.calculateDigest(digCalc, ERSUtil.buildIndexedHashList(values).iterator());
        }
        return values[0];
    }

    static List<byte[]> buildIndexedHashList(byte[][] values) {
        SortedHashList hashes = new SortedHashList();
        for (int i = 0; i != values.length; ++i) {
            hashes.add(values[i]);
        }
        return hashes.toList();
    }

    static List<byte[]> buildHashList(DigestCalculator digCalc, List<ERSData> dataObjects, byte[] previousChainHash) {
        SortedHashList hashes = new SortedHashList();
        for (int i = 0; i != dataObjects.size(); ++i) {
            hashes.add(dataObjects.get(i).getHash(digCalc, previousChainHash));
        }
        return hashes.toList();
    }

    static List<IndexedHash> buildIndexedHashList(DigestCalculator digCalc, List<ERSData> dataObjects, byte[] previousChainsHash) {
        SortedIndexedHashList hashes = new SortedIndexedHashList();
        for (int i = 0; i != dataObjects.size(); ++i) {
            byte[] hash = dataObjects.get(i).getHash(digCalc, previousChainsHash);
            hashes.add(new IndexedHash(i, hash));
        }
        return hashes.toList();
    }

    static byte[] concatPreviousHashes(DigestCalculator digCalc, byte[] chainHash, byte[] dataHash) {
        if (chainHash == null) {
            return dataHash;
        }
        try {
            OutputStream digOut = digCalc.getOutputStream();
            digOut.write(dataHash);
            digOut.write(chainHash);
            digOut.close();
            return digCalc.getDigest();
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to hash data");
        }
    }
}

