/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.DisjunctionMaxScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;

public class DisjunctionMaxQuery
extends Query
implements Iterable<Query> {
    private ArrayList<Query> disjuncts = new ArrayList();
    private float tieBreakerMultiplier = 0.0f;

    public DisjunctionMaxQuery(float tieBreakerMultiplier) {
        this.tieBreakerMultiplier = tieBreakerMultiplier;
    }

    public DisjunctionMaxQuery(Collection<Query> disjuncts, float tieBreakerMultiplier) {
        this.tieBreakerMultiplier = tieBreakerMultiplier;
        this.add(disjuncts);
    }

    public void add(Query query) {
        this.disjuncts.add(query);
    }

    public void add(Collection<Query> disjuncts) {
        this.disjuncts.addAll(disjuncts);
    }

    @Override
    public Iterator<Query> iterator() {
        return this.disjuncts.iterator();
    }

    public ArrayList<Query> getDisjuncts() {
        return this.disjuncts;
    }

    public float getTieBreakerMultiplier() {
        return this.tieBreakerMultiplier;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new DisjunctionMaxWeight(searcher);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        int numDisjunctions = this.disjuncts.size();
        if (numDisjunctions == 1) {
            Query singleton = this.disjuncts.get(0);
            Query result = singleton.rewrite(reader);
            if (this.getBoost() != 1.0f) {
                if (result == singleton) {
                    result = result.clone();
                }
                result.setBoost(this.getBoost() * result.getBoost());
            }
            return result;
        }
        DisjunctionMaxQuery clone = null;
        for (int i = 0; i < numDisjunctions; ++i) {
            Query clause = this.disjuncts.get(i);
            Query rewrite = clause.rewrite(reader);
            if (rewrite == clause) continue;
            if (clone == null) {
                clone = this.clone();
            }
            clone.disjuncts.set(i, rewrite);
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    @Override
    public DisjunctionMaxQuery clone() {
        DisjunctionMaxQuery clone = (DisjunctionMaxQuery)super.clone();
        clone.disjuncts = (ArrayList)this.disjuncts.clone();
        return clone;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        for (Query query : this.disjuncts) {
            query.extractTerms(terms);
        }
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(");
        int numDisjunctions = this.disjuncts.size();
        for (int i = 0; i < numDisjunctions; ++i) {
            Query subquery = this.disjuncts.get(i);
            if (subquery instanceof BooleanQuery) {
                buffer.append("(");
                buffer.append(subquery.toString(field));
                buffer.append(")");
            } else {
                buffer.append(subquery.toString(field));
            }
            if (i == numDisjunctions - 1) continue;
            buffer.append(" | ");
        }
        buffer.append(")");
        if (this.tieBreakerMultiplier != 0.0f) {
            buffer.append("~");
            buffer.append(this.tieBreakerMultiplier);
        }
        if ((double)this.getBoost() != 1.0) {
            buffer.append("^");
            buffer.append(this.getBoost());
        }
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DisjunctionMaxQuery)) {
            return false;
        }
        DisjunctionMaxQuery other = (DisjunctionMaxQuery)o;
        return this.getBoost() == other.getBoost() && this.tieBreakerMultiplier == other.tieBreakerMultiplier && this.disjuncts.equals(other.disjuncts);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) + Float.floatToIntBits(this.tieBreakerMultiplier) + this.disjuncts.hashCode();
    }

    protected class DisjunctionMaxWeight
    extends Weight {
        protected ArrayList<Weight> weights = new ArrayList();

        public DisjunctionMaxWeight(IndexSearcher searcher) throws IOException {
            for (Query disjunctQuery : DisjunctionMaxQuery.this.disjuncts) {
                this.weights.add(disjunctQuery.createWeight(searcher));
            }
        }

        @Override
        public Query getQuery() {
            return DisjunctionMaxQuery.this;
        }

        @Override
        public float getValueForNormalization() throws IOException {
            float max = 0.0f;
            float sum = 0.0f;
            for (Weight currentWeight : this.weights) {
                float sub = currentWeight.getValueForNormalization();
                sum += sub;
                max = Math.max(max, sub);
            }
            float boost = DisjunctionMaxQuery.this.getBoost();
            return ((sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier * DisjunctionMaxQuery.this.tieBreakerMultiplier + max) * boost * boost;
        }

        @Override
        public void normalize(float norm, float topLevelBoost) {
            topLevelBoost *= DisjunctionMaxQuery.this.getBoost();
            for (Weight wt : this.weights) {
                wt.normalize(norm, topLevelBoost);
            }
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            Scorer[] scorers = new Scorer[this.weights.size()];
            int idx = 0;
            for (Weight w : this.weights) {
                Scorer subScorer = w.scorer(context, true, false, acceptDocs);
                if (subScorer == null) continue;
                scorers[idx++] = subScorer;
            }
            if (idx == 0) {
                return null;
            }
            DisjunctionMaxScorer result = new DisjunctionMaxScorer(this, DisjunctionMaxQuery.this.tieBreakerMultiplier, scorers, idx);
            return result;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            if (DisjunctionMaxQuery.this.disjuncts.size() == 1) {
                return this.weights.get(0).explain(context, doc);
            }
            ComplexExplanation result = new ComplexExplanation();
            float max = 0.0f;
            float sum = 0.0f;
            result.setDescription(DisjunctionMaxQuery.this.tieBreakerMultiplier == 0.0f ? "max of:" : "max plus " + DisjunctionMaxQuery.this.tieBreakerMultiplier + " times others of:");
            for (Weight wt : this.weights) {
                Explanation e = wt.explain(context, doc);
                if (!e.isMatch()) continue;
                result.setMatch(Boolean.TRUE);
                result.addDetail(e);
                sum += e.getValue();
                max = Math.max(max, e.getValue());
            }
            result.setValue(max + (sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier);
            return result;
        }
    }
}

