/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.util.OpenBitSet;
import java.io.IOException;

public class OpenBitSetDISI
extends OpenBitSet {
    public OpenBitSetDISI(DocIdSetIterator disi, int maxSize) throws IOException {
        super(maxSize);
        this.inPlaceOr(disi);
    }

    public OpenBitSetDISI(int maxSize) {
        super(maxSize);
    }

    public void inPlaceOr(DocIdSetIterator disi) throws IOException {
        int doc;
        long size = this.size();
        while ((long)(doc = disi.nextDoc()) < size) {
            this.fastSet(doc);
        }
    }

    public void inPlaceAnd(DocIdSetIterator disi) throws IOException {
        int disiDoc;
        int bitSetDoc = this.nextSetBit(0);
        while (bitSetDoc != -1 && (disiDoc = disi.advance(bitSetDoc)) != Integer.MAX_VALUE) {
            this.clear(bitSetDoc, disiDoc);
            bitSetDoc = this.nextSetBit(disiDoc + 1);
        }
        if (bitSetDoc != -1) {
            this.clear((long)bitSetDoc, this.size());
        }
    }

    public void inPlaceNot(DocIdSetIterator disi) throws IOException {
        int doc;
        long size = this.size();
        while ((long)(doc = disi.nextDoc()) < size) {
            this.fastClear(doc);
        }
    }

    public void inPlaceXor(DocIdSetIterator disi) throws IOException {
        int doc;
        long size = this.size();
        while ((long)(doc = disi.nextDoc()) < size) {
            this.fastFlip(doc);
        }
    }
}

