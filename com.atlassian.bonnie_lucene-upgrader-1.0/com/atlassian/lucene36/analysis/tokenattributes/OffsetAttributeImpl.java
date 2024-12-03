/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.util.AttributeImpl;
import java.io.Serializable;

public class OffsetAttributeImpl
extends AttributeImpl
implements OffsetAttribute,
Cloneable,
Serializable {
    private int startOffset;
    private int endOffset;

    public int startOffset() {
        return this.startOffset;
    }

    public void setOffset(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public int endOffset() {
        return this.endOffset;
    }

    public void clear() {
        this.startOffset = 0;
        this.endOffset = 0;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof OffsetAttributeImpl) {
            OffsetAttributeImpl o = (OffsetAttributeImpl)other;
            return o.startOffset == this.startOffset && o.endOffset == this.endOffset;
        }
        return false;
    }

    public int hashCode() {
        int code = this.startOffset;
        code = code * 31 + this.endOffset;
        return code;
    }

    public void copyTo(AttributeImpl target) {
        OffsetAttribute t = (OffsetAttribute)((Object)target);
        t.setOffset(this.startOffset, this.endOffset);
    }
}

