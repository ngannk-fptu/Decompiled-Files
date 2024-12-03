/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedMember;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ResolvedField
extends ResolvedMember<Field>
implements Comparable<ResolvedField> {
    public ResolvedField(ResolvedType context, Annotations ann, Field field, ResolvedType type) {
        super(context, ann, field, type);
    }

    public boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(this.getModifiers());
    }

    @Override
    public int compareTo(ResolvedField other) {
        return this.getName().compareTo(other.getName());
    }
}

