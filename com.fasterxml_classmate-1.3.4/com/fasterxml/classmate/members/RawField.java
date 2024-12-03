/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.RawMember;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class RawField
extends RawMember {
    protected final Field _field;
    private final int _hashCode;

    public RawField(ResolvedType context, Field field) {
        super(context);
        this._field = field;
        this._hashCode = this._field == null ? 0 : this._field.hashCode();
    }

    @Override
    public Field getRawMember() {
        return this._field;
    }

    public boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(this.getModifiers());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        RawField other = (RawField)o;
        return other._field == this._field;
    }

    @Override
    public int hashCode() {
        return this._hashCode;
    }
}

