/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.util.FixedBitSet;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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

    @Deprecated
    public int getTotalNumberOfTerms() {
        return ((MultiTermQuery)this.query).getTotalNumberOfTerms();
    }

    @Deprecated
    public void clearTotalNumberOfTerms() {
        ((MultiTermQuery)this.query).clearTotalNumberOfTerms();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        FixedBitSet fixedBitSet;
        FilteredTermEnum enumerator;
        block8: {
            enumerator = ((MultiTermQuery)this.query).getEnum(reader);
            if (((TermEnum)enumerator).term() != null) break block8;
            DocIdSet docIdSet = DocIdSet.EMPTY_DOCIDSET;
            Object var14_5 = null;
            ((TermEnum)enumerator).close();
            return docIdSet;
        }
        try {
            FixedBitSet bitSet = new FixedBitSet(reader.maxDoc());
            int[] docs = new int[32];
            int[] freqs = new int[32];
            TermDocs termDocs = reader.termDocs();
            try {
                Term term;
                int termCount = 0;
                while ((term = ((TermEnum)enumerator).term()) != null) {
                    int count;
                    ++termCount;
                    termDocs.seek(term);
                    while ((count = termDocs.read(docs, freqs)) != 0) {
                        for (int i = 0; i < count; ++i) {
                            bitSet.set(docs[i]);
                        }
                    }
                    if (((TermEnum)enumerator).next()) continue;
                }
                ((MultiTermQuery)this.query).incTotalNumberOfTerms(termCount);
                Object var12_16 = null;
            }
            catch (Throwable throwable) {
                Object var12_17 = null;
                termDocs.close();
                throw throwable;
            }
            termDocs.close();
            fixedBitSet = bitSet;
            Object var14_6 = null;
        }
        catch (Throwable throwable) {
            Object var14_7 = null;
            ((TermEnum)enumerator).close();
            throw throwable;
        }
        ((TermEnum)enumerator).close();
        return fixedBitSet;
    }
}

