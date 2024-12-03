/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
    public static Object invokeMethodOnObject(Object obj, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ReflectionUtil.invokeMethodOnObject(obj, methodName, null, null);
    }

    public static Object invokeMethodOnObject(Object obj, String methodName, Class[] paramTypes, Object[] params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = obj.getClass().getMethod(methodName, paramTypes);
        return method.invoke(obj, params);
    }
}

