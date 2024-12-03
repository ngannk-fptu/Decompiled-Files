/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.plugins.rest.common.expand.resolver;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.common.expand.DefaultExpandContext;
import com.atlassian.plugins.rest.common.expand.EntityCrawler;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionEntityExpanderResolver
implements EntityExpanderResolver {
    private static final List<Class<? extends Collection>> TYPES = Arrays.asList(List.class, Collection.class);
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final Map<Class<?>, EntityExpander<?>> EXPANDERS = ImmutableMap.builder().put(List.class, (Object)new ListExpander()).put(Collection.class, (Object)new CollectionExpander()).build();

    @Override
    public boolean hasExpander(Class<?> type) {
        for (Class<? extends Collection> expandableType : TYPES) {
            if (!expandableType.isAssignableFrom(type)) continue;
            return true;
        }
        return false;
    }

    @Override
    public <T> EntityExpander<T> getExpander(Class<? extends T> type) {
        for (Class<? extends Collection> expandableType : TYPES) {
            if (!expandableType.isAssignableFrom(type)) continue;
            return EXPANDERS.get(expandableType);
        }
        return null;
    }

    private static class CollectionExpander
    implements EntityExpander<Collection> {
        private CollectionExpander() {
        }

        @Override
        public Collection expand(ExpandContext<Collection> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
            LinkedList list = new LinkedList();
            for (Object item : context.getEntity()) {
                DefaultExpandContext itemContext = new DefaultExpandContext(item, context.getExpandable(), context.getEntityExpandParameter());
                EntityExpander<?> entityExpander = item != null ? expanderResolver.getExpander(item.getClass()) : null;
                list.add(entityExpander != null ? entityExpander.expand(itemContext, expanderResolver, entityCrawler) : item);
            }
            return list;
        }
    }

    private static class ListExpander
    implements EntityExpander<List> {
        private ListExpander() {
        }

        @Override
        public List expand(ExpandContext<List> context, EntityExpanderResolver expanderResolver, EntityCrawler entityCrawler) {
            LinkedList list = new LinkedList();
            for (Object item : context.getEntity()) {
                DefaultExpandContext itemContext = new DefaultExpandContext(item, context.getExpandable(), context.getEntityExpandParameter());
                EntityExpander<?> entityExpander = item != null ? expanderResolver.getExpander(item.getClass()) : null;
                list.add(entityExpander != null ? entityExpander.expand(itemContext, expanderResolver, entityCrawler) : item);
            }
            return list;
        }
    }
}

