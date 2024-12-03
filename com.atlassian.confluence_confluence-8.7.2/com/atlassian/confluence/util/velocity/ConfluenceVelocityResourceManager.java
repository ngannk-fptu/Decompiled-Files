/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.velocity.ConfigurableResourceManager
 *  com.atlassian.confluence.velocity.ConfigurableResourceManager$ResourceFactory
 *  com.atlassian.vcache.VCacheFactory
 *  io.atlassian.fugue.Either
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.resource.ContentResource
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.ResourceCache
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.setup.velocity.DecoratorName;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceCache;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityTemplateImpl;
import com.atlassian.confluence.util.velocity.ResourceLoaderWrapper;
import com.atlassian.confluence.velocity.ConfigurableResourceManager;
import com.atlassian.vcache.VCacheFactory;
import io.atlassian.fugue.Either;
import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceVelocityResourceManager
extends ConfigurableResourceManager {
    private static final Logger privateLog = LoggerFactory.getLogger(ConfluenceVelocityResourceManager.class);
    private static ConfluenceVelocityResourceManager instance;
    private static final ConfigurableResourceManager.ResourceFactory RESOURCE_FACTORY;
    private static Either<VCacheFactory, CacheFactory> cacheFactory;

    public ConfluenceVelocityResourceManager() {
        instance = this;
    }

    public static void setCacheFactory(CacheFactory cacheFactory) {
        ConfluenceVelocityResourceManager.cacheFactory = Either.right((Object)cacheFactory);
        if (instance != null) {
            instance.initConfluenceVelocityCache();
        }
    }

    @Deprecated
    public static void setCacheFactory(VCacheFactory cacheFactory) {
        ConfluenceVelocityResourceManager.cacheFactory = Either.left((Object)cacheFactory);
        if (instance != null) {
            instance.initConfluenceVelocityCache();
        }
    }

    public void initialize(RuntimeServices runtimeServices) throws Exception {
        super.initialize(runtimeServices);
        this.initConfluenceVelocityCache();
    }

    private synchronized void initConfluenceVelocityCache() {
        if (cacheFactory == null) {
            privateLog.debug("No cache manager. Using default resource cache");
            return;
        }
        privateLog.debug("Initializing ConfluenceVelocityResourceCache");
        this.globalCache = (ResourceCache)cacheFactory.fold(ConfluenceVelocityResourceCache::new, cf -> new ConfluenceVelocityResourceCache(CoreCache.VELOCITY_RESOURCES.getCache((CacheFactory)cf)));
        this.globalCache.initialize(this.rsvc);
    }

    protected ResourceLoader postProcessLoader(ResourceLoader loader, ExtendedProperties config) {
        if (!config.getBoolean("confluence.space.decorator.loader", false)) {
            loader = new DecoratorFilteredResourceLoader(loader);
        }
        return super.postProcessLoader(loader, config);
    }

    protected ConfigurableResourceManager.ResourceFactory getResourceFactory() {
        return RESOURCE_FACTORY;
    }

    static {
        RESOURCE_FACTORY = new ConfluenceResourceFactory();
    }

    public static class ConfluenceResourceFactory
    implements ConfigurableResourceManager.ResourceFactory {
        public Resource getResource(String resourceName, int resourceType) {
            ConfluenceVelocityTemplateImpl resource = null;
            switch (resourceType) {
                case 1: {
                    resource = new ConfluenceVelocityTemplateImpl();
                    break;
                }
                case 2: {
                    resource = new ContentResource();
                    break;
                }
            }
            return resource;
        }
    }

    private static class DecoratorFilteredResourceLoader
    extends ResourceLoaderWrapper {
        public DecoratorFilteredResourceLoader(ResourceLoader loader) {
            super(loader);
        }

        @Override
        public final InputStream getResourceStream(String s) throws ResourceNotFoundException {
            if (DecoratorName.isSpaceDecoratorSource(s)) {
                return null;
            }
            return super.getResourceStream(s);
        }
    }
}

