/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResolvedInterfaceType
extends ResolvedType {
    protected final ResolvedType[] _superInterfaces;
    protected RawField[] _constantFields;
    protected RawMethod[] _memberMethods;

    public ResolvedInterfaceType(Class<?> erased, TypeBindings bindings, ResolvedType[] superInterfaces) {
        super(erased, bindings);
        this._superInterfaces = superInterfaces == null ? NO_TYPES : superInterfaces;
    }

    @Override
    public boolean canCreateSubtypes() {
        return true;
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
        return this._superInterfaces.length == 0 ? Collections.emptyList() : Arrays.asList(this._superInterfaces);
    }

    @Override
    public ResolvedType getArrayElementType() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return true;
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
    public synchronized List<RawField> getStaticFields() {
        if (this._constantFields == null) {
            this._constantFields = this._getFields(true);
        }
        if (this._constantFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._constantFields);
    }

    @Override
    public synchronized List<RawMethod> getMemberMethods() {
        if (this._memberMethods == null) {
            this._memberMethods = this._getMethods(false);
        }
        if (this._memberMethods.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._memberMethods);
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
        return this._appendClassDescription(sb);
    }

    @Override
    public StringBuilder appendFullDescription(StringBuilder sb) {
        sb = this._appendClassDescription(sb);
        int count = this._superInterfaces.length;
        if (count > 0) {
            sb.append(" extends ");
            for (int i = 0; i < count; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                sb = this._superInterfaces[i].appendBriefDescription(sb);
            }
        }
        return sb;
    }
}

