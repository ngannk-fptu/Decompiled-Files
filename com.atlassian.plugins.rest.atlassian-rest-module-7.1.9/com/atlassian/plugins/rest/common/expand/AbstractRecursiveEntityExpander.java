/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;

public abstract class AbstractRecursiveEntityExpander<T>
implements EntityExpander<T> {
    @Override
    public final T expand(ExpandContext<T> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
        T expandedEntity = this.expandInternal(context.getEntity());
        if (!context.getEntityExpandParameter().isEmpty()) {
            entityCrawler.crawl(expandedEntity, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()), expanderResolver);
        }
        return expandedEntity;
    }

    protected abstract T expandInternal(T var1);
}

