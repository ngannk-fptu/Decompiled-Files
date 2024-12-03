/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.BooleanQuery$TooManyClauses
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.similarities.DefaultSimilarity
 *  org.apache.lucene.search.similarities.TFIDFSimilarity
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.PriorityQueue
 *  org.apache.lucene.util.UnicodeUtil
 */
package org.apache.lucene.queries.mlt;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.UnicodeUtil;

public final class MoreLikeThis {
    public static final int DEFAULT_MAX_NUM_TOKENS_PARSED = 5000;
    public static final int DEFAULT_MIN_TERM_FREQ = 2;
    public static final int DEFAULT_MIN_DOC_FREQ = 5;
    public static final int DEFAULT_MAX_DOC_FREQ = Integer.MAX_VALUE;
    public static final boolean DEFAULT_BOOST = false;
    public static final String[] DEFAULT_FIELD_NAMES = new String[]{"contents"};
    public static final int DEFAULT_MIN_WORD_LENGTH = 0;
    public static final int DEFAULT_MAX_WORD_LENGTH = 0;
    public static final Set<?> DEFAULT_STOP_WORDS = null;
    private Set<?> stopWords = DEFAULT_STOP_WORDS;
    public static final int DEFAULT_MAX_QUERY_TERMS = 25;
    private Analyzer analyzer = null;
    private int minTermFreq = 2;
    private int minDocFreq = 5;
    private int maxDocFreq = Integer.MAX_VALUE;
    private boolean boost = false;
    private String[] fieldNames = DEFAULT_FIELD_NAMES;
    private int maxNumTokensParsed = 5000;
    private int minWordLen = 0;
    private int maxWordLen = 0;
    private int maxQueryTerms = 25;
    private TFIDFSimilarity similarity;
    private final IndexReader ir;
    private float boostFactor = 1.0f;

    public float getBoostFactor() {
        return this.boostFactor;
    }

    public void setBoostFactor(float boostFactor) {
        this.boostFactor = boostFactor;
    }

    public MoreLikeThis(IndexReader ir) {
        this(ir, (TFIDFSimilarity)new DefaultSimilarity());
    }

    public MoreLikeThis(IndexReader ir, TFIDFSimilarity sim) {
        this.ir = ir;
        this.similarity = sim;
    }

    public TFIDFSimilarity getSimilarity() {
        return this.similarity;
    }

    public void setSimilarity(TFIDFSimilarity similarity) {
        this.similarity = similarity;
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public int getMinTermFreq() {
        return this.minTermFreq;
    }

    public void setMinTermFreq(int minTermFreq) {
        this.minTermFreq = minTermFreq;
    }

    public int getMinDocFreq() {
        return this.minDocFreq;
    }

    public void setMinDocFreq(int minDocFreq) {
        this.minDocFreq = minDocFreq;
    }

    public int getMaxDocFreq() {
        return this.maxDocFreq;
    }

    public void setMaxDocFreq(int maxFreq) {
        this.maxDocFreq = maxFreq;
    }

    public void setMaxDocFreqPct(int maxPercentage) {
        this.maxDocFreq = maxPercentage * this.ir.numDocs() / 100;
    }

    public boolean isBoost() {
        return this.boost;
    }

    public void setBoost(boolean boost) {
        this.boost = boost;
    }

    public String[] getFieldNames() {
        return this.fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public int getMinWordLen() {
        return this.minWordLen;
    }

    public void setMinWordLen(int minWordLen) {
        this.minWordLen = minWordLen;
    }

    public int getMaxWordLen() {
        return this.maxWordLen;
    }

    public void setMaxWordLen(int maxWordLen) {
        this.maxWordLen = maxWordLen;
    }

    public void setStopWords(Set<?> stopWords) {
        this.stopWords = stopWords;
    }

    public Set<?> getStopWords() {
        return this.stopWords;
    }

    public int getMaxQueryTerms() {
        return this.maxQueryTerms;
    }

    public void setMaxQueryTerms(int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }

    public int getMaxNumTokensParsed() {
        return this.maxNumTokensParsed;
    }

    public void setMaxNumTokensParsed(int i) {
        this.maxNumTokensParsed = i;
    }

    public Query like(int docNum) throws IOException {
        if (this.fieldNames == null) {
            Collection fields = MultiFields.getIndexedFields((IndexReader)this.ir);
            this.fieldNames = fields.toArray(new String[fields.size()]);
        }
        return this.createQuery(this.retrieveTerms(docNum));
    }

    public Query like(Reader r, String fieldName) throws IOException {
        return this.createQuery(this.retrieveTerms(r, fieldName));
    }

    private Query createQuery(PriorityQueue<Object[]> q) {
        Object cur;
        BooleanQuery query = new BooleanQuery();
        int qterms = 0;
        float bestScore = 0.0f;
        while ((cur = q.pop()) != null) {
            Object[] ar = (Object[])cur;
            TermQuery tq = new TermQuery(new Term((String)ar[1], (String)ar[0]));
            if (this.boost) {
                if (qterms == 0) {
                    bestScore = ((Float)ar[2]).floatValue();
                }
                float myScore = ((Float)ar[2]).floatValue();
                tq.setBoost(this.boostFactor * myScore / bestScore);
            }
            try {
                query.add((Query)tq, BooleanClause.Occur.SHOULD);
            }
            catch (BooleanQuery.TooManyClauses ignore) {
                break;
            }
            if (this.maxQueryTerms <= 0 || ++qterms < this.maxQueryTerms) continue;
            break;
        }
        return query;
    }

    private PriorityQueue<Object[]> createQueue(Map<String, Int> words) throws IOException {
        int numDocs = this.ir.numDocs();
        FreqQ res = new FreqQ(words.size());
        for (String word : words.keySet()) {
            int tf = words.get((Object)word).x;
            if (this.minTermFreq > 0 && tf < this.minTermFreq) continue;
            String topField = this.fieldNames[0];
            int docFreq = 0;
            for (String fieldName : this.fieldNames) {
                int freq = this.ir.docFreq(new Term(fieldName, word));
                topField = freq > docFreq ? fieldName : topField;
                docFreq = freq > docFreq ? freq : docFreq;
            }
            if (this.minDocFreq > 0 && docFreq < this.minDocFreq || docFreq > this.maxDocFreq || docFreq == 0) continue;
            float idf = this.similarity.idf((long)docFreq, (long)numDocs);
            float score = (float)tf * idf;
            res.insertWithOverflow(new Object[]{word, topField, Float.valueOf(score), Float.valueOf(idf), docFreq, tf});
        }
        return res;
    }

    public String describeParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append("maxQueryTerms  : ").append(this.maxQueryTerms).append("\n");
        sb.append("\t").append("minWordLen     : ").append(this.minWordLen).append("\n");
        sb.append("\t").append("maxWordLen     : ").append(this.maxWordLen).append("\n");
        sb.append("\t").append("fieldNames     : ");
        String delim = "";
        for (String fieldName : this.fieldNames) {
            sb.append(delim).append(fieldName);
            delim = ", ";
        }
        sb.append("\n");
        sb.append("\t").append("boost          : ").append(this.boost).append("\n");
        sb.append("\t").append("minTermFreq    : ").append(this.minTermFreq).append("\n");
        sb.append("\t").append("minDocFreq     : ").append(this.minDocFreq).append("\n");
        return sb.toString();
    }

    public PriorityQueue<Object[]> retrieveTerms(int docNum) throws IOException {
        HashMap<String, Int> termFreqMap = new HashMap<String, Int>();
        for (String fieldName : this.fieldNames) {
            Fields vectors = this.ir.getTermVectors(docNum);
            Terms vector = vectors != null ? vectors.terms(fieldName) : null;
            if (vector == null) {
                IndexableField[] fields;
                Document d = this.ir.document(docNum);
                for (IndexableField field : fields = d.getFields(fieldName)) {
                    String stringValue = field.stringValue();
                    if (stringValue == null) continue;
                    this.addTermFrequencies(new StringReader(stringValue), termFreqMap, fieldName);
                }
                continue;
            }
            this.addTermFrequencies(termFreqMap, vector);
        }
        return this.createQueue(termFreqMap);
    }

    private void addTermFrequencies(Map<String, Int> termFreqMap, Terms vector) throws IOException {
        BytesRef text;
        TermsEnum termsEnum = vector.iterator(null);
        CharsRef spare = new CharsRef();
        while ((text = termsEnum.next()) != null) {
            UnicodeUtil.UTF8toUTF16((BytesRef)text, (CharsRef)spare);
            String term = spare.toString();
            if (this.isNoiseWord(term)) continue;
            int freq = (int)termsEnum.totalTermFreq();
            Int cnt = termFreqMap.get(term);
            if (cnt == null) {
                cnt = new Int();
                termFreqMap.put(term, cnt);
                cnt.x = freq;
                continue;
            }
            cnt.x += freq;
        }
    }

    private void addTermFrequencies(Reader r, Map<String, Int> termFreqMap, String fieldName) throws IOException {
        if (this.analyzer == null) {
            throw new UnsupportedOperationException("To use MoreLikeThis without term vectors, you must provide an Analyzer");
        }
        TokenStream ts = this.analyzer.tokenStream(fieldName, r);
        int tokenCount = 0;
        CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String word = termAtt.toString();
            if (++tokenCount > this.maxNumTokensParsed) break;
            if (this.isNoiseWord(word)) continue;
            Int cnt = termFreqMap.get(word);
            if (cnt == null) {
                termFreqMap.put(word, new Int());
                continue;
            }
            ++cnt.x;
        }
        ts.end();
        ts.close();
    }

    private boolean isNoiseWord(String term) {
        int len = term.length();
        if (this.minWordLen > 0 && len < this.minWordLen) {
            return true;
        }
        if (this.maxWordLen > 0 && len > this.maxWordLen) {
            return true;
        }
        return this.stopWords != null && this.stopWords.contains(term);
    }

    public PriorityQueue<Object[]> retrieveTerms(Reader r, String fieldName) throws IOException {
        HashMap<String, Int> words = new HashMap<String, Int>();
        this.addTermFrequencies(r, words, fieldName);
        return this.createQueue(words);
    }

    public String[] retrieveInterestingTerms(int docNum) throws IOException {
        Object cur;
        ArrayList<Object> al = new ArrayList<Object>(this.maxQueryTerms);
        PriorityQueue<Object[]> pq = this.retrieveTerms(docNum);
        int lim = this.maxQueryTerms;
        while ((cur = pq.pop()) != null && lim-- > 0) {
            Object[] ar = (Object[])cur;
            al.add(ar[0]);
        }
        String[] res = new String[al.size()];
        return al.toArray(res);
    }

    public String[] retrieveInterestingTerms(Reader r, String fieldName) throws IOException {
        Object cur;
        ArrayList<Object> al = new ArrayList<Object>(this.maxQueryTerms);
        PriorityQueue<Object[]> pq = this.retrieveTerms(r, fieldName);
        int lim = this.maxQueryTerms;
        while ((cur = pq.pop()) != null && lim-- > 0) {
            Object[] ar = (Object[])cur;
            al.add(ar[0]);
        }
        String[] res = new String[al.size()];
        return al.toArray(res);
    }

    private static class Int {
        int x = 1;

        Int() {
        }
    }

    private static class FreqQ
    extends PriorityQueue<Object[]> {
        FreqQ(int s) {
            super(s);
        }

        protected boolean lessThan(Object[] aa, Object[] bb) {
            Float fa = (Float)aa[2];
            Float fb = (Float)bb[2];
            return fa.floatValue() > fb.floatValue();
        }
    }
}

