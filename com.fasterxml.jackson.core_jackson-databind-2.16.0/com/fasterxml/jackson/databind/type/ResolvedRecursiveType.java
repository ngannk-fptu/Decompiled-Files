/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.IdentityEqualityType;
import com.fasterxml.jackson.databind.type.TypeBindings;

public class ResolvedRecursiveType
extends IdentityEqualityType {
    private static final long serialVersionUID = 1L;
    protected JavaType _referencedType;

    public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
        super(erasedType, bindings, null, null, 0, null, null, false);
    }

    public void setReference(JavaType ref) {
        if (this._referencedType != null) {
            throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
        }
        this._referencedType = ref;
    }

    @Override
    public JavaType getSuperClass() {
        if (this._referencedType != null) {
            return this._referencedType.getSuperClass();
        }
        return super.getSuperClass();
    }

    public JavaType getSelfReferencedType() {
        return this._referencedType;
    }

    @Override
    public TypeBindings getBindings() {
        if (this._referencedType != null) {
            return this._referencedType.getBindings();
        }
        return super.getBindings();
    }

    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        if (this._referencedType != null) {
            return this._referencedType.getErasedSignature(sb);
        }
        return sb.append("?");
    }

    @Override
    public StringBuilder getErasedSignature(StringBuilder sb) {
        if (this._referencedType != null) {
            return this._referencedType.getErasedSignature(sb);
        }
        return sb;
    }

    @Override
    public JavaType withContentType(JavaType contentType) {
        return this;
    }

    @Override
    public JavaType withTypeHandler(Object h) {
        return this;
    }

    @Override
    public JavaType withContentTypeHandler(Object h) {
        return this;
    }

    @Override
    public JavaType withValueHandler(Object h) {
        return this;
    }

    @Override
    public JavaType withContentValueHandler(Object h) {
        return this;
    }

    @Override
    public JavaType withStaticTyping() {
        return this;
    }

    @Override
    public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return null;
    }

    @Override
    public boolean isContainerType() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
        if (this._referencedType == null) {
            sb.append("UNRESOLVED");
        } else {
            sb.append(this._referencedType.getRawClass().getName());
        }
        return sb.toString();
    }
}

