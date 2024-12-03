/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.Advised
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.util.Assert
 */
package org.springframework.security.util;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.util.Assert;

public final class MethodInvocationUtils {
    private MethodInvocationUtils() {
    }

    public static MethodInvocation create(Object object, String methodName, Object ... args) {
        Advised a;
        Assert.notNull((Object)object, (String)"Object required");
        Class[] classArgs = null;
        if (args != null) {
            classArgs = new Class[args.length];
            for (int i = 0; i < args.length; ++i) {
                classArgs[i] = args[i].getClass();
            }
        }
        Class target = AopUtils.getTargetClass((Object)object);
        if (object instanceof Advised && !(a = (Advised)object).isProxyTargetClass()) {
            Class[] possibleInterfaces;
            for (Class possibleInterface : possibleInterfaces = a.getProxiedInterfaces()) {
                try {
                    possibleInterface.getMethod(methodName, classArgs);
                    target = possibleInterface;
                    break;
                }
                catch (Exception exception) {
                }
            }
        }
        return MethodInvocationUtils.createFromClass(object, target, methodName, classArgs, args);
    }

    public static MethodInvocation createFromClass(Class<?> clazz, String methodName) {
        MethodInvocation invocation = MethodInvocationUtils.createFromClass(null, clazz, methodName, null, null);
        if (invocation == null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) continue;
                Assert.isTrue((invocation == null ? 1 : 0) != 0, () -> "The class " + clazz + " has more than one method named '" + methodName + "'");
                invocation = new SimpleMethodInvocation(null, method, new Object[0]);
            }
        }
        return invocation;
    }

    public static MethodInvocation createFromClass(Object targetObject, Class<?> clazz, String methodName, Class<?>[] classArgs, Object[] args) {
        Assert.notNull(clazz, (String)"Class required");
        Assert.hasText((String)methodName, (String)"MethodName required");
        try {
            Method method = clazz.getMethod(methodName, classArgs);
            return new SimpleMethodInvocation(targetObject, method, args);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }
}

