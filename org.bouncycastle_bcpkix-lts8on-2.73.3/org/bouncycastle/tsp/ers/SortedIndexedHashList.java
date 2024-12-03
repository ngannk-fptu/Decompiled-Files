/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bouncycastle.tsp.ers.ByteArrayComparator;
import org.bouncycastle.tsp.ers.IndexedHash;

public class SortedIndexedHashList {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
    private final LinkedList<IndexedHash> baseList = new LinkedList();

    public IndexedHash getFirst() {
        return this.baseList.getFirst();
    }

    public void add(IndexedHash hash) {
        if (this.baseList.size() == 0) {
            this.baseList.addFirst(hash);
        } else if (hashComp.compare(hash.digest, this.baseList.get((int)0).digest) < 0) {
            this.baseList.addFirst(hash);
        } else {
            int index;
            for (index = 1; index < this.baseList.size() && hashComp.compare(this.baseList.get((int)index).digest, hash.digest) <= 0; ++index) {
            }
            if (index == this.baseList.size()) {
                this.baseList.add(hash);
            } else {
                this.baseList.add(index, hash);
            }
        }
    }

    public int size() {
        return this.baseList.size();
    }

    public List<IndexedHash> toList() {
        return new ArrayList<IndexedHash>(this.baseList);
    }
}

