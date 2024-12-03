/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.util.AttributeImpl;
import java.io.Serializable;

public class PositionIncrementAttributeImpl
extends AttributeImpl
implements PositionIncrementAttribute,
Cloneable,
Serializable {
    private int positionIncrement = 1;

    public void setPositionIncrement(int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: got " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }

    public int getPositionIncrement() {
        return this.positionIncrement;
    }

    public void clear() {
        this.positionIncrement = 1;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof PositionIncrementAttributeImpl) {
            PositionIncrementAttributeImpl _other = (PositionIncrementAttributeImpl)other;
            return this.positionIncrement == _other.positionIncrement;
        }
        return false;
    }

    public int hashCode() {
        return this.positionIncrement;
    }

    public void copyTo(AttributeImpl target) {
        PositionIncrementAttribute t = (PositionIncrementAttribute)((Object)target);
        t.setPositionIncrement(this.positionIncrement);
    }
}

