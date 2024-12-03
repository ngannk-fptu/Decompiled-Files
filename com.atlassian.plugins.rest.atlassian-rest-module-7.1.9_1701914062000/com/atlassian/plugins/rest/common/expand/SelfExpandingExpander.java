/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.SelfExpanding;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public class SelfExpandingExpander
extends AbstractRecursiveEntityExpander<SelfExpanding> {
    @Override
    protected SelfExpanding expandInternal(SelfExpanding selfExpandingObject) {
        selfExpandingObject.expand();
        return selfExpandingObject;
    }

    public static class Resolver
    implements EntityExpanderResolver {
        private static final SelfExpandingExpander expander = new SelfExpandingExpander();

        public <T> boolean hasExpander(T instance) {
            return this.hasExpander(instance.getClass());
        }

        @Override
        public boolean hasExpander(Class<?> aClass) {
            return SelfExpanding.class.isAssignableFrom(aClass);
        }

        public <T> EntityExpander<T> getExpander(T instance) {
            return this.getExpander(instance.getClass());
        }

        @Override
        public <T> EntityExpander<T> getExpander(Class<? extends T> aClass) {
            return this.hasExpander(aClass) ? expander : null;
        }
    }
}

