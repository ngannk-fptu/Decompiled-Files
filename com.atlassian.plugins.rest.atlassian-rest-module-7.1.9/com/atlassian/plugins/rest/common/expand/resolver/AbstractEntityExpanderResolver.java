/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import java.util.Objects;

abstract class AbstractEntityExpanderResolver
implements EntityExpanderResolver {
    AbstractEntityExpanderResolver() {
    }

    public final <T> boolean hasExpander(T instance) {
        return this.hasExpander((T)Objects.requireNonNull(instance).getClass());
    }

    public final <T> EntityExpander<T> getExpander(T instance) {
        return this.getExpander((T)Objects.requireNonNull(instance).getClass());
    }
}

