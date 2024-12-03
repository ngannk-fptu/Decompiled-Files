/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.aop.TrueClassFilter;

@FunctionalInterface
public interface ClassFilter {
    public static final ClassFilter TRUE = TrueClassFilter.INSTANCE;

    public boolean matches(Class<?> var1);
}

