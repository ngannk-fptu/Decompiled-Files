/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.method.HandlerMethod
 */
package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {
    public String getName(HandlerMethod var1, T var2);
}

