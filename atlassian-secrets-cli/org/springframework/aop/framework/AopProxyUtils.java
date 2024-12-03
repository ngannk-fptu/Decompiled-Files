/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.core.DecoratingProxy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class AopProxyUtils {
    @Nullable
    public static Object getSingletonTarget(Object candidate) {
        TargetSource targetSource;
        if (candidate instanceof Advised && (targetSource = ((Advised)candidate).getTargetSource()) instanceof SingletonTargetSource) {
            return ((SingletonTargetSource)targetSource).getTarget();
        }
        return null;
    }

    public static Class<?> ultimateTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Object current = candidate;
        Class<?> result = null;
        while (current instanceof TargetClassAware) {
            result = ((TargetClassAware)current).getTargetClass();
            current = AopProxyUtils.getSingletonTarget(current);
        }
        if (result == null) {
            result = AopUtils.isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
        }
        return result;
    }

    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised) {
        return AopProxyUtils.completeProxiedInterfaces(advised, false);
    }

    static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised, boolean decoratingProxy) {
        Class<?> targetClass;
        Class<?>[] specifiedInterfaces = advised.getProxiedInterfaces();
        if (specifiedInterfaces.length == 0 && (targetClass = advised.getTargetClass()) != null) {
            if (targetClass.isInterface()) {
                advised.setInterfaces(targetClass);
            } else if (Proxy.isProxyClass(targetClass)) {
                advised.setInterfaces(targetClass.getInterfaces());
            }
            specifiedInterfaces = advised.getProxiedInterfaces();
        }
        boolean addSpringProxy = !advised.isInterfaceProxied(SpringProxy.class);
        boolean addAdvised = !advised.isOpaque() && !advised.isInterfaceProxied(Advised.class);
        boolean addDecoratingProxy = decoratingProxy && !advised.isInterfaceProxied(DecoratingProxy.class);
        int nonUserIfcCount = 0;
        if (addSpringProxy) {
            ++nonUserIfcCount;
        }
        if (addAdvised) {
            ++nonUserIfcCount;
        }
        if (addDecoratingProxy) {
            ++nonUserIfcCount;
        }
        Class[] proxiedInterfaces = new Class[specifiedInterfaces.length + nonUserIfcCount];
        System.arraycopy(specifiedInterfaces, 0, proxiedInterfaces, 0, specifiedInterfaces.length);
        int index = specifiedInterfaces.length;
        if (addSpringProxy) {
            proxiedInterfaces[index] = SpringProxy.class;
            ++index;
        }
        if (addAdvised) {
            proxiedInterfaces[index] = Advised.class;
            ++index;
        }
        if (addDecoratingProxy) {
            proxiedInterfaces[index] = DecoratingProxy.class;
        }
        return proxiedInterfaces;
    }

    public static Class<?>[] proxiedUserInterfaces(Object proxy) {
        Class<?>[] proxyInterfaces = proxy.getClass().getInterfaces();
        int nonUserIfcCount = 0;
        if (proxy instanceof SpringProxy) {
            ++nonUserIfcCount;
        }
        if (proxy instanceof Advised) {
            ++nonUserIfcCount;
        }
        if (proxy instanceof DecoratingProxy) {
            ++nonUserIfcCount;
        }
        Object[] userInterfaces = new Class[proxyInterfaces.length - nonUserIfcCount];
        System.arraycopy(proxyInterfaces, 0, userInterfaces, 0, userInterfaces.length);
        Assert.notEmpty(userInterfaces, "JDK proxy must implement one or more interfaces");
        return userInterfaces;
    }

    public static boolean equalsInProxy(AdvisedSupport a, AdvisedSupport b) {
        return a == b || AopProxyUtils.equalsProxiedInterfaces(a, b) && AopProxyUtils.equalsAdvisors(a, b) && a.getTargetSource().equals(b.getTargetSource());
    }

    public static boolean equalsProxiedInterfaces(AdvisedSupport a, AdvisedSupport b) {
        return Arrays.equals(a.getProxiedInterfaces(), b.getProxiedInterfaces());
    }

    public static boolean equalsAdvisors(AdvisedSupport a, AdvisedSupport b) {
        return Arrays.equals(a.getAdvisors(), b.getAdvisors());
    }

    static Object[] adaptArgumentsIfNecessary(Method method, @Nullable Object[] arguments) {
        Object varargArray;
        int varargIndex;
        Class<?> varargType;
        Class<?>[] paramTypes;
        if (ObjectUtils.isEmpty(arguments)) {
            return new Object[0];
        }
        if (method.isVarArgs() && (paramTypes = method.getParameterTypes()).length == arguments.length && (varargType = paramTypes[varargIndex = paramTypes.length - 1]).isArray() && (varargArray = arguments[varargIndex]) instanceof Object[] && !varargType.isInstance(varargArray)) {
            Object[] newArguments = new Object[arguments.length];
            System.arraycopy(arguments, 0, newArguments, 0, varargIndex);
            Class<?> targetElementType = varargType.getComponentType();
            int varargLength = Array.getLength(varargArray);
            Object newVarargArray = Array.newInstance(targetElementType, varargLength);
            System.arraycopy(varargArray, 0, newVarargArray, 0, varargLength);
            newArguments[varargIndex] = newVarargArray;
            return newArguments;
        }
        return arguments;
    }
}

