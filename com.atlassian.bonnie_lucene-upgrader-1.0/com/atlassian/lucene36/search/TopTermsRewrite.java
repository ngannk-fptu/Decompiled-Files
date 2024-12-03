/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.TermCollectingRewrite;
import java.io.IOException;
import java.util.PriorityQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TopTermsRewrite<Q extends Query>
extends TermCollectingRewrite<Q> {
    private final int size;

    public TopTermsRewrite(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    protected abstract int getMaxSize();

    public Q rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        final int maxSize = Math.min(this.size, this.getMaxSize());
        final PriorityQueue stQueue = new PriorityQueue();
        this.collectTerms(reader, query, new TermCollectingRewrite.TermCollector(){
            private ScoreTerm st = new ScoreTerm();

            public boolean collect(Term t, float boost) {
                if (stQueue.size() >= maxSize && boost <= ((ScoreTerm)stQueue.peek()).boost) {
                    return true;
                }
                this.st.term = t;
                this.st.boost = boost;
                stQueue.offer(this.st);
                this.st = stQueue.size() > maxSize ? (ScoreTerm)stQueue.poll() : new ScoreTerm();
                return true;
            }
        });
        Object q = this.getTopLevelQuery();
        for (ScoreTerm st : stQueue) {
            this.addClause(q, st.term, query.getBoost() * st.boost);
        }
        query.incTotalNumberOfTerms(stQueue.size());
        return q;
    }

    public int hashCode() {
        return 31 * this.size;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TopTermsRewrite other = (TopTermsRewrite)obj;
        return this.size == other.size;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ScoreTerm
    implements Comparable<ScoreTerm> {
        public Term term;
        public float boost;

        private ScoreTerm() {
        }

        @Override
        public int compareTo(ScoreTerm other) {
            if (this.boost == other.boost) {
                return other.term.compareTo(this.term);
            }
            return Float.compare(this.boost, other.boost);
        }
    }
}

