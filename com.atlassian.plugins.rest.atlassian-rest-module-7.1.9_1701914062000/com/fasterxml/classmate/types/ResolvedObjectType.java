/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.types.ResolvedRecursiveType;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResolvedObjectType
extends ResolvedType {
    protected final ResolvedType _superClass;
    protected final ResolvedType[] _superInterfaces;
    protected final int _modifiers;
    protected RawConstructor[] _constructors;
    protected RawField[] _memberFields;
    protected RawField[] _staticFields;
    protected RawMethod[] _memberMethods;
    protected RawMethod[] _staticMethods;

    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedType superClass, List<ResolvedType> interfaces) {
        this(erased, bindings, superClass, interfaces == null || interfaces.isEmpty() ? NO_TYPES : interfaces.toArray(new ResolvedType[interfaces.size()]));
    }

    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedType superClass, ResolvedType[] interfaces) {
        super(erased, bindings);
        if (superClass != null && !(superClass instanceof ResolvedObjectType) && !(superClass instanceof ResolvedRecursiveType)) {
            throw new IllegalArgumentException("Unexpected parent type for " + erased.getName() + ": " + superClass.getClass().getName());
        }
        this._superClass = superClass;
        this._superInterfaces = interfaces == null ? NO_TYPES : interfaces;
        this._modifiers = erased.getModifiers();
    }

    @Deprecated
    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedObjectType superClass, List<ResolvedType> interfaces) {
        this(erased, bindings, (ResolvedType)superClass, interfaces);
    }

    @Deprecated
    public ResolvedObjectType(Class<?> erased, TypeBindings bindings, ResolvedObjectType superClass, ResolvedType[] interfaces) {
        this(erased, bindings, (ResolvedType)superClass, interfaces);
    }

    public static ResolvedObjectType create(Class<?> erased, TypeBindings bindings, ResolvedType superClass, List<ResolvedType> interfaces) {
        return new ResolvedObjectType(erased, bindings, superClass, interfaces);
    }

    @Override
    public boolean canCreateSubtypes() {
        return true;
    }

    @Override
    public ResolvedObjectType getParentClass() {
        if (this._superClass == null) {
            return null;
        }
        if (this._superClass instanceof ResolvedObjectType) {
            return (ResolvedObjectType)this._superClass;
        }
        ResolvedType rt = ((ResolvedRecursiveType)this._superClass).getSelfReferencedType();
        if (!(rt instanceof ResolvedObjectType)) {
            throw new IllegalStateException("Internal error: self-referential parent type (" + this._superClass + ") does not resolve into proper ResolvedObjectType, but instead to: " + rt);
        }
        return (ResolvedObjectType)rt;
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
    public final ResolvedType getArrayElementType() {
        return null;
    }

    @Override
    public final boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this._modifiers);
    }

    @Override
    public final boolean isArray() {
        return false;
    }

    @Override
    public final boolean isPrimitive() {
        return false;
    }

    @Override
    public synchronized List<RawField> getMemberFields() {
        if (this._memberFields == null) {
            this._memberFields = this._getFields(false);
        }
        if (this._memberFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._memberFields);
    }

    @Override
    public synchronized List<RawField> getStaticFields() {
        if (this._staticFields == null) {
            this._staticFields = this._getFields(true);
        }
        if (this._staticFields.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._staticFields);
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
    public synchronized List<RawMethod> getStaticMethods() {
        if (this._staticMethods == null) {
            this._staticMethods = this._getMethods(true);
        }
        if (this._staticMethods.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._staticMethods);
    }

    @Override
    public List<RawConstructor> getConstructors() {
        if (this._constructors == null) {
            this._constructors = this._getConstructors();
        }
        if (this._constructors.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(this._constructors);
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
        int count;
        sb = this._appendClassDescription(sb);
        if (this._superClass != null) {
            sb.append(" extends ");
            sb = this._superClass.appendBriefDescription(sb);
        }
        if ((count = this._superInterfaces.length) > 0) {
            sb.append(" implements ");
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

