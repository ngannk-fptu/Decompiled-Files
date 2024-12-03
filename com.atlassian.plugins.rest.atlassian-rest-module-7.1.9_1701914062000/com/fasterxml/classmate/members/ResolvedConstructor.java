/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.members;

import com.fasterxml.classmate.Annotations;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import java.lang.reflect.Constructor;

public final class ResolvedConstructor
extends ResolvedParameterizedMember<Constructor<?>> {
    public ResolvedConstructor(ResolvedType context, Annotations ann, Constructor<?> constructor, ResolvedType[] argumentTypes) {
        super(context, ann, constructor, null, argumentTypes);
    }
}

