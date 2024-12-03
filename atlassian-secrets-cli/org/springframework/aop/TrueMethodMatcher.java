/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;

class TrueMethodMatcher
implements MethodMatcher,
Serializable {
    public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

    private TrueMethodMatcher() {
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, @Nullable Class<?> targetClass) {
        return true;
    }

    @Override
    public boolean matches(Method method, @Nullable Class<?> targetClass, Object ... args) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "MethodMatcher.TRUE";
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

