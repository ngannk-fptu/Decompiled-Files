/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.ExactPhraseScorer;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.SloppyPhraseScorer;
import com.atlassian.lucene36.search.TermQuery;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PhraseQuery
extends Query {
    private String field;
    private ArrayList<Term> terms = new ArrayList(4);
    private ArrayList<Integer> positions = new ArrayList(4);
    private int maxPosition = 0;
    private int slop = 0;

    public void setSlop(int s) {
        this.slop = s;
    }

    public int getSlop() {
        return this.slop;
    }

    public void add(Term term) {
        int position = 0;
        if (this.positions.size() > 0) {
            position = this.positions.get(this.positions.size() - 1) + 1;
        }
        this.add(term, position);
    }

    public void add(Term term, int position) {
        if (this.terms.size() == 0) {
            this.field = term.field();
        } else if (term.field() != this.field) {
            throw new IllegalArgumentException("All phrase terms must be in the same field: " + term);
        }
        this.terms.add(term);
        this.positions.add(position);
        if (position > this.maxPosition) {
            this.maxPosition = position;
        }
    }

    public Term[] getTerms() {
        return this.terms.toArray(new Term[0]);
    }

    public int[] getPositions() {
        int[] result = new int[this.positions.size()];
        for (int i = 0; i < this.positions.size(); ++i) {
            result[i] = this.positions.get(i);
        }
        return result;
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        if (this.terms.size() == 1) {
            TermQuery tq = new TermQuery(this.terms.get(0));
            tq.setBoost(this.getBoost());
            return tq;
        }
        return super.rewrite(reader);
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        if (this.terms.size() == 1) {
            Term term = this.terms.get(0);
            TermQuery termQuery = new TermQuery(term);
            termQuery.setBoost(this.getBoost());
            return ((Query)termQuery).createWeight(searcher);
        }
        return new PhraseWeight(searcher);
    }

    @Override
    public void extractTerms(Set<Term> queryTerms) {
        queryTerms.addAll(this.terms);
    }

    @Override
    public String toString(String f) {
        int i;
        StringBuilder buffer = new StringBuilder();
        if (this.field != null && !this.field.equals(f)) {
            buffer.append(this.field);
            buffer.append(":");
        }
        buffer.append("\"");
        String[] pieces = new String[this.maxPosition + 1];
        for (i = 0; i < this.terms.size(); ++i) {
            int pos = this.positions.get(i);
            String s = pieces[pos];
            s = s == null ? this.terms.get(i).text() : s + "|" + this.terms.get(i).text();
            pieces[pos] = s;
        }
        for (i = 0; i < pieces.length; ++i) {
            String s;
            if (i > 0) {
                buffer.append(' ');
            }
            if ((s = pieces[i]) == null) {
                buffer.append('?');
                continue;
            }
            buffer.append(s);
        }
        buffer.append("\"");
        if (this.slop != 0) {
            buffer.append("~");
            buffer.append(this.slop);
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PhraseQuery)) {
            return false;
        }
        PhraseQuery other = (PhraseQuery)o;
        return this.getBoost() == other.getBoost() && this.slop == other.slop && this.terms.equals(other.terms) && this.positions.equals(other.positions);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.slop ^ this.terms.hashCode() ^ this.positions.hashCode();
    }

    private class PhraseWeight
    extends Weight {
        private final Similarity similarity;
        private float value;
        private float idf;
        private float queryNorm;
        private float queryWeight;
        private Explanation.IDFExplanation idfExp;

        public PhraseWeight(Searcher searcher) throws IOException {
            this.similarity = PhraseQuery.this.getSimilarity(searcher);
            this.idfExp = this.similarity.idfExplain(PhraseQuery.this.terms, searcher);
            this.idf = this.idfExp.getIdf();
        }

        public String toString() {
            return "weight(" + PhraseQuery.this + ")";
        }

        public Query getQuery() {
            return PhraseQuery.this;
        }

        public float getValue() {
            return this.value;
        }

        public float sumOfSquaredWeights() {
            this.queryWeight = this.idf * PhraseQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float queryNorm) {
            this.queryNorm = queryNorm;
            this.queryWeight *= queryNorm;
            this.value = this.queryWeight * this.idf;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            if (PhraseQuery.this.terms.size() == 0) {
                return null;
            }
            Comparable[] postingsFreqs = new PostingsAndFreq[PhraseQuery.this.terms.size()];
            for (int i = 0; i < PhraseQuery.this.terms.size(); ++i) {
                Term t = (Term)PhraseQuery.this.terms.get(i);
                TermPositions p = reader.termPositions(t);
                if (p == null) {
                    return null;
                }
                postingsFreqs[i] = new PostingsAndFreq(p, reader.docFreq(t), (Integer)PhraseQuery.this.positions.get(i), t);
            }
            if (PhraseQuery.this.slop == 0) {
                ArrayUtil.mergeSort((Comparable[])postingsFreqs);
            }
            if (PhraseQuery.this.slop == 0) {
                ExactPhraseScorer s = new ExactPhraseScorer(this, (PostingsAndFreq[])postingsFreqs, this.similarity, reader.norms(PhraseQuery.this.field));
                if (s.noDocs) {
                    return null;
                }
                return s;
            }
            return new SloppyPhraseScorer(this, (PostingsAndFreq[])postingsFreqs, this.similarity, PhraseQuery.this.slop, reader.norms(PhraseQuery.this.field));
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            ComplexExplanation result = new ComplexExplanation();
            result.setDescription("weight(" + this.getQuery() + " in " + doc + "), product of:");
            StringBuilder docFreqs = new StringBuilder();
            StringBuilder query = new StringBuilder();
            query.append('\"');
            docFreqs.append(this.idfExp.explain());
            for (int i = 0; i < PhraseQuery.this.terms.size(); ++i) {
                if (i != 0) {
                    query.append(" ");
                }
                Term term = (Term)PhraseQuery.this.terms.get(i);
                query.append(term.text());
            }
            query.append('\"');
            Explanation idfExpl = new Explanation(this.idf, "idf(" + PhraseQuery.this.field + ":" + docFreqs + ")");
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + this.getQuery() + "), product of:");
            Explanation boostExpl = new Explanation(PhraseQuery.this.getBoost(), "boost");
            if (PhraseQuery.this.getBoost() != 1.0f) {
                queryExpl.addDetail(boostExpl);
            }
            queryExpl.addDetail(idfExpl);
            Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
            queryExpl.addDetail(queryNormExpl);
            queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
            result.addDetail(queryExpl);
            Explanation fieldExpl = new Explanation();
            fieldExpl.setDescription("fieldWeight(" + PhraseQuery.this.field + ":" + query + " in " + doc + "), product of:");
            Scorer scorer = this.scorer(reader, true, false);
            if (scorer == null) {
                return new Explanation(0.0f, "no matching docs");
            }
            Explanation tfExplanation = new Explanation();
            int d = scorer.advance(doc);
            float phraseFreq = d == doc ? scorer.freq() : 0.0f;
            tfExplanation.setValue(this.similarity.tf(phraseFreq));
            tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");
            fieldExpl.addDetail(tfExplanation);
            fieldExpl.addDetail(idfExpl);
            Explanation fieldNormExpl = new Explanation();
            byte[] fieldNorms = reader.norms(PhraseQuery.this.field);
            float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field=" + PhraseQuery.this.field + ", doc=" + doc + ")");
            fieldExpl.addDetail(fieldNormExpl);
            fieldExpl.setValue(tfExplanation.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
            result.addDetail(fieldExpl);
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());
            result.setMatch(tfExplanation.isMatch());
            return result;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class PostingsAndFreq
    implements Comparable<PostingsAndFreq> {
        final TermPositions postings;
        final int docFreq;
        final int position;
        final Term[] terms;
        final int nTerms;

        public PostingsAndFreq(TermPositions postings, int docFreq, int position, Term ... terms) {
            this.postings = postings;
            this.docFreq = docFreq;
            this.position = position;
            int n = this.nTerms = terms == null ? 0 : terms.length;
            if (this.nTerms > 0) {
                if (terms.length == 1) {
                    this.terms = terms;
                } else {
                    Object[] terms2 = new Term[terms.length];
                    System.arraycopy(terms, 0, terms2, 0, terms.length);
                    Arrays.sort(terms2);
                    this.terms = terms2;
                }
            } else {
                this.terms = null;
            }
        }

        @Override
        public int compareTo(PostingsAndFreq other) {
            if (this.docFreq != other.docFreq) {
                return this.docFreq - other.docFreq;
            }
            if (this.position != other.position) {
                return this.position - other.position;
            }
            if (this.nTerms != other.nTerms) {
                return this.nTerms - other.nTerms;
            }
            if (this.nTerms == 0) {
                return 0;
            }
            for (int i = 0; i < this.terms.length; ++i) {
                int res = this.terms[i].compareTo(other.terms[i]);
                if (res == 0) continue;
                return res;
            }
            return 0;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.docFreq;
            result = 31 * result + this.position;
            for (int i = 0; i < this.nTerms; ++i) {
                result = 31 * result + this.terms[i].hashCode();
            }
            return result;
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
            PostingsAndFreq other = (PostingsAndFreq)obj;
            if (this.docFreq != other.docFreq) {
                return false;
            }
            if (this.position != other.position) {
                return false;
            }
            if (this.terms == null) {
                return other.terms == null;
            }
            return Arrays.equals(this.terms, other.terms);
        }
    }
}

