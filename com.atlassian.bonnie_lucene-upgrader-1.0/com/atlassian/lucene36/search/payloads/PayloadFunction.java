/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.payloads;

import com.atlassian.lucene36.search.Explanation;
import java.io.Serializable;

public abstract class PayloadFunction
implements Serializable {
    public abstract float currentScore(int var1, String var2, int var3, int var4, int var5, float var6, float var7);

    public abstract float docScore(int var1, String var2, int var3, float var4);

    public Explanation explain(int docId, int numPayloadsSeen, float payloadScore) {
        Explanation result = new Explanation();
        result.setDescription("Unimpl Payload Function Explain");
        result.setValue(1.0f);
        return result;
    }

    public abstract int hashCode();

    public abstract boolean equals(Object var1);
}

