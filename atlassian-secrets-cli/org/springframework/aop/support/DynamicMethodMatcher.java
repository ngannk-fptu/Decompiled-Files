/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;

public abstract class DynamicMethodMatcher
implements MethodMatcher {
    @Override
    public final boolean isRuntime() {
        return true;
    }

    @Override
    public boolean matches(Method method, @Nullable Class<?> targetClass) {
        return true;
    }
}

