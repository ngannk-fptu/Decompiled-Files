/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityExpander;

public interface EntityExpanderResolver {
    public boolean hasExpander(Class<?> var1);

    public <T> EntityExpander<T> getExpander(Class<? extends T> var1);
}

