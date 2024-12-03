/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStamp
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 *  org.bouncycastle.asn1.tsp.TSTInfo
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.ers.BinaryTreeRootCalculator;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.tsp.ers.IndexedHash;
import org.bouncycastle.util.Arrays;

public class ERSArchiveTimeStampGenerator {
    private final DigestCalculator digCalc;
    private List<ERSData> dataObjects = new ArrayList<ERSData>();
    private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();
    private byte[] previousChainHash;

    public ERSArchiveTimeStampGenerator(DigestCalculator digCalc) {
        this.digCalc = digCalc;
    }

    public void addData(ERSData dataObject) {
        this.dataObjects.add(dataObject);
    }

    public void addAllData(List<ERSData> dataObjects) {
        this.dataObjects.addAll(dataObjects);
    }

    void addPreviousChains(ArchiveTimeStampSequence archiveTimeStampSequence) throws IOException {
        OutputStream digOut = this.digCalc.getOutputStream();
        digOut.write(archiveTimeStampSequence.getEncoded("DER"));
        digOut.close();
        this.previousChainHash = this.digCalc.getDigest();
    }

    public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator tspReqGenerator) throws TSPException, IOException {
        PartialHashtree[] reducedHashTree = this.getPartialHashtrees();
        byte[] rootHash = this.rootNodeCalculator.computeRootHash(this.digCalc, reducedHashTree);
        return tspReqGenerator.generate(this.digCalc.getAlgorithmIdentifier(), rootHash);
    }

    public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator tspReqGenerator, BigInteger nonce) throws TSPException, IOException {
        PartialHashtree[] reducedHashTree = this.getPartialHashtrees();
        byte[] rootHash = this.rootNodeCalculator.computeRootHash(this.digCalc, reducedHashTree);
        return tspReqGenerator.generate(this.digCalc.getAlgorithmIdentifier(), rootHash, nonce);
    }

    public ERSArchiveTimeStamp generateArchiveTimeStamp(TimeStampResponse tspResponse) throws TSPException, ERSException {
        PartialHashtree[] reducedHashTree = this.getPartialHashtrees();
        if (reducedHashTree.length != 1) {
            throw new ERSException("multiple reduced hash trees found");
        }
        byte[] rootHash = this.rootNodeCalculator.computeRootHash(this.digCalc, reducedHashTree);
        if (tspResponse.getStatus() != 0) {
            throw new TSPException("TSP response error status: " + tspResponse.getStatusString());
        }
        TSTInfo tstInfo = tspResponse.getTimeStampToken().getTimeStampInfo().toASN1Structure();
        if (!tstInfo.getMessageImprint().getHashAlgorithm().equals((Object)this.digCalc.getAlgorithmIdentifier())) {
            throw new ERSException("time stamp imprint for wrong algorithm");
        }
        if (!Arrays.areEqual((byte[])tstInfo.getMessageImprint().getHashedMessage(), (byte[])rootHash)) {
            throw new ERSException("time stamp imprint for wrong root hash");
        }
        if (reducedHashTree[0].getValueCount() == 1) {
            return new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, tspResponse.getTimeStampToken().toCMSSignedData().toASN1Structure()), this.digCalc);
        }
        return new ERSArchiveTimeStamp(new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), reducedHashTree, tspResponse.getTimeStampToken().toCMSSignedData().toASN1Structure()), this.digCalc);
    }

    public List<ERSArchiveTimeStamp> generateArchiveTimeStamps(TimeStampResponse tspResponse) throws TSPException, ERSException {
        PartialHashtree[] reducedHashTree = this.getPartialHashtrees();
        byte[] rootHash = this.rootNodeCalculator.computeRootHash(this.digCalc, reducedHashTree);
        if (tspResponse.getStatus() != 0) {
            throw new TSPException("TSP response error status: " + tspResponse.getStatusString());
        }
        TSTInfo tstInfo = tspResponse.getTimeStampToken().getTimeStampInfo().toASN1Structure();
        if (!tstInfo.getMessageImprint().getHashAlgorithm().equals((Object)this.digCalc.getAlgorithmIdentifier())) {
            throw new ERSException("time stamp imprint for wrong algorithm");
        }
        if (!Arrays.areEqual((byte[])tstInfo.getMessageImprint().getHashedMessage(), (byte[])rootHash)) {
            throw new ERSException("time stamp imprint for wrong root hash");
        }
        ContentInfo timeStamp = tspResponse.getTimeStampToken().toCMSSignedData().toASN1Structure();
        ArrayList<ERSArchiveTimeStamp> atss = new ArrayList<ERSArchiveTimeStamp>();
        if (reducedHashTree.length == 1 && reducedHashTree[0].getValueCount() == 1) {
            atss.add(new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, timeStamp), this.digCalc));
        } else {
            int i;
            ERSArchiveTimeStamp[] archiveTimeStamps = new ERSArchiveTimeStamp[reducedHashTree.length];
            for (i = 0; i != reducedHashTree.length; ++i) {
                PartialHashtree[] path = this.rootNodeCalculator.computePathToRoot(this.digCalc, reducedHashTree[i], i);
                archiveTimeStamps[reducedHashTree[i].order] = new ERSArchiveTimeStamp(new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), path, timeStamp), this.digCalc);
            }
            for (i = 0; i != reducedHashTree.length; ++i) {
                atss.add(archiveTimeStamps[i]);
            }
        }
        return atss;
    }

    private IndexedPartialHashtree[] getPartialHashtrees() {
        int i;
        List<IndexedHash> hashes = ERSUtil.buildIndexedHashList(this.digCalc, this.dataObjects, this.previousChainHash);
        IndexedPartialHashtree[] trees = new IndexedPartialHashtree[hashes.size()];
        HashSet<ERSDataGroup> dataGroupSet = new HashSet<ERSDataGroup>();
        for (i = 0; i != this.dataObjects.size(); ++i) {
            if (!(this.dataObjects.get(i) instanceof ERSDataGroup)) continue;
            dataGroupSet.add((ERSDataGroup)this.dataObjects.get(i));
        }
        for (i = 0; i != hashes.size(); ++i) {
            byte[] hash = hashes.get((int)i).digest;
            ERSData d = this.dataObjects.get(hashes.get((int)i).order);
            if (d instanceof ERSDataGroup) {
                ERSDataGroup data = (ERSDataGroup)d;
                List<byte[]> dHashes = data.getHashes(this.digCalc, this.previousChainHash);
                trees[i] = new IndexedPartialHashtree(hashes.get((int)i).order, (byte[][])dHashes.toArray((T[])new byte[dHashes.size()][]));
                continue;
            }
            trees[i] = new IndexedPartialHashtree(hashes.get((int)i).order, hash);
        }
        return trees;
    }

    private static class IndexedPartialHashtree
    extends PartialHashtree {
        final int order;

        private IndexedPartialHashtree(int order, byte[] partial) {
            super(partial);
            this.order = order;
        }

        private IndexedPartialHashtree(int order, byte[][] partial) {
            super(partial);
            this.order = order;
        }
    }
}

