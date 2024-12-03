/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.util.AttributeImpl;

public class FlagsAttributeImpl
extends AttributeImpl
implements FlagsAttribute,
Cloneable {
    private int flags = 0;

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public void clear() {
        this.flags = 0;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof FlagsAttributeImpl) {
            return ((FlagsAttributeImpl)other).flags == this.flags;
        }
        return false;
    }

    public int hashCode() {
        return this.flags;
    }

    @Override
    public void copyTo(AttributeImpl target) {
        FlagsAttribute t = (FlagsAttribute)((Object)target);
        t.setFlags(this.flags);
    }
}

