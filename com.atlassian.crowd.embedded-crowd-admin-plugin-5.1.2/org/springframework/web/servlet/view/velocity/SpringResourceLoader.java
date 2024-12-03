/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.view.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

public class SpringResourceLoader
extends org.apache.velocity.runtime.resource.loader.ResourceLoader {
    public static final Logger logger = LoggerFactory.getLogger(SpringResourceLoader.class);
    public static final String NAME = "spring";
    public static final String SPRING_RESOURCE_LOADER_CLASS = "spring.resource.loader.class";
    public static final String SPRING_RESOURCE_LOADER_CACHE = "spring.resource.loader.cache";
    public static final String SPRING_RESOURCE_LOADER = "spring.resource.loader";
    public static final String SPRING_RESOURCE_LOADER_PATH = "spring.resource.loader.path";
    private ResourceLoader resourceLoader;
    private String[] resourceLoaderPaths;

    public void init(ExtendedProperties configuration) {
        this.resourceLoader = (ResourceLoader)this.rsvc.getApplicationAttribute((Object)SPRING_RESOURCE_LOADER);
        String resourceLoaderPath = (String)this.rsvc.getApplicationAttribute((Object)SPRING_RESOURCE_LOADER_PATH);
        if (this.resourceLoader == null) {
            throw new IllegalArgumentException("'resourceLoader' application attribute must be present for SpringResourceLoader");
        }
        if (resourceLoaderPath == null) {
            throw new IllegalArgumentException("'resourceLoaderPath' application attribute must be present for SpringResourceLoader");
        }
        this.resourceLoaderPaths = StringUtils.commaDelimitedListToStringArray((String)resourceLoaderPath);
        for (int i = 0; i < this.resourceLoaderPaths.length; ++i) {
            String path = this.resourceLoaderPaths[i];
            if (path.endsWith("/")) continue;
            this.resourceLoaderPaths[i] = path + "/";
        }
        if (logger.isInfoEnabled()) {
            logger.info("SpringResourceLoader for Velocity: using resource loader [" + this.resourceLoader + "] and resource loader paths " + Arrays.asList(this.resourceLoaderPaths));
        }
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for Velocity resource with name [" + source + "]");
        }
        for (String resourceLoaderPath : this.resourceLoaderPaths) {
            org.springframework.core.io.Resource resource = this.resourceLoader.getResource(resourceLoaderPath + source);
            try {
                return resource.getInputStream();
            }
            catch (IOException ex) {
                if (!logger.isDebugEnabled()) continue;
                logger.debug("Could not find Velocity resource: " + resource);
            }
        }
        throw new ResourceNotFoundException("Could not find resource [" + source + "] in Spring resource loader path");
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0L;
    }
}

