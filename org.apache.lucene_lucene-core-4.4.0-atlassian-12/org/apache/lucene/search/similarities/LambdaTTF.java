/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Lambda;

public class LambdaTTF
extends Lambda {
    @Override
    public final float lambda(BasicStats stats) {
        return ((float)stats.getTotalTermFreq() + 1.0f) / ((float)stats.getNumberOfDocuments() + 1.0f);
    }

    @Override
    public final Explanation explain(BasicStats stats) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.lambda(stats));
        result.addDetail(new Explanation(stats.getTotalTermFreq(), "totalTermFreq"));
        result.addDetail(new Explanation(stats.getNumberOfDocuments(), "numberOfDocuments"));
        return result;
    }

    @Override
    public String toString() {
        return "L";
    }
}

