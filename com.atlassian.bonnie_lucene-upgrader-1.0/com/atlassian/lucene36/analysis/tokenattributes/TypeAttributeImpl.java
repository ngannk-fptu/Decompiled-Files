/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import com.atlassian.lucene36.util.AttributeImpl;
import java.io.Serializable;

public class TypeAttributeImpl
extends AttributeImpl
implements TypeAttribute,
Cloneable,
Serializable {
    private String type;

    public TypeAttributeImpl() {
        this("word");
    }

    public TypeAttributeImpl(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public void copyTo(AttributeImpl target) {
        TypeAttribute t = (TypeAttribute)((Object)target);
        t.setType(this.type);
    }
}

