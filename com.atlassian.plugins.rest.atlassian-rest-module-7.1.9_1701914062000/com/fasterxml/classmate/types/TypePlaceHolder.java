/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.Collections;
import java.util.List;

public class TypePlaceHolder
extends ResolvedType {
    protected final int _ordinal;
    protected ResolvedType _actualType;

    public TypePlaceHolder(int ordinal) {
        super(Object.class, TypeBindings.emptyBindings());
        this._ordinal = ordinal;
    }

    @Override
    public boolean canCreateSubtypes() {
        return false;
    }

    public ResolvedType actualType() {
        return this._actualType;
    }

    public void actualType(ResolvedType t) {
        this._actualType = t;
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
    public ResolvedType getArrayElementType() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public StringBuilder appendSignature(StringBuilder sb) {
        return this._appendClassSignature(sb);
    }

    @Override
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        return this._appendErasedClassSignature(sb);
    }

    @Override
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        sb.append('<').append(this._ordinal).append('>');
        return sb;
    }

    @Override
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return this.appendBriefDescription(sb);
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }
}

