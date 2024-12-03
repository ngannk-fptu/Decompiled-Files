/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.Collections;
import java.util.List;

public final class ResolvedArrayType
extends ResolvedType {
    protected final ResolvedType _elementType;

    public ResolvedArrayType(Class<?> erased, TypeBindings bindings, ResolvedType elementType) {
        super(erased, bindings);
        this._elementType = elementType;
    }

    @Override
    public boolean canCreateSubtypes() {
        return false;
    }

    @Override
    public ResolvedType getParentClass() {
        return null;
    }

    @Override
    public ResolvedType getSelfReferencedType() {
        return null;
    }

    @Override
    public List<ResolvedType> getImplementedInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public ResolvedType getArrayElementType() {
        return this._elementType;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public StringBuilder appendSignature(StringBuilder sb) {
        sb.append('[');
        return this._elementType.appendSignature(sb);
    }

    @Override
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        sb.append('[');
        return this._elementType.appendErasedSignature(sb);
    }

    @Override
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        sb = this._elementType.appendBriefDescription(sb);
        sb.append("[]");
        return sb;
    }

    @Override
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return this.appendBriefDescription(sb);
    }
}

