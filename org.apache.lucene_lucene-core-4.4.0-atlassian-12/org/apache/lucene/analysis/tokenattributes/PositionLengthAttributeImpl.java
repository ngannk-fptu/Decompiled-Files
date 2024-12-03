/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.AttributeImpl;

public class PositionLengthAttributeImpl
extends AttributeImpl
implements PositionLengthAttribute,
Cloneable {
    private int positionLength = 1;

    @Override
    public void setPositionLength(int positionLength) {
        if (positionLength < 1) {
            throw new IllegalArgumentException("Position length must be 1 or greater: got " + positionLength);
        }
        this.positionLength = positionLength;
    }

    @Override
    public int getPositionLength() {
        return this.positionLength;
    }

    @Override
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

    @Override
    public void copyTo(AttributeImpl target) {
        PositionLengthAttribute t = (PositionLengthAttribute)((Object)target);
        t.setPositionLength(this.positionLength);
    }
}

