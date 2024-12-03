/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.Query;
import java.io.IOException;

public class NGramPhraseQuery
extends PhraseQuery {
    private final int n;

    public NGramPhraseQuery(int n) {
        this.n = n;
    }

    public Query rewrite(IndexReader reader) throws IOException {
        int pos;
        if (this.getSlop() != 0) {
            return super.rewrite(reader);
        }
        if (this.n < 2 || this.getTerms().length < 3) {
            return super.rewrite(reader);
        }
        int[] positions = this.getPositions();
        Term[] terms = this.getTerms();
        int prevPosition = positions[0];
        for (int i = 1; i < positions.length; ++i) {
            pos = positions[i];
            if (prevPosition + 1 != pos) {
                return super.rewrite(reader);
            }
            prevPosition = pos;
        }
        PhraseQuery optimized = new PhraseQuery();
        pos = 0;
        int lastPos = terms.length - 1;
        for (int i = 0; i < terms.length; ++i) {
            if (pos % this.n == 0 || pos >= lastPos) {
                optimized.add(terms[i], positions[i]);
            }
            ++pos;
        }
        return optimized;
    }

    public boolean equals(Object o) {
        if (!(o instanceof NGramPhraseQuery)) {
            return false;
        }
        NGramPhraseQuery other = (NGramPhraseQuery)o;
        if (this.n != other.n) {
            return false;
        }
        return super.equals(other);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.getSlop() ^ this.getTerms().hashCode() ^ this.getPositions().hashCode() ^ this.n;
    }
}

