/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.PositionLengthAttribute;
import com.atlassian.lucene36.util.AttributeImpl;

public class PositionLengthAttributeImpl
extends AttributeImpl
implements PositionLengthAttribute,
Cloneable {
    private int positionLength = 1;

    public void setPositionLength(int positionLength) {
        if (positionLength < 1) {
            throw new IllegalArgumentException("Position length must be 1 or greater: got " + positionLength);
        }
        this.positionLength = positionLength;
    }

    public int getPositionLength() {
        return this.positionLength;
    }

    public void clear() {
        this.positionLength = 1;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof PositionLengthAttributeImpl) {
            PositionLengthAttributeImpl _other = (PositionLengthAttributeImpl)other;
            return this.positionLength == _other.positionLength;
        }
        return false;
    }

    public int hashCode() {
        return this.positionLength;
    }

    public void copyTo(AttributeImpl target) {
        PositionLengthAttribute t = (PositionLengthAttribute)((Object)target);
        t.setPositionLength(this.positionLength);
    }
}

