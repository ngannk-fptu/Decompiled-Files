/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.FieldCacheDocIdSet;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.util.FixedBitSet;
import java.io.IOException;

public class FieldCacheTermsFilter
extends Filter {
    private String field;
    private String[] terms;

    public FieldCacheTermsFilter(String field, String ... terms) {
        this.field = field;
        this.terms = terms;
    }

    public FieldCache getFieldCache() {
        return FieldCache.DEFAULT;
    }

    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        final FieldCache.StringIndex fcsi = this.getFieldCache().getStringIndex(reader, this.field);
        final FixedBitSet bits = new FixedBitSet(fcsi.lookup.length);
        for (int i = 0; i < this.terms.length; ++i) {
            int termNumber = fcsi.binarySearchLookup(this.terms[i]);
            if (termNumber <= 0) continue;
            bits.set(termNumber);
        }
        return new FieldCacheDocIdSet(reader){

            protected final boolean matchDoc(int doc) {
                return bits.get(fcsi.order[doc]);
            }
        };
    }
}

