/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermFreqVector;
import java.util.Arrays;

class SegmentTermVector
implements TermFreqVector {
    private String field;
    private String[] terms;
    private int[] termFreqs;

    SegmentTermVector(String field, String[] terms, int[] termFreqs) {
        this.field = field;
        this.terms = terms;
        this.termFreqs = termFreqs;
    }

    public String getField() {
        return this.field;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append(this.field).append(": ");
        if (this.terms != null) {
            for (int i = 0; i < this.terms.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.terms[i]).append('/').append(this.termFreqs[i]);
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public int size() {
        return this.terms == null ? 0 : this.terms.length;
    }

    public String[] getTerms() {
        return this.terms;
    }

    public int[] getTermFrequencies() {
        return this.termFreqs;
    }

    public int indexOf(String termText) {
        if (this.terms == null) {
            return -1;
        }
        int res = Arrays.binarySearch(this.terms, termText);
        return res >= 0 ? res : -1;
    }

    public int[] indexesOf(String[] termNumbers, int start, int len) {
        int[] res = new int[len];
        for (int i = 0; i < len; ++i) {
            res[i] = this.indexOf(termNumbers[start + i]);
        }
        return res;
    }
}

