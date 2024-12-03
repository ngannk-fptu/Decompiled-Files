/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.hibernate.criterion.Expression
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateOperations
 */
package com.atlassian.confluence.impl.content.render.prefetch.hibernate;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hibernate.criterion.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateOperations;

public class HibernatePrefetchHelper {
    private static final Logger log = LoggerFactory.getLogger(HibernatePrefetchHelper.class);
    private final HibernateOperations hibernate;

    public HibernatePrefetchHelper(HibernateOperations hibernate) {
        this.hibernate = Objects.requireNonNull(hibernate);
    }

    public <T> Collection<T> prefetchEntitiesById(String idPropertyName, Collection<?> ids, Class<T> entityType) {
        return (Collection)this.hibernate.execute(session -> {
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }
            List results = session.createCriteria(entityType).add(Expression.in((String)idPropertyName, (Collection)ids)).list();
            log.debug("Prefetched {} {} entities with IDs {}", new Object[]{results.size(), entityType.getSimpleName(), ids});
            return results;
        });
    }

    public static <I, O> Collection<O> partitionedQuery(Collection<I> inputItems, int partitionSize, Function<Collection<I>, Collection<O>> query) {
        return Lists.partition(new ArrayList<I>(inputItems), (int)partitionSize).stream().flatMap(partition -> ((Collection)query.apply((Collection)partition)).stream()).collect(Collectors.toList());
    }
}

