/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.google.common.base.Throwables
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.google.common.base.Throwables;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheableDirectoryInstanceLoader
implements DirectoryInstanceLoader {
    private final DelegatingDirectoryInstanceLoader delegate;
    private final EventListenerRegistrar eventListenerRegistrar;
    private static final Logger log = LoggerFactory.getLogger(CacheableDirectoryInstanceLoader.class);
    private final Cache<Long, RemoteDirectory> directoryById;

    public CacheableDirectoryInstanceLoader(DelegatingDirectoryInstanceLoader delegate, EventListenerRegistrar eventListenerRegistrar, CacheFactory cacheFactory) {
        this.delegate = delegate;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.directoryById = CoreCache.REMOTE_DIRECTORY_BY_ID.getCache(cacheFactory);
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        try {
            return (RemoteDirectory)this.directoryById.get((Object)directory.getId(), () -> {
                try {
                    log.debug("Creating new instance of directory {}", (Object)directory.getId());
                    return this.delegate.getDirectory(directory);
                }
                catch (DirectoryInstantiationException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (RuntimeException e) {
            Throwables.propagateIfInstanceOf((Throwable)e.getCause(), DirectoryInstantiationException.class);
            throw new DirectoryInstantiationException((Throwable)e);
        }
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        return this.delegate.getRawDirectory(id, className, attributes);
    }

    public boolean canLoad(String className) {
        return this.delegate.canLoad(className);
    }

    @EventListener
    public void handleEvent(DirectoryUpdatedEvent event) {
        this.removeDirectoryFromCache(event.getDirectory());
    }

    @EventListener
    public void handleEvent(DirectoryDeletedEvent event) {
        this.removeDirectoryFromCache(event.getDirectory());
    }

    private void removeDirectoryFromCache(Directory directory) {
        log.debug("Evicting directory instance {} from memory cache", (Object)directory.getId());
        this.directoryById.remove((Object)directory.getId());
    }
}

