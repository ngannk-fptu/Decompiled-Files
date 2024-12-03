/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

class TruePointcut
implements Pointcut,
Serializable {
    public static final TruePointcut INSTANCE = new TruePointcut();

    private TruePointcut() {
    }

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return MethodMatcher.TRUE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "Pointcut.TRUE";
    }
}

