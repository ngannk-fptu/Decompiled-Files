/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.quartz.spi.ClassLoadHelper
 */
package org.springframework.scheduling.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.spi.ClassLoadHelper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ResourceLoaderClassLoadHelper
implements ClassLoadHelper {
    protected static final Log logger = LogFactory.getLog(ResourceLoaderClassLoadHelper.class);
    @Nullable
    private ResourceLoader resourceLoader;

    public ResourceLoaderClassLoadHelper() {
    }

    public ResourceLoaderClassLoadHelper(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void initialize() {
        if (this.resourceLoader == null) {
            this.resourceLoader = SchedulerFactoryBean.getConfigTimeResourceLoader();
            if (this.resourceLoader == null) {
                this.resourceLoader = new DefaultResourceLoader();
            }
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        return ClassUtils.forName(name, this.resourceLoader.getClassLoader());
    }

    public <T> Class<? extends T> loadClass(String name, Class<T> clazz) throws ClassNotFoundException {
        return this.loadClass(name);
    }

    @Nullable
    public URL getResource(String name) {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        Resource resource = this.resourceLoader.getResource(name);
        if (resource.exists()) {
            try {
                return resource.getURL();
            }
            catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)("Could not load " + resource));
                }
                return null;
            }
        }
        return this.getClassLoader().getResource(name);
    }

    @Nullable
    public InputStream getResourceAsStream(String name) {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        Resource resource = this.resourceLoader.getResource(name);
        if (resource.exists()) {
            try {
                return resource.getInputStream();
            }
            catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)("Could not load " + resource));
                }
                return null;
            }
        }
        return this.getClassLoader().getResourceAsStream(name);
    }

    public ClassLoader getClassLoader() {
        Assert.state(this.resourceLoader != null, "ResourceLoaderClassLoadHelper not initialized");
        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        Assert.state(classLoader != null, "No ClassLoader");
        return classLoader;
    }
}

