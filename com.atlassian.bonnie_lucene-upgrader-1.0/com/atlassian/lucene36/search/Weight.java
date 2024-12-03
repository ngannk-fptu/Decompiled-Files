/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;
import java.io.Serializable;

public abstract class Weight
implements Serializable {
    public abstract Explanation explain(IndexReader var1, int var2) throws IOException;

    public abstract Query getQuery();

    public abstract float getValue();

    public abstract void normalize(float var1);

    public abstract Scorer scorer(IndexReader var1, boolean var2, boolean var3) throws IOException;

    public abstract float sumOfSquaredWeights() throws IOException;

    public boolean scoresDocsOutOfOrder() {
        return false;
    }
}

