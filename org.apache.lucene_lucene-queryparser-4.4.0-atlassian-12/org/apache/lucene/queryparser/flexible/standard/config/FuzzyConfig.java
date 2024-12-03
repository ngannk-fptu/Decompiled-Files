/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.config;

public class FuzzyConfig {
    private int prefixLength = 0;
    private float minSimilarity = 2.0f;

    public int getPrefixLength() {
        return this.prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public float getMinSimilarity() {
        return this.minSimilarity;
    }

    public void setMinSimilarity(float minSimilarity) {
        this.minSimilarity = minSimilarity;
    }
}

