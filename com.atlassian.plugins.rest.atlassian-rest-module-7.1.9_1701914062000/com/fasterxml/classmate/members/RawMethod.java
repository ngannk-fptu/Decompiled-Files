/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.RawMember;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class RawMethod
extends RawMember {
    protected final Method _method;
    protected final int _hashCode;

    public RawMethod(ResolvedType context, Method method) {
        super(context);
        this._method = method;
        this._hashCode = this._method == null ? 0 : this._method.hashCode();
    }

    @Override
    public Method getRawMember() {
        return this._method;
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

    public MethodKey createKey() {
        String name = this._method.getName();
        Class<?>[] argTypes = this._method.getParameterTypes();
        return new MethodKey(name, argTypes);
    }

    @Override
    public int hashCode() {
        return this._hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        RawMethod other = (RawMethod)o;
        return other._method == this._method;
    }
}

