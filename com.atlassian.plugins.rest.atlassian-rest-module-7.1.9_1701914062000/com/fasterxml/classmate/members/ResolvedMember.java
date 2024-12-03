/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public abstract class ResolvedMember<T extends Member> {
    protected final ResolvedType _declaringType;
    protected final Annotations _annotations;
    protected final T _member;
    protected final ResolvedType _type;
    protected final int _hashCode;

    protected ResolvedMember(ResolvedType context, Annotations ann, T member, ResolvedType type) {
        this._declaringType = context;
        this._annotations = ann;
        this._member = member;
        this._type = type;
        this._hashCode = this._member == null ? 0 : this._member.hashCode();
    }

    public void applyOverride(Annotation override) {
        this._annotations.add(override);
    }

    public void applyOverrides(Annotations overrides) {
        this._annotations.addAll(overrides);
    }

    public void applyDefault(Annotation override) {
        this._annotations.addAsDefault(override);
    }

    public <A extends Annotation> A get(Class<A> cls) {
        return this._annotations.get(cls);
    }

    public Annotations getAnnotations() {
        return this._annotations;
    }

    public final ResolvedType getDeclaringType() {
        return this._declaringType;
    }

    public ResolvedType getType() {
        return this._type;
    }

    public T getRawMember() {
        return this._member;
    }

    public String getName() {
        return this.getRawMember().getName();
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }

    public String toString() {
        return this.getName();
    }

    protected final int getModifiers() {
        return this.getRawMember().getModifiers();
    }

    public int hashCode() {
        return this._hashCode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        ResolvedMember other = (ResolvedMember)o;
        return other._member == this._member;
    }
}

