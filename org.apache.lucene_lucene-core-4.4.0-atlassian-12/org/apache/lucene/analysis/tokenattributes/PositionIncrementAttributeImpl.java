/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeImpl;

public class PositionIncrementAttributeImpl
extends AttributeImpl
implements PositionIncrementAttribute,
Cloneable {
    private int positionIncrement = 1;

    @Override
    public void setPositionIncrement(int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: got " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }

    @Override
    public int getPositionIncrement() {
        return this.positionIncrement;
    }

    @Override
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

    @Override
    public void copyTo(AttributeImpl target) {
        PositionIncrementAttribute t = (PositionIncrementAttribute)((Object)target);
        t.setPositionIncrement(this.positionIncrement);
    }
}

