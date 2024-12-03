/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;

public abstract class BasicModel {
    public abstract float score(BasicStats var1, float var2);

    public Explanation explain(BasicStats stats, float tfn) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.score(stats, tfn));
        result.addDetail(new Explanation(tfn, "tfn"));
        result.addDetail(new Explanation(stats.getNumberOfDocuments(), "numberOfDocuments"));
        result.addDetail(new Explanation(stats.getTotalTermFreq(), "totalTermFreq"));
        return result;
    }

    public abstract String toString();
}

