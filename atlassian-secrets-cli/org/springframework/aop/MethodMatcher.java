/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.aop.TrueMethodMatcher;
import org.springframework.lang.Nullable;

public interface MethodMatcher {
    public static final MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

    public boolean matches(Method var1, @Nullable Class<?> var2);

    public boolean isRuntime();

    public boolean matches(Method var1, @Nullable Class<?> var2, Object ... var3);
}

