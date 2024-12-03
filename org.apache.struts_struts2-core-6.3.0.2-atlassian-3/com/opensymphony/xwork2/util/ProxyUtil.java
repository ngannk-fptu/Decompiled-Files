/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.reflect.ConstructorUtils
 *  org.apache.commons.lang3.reflect.FieldUtils
 *  org.apache.commons.lang3.reflect.MethodUtils
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ProxyUtil {
    private static final String SPRING_ADVISED_CLASS_NAME = "org.springframework.aop.framework.Advised";
    private static final String SPRING_SPRINGPROXY_CLASS_NAME = "org.springframework.aop.SpringProxy";
    private static final String SPRING_SINGLETONTARGETSOURCE_CLASS_NAME = "org.springframework.aop.target.SingletonTargetSource";
    private static final String SPRING_TARGETCLASSAWARE_CLASS_NAME = "org.springframework.aop.TargetClassAware";
    private static final Map<Class<?>, Boolean> isProxyCache = new ConcurrentHashMap(256);
    private static final Map<Member, Boolean> isProxyMemberCache = new ConcurrentHashMap<Member, Boolean>(256);

    public static Class<?> ultimateTargetClass(Object candidate) {
        Class<?> result = null;
        if (ProxyUtil.isSpringAopProxy(candidate)) {
            result = ProxyUtil.springUltimateTargetClass(candidate);
        }
        if (result == null) {
            result = candidate.getClass();
        }
        return result;
    }

    public static boolean isProxy(Object object) {
        Class<?> clazz = object.getClass();
        Boolean flag = isProxyCache.get(clazz);
        if (flag != null) {
            return flag;
        }
        boolean isProxy = ProxyUtil.isSpringAopProxy(object);
        isProxyCache.put(clazz, isProxy);
        return isProxy;
    }

    public static boolean isProxyMember(Member member, Object object) {
        if (!Modifier.isStatic(member.getModifiers()) && !ProxyUtil.isProxy(object)) {
            return false;
        }
        Boolean flag = isProxyMemberCache.get(member);
        if (flag != null) {
            return flag;
        }
        boolean isProxyMember = ProxyUtil.isSpringProxyMember(member);
        isProxyMemberCache.put(member, isProxyMember);
        return isProxyMember;
    }

    private static Class<?> springUltimateTargetClass(Object candidate) {
        Object current = candidate;
        Class<?> result = null;
        while (null != current && ProxyUtil.implementsInterface(current.getClass(), SPRING_TARGETCLASSAWARE_CLASS_NAME)) {
            try {
                result = (Class<?>)MethodUtils.invokeMethod((Object)current, (String)"getTargetClass");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            current = ProxyUtil.getSingletonTarget(current);
        }
        if (result == null) {
            Class<?> clazz = candidate.getClass();
            result = ProxyUtil.isCglibProxyClass(clazz) ? clazz.getSuperclass() : candidate.getClass();
        }
        return result;
    }

    private static boolean isSpringAopProxy(Object object) {
        Class<?> clazz = object.getClass();
        return ProxyUtil.implementsInterface(clazz, SPRING_SPRINGPROXY_CLASS_NAME) && (Proxy.isProxyClass(clazz) || ProxyUtil.isCglibProxyClass(clazz));
    }

    private static boolean isSpringProxyMember(Member member) {
        try {
            Class clazz = ClassLoaderUtil.loadClass(SPRING_ADVISED_CLASS_NAME, ProxyUtil.class);
            if (ProxyUtil.hasMember(clazz, member)) {
                return true;
            }
            clazz = ClassLoaderUtil.loadClass(SPRING_TARGETCLASSAWARE_CLASS_NAME, ProxyUtil.class);
            if (ProxyUtil.hasMember(clazz, member)) {
                return true;
            }
            clazz = ClassLoaderUtil.loadClass(SPRING_SPRINGPROXY_CLASS_NAME, ProxyUtil.class);
            if (ProxyUtil.hasMember(clazz, member)) {
                return true;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return false;
    }

    private static Object getSingletonTarget(Object candidate) {
        try {
            Object targetSource;
            if (ProxyUtil.implementsInterface(candidate.getClass(), SPRING_ADVISED_CLASS_NAME) && ProxyUtil.implementsInterface((targetSource = MethodUtils.invokeMethod((Object)candidate, (String)"getTargetSource")).getClass(), SPRING_SINGLETONTARGETSOURCE_CLASS_NAME)) {
                return MethodUtils.invokeMethod((Object)targetSource, (String)"getTarget");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

    private static boolean implementsInterface(Class<?> clazz, String ifaceClassName) {
        try {
            Class ifaceClass = ClassLoaderUtil.loadClass(ifaceClassName, ProxyUtil.class);
            return ifaceClass.isAssignableFrom(clazz);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean hasMember(Class<?> clazz, Member member) {
        if (member instanceof Method) {
            return null != MethodUtils.getMatchingMethod(clazz, (String)member.getName(), (Class[])((Method)member).getParameterTypes());
        }
        if (member instanceof Field) {
            return null != FieldUtils.getField(clazz, (String)member.getName(), (boolean)true);
        }
        if (member instanceof Constructor) {
            return null != ConstructorUtils.getMatchingAccessibleConstructor(clazz, (Class[])((Constructor)member).getParameterTypes());
        }
        return false;
    }
}

