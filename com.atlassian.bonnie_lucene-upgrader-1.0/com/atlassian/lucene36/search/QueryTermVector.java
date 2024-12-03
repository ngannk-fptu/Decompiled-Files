/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class QueryTermVector
implements TermFreqVector {
    private String[] terms = new String[0];
    private int[] termFreqs = new int[0];

    public String getField() {
        return null;
    }

    public QueryTermVector(String[] queryTerms) {
        this.processTerms(queryTerms);
    }

    public QueryTermVector(String queryString, Analyzer analyzer) {
        if (analyzer != null) {
            TokenStream stream;
            try {
                stream = analyzer.reusableTokenStream("", new StringReader(queryString));
            }
            catch (IOException e1) {
                stream = null;
            }
            if (stream != null) {
                ArrayList<String> terms = new ArrayList<String>();
                try {
                    boolean hasMoreTokens = false;
                    stream.reset();
                    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
                    hasMoreTokens = stream.incrementToken();
                    while (hasMoreTokens) {
                        terms.add(termAtt.toString());
                        hasMoreTokens = stream.incrementToken();
                    }
                    this.processTerms(terms.toArray(new String[terms.size()]));
                }
                catch (IOException e) {
                    // empty catch block
                }
            }
        }
    }

    private void processTerms(String[] queryTerms) {
        if (queryTerms != null) {
            int i;
            ArrayUtil.quickSort((Comparable[])queryTerms);
            HashMap<String, Integer> tmpSet = new HashMap<String, Integer>(queryTerms.length);
            ArrayList<String> tmpList = new ArrayList<String>(queryTerms.length);
            ArrayList<Integer> tmpFreqs = new ArrayList<Integer>(queryTerms.length);
            int j = 0;
            for (i = 0; i < queryTerms.length; ++i) {
                String term = queryTerms[i];
                Integer position = (Integer)tmpSet.get(term);
                if (position == null) {
                    tmpSet.put(term, j++);
                    tmpList.add(term);
                    tmpFreqs.add(1);
                    continue;
                }
                Integer integer = (Integer)tmpFreqs.get(position);
                tmpFreqs.set(position, integer + 1);
            }
            this.terms = tmpList.toArray(this.terms);
            this.termFreqs = new int[tmpFreqs.size()];
            i = 0;
            for (Integer integer : tmpFreqs) {
                this.termFreqs[i++] = integer;
            }
        }
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < this.terms.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.terms[i]).append('/').append(this.termFreqs[i]);
        }
        sb.append('}');
        return sb.toString();
    }

    public int size() {
        return this.terms.length;
    }

    public String[] getTerms() {
        return this.terms;
    }

    public int[] getTermFrequencies() {
        return this.termFreqs;
    }

    public int indexOf(String term) {
        int res = Arrays.binarySearch(this.terms, term);
        return res >= 0 ? res : -1;
    }

    public int[] indexesOf(String[] terms, int start, int len) {
        int[] res = new int[len];
        for (int i = 0; i < len; ++i) {
            res[i] = this.indexOf(terms[i]);
        }
        return res;
    }
}

