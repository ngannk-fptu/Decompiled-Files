/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

public class ResolvedRecursiveType
extends ResolvedType {
    protected ResolvedType _referencedType;

    public ResolvedRecursiveType(Class<?> erased, TypeBindings bindings) {
        super(erased, bindings);
    }

    @Override
    public boolean canCreateSubtypes() {
        return this._referencedType.canCreateSubtypes();
    }

    public void setReference(ResolvedType ref) {
        if (this._referencedType != null) {
            throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
        }
        this._referencedType = ref;
    }

    @Override
    public ResolvedType getParentClass() {
        return null;
    }

    @Override
    public ResolvedType getSelfReferencedType() {
        return this._referencedType;
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
        return this._erasedType.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this._erasedType.getModifiers());
    }

    @Override
    public boolean isArray() {
        return this._erasedType.isArray();
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public List<RawField> getMemberFields() {
        return this._referencedType.getMemberFields();
    }

    @Override
    public List<RawField> getStaticFields() {
        return this._referencedType.getStaticFields();
    }

    @Override
    public List<RawMethod> getStaticMethods() {
        return this._referencedType.getStaticMethods();
    }

    @Override
    public List<RawMethod> getMemberMethods() {
        return this._referencedType.getMemberMethods();
    }

    @Override
    public List<RawConstructor> getConstructors() {
        return this._referencedType.getConstructors();
    }

    @Override
    public StringBuilder appendSignature(StringBuilder sb) {
        return this.appendErasedSignature(sb);
    }

    @Override
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        return this._appendErasedClassSignature(sb);
    }

    @Override
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        return this._appendClassDescription(sb);
    }

    @Override
    public StringBuilder appendFullDescription(StringBuilder sb) {
        return this.appendBriefDescription(sb);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        ResolvedRecursiveType other = (ResolvedRecursiveType)o;
        if (this._referencedType == null) {
            return other._referencedType == null;
        }
        return this._referencedType.equals(other._referencedType);
    }
}

