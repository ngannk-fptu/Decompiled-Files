/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;

public interface GenericSignatureInformationProvider {
    public UnresolvedType[] getGenericParameterTypes(ReflectionBasedResolvedMemberImpl var1);

    public UnresolvedType getGenericReturnType(ReflectionBasedResolvedMemberImpl var1);

    public boolean isBridge(ReflectionBasedResolvedMemberImpl var1);

    public boolean isVarArgs(ReflectionBasedResolvedMemberImpl var1);

    public boolean isSynthetic(ReflectionBasedResolvedMemberImpl var1);
}

