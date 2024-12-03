/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.RawMember;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.reflect.Constructor;

public final class RawConstructor
extends RawMember {
    protected final Constructor<?> _constructor;
    protected final int _hashCode;

    public RawConstructor(ResolvedType context, Constructor<?> constructor) {
        super(context);
        this._constructor = constructor;
        this._hashCode = this._constructor == null ? 0 : this._constructor.hashCode();
    }

    public MethodKey createKey() {
        String name = "<init>";
        Class<?>[] argTypes = this._constructor.getParameterTypes();
        return new MethodKey(name, argTypes);
    }

    @Override
    public Constructor<?> getRawMember() {
        return this._constructor;
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
        RawConstructor other = (RawConstructor)o;
        return other._constructor == this._constructor;
    }
}

