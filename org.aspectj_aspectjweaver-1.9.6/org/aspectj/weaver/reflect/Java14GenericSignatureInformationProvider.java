/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.reflect.GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;

public class Java14GenericSignatureInformationProvider
implements GenericSignatureInformationProvider {
    @Override
    public UnresolvedType[] getGenericParameterTypes(ReflectionBasedResolvedMemberImpl resolvedMember) {
        return resolvedMember.getParameterTypes();
    }

    @Override
    public UnresolvedType getGenericReturnType(ReflectionBasedResolvedMemberImpl resolvedMember) {
        return resolvedMember.getReturnType();
    }

    @Override
    public boolean isBridge(ReflectionBasedResolvedMemberImpl resolvedMember) {
        return false;
    }

    @Override
    public boolean isVarArgs(ReflectionBasedResolvedMemberImpl resolvedMember) {
        return false;
    }

    @Override
    public boolean isSynthetic(ReflectionBasedResolvedMemberImpl resolvedMember) {
        return false;
    }
}

