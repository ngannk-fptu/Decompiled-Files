/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class BridgeMethodResolver {
    public static Method findBridgedMethod(Method bridgeMethod) {
        Method[] methods;
        if (!bridgeMethod.isBridge()) {
            return bridgeMethod;
        }
        ArrayList<Method> candidateMethods = new ArrayList<Method>();
        for (Method candidateMethod : methods = ReflectionUtils.getAllDeclaredMethods(bridgeMethod.getDeclaringClass())) {
            if (!BridgeMethodResolver.isBridgedCandidateFor(candidateMethod, bridgeMethod)) continue;
            candidateMethods.add(candidateMethod);
        }
        if (candidateMethods.size() == 1) {
            return (Method)candidateMethods.get(0);
        }
        Method bridgedMethod = BridgeMethodResolver.searchCandidates(candidateMethods, bridgeMethod);
        if (bridgedMethod != null) {
            return bridgedMethod;
        }
        return bridgeMethod;
    }

    private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
        return !candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) && candidateMethod.getName().equals(bridgeMethod.getName()) && candidateMethod.getParameterCount() == bridgeMethod.getParameterCount();
    }

    @Nullable
    private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
        if (candidateMethods.isEmpty()) {
            return null;
        }
        Method previousMethod = null;
        boolean sameSig = true;
        for (Method candidateMethod : candidateMethods) {
            if (BridgeMethodResolver.isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
                return candidateMethod;
            }
            if (previousMethod != null) {
                sameSig = sameSig && Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
            }
            previousMethod = candidateMethod;
        }
        return sameSig ? candidateMethods.get(0) : null;
    }

    static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
        if (BridgeMethodResolver.isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
            return true;
        }
        Method method = BridgeMethodResolver.findGenericDeclaration(bridgeMethod);
        return method != null && BridgeMethodResolver.isResolvedTypeMatch(method, candidateMethod, declaringClass);
    }

    private static boolean isResolvedTypeMatch(Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
        Class<?>[] candidateParameters;
        Type[] genericParameters = genericMethod.getGenericParameterTypes();
        if (genericParameters.length != (candidateParameters = candidateMethod.getParameterTypes()).length) {
            return false;
        }
        for (int i = 0; i < candidateParameters.length; ++i) {
            ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
            Class<?> candidateParameter = candidateParameters[i];
            if (candidateParameter.isArray() && !candidateParameter.getComponentType().equals(genericParameter.getComponentType().resolve(Object.class))) {
                return false;
            }
            if (candidateParameter.equals(genericParameter.resolve(Object.class))) continue;
            return false;
        }
        return true;
    }

    @Nullable
    private static Method findGenericDeclaration(Method bridgeMethod) {
        for (Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass(); superclass != null && Object.class != superclass; superclass = superclass.getSuperclass()) {
            Method method = BridgeMethodResolver.searchForMatch(superclass, bridgeMethod);
            if (method == null || method.isBridge()) continue;
            return method;
        }
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
        return BridgeMethodResolver.searchInterfaces(interfaces, bridgeMethod);
    }

    @Nullable
    private static Method searchInterfaces(Class<?>[] interfaces, Method bridgeMethod) {
        for (Class<?> ifc : interfaces) {
            Method method = BridgeMethodResolver.searchForMatch(ifc, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
            method = BridgeMethodResolver.searchInterfaces(ifc.getInterfaces(), bridgeMethod);
            if (method == null) continue;
            return method;
        }
        return null;
    }

    @Nullable
    private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
        try {
            return type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static boolean isVisibilityBridgeMethodPair(Method bridgeMethod, Method bridgedMethod) {
        if (bridgeMethod == bridgedMethod) {
            return true;
        }
        return Arrays.equals(bridgeMethod.getParameterTypes(), bridgedMethod.getParameterTypes()) && bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType());
    }
}

