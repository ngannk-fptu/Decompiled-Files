/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.TruePointcut;

public interface Pointcut {
    public static final Pointcut TRUE = TruePointcut.INSTANCE;

    public ClassFilter getClassFilter();

    public MethodMatcher getMethodMatcher();
}

