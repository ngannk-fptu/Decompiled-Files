/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.PayloadAttribute;
import com.atlassian.lucene36.index.Payload;
import com.atlassian.lucene36.util.AttributeImpl;
import java.io.Serializable;

public class PayloadAttributeImpl
extends AttributeImpl
implements PayloadAttribute,
Cloneable,
Serializable {
    private Payload payload;

    public PayloadAttributeImpl() {
    }

    public PayloadAttributeImpl(Payload payload) {
        this.payload = payload;
    }

    public Payload getPayload() {
        return this.payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public void clear() {
        this.payload = null;
    }

    public Object clone() {
        PayloadAttributeImpl clone = (PayloadAttributeImpl)super.clone();
        if (this.payload != null) {
            clone.payload = (Payload)this.payload.clone();
        }
        return clone;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof PayloadAttribute) {
            PayloadAttributeImpl o = (PayloadAttributeImpl)other;
            if (o.payload == null || this.payload == null) {
                return o.payload == null && this.payload == null;
            }
            return o.payload.equals(this.payload);
        }
        return false;
    }

    public int hashCode() {
        return this.payload == null ? 0 : this.payload.hashCode();
    }

    public void copyTo(AttributeImpl target) {
        PayloadAttribute t = (PayloadAttribute)((Object)target);
        t.setPayload(this.payload == null ? null : (Payload)this.payload.clone());
    }
}

