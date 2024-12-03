/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.payloads;

import org.apache.lucene.search.Explanation;

public abstract class PayloadFunction {
    public abstract float currentScore(int var1, String var2, int var3, int var4, int var5, float var6, float var7);

    public abstract float docScore(int var1, String var2, int var3, float var4);

    public Explanation explain(int docId, String field, int numPayloadsSeen, float payloadScore) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ".docScore()");
        result.setValue(this.docScore(docId, field, numPayloadsSeen, payloadScore));
        return result;
    }

    public abstract int hashCode();

    public abstract boolean equals(Object var1);
}

