/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public abstract class RawMember {
    protected final ResolvedType _declaringType;

    protected RawMember(ResolvedType context) {
        this._declaringType = context;
    }

    public final ResolvedType getDeclaringType() {
        return this._declaringType;
    }

    public abstract Member getRawMember();

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

    public Annotation[] getAnnotations() {
        return ((AnnotatedElement)((Object)this.getRawMember())).getAnnotations();
    }

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public String toString() {
        return this.getName();
    }

    protected final int getModifiers() {
        return this.getRawMember().getModifiers();
    }
}

