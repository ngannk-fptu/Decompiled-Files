/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.util.PriorityQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TopDocsCollector<T extends ScoreDoc>
extends Collector {
    protected static final TopDocs EMPTY_TOPDOCS = new TopDocs(0, new ScoreDoc[0], Float.NaN);
    protected PriorityQueue<T> pq;
    protected int totalHits;

    protected TopDocsCollector(PriorityQueue<T> pq) {
        this.pq = pq;
    }

    protected void populateResults(ScoreDoc[] results, int howMany) {
        for (int i = howMany - 1; i >= 0; --i) {
            results[i] = (ScoreDoc)this.pq.pop();
        }
    }

    protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
        return results == null ? EMPTY_TOPDOCS : new TopDocs(this.totalHits, results);
    }

    public int getTotalHits() {
        return this.totalHits;
    }

    protected int topDocsSize() {
        return this.totalHits < this.pq.size() ? this.totalHits : this.pq.size();
    }

    public TopDocs topDocs() {
        return this.topDocs(0, this.topDocsSize());
    }

    public TopDocs topDocs(int start) {
        return this.topDocs(start, this.topDocsSize());
    }

    public TopDocs topDocs(int start, int howMany) {
        int size = this.topDocsSize();
        if (start < 0 || start >= size || howMany <= 0) {
            return this.newTopDocs(null, start);
        }
        howMany = Math.min(size - start, howMany);
        ScoreDoc[] results = new ScoreDoc[howMany];
        for (int i = this.pq.size() - start - howMany; i > 0; --i) {
            this.pq.pop();
        }
        this.populateResults(results, howMany);
        return this.newTopDocs(results, start);
    }
}

