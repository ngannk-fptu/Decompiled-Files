/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.vcache.JvmCacheSettingsBuilder
 *  com.atlassian.vcache.VCacheFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.ResourceCache
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.cache.Cache;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.vcache.JvmCacheAdapter;
import com.atlassian.vcache.JvmCacheSettingsBuilder;
import com.atlassian.vcache.VCacheFactory;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceVelocityResourceCache
implements ResourceCache {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceVelocityResourceCache.class);
    private static ConfluenceVelocityResourceCache instance;
    private RuntimeServices velocityServices;
    private final Cache<Object, Resource> cache;

    ConfluenceVelocityResourceCache(Cache<Object, Resource> cache) {
        this.cache = cache;
        instance = this;
    }

    @Deprecated
    public ConfluenceVelocityResourceCache(VCacheFactory cacheFactory) {
        this(new JvmCacheAdapter<Object, Resource>(CoreCache.VELOCITY_RESOURCES.resolve(cacheName -> cacheFactory.getJvmCache(cacheName, new JvmCacheSettingsBuilder().build()))));
    }

    public static ConfluenceVelocityResourceCache getInstance() {
        return instance;
    }

    public static void removeFromCaches(@NonNull Object key) {
        log.info("removing " + key + " from caches");
        if (instance == null) {
            return;
        }
        ConfluenceVelocityResourceCache cache = instance;
        cache.remove(key);
        String searchKey = key.toString();
        if (searchKey.startsWith("/")) {
            searchKey = searchKey.substring(1);
        }
        Iterator<?> keyIterator = cache.enumerateKeys();
        while (keyIterator.hasNext()) {
            String cachedKey = (String)keyIterator.next();
            if (!StringUtils.contains((CharSequence)cachedKey, (CharSequence)searchKey)) continue;
            cache.remove(cachedKey);
        }
    }

    public void initialize(@NonNull RuntimeServices velocityServices) {
        this.velocityServices = velocityServices;
        if (velocityServices.getLog().isDebugEnabled()) {
            velocityServices.getLog().debug((Object)"Initialising Confluence Velocity Cache");
        }
    }

    public Resource get(@NonNull Object key) {
        if (this.velocityServices.getLog().isDebugEnabled()) {
            this.velocityServices.getLog().debug((Object)("Getting " + key + " from cache"));
        }
        return this.getResource(key);
    }

    public Resource put(@NonNull Object key, @Nullable Resource resource) {
        if (this.velocityServices.getLog().isDebugEnabled()) {
            this.velocityServices.getLog().debug((Object)("Adding " + key + " to cache"));
        }
        if (resource == null) {
            log.warn("Resource with key '" + key + "' is being cached with a null resource.");
            if (log.isDebugEnabled()) {
                log.debug("Full stack of null resource addition", (Throwable)new Exception("Null Resource Added With Key: " + key));
            }
        } else {
            this.cache.put(key, (Object)resource);
        }
        return resource;
    }

    public Resource remove(@NonNull Object key) {
        if (this.velocityServices.getLog().isDebugEnabled()) {
            this.velocityServices.getLog().debug((Object)("Removing " + key + " from cache"));
        }
        Resource resource = this.getResource(key);
        this.cache.remove(key);
        return resource;
    }

    public Iterator<?> enumerateKeys() {
        return this.cache.getKeys().iterator();
    }

    public void clear() {
        this.cache.removeAll();
    }

    private Resource getResource(Object key) {
        if (ConfluenceSystemProperties.isDisableCaches()) {
            return null;
        }
        return (Resource)this.cache.get(key);
    }
}

