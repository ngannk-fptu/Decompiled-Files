/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.BoostAttribute
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.MaxNonCompetitiveBoostAttribute
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.similarities.DefaultSimilarity
 *  org.apache.lucene.search.similarities.TFIDFSimilarity
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.PriorityQueue
 */
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.sandbox.queries.SlowFuzzyTermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

public class FuzzyLikeThisQuery
extends Query {
    static TFIDFSimilarity sim = new DefaultSimilarity();
    Query rewrittenQuery = null;
    ArrayList<FieldVals> fieldVals = new ArrayList();
    Analyzer analyzer;
    ScoreTermQueue q;
    int MAX_VARIANTS_PER_TERM = 50;
    boolean ignoreTF = false;
    private int maxNumTerms;

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.analyzer == null ? 0 : this.analyzer.hashCode());
        result = 31 * result + (this.fieldVals == null ? 0 : this.fieldVals.hashCode());
        result = 31 * result + (this.ignoreTF ? 1231 : 1237);
        result = 31 * result + this.maxNumTerms;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        FuzzyLikeThisQuery other = (FuzzyLikeThisQuery)((Object)obj);
        if (this.analyzer == null ? other.analyzer != null : !this.analyzer.equals(other.analyzer)) {
            return false;
        }
        if (this.fieldVals == null ? other.fieldVals != null : !this.fieldVals.equals(other.fieldVals)) {
            return false;
        }
        if (this.ignoreTF != other.ignoreTF) {
            return false;
        }
        return this.maxNumTerms == other.maxNumTerms;
    }

    public FuzzyLikeThisQuery(int maxNumTerms, Analyzer analyzer) {
        this.q = new ScoreTermQueue(maxNumTerms);
        this.analyzer = analyzer;
        this.maxNumTerms = maxNumTerms;
    }

    public void addTerms(String queryString, String fieldName, float minSimilarity, int prefixLength) {
        this.fieldVals.add(new FieldVals(fieldName, minSimilarity, prefixLength, queryString));
    }

    private void addTerms(IndexReader reader, FieldVals f) throws IOException {
        if (f.queryString == null) {
            return;
        }
        TokenStream ts = this.analyzer.tokenStream(f.fieldName, f.queryString);
        CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
        int corpusNumDocs = reader.numDocs();
        HashSet<String> processedTerms = new HashSet<String>();
        ts.reset();
        Terms terms = MultiFields.getTerms((IndexReader)reader, (String)f.fieldName);
        if (terms == null) {
            return;
        }
        while (ts.incrementToken()) {
            BytesRef possibleMatch;
            String term = termAtt.toString();
            if (processedTerms.contains(term)) continue;
            processedTerms.add(term);
            ScoreTermQueue variantsQ = new ScoreTermQueue(this.MAX_VARIANTS_PER_TERM);
            float minScore = 0.0f;
            Term startTerm = new Term(f.fieldName, term);
            AttributeSource atts = new AttributeSource();
            MaxNonCompetitiveBoostAttribute maxBoostAtt = (MaxNonCompetitiveBoostAttribute)atts.addAttribute(MaxNonCompetitiveBoostAttribute.class);
            SlowFuzzyTermsEnum fe = new SlowFuzzyTermsEnum(terms, atts, startTerm, f.minSimilarity, f.prefixLength);
            int df = reader.docFreq(startTerm);
            int numVariants = 0;
            int totalVariantDocFreqs = 0;
            BoostAttribute boostAtt = (BoostAttribute)fe.attributes().addAttribute(BoostAttribute.class);
            while ((possibleMatch = fe.next()) != null) {
                ++numVariants;
                totalVariantDocFreqs += fe.docFreq();
                float score = boostAtt.getBoost();
                if (variantsQ.size() < this.MAX_VARIANTS_PER_TERM || score > minScore) {
                    ScoreTerm st = new ScoreTerm(new Term(startTerm.field(), BytesRef.deepCopyOf((BytesRef)possibleMatch)), score, startTerm);
                    variantsQ.insertWithOverflow(st);
                    minScore = ((ScoreTerm)variantsQ.top()).score;
                }
                maxBoostAtt.setMaxNonCompetitiveBoost(variantsQ.size() >= this.MAX_VARIANTS_PER_TERM ? minScore : Float.NEGATIVE_INFINITY);
            }
            if (numVariants <= 0) continue;
            int avgDf = totalVariantDocFreqs / numVariants;
            if (df == 0) {
                df = avgDf;
            }
            int size = variantsQ.size();
            for (int i = 0; i < size; ++i) {
                ScoreTerm st = (ScoreTerm)variantsQ.pop();
                st.score = st.score * st.score * sim.idf((long)df, (long)corpusNumDocs);
                this.q.insertWithOverflow(st);
            }
        }
        ts.end();
        ts.close();
    }

    public Query rewrite(IndexReader reader) throws IOException {
        if (this.rewrittenQuery != null) {
            return this.rewrittenQuery;
        }
        for (FieldVals f : this.fieldVals) {
            this.addTerms(reader, f);
        }
        this.fieldVals.clear();
        BooleanQuery bq = new BooleanQuery();
        HashMap<Term, ArrayList<ScoreTerm>> variantQueries = new HashMap<Term, ArrayList<ScoreTerm>>();
        int size = this.q.size();
        for (int i = 0; i < size; ++i) {
            ScoreTerm st = (ScoreTerm)this.q.pop();
            ArrayList<ScoreTerm> l = (ArrayList<ScoreTerm>)variantQueries.get(st.fuzziedSourceTerm);
            if (l == null) {
                l = new ArrayList<ScoreTerm>();
                variantQueries.put(st.fuzziedSourceTerm, l);
            }
            l.add(st);
        }
        for (ArrayList variants : variantQueries.values()) {
            if (variants.size() == 1) {
                ScoreTerm st = (ScoreTerm)variants.get(0);
                ConstantScoreQuery tq = this.ignoreTF ? new ConstantScoreQuery((Query)new TermQuery(st.term)) : new TermQuery(st.term, 1);
                tq.setBoost(st.score);
                bq.add((Query)tq, BooleanClause.Occur.SHOULD);
                continue;
            }
            BooleanQuery termVariants = new BooleanQuery(true);
            for (ScoreTerm st : variants) {
                ConstantScoreQuery tq = this.ignoreTF ? new ConstantScoreQuery((Query)new TermQuery(st.term)) : new TermQuery(st.term, 1);
                tq.setBoost(st.score);
                termVariants.add((Query)tq, BooleanClause.Occur.SHOULD);
            }
            bq.add((Query)termVariants, BooleanClause.Occur.SHOULD);
        }
        bq.setBoost(this.getBoost());
        this.rewrittenQuery = bq;
        return bq;
    }

    public String toString(String field) {
        return null;
    }

    public boolean isIgnoreTF() {
        return this.ignoreTF;
    }

    public void setIgnoreTF(boolean ignoreTF) {
        this.ignoreTF = ignoreTF;
    }

    private static class ScoreTermQueue
    extends PriorityQueue<ScoreTerm> {
        public ScoreTermQueue(int size) {
            super(size);
        }

        protected boolean lessThan(ScoreTerm termA, ScoreTerm termB) {
            if (termA.score == termB.score) {
                return termA.term.compareTo(termB.term) > 0;
            }
            return termA.score < termB.score;
        }
    }

    private static class ScoreTerm {
        public Term term;
        public float score;
        Term fuzziedSourceTerm;

        public ScoreTerm(Term term, float score, Term fuzziedSourceTerm) {
            this.term = term;
            this.score = score;
            this.fuzziedSourceTerm = fuzziedSourceTerm;
        }
    }

    class FieldVals {
        String queryString;
        String fieldName;
        float minSimilarity;
        int prefixLength;

        public FieldVals(String name, float similarity, int length, String queryString) {
            this.fieldName = name;
            this.minSimilarity = similarity;
            this.prefixLength = length;
            this.queryString = queryString;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.fieldName == null ? 0 : this.fieldName.hashCode());
            result = 31 * result + Float.floatToIntBits(this.minSimilarity);
            result = 31 * result + this.prefixLength;
            result = 31 * result + (this.queryString == null ? 0 : this.queryString.hashCode());
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
            FieldVals other = (FieldVals)obj;
            if (this.fieldName == null ? other.fieldName != null : !this.fieldName.equals(other.fieldName)) {
                return false;
            }
            if (Float.floatToIntBits(this.minSimilarity) != Float.floatToIntBits(other.minSimilarity)) {
                return false;
            }
            if (this.prefixLength != other.prefixLength) {
                return false;
            }
            return !(this.queryString == null ? other.queryString != null : !this.queryString.equals(other.queryString));
        }
    }
}

