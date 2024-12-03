/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;

public final class HierarchicType {
    protected final boolean _isMixin;
    protected final ResolvedType _type;
    protected final int _priority;

    public HierarchicType(ResolvedType type, boolean mixin, int priority) {
        this._type = type;
        this._isMixin = mixin;
        this._priority = priority;
    }

    public ResolvedType getType() {
        return this._type;
    }

    public Class<?> getErasedType() {
        return this._type.getErasedType();
    }

    public boolean isMixin() {
        return this._isMixin;
    }

    public int getPriority() {
        return this._priority;
    }

    public String toString() {
        return this._type.toString();
    }

    public int hashCode() {
        return this._type.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        HierarchicType other = (HierarchicType)o;
        return this._type.equals(other._type);
    }
}

