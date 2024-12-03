/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.Similarity;

public class BasicStats
extends Similarity.SimWeight {
    final String field;
    protected long numberOfDocuments;
    protected long numberOfFieldTokens;
    protected float avgFieldLength;
    protected long docFreq;
    protected long totalTermFreq;
    protected final float queryBoost;
    protected float topLevelBoost;
    protected float totalBoost;

    public BasicStats(String field, float queryBoost) {
        this.field = field;
        this.queryBoost = queryBoost;
        this.totalBoost = queryBoost;
    }

    public long getNumberOfDocuments() {
        return this.numberOfDocuments;
    }

    public void setNumberOfDocuments(long numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }

    public long getNumberOfFieldTokens() {
        return this.numberOfFieldTokens;
    }

    public void setNumberOfFieldTokens(long numberOfFieldTokens) {
        this.numberOfFieldTokens = numberOfFieldTokens;
    }

    public float getAvgFieldLength() {
        return this.avgFieldLength;
    }

    public void setAvgFieldLength(float avgFieldLength) {
        this.avgFieldLength = avgFieldLength;
    }

    public long getDocFreq() {
        return this.docFreq;
    }

    public void setDocFreq(long docFreq) {
        this.docFreq = docFreq;
    }

    public long getTotalTermFreq() {
        return this.totalTermFreq;
    }

    public void setTotalTermFreq(long totalTermFreq) {
        this.totalTermFreq = totalTermFreq;
    }

    @Override
    public float getValueForNormalization() {
        float rawValue = this.rawNormalizationValue();
        return rawValue * rawValue;
    }

    protected float rawNormalizationValue() {
        return this.queryBoost;
    }

    @Override
    public void normalize(float queryNorm, float topLevelBoost) {
        this.topLevelBoost = topLevelBoost;
        this.totalBoost = this.queryBoost * topLevelBoost;
    }

    public float getTotalBoost() {
        return this.totalBoost;
    }
}

