/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Throwables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.Supplier;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Throwables;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class CacheableDirectoryInstanceLoader
implements DirectoryInstanceLoader {
    private static final Logger log = LoggerFactory.getLogger(CacheableDirectoryInstanceLoader.class);
    private final DelegatingDirectoryInstanceLoader delegate;
    private final EventPublisher eventPublisher;
    private final Cache<Long, RemoteDirectory> directoryCache;

    public CacheableDirectoryInstanceLoader(DelegatingDirectoryInstanceLoader delegate, EventPublisher eventPublisher, CacheFactory cacheFactory) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
        this.directoryCache = this.createCache(cacheFactory);
        this.eventPublisher.register((Object)this);
    }

    private Cache<Long, RemoteDirectory> createCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CacheableDirectoryInstanceLoader.class.getName(), null, new CacheSettingsBuilder().remote().replicateViaInvalidation().build());
    }

    public RemoteDirectory getDirectory(final Directory directory) throws DirectoryInstantiationException {
        long id = directory.getId();
        try {
            return (RemoteDirectory)this.directoryCache.get((Object)id, (Supplier)new Supplier<RemoteDirectory>(){

                public RemoteDirectory get() {
                    try {
                        return CacheableDirectoryInstanceLoader.this.delegate.getDirectory(directory);
                    }
                    catch (DirectoryInstantiationException e) {
                        throw new CacheException((Throwable)e);
                    }
                }
            });
        }
        catch (CacheException e) {
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
        this.directoryCache.remove((Object)event.getDirectoryId());
    }

    @EventListener
    public void handleEvent(DirectoryDeletedEvent event) {
        this.directoryCache.remove((Object)event.getDirectoryId());
    }

    @EventListener
    public void handleEvent(XMLRestoreFinishedEvent event) {
        this.directoryCache.removeAll();
        log.debug("Directory Cache cleared.");
    }
}

