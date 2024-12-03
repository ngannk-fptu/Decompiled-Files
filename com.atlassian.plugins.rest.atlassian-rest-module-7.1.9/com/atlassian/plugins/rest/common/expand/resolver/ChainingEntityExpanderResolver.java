/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import java.util.List;
import java.util.Objects;

public class ChainingEntityExpanderResolver
implements EntityExpanderResolver {
    private final List<EntityExpanderResolver> resolvers;

    public ChainingEntityExpanderResolver(List<EntityExpanderResolver> resolvers) {
        for (EntityExpanderResolver resolver : Objects.requireNonNull(resolvers)) {
            if (resolver != null) continue;
            throw new NullPointerException("Resolver items cannot be null!");
        }
        this.resolvers = resolvers;
    }

    @Override
    public boolean hasExpander(Class<?> type) {
        Objects.requireNonNull(type);
        for (EntityExpanderResolver resolver : this.resolvers) {
            if (!resolver.hasExpander(type)) continue;
            return true;
        }
        return false;
    }

    @Override
    public <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        for (EntityExpanderResolver resolver : this.resolvers) {
            EntityExpander<? extends T> expander = resolver.getExpander(type);
            if (expander == null) continue;
            return expander;
        }
        return null;
    }
}

