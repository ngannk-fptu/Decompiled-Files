/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.util.velocity;

import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.util.Assert;

public abstract class ResourceLoaderWrapper
extends ResourceLoader {
    private final ResourceLoader wrappedLoader;

    public ResourceLoaderWrapper(ResourceLoader resourceLoader) {
        Assert.notNull((Object)resourceLoader, (String)"resourceLoader must not be null");
        this.wrappedLoader = resourceLoader;
    }

    public void init(ExtendedProperties extendedProperties) {
        this.wrappedLoader.init(extendedProperties);
    }

    public long getLastModified(Resource resource) {
        return this.wrappedLoader.getLastModified(resource);
    }

    public boolean isSourceModified(Resource resource) {
        return this.wrappedLoader.isSourceModified(resource);
    }

    public boolean isCachingOn() {
        return this.wrappedLoader.isCachingOn();
    }

    public void setCachingOn(boolean cachingOn) {
        this.wrappedLoader.setCachingOn(cachingOn);
        super.setCachingOn(cachingOn);
    }

    public void setModificationCheckInterval(long ms) {
        this.wrappedLoader.setModificationCheckInterval(ms);
        super.setModificationCheckInterval(ms);
    }

    public long getModificationCheckInterval() {
        return this.wrappedLoader.getModificationCheckInterval();
    }

    public void commonInit(RuntimeServices runtimeServices, ExtendedProperties extendedProperties) {
        this.wrappedLoader.commonInit(runtimeServices, extendedProperties);
        super.commonInit(runtimeServices, extendedProperties);
    }

    public InputStream getResourceStream(String resourceName) throws ResourceNotFoundException {
        return this.wrappedLoader.getResourceStream(resourceName);
    }

    public ResourceLoader getBaseResourceLoader() {
        ResourceLoader loader = this.wrappedLoader;
        while (loader instanceof ResourceLoaderWrapper) {
            loader = ((ResourceLoaderWrapper)loader).wrappedLoader;
        }
        return loader;
    }

    public String toString() {
        return "Wrapped resource loader (" + ((Object)((Object)this)).getClass() + "): " + this.wrappedLoader;
    }
}

