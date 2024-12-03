/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.reflect.GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.JavaLangTypeToResolvedTypeConverter;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;

public class Java15GenericSignatureInformationProvider
implements GenericSignatureInformationProvider {
    private final World world;

    public Java15GenericSignatureInformationProvider(World forWorld) {
        this.world = forWorld;
    }

    @Override
    public UnresolvedType[] getGenericParameterTypes(ReflectionBasedResolvedMemberImpl resolvedMember) {
        JavaLangTypeToResolvedTypeConverter typeConverter = new JavaLangTypeToResolvedTypeConverter(this.world);
        Type[] pTypes = new Type[]{};
        Member member = resolvedMember.getMember();
        if (member instanceof Method) {
            pTypes = ((Method)member).getGenericParameterTypes();
        } else if (member instanceof Constructor) {
            pTypes = ((Constructor)member).getGenericParameterTypes();
        }
        return typeConverter.fromTypes(pTypes);
    }

    @Override
    public UnresolvedType getGenericReturnType(ReflectionBasedResolvedMemberImpl resolvedMember) {
        JavaLangTypeToResolvedTypeConverter typeConverter = new JavaLangTypeToResolvedTypeConverter(this.world);
        Member member = resolvedMember.getMember();
        if (member instanceof Field) {
            return typeConverter.fromType(((Field)member).getGenericType());
        }
        if (member instanceof Method) {
            return typeConverter.fromType(((Method)member).getGenericReturnType());
        }
        if (member instanceof Constructor) {
            return typeConverter.fromType(((Constructor)member).getDeclaringClass());
        }
        throw new IllegalStateException("unexpected member type: " + member);
    }

    @Override
    public boolean isBridge(ReflectionBasedResolvedMemberImpl resolvedMember) {
        Member member = resolvedMember.getMember();
        if (member instanceof Method) {
            return ((Method)member).isBridge();
        }
        return false;
    }

    @Override
    public boolean isVarArgs(ReflectionBasedResolvedMemberImpl resolvedMember) {
        Member member = resolvedMember.getMember();
        if (member instanceof Method) {
            return ((Method)member).isVarArgs();
        }
        if (member instanceof Constructor) {
            return ((Constructor)member).isVarArgs();
        }
        return false;
    }

    @Override
    public boolean isSynthetic(ReflectionBasedResolvedMemberImpl resolvedMember) {
        Member member = resolvedMember.getMember();
        return member.isSynthetic();
    }
}

