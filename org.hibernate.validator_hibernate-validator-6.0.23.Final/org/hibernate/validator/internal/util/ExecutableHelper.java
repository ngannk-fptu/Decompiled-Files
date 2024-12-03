/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.Filter
 *  com.fasterxml.classmate.MemberResolver
 *  com.fasterxml.classmate.ResolvedType
 *  com.fasterxml.classmate.ResolvedTypeWithMembers
 *  com.fasterxml.classmate.TypeResolver
 *  com.fasterxml.classmate.members.RawMethod
 *  com.fasterxml.classmate.members.ResolvedMethod
 */
package org.hibernate.validator.internal.util;

import com.fasterxml.classmate.Filter;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetResolvedMemberMethods;

public final class ExecutableHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final TypeResolver typeResolver;

    public ExecutableHelper(TypeResolutionHelper typeResolutionHelper) {
        this.typeResolver = typeResolutionHelper.getTypeResolver();
    }

    public boolean overrides(Method subTypeMethod, Method superTypeMethod) {
        Contracts.assertValueNotNull(subTypeMethod, "subTypeMethod");
        Contracts.assertValueNotNull(superTypeMethod, "superTypeMethod");
        if (subTypeMethod.equals(superTypeMethod)) {
            return false;
        }
        if (!subTypeMethod.getName().equals(superTypeMethod.getName())) {
            return false;
        }
        if (subTypeMethod.getParameterTypes().length != superTypeMethod.getParameterTypes().length) {
            return false;
        }
        if (!superTypeMethod.getDeclaringClass().isAssignableFrom(subTypeMethod.getDeclaringClass())) {
            return false;
        }
        if (Modifier.isStatic(superTypeMethod.getModifiers()) || Modifier.isStatic(subTypeMethod.getModifiers())) {
            return false;
        }
        if (subTypeMethod.isBridge()) {
            return false;
        }
        if (Modifier.isPrivate(superTypeMethod.getModifiers())) {
            return false;
        }
        if (!(Modifier.isPublic(superTypeMethod.getModifiers()) || Modifier.isProtected(superTypeMethod.getModifiers()) || superTypeMethod.getDeclaringClass().getPackage().equals(subTypeMethod.getDeclaringClass().getPackage()))) {
            return false;
        }
        return this.instanceMethodParametersResolveToSameTypes(subTypeMethod, superTypeMethod);
    }

    public static String getSimpleName(Executable executable) {
        return executable instanceof Constructor ? executable.getDeclaringClass().getSimpleName() : executable.getName();
    }

    public static String getSignature(Executable executable) {
        return ExecutableHelper.getSignature(ExecutableHelper.getSimpleName(executable), executable.getParameterTypes());
    }

    public static String getSignature(String name, Class<?>[] parameterTypes) {
        return Stream.of(parameterTypes).map(t -> t.getName()).collect(Collectors.joining(",", name + "(", ")"));
    }

    public static String getExecutableAsString(String name, Class<?> ... parameterTypes) {
        return Stream.of(parameterTypes).map(t -> t.getSimpleName()).collect(Collectors.joining(", ", name + "(", ")"));
    }

    public static ElementType getElementType(Executable executable) {
        return executable instanceof Constructor ? ElementType.CONSTRUCTOR : ElementType.METHOD;
    }

    private boolean instanceMethodParametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod) {
        if (subTypeMethod.getParameterTypes().length == 0) {
            return true;
        }
        ResolvedType resolvedSubType = this.typeResolver.resolve(subTypeMethod.getDeclaringClass(), new Type[0]);
        MemberResolver memberResolver = new MemberResolver(this.typeResolver);
        memberResolver.setMethodFilter((Filter)new SimpleMethodFilter(subTypeMethod, superTypeMethod));
        ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedSubType, null, null);
        ResolvedMethod[] resolvedMethods = this.run(GetResolvedMemberMethods.action(typeWithMembers));
        if (resolvedMethods.length == 1) {
            return true;
        }
        try {
            for (int i = 0; i < resolvedMethods[0].getArgumentCount(); ++i) {
                if (resolvedMethods[0].getArgumentType(i).equals((Object)resolvedMethods[1].getArgumentType(i))) continue;
                return false;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            LOG.debug("Error in ExecutableHelper#instanceMethodParametersResolveToSameTypes comparing " + subTypeMethod + " with " + superTypeMethod);
        }
        return true;
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    private static class SimpleMethodFilter
    implements Filter<RawMethod> {
        private final Method method1;
        private final Method method2;

        private SimpleMethodFilter(Method method1, Method method2) {
            this.method1 = method1;
            this.method2 = method2;
        }

        public boolean include(RawMethod element) {
            return element.getRawMember().equals(this.method1) || element.getRawMember().equals(this.method2);
        }
    }
}

