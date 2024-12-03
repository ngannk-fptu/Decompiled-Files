/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public final class OptionalWrapper {
    public static <T> Optional<T> fugueToJavaOptional(Object object, String methodName, Class<T> resultType, Object ... params) {
        Class[] a = (Class[])Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, a);
            Object supposedOptional = method.invoke(object, params);
            return Optional.ofNullable(resultType.cast(OptionalWrapper.get(supposedOptional)));
        }
        catch (ReflectiveOperationException roe) {
            throw new OptionalAccessException(roe);
        }
    }

    private static Object get(Object supposedOptional) throws ReflectiveOperationException {
        Method isDefined = supposedOptional.getClass().getMethod("getOrNull", new Class[0]);
        return isDefined.invoke(supposedOptional, new Object[0]);
    }

    private static class OptionalAccessException
    extends RuntimeException {
        public OptionalAccessException(Exception cause) {
            super("Can't get optional value", cause);
        }
    }
}

