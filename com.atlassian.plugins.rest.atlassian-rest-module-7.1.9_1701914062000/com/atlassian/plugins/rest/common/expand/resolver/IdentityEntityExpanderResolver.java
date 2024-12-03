/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public class IdentityEntityExpanderResolver
implements EntityExpanderResolver {
    private static final EntityExpander IDENTITY = new IdentityExpander();

    @Override
    public boolean hasExpander(Class<?> type) {
        return true;
    }

    @Override
    public <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        return IDENTITY;
    }

    private static class IdentityExpander
    extends AbstractRecursiveEntityExpander<Object> {
        private IdentityExpander() {
        }

        @Override
        protected Object expandInternal(Object entity) {
            return entity;
        }
    }
}

