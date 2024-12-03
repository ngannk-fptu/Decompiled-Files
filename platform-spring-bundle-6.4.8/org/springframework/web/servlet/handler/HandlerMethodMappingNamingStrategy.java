/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {
    public String getName(HandlerMethod var1, T var2);
}

