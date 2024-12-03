/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.confluence.cache.Deferred;
import com.atlassian.confluence.cache.DeferredCachedReference;
import com.atlassian.confluence.cache.DeferredOperationsCache;
import com.atlassian.confluence.cache.TransactionalCacheFactory;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionalCacheFactoryCleaner {
    private static final Logger log = LoggerFactory.getLogger(TransactionalCacheFactoryCleaner.class);
    private static final Function<Deferred, String> TO_NAME = Deferred::getName;
    private static final Predicate<Deferred> IS_CACHE = deferred -> deferred instanceof DeferredOperationsCache;
    private static final Predicate<Deferred> IS_CACHED_REFERENCE = deferred -> deferred instanceof DeferredCachedReference;
    private static final Supplier<TransactionalCacheFactory> transactionalCacheFactoryRef = new LazyComponentReference("transactionalCacheFactory");

    public static Cleaner cleaner(String identifier) {
        if (ContainerManager.isContainerSetup()) {
            TreeSet cachedReferenceNames;
            Iterable<Deferred> deferreds = ((TransactionalCacheFactory)transactionalCacheFactoryRef.get()).getDeferreds();
            TreeSet cacheNames = Sets.newTreeSet((Iterable)Iterables.transform((Iterable)Iterables.filter(deferreds, IS_CACHE), TO_NAME));
            if (!cacheNames.isEmpty()) {
                log.warn("Found some transactional caches still bound to thread following execution of [{}] - {}", (Object)identifier, (Object)cacheNames);
            }
            if (!(cachedReferenceNames = Sets.newTreeSet((Iterable)Iterables.transform((Iterable)Iterables.filter(deferreds, IS_CACHED_REFERENCE), TO_NAME))).isEmpty()) {
                log.warn("Found some transactional cached references still bound to thread following execution of [{}] - {}", (Object)identifier, (Object)cachedReferenceNames);
            }
        }
        return () -> {
            if (ContainerManager.isContainerSetup()) {
                TreeSet cachedReferenceNames;
                Iterable<Deferred> deferreds = ((TransactionalCacheFactory)transactionalCacheFactoryRef.get()).forceUnbindCaches();
                TreeSet cacheNames = Sets.newTreeSet((Iterable)Iterables.transform((Iterable)Iterables.filter(deferreds, IS_CACHE), TO_NAME));
                if (!cacheNames.isEmpty()) {
                    log.warn("Forcibly unbound thread-local transactional caches prior to request execution: {}", (Object)cacheNames);
                }
                if (!(cachedReferenceNames = Sets.newTreeSet((Iterable)Iterables.transform((Iterable)Iterables.filter(deferreds, IS_CACHED_REFERENCE), TO_NAME))).isEmpty()) {
                    log.warn("Forcibly unbound thread-local transactional cached references prior to request execution: {}", (Object)cachedReferenceNames);
                }
            }
        };
    }

    public static interface Cleaner
    extends AutoCloseable {
        @Override
        public void close();
    }
}

