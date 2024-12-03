/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermCollectingRewrite;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;

public abstract class TopTermsRewrite<Q extends Query>
extends TermCollectingRewrite<Q> {
    private final int size;
    private static final Comparator<ScoreTerm> scoreTermSortByTermComp = new Comparator<ScoreTerm>(){

        @Override
        public int compare(ScoreTerm st1, ScoreTerm st2) {
            assert (st1.termComp == st2.termComp) : "term comparator should not change between segments";
            return st1.termComp.compare(st1.bytes, st2.bytes);
        }
    };

    public TopTermsRewrite(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    protected abstract int getMaxSize();

    public final Q rewrite(IndexReader reader, MultiTermQuery query) throws IOException {
        final int maxSize = Math.min(this.size, this.getMaxSize());
        final PriorityQueue stQueue = new PriorityQueue();
        this.collectTerms(reader, query, new TermCollectingRewrite.TermCollector(){
            private final MaxNonCompetitiveBoostAttribute maxBoostAtt;
            private final Map<BytesRef, ScoreTerm> visitedTerms;
            private TermsEnum termsEnum;
            private Comparator<BytesRef> termComp;
            private BoostAttribute boostAtt;
            private ScoreTerm st;
            private BytesRef lastTerm;
            {
                this.maxBoostAtt = this.attributes.addAttribute(MaxNonCompetitiveBoostAttribute.class);
                this.visitedTerms = new HashMap<BytesRef, ScoreTerm>();
            }

            @Override
            public void setNextEnum(TermsEnum termsEnum) {
                this.termsEnum = termsEnum;
                this.termComp = termsEnum.getComparator();
                assert (this.compareToLastTerm(null));
                if (this.st == null) {
                    this.st = new ScoreTerm(this.termComp, new TermContext(this.topReaderContext));
                }
                this.boostAtt = termsEnum.attributes().addAttribute(BoostAttribute.class);
            }

            private boolean compareToLastTerm(BytesRef t) {
                if (this.lastTerm == null && t != null) {
                    this.lastTerm = BytesRef.deepCopyOf(t);
                } else if (t == null) {
                    this.lastTerm = null;
                } else {
                    assert (this.termsEnum.getComparator().compare(this.lastTerm, t) < 0) : "lastTerm=" + this.lastTerm + " t=" + t;
                    this.lastTerm.copyBytes(t);
                }
                return true;
            }

            @Override
            public boolean collect(BytesRef bytes) throws IOException {
                ScoreTerm t;
                float boost = this.boostAtt.getBoost();
                assert (this.compareToLastTerm(bytes));
                if (stQueue.size() == maxSize) {
                    t = (ScoreTerm)stQueue.peek();
                    if (boost < t.boost) {
                        return true;
                    }
                    if (boost == t.boost && this.termComp.compare(bytes, t.bytes) > 0) {
                        return true;
                    }
                }
                t = this.visitedTerms.get(bytes);
                TermState state = this.termsEnum.termState();
                assert (state != null);
                if (t != null) {
                    assert (t.boost == boost) : "boost should be equal in all segment TermsEnums";
                    t.termState.register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                } else {
                    this.st.bytes.copyBytes(bytes);
                    this.st.boost = boost;
                    this.visitedTerms.put(this.st.bytes, this.st);
                    assert (this.st.termState.docFreq() == 0);
                    this.st.termState.register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                    stQueue.offer(this.st);
                    if (stQueue.size() > maxSize) {
                        this.st = (ScoreTerm)stQueue.poll();
                        this.visitedTerms.remove(this.st.bytes);
                        this.st.termState.clear();
                    } else {
                        this.st = new ScoreTerm(this.termComp, new TermContext(this.topReaderContext));
                    }
                    assert (stQueue.size() <= maxSize) : "the PQ size must be limited to maxSize";
                    if (stQueue.size() == maxSize) {
                        t = (ScoreTerm)stQueue.peek();
                        this.maxBoostAtt.setMaxNonCompetitiveBoost(t.boost);
                        this.maxBoostAtt.setCompetitiveTerm(t.bytes);
                    }
                }
                return true;
            }
        });
        Object q = this.getTopLevelQuery();
        ScoreTerm[] scoreTerms = stQueue.toArray(new ScoreTerm[stQueue.size()]);
        ArrayUtil.timSort(scoreTerms, scoreTermSortByTermComp);
        for (ScoreTerm st : scoreTerms) {
            Term term = new Term(query.field, st.bytes);
            assert (reader.docFreq(term) == st.termState.docFreq()) : "reader DF is " + reader.docFreq(term) + " vs " + st.termState.docFreq() + " term=" + term;
            this.addClause(q, term, st.termState.docFreq(), query.getBoost() * st.boost, st.termState);
        }
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

    static final class ScoreTerm
    implements Comparable<ScoreTerm> {
        public final Comparator<BytesRef> termComp;
        public final BytesRef bytes = new BytesRef();
        public float boost;
        public final TermContext termState;

        public ScoreTerm(Comparator<BytesRef> termComp, TermContext termState) {
            this.termComp = termComp;
            this.termState = termState;
        }

        @Override
        public int compareTo(ScoreTerm other) {
            if (this.boost == other.boost) {
                return this.termComp.compare(other.bytes, this.bytes);
            }
            return Float.compare(this.boost, other.boost);
        }
    }
}

