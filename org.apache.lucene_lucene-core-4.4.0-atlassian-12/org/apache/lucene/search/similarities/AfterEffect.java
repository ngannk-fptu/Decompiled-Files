/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;

public abstract class AfterEffect {
    public abstract float score(BasicStats var1, float var2);

    public abstract Explanation explain(BasicStats var1, float var2);

    public abstract String toString();

    public static final class NoAfterEffect
    extends AfterEffect {
        @Override
        public final float score(BasicStats stats, float tfn) {
            return 1.0f;
        }

        @Override
        public final Explanation explain(BasicStats stats, float tfn) {
            return new Explanation(1.0f, "no aftereffect");
        }

        @Override
        public String toString() {
            return "";
        }
    }
}

