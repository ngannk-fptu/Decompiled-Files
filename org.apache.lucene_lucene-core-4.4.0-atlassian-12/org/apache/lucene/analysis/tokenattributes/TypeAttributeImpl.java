/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeImpl;

public class TypeAttributeImpl
extends AttributeImpl
implements TypeAttribute,
Cloneable {
    private String type;

    public TypeAttributeImpl() {
        this("word");
    }

    public TypeAttributeImpl(String type) {
        this.type = type;
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void clear() {
        this.type = "word";
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof TypeAttributeImpl) {
            TypeAttributeImpl o = (TypeAttributeImpl)other;
            return this.type == null ? o.type == null : this.type.equals(o.type);
        }
        return false;
    }

    public int hashCode() {
        return this.type == null ? 0 : this.type.hashCode();
    }

    @Override
    public void copyTo(AttributeImpl target) {
        TypeAttribute t = (TypeAttribute)((Object)target);
        t.setType(this.type);
    }
}

