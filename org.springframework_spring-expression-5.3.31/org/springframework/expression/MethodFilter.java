/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

@FunctionalInterface
public interface MethodFilter {
    public List<Method> filter(List<Method> var1);
}

