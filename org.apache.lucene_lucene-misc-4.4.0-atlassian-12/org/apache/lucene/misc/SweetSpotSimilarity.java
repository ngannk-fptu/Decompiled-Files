/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.FieldInvertState
 *  org.apache.lucene.search.similarities.DefaultSimilarity
 */
package org.apache.lucene.misc;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class SweetSpotSimilarity
extends DefaultSimilarity {
    private int ln_min = 1;
    private int ln_max = 1;
    private float ln_steep = 0.5f;
    private float tf_base = 0.0f;
    private float tf_min = 0.0f;
    private float tf_hyper_min = 0.0f;
    private float tf_hyper_max = 2.0f;
    private double tf_hyper_base = 1.3;
    private float tf_hyper_xoffset = 10.0f;

    public void setBaselineTfFactors(float base, float min) {
        this.tf_min = min;
        this.tf_base = base;
    }

    public void setHyperbolicTfFactors(float min, float max, double base, float xoffset) {
        this.tf_hyper_min = min;
        this.tf_hyper_max = max;
        this.tf_hyper_base = base;
        this.tf_hyper_xoffset = xoffset;
    }

    public void setLengthNormFactors(int min, int max, float steepness, boolean discountOverlaps) {
        this.ln_min = min;
        this.ln_max = max;
        this.ln_steep = steepness;
        this.discountOverlaps = discountOverlaps;
    }

    public float lengthNorm(FieldInvertState state) {
        int numTokens = this.discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
        return state.getBoost() * this.computeLengthNorm(numTokens);
    }

    public float computeLengthNorm(int numTerms) {
        int l = this.ln_min;
        int h = this.ln_max;
        float s = this.ln_steep;
        return (float)(1.0 / Math.sqrt(s * (float)(Math.abs(numTerms - l) + Math.abs(numTerms - h) - (h - l)) + 1.0f));
    }

    public float tf(float freq) {
        return this.baselineTf(freq);
    }

    public float baselineTf(float freq) {
        if (0.0f == freq) {
            return 0.0f;
        }
        return freq <= this.tf_min ? this.tf_base : (float)Math.sqrt(freq + this.tf_base * this.tf_base - this.tf_min);
    }

    public float hyperbolicTf(float freq) {
        if (0.0f == freq) {
            return 0.0f;
        }
        float min = this.tf_hyper_min;
        float max = this.tf_hyper_max;
        double base = this.tf_hyper_base;
        float xoffset = this.tf_hyper_xoffset;
        double x = freq - xoffset;
        float result = min + (float)((double)((max - min) / 2.0f) * ((Math.pow(base, x) - Math.pow(base, -x)) / (Math.pow(base, x) + Math.pow(base, -x)) + 1.0));
        return Float.isNaN(result) ? max : result;
    }
}

