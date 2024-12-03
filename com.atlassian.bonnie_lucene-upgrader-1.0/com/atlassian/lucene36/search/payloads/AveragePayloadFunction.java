/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.payloads;

import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.payloads.PayloadFunction;

public class AveragePayloadFunction
extends PayloadFunction {
    public float currentScore(int docId, String field, int start, int end, int numPayloadsSeen, float currentScore, float currentPayloadScore) {
        return currentPayloadScore + currentScore;
    }

    public float docScore(int docId, String field, int numPayloadsSeen, float payloadScore) {
        return numPayloadsSeen > 0 ? payloadScore / (float)numPayloadsSeen : 1.0f;
    }

    public Explanation explain(int doc, int numPayloadsSeen, float payloadScore) {
        Explanation payloadBoost = new Explanation();
        float avgPayloadScore = numPayloadsSeen > 0 ? payloadScore / (float)numPayloadsSeen : 1.0f;
        payloadBoost.setValue(avgPayloadScore);
        payloadBoost.setDescription("AveragePayloadFunction(...)");
        return payloadBoost;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.getClass().hashCode();
        return result;
    }

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

