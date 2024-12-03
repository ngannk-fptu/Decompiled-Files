/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.index.Payload;
import com.atlassian.lucene36.util.Attribute;

public interface PayloadAttribute
extends Attribute {
    public Payload getPayload();

    public void setPayload(Payload var1);
}

