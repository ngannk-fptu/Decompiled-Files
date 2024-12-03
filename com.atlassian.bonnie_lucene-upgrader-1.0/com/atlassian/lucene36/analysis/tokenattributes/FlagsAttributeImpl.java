/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.FlagsAttribute;
import com.atlassian.lucene36.util.AttributeImpl;
import java.io.Serializable;

public class FlagsAttributeImpl
extends AttributeImpl
implements FlagsAttribute,
Cloneable,
Serializable {
    private int flags = 0;

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

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

    public void copyTo(AttributeImpl target) {
        FlagsAttribute t = (FlagsAttribute)((Object)target);
        t.setFlags(this.flags);
    }
}

