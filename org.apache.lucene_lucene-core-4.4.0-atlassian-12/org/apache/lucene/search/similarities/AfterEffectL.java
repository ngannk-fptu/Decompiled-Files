/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.AfterEffect;
import org.apache.lucene.search.similarities.BasicStats;

public class AfterEffectL
extends AfterEffect {
    @Override
    public final float score(BasicStats stats, float tfn) {
        return 1.0f / (tfn + 1.0f);
    }

    @Override
    public final Explanation explain(BasicStats stats, float tfn) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.score(stats, tfn));
        result.addDetail(new Explanation(tfn, "tfn"));
        return result;
    }

    @Override
    public String toString() {
        return "L";
    }
}

