/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;

public abstract class StaticMethodMatcher
implements MethodMatcher {
    @Override
    public final boolean isRuntime() {
        return false;
    }

    @Override
    public final boolean matches(Method method, Class<?> targetClass, Object ... args) {
        throw new UnsupportedOperationException("Illegal MethodMatcher usage");
    }
}

