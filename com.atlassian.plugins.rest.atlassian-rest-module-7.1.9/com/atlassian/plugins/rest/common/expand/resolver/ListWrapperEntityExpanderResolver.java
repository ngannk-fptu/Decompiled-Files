/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapper;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import com.atlassian.plugins.rest.common.util.ReflectionUtils;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class ListWrapperEntityExpanderResolver
implements EntityExpanderResolver {
    static final ListWrapperEntityExpander EXPANDER = new ListWrapperEntityExpander();

    @Override
    public boolean hasExpander(Class<?> type) {
        return ListWrapper.class.isAssignableFrom(Objects.requireNonNull(type));
    }

    @Override
    public <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        return ListWrapper.class.isAssignableFrom(type) ? EXPANDER : null;
    }

    static class ListWrapperEntityExpander<T>
    implements EntityExpander<ListWrapper<T>> {
        ListWrapperEntityExpander() {
        }

        @Override
        public ListWrapper<T> expand(ExpandContext<ListWrapper<T>> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
            ListWrapper<T> entity = context.getEntity();
            HashSet collectionFields = Sets.newHashSet();
            for (Class<?> cls = entity.getClass(); cls != null; cls = cls.getSuperclass()) {
                Field[] fields;
                for (Field field : fields = cls.getDeclaredFields()) {
                    if (!Collection.class.isAssignableFrom(field.getType())) continue;
                    collectionFields.add(field);
                }
            }
            if (collectionFields.isEmpty()) {
                throw new RuntimeException("Entity " + entity.getClass() + " has no collection field, cannot expand.");
            }
            if (collectionFields.size() > 1) {
                throw new RuntimeException("Entity " + entity.getClass() + " has more than one collection field, cannot determine which collection to expand.");
            }
            ReflectionUtils.setFieldValue((Field)collectionFields.iterator().next(), entity, entity.getCallback().getItems(context.getEntityExpandParameter().getIndexes(context.getExpandable())));
            entityCrawler.crawl(entity, context.getEntityExpandParameter().getExpandParameter(context.getExpandable()), expanderResolver);
            return entity;
        }
    }
}

