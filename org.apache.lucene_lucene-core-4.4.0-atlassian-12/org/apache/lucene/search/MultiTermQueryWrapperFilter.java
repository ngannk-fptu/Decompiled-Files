/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;

public class MultiTermQueryWrapperFilter<Q extends MultiTermQuery>
extends Filter {
    protected final Q query;

    protected MultiTermQueryWrapperFilter(Q query) {
        this.query = query;
    }

    public String toString() {
        return ((Query)this.query).toString();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass().equals(o.getClass())) {
            return ((MultiTermQuery)this.query).equals(((MultiTermQueryWrapperFilter)o).query);
        }
        return false;
    }

    public final int hashCode() {
        return ((MultiTermQuery)this.query).hashCode();
    }

    public final String getField() {
        return ((MultiTermQuery)this.query).getField();
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        AtomicReader reader = context.reader();
        Fields fields = reader.fields();
        if (fields == null) {
            return null;
        }
        Terms terms = fields.terms(((MultiTermQuery)this.query).field);
        if (terms == null) {
            return null;
        }
        TermsEnum termsEnum = ((MultiTermQuery)this.query).getTermsEnum(terms);
        assert (termsEnum != null);
        if (termsEnum.next() != null) {
            FixedBitSet bitSet = new FixedBitSet(context.reader().maxDoc());
            DocsEnum docsEnum = null;
            do {
                int docid;
                docsEnum = termsEnum.docs(acceptDocs, docsEnum, 0);
                while ((docid = docsEnum.nextDoc()) != Integer.MAX_VALUE) {
                    bitSet.set(docid);
                }
            } while (termsEnum.next() != null);
            return bitSet;
        }
        return null;
    }
}

