/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class DefaultSimilarity
extends TFIDFSimilarity {
    private static final float[] NORM_TABLE = new float[256];
    protected boolean discountOverlaps = true;

    @Override
    public float coord(int overlap, int maxOverlap) {
        return (float)overlap / (float)maxOverlap;
    }

    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
    }

    @Override
    public final long encodeNormValue(float f) {
        return SmallFloat.floatToByte315(f);
    }

    @Override
    public final float decodeNormValue(long norm) {
        return NORM_TABLE[(int)(norm & 0xFFL)];
    }

    @Override
    public float lengthNorm(FieldInvertState state) {
        int numTerms = this.discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
        return state.getBoost() * (float)(1.0 / Math.sqrt(numTerms));
    }

    @Override
    public float tf(float freq) {
        return (float)Math.sqrt(freq);
    }

    @Override
    public float sloppyFreq(int distance) {
        return 1.0f / (float)(distance + 1);
    }

    @Override
    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        return 1.0f;
    }

    @Override
    public float idf(long docFreq, long numDocs) {
        return (float)(Math.log((double)numDocs / (double)(docFreq + 1L)) + 1.0);
    }

    public void setDiscountOverlaps(boolean v) {
        this.discountOverlaps = v;
    }

    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }

    public String toString() {
        return "DefaultSimilarity";
    }

    static {
        for (int i = 0; i < 256; ++i) {
            DefaultSimilarity.NORM_TABLE[i] = SmallFloat.byte315ToFloat((byte)i);
        }
    }
}

