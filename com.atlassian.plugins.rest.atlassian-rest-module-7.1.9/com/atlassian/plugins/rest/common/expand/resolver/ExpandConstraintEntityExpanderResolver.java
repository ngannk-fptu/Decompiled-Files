/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandConstraint;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.ExpandException;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ExpandConstraintEntityExpanderResolver
implements EntityExpanderResolver {
    @Override
    public boolean hasExpander(Class<?> type) {
        return this.getConstrainMethod(Objects.requireNonNull(type)) != null;
    }

    @Override
    public <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        Method method = this.getConstrainMethod(Objects.requireNonNull(type));
        return method != null ? new ExpandConstraintEntityExpander(method) : null;
    }

    private <T> Method getConstrainMethod(Class<? extends T> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getAnnotation(ExpandConstraint.class) == null || method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(Indexes.class)) continue;
            return method;
        }
        return null;
    }

    private static class ExpandConstraintEntityExpander
    implements EntityExpander<Object> {
        private final Method method;

        public ExpandConstraintEntityExpander(Method method) {
            this.method = Objects.requireNonNull(method);
        }

        @Override
        public Object expand(ExpandContext<Object> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
            Object entity = context.getEntity();
            try {
                this.method.invoke(entity, context.getEntityExpandParameter().getIndexes(context.getExpandable()));
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExpandException(e);
            }
            entityCrawler.crawl(entity, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()), expanderResolver);
            return entity;
        }
    }
}

