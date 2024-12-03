/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Filter;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Searchable;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Sort;
import com.atlassian.lucene36.search.TopDocs;
import com.atlassian.lucene36.search.TopFieldDocs;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

@Deprecated
public abstract class Searcher
implements Searchable {
    private Similarity similarity = Similarity.getDefault();

    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(query), filter, n, sort);
    }

    public TopFieldDocs search(Query query, int n, Sort sort) throws IOException {
        return this.search(this.createNormalizedWeight(query), null, n, sort);
    }

    public void search(Query query, Collector results) throws IOException {
        this.search(this.createNormalizedWeight(query), null, results);
    }

    public void search(Query query, Filter filter, Collector results) throws IOException {
        this.search(this.createNormalizedWeight(query), filter, results);
    }

    public TopDocs search(Query query, Filter filter, int n) throws IOException {
        return this.search(this.createNormalizedWeight(query), filter, n);
    }

    public TopDocs search(Query query, int n) throws IOException {
        return this.search(query, null, n);
    }

    public Explanation explain(Query query, int doc) throws IOException {
        return this.explain(this.createNormalizedWeight(query), doc);
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public Similarity getSimilarity() {
        return this.similarity;
    }

    public Weight createNormalizedWeight(Query query) throws IOException {
        query = this.rewrite(query);
        Weight weight = query.createWeight(this);
        float sum = weight.sumOfSquaredWeights();
        float norm = query.getSimilarity(this).queryNorm(sum);
        if (Float.isInfinite(norm) || Float.isNaN(norm)) {
            norm = 1.0f;
        }
        weight.normalize(norm);
        return weight;
    }

    @Deprecated
    protected final Weight createWeight(Query query) throws IOException {
        return this.createNormalizedWeight(query);
    }

    public int[] docFreqs(Term[] terms) throws IOException {
        int[] result = new int[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            result[i] = this.docFreq(terms[i]);
        }
        return result;
    }

    public abstract void search(Weight var1, Filter var2, Collector var3) throws IOException;

    public abstract void close() throws IOException;

    public abstract int docFreq(Term var1) throws IOException;

    public abstract int maxDoc() throws IOException;

    public abstract TopDocs search(Weight var1, Filter var2, int var3) throws IOException;

    public abstract Document doc(int var1) throws CorruptIndexException, IOException;

    public abstract Document doc(int var1, FieldSelector var2) throws CorruptIndexException, IOException;

    public abstract Query rewrite(Query var1) throws IOException;

    public abstract Explanation explain(Weight var1, int var2) throws IOException;

    public abstract TopFieldDocs search(Weight var1, Filter var2, int var3, Sort var4) throws IOException;
}

