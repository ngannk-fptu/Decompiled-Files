/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bouncycastle.tsp.ers.ByteArrayComparator;

public class SortedHashList {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
    private final LinkedList<byte[]> baseList = new LinkedList();

    public byte[] getFirst() {
        return this.baseList.getFirst();
    }

    public void add(byte[] hash) {
        if (this.baseList.size() == 0) {
            this.baseList.addFirst(hash);
        } else if (hashComp.compare(hash, this.baseList.get(0)) < 0) {
            this.baseList.addFirst(hash);
        } else {
            int index;
            for (index = 1; index < this.baseList.size() && hashComp.compare(this.baseList.get(index), hash) <= 0; ++index) {
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

    public List<byte[]> toList() {
        return new ArrayList<byte[]>(this.baseList);
    }
}

