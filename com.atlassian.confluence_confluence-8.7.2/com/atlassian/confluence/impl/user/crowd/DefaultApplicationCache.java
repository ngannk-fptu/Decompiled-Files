/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Throwables
 *  javax.annotation.PostConstruct
 *  org.hibernate.Hibernate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.user.crowd.ApplicationCache;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Throwables;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultApplicationCache
implements ApplicationCache {
    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationCache.class);
    private static final String CACHE_KEY = DefaultApplicationCache.class.getName();
    private final TransactionAwareCacheFactory cacheFactory;
    private final EventPublisher eventPublisher;

    public DefaultApplicationCache(TransactionAwareCacheFactory cacheFactory, EventPublisher eventPublisher) {
        this.cacheFactory = cacheFactory;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void directoryUpdated(DirectoryUpdatedEvent directoryUpdatedEvent) {
        this.removeAll();
    }

    @Override
    public Application getApplication(String name, ApplicationCache.Loader loader) throws ApplicationNotFoundException {
        try {
            return this.getCache().get(name, (Supplier<Application>)((Supplier)() -> {
                try {
                    return DefaultApplicationCache.toCacheValue(loader.getApplication(name));
                }
                catch (ApplicationNotFoundException e) {
                    throw new ApplicationNotFoundRuntimeException(e);
                }
            }));
        }
        catch (RuntimeException ex) {
            throw DefaultApplicationCache.unwrapOrRethrow(ex, ApplicationNotFoundException.class);
        }
    }

    public static <T extends Exception> T unwrapOrRethrow(RuntimeException ex, Class<T> expectedType) {
        return (T)DefaultApplicationCache.getCausalChain(ex).filter(expectedType::isInstance).map(expectedType::cast).findFirst().orElseThrow(() -> ex);
    }

    private static Stream<Throwable> getCausalChain(RuntimeException ex) {
        try {
            return Throwables.getCausalChain((Throwable)ex).stream();
        }
        catch (IllegalArgumentException iaex) {
            log.warn("Failed to decode cache exception", (Throwable)iaex);
            return Stream.of(ex);
        }
    }

    private static String toCacheKey(String name) {
        return name.toLowerCase();
    }

    private static Application toCacheValue(Application application) {
        for (DirectoryMapping mapping : application.getDirectoryMappings()) {
            Hibernate.initialize((Object)mapping.getDirectory());
        }
        return ImmutableApplication.builder((Application)application).build();
    }

    @Override
    public void removeApplication(String name) {
        this.getCache().remove(DefaultApplicationCache.toCacheKey(name));
    }

    @Override
    public void removeAll() {
        this.getCache().removeAll();
    }

    private TransactionAwareCache<String, Application> getCache() {
        return this.cacheFactory.getTxCache(CACHE_KEY);
    }

    private static class ApplicationNotFoundRuntimeException
    extends RuntimeException {
        ApplicationNotFoundRuntimeException(ApplicationNotFoundException cause) {
            super(cause);
        }
    }
}

