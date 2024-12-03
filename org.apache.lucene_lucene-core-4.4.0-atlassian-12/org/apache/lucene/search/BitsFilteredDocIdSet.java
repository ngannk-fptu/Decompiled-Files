/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FilteredDocIdSet;
import org.apache.lucene.util.Bits;

public final class BitsFilteredDocIdSet
extends FilteredDocIdSet {
    private final Bits acceptDocs;

    public static DocIdSet wrap(DocIdSet set, Bits acceptDocs) {
        return set == null || acceptDocs == null ? set : new BitsFilteredDocIdSet(set, acceptDocs);
    }

    public BitsFilteredDocIdSet(DocIdSet innerSet, Bits acceptDocs) {
        super(innerSet);
        if (acceptDocs == null) {
            throw new NullPointerException("acceptDocs is null");
        }
        this.acceptDocs = acceptDocs;
    }

    @Override
    protected boolean match(int docid) {
        return this.acceptDocs.get(docid);
    }
}

