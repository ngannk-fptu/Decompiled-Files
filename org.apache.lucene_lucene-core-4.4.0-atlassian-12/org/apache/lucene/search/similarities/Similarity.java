/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.util.BytesRef;

public abstract class Similarity {
    public float coord(int overlap, int maxOverlap) {
        return 1.0f;
    }

    public float queryNorm(float valueForNormalization) {
        return 1.0f;
    }

    public abstract long computeNorm(FieldInvertState var1);

    public abstract SimWeight computeWeight(float var1, CollectionStatistics var2, TermStatistics ... var3);

    public abstract SimScorer simScorer(SimWeight var1, AtomicReaderContext var2) throws IOException;

    public static abstract class SimWeight {
        public abstract float getValueForNormalization();

        public abstract void normalize(float var1, float var2);
    }

    public static abstract class SimScorer {
        public abstract float score(int var1, float var2);

        public abstract float computeSlopFactor(int var1);

        public abstract float computePayloadFactor(int var1, int var2, int var3, BytesRef var4);

        public Explanation explain(int doc, Explanation freq) {
            Explanation result = new Explanation(this.score(doc, freq.getValue()), "score(doc=" + doc + ",freq=" + freq.getValue() + "), with freq of:");
            result.addDetail(freq);
            return result;
        }
    }
}

