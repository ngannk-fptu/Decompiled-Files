/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.support;

import java.lang.reflect.Method;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;

public class DataBindingPropertyAccessor
extends ReflectivePropertyAccessor {
    private DataBindingPropertyAccessor(boolean allowWrite) {
        super(allowWrite);
    }

    @Override
    protected boolean isCandidateForProperty(Method method, Class<?> targetClass) {
        Class<?> clazz = method.getDeclaringClass();
        return clazz != Object.class && clazz != Class.class && !ClassLoader.class.isAssignableFrom(targetClass);
    }

    public static DataBindingPropertyAccessor forReadOnlyAccess() {
        return new DataBindingPropertyAccessor(false);
    }

    public static DataBindingPropertyAccessor forReadWriteAccess() {
        return new DataBindingPropertyAccessor(true);
    }
}

