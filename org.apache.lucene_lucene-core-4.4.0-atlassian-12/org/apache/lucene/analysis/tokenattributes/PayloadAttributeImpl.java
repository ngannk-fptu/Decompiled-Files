/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.BytesRef;

public class PayloadAttributeImpl
extends AttributeImpl
implements PayloadAttribute,
Cloneable {
    private BytesRef payload;

    public PayloadAttributeImpl() {
    }

    public PayloadAttributeImpl(BytesRef payload) {
        this.payload = payload;
    }

    @Override
    public BytesRef getPayload() {
        return this.payload;
    }

    @Override
    public void setPayload(BytesRef payload) {
        this.payload = payload;
    }

    @Override
    public void clear() {
        this.payload = null;
    }

    @Override
    public PayloadAttributeImpl clone() {
        PayloadAttributeImpl clone = (PayloadAttributeImpl)super.clone();
        if (this.payload != null) {
            clone.payload = this.payload.clone();
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

    @Override
    public void copyTo(AttributeImpl target) {
        PayloadAttribute t = (PayloadAttribute)((Object)target);
        t.setPayload(this.payload == null ? null : this.payload.clone());
    }
}

