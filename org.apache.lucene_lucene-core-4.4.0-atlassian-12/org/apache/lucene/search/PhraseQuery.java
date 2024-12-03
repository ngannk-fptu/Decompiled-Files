/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.ExactPhraseScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SloppyPhraseScorer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

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
        } else if (!term.field().equals(this.field)) {
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
        if (this.terms.isEmpty()) {
            BooleanQuery bq = new BooleanQuery();
            bq.setBoost(this.getBoost());
            return bq;
        }
        if (this.terms.size() == 1) {
            TermQuery tq = new TermQuery(this.terms.get(0));
            tq.setBoost(this.getBoost());
            return tq;
        }
        return super.rewrite(reader);
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
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
        private final Similarity.SimWeight stats;
        private transient TermContext[] states;

        public PhraseWeight(IndexSearcher searcher) throws IOException {
            this.similarity = searcher.getSimilarity();
            IndexReaderContext context = searcher.getTopReaderContext();
            this.states = new TermContext[PhraseQuery.this.terms.size()];
            TermStatistics[] termStats = new TermStatistics[PhraseQuery.this.terms.size()];
            for (int i = 0; i < PhraseQuery.this.terms.size(); ++i) {
                Term term = (Term)PhraseQuery.this.terms.get(i);
                this.states[i] = TermContext.build(context, term, true);
                termStats[i] = searcher.termStatistics(term, this.states[i]);
            }
            this.stats = this.similarity.computeWeight(PhraseQuery.this.getBoost(), searcher.collectionStatistics(PhraseQuery.this.field), termStats);
        }

        public String toString() {
            return "weight(" + PhraseQuery.this + ")";
        }

        @Override
        public Query getQuery() {
            return PhraseQuery.this;
        }

        @Override
        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.stats.normalize(queryNorm, topLevelBoost);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            assert (!PhraseQuery.this.terms.isEmpty());
            AtomicReader reader = context.reader();
            Bits liveDocs = acceptDocs;
            Comparable[] postingsFreqs = new PostingsAndFreq[PhraseQuery.this.terms.size()];
            Terms fieldTerms = reader.terms(PhraseQuery.this.field);
            if (fieldTerms == null) {
                return null;
            }
            TermsEnum te = fieldTerms.iterator(null);
            for (int i = 0; i < PhraseQuery.this.terms.size(); ++i) {
                Term t = (Term)PhraseQuery.this.terms.get(i);
                TermState state = this.states[i].get(context.ord);
                if (state == null) {
                    assert (this.termNotInReader(reader, t)) : "no termstate found but term exists in reader";
                    return null;
                }
                te.seekExact(t.bytes(), state);
                DocsAndPositionsEnum postingsEnum = te.docsAndPositions(liveDocs, null, 0);
                if (postingsEnum == null) {
                    assert (te.seekExact(t.bytes(), false)) : "termstate found but no term exists in reader";
                    throw new IllegalStateException("field \"" + t.field() + "\" was indexed without position data; cannot run PhraseQuery (term=" + t.text() + ")");
                }
                postingsFreqs[i] = new PostingsAndFreq(postingsEnum, te.docFreq(), (Integer)PhraseQuery.this.positions.get(i), t);
            }
            if (PhraseQuery.this.slop == 0) {
                ArrayUtil.timSort((Comparable[])postingsFreqs);
            }
            if (PhraseQuery.this.slop == 0) {
                ExactPhraseScorer s = new ExactPhraseScorer(this, (PostingsAndFreq[])postingsFreqs, this.similarity.simScorer(this.stats, context));
                if (s.noDocs) {
                    return null;
                }
                return s;
            }
            return new SloppyPhraseScorer(this, (PostingsAndFreq[])postingsFreqs, PhraseQuery.this.slop, this.similarity.simScorer(this.stats, context));
        }

        private boolean termNotInReader(AtomicReader reader, Term term) throws IOException {
            return reader.docFreq(term) == 0;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            int newDoc;
            Scorer scorer = this.scorer(context, true, false, context.reader().getLiveDocs());
            if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
                float freq = PhraseQuery.this.slop == 0 ? (float)scorer.freq() : ((SloppyPhraseScorer)scorer).sloppyFreq();
                Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                ComplexExplanation result = new ComplexExplanation();
                result.setDescription("weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:");
                Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "phraseFreq=" + freq));
                result.addDetail(scoreExplanation);
                result.setValue(scoreExplanation.getValue());
                result.setMatch(true);
                return result;
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }
    }

    static class PostingsAndFreq
    implements Comparable<PostingsAndFreq> {
        final DocsAndPositionsEnum postings;
        final int docFreq;
        final int position;
        final Term[] terms;
        final int nTerms;

        public PostingsAndFreq(DocsAndPositionsEnum postings, int docFreq, int position, Term ... terms) {
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

