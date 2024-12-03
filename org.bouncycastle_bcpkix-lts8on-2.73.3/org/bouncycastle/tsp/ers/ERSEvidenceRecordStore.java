/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStamp
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.StoreException
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.tsp.ers.ERSEvidenceRecordSelector;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class ERSEvidenceRecordStore
implements Store<ERSEvidenceRecord> {
    private Map<HashNode, List<ERSEvidenceRecord>> recordMap = new HashMap<HashNode, List<ERSEvidenceRecord>>();
    private DigestCalculator digCalc = null;

    public ERSEvidenceRecordStore(Collection<ERSEvidenceRecord> records) throws OperatorCreationException {
        for (ERSEvidenceRecord record : records) {
            PartialHashtree dataLeaf;
            ArchiveTimeStamp archiveTimeStamp = record.getArchiveTimeStamps()[0];
            if (this.digCalc == null) {
                DigestCalculatorProvider digProv = record.getDigestAlgorithmProvider();
                this.digCalc = digProv.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
            }
            if ((dataLeaf = archiveTimeStamp.getHashTreeLeaf()) != null) {
                byte[][] dataHashes = dataLeaf.getValues();
                if (dataHashes.length > 1) {
                    for (int i = 0; i != dataHashes.length; ++i) {
                        this.addRecord(new HashNode(dataHashes[i]), record);
                    }
                    this.addRecord(new HashNode(ERSUtil.computeNodeHash(this.digCalc, dataLeaf)), record);
                    continue;
                }
                this.addRecord(new HashNode(dataHashes[0]), record);
                continue;
            }
            this.addRecord(new HashNode(archiveTimeStamp.getTimeStampDigestValue()), record);
        }
    }

    private void addRecord(HashNode hashNode, ERSEvidenceRecord record) {
        List<ERSEvidenceRecord> recs = this.recordMap.get(hashNode);
        if (recs != null) {
            ArrayList<ERSEvidenceRecord> newRecs = new ArrayList<ERSEvidenceRecord>(recs.size() + 1);
            newRecs.addAll(recs);
            newRecs.add(record);
            this.recordMap.put(hashNode, newRecs);
        } else {
            this.recordMap.put(hashNode, Collections.singletonList(record));
        }
    }

    public Collection<ERSEvidenceRecord> getMatches(Selector<ERSEvidenceRecord> selector) throws StoreException {
        if (selector instanceof ERSEvidenceRecordSelector) {
            HashNode node = new HashNode(((ERSEvidenceRecordSelector)selector).getData().getHash(this.digCalc, null));
            List<ERSEvidenceRecord> records = this.recordMap.get(node);
            if (records != null) {
                ArrayList<ERSEvidenceRecord> rv = new ArrayList<ERSEvidenceRecord>(records.size());
                for (int i = 0; i != records.size(); ++i) {
                    ERSEvidenceRecord record = records.get(i);
                    if (!selector.match((Object)record)) continue;
                    rv.add(record);
                }
                return Collections.unmodifiableList(rv);
            }
            return Collections.emptyList();
        }
        if (selector == null) {
            HashSet<ERSEvidenceRecord> rv = new HashSet<ERSEvidenceRecord>(this.recordMap.size());
            Iterator<List<ERSEvidenceRecord>> it = this.recordMap.values().iterator();
            while (it.hasNext()) {
                rv.addAll(it.next());
            }
            return Collections.unmodifiableList(new ArrayList(rv));
        }
        HashSet<ERSEvidenceRecord> rv = new HashSet<ERSEvidenceRecord>();
        for (List<ERSEvidenceRecord> next : this.recordMap.values()) {
            for (int i = 0; i != next.size(); ++i) {
                if (!selector.match((Object)next.get(i))) continue;
                rv.add(next.get(i));
            }
        }
        return Collections.unmodifiableList(new ArrayList(rv));
    }

    private static class HashNode {
        private final byte[] dataHash;
        private final int hashCode;

        public HashNode(byte[] dataHash) {
            this.dataHash = dataHash;
            this.hashCode = Arrays.hashCode((byte[])dataHash);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            if (o instanceof HashNode) {
                return Arrays.areEqual((byte[])this.dataHash, (byte[])((HashNode)o).dataHash);
            }
            return false;
        }
    }
}

