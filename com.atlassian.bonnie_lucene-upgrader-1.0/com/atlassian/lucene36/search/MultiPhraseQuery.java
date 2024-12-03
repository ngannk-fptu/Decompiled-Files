/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.MultipleTermPositions;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.ExactPhraseScorer;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.PhraseQuery;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiPhraseQuery
extends Query {
    private String field;
    private ArrayList<Term[]> termArrays = new ArrayList();
    private ArrayList<Integer> positions = new ArrayList();
    private int slop = 0;

    public void setSlop(int s) {
        this.slop = s;
    }

    public int getSlop() {
        return this.slop;
    }

    public void add(Term term) {
        this.add(new Term[]{term});
    }

    public void add(Term[] terms) {
        int position = 0;
        if (this.positions.size() > 0) {
            position = this.positions.get(this.positions.size() - 1) + 1;
        }
        this.add(terms, position);
    }

    public void add(Term[] terms, int position) {
        if (this.termArrays.size() == 0) {
            this.field = terms[0].field();
        }
        for (int i = 0; i < terms.length; ++i) {
            if (terms[i].field() == this.field) continue;
            throw new IllegalArgumentException("All phrase terms must be in the same field (" + this.field + "): " + terms[i]);
        }
        this.termArrays.add(terms);
        this.positions.add(position);
    }

    public List<Term[]> getTermArrays() {
        return Collections.unmodifiableList(this.termArrays);
    }

    public int[] getPositions() {
        int[] result = new int[this.positions.size()];
        for (int i = 0; i < this.positions.size(); ++i) {
            result[i] = this.positions.get(i);
        }
        return result;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        for (Term[] arr : this.termArrays) {
            for (Term term : arr) {
                terms.add(term);
            }
        }
    }

    @Override
    public Query rewrite(IndexReader reader) {
        if (this.termArrays.size() == 1) {
            Term[] terms = this.termArrays.get(0);
            BooleanQuery boq = new BooleanQuery(true);
            for (int i = 0; i < terms.length; ++i) {
                boq.add(new TermQuery(terms[i]), BooleanClause.Occur.SHOULD);
            }
            boq.setBoost(this.getBoost());
            return boq;
        }
        return this;
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        return new MultiPhraseWeight(searcher);
    }

    @Override
    public final String toString(String f) {
        StringBuilder buffer = new StringBuilder();
        if (this.field == null || !this.field.equals(f)) {
            buffer.append(this.field);
            buffer.append(":");
        }
        buffer.append("\"");
        int lastPos = -1;
        boolean first = true;
        for (int i = 0; i < this.termArrays.size(); ++i) {
            int j;
            Term[] terms = this.termArrays.get(i);
            int position = this.positions.get(i);
            if (first) {
                first = false;
            } else {
                buffer.append(" ");
                for (j = 1; j < position - lastPos; ++j) {
                    buffer.append("? ");
                }
            }
            if (terms.length > 1) {
                buffer.append("(");
                for (j = 0; j < terms.length; ++j) {
                    buffer.append(terms[j].text());
                    if (j >= terms.length - 1) continue;
                    buffer.append(" ");
                }
                buffer.append(")");
            } else {
                buffer.append(terms[0].text());
            }
            lastPos = position;
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
        if (!(o instanceof MultiPhraseQuery)) {
            return false;
        }
        MultiPhraseQuery other = (MultiPhraseQuery)o;
        return this.getBoost() == other.getBoost() && this.slop == other.slop && this.termArraysEquals(this.termArrays, other.termArrays) && this.positions.equals(other.positions);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.slop ^ this.termArraysHashCode() ^ this.positions.hashCode() ^ 0x4AC65113;
    }

    private int termArraysHashCode() {
        int hashCode = 1;
        for (Object[] objectArray : this.termArrays) {
            hashCode = 31 * hashCode + (objectArray == null ? 0 : Arrays.hashCode(objectArray));
        }
        return hashCode;
    }

    private boolean termArraysEquals(List<Term[]> termArrays1, List<Term[]> termArrays2) {
        if (termArrays1.size() != termArrays2.size()) {
            return false;
        }
        ListIterator<Term[]> iterator1 = termArrays1.listIterator();
        ListIterator<Term[]> iterator2 = termArrays2.listIterator();
        while (iterator1.hasNext()) {
            Object[] termArray1 = iterator1.next();
            Object[] termArray2 = iterator2.next();
            if (termArray1 != null ? Arrays.equals(termArray1, termArray2) : termArray2 == null) continue;
            return false;
        }
        return true;
    }

    private class MultiPhraseWeight
    extends Weight {
        private Similarity similarity;
        private float value;
        private final Explanation.IDFExplanation idfExp;
        private float idf;
        private float queryNorm;
        private float queryWeight;

        public MultiPhraseWeight(Searcher searcher) throws IOException {
            this.similarity = MultiPhraseQuery.this.getSimilarity(searcher);
            ArrayList<Term> allTerms = new ArrayList<Term>();
            Iterator i$ = MultiPhraseQuery.this.termArrays.iterator();
            while (i$.hasNext()) {
                Term[] terms;
                for (Term term : terms = (Term[])i$.next()) {
                    allTerms.add(term);
                }
            }
            this.idfExp = this.similarity.idfExplain(allTerms, searcher);
            this.idf = this.idfExp.getIdf();
        }

        public Query getQuery() {
            return MultiPhraseQuery.this;
        }

        public float getValue() {
            return this.value;
        }

        public float sumOfSquaredWeights() {
            this.queryWeight = this.idf * MultiPhraseQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float queryNorm) {
            this.queryNorm = queryNorm;
            this.queryWeight *= queryNorm;
            this.value = this.queryWeight * this.idf;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            if (MultiPhraseQuery.this.termArrays.size() == 0) {
                return null;
            }
            Comparable[] postingsFreqs = new PhraseQuery.PostingsAndFreq[MultiPhraseQuery.this.termArrays.size()];
            for (int pos = 0; pos < postingsFreqs.length; ++pos) {
                int docFreq;
                TermPositions p;
                Term[] terms = (Term[])MultiPhraseQuery.this.termArrays.get(pos);
                if (terms.length > 1) {
                    p = new MultipleTermPositions(reader, terms);
                    docFreq = 0;
                    for (int termIdx = 0; termIdx < terms.length; ++termIdx) {
                        docFreq += reader.docFreq(terms[termIdx]);
                    }
                } else {
                    p = reader.termPositions(terms[0]);
                    docFreq = reader.docFreq(terms[0]);
                    if (p == null) {
                        return null;
                    }
                }
                postingsFreqs[pos] = new PhraseQuery.PostingsAndFreq(p, docFreq, (Integer)MultiPhraseQuery.this.positions.get(pos), terms);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                ArrayUtil.mergeSort((Comparable[])postingsFreqs);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                ExactPhraseScorer s = new ExactPhraseScorer(this, (PhraseQuery.PostingsAndFreq[])postingsFreqs, this.similarity, reader.norms(MultiPhraseQuery.this.field));
                if (s.noDocs) {
                    return null;
                }
                return s;
            }
            return new SloppyPhraseScorer(this, (PhraseQuery.PostingsAndFreq[])postingsFreqs, this.similarity, MultiPhraseQuery.this.slop, reader.norms(MultiPhraseQuery.this.field));
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            ComplexExplanation result = new ComplexExplanation();
            result.setDescription("weight(" + this.getQuery() + " in " + doc + "), product of:");
            Explanation idfExpl = new Explanation(this.idf, "idf(" + MultiPhraseQuery.this.field + ":" + this.idfExp.explain() + ")");
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + this.getQuery() + "), product of:");
            Explanation boostExpl = new Explanation(MultiPhraseQuery.this.getBoost(), "boost");
            if (MultiPhraseQuery.this.getBoost() != 1.0f) {
                queryExpl.addDetail(boostExpl);
            }
            queryExpl.addDetail(idfExpl);
            Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
            queryExpl.addDetail(queryNormExpl);
            queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
            result.addDetail(queryExpl);
            ComplexExplanation fieldExpl = new ComplexExplanation();
            fieldExpl.setDescription("fieldWeight(" + this.getQuery() + " in " + doc + "), product of:");
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
            byte[] fieldNorms = reader.norms(MultiPhraseQuery.this.field);
            float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field=" + MultiPhraseQuery.this.field + ", doc=" + doc + ")");
            fieldExpl.addDetail(fieldNormExpl);
            fieldExpl.setMatch(tfExplanation.isMatch());
            fieldExpl.setValue(tfExplanation.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
            result.addDetail(fieldExpl);
            result.setMatch(fieldExpl.getMatch());
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());
            if (queryExpl.getValue() == 1.0f) {
                return fieldExpl;
            }
            return result;
        }
    }
}

