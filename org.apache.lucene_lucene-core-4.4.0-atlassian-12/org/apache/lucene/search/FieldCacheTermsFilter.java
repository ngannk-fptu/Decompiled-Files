/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

public class FieldCacheTermsFilter
extends Filter {
    private String field;
    private BytesRef[] terms;

    public FieldCacheTermsFilter(String field, BytesRef ... terms) {
        this.field = field;
        this.terms = terms;
    }

    public FieldCacheTermsFilter(String field, String ... terms) {
        this.field = field;
        this.terms = new BytesRef[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            this.terms[i] = new BytesRef(terms[i]);
        }
    }

    public FieldCache getFieldCache() {
        return FieldCache.DEFAULT;
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        final SortedDocValues fcsi = this.getFieldCache().getTermsIndex(context.reader(), this.field);
        final FixedBitSet bits = new FixedBitSet(fcsi.getValueCount());
        for (int i = 0; i < this.terms.length; ++i) {
            int ord = fcsi.lookupTerm(this.terms[i]);
            if (ord < 0) continue;
            bits.set(ord);
        }
        return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs){

            @Override
            protected final boolean matchDoc(int doc) {
                int ord = fcsi.getOrd(doc);
                if (ord == -1) {
                    return false;
                }
                return bits.get(ord);
            }
        };
    }
}

