/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BytesRef;

public class SingletonSortedSetDocValues
extends SortedSetDocValues {
    private final SortedDocValues in;
    private int docID;
    private boolean set;

    public SingletonSortedSetDocValues(SortedDocValues in) {
        this.in = in;
    }

    @Override
    public long nextOrd() {
        if (this.set) {
            return -1L;
        }
        this.set = true;
        return this.in.getOrd(this.docID);
    }

    @Override
    public void setDocument(int docID) {
        this.docID = docID;
        this.set = false;
    }

    @Override
    public void lookupOrd(long ord, BytesRef result) {
        this.in.lookupOrd((int)ord, result);
    }

    @Override
    public long getValueCount() {
        return this.in.getValueCount();
    }

    @Override
    public long lookupTerm(BytesRef key) {
        return this.in.lookupTerm(key);
    }
}

