/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;

public interface IntroductionAwareMethodMatcher
extends MethodMatcher {
    public boolean matches(Method var1, @Nullable Class<?> var2, boolean var3);
}

