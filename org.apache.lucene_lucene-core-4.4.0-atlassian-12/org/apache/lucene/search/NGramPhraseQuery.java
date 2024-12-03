/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

public class NGramPhraseQuery
extends PhraseQuery {
    private final int n;

    public NGramPhraseQuery(int n) {
        this.n = n;
    }

    @Override
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
        optimized.setBoost(this.getBoost());
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

    @Override
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

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.getSlop() ^ this.getTerms().hashCode() ^ this.getPositions().hashCode() ^ this.n;
    }
}

