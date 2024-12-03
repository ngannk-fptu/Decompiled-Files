/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.Expander;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import java.util.Objects;

public abstract class AbstractAnnotationEntityExpanderResolver
implements EntityExpanderResolver {
    @Override
    public boolean hasExpander(Class<?> type) {
        return Objects.requireNonNull(type).getAnnotation(Expander.class) != null;
    }

    @Override
    public final <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        Expander expander = Objects.requireNonNull(type).getAnnotation(Expander.class);
        return expander != null ? this.getEntityExpander(expander) : null;
    }

    protected abstract EntityExpander<?> getEntityExpander(Expander var1);
}

