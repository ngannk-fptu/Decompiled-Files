/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResolvedPrimitiveType
extends ResolvedType {
    private static final ResolvedPrimitiveType VOID = new ResolvedPrimitiveType(Void.TYPE, 'V', "void");
    protected final String _signature;
    protected final String _description;

    protected ResolvedPrimitiveType(Class<?> erased, char sig, String desc) {
        super(erased, TypeBindings.emptyBindings());
        this._signature = String.valueOf(sig);
        this._description = desc;
    }

    public static List<ResolvedPrimitiveType> all() {
        ArrayList<ResolvedPrimitiveType> all = new ArrayList<ResolvedPrimitiveType>();
        all.add(new ResolvedPrimitiveType(Boolean.TYPE, 'Z', "boolean"));
        all.add(new ResolvedPrimitiveType(Byte.TYPE, 'B', "byte"));
        all.add(new ResolvedPrimitiveType(Short.TYPE, 'S', "short"));
        all.add(new ResolvedPrimitiveType(Character.TYPE, 'C', "char"));
        all.add(new ResolvedPrimitiveType(Integer.TYPE, 'I', "int"));
        all.add(new ResolvedPrimitiveType(Long.TYPE, 'J', "long"));
        all.add(new ResolvedPrimitiveType(Float.TYPE, 'F', "float"));
        all.add(new ResolvedPrimitiveType(Double.TYPE, 'D', "double"));
        return all;
    }

    public static ResolvedPrimitiveType voidType() {
        return VOID;
    }

    @Override
    public boolean canCreateSubtypes() {
        return false;
    }

    @Override
    public ResolvedType getSelfReferencedType() {
        return null;
    }

    @Override
    public ResolvedType getParentClass() {
        return null;
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
        return null;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public List<ResolvedType> getImplementedInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public String getSignature() {
        return this._signature;
    }

    @Override
    public String getErasedSignature() {
        return this._signature;
    }

    @Override
    public String getFullDescription() {
        return this._description;
    }

    @Override
    public StringBuilder appendSignature(StringBuilder sb) {
        sb.append(this._signature);
        return sb;
    }

    @Override
    public StringBuilder appendErasedSignature(StringBuilder sb) {
        sb.append(this._signature);
        return sb;
    }

    @Override
    public StringBuilder appendFullDescription(StringBuilder sb) {
        sb.append(this._description);
        return sb;
    }

    @Override
    public StringBuilder appendBriefDescription(StringBuilder sb) {
        sb.append(this._description);
        return sb;
    }
}

