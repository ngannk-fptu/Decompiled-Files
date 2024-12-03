/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.BufferedDeletes;
import com.atlassian.lucene36.index.BufferedDeletesStream;
import com.atlassian.lucene36.index.PrefixCodedTerms;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.util.Iterator;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class FrozenBufferedDeletes {
    static final int BYTES_PER_DEL_QUERY = RamUsageEstimator.NUM_BYTES_OBJECT_REF + 4 + 24;
    final PrefixCodedTerms terms;
    int termCount;
    final Query[] queries;
    final int[] queryLimits;
    final int bytesUsed;
    final int numTermDeletes;
    final long gen;

    public FrozenBufferedDeletes(BufferedDeletes deletes, long gen) {
        Comparable[] termsArray = deletes.terms.keySet().toArray(new Term[deletes.terms.size()]);
        this.termCount = termsArray.length;
        ArrayUtil.mergeSort((Comparable[])termsArray);
        PrefixCodedTerms.Builder builder = new PrefixCodedTerms.Builder();
        for (Comparable term : termsArray) {
            builder.add((Term)term);
        }
        this.terms = builder.finish();
        this.queries = new Query[deletes.queries.size()];
        this.queryLimits = new int[deletes.queries.size()];
        int upto = 0;
        for (Map.Entry<Query, Integer> ent : deletes.queries.entrySet()) {
            this.queries[upto] = ent.getKey();
            this.queryLimits[upto] = ent.getValue();
            ++upto;
        }
        this.bytesUsed = (int)this.terms.getSizeInBytes() + this.queries.length * BYTES_PER_DEL_QUERY;
        this.numTermDeletes = deletes.numTermDeletes.get();
        this.gen = gen;
    }

    public Iterable<Term> termsIterable() {
        return new Iterable<Term>(){

            @Override
            public Iterator<Term> iterator() {
                return FrozenBufferedDeletes.this.terms.iterator();
            }
        };
    }

    public Iterable<BufferedDeletesStream.QueryAndLimit> queriesIterable() {
        return new Iterable<BufferedDeletesStream.QueryAndLimit>(){

            @Override
            public Iterator<BufferedDeletesStream.QueryAndLimit> iterator() {
                return new Iterator<BufferedDeletesStream.QueryAndLimit>(){
                    private int upto;

                    @Override
                    public boolean hasNext() {
                        return this.upto < FrozenBufferedDeletes.this.queries.length;
                    }

                    @Override
                    public BufferedDeletesStream.QueryAndLimit next() {
                        BufferedDeletesStream.QueryAndLimit ret = new BufferedDeletesStream.QueryAndLimit(FrozenBufferedDeletes.this.queries[this.upto], FrozenBufferedDeletes.this.queryLimits[this.upto]);
                        ++this.upto;
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public String toString() {
        String s = "";
        if (this.numTermDeletes != 0) {
            s = s + " " + this.numTermDeletes + " deleted terms (unique count=" + this.termCount + ")";
        }
        if (this.queries.length != 0) {
            s = s + " " + this.queries.length + " deleted queries";
        }
        if (this.bytesUsed != 0) {
            s = s + " bytesUsed=" + this.bytesUsed;
        }
        return s;
    }

    boolean any() {
        return this.termCount > 0 || this.queries.length > 0;
    }
}

