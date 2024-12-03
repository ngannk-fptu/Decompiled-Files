/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ResolvedMethod
extends ResolvedParameterizedMember<Method>
implements Comparable<ResolvedMethod> {
    public ResolvedMethod(ResolvedType context, Annotations ann, Method method, ResolvedType returnType, ResolvedType[] argumentTypes) {
        super(context, ann, method, returnType, argumentTypes);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }

    public boolean isStrict() {
        return Modifier.isStrict(this.getModifiers());
    }

    public boolean isNative() {
        return Modifier.isNative(this.getModifiers());
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.getModifiers());
    }

    public ResolvedType getReturnType() {
        return this.getType();
    }

    @Override
    public int compareTo(ResolvedMethod other) {
        int diff = this.getName().compareTo(other.getName());
        if (diff == 0) {
            diff = this.getArgumentCount() - other.getArgumentCount();
        }
        return diff;
    }
}

