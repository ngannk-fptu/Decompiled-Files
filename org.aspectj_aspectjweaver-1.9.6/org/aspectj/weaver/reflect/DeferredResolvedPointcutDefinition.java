/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.UnresolvedType;

public class DeferredResolvedPointcutDefinition
extends ResolvedPointcutDefinition {
    public DeferredResolvedPointcutDefinition(UnresolvedType declaringType, int modifiers, String name, UnresolvedType[] parameterTypes) {
        super(declaringType, modifiers, name, parameterTypes, UnresolvedType.VOID, null);
    }
}

