/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.util.AttributeSource;

public final class FieldInvertState {
    int position;
    int length;
    int numOverlap;
    int offset;
    int maxTermFrequency;
    int uniqueTermCount;
    float boost;
    AttributeSource attributeSource;

    public FieldInvertState() {
    }

    public FieldInvertState(int position, int length, int numOverlap, int offset, float boost) {
        this.position = position;
        this.length = length;
        this.numOverlap = numOverlap;
        this.offset = offset;
        this.boost = boost;
    }

    void reset(float docBoost) {
        this.position = 0;
        this.length = 0;
        this.numOverlap = 0;
        this.offset = 0;
        this.maxTermFrequency = 0;
        this.uniqueTermCount = 0;
        this.boost = docBoost;
        this.attributeSource = null;
    }

    public int getPosition() {
        return this.position;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getNumOverlap() {
        return this.numOverlap;
    }

    public void setNumOverlap(int numOverlap) {
        this.numOverlap = numOverlap;
    }

    public int getOffset() {
        return this.offset;
    }

    public float getBoost() {
        return this.boost;
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public int getMaxTermFrequency() {
        return this.maxTermFrequency;
    }

    public int getUniqueTermCount() {
        return this.uniqueTermCount;
    }

    public AttributeSource getAttributeSource() {
        return this.attributeSource;
    }
}

