/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import java.io.IOException;

public final class FuzzyTermEnum
extends FilteredTermEnum {
    private int[] p;
    private int[] d;
    private float similarity;
    private boolean endEnum = false;
    private Term searchTerm = null;
    private final String field;
    private final char[] text;
    private final String prefix;
    private final float minimumSimilarity;
    private final float scale_factor;

    public FuzzyTermEnum(IndexReader reader, Term term) throws IOException {
        this(reader, term, 0.5f, 0);
    }

    public FuzzyTermEnum(IndexReader reader, Term term, float minSimilarity) throws IOException {
        this(reader, term, minSimilarity, 0);
    }

    public FuzzyTermEnum(IndexReader reader, Term term, float minSimilarity, int prefixLength) throws IOException {
        if (minSimilarity >= 1.0f) {
            throw new IllegalArgumentException("minimumSimilarity cannot be greater than or equal to 1");
        }
        if (minSimilarity < 0.0f) {
            throw new IllegalArgumentException("minimumSimilarity cannot be less than 0");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength cannot be less than 0");
        }
        this.minimumSimilarity = minSimilarity;
        this.scale_factor = 1.0f / (1.0f - this.minimumSimilarity);
        this.searchTerm = term;
        this.field = this.searchTerm.field();
        int fullSearchTermLength = this.searchTerm.text().length();
        int realPrefixLength = prefixLength > fullSearchTermLength ? fullSearchTermLength : prefixLength;
        this.text = this.searchTerm.text().substring(realPrefixLength).toCharArray();
        this.prefix = this.searchTerm.text().substring(0, realPrefixLength);
        this.p = new int[this.text.length + 1];
        this.d = new int[this.text.length + 1];
        this.setEnum(reader.terms(new Term(this.searchTerm.field(), this.prefix)));
    }

    protected final boolean termCompare(Term term) {
        if (this.field == term.field() && term.text().startsWith(this.prefix)) {
            String target = term.text().substring(this.prefix.length());
            this.similarity = this.similarity(target);
            return this.similarity > this.minimumSimilarity;
        }
        this.endEnum = true;
        return false;
    }

    public final float difference() {
        return (this.similarity - this.minimumSimilarity) * this.scale_factor;
    }

    public final boolean endEnum() {
        return this.endEnum;
    }

    private float similarity(String target) {
        int m = target.length();
        int n = this.text.length;
        if (n == 0) {
            return this.prefix.length() == 0 ? 0.0f : 1.0f - (float)m / (float)this.prefix.length();
        }
        if (m == 0) {
            return this.prefix.length() == 0 ? 0.0f : 1.0f - (float)n / (float)this.prefix.length();
        }
        int maxDistance = this.calculateMaxDistance(m);
        if (maxDistance < Math.abs(m - n)) {
            return 0.0f;
        }
        for (int i = 0; i <= n; ++i) {
            this.p[i] = i;
        }
        for (int j = 1; j <= m; ++j) {
            int bestPossibleEditDistance = m;
            char t_j = target.charAt(j - 1);
            this.d[0] = j;
            for (int i = 1; i <= n; ++i) {
                this.d[i] = t_j != this.text[i - 1] ? Math.min(Math.min(this.d[i - 1], this.p[i]), this.p[i - 1]) + 1 : Math.min(Math.min(this.d[i - 1] + 1, this.p[i] + 1), this.p[i - 1]);
                bestPossibleEditDistance = Math.min(bestPossibleEditDistance, this.d[i]);
            }
            if (j > maxDistance && bestPossibleEditDistance > maxDistance) {
                return 0.0f;
            }
            int[] _d = this.p;
            this.p = this.d;
            this.d = _d;
        }
        return 1.0f - (float)this.p[n] / (float)(this.prefix.length() + Math.min(n, m));
    }

    private int calculateMaxDistance(int m) {
        return (int)((1.0f - this.minimumSimilarity) * (float)(Math.min(this.text.length, m) + this.prefix.length()));
    }

    public void close() throws IOException {
        this.d = null;
        this.p = null;
        this.searchTerm = null;
        super.close();
    }
}

