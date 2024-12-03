/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate;

import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ResolvedType
implements Type {
    public static final ResolvedType[] NO_TYPES = new ResolvedType[0];
    protected static final RawConstructor[] NO_CONSTRUCTORS = new RawConstructor[0];
    protected static final RawField[] NO_FIELDS = new RawField[0];
    protected static final RawMethod[] NO_METHODS = new RawMethod[0];
    protected final Class<?> _erasedType;
    protected final TypeBindings _typeBindings;

    protected ResolvedType(Class<?> cls, TypeBindings bindings) {
        this._erasedType = cls;
        this._typeBindings = bindings == null ? TypeBindings.emptyBindings() : bindings;
    }

    public abstract boolean canCreateSubtypes();

    public final boolean canCreateSubtype(Class<?> subtype) {
        return this.canCreateSubtypes() && this._erasedType.isAssignableFrom(subtype);
    }

    public Class<?> getErasedType() {
        return this._erasedType;
    }

    public abstract ResolvedType getParentClass();

    public abstract ResolvedType getSelfReferencedType();

    public abstract ResolvedType getArrayElementType();

    public abstract List<ResolvedType> getImplementedInterfaces();

    public List<ResolvedType> getTypeParameters() {
        return this._typeBindings.getTypeParameters();
    }

    public TypeBindings getTypeBindings() {
        return this._typeBindings;
    }

    public List<ResolvedType> typeParametersFor(Class<?> erasedSupertype) {
        ResolvedType type = this.findSupertype(erasedSupertype);
        if (type != null) {
            return type.getTypeParameters();
        }
        return null;
    }

    public ResolvedType findSupertype(Class<?> erasedSupertype) {
        ResolvedType type;
        ResolvedType pc;
        if (erasedSupertype == this._erasedType) {
            return this;
        }
        if (erasedSupertype.isInterface()) {
            for (ResolvedType it : this.getImplementedInterfaces()) {
                ResolvedType type2 = it.findSupertype(erasedSupertype);
                if (type2 == null) continue;
                return type2;
            }
        }
        if ((pc = this.getParentClass()) != null && (type = pc.findSupertype(erasedSupertype)) != null) {
            return type;
        }
        return null;
    }

    public abstract boolean isInterface();

    public final boolean isConcrete() {
        return !this.isAbstract();
    }

    public abstract boolean isAbstract();

    public abstract boolean isArray();

    public abstract boolean isPrimitive();

    public final boolean isInstanceOf(Class<?> type) {
        return type.isAssignableFrom(this._erasedType);
    }

    public List<RawConstructor> getConstructors() {
        return Collections.emptyList();
    }

    public List<RawField> getMemberFields() {
        return Collections.emptyList();
    }

    public List<RawMethod> getMemberMethods() {
        return Collections.emptyList();
    }

    public List<RawField> getStaticFields() {
        return Collections.emptyList();
    }

    public List<RawMethod> getStaticMethods() {
        return Collections.emptyList();
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        return this.appendSignature(sb).toString();
    }

    public String getErasedSignature() {
        StringBuilder sb = new StringBuilder();
        return this.appendErasedSignature(sb).toString();
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        return this.appendFullDescription(sb).toString();
    }

    public String getBriefDescription() {
        StringBuilder sb = new StringBuilder();
        return this.appendBriefDescription(sb).toString();
    }

    public abstract StringBuilder appendBriefDescription(StringBuilder var1);

    public abstract StringBuilder appendFullDescription(StringBuilder var1);

    public abstract StringBuilder appendSignature(StringBuilder var1);

    public abstract StringBuilder appendErasedSignature(StringBuilder var1);

    public String toString() {
        return this.getBriefDescription();
    }

    public int hashCode() {
        return this._erasedType.getName().hashCode() + this._typeBindings.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        ResolvedType other = (ResolvedType)o;
        if (other._erasedType != this._erasedType) {
            return false;
        }
        return this._typeBindings.equals(other._typeBindings);
    }

    protected StringBuilder _appendClassSignature(StringBuilder sb) {
        sb.append('L');
        sb = this._appendClassName(sb);
        int count = this._typeBindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                sb = this._typeBindings.getBoundType(i).appendErasedSignature(sb);
            }
            sb.append('>');
        }
        sb.append(';');
        return sb;
    }

    protected StringBuilder _appendErasedClassSignature(StringBuilder sb) {
        sb.append('L');
        sb = this._appendClassName(sb);
        sb.append(';');
        return sb;
    }

    protected StringBuilder _appendClassDescription(StringBuilder sb) {
        sb.append(this._erasedType.getName());
        int count = this._typeBindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb = this._typeBindings.getBoundType(i).appendBriefDescription(sb);
            }
            sb.append('>');
        }
        return sb;
    }

    protected StringBuilder _appendClassName(StringBuilder sb) {
        String name = this._erasedType.getName();
        int len = name.length();
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            if (c == '.') {
                c = '/';
            }
            sb.append(c);
        }
        return sb;
    }

    protected RawField[] _getFields(boolean statics) {
        ArrayList<RawField> fields = new ArrayList<RawField>();
        for (Field f : this._erasedType.getDeclaredFields()) {
            if (f.isSynthetic() || Modifier.isStatic(f.getModifiers()) != statics) continue;
            fields.add(new RawField(this, f));
        }
        if (fields.isEmpty()) {
            return NO_FIELDS;
        }
        return fields.toArray(new RawField[fields.size()]);
    }

    protected RawMethod[] _getMethods(boolean statics) {
        ArrayList<RawMethod> methods = new ArrayList<RawMethod>();
        for (Method m : this._erasedType.getDeclaredMethods()) {
            if (m.isSynthetic() || Modifier.isStatic(m.getModifiers()) != statics) continue;
            methods.add(new RawMethod(this, m));
        }
        if (methods.isEmpty()) {
            return NO_METHODS;
        }
        return methods.toArray(new RawMethod[methods.size()]);
    }

    protected RawConstructor[] _getConstructors() {
        ArrayList<RawConstructor> ctors = new ArrayList<RawConstructor>();
        for (Constructor<?> c : this._erasedType.getDeclaredConstructors()) {
            if (c.isSynthetic()) continue;
            ctors.add(new RawConstructor(this, c));
        }
        if (ctors.isEmpty()) {
            return NO_CONSTRUCTORS;
        }
        return ctors.toArray(new RawConstructor[ctors.size()]);
    }
}

