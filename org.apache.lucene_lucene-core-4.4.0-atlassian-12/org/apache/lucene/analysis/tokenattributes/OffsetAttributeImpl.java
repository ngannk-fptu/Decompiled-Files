/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeImpl;

public class OffsetAttributeImpl
extends AttributeImpl
implements OffsetAttribute,
Cloneable {
    private int startOffset;
    private int endOffset;

    @Override
    public int startOffset() {
        return this.startOffset;
    }

    @Override
    public void setOffset(int startOffset, int endOffset) {
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException("startOffset must be non-negative, and endOffset must be >= startOffset, startOffset=" + startOffset + ",endOffset=" + endOffset);
        }
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public int endOffset() {
        return this.endOffset;
    }

    @Override
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

    @Override
    public void copyTo(AttributeImpl target) {
        OffsetAttribute t = (OffsetAttribute)((Object)target);
        t.setOffset(this.startOffset, this.endOffset);
    }
}

