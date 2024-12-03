/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.payloads;

import org.apache.lucene.search.payloads.PayloadFunction;

public class MaxPayloadFunction
extends PayloadFunction {
    @Override
    public float currentScore(int docId, String field, int start, int end, int numPayloadsSeen, float currentScore, float currentPayloadScore) {
        if (numPayloadsSeen == 0) {
            return currentPayloadScore;
        }
        return Math.max(currentPayloadScore, currentScore);
    }

    @Override
    public float docScore(int docId, String field, int numPayloadsSeen, float payloadScore) {
        return numPayloadsSeen > 0 ? payloadScore : 1.0f;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.getClass().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass();
    }
}

