/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.RamUsageEstimator;

class BufferedDeletes {
    static final int BYTES_PER_DEL_TERM = 9 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 7 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 40;
    static final int BYTES_PER_DEL_DOCID = 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4;
    static final int BYTES_PER_DEL_QUERY = 5 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + 24;
    final AtomicInteger numTermDeletes = new AtomicInteger();
    final Map<Term, Integer> terms = new HashMap<Term, Integer>();
    final Map<Query, Integer> queries = new HashMap<Query, Integer>();
    final List<Integer> docIDs = new ArrayList<Integer>();
    public static final Integer MAX_INT = Integer.MAX_VALUE;
    final AtomicLong bytesUsed;
    private static final boolean VERBOSE_DELETES = false;
    long gen;

    public BufferedDeletes() {
        this(new AtomicLong());
    }

    BufferedDeletes(AtomicLong bytesUsed) {
        assert (bytesUsed != null);
        this.bytesUsed = bytesUsed;
    }

    public String toString() {
        String s = "gen=" + this.gen;
        if (this.numTermDeletes.get() != 0) {
            s = s + " " + this.numTermDeletes.get() + " deleted terms (unique count=" + this.terms.size() + ")";
        }
        if (this.queries.size() != 0) {
            s = s + " " + this.queries.size() + " deleted queries";
        }
        if (this.docIDs.size() != 0) {
            s = s + " " + this.docIDs.size() + " deleted docIDs";
        }
        if (this.bytesUsed.get() != 0L) {
            s = s + " bytesUsed=" + this.bytesUsed.get();
        }
        return s;
    }

    public void addQuery(Query query, int docIDUpto) {
        Integer current = this.queries.put(query, docIDUpto);
        if (current == null) {
            this.bytesUsed.addAndGet(BYTES_PER_DEL_QUERY);
        }
    }

    public void addDocID(int docID) {
        this.docIDs.add(docID);
        this.bytesUsed.addAndGet(BYTES_PER_DEL_DOCID);
    }

    public void addTerm(Term term, int docIDUpto) {
        Integer current = this.terms.get(term);
        if (current != null && docIDUpto < current) {
            return;
        }
        this.terms.put(term, docIDUpto);
        this.numTermDeletes.incrementAndGet();
        if (current == null) {
            this.bytesUsed.addAndGet(BYTES_PER_DEL_TERM + term.bytes.length + 2 * term.field().length());
        }
    }

    void clear() {
        this.terms.clear();
        this.queries.clear();
        this.docIDs.clear();
        this.numTermDeletes.set(0);
        this.bytesUsed.set(0L);
    }

    void clearDocIDs() {
        this.bytesUsed.addAndGet(-this.docIDs.size() * BYTES_PER_DEL_DOCID);
        this.docIDs.clear();
    }

    boolean any() {
        return this.terms.size() > 0 || this.docIDs.size() > 0 || this.queries.size() > 0;
    }
}

