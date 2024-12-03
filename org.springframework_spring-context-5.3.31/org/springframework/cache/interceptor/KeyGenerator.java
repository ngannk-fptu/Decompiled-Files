/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
public interface KeyGenerator {
    public Object generate(Object var1, Method var2, Object ... var3);
}

