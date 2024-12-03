/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanScorer;
import com.atlassian.lucene36.search.BooleanScorer2;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BooleanQuery
extends Query
implements Iterable<BooleanClause> {
    private static int maxClauseCount = 1024;
    private ArrayList<BooleanClause> clauses = new ArrayList();
    private final boolean disableCoord;
    protected int minNrShouldMatch = 0;

    public static int getMaxClauseCount() {
        return maxClauseCount;
    }

    public static void setMaxClauseCount(int maxClauseCount) {
        if (maxClauseCount < 1) {
            throw new IllegalArgumentException("maxClauseCount must be >= 1");
        }
        BooleanQuery.maxClauseCount = maxClauseCount;
    }

    public BooleanQuery() {
        this.disableCoord = false;
    }

    public BooleanQuery(boolean disableCoord) {
        this.disableCoord = disableCoord;
    }

    public boolean isCoordDisabled() {
        return this.disableCoord;
    }

    public void setMinimumNumberShouldMatch(int min) {
        this.minNrShouldMatch = min;
    }

    public int getMinimumNumberShouldMatch() {
        return this.minNrShouldMatch;
    }

    public void add(Query query, BooleanClause.Occur occur) {
        this.add(new BooleanClause(query, occur));
    }

    public void add(BooleanClause clause) {
        if (this.clauses.size() >= maxClauseCount) {
            throw new TooManyClauses();
        }
        this.clauses.add(clause);
    }

    public BooleanClause[] getClauses() {
        return this.clauses.toArray(new BooleanClause[this.clauses.size()]);
    }

    public List<BooleanClause> clauses() {
        return this.clauses;
    }

    @Override
    public final Iterator<BooleanClause> iterator() {
        return this.clauses().iterator();
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        return new BooleanWeight(searcher, this.disableCoord);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        BooleanClause c;
        if (this.minNrShouldMatch == 0 && this.clauses.size() == 1 && !(c = this.clauses.get(0)).isProhibited()) {
            Query query = c.getQuery().rewrite(reader);
            if (this.getBoost() != 1.0f) {
                if (query == c.getQuery()) {
                    query = (Query)query.clone();
                }
                query.setBoost(this.getBoost() * query.getBoost());
            }
            return query;
        }
        BooleanQuery clone = null;
        for (int i = 0; i < this.clauses.size(); ++i) {
            BooleanClause c2 = this.clauses.get(i);
            Query query = c2.getQuery().rewrite(reader);
            if (query == c2.getQuery()) continue;
            if (clone == null) {
                clone = (BooleanQuery)this.clone();
            }
            clone.clauses.set(i, new BooleanClause(query, c2.getOccur()));
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        for (BooleanClause clause : this.clauses) {
            clause.getQuery().extractTerms(terms);
        }
    }

    @Override
    public Object clone() {
        BooleanQuery clone = (BooleanQuery)super.clone();
        clone.clauses = (ArrayList)this.clauses.clone();
        return clone;
    }

    @Override
    public String toString(String field) {
        boolean needParens;
        StringBuilder buffer = new StringBuilder();
        boolean bl = needParens = (double)this.getBoost() != 1.0 || this.getMinimumNumberShouldMatch() > 0;
        if (needParens) {
            buffer.append("(");
        }
        for (int i = 0; i < this.clauses.size(); ++i) {
            BooleanClause c = this.clauses.get(i);
            if (c.isProhibited()) {
                buffer.append("-");
            } else if (c.isRequired()) {
                buffer.append("+");
            }
            Query subQuery = c.getQuery();
            if (subQuery != null) {
                if (subQuery instanceof BooleanQuery) {
                    buffer.append("(");
                    buffer.append(subQuery.toString(field));
                    buffer.append(")");
                } else {
                    buffer.append(subQuery.toString(field));
                }
            } else {
                buffer.append("null");
            }
            if (i == this.clauses.size() - 1) continue;
            buffer.append(" ");
        }
        if (needParens) {
            buffer.append(")");
        }
        if (this.getMinimumNumberShouldMatch() > 0) {
            buffer.append('~');
            buffer.append(this.getMinimumNumberShouldMatch());
        }
        if (this.getBoost() != 1.0f) {
            buffer.append(ToStringUtils.boost(this.getBoost()));
        }
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanQuery)) {
            return false;
        }
        BooleanQuery other = (BooleanQuery)o;
        return this.getBoost() == other.getBoost() && this.clauses.equals(other.clauses) && this.getMinimumNumberShouldMatch() == other.getMinimumNumberShouldMatch() && this.disableCoord == other.disableCoord;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.clauses.hashCode() + this.getMinimumNumberShouldMatch() + (this.disableCoord ? 17 : 0);
    }

    protected class BooleanWeight
    extends Weight {
        protected Similarity similarity;
        protected ArrayList<Weight> weights;
        protected int maxCoord;
        private final boolean disableCoord;

        public BooleanWeight(Searcher searcher, boolean disableCoord) throws IOException {
            this.similarity = BooleanQuery.this.getSimilarity(searcher);
            this.disableCoord = disableCoord;
            this.weights = new ArrayList(BooleanQuery.this.clauses.size());
            for (int i = 0; i < BooleanQuery.this.clauses.size(); ++i) {
                BooleanClause c = (BooleanClause)BooleanQuery.this.clauses.get(i);
                this.weights.add(c.getQuery().createWeight(searcher));
                if (c.isProhibited()) continue;
                ++this.maxCoord;
            }
        }

        public Query getQuery() {
            return BooleanQuery.this;
        }

        public float getValue() {
            return BooleanQuery.this.getBoost();
        }

        public float sumOfSquaredWeights() throws IOException {
            float sum = 0.0f;
            for (int i = 0; i < this.weights.size(); ++i) {
                float s = this.weights.get(i).sumOfSquaredWeights();
                if (((BooleanClause)BooleanQuery.this.clauses.get(i)).isProhibited()) continue;
                sum += s;
            }
            return sum *= BooleanQuery.this.getBoost() * BooleanQuery.this.getBoost();
        }

        float coord(int overlap, int maxOverlap) {
            return maxOverlap == 1 ? 1.0f : this.similarity.coord(overlap, maxOverlap);
        }

        public void normalize(float norm) {
            norm *= BooleanQuery.this.getBoost();
            for (Weight w : this.weights) {
                w.normalize(norm);
            }
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            float coordFactor;
            int minShouldMatch = BooleanQuery.this.getMinimumNumberShouldMatch();
            ComplexExplanation sumExpl = new ComplexExplanation();
            sumExpl.setDescription("sum of:");
            int coord = 0;
            float sum = 0.0f;
            boolean fail = false;
            int shouldMatchCount = 0;
            Iterator cIter = BooleanQuery.this.clauses.iterator();
            for (Weight w : this.weights) {
                Explanation r;
                BooleanClause c = (BooleanClause)cIter.next();
                if (w.scorer(reader, true, true) == null) {
                    if (!c.isRequired()) continue;
                    fail = true;
                    Explanation r2 = new Explanation(0.0f, "no match on required clause (" + c.getQuery().toString() + ")");
                    sumExpl.addDetail(r2);
                    continue;
                }
                Explanation e = w.explain(reader, doc);
                if (e.isMatch()) {
                    if (!c.isProhibited()) {
                        sumExpl.addDetail(e);
                        sum += e.getValue();
                        ++coord;
                    } else {
                        r = new Explanation(0.0f, "match on prohibited clause (" + c.getQuery().toString() + ")");
                        r.addDetail(e);
                        sumExpl.addDetail(r);
                        fail = true;
                    }
                    if (c.getOccur() != BooleanClause.Occur.SHOULD) continue;
                    ++shouldMatchCount;
                    continue;
                }
                if (!c.isRequired()) continue;
                r = new Explanation(0.0f, "no match on required clause (" + c.getQuery().toString() + ")");
                r.addDetail(e);
                sumExpl.addDetail(r);
                fail = true;
            }
            if (fail) {
                sumExpl.setMatch(Boolean.FALSE);
                sumExpl.setValue(0.0f);
                sumExpl.setDescription("Failure to meet condition(s) of required/prohibited clause(s)");
                return sumExpl;
            }
            if (shouldMatchCount < minShouldMatch) {
                sumExpl.setMatch(Boolean.FALSE);
                sumExpl.setValue(0.0f);
                sumExpl.setDescription("Failure to match minimum number of optional clauses: " + minShouldMatch);
                return sumExpl;
            }
            sumExpl.setMatch(0 < coord ? Boolean.TRUE : Boolean.FALSE);
            sumExpl.setValue(sum);
            float f = coordFactor = this.disableCoord ? 1.0f : this.coord(coord, this.maxCoord);
            if (coordFactor == 1.0f) {
                return sumExpl;
            }
            ComplexExplanation result = new ComplexExplanation(sumExpl.isMatch(), sum * coordFactor, "product of:");
            result.addDetail(sumExpl);
            result.addDetail(new Explanation(coordFactor, "coord(" + coord + "/" + this.maxCoord + ")"));
            return result;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            ArrayList<Scorer> required = new ArrayList<Scorer>();
            ArrayList<Scorer> prohibited = new ArrayList<Scorer>();
            ArrayList<Scorer> optional = new ArrayList<Scorer>();
            Iterator cIter = BooleanQuery.this.clauses.iterator();
            for (Weight w : this.weights) {
                BooleanClause c = (BooleanClause)cIter.next();
                Scorer subScorer = w.scorer(reader, true, false);
                if (subScorer == null) {
                    if (!c.isRequired()) continue;
                    return null;
                }
                if (c.isRequired()) {
                    required.add(subScorer);
                    continue;
                }
                if (c.isProhibited()) {
                    prohibited.add(subScorer);
                    continue;
                }
                optional.add(subScorer);
            }
            if (!scoreDocsInOrder && topScorer && required.size() == 0) {
                return new BooleanScorer(this, this.disableCoord, this.similarity, BooleanQuery.this.minNrShouldMatch, optional, prohibited, this.maxCoord);
            }
            if (required.size() == 0 && optional.size() == 0) {
                return null;
            }
            if (optional.size() < BooleanQuery.this.minNrShouldMatch) {
                return null;
            }
            return new BooleanScorer2(this, this.disableCoord, this.similarity, BooleanQuery.this.minNrShouldMatch, required, prohibited, optional, this.maxCoord);
        }

        public boolean scoresDocsOutOfOrder() {
            for (BooleanClause c : BooleanQuery.this.clauses) {
                if (!c.isRequired()) continue;
                return false;
            }
            return true;
        }
    }

    public static class TooManyClauses
    extends RuntimeException {
        public TooManyClauses() {
            super("maxClauseCount is set to " + maxClauseCount);
        }
    }
}

