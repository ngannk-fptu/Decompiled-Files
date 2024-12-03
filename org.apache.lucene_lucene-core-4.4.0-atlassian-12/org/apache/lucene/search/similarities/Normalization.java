/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;

public abstract class Normalization {
    public abstract float tfn(BasicStats var1, float var2, float var3);

    public Explanation explain(BasicStats stats, float tf, float len) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.tfn(stats, tf, len));
        result.addDetail(new Explanation(tf, "tf"));
        result.addDetail(new Explanation(stats.getAvgFieldLength(), "avgFieldLength"));
        result.addDetail(new Explanation(len, "len"));
        return result;
    }

    public abstract String toString();

    public static final class NoNormalization
    extends Normalization {
        @Override
        public final float tfn(BasicStats stats, float tf, float len) {
            return tf;
        }

        @Override
        public final Explanation explain(BasicStats stats, float tf, float len) {
            return new Explanation(1.0f, "no normalization");
        }

        @Override
        public String toString() {
            return "";
        }
    }
}

