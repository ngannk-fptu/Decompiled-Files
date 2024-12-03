/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.ExactPhraseScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SloppyPhraseScorer;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.UnionDocsAndPositionsEnum;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

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
            if (terms[i].field().equals(this.field)) continue;
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
        if (this.termArrays.isEmpty()) {
            BooleanQuery bq = new BooleanQuery();
            bq.setBoost(this.getBoost());
            return bq;
        }
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
    public Weight createWeight(IndexSearcher searcher) throws IOException {
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
        int k = 0;
        Iterator<Term[]> i = this.termArrays.iterator();
        int lastPos = -1;
        boolean first = true;
        while (i.hasNext()) {
            int j;
            Term[] terms = i.next();
            int position = this.positions.get(k);
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
            ++k;
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
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();

        public MultiPhraseWeight(IndexSearcher searcher) throws IOException {
            this.similarity = searcher.getSimilarity();
            IndexReaderContext context = searcher.getTopReaderContext();
            ArrayList<TermStatistics> allTermStats = new ArrayList<TermStatistics>();
            Iterator iterator = MultiPhraseQuery.this.termArrays.iterator();
            while (iterator.hasNext()) {
                Term[] terms;
                for (Term term : terms = (Term[])iterator.next()) {
                    TermContext termContext = this.termContexts.get(term);
                    if (termContext == null) {
                        termContext = TermContext.build(context, term, true);
                        this.termContexts.put(term, termContext);
                    }
                    allTermStats.add(searcher.termStatistics(term, termContext));
                }
            }
            this.stats = this.similarity.computeWeight(MultiPhraseQuery.this.getBoost(), searcher.collectionStatistics(MultiPhraseQuery.this.field), allTermStats.toArray(new TermStatistics[allTermStats.size()]));
        }

        @Override
        public Query getQuery() {
            return MultiPhraseQuery.this;
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
            assert (!MultiPhraseQuery.this.termArrays.isEmpty());
            AtomicReader reader = context.reader();
            Bits liveDocs = acceptDocs;
            Comparable[] postingsFreqs = new PhraseQuery.PostingsAndFreq[MultiPhraseQuery.this.termArrays.size()];
            Terms fieldTerms = reader.terms(MultiPhraseQuery.this.field);
            if (fieldTerms == null) {
                return null;
            }
            TermsEnum termsEnum = fieldTerms.iterator(null);
            for (int pos = 0; pos < postingsFreqs.length; ++pos) {
                int docFreq;
                DocsAndPositionsEnum postingsEnum;
                Term[] terms = (Term[])MultiPhraseQuery.this.termArrays.get(pos);
                if (terms.length > 1) {
                    postingsEnum = new UnionDocsAndPositionsEnum(liveDocs, context, terms, this.termContexts, termsEnum);
                    docFreq = 0;
                    for (int termIdx = 0; termIdx < terms.length; ++termIdx) {
                        Term term = terms[termIdx];
                        TermState termState = this.termContexts.get(term).get(context.ord);
                        if (termState == null) continue;
                        termsEnum.seekExact(term.bytes(), termState);
                        docFreq += termsEnum.docFreq();
                    }
                    if (docFreq == 0) {
                        return null;
                    }
                } else {
                    Term term = terms[0];
                    TermState termState = this.termContexts.get(term).get(context.ord);
                    if (termState == null) {
                        return null;
                    }
                    termsEnum.seekExact(term.bytes(), termState);
                    postingsEnum = termsEnum.docsAndPositions(liveDocs, null, 0);
                    if (postingsEnum == null) {
                        assert (termsEnum.docs(liveDocs, null, 0) != null) : "termstate found but no term exists in reader";
                        throw new IllegalStateException("field \"" + term.field() + "\" was indexed without position data; cannot run PhraseQuery (term=" + term.text() + ")");
                    }
                    docFreq = termsEnum.docFreq();
                }
                postingsFreqs[pos] = new PhraseQuery.PostingsAndFreq(postingsEnum, docFreq, (Integer)MultiPhraseQuery.this.positions.get(pos), terms);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                ArrayUtil.timSort((Comparable[])postingsFreqs);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                ExactPhraseScorer s = new ExactPhraseScorer(this, (PhraseQuery.PostingsAndFreq[])postingsFreqs, this.similarity.simScorer(this.stats, context));
                if (s.noDocs) {
                    return null;
                }
                return s;
            }
            return new SloppyPhraseScorer(this, (PhraseQuery.PostingsAndFreq[])postingsFreqs, MultiPhraseQuery.this.slop, this.similarity.simScorer(this.stats, context));
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            int newDoc;
            Scorer scorer = this.scorer(context, true, false, context.reader().getLiveDocs());
            if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
                float freq = MultiPhraseQuery.this.slop == 0 ? (float)scorer.freq() : ((SloppyPhraseScorer)scorer).sloppyFreq();
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
}

