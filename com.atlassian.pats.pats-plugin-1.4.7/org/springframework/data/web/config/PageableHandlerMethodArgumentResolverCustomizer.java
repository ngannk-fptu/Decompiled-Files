/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.web.config;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

@FunctionalInterface
public interface PageableHandlerMethodArgumentResolverCustomizer {
    public void customize(PageableHandlerMethodArgumentResolver var1);
}

