/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.core.MethodIntrospector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.aop.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class AopUtils {
    public static boolean isAopProxy(@Nullable Object object) {
        return object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) || object.getClass().getName().contains("$$"));
    }

    public static boolean isJdkDynamicProxy(@Nullable Object object) {
        return object instanceof SpringProxy && Proxy.isProxyClass(object.getClass());
    }

    public static boolean isCglibProxy(@Nullable Object object) {
        return object instanceof SpringProxy && object.getClass().getName().contains("$$");
    }

    public static Class<?> getTargetClass(Object candidate) {
        Assert.notNull((Object)candidate, (String)"Candidate object must not be null");
        Class<?> result = null;
        if (candidate instanceof TargetClassAware) {
            result = ((TargetClassAware)candidate).getTargetClass();
        }
        if (result == null) {
            result = AopUtils.isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
        }
        return result;
    }

    public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
        if (targetType == null) {
            return method;
        }
        Method methodToUse = MethodIntrospector.selectInvocableMethod((Method)method, targetType);
        if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) && SpringProxy.class.isAssignableFrom(targetType)) {
            throw new IllegalStateException(String.format("Need to invoke method '%s' found on proxy for target class '%s' but cannot be delegated to target bean. Switch its visibility to package or protected.", method.getName(), method.getDeclaringClass().getSimpleName()));
        }
        return methodToUse;
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        return ReflectionUtils.isEqualsMethod((Method)method);
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return ReflectionUtils.isHashCodeMethod((Method)method);
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return ReflectionUtils.isToStringMethod((Method)method);
    }

    public static boolean isFinalizeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("finalize") && method.getParameterCount() == 0;
    }

    public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
        Class specificTargetClass = targetClass != null ? ClassUtils.getUserClass(targetClass) : null;
        Method resolvedMethod = ClassUtils.getMostSpecificMethod((Method)method, (Class)specificTargetClass);
        return BridgeMethodResolver.findBridgedMethod((Method)resolvedMethod);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        return AopUtils.canApply(pc, targetClass, false);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
        Assert.notNull((Object)pc, (String)"Pointcut must not be null");
        if (!pc.getClassFilter().matches(targetClass)) {
            return false;
        }
        MethodMatcher methodMatcher = pc.getMethodMatcher();
        if (methodMatcher == MethodMatcher.TRUE) {
            return true;
        }
        IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
        if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
            introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher)methodMatcher;
        }
        LinkedHashSet<Class> classes = new LinkedHashSet<Class>();
        if (!Proxy.isProxyClass(targetClass)) {
            classes.add(ClassUtils.getUserClass(targetClass));
        }
        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        for (Class clazz : classes) {
            Method[] methods;
            for (Method method : methods = ReflectionUtils.getAllDeclaredMethods((Class)clazz)) {
                if (!(introductionAwareMethodMatcher != null ? introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) : methodMatcher.matches(method, targetClass))) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass) {
        return AopUtils.canApply(advisor, targetClass, false);
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
        if (advisor instanceof IntroductionAdvisor) {
            return ((IntroductionAdvisor)advisor).getClassFilter().matches(targetClass);
        }
        if (advisor instanceof PointcutAdvisor) {
            PointcutAdvisor pca = (PointcutAdvisor)advisor;
            return AopUtils.canApply(pca.getPointcut(), targetClass, hasIntroductions);
        }
        return true;
    }

    public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        ArrayList<Advisor> eligibleAdvisors = new ArrayList<Advisor>();
        for (Advisor candidate : candidateAdvisors) {
            if (!(candidate instanceof IntroductionAdvisor) || !AopUtils.canApply(candidate, clazz)) continue;
            eligibleAdvisors.add(candidate);
        }
        boolean hasIntroductions = !eligibleAdvisors.isEmpty();
        for (Advisor candidate : candidateAdvisors) {
            if (candidate instanceof IntroductionAdvisor || !AopUtils.canApply(candidate, clazz, hasIntroductions)) continue;
            eligibleAdvisors.add(candidate);
        }
        return eligibleAdvisors;
    }

    @Nullable
    public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args) throws Throwable {
        try {
            ReflectionUtils.makeAccessible((Method)method);
            return method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
        catch (IllegalArgumentException ex) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" + method + "] on target [" + target + "]", ex);
        }
        catch (IllegalAccessException ex) {
            throw new AopInvocationException("Could not access method [" + method + "]", ex);
        }
    }
}

